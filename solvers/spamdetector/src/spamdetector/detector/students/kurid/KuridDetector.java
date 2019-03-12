package spamdetector.detector.students.kurid;

import java.util.*;
import spamdetector.detector.Classifier;
import spamdetector.detector.TestCase;
import tester.Solver;

public class KuridDetector implements Solver<TestCase, Classifier> {

	private double weightsOfSpamEmail [];
	private double weightsOfUsefulEmail [];
	
	
	private String[] spams;
	private String[] useful;
	private String[] classifiers;
	private boolean[] alreadyUsed;
	
	private HashMap<Integer, Double> errors;
	private ArrayList<Integer> rules;
	private ArrayList<Double> coefs;
	
	
	public KuridDetector() {
		errors = new HashMap<>();
		rules = new ArrayList<>();
		coefs = new ArrayList<>();
	}
	
	public Classifier solve(TestCase testCase) {
		spams = testCase.getSpamEmails();
		useful = testCase.getUsefulEmails();
		classifiers = testCase.getWeakClassifiers();
		alreadyUsed = new boolean[classifiers.length];
		firstWeights(spams.length, useful.length);	
		while(true){
			computeErrors();
			int h = chooseBest();
			if(h == -1) break;
			double a = computeCoeficient(h);
			rules.add(h);
			coefs.add(a);
			alreadyUsed[h] = true;
			newWeights(h);
		}
		
		Classifier result = new Classifier();
    	
		int rulesIds[] = new int[rules.size()];
    	for(int i = 0; i <rules.size(); i++){
    		rulesIds[i] = rules.get(i);
    	}
		result.setRuleIds(rulesIds);
		
		double cf[] = new double[coefs.size()];
    	for(int i = 0; i <coefs.size(); i++){
    		cf[i] = coefs.get(i);
    	}
    	result.setCoefs(cf);
        return result;
    }

	
	private void newWeights(int h) {
		String cl = classifiers[h];
		for(int k =0; k < spams.length; k++){
			double a = weightsOfSpamEmail[k]; 
			if(isSpam(cl,spams[k])){
				weightsOfSpamEmail[k] = 0.5*(a/(1-errors.get(h)));
			}else{
				weightsOfSpamEmail[k] = 0.5*(a/errors.get(h));
			}
		}
		
		
		for(int k =0; k < useful.length; k++){
			double a = weightsOfUsefulEmail[k];
			if(!isSpam(cl,useful[k])){
				weightsOfUsefulEmail[k] = 0.5*(a/(1-errors.get(h)));
			}else{
				weightsOfUsefulEmail[k] = 0.5*(a/errors.get(h));
			}
		}
	}


	private double computeCoeficient(int h) {
		double e = errors.get(h);
		double result = (1-e)/e;
		result = 0.5*Math.log(result);
		return result;
	}

	
	private int chooseBest() {
		double error = 1;
		int result = -1;
		for(int i =0; i < classifiers.length; i++){
			if(!alreadyUsed[i]){
				double e = errors.get(i);
				if(e < 0.5 && e < error){
					error = e;
					result = i;
				}
			}
		}
		
		return result;
	}

	private void computeErrors() {
		for(int i = 0; i < classifiers.length; i++){
			double e = error(i);
			errors.put(i, e);
		}
	}
	

	private double error(int h) {
		double result = 0;
		String cl = classifiers[h];
		for(int k =0; k < spams.length; k++){
			if(!isSpam(cl,spams[k])){
				result += weightsOfSpamEmail[k];
			}
		}
		for(int k =0; k < useful.length; k++){
			if(isSpam(cl,useful[k])){
				result += weightsOfUsefulEmail[k];
			}
		}
		return result;
	}


	private boolean isSpam(String cl, String email) {
		if(cl.contains("|")){
			String s1 = cl.substring(0,cl.indexOf('|'));
			String s2 = cl.substring(cl.indexOf('|')+1);
			if(email.contains(s1) || email.contains(s2)){
				return true;
			}
		}
		if(cl.contains("&")){
			String s1 = cl.substring(0,cl.indexOf('&'));
			String s2 = cl.substring(cl.indexOf('&')+1);
			if(email.contains(s1) && email.contains(s2)){
				return true;
			}
		}
		if(email.contains(cl)) return true;
		
		return false;
	}


	private void firstWeights(int spamSize, int usefullSize) {
		weightsOfSpamEmail = new double[spamSize];
		weightsOfUsefulEmail = new double[usefullSize];
		for(int i = 0; i < Math.max(spamSize, usefullSize); i++){
			if(i < spamSize){
				weightsOfSpamEmail[i] = 1/(double)(spamSize+usefullSize);
			}
			if(i < usefullSize){
				weightsOfUsefulEmail[i] = 1/(double)(spamSize+usefullSize);
			}
		}
	}

}
