package spamdetector.detector.students.mkapa;

import java.util.ArrayList;
import java.util.HashSet;

import spamdetector.detector.Classifier;
import spamdetector.detector.TestCase;
import tester.Solver;
import java.lang.Math;

public class MyDetector implements Solver<TestCase, Classifier> {

	TestCase test;

	ArrayList<String> spam = new ArrayList<>();
	ArrayList<String> notspam = new ArrayList<>();
	
	ArrayList<Double> spamWeights = new ArrayList<>();
	double[] notSpamWeights = new double[467567];

	
	HashSet<Integer> considered = new HashSet<>();

	ArrayList<Double> errors = new ArrayList<>();
	
	// indexes of classified and misclassified tests by current classifier 
	ArrayList<Integer> misclassified = new ArrayList<>();
	ArrayList<Integer> classified = new ArrayList<>();
	ArrayList<Integer> misclassifiedSpams = new ArrayList<>();
	ArrayList<Integer> classifiedSpams = new ArrayList<>();

	@Override
	public Classifier solve(TestCase t) {
		test = t;
		double initWeight = ((double) 1.0) / ((double)test.getSpamEmails().length + (double)test.getUsefulEmails().length);
		initLists(); // init spam and notspam Lists
		InitWeights(initWeight); // init weights
		initErrors(); // init errors to error List
		Classifier result = new Classifier(); // result classifier
		ArrayList<Double> coefs = new ArrayList<>();
		ArrayList<Integer> rules = new ArrayList<>();
		while (true) {
			int weak = BestClassifier(); // the best from weak classifiers
			// if all considered
			if(weak == -1){
				result.setCoefs(toDoubleArray(coefs));
				result.setRuleIds(toIntArray(rules));
				return result;
			}
			double error = getError(test.getWeakClassifiers()[weak]); 
			
			// if error of the best of weak classifiers is up to 0.5
			if(error > 0.5){	
				result.setCoefs(toDoubleArray(coefs));
				result.setRuleIds(toIntArray(rules));
				return result;
			}
			double alpha = getAlpha(error);
			coefs.add(alpha); 
			rules.add(weak);
			updateWeights(error);
			initErrors();  // changes errors according to updated weights
		}
	}

	private void initLists() {
		for (int i = 0; i < test.getSpamEmails().length; i++) {
			spam.add(test.getSpamEmails()[i]);
		}
		for (int i = 0; i < test.getUsefulEmails().length; i++) {
			notspam.add(test.getUsefulEmails()[i]);
		}
	}

	private int[] toIntArray(ArrayList<Integer> rules) {
		int[] res = new int[rules.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = rules.get(i);
		}
		return res;
	}

	private double[] toDoubleArray(ArrayList<Double> coefs) {
		double[] res = new double[coefs.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = coefs.get(i);
		}
		return res;
	}

	private void updateWeights(double error) {
		for (int i = 0; i < misclassified.size(); i++) {
			int a = misclassified.get(i);
			double newWeight = nextWeight(notSpamWeights[a], error);
			notSpamWeights[a] = newWeight;
		}
		for (int i = 0; i < misclassifiedSpams.size(); i++) {
			int a = misclassifiedSpams.get(i);
			double newWeight = nextWeight(spamWeights.get(a), error);
			spamWeights.set(a, newWeight); 
		}
		for (int i = 0; i < classified.size(); i++) {
			int a = classified.get(i);
			double newWeight = nextWeight(notSpamWeights[a], 1.0-error);
			notSpamWeights[a] = newWeight;
		}
		for (int i = 0; i < classifiedSpams.size(); i++) {
			int a = classifiedSpams.get(i);
			double newWeight = nextWeight(spamWeights.get(a), 1.0-error);
			spamWeights.set(a, newWeight); 
		}
	}

	private void InitWeights(double initWeight) {
		for (int i = 0; i < spam.size(); i++) {
			spamWeights.add((double) initWeight);
		}
		for (int i = 0; i < notspam.size(); i++) {
			notSpamWeights[i] = initWeight;
		}
	}

	// add each error to error's List
	private void initErrors(){
		errors.clear();
		for (int i = 0; i < test.getWeakClassifiers().length; i++) {
			double er = getError(test.getWeakClassifiers()[i]);
			errors.add(er);
		}
	}
	// returns index of best simple classifier
	private int BestClassifier() {
		int result = -1;
		double tmp = 100000000.0;
		for (int i = 0; i < errors.size(); i++) {
			if (!considered.contains(i) &&  errors.get(i) < tmp) {
				result = i;
				tmp = errors.get(i);
			}
		}
		considered.add(result);
		return result;
	}

	// gets classificator's error
	// fills classified and misclassified Lists
	private double getError(String classificator) {
		misclassified.clear();
		classified.clear();
		misclassifiedSpams.clear();
		classifiedSpams.clear();
		
		double error = 0.0;
		for (int i = 0; i < spam.size(); i++) {
			if (isSpam(spam.get(i), classificator) == -1) {
				error += (double)spamWeights.get(i);
				misclassifiedSpams.add(i);
			}else{
			    classifiedSpams.add(i);
			}
		}
		for (int i = 0; i < notspam.size(); i++) {
			if (isSpam(notspam.get(i), classificator) == 1) {
				error += notSpamWeights[i];
				misclassified.add(i);
			}else{
				classified.add(i);
			}
		}
		return error;
	}

	// true if spam
	private int isSpam(String txt, String classiffier) {
		int res = -1;
		String one = "", two = "";
		int indexofTwo = classiffier.indexOf("|");
		if(indexofTwo != -1){
			one = classiffier.substring(0,indexofTwo);
			two = classiffier.substring(indexofTwo+1);
			if(txt.indexOf(one) != -1 || txt.indexOf(two) != -1){
				return 1;
			}
		}else{
			indexofTwo = classiffier.indexOf("&");
			if(indexofTwo != -1){
				one = classiffier.substring(0,indexofTwo);
				two = classiffier.substring(indexofTwo+1);
				if(txt.indexOf(one) != -1 && txt.indexOf(one) != -1){
					return 1;
				}
			}
		}
		
		if (indexofTwo == -1 && txt.indexOf(classiffier) != -1) {
			res = 1;
		}
		return res;
	}

	private double getAlpha(double error) {
		return 0.5 * ((double)Math.log((1.0 - error) / error));
	}

	private double nextWeight(double lastWeight, double error) {
		return 0.5*((double)lastWeight/(error));
	}
}
