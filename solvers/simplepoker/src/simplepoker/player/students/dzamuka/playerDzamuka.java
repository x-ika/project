package simplepoker.player.students.dzamuka;

import java.util.Collection;

import simplepoker.controller.*;

public class playerDzamuka implements Player {
	private double card;
	private double roundsCalled = 2;
	private double roundsRaised = 2;
	private double roundsReraised = 2;
	private double roundsPlayed = 3;
	
	public void startHand(double card) {
		this.card = card;
	}
	
	//ramdeni unda gavcallo
	private int howMuchToCall(int me, int[] bank, int[] money){
		int n = 0;
		for(int i = 0; i<bank.length; i++){
			n = Math.max(n, bank[i]);
		}
		return Math.min(n - bank[me], money[me]);
	}
	
	//ramdenia poti
	private int howMuchInBank(int me, int[] bank){
		int bankSize = 0;
		for(int i=0; i<bank.length; i++){
			bankSize += bank[i];
		}
		return bankSize;
	}
	
	private double countMyCardStreangth(double num1, double num2){
		double k = 1 - num1/num2;
		return (card - k)/(1-k);
	}
	
	private double bigRaise = 0;
	private double myRaised = 3;
	private boolean iRaised = false;
	private boolean iChecked = false;
	private boolean iCalled = false;
	
	private double mySavedChance;
	
	public int makeBet(int me, int diller, int round, int[] money, int[] bank,
			Collection<Integer> inGame) {
		double bankSize = howMuchInBank(me, bank);
		double toCall = howMuchToCall(me, bank, money);
		
		if(round == 0){
			if(toCall>20) bigRaise++;
			roundsPlayed++;
			iRaised = false;
			iCalled = false;
			iChecked = false;
			if(toCall == 0){
				if(card > 0.5){
					iRaised = true;
					myRaised++;
					return Math.min(3, money[me]);
				} else {
					double k = Math.random();
					if(k<0.2 && diller!=me){
						iRaised = true;
						myRaised++;
						return Math.min(3, money[me]);
					}
					if(roundsCalled/myRaised < 0.09){
						myRaised++;
						return Math.min(3, money[me]);
					}
					iChecked = true;
					return 0;
				}
			} else {
				roundsRaised++;
				double winningChance = countMyCardStreangth(roundsRaised,roundsPlayed);
				if(0.9 < winningChance){
					return money[me];
				}
				if(0.5 < winningChance){
					iCalled = true;
					return (int) toCall;
				}
				return 0;
			}
		} else if (round < 3){
			if(round==1){
				double k;
				if(iRaised){
					iRaised = false;
					if(toCall==0){
						roundsCalled++;
						k = countMyCardStreangth(roundsCalled, myRaised);
						mySavedChance = k;
						if(k>0.9) return Math.min(9, money[me]);
						if(k>0.5) return Math.min(3, money[me]);
						return 0;
					} else {
						roundsReraised++;
						k=countMyCardStreangth(roundsReraised, myRaised);
						mySavedChance = k;
						if(0.9 < k){
							return money[me];
						}
						if(0.5 < k){
							return (int) toCall;
						}
						return 0;
					}
				} else if(iCalled || (iChecked && me==diller)){
					if(toCall!=0){
						if(iChecked){
							if(toCall > 20) bigRaise++;
							roundsRaised++;
							iChecked = false;
						} else {
							iCalled = false;
						}
						if(toCall > 20){
							k = countMyCardStreangth(bigRaise, roundsPlayed);
						} else {
							k = countMyCardStreangth(roundsRaised, roundsPlayed);
						}
						mySavedChance = k;
						if(0.9 < k){
							return money[me];
						}
						if(0.5 < k) return (int) toCall;
						return 0;
					} else {
						if(iChecked){
							iChecked = false;
							mySavedChance = card;
						} else {
							iCalled = false;
							k = countMyCardStreangth(roundsRaised, roundsPlayed);
							mySavedChance = k;
						}
						return 0;
					}
				} else {
					iChecked = false;
					mySavedChance = card;
					if(toCall == 0) return 0;
					if(mySavedChance > 0.8) return (int) toCall;
					return 0;
				}
				
			} else {
				if(toCall!=0){
					if(mySavedChance>toCall/bankSize) return (int)toCall;
					return 0;
				}
				if(mySavedChance>0.9) {
					return Math.min(9878978,money[me]);
				}
				if(mySavedChance>0.6) return Math.min(money[me], (int)bankSize);
				return 0;
			}
		} else {
			if(mySavedChance > toCall/bankSize) return (int)toCall; 
			return 0;
		}
	}

	public void finishHand(double[] allOpenedCards) {
	}

}