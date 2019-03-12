package spamdetector.detector.students.eakhv;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import spamdetector.detector.Classifier;
import spamdetector.detector.TestCase;
import tester.Solver;


public class ElenesSolver implements Solver<TestCase, Classifier>{
	
	int[] coefficients;
	boolean[][] mails;
	double errors[];
	double weights[];
	
	
	public int minError(String[] classifiers,	String[] usefullMails,	String[] spamMails, List<Integer> seenClassifiers){
		double minErr=1 ;
		int index=-1;
		for(int i=0;i<classifiers.length;i++){
			if(seenClassifiers.contains(i)) continue;
			String spamWord=classifiers[i];
			
			int type = checkWord(spamWord);
			double error =0;
			if(type==0){
				
				for(int j=0;j<usefullMails.length;j++){
					String mail = usefullMails[j];
					if(mail.contains(spamWord)){
						error+=weights[j];
						
						mails[i][j]=false;
					}else{
						mails[i][j]=true;
					}
					
				}
				for(int j=0;j<spamMails.length;j++){
					String mail = spamMails[j];
					if(!mail.contains(spamWord)){
						error+=weights[usefullMails.length+j];
						mails[i][usefullMails.length+j]=false;
					}else{
						mails[i][usefullMails.length+j]=true;
					}
					
				}
				
			}else if(type==1){
				String first=spamWord.substring(0,spamWord.indexOf("|"));
				String second=spamWord.substring(spamWord.indexOf("|")+1);
				for(int j=0;j<usefullMails.length;j++){
					String mail = usefullMails[j];
					if(mail.contains(first)||mail.contains(second)){
						error+=weights[j];
						mails[i][j]=false;
					}else{
						mails[i][j]=true;
					}
					
				}
				for(int j=0;j<spamMails.length;j++){
					String mail = spamMails[j];
					if(!mail.contains(first)&&!mail.contains(second)){
						error+=weights[usefullMails.length+j];
						mails[i][usefullMails.length+j]=false;
					}else{
						mails[i][usefullMails.length+j]=true;
					}
					
				}
			}else{
			
				String first=spamWord.substring(0,spamWord.indexOf("&"));
				String second=spamWord.substring(spamWord.indexOf("&")+1);
				for(int j=0;j<usefullMails.length;j++){
					String mail = usefullMails[j];
					if(mail.contains(first)&&mail.contains(second)){
						error+=weights[j];
						mails[i][j]=false;
					}else{
						mails[i][j]=true;
					}
					
				}
				for(int j=0;j<spamMails.length;j++){
					String mail = spamMails[j];
					if(!mail.contains(first)||!mail.contains(second)){
						error+=weights[usefullMails.length+j];
						mails[i][usefullMails.length+j]=false;
					}else{
						mails[i][usefullMails.length+j]=true;
					}
					
				}
				
			}
			errors[i]=(double)error;
			if(error<minErr){
				minErr=error;
				index = i;
			}
		}
		return index;
		
	}
	private int checkWord(String spamWord) {
		if( (!spamWord.contains("&"))&&(!spamWord.contains("|"))){
			return 0;
		}else if( !spamWord.contains("&")){
			return 1;
		}else{
			return 2;
		}
		
	}

	@Override
	public Classifier solve(TestCase t) {
		String[] classifiers=t.getWeakClassifiers();
		String[] usefulMails=t.getUsefulEmails();
		String[] spamMails=t.getSpamEmails();
		List<Integer> seenClassifiers = new ArrayList<>();
		
		int n = spamMails.length+usefulMails.length;
		double weight  =(double) 1/n;
	
		mails= new boolean [classifiers.length][n];
		errors = new double[classifiers.length];
		
		weights = new double[n];
		
		for(int i=0;i<n;i++) weights[i]=(double)weight;
		List<Double> alphas = new ArrayList<>();
		while(true){
			
			if(seenClassifiers.size()==classifiers.length)break;
			int index=minError(classifiers, usefulMails, spamMails,seenClassifiers);
			seenClassifiers.add(index);
			double minErr = errors[index];
			if(minErr>0.5) break;
			double alpha =(double) ( (Math.log((1 - minErr) / minErr))/2);
			alphas.add(alpha);
			changeWeights(n,index,minErr);
			
		}
		Classifier c = new Classifier();
		int ruleids[]= new int[alphas.size()];
		  double[] coefs= new double[alphas.size()];
		for(int i=0;i<alphas.size();i++){
			ruleids[i]=seenClassifiers.get(i);
			coefs[i]=alphas.get(i);			
		}
		c.setCoefs(coefs);
		c.setRuleIds(ruleids);
		return c;
	}

	

	private void changeWeights(int n,int index, double err) {
		for(int i=0;i<n;i++){
			if(!mails[index][i]){
				weights[i] = (double)(weights[i]/err)/2;
				
			}else{
				weights[i] =(double) (weights[i]/(1-err))/2;
				
			}
		}
	}
	

}
