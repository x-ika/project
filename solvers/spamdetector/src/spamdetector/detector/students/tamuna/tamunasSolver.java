package spamdetector.detector.students.tamuna;

import java.util.ArrayList;

import spamdetector.detector.Classifier;
import spamdetector.detector.TestCase;
import tester.Solver;

public class tamunasSolver implements Solver<TestCase, Classifier>{

	public String [] classifiers;
	public String [] good;
	public String [] spam;
	public boolean [] used;
	public double [] goodCoef;
	public double [] spamCoef;
	public boolean [] correctGood;
	public boolean [] correctSpam;
	public boolean [] lastcorrectGood;
	public boolean [] lastcorrectSpam;
	ArrayList<Double> coef;
	ArrayList<Integer> saveId;
	
	public Classifier solve(TestCase t) {
		coef = new ArrayList<>();
		saveId = new ArrayList<>();
		
		
		classifiers = t.getWeakClassifiers();
		good = t.getUsefulEmails();
		spam = t.getSpamEmails();
		
		goodCoef = new double[good.length];
		spamCoef = new double[spam.length];
		correctGood = new boolean[good.length];
		correctSpam = new boolean[spam.length];
		used = new boolean [classifiers.length];
		
		for (int i = 0; i < goodCoef.length; i++) {
			goodCoef[i] = 1.0/(good.length + spam.length);
			
		}
		for (int i = 0; i < spamCoef.length; i++) {
			spamCoef[i] = 1.0/(good.length + spam.length);
			
		}
		while(true) {
			int ind = 0;
			double max = 200;
			for (int i = 0; i < classifiers.length; i++) {
				if(!used[i])	{
					double k  = countErrors(classifiers[i]);
					if(k < max) {
						max = k;
						ind = i;
						lastcorrectGood = correctGood.clone();
						lastcorrectSpam = correctSpam.clone();
					}
				}
			}
			used[ind] = true;
			//System.out.println(countErrors(classifiers[0]));
			//System.out.println(check(classifiers[0], spam[1]));
			if(max >= 0.5 || max == 0) break;
			coef.add(0.5 * Math.log((1 - max )/ max));
			saveId.add(ind);
			for (int i = 0; i < lastcorrectGood.length; i++) {
				if(!lastcorrectGood[i]) {
					goodCoef[i] = goodCoef[i] / (2 * max);
					
				} else {
					goodCoef[i] = goodCoef[i] / (2 * (1 - max));
				}
			}
			for (int i = 0; i < lastcorrectSpam.length; i++) {
				if(!lastcorrectSpam[i]) {
					spamCoef[i] = spamCoef[i] / (2 * max);
					
				} else {
					spamCoef[i] = spamCoef[i] / (2 * (1 - max));
				}
			}
			
		}
		
		double[] arr = new double[coef.size()];
		for (int i = 0; i < coef.size(); i++) {
			arr[i] = coef.get(i);
		}
		int[] arr1 = new int[saveId.size()];
		for (int i = 0; i < saveId.size(); i++) {
			arr1[i] = saveId.get(i);
		}
		
		Classifier obj = new Classifier();
		obj.setCoefs(arr);
		obj.setRuleIds(arr1);
		
		return obj;
	}
	
	public double countErrors(String c) {
		double q = 0;
		
		for (int i = 0; i < good.length; i++) {
			if(check(c, good[i])) {
				q = q + goodCoef[i];
				correctGood[i] = false;
			} else correctGood[i] = true;
		}
		
		for (int i = 0; i < spam.length; i++) {
			if(!check(c, spam[i])) {
				q = q + spamCoef[i];
				correctSpam[i] = false;
			} else correctSpam[i] = true;
		}
		
		return q;
	}
	
	public boolean check(String c, String email) {
		String a, b;
		if(c.indexOf('&') != -1) {
			a = c.substring(0, c.indexOf('&'));
			b = c.substring(c.indexOf('&') + 1, c.length());
			if(email.indexOf(a) != -1 && email.indexOf(b) != -1) 
				return true;
			else return false;
		}
		
		else if(c.indexOf('|') != -1) {
			a = c.substring(0, c.indexOf('|'));
			b = c.substring(c.indexOf('|') + 1, c.length());
			if(email.indexOf(a) != -1 || email.indexOf(b) != -1) 
				return true;
			else return false;
		}
		else return (email.indexOf(c) != -1);
	}
	
}
