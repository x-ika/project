package spamdetector.detector.students.vmask11;

import java.util.ArrayList;
import java.util.List;

import spamdetector.detector.Classifier;
import spamdetector.detector.TestCase;
import tester.Solver;

public class VatoDetector implements Solver<TestCase, Classifier> {
	private static final int TYPE_FIRST = 1;
    private static final int TYPE_SECOND = 2;
    private static final int TYPE_THIRD = 3;

	private static int getType(String rule) {
		if (rule.indexOf("|") != -1)
			return TYPE_SECOND;
		if (rule.indexOf("&") != -1)
			return TYPE_THIRD;
		return TYPE_FIRST;
	}

	private static boolean isSpam(WClassifier classifier, String email) {
		 switch (classifier.type) {
			 case TYPE_FIRST:
				return email.indexOf(classifier.rule) != -1;
			 case TYPE_SECOND:
				String r1 = classifier.rule.substring(0, classifier.rule.indexOf("|")),
					r2 = classifier.rule.substring(classifier.rule.indexOf("|") + 1);
				return email.indexOf(r1) != -1 || email.indexOf(r2) != -1;
			 case TYPE_THIRD:
				r1 = classifier.rule.substring(0, classifier.rule.indexOf("&"));
				r2 = classifier.rule.substring(classifier.rule.indexOf("&") + 1);
				return email.indexOf(r1) != -1 && email.indexOf(r2) != -1;
		 }
		return true;
	}

	private class Sample {
		public String content;
		public double weight;

		public Sample(String content, double weight) {
			this.content = content;
			this.weight = weight;
		}
	}

	private class WClassifier {
		public String rule;
		public int type;
		public double error;
		public boolean usefulEmWasClassified[],
			spamEmWasClassified[],
			canUse;

		public WClassifier(String rule) {
			this.rule = rule;
			type = getType(rule);
			usefulEmWasClassified = new boolean[usefulEmails.length];
			spamEmWasClassified = new boolean[spamEmails.length];
			canUse = true;
		}
	}

	private Sample[] usefulEmails;
    private Sample[] spamEmails;
    private List<Integer> ruleIds = new ArrayList<>();
    private List<Double> coefs = new ArrayList<>();
	private WClassifier classifiers[];
	private double error; // computed error in every turn

	private void init(TestCase testCase) {
		double weight = 1.0 / (testCase.getUsefulEmails().length +
				testCase.getSpamEmails().length);
		usefulEmails = new Sample[testCase.getUsefulEmails().length];
		for (int i = 0; i < usefulEmails.length; i++)
			usefulEmails[i] = new Sample(testCase.getUsefulEmails()[i], weight);
		spamEmails = new Sample[testCase.getSpamEmails().length];
		for (int i = 0; i < spamEmails.length; i++)
			spamEmails[i] = new Sample(testCase.getSpamEmails()[i], weight);
		classifiers = new WClassifier[testCase.getWeakClassifiers().length];
		for (int i = 0; i < testCase.getWeakClassifiers().length; i++)
			classifiers[i] = new WClassifier(testCase.getWeakClassifiers()[i]);
	}

	public Classifier solve(TestCase testCase) {
		init(testCase);
		int used = 0;
		while (used != classifiers.length) {
			int bestIndex = 0;
			error = 1;
			for (int i = 0; i < classifiers.length; i++) {
				if (!classifiers[i].canUse)
					continue;
				classifiers[i].error = 0;
				for (int j = 0; j < usefulEmails.length; j++) {
					if (isSpam(classifiers[i], usefulEmails[j].content)) { // classifier chooses wrong answer
						classifiers[i].error += usefulEmails[j].weight;
						classifiers[i].usefulEmWasClassified[j] = false;
					} else
						classifiers[i].usefulEmWasClassified[j] = true;
				}
				for (int j = 0; j < spamEmails.length; j++) {
					if (!isSpam(classifiers[i], spamEmails[j].content)) {
						classifiers[i].error += spamEmails[j].weight;
						classifiers[i].spamEmWasClassified[j] = false;
					} else
						classifiers[i].spamEmWasClassified[j] = true;
				}
				if (classifiers[i].error < error) {
					bestIndex = i;
					error = classifiers[i].error;
				}
			}
			if (error > 0.5) // when error is more than 1/2 return
				break;

			ruleIds.add(bestIndex);
			coefs.add(Math.log((1 - error) / error) / 2);

			computeNewWeights(classifiers[bestIndex]);
			classifiers[bestIndex].canUse = false;
		}
		return getResult();
	}



	private Classifier getResult() {
		Classifier result = new Classifier();
		int ruleIdsArr[] = new int[ruleIds.size()];
		double coefsArr[] = new double[coefs.size()];
		for (int i = 0; i < ruleIdsArr.length; i++)
			ruleIdsArr[i] = ruleIds.get(i);
		for (int i = 0; i < coefsArr.length; i++)
			coefsArr[i] = coefs.get(i);
		result.setRuleIds(ruleIdsArr);
		result.setCoefs(coefsArr);
		return result;
	}

	private void computeNewWeights(WClassifier classifier) {
		for (int i = 0; i < usefulEmails.length; i++)
			if (classifier.usefulEmWasClassified[i])
				usefulEmails[i].weight = usefulEmails[i].weight / (2 * (1 - error));
			else
				usefulEmails[i].weight = usefulEmails[i].weight / (2 * error);
		for (int i = 0; i < spamEmails.length; i++)
			if (classifier.spamEmWasClassified[i])
				spamEmails[i].weight = spamEmails[i].weight / (2 * (1 - error));
			else
				spamEmails[i].weight = spamEmails[i].weight / (2 * error);
	}
}