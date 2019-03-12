package spamdetector.detector.students.kurid;

import java.util.Map;

import spamdetector.detector.Classifier;
import spamdetector.detector.TestCase;
import tester.Solver;

public class work implements Solver<TestCase, Classifier> {

//	private ArrayList<Integer> ruleIds ;
	private double weightsOfSpamEmail [];
	private double weightsOfUsefulEmail [];
	private Map<Integer, Double> errors;
	
	public Classifier solve(TestCase testCase) {
		String[] spams = testCase.getSpamEmails();
		String[] useful = testCase.getUsefulEmails();
		firstWeights(spams.length, useful.length);
		
//		while(true){
			computeErrors();
			int  h = chooseBest();
			int a = computeCoefficient();
//		}
		
		
		
//		firstWeights(3, 2);
//		System.out.println("spams");
//		for (int i = 0; i < weightsOfSpamEmail.length; i++) {
//			System.out.println( weightsOfSpamEmail[i]);
//		}
//		System.out.println("useful");
//		for (int i = 0; i < weightsOfUsefulEmail.length; i++) {
//			System.out.println( weightsOfUsefulEmail[i]);
//		}
		
		
		
    	Classifier result = new Classifier();
//    	ruleIds = new ArrayList<>();

    	
//    	int rules[] = new int[ruleIds.size()];
//    	for(int i = 0; i <ruleIds.size(); i++){
//    		rules[i] = ruleIds.get(i);
//    	}
    	result.setRuleIds(new int[]{1, 2});
    	result.setCoefs(new double[]{1.5, 3.5});
        return result;
    }

	private int computeCoefficient() {
		return 0;
	}

	private int chooseBest() {
		return 0;
	}

	private void computeErrors() {

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
	
	
	public static void main(String[] args) {
		KuridDetector k = new KuridDetector();
		k.solve(new TestCase());
		
//		String cl = "asd|qqq";
//		String s1 = cl.substring(0,cl.indexOf('|'));
//		String s2 = cl.substring(cl.indexOf('|')+1);
//		System.out.println("s1 = " + s1);
//		System.out.println("s2 = " + s2);
	}

}

