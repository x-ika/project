package spamdetector.detector.students.Giglema;

import java.util.ArrayList;

import spamdetector.detector.*;
import tester.Solver;

public class GiglemaSolver implements Solver<TestCase, Classifier>{

	private double[] spamW;
	private double[] usefulW;
	private String[] spams;
	private String[] useful;
	private String[] classifiers;
	private double[] errors;
	private boolean[] used;
	private ArrayList<Integer> rules;
	private ArrayList<Double> coefs;
	
	@Override
	public Classifier solve(TestCase t) {
		spams = t.getSpamEmails();
		useful = t.getUsefulEmails();
		classifiers = t.getWeakClassifiers();
		spamW = new double[spams.length];
		usefulW = new double[useful.length];
		errors = new double[classifiers.length];
		used = new boolean[classifiers.length];
		rules = new ArrayList<>();
		coefs = new ArrayList<>();
		
		for (int i=0; i<spams.length; ++i)
			spamW[i] = 1/(double)(spams.length+useful.length);
		for (int i=0; i<useful.length; ++i)
			usefulW[i] = 1/(double)(spams.length+useful.length);
		
		while(true){
			for(int i = 0; i < classifiers.length; ++i)
				errors[i] = error(i);
			int h = getBest();
			if(h == -1) break;
			rules.add(h);
			coefs.add(0.5*Math.log((1-errors[h])/errors[h]));
			used[h] = true;
			newWeights(h);
		}
		
		Classifier res = new Classifier();
		int r[] = new int[rules.size()];
    	for(int i = 0; i <rules.size(); i++)
    		r[i] = rules.get(i);
		res.setRuleIds(r);
		double cf[] = new double[coefs.size()];
    	for(int i = 0; i <coefs.size(); i++)
    		cf[i] = coefs.get(i);
    	res.setCoefs(cf);
		return res;
	}
	
	private void newWeights(int h) {
		String c = classifiers[h];
		for(int i =0; i < spams.length; ++i)
			spamW[i] = 0.5*spamW[i]/(isSpam(c,spams[i]) ? 1-errors[h] : errors[h]);
		for(int i =0; i < useful.length; ++i)
			usefulW[i] = 0.5*usefulW[i]/(isSpam(c,useful[i])? 1-errors[h] : errors[h]);
	}
	
	private int getBest() {
		double error = 1;
		int res = -1;
		for(int i =0; i < classifiers.length; ++i){
			if(used[i]) continue;
			double e = errors[i];
			if(e < 0.5 && e < error){
				error = e;
				res = i;
			}
		}
		return res;
	}
	
	private double error(int h) {
		double res = 0;
		String c = classifiers[h];
		for(int i =0; i < spams.length; ++i){
			if(!isSpam(c,spams[i]))
				res += spamW[i];
		}
		for(int i =0; i < useful.length; ++i){
			if(isSpam(c,useful[i]))
				res += usefulW[i];
		}
		return res;
	}
	
	private boolean isSpam(String c, String email) {
		if(c.contains("|")){
			int ind = c.indexOf('|');
			return email.contains(c.substring(0,ind)) || email.contains(c.substring(ind+1));
		}
		if(c.contains("&")){
			int ind = c.indexOf('&');
			return email.contains(c.substring(0,ind)) && email.contains(c.substring(ind+1));
		}		
		return email.contains(c);
	}
}