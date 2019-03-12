package simplepoker.player.students.barbi;

import java.util.*;
import simplepoker.controller.Player;

public class MyPlayer implements Player {
	
	private double card;
	private int[] openraiseZero;
	private int[] openraiseOne;
	private int[] openraiseTwo;
	
	private double koef = 0.28897;
	private double allIn = 0.99711;
	private double AllInM = 0.997;
	private int learningNumb = 400;
	
	//private int[] callraise;
	private int darigeba;
	
	public void startHand(double card) {
		this.card = card;
		
	}

	public int makeBet(int me, int diller, int round, int[] money, int[] bank, Collection<Integer> inGame) {
		if(round == 0) darigeba ++;
		int n = money.length;
		if(darigeba == 1) createStatistics(bank, me, diller);
		ArrayList<Integer> arr = (ArrayList<Integer>) inGame;
		if(me != diller) fillStatistics(bank, me, diller, round);
		
		
		int max = 0;
		int pot = 0;
	
		for (int i = 0; i < bank.length; i++) {
			max = Math.max(max, bank[i]); pot += bank[i];
		}
			
		
		if(n == 2){
			if(darigeba < learningNumb){
				 int bet = betWithoutInfo(me, diller, round, money, bank,  pot, max);
				 return bet;
				
			}else{
				int bet2 = betWithInfo(me, diller, round, money, bank,  pot, max);
				return bet2;
			}
		}else{
			
			return playInMultiWay(me, diller, round, money, bank, pot, max);
			
		}
		
	}
	
	private int playInMultiWay(int me, int diller, int round, int[] money, int[] bank, int pot, int max){
		int toCall = max - bank[me];
		
		if(round == 0){
			if(diller == me){
				if(card >= AllInM) return money[me];
				else return Math.min(1, money[me]);
			}else{
				if(toCall == 0){
					if(card >= AllInM) return money[me];
					else return Math.min(1, money[me]);
				}else{
					if(card >= AllInM) return money[me];
					else return 0;
				}
			}
		}
		
		if(round == 1 || round == 2 || round == 3){
			if(card >= allIn) return money[me];
			else return 0;
		
		}
		
		
		
		return 0;
	}
	 
	private int betWithInfo(int me, int diller, int round, int[] money, int[] bank, int pot, int max){
		
		int toCall = max - bank[me];
		int n = money.length;
		int mteri = (me + 1) % n;
		double raisefrequencyZero  = (double)openraiseZero[mteri]/(double)darigeba;
		//System.out.println(raisefrequencyZero + " aeeeeee");
		double myCallFreqZero = 1 -  raisefrequencyZero * koef;
		
		if(round == 0){
			if(diller == me){
				if(card >= 0.97)	return Math.min(money[me], 30);
				return 1;
			}else {
				if(card > allIn) return money[me];
				if(card >= myCallFreqZero && toCall <= 15){
					 return Math.min(toCall, money[me]);
				}
				if(toCall <= 2 ){
					return Math.min(money[me], toCall);
				} 
				else {
					return 0;
				}
			}
		}
		
		
		if(round == 1){
			double raisefrequencyOne  = (double)openraiseOne[mteri]/(double)darigeba;
			double myCallFreqOne = 1 -  raisefrequencyOne * koef;
			
			if(diller == me){
				if(toCall != 0){
					if(card <= allIn) return 0;
					else return money[me];
				}else{
					if(card >= 0.98546)	return Math.min(money[me], 50);
					if(card >= 0.97) return Math.min(money[me], 30);
					return 0;
				}
			}else{
				
				if(card > allIn) return money[me];
				
				if(toCall == 0) {
					if(card >= 0.97) return Math.min(money[me], 30);
					return Math.min(money[me], 1);
					
				}else{
					if(card >= myCallFreqOne && toCall <= 30) return Math.min(toCall, money[me]);
					else return 0;
				}
			}
		}
		
		
		if(round == 2){
			double raisefrequencyTwo  = (double)openraiseTwo[mteri]/(double)darigeba;
			double myCallFreqTwo = 1 -  raisefrequencyTwo * koef;
			
			if(diller == me){
				if(toCall != 0){
					if(card <= allIn) return 0;
					else return money[me];
				}else{
					if(card >= 0.98546)	return Math.min(money[me], 100);
					if(card >= 0.97) return Math.min(money[me], 50);
					return 0;
				}
			}else{
				
				if(card > allIn) return money[me];
				
				if(toCall == 0) {
					if(card >= 0.97) return Math.min(money[me], 50);
					return Math.min(money[me], 1);
					
				}else{
					if(card >= myCallFreqTwo && toCall <= 50) return Math.min(toCall, money[me]);
					else return 0;
				}
			}
		}
		
		
		if(round == 3){
			if(diller == me){
				if(card > allIn) return Math.min(money[me], toCall);;
				if(card >= 97){
					if(toCall == 0) return 0;
					if(toCall <= 30) return Math.min(toCall, money[me]);
					else return 0;
				}
				return 0;
				
			}else{
				
				if(card > allIn) return Math.min(money[me], toCall);;
				if(card >= 0.97){
					if(toCall == 0) return 0;
					
					if(toCall <= 30 ) return Math.min(toCall, money[me]);
					else return 0;
				}
				
				return 0;
			}

		}
		
		return 0;
	}
	

	private int betWithoutInfo(int me, int diller, int round, int[] money, int[] bank, int pot, int max){
		int toCall = max - bank[me];
	//	System.out.println(toCall + " toCall");
		
		if(round == 0) {
			if(diller == me){
				if(card >= 0.97)	return Math.min(money[me], 30);
				return 1;
			}
			else {
				if(card > allIn) return money[me];
				if(card >= 0.97){
					if(toCall <= 10) return Math.min(toCall, money[me]);
					else return 0;
				}
				
				if(toCall <= 2 ){
					return Math.min(money[me], toCall);
					
				} 
				else {
					return 0;
				}
				
			}
		}
		
		
		
		if(round == 1){
			if(diller == me){
				if(toCall != 0){
					if(card <= allIn) return 0;
					else return money[me];
				}else{
					if(card >= 0.98546)	return Math.min(money[me], 50);
					if(card >= 0.97) return Math.min(money[me], 30);
					return 1;
					
				}
			}else{
				
				if(card > allIn) return money[me];
				if(card >= 0.97){
					if(toCall == 0) return Math.min(money[me], 30);;
					
					if(toCall <= 20 ) return Math.min(toCall, money[me]);
					else return 0;
				}
				
				if(toCall == 0){
					return Math.min(money[me], 1);
					
				} 
				else {
					return 0;
				}
			}
		}
		
		if(round == 2){
			if(diller == me){
				if(toCall != 0){
					if(card <= allIn) return 0;
					else return money[me];
				}else{
					if(card >= 0.98546)	return Math.min(money[me], 100);
					if(card >= 0.97) return Math.min(money[me], 50);;
					return 1;
					
				}
			}else{
				
				if(card > allIn) return money[me];
				if(card >= 0.97){
					if(toCall == 0) return Math.min(money[me], 35);;
					
					if(toCall <= 30 ) return Math.min(toCall, money[me]);
					else return 0;
				}
				
				return 0;
			}
		}
		
		if(round == 3){
			if(diller == me){
				if(card > allIn) return Math.min(money[me], toCall);;
				if(card >= 97){
					if(toCall == 0) return 0;
					if(toCall <= 30) return Math.min(toCall, money[me]);
					else return 0;
				}
				
				return 0;
				
			}else{
				
				if(card > allIn) return Math.min(money[me], toCall);;
				if(card >= 0.97){
					if(toCall == 0) return 0;
					
					if(toCall <= 30 ) return Math.min(toCall, money[me]);
					else return 0;
				}
				
				return 0;
			}

		}
		
		
		
		
		return 0;
	}
	
	
	private void fillStatistics(int[] bank, int me, int diller, int round){
		int n = bank.length;
		
		for (int i = diller; i%n != me; i++) {
			if(bank[i % n] != bank[(i + 1) % n]){
				if(round == 0) openraiseZero[i % n] ++;
				if(round == 1) openraiseOne[i % n] ++;
				if(round == 2) openraiseTwo[i % n] ++;
				break;
			}
		}
		
	}
	
	
	
	
	private void createStatistics(int[] bank, int me, int diller){
		openraiseZero = new int[bank.length];
		openraiseOne = new int[bank.length];
		openraiseTwo= new int[bank.length];
		
	}
	
	public void finishHand(double[] allOpenedCards) {
		
	}

}
