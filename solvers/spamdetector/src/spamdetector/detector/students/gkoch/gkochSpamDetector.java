package spamdetector.detector.students.gkoch;

import java.util.ArrayList;
import java.util.List;

import spamdetector.detector.Classifier;
import spamdetector.detector.TestCase;
import tester.Solver;

public class gkochSpamDetector implements Solver<TestCase, Classifier> {

	private double[] weights;
	private List<initialClassifier> weaks = new ArrayList<>();
	private List<finalClassifier> strong = new ArrayList<>();
	private List<Integer> errors = new ArrayList<>();
	private TestCase test;

	public Classifier solve(TestCase test) {
		initArrays(test);
		findStrongClassifier();
		int[] ids = new int[strong.size()];
		double[] coef = new double[strong.size()];
		for(int i = 0 ; i < strong.size(); i++){
			ids[i] = strong.get(i).index;
			coef[i] = strong.get(i).coefficient;
		}
		Classifier ans = new Classifier();
		ans.setRuleIds(ids);
		ans.setCoefs(coef);
		return ans;
	}

	//adaptive boosting algorithm implementation
	//greats strong classifier and stores it in array list called strong
	private void findStrongClassifier() {
		while (!weaks.isEmpty()) {
			initialClassifier init = getBestClassifier();
			if (init == null)
				return;
			addFinalClassifier(init);
			updateWeights();
		}
	}

	//update weights using error rate
	private void updateWeights() {
		double sum1 = 0;
		double sum2 = 0;
		for (int i = 0; i < weights.length; i++) {
			if(errors.contains(i)){
				sum1 += weights[i]; 
			}else{
				sum2 += weights[i];
			}
		}
		for (int i = 0; i < weights.length; i++) {
			if(errors.contains(i)){
				weights[i] = 0.5*weights[i]/sum1;
			}else{
				weights[i] = 0.5*weights[i]/sum2;
			}
		}
	}

	// count a coefficient for h and store index
	private void addFinalClassifier(initialClassifier init) {
		finalClassifier H = new finalClassifier();
		H.index = init.index;
		double error = getError(init);
		H.coefficient = 0.5 * Math.log((1 - error) / error);
		strong.add(H);
	}

	// chooses minimal error rate classifier
	private initialClassifier getBestClassifier() {
		int index = 0;
		double minError = 1;
		for (int i = 0; i < weaks.size(); i++) {
			double tmpError = getError(weaks.get(i));
			if (tmpError <= minError) {
				minError = tmpError;
				index = i;
			}
		}
		if (minError >= 0.5)
			return null;
		return weaks.remove(index);
	}

	// returns error for concrete classifier
	private double getError(initialClassifier init) {
		errors.clear();
		double error = 0;
		for (int i = 0; i < test.getUsefulEmails().length; i++) {
			int result = testClassifier(test.getUsefulEmails()[i], init);
			if (result == 1) {
				error += weights[i];
				errors.add(i);
			}
		}
		for (int i = 0; i < test.getSpamEmails().length; i++) {
			int result = testClassifier(test.getSpamEmails()[i], init);
			if (result == 0) {
				error += weights[i + test.getUsefulEmails().length];
				errors.add(i + test.getUsefulEmails().length);
			}
		}
		return error;
	}

	// return result if string is spam or not return 1 if it is spam otherwise 0
	private int testClassifier(String string, initialClassifier init) {
		if (init.classifier.indexOf('|') >= 0) {
			int index = init.classifier.indexOf('|');
			String str1 = init.classifier.substring(0, index);
			String str2 = init.classifier.substring(index + 1);
			if (string.indexOf(str1) >= 0 || string.indexOf(str2) >= 0) {
				return 1;
			}
			return 0;
		} else if (init.classifier.indexOf('&') >= 0) {
			int index = init.classifier.indexOf('&');
			String str1 = init.classifier.substring(0, index);
			String str2 = init.classifier.substring(index + 1);
			if (string.indexOf(str1) >= 0 && string.indexOf(str2) >= 0) {
				return 1;
			}
			return 0;
		} else {
			if (string.indexOf(init.classifier) >= 0) {
				return 1;
			}
			return 0;
		}
	}

	// create data structures which is useful
	private void initArrays(TestCase test) {
		this.test = test;
		for (int i = 0; i < test.getWeakClassifiers().length; i++) {
			initialClassifier init = new initialClassifier();
			init.classifier = test.getWeakClassifiers()[i];
			init.index = i;
			weaks.add(init);
		}
		weights = new double[test.getSpamEmails().length + test.getUsefulEmails().length];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = ((double) 1) / weights.length;
		}
	}

	//finals Classifier is object that have its coefficient and index of weak classifier
	private class finalClassifier {
		private int index;
		private double coefficient;
	}

	//initial classifier is an object with test classifier and index which is appointed for it
	private class initialClassifier {
		private int index;
		private String classifier;
	}
}
