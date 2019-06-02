package spamdetector.detector.students.gigi;

import java.util.ArrayList;

import spamdetector.detector.Classifier;
import spamdetector.detector.TestCase;
import tester.Solver;

public class GigiSolver implements Solver<TestCase,Classifier>{
	
	@Override
	public Classifier solve(TestCase t) {
		String[] cl = t.getWeakClassifiers();
		MyClassifier[] arr = new MyClassifier[cl.length];
		for(int i=0;i<arr.length;i++)
			arr[i]=new MyClassifier(cl[i]);
		MyEmail[] good = new MyEmail[t.getUsefulEmails().length];
		for(int i=0;i<good.length;i++){
			good[i] = new MyEmail(t.getUsefulEmails()[i],1.0/(t.getUsefulEmails().length
					+t.getSpamEmails().length));
			//System.out.println(good[i].getBody()+" "+good[i].getCoef());
		}
		MyEmail[] spam = new MyEmail[t.getSpamEmails().length];
		for(int i=0;i<spam.length;i++){
			spam[i] = new MyEmail(t.getSpamEmails()[i], 1.0/(t.getUsefulEmails().length
					+t.getSpamEmails().length));
			//System.out.println(spam[i].getBody()+" "+spam[i].getCoef());
		}
		
		ArrayList<Integer> ruleid = new ArrayList<>();
		ArrayList<Double> coefs = new ArrayList<>();
		boolean[] used = new boolean[arr.length];
		for(int i=0;i<used.length;i++)used[i]=false;
		while(true){
			boolean[] fgguessed=null;
			boolean[] fbguessed=null;
			boolean[] gguessed = new boolean[good.length];
			boolean[] bguessed = new boolean[spam.length];
			int minindex=-1;
			double minvalue=200;
			for(int i=0;i<arr.length;i++){
				if(!used[i]){
					double cur = getErrorRate(arr[i], good, spam,gguessed,bguessed);
					if(cur < minvalue){
						minvalue=cur;
						minindex=i;
						fgguessed=gguessed.clone();
						fbguessed=bguessed.clone();
					}
				}
			}
			//System.out.println(minvalue);
			if(minvalue>=0.5){
				//System.out.println("DAVABREAKE");
				break;
			}
			if(minvalue==0){
				ruleid.add(minindex);
				coefs.add(0.5*Math.log((1-minvalue)/minvalue));
				break;//best chcker evaaaah
			}
			used[minindex]=true;
			ruleid.add(minindex);
			coefs.add(0.5*Math.log((1-minvalue)/minvalue));
			for(int i=0;i<good.length;i++){
				if(fgguessed[i])
					good[i].setCoef(good[i].getCoef()*0.5/(1-minvalue));
				else good[i].setCoef(good[i].getCoef()*0.5/minvalue);
			}
			for(int i=0;i<spam.length;i++){
				if(fbguessed[i])
					spam[i].setCoef(spam[i].getCoef()*0.5/(1-minvalue));
				else spam[i].setCoef(spam[i].getCoef()*0.5/minvalue);
			}
		}
		Classifier c = new Classifier();
		double[] cans = new double[coefs.size()];
		for(int i=0;i<coefs.size();i++){
			cans[i]=coefs.get(i);
			//System.out.println(coefs.get(i));
		}
		int[] rans = new int[ruleid.size()];
		for(int i=0;i<ruleid.size();i++){
			rans[i]=ruleid.get(i);
			//System.out.println(ruleid.get(i));
		}
		c.setCoefs(cans);
		c.setRuleIds(rans);
		return c;
	}
	private double getErrorRate(MyClassifier c,MyEmail[] good,MyEmail[] spam,
			boolean[] gguessed,boolean bguessed[]){
		double ans=0;
		for(int i=0;i<good.length;i++){
			if(c.test(good[i])){
				ans+=good[i].getCoef();
				gguessed[i]=false;
			}
			else gguessed[i]=true;
		}
		for(int i=0;i<spam.length;i++){
			if(!c.test(spam[i])){
				ans+=spam[i].getCoef();
				bguessed[i]=false;
			}
			else bguessed[i]=true;
		}
		return ans;
	}
	class MyClassifier{
		private String first;
		private String second;
		private int type;
		public MyClassifier(String c){
			if(c.indexOf('|')==-1){
				if(c.indexOf('&')==-1){
					first=c;
					second=c;
					type=0;
				}
				else{
					first=c.substring(0, c.indexOf('&'));
					second=c.substring(c.indexOf('&')+1,c.length());
					type=1;
				}
			}
			else{
				first=c.substring(0, c.indexOf('|'));
				second=c.substring(c.indexOf('|')+1,c.length());
				type=2;
			}
		}
		public boolean test(MyEmail e){
			boolean f=false,s=false;
			if(e.getBody().indexOf(this.first)!=-1)
				f=true;
			if(e.getBody().indexOf(this.second)!=-1)
				s=true;
			if(this.type==0 && f)
				return true;
			if(this.type==1 && f && s)
				return true;
			if(this.type==2 && (f || s))
				return true;
			return false;
		}
		public String getFirst() {
			return first;
		}
		public void setFirst(String first) {
			this.first = first;
		}
		public String getSecond() {
			return second;
		}
		public void setSecond(String second) {
			this.second = second;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
	}
	class MyEmail{
		private double coef=0.9;
		private String body;
		public MyEmail(String body,double coef){
			this.coef = coef;
			this.body = body;
		}
		public double getCoef() {
			return coef;
		}
		public void setCoef(double coef) {
			this.coef = coef;
		}
		public String getBody() {
			return body;
		}
		public void setBody(String body) {
			this.body = body;
		}
	}
}
