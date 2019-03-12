package spamdetector.detector.students.zgven;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import spamdetector.detector.Classifier;
import spamdetector.detector.TestCase;
import tester.Solver;

public class ZurasDetector implements Solver<TestCase, Classifier> {

	private int sampleNum;
	private double [] weights;
	private int [] classifiedSamples;
	private double bestErrorValue;
	private Set<Integer> used = new HashSet<>();
	private ArrayList<Integer> indexList = new ArrayList<>();
	private ArrayList<Double> coefList = new ArrayList<>();
	
	
	private int getClassifierAnswer(String email, String classifier, boolean isSpam) {
		String firstWord;
		String secondWord = "";
		int specialPos = classifier.indexOf("|") + classifier.indexOf("&") + 1;
		if (specialPos == -1) {
			if (isSpam) {
				if (email.contains(classifier)) return 0;
				return 1;
			}
			if (email.contains(classifier)) return 1;
			return 0;
		} else {
			firstWord = classifier.substring(0, specialPos);
			secondWord = classifier.substring(specialPos + 1);
			if (classifier.charAt(specialPos) == '|') {
				if (isSpam) {
					if (email.contains(firstWord) || email.contains(secondWord)) return 0;
					return 1;
				}
				if (email.contains(firstWord) || email.contains(secondWord)) return 1;
				return 0;
			} else {
				if (isSpam) {
					if (email.contains(firstWord) && email.contains(secondWord)) return 0;
					return 1;
				}
				if (email.contains(firstWord) && email.contains(secondWord)) return 1;
				return 0;
			}
		}
	}
	
	private double getClassifierError(TestCase t, String classifier) {
		double result = 0;
		for (int i = 0; i < t.getSpamEmails().length; i++) {
			int answer = getClassifierAnswer(t.getSpamEmails()[i], classifier, true);
			result += weights[i] * answer;
		}
		for (int i = 0; i < t.getUsefulEmails().length; i++) {
			int answer = getClassifierAnswer(t.getUsefulEmails()[i], classifier, false);
			result += weights[i + t.getSpamEmails().length] * answer;
		}	
		return result;
	}
	
	private void fillClassifiedSamples(TestCase t, String classifier) {
		for (int i = 0; i < t.getSpamEmails().length; i++) {
			int answer = getClassifierAnswer(t.getSpamEmails()[i], classifier, true);
			classifiedSamples[i] = java.lang.Math.abs(answer - 1);
		}
		for (int i = 0; i < t.getUsefulEmails().length; i++) {
			int answer = getClassifierAnswer(t.getUsefulEmails()[i], classifier, false);
			classifiedSamples[i + t.getSpamEmails().length] = java.lang.Math.abs(answer - 1);
		}
	}
	
	private int getBestClassifier(TestCase t) {
		int bestIndex = -1;
		bestErrorValue = 1;
		for (int i = 0; i < t.getWeakClassifiers().length; i++) {
			if (!used.contains(i)) {
				double currError = getClassifierError(t, t.getWeakClassifiers()[i]);
				if (currError < bestErrorValue) {
					bestErrorValue = currError;
					bestIndex = i;
				}
			}
		}
		if (bestErrorValue >= 0.5) return -1;
		if (bestIndex != -1) used.add(bestIndex);
		return bestIndex;
	}
	
	private Classifier generateSolution() {
		Classifier result = new Classifier();
		int [] indexArr = new int[indexList.size()];
		for (int i = 0; i < indexList.size(); i++) indexArr[i] = indexList.get(i);
		double [] coefArr = new double[coefList.size()];
		for (int i = 0; i < coefList.size(); i++) coefArr[i] = coefList.get(i);
		result.setRuleIds(indexArr);
		result.setCoefs(coefArr);
		return result;
	}
	
	
	public Classifier solve(TestCase t) {
		sampleNum = t.getSpamEmails().length + t.getUsefulEmails().length;
		weights = new double[sampleNum];
		classifiedSamples = new int[sampleNum];
		for (int i = 0; i < sampleNum; i++) weights[i] = (double)1 / sampleNum;
		for (int i = 0; i < t.getWeakClassifiers().length; i++) {
			int currBest = getBestClassifier(t);
			if (currBest == -1) break;
			indexList.add(currBest);
			double alpha = 0.5 * java.lang.Math.log((1-bestErrorValue)/bestErrorValue);
			coefList.add(alpha);
			if (bestErrorValue == 0) break;
			double sum;
			fillClassifiedSamples(t, t.getWeakClassifiers()[currBest]);
			for (int j = 0; j < weights.length; j++) {
				if (classifiedSamples[j] == 1) sum = 1 - bestErrorValue;
				else sum = bestErrorValue;
				weights[j] = 0.5 * weights[j] / sum;
			}
		}
		
		return generateSolution();
	}

}
