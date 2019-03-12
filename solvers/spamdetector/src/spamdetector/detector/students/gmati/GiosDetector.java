package spamdetector.detector.students.gmati;

import java.util.*;
import spamdetector.detector.Classifier;
import spamdetector.detector.TestCase;
import tester.Solver;

public class GiosDetector implements Solver<TestCase, Classifier> {
	private String[] spams;// = {"a", "abb", "adb"};
	private String[] useful;// = {"a", "c", "c"};
	private String[] rules;// = { "a|k", "ad"};
	private ArrayList<Integer> ruleIds = new ArrayList<>();
	private ArrayList<Double> coefs = new ArrayList<>();
	private boolean[] used;
	private double[] indexes;
	private int totalMails;
	
	public Classifier solve(TestCase testCase) {
        Classifier result = new Classifier();
        spams = testCase.getSpamEmails();
        useful = testCase.getUsefulEmails();
        rules = testCase.getWeakClassifiers();
        used = new boolean[rules.length];
        totalMails += (spams.length + useful.length);
        indexes = new double[totalMails];
        for (int i = 0; i < indexes.length; i++) {
			indexes[i] = 1.0/(double)totalMails;
		}
//        System.out.println(Arrays.toString(rules));
        cretaeClassifier();
        double[] c = new double[coefs.size()];
        int[] r = new int[ruleIds.size()];
        for(int i = 0; i < coefs.size(); i++){
        	c[i] = coefs.get(i);
        	r[i] = ruleIds.get(i);
        }
//        System.out.println(Arrays.toString(ruleIds.toArray()));
        result.setCoefs(c);
        result.setRuleIds(r);
        
        return result;
    }
	
	private void cretaeClassifier() {
		while(true){
			if(isPerfect())
				break;
			int curClassifier = getBestClassifier();
			if(curClassifier < 0){
				break;
			}
			used[curClassifier] = true;
			classify(curClassifier);
		}
	}

	private boolean isPerfect() {
		for(int i = 0; i < totalMails; i++){
			double sum = 0;
			for(int j = 0; j < ruleIds.size(); j++){
				int c = 1;
				if(i < spams.length){
					if(!isSpam(spams[i], rules[j])){
						c = -1;
					}
				}else{
					if(isSpam(useful[i-spams.length], rules[j])){
						c = -1;
					}
				}
				sum += coefs.get(j)*c;
			}
			if(i < spams.length && sum > 0)
				return false;
			if(i >= spams.length && sum < 0)
				return false;
		}
		
		return ruleIds.size() > 0;
	}

	private void classify(int curClassifier) {
		String classifier = rules[curClassifier];
		boolean[] mistakes = new boolean[totalMails];
		double error = getError(classifier, mistakes);
		double newCoef = Math.log((1.0-error)/error)*0.5;
		for(int i = 0; i < totalMails; i++){
			if(mistakes[i]){
				indexes[i] = indexes[i] * (0.5/error);
			}else{
				indexes[i] = indexes[i] * (0.5/(1-error));
			}
		}
		coefs.add(newCoef);
		ruleIds.add(curClassifier);
		
	}



	private int getBestClassifier() {
		int res = -1;
		double bestError = 1;
		for(int i = 0; i < rules.length; i++){
			if(!used[i]){
				double tmpError = getError(rules[i], new boolean[totalMails]);
				if(tmpError < bestError){
					bestError = tmpError;
					res = i;
				}
			}
		}
		if(bestError > 0.5)
			res = -1;
		return res;
	}



	private double getError(String classifier, boolean[] mistakes) {
		double error = 0;
		for(int i = 0; i < spams.length; i++){
			if(!isSpam(spams[i], classifier)){
				error += indexes[i];
				mistakes[i] = true;
			}
		}
		for(int i = 0; i < useful.length; i++){
			if(isSpam(useful[i], classifier)){
				error += indexes[spams.length + i];
				mistakes[spams.length + i] = true;
			}
		}
		return error;
	}



	private boolean isSpam(String mail, String classifier) {
		List<String> spamWords = new ArrayList<>();
		StringTokenizer tk = new StringTokenizer(classifier, "|&");
		while(tk.hasMoreTokens())
			spamWords.add(tk.nextToken());
		boolean res = false;
		if(classifier.contains("|")){
			for(String w: spamWords){
				if(mail.contains(w))
					return true;
			}
		}else{
			res = true;
			for(String w: spamWords){
				if(!mail.contains(w))
					return false;
			}
		}
		return res;
	}
	
}
