package spamdetector.detector.students.dato;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import spamdetector.detector.Classifier;
import spamdetector.detector.TestCase;
import tester.Solver;

public class DatosSolver implements Solver<TestCase, Classifier> {
	Vector<String> usedClassifiers = new Vector<>();
	Vector<Integer> ruleIds = new Vector<>();
	Vector<Double> coefs = new Vector<>();
	HashMap<Integer, ArrayList<Integer>> misclassified = new HashMap<>();
	double[] weights;
	String[] spams, classifiers, usefuls;

	public Classifier solve(TestCase testCase) {
		spams = testCase.getSpamEmails();
		classifiers = testCase.getWeakClassifiers();
		usefuls = testCase.getUsefulEmails();
		weights = new double[spams.length + (usefuls.length)];
		for (int i = 0; i < weights.length; i++)
			weights[i] = (double) 1 / (weights.length);
		generateErrors();
		Classifier result = new Classifier();
		int classifierId = 0;

		while (usedClassifiers.size() < classifiers.length) {
			double e = 0.5;
			for (int i = 0; i < classifiers.length; i++) {
				if (!usedClassifiers.contains(classifiers[i])) {
					double k = computeError(i);
					if (e > k) {
						e = k;
						classifierId = i;
					}
				}
			}
			if (e > 0.5) {
				int[] ruleIdsArr = new int[ruleIds.size()];
				double[] coefsArr = new double[ruleIds.size()];
				for (int j = 0; j < ruleIds.size(); j++) {
					ruleIdsArr[j] = ruleIds.get(j);
					coefsArr[j] = coefs.get(j);
				}
				result.setRuleIds(ruleIdsArr);
				result.setCoefs(coefsArr);
				return result;
			}
			double alfa = 0.5 * (Math.log((1 - e) / e));
			ruleIds.add(classifierId);
			coefs.add(alfa);
			usedClassifiers.add(classifiers[classifierId]);
			normalize(e, classifierId);
		}
		int[] ruleIdsArr = new int[ruleIds.size()];
		double[] coefsArr = new double[ruleIds.size()];
		for (int j = 0; j < ruleIds.size(); j++) {
			ruleIdsArr[j] = ruleIds.get(j);
			coefsArr[j] = coefs.get(j);
		}
		result.setRuleIds(ruleIdsArr);
		result.setCoefs(coefsArr);
//		for(int i=0;i<ruleIdsArr.length;i++)
//			System.out.println("coefs "+ coefsArr[i]+ " classifiers "+ruleIdsArr[i]);
		return result;
	}

	// writing misclasified mails for each classifier in order easily to compute
	// error
	private void generateErrors() {
		for (int i = 0; i < classifiers.length; i++) {
			ArrayList<Integer> errors = new ArrayList<>();
			for (int j = 0; j < spams.length; j++) {
				if (!isSpam(spams[j], classifiers[i]))
					errors.add(j);
			}
			for (int j = 0; j < usefuls.length; j++) {
				if (isSpam(usefuls[j], classifiers[i]))
					errors.add((spams.length) + j);
			}
			misclassified.put(i, errors);
		}

	}

	// determining whether mail is spam according to classifier
	private boolean isSpam(String mail, String classifier) {
		if (mail.contains(classifier))
			return true;
		int i;
		StringBuilder a = new StringBuilder();
		StringBuilder b = new StringBuilder();
		for (i = 0; i < classifier.length(); i++) {
			if (classifier.charAt(i) == '|') {
				for (int j = 0; j < i; j++)
					a.append(classifier.charAt(j));
				for (int k = i + 1; k < classifier.length(); k++)
					b.append(classifier.charAt(k));
				if (mail.contains(a) || mail.contains(b))
					return true;
			}
			if (classifier.charAt(i) == '&') {
				for (int j = 0; j < i; j++)
					a.append(classifier.charAt(j));
				for (int j = i + 1; j < classifier.length(); j++)
					b.append(classifier.charAt(j));
				if (mail.contains(a) && mail.contains(b))
					return true;
			}
		}
		return false;
	}

	// computing error updating weights for mails which were marked as spam or
	// usefull by isSpam
	private double computeError(int classifierId) {
		double e = 0;
		for (int i : misclassified.get(classifierId))
			e += weights[i];
		return e;
	}

	private void normalize(double e, int classifierId) {
		for(int i=0;i<weights.length;i++)
			if(misclassified.get(classifierId).contains(i)){
				weights[i] = 0.5 * (weights[i] / e);
			} else {
				weights[i] = 0.5 * (weights[i] / (1 - e));
			}
	}
}
