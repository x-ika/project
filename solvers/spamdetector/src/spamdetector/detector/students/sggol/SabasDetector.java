package spamdetector.detector.students.sggol;

import java.util.ArrayList;
import java.util.Collections;

import spamdetector.detector.Classifier;
import spamdetector.detector.TestCase;
import tester.Solver;

public class SabasDetector implements Solver<TestCase, Classifier> {
	int usefulNum;
	int spamNum;
	int totalNum;
	String[] usefulEmails;
	String[] spamEmails;
	String[] weakClassifiers;
	double [] usefCoefs;
	double [] spamCoefs;
	boolean [] pasives;
	class pairT implements Comparable<pairT>{
		public int pos;
		public double errorRate;
		public int compareTo(pairT p1) {
			if(this.errorRate > p1.errorRate){
				return 1;
			}
			return -1;
		}
	}
	
	public Classifier solve(TestCase testCase) {
		usefulEmails = testCase.getUsefulEmails();		
		spamEmails = testCase.getSpamEmails();
		weakClassifiers = testCase.getWeakClassifiers();
		pasives = new boolean[weakClassifiers.length];
		usefulNum = usefulEmails.length;
		spamNum = usefulEmails.length;
		totalNum = usefulNum + spamNum;
		usefCoefs = new double[usefulNum];
		spamCoefs = new double[spamNum];
		for(int i=0;i<usefulNum;i++){
			usefCoefs[i]= 1.0/totalNum;
		}
		for(int i=0;i<spamNum;i++){
			spamCoefs[i]= 1.0/totalNum;
		}		
		ArrayList<pairT> L = getErrorRates();
		
		
		int [] arr1 = new int[weakClassifiers.length];
		double [] arr2 = new double[weakClassifiers.length];
		int k=0;
		while(L.size()>0){
			Collections.sort(L);
			pairT p1 = L.remove(0);
			pasives[p1.pos] = true;
			double rate  = p1.errorRate;
			if(rate>0.5){
				break;
			}
			double alfa = (Math.log((1-rate)/rate)) ;
			alfa=alfa*0.5;
//			System.out.print(p1.pos+ "    " );
//			System.out.println(alfa);
			arr1[k] = p1.pos;
			arr2[k] = alfa;
			
			for(int j=0;j<usefulNum;j++){
				String msg = usefulEmails[j];
				if(msg.contains(weakClassifiers[p1.pos])){
					usefCoefs[j]*=1.0/(2*rate);
				}else{
					usefCoefs[j]*=1.0/(2*(1-rate));
				}
			}
			for(int j=0;j<spamNum;j++){
				String msg = spamEmails[j];
				if(!msg.contains(weakClassifiers[p1.pos])){
					spamCoefs[j]*=1.0/(2*rate);
				}else{
					spamCoefs[j]*=1.0/(2*(1-rate));
				}
			}
			L = getErrorRates();
			k++;
		}
		Classifier result = new Classifier();
        result.setRuleIds(arr1);
        result.setCoefs(arr2);
		
		return result;
	}

	private ArrayList<pairT> getErrorRates() {
		ArrayList<pairT> L = new ArrayList<>();
		
		for(int i=0;i<weakClassifiers.length;i++){
			if(!pasives[i]){
				String cl = weakClassifiers[i];
				double rate = 0;
				pairT p = new pairT();
				for(int j=0;j<usefulNum;j++){
					String msg = usefulEmails[j];
					if(msg.contains(cl)){
						rate+=usefCoefs[j];
					}
				}
				for(int j=0;j<spamNum;j++){
					String msg = spamEmails[j];
					if(!msg.contains(cl)){
						rate+=spamCoefs[j];
					}
				}
				p.errorRate=rate;
				p.pos=i;
				L.add(p);			
			}
		}
		
		return L;
	}

}
