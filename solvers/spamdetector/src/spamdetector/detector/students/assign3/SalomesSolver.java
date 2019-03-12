package spamdetector.detector.students.assign3;

import java.util.ArrayList;
import java.util.StringTokenizer;

import spamdetector.detector.Classifier;
import spamdetector.detector.TestCase;
import tester.Solver;

public class SalomesSolver implements Solver<TestCase, Classifier>{

	private static final double MAX_INT = 88888888;
	public Classifier c;
	public TestCase testCase;
	private ArrayList<Integer> ruleIds;
	private ArrayList <Double> coefs;
	private double [] weights;
	private boolean [] isError;
	private boolean [] usedClassifiers;
	private double [] errors;
	
	
	private void initialize(String[] classifiers){
		usedClassifiers = new boolean[classifiers.length];
		int size = testCase.getSpamEmails().length + testCase.getUsefulEmails().length;
		ruleIds = new ArrayList<>();
		coefs = new ArrayList<>();
		weights = new double[size];
		isError = new boolean[size];
		errors = new double[classifiers.length];
		
		for(int i = 0; i < weights.length; i++){
			weights[i] = (double) 1/size;
		}
	}
	
	
	public Classifier solve(TestCase t) {
		
		c = new Classifier();
		
		this.testCase = t;
		
		String [] classifiers = t.getWeakClassifiers();
		
		initialize(classifiers);
		
		while(true){
			for(int i = 0; i < classifiers.length; i++){
				if(!usedClassifiers[i]){
					errors[i] = countError(classifiers[i], t.getSpamEmails(), t.getUsefulEmails());
					reset();
				}
			}
			int best = getBest();
			
			if(best == - 1) break;
			else if(errors[best] > 0.5)break;
			else if(saveAndChange(classifiers[best], best) < 0) break;
				
		}
		
		double[]towriteCoefs = new double[coefs.size()];
		for(int i = 0; i < coefs.size(); i++)
			towriteCoefs[i] = coefs.get(i);
		
		c.setCoefs(towriteCoefs);
	
		int[]towriteIds = new int[ruleIds.size()];
		for(int i = 0; i < ruleIds.size(); i++)
			towriteIds[i] = ruleIds.get(i);
		
		c.setRuleIds(towriteIds);	
		return c;
	}
	
	
	

	private int saveAndChange(String clasificator, int best){
		double err = countError(clasificator, testCase.getSpamEmails(), testCase.getUsefulEmails());
		
		ruleIds.add(best);

		if(err == 0){
			return -1;
		}else{
			double alfa = (double) ( (Math.log((1 - err) / err))/ 2);

			coefs.add(alfa);
		}
		
		usedClassifiers[best] = true;
		
		for(int i = 0; i < weights.length; i++){
			if(isError[i]){
				weights[i] = (double)((weights[i]/err)/2);
				
			}else{
				weights[i] = (double)((weights[i]/(1-err))/2);
			}
		}
		reset();
		
		return 0;
	}
	
	

	
	private double countError(String currC, String[] spams, String [] usefuls) {
		double error = 0;
		if(currC.indexOf("|") > -1){
			return countOr(currC, spams, usefuls);
		}else if(currC.indexOf("&") > -1){
			return countAnd(currC, spams, usefuls);	
		}else{
			for(int i = 0; i < spams.length; i++){
				if(spams[i].indexOf(currC) <= -1 ){
					error += weights[i];
					isError[i] = true;
				}
			}
			for(int j = 0; j < usefuls.length; j++){
				if(usefuls[j].indexOf(currC) > -1 ){
					error += weights[spams.length + j];	
					isError[spams.length + j] = true;
				}
			}
		}
		return error;	
	}
	
	
	private int countOr(String currC, String [] spams, String[] usefuls){
		int error = 0;
		StringTokenizer tk = new StringTokenizer(currC, "|");
		
		String []tokens = new String [2];
		int t = 0;
		while(tk.hasMoreTokens()){
			tokens[t] = tk.nextToken();
			t++;
		}
		
		for(int i = 0; i < spams.length; i++){
			boolean has = false;
			for(int k = 0 ; k < tokens.length; k++){
				if(spams[i].indexOf(tokens[k]) > -1 ){
					has = true;
				}
			}
			if(!has) {
				error += weights[i];
				isError[i] = true;
			}
		}
		
		for(int j = 0; j < usefuls.length; j++){
			boolean has = false;
			for(int k = 0; k < tokens.length; k++){
				if(usefuls[j].indexOf(tokens[k]) > -1 ){
					if(!has) {
						error += weights[spams.length + j];
						isError[spams.length + j] = true;
						has = true;
					}
				}
			}
		}
		return error;
	}
	
	private void reset(){
		for(int i = 0; i < isError.length; i++){
			isError[i] = false;
		}
	}
	
	private int countAnd(String currC, String [] spams, String[] usefuls){
		int error = 0;
		StringTokenizer tk = new StringTokenizer(currC, "&");
		String []tokens = new String [2];
		int t = 0;
		while(tk.hasMoreTokens()){
			tokens[t] = tk.nextToken();
			t++;
		}
		int i = 0;
		for(; i < spams.length; i++){
			boolean has = false;
			for(int k = 0 ; k < tokens.length; k++){
				if(spams[i].indexOf(tokens[k]) <= -1 ){
					if(!has){
						error += weights[i];
						isError[i] = true;
						has = true;
					}
				}
			}
		}
		for(int j = 0; j < usefuls.length; j++){
			boolean has = false;
			for(int k = 0 ; k < tokens.length; k++){
				if(usefuls[j].indexOf(tokens[k]) > -1 ){
					if(has){ 
						error += weights[spams.length + j];
						isError[spams.length + j] = true;
					}
					else has = true;
				}
			}
		}
		return error;
	}
	
	private int getBest(){
		int ind = -1;
		double lowest = MAX_INT;
		for(int i = 0; i < errors.length; i++){
			if(!usedClassifiers[i]){
				if(errors[i] < lowest){
					lowest = errors[i];
					ind = i;
				}
			}
		}
		return ind;
	}
	
}
