package spamdetector.detector.students.lgure;

import java.util.ArrayList;

import spamdetector.detector.Classifier;
import spamdetector.detector.TestCase;
import tester.Solver;

public class LashasDetector implements Solver<TestCase, Classifier>{
	private static final String ALREADY_USED_CLASSIFIER = "~~~~~alreadyusedclassifier&&&&&&&&&&";
	
	private String[] usefulEmails;
    private String[] spamEmails;
    private String[] weakClassifiers;
    
	private int emailsCuantity;
	
	private ArrayList<Integer> ruleIds = new ArrayList<>();
    private ArrayList<Double> coefs = new ArrayList<>();
    
	private double futureWeights[];
	
	private double currentWeights[];
	
	
	private class privateClassifier {
		int index;
		
		double errorRate;
		
		public void setIndex(int  i) {
			this.index = i;
		}
		
		public void setErrorRate(double d) {
			this.errorRate = d;
		}
		
		public int getIndex() {
			return this.index;
		}
		
		public double getErrorRate(){
			return this.errorRate;
		}
	}

	public Classifier solve(TestCase testCase) {
		
		init(testCase);
		initialWeights();
		
		for (int step = 0; step < this.weakClassifiers.length; step++) {
			privateClassifier c = findLowestErrorRate();
			if (c.getErrorRate() >= 1.0/2) {
				break;
			}else {
				ruleIds.add(c.getIndex());
				coefs.add(countCoeficient(c.getErrorRate()));
				reweightSamples(c);
				
				weakClassifiers[c.getIndex()] = ALREADY_USED_CLASSIFIER;
			}
		}
		
		//result
		
		int[] ruleId = new int[ruleIds.size()];
		for (int i = 0; i < ruleIds.size(); i++) {
			ruleId[i] = ruleIds.get(i);
		}
		double[] coef = new double[coefs.size()];
		for( int i = 0; i < coefs.size(); i++) {
			coef[i] = coefs.get(i);
		}
		Classifier result = new Classifier();
		result.setCoefs(coef);
		result.setRuleIds(ruleId);
		return result;
		
	}


	private void reweightSamples(privateClassifier c) {
		boolean and = false;
		String classifier = weakClassifiers[c.getIndex()];
		String[] classifiers;
		if (classifier.contains("&")) {
			classifiers = classifier.split("&");
			and = true;
		} else {
			classifiers = classifier.split("\\|");
		}

		for (int i = 0; i < usefulEmails.length; i++) {
			if (checkEmailIfSpam(usefulEmails[i], classifiers,and)) {
				//misclassified
				futureWeights[i] = currentWeights[i] / (2.0 * c.getErrorRate());
			} else {
				//correctly classified
				futureWeights[i] = currentWeights[i] / (2.0 * (1.0 - c.getErrorRate()));
			}
		}
		
		for (int i = 0; i < spamEmails.length; i++) {
			if (!checkEmailIfSpam(spamEmails[i], classifiers,and)) {
				//misclassified
				futureWeights[i + usefulEmails.length] = currentWeights[i + usefulEmails.length] / (2.0 * c.getErrorRate());
			} else {
				//correctly classified
				futureWeights[i + usefulEmails.length] = currentWeights[i + usefulEmails.length] / (2.0 * (1.0 - c.getErrorRate()));
			}
		}
		
		//swap futureWeights and currentWeights
		double[] tmp = futureWeights;
		futureWeights = currentWeights;
		currentWeights = tmp;
	}


	private Double countCoeficient(double e) {
		return Math.log((1.0-e) / e) /2.0;
	}


	private void init(TestCase testCase) {
		this.usefulEmails = testCase.getUsefulEmails();
		this.spamEmails = testCase.getSpamEmails();
		this.weakClassifiers = testCase.getWeakClassifiers().clone();
		this.emailsCuantity = usefulEmails.length + spamEmails.length;
		
		this.coefs = new ArrayList<>();
		this.ruleIds = new ArrayList<>();
	}


	private privateClassifier findLowestErrorRate() {
		double lowestErrorRate = 1.0 / 2;
		int lowestErrorRateIndex = -1;
		for (int i = 0; i < weakClassifiers.length; i++) {
			if (weakClassifiers[i].equals(ALREADY_USED_CLASSIFIER)) {
				continue;
			}
			double errorRate = countErrorRate(weakClassifiers[i]);
			if (errorRate < lowestErrorRate) {
				lowestErrorRate = errorRate;
				lowestErrorRateIndex = i;
			}
		}
	
		privateClassifier c = new privateClassifier();
		c.setErrorRate(lowestErrorRate);
		c.setIndex(lowestErrorRateIndex);
		return c;
	}


	private double countErrorRate(String classifier) {
		boolean and = false;
		String[] classifiers;
		if (classifier.contains("&")) {
			classifiers = classifier.split("&");
			and = true;
		} else {
			classifiers = classifier.split("\\|");
		}

		double errorRate = 0.0;
		for (int i = 0; i < usefulEmails.length; i++) {
			if (checkEmailIfSpam(usefulEmails[i], classifiers, and)) {
				errorRate += currentWeights[i];
			}
		}
		
		for (int i = 0; i < spamEmails.length; i++) {
			if (!checkEmailIfSpam(spamEmails[i], classifiers, and)) {
				errorRate += currentWeights[i + usefulEmails.length];
			}
		}
		
		return errorRate;
	}

	/*
	 * returns "true" if classifier thinks it is spam, or "false" otherwise
	 * "and" parameter id flag, if there was "&" in classifier or not
	 */
	private boolean checkEmailIfSpam(String email, String[] classifiers, boolean and) {
		if (and) {
			return email.contains(classifiers[0]) && email.contains(classifiers[1]);
		} else {
			for (String s : classifiers) {
				if (email.contains(s)) return true;
			}
		}
		return false;
	}


	/*
 * Tavdapirvelad yvela wonebs gaxdis 1/mailebis raodenoba
 */
	private void initialWeights() {
		futureWeights = new double[emailsCuantity];
		currentWeights = new double[emailsCuantity];
		
		double initialWeight = 1.0 / emailsCuantity;
		for (int i = 0; i < emailsCuantity; i++) {
			currentWeights[i] = initialWeight;
		}
	}

}
