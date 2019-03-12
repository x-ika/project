package simplepoker.player.students.natro;

import simplepoker.controller.Player;

import java.util.Collection;

public class zurab_natroshvili implements Player {
    protected double card;
    protected int makeBet;
    public void startHand(double card) {
        this.card = card;
    }   
    private double WhatIsChanceToWin(int me, int bank[]){
        int n = -1;
        for (int x : bank) {
            if (x > 1) {
                n++;
            }
        }
        double k = Math.pow(card, n);
        return k;
    }    
    private int howMuchCall(int bank[], int money[], int me, int diller){
        int max = 0;
        for (int x : bank) {
            max = Math.max(max, x);
        }
        int wholeBank=0;
        int n = -1;
        for (int x : bank) {
        	wholeBank+=x;
            if (x > 1) {
                n++;
            }
        }
 //       int FromMe_toDiller = n-1-Math.abs(me-diller);                
        if(max + wholeBank < money[me]*WhatIsChanceToWin(me, bank)){
        	return 0;
        }
        else{
        	return (int) (money[me]*WhatIsChanceToWin(me, bank));
        }
    }    
    protected int capBet(int me, int[] money, int bet) {
    	
    	return Math.min(money[me], bet);
    }    
    protected int raiseBet(int me, int[] money, int[] bank, int amountToRaise) {    	
    	int max_bet = 0;
        for (int cur_bet : bank) {
            max_bet = Math.max(max_bet, cur_bet);
        }       
        return capBet(me, money, max_bet - bank[me] + amountToRaise);
    }    
    protected int callBet(int me, int[] money, int[] bank) {
    	
    	return raiseBet(me, money, bank, 0);
    }    
    protected int raiseToTarget(int me, int[] money, int[] bank, int targetBet) {
    	
    	return capBet(me, money, Math.max(callBet(me, money, bank), targetBet - bank[me]));
    }   
    protected int raiseUpTo(int me, int[] money, int[] bank, int targetBet) {
    	
    	if (targetBet < callBet(me, money, bank) + bank[me]) {
    		return 0;
    	}
    	return capBet(me, money, targetBet - bank[me]);
    }    
    public int makeBet(int me, int diller, int round, int[] money, int[] bank, Collection<Integer> inGame) {
    	if(card<0.4){
            int n = -1;
            for (int x : bank) {
                if (x > 1) {
                    n++;
                }
            }    		
            int max = 0;
            for (int x : bank) {
                max = Math.max(max, x);
            }                       
    		if(n==1){
    			if(round==0 && max<=(int) (2 - Math.log(0.1))){
    				return callBet(me, money, bank);
    			}
    			if(round ==1){
    				return Math.min(money[me], max - bank[me]+5);
    			}
    		}
    		return 0;
    	}
    	if(card<=0.9 && card>=0.4){
            if (round < 1) {
            	makeBet = raiseUpTo(me, money, bank, (int) (2 - Math.log(1-card)) ); 
            	return makeBet;
            } else {
            	
            	if (callBet(me, money, bank) < (int) (1.5 * makeBet) ) {
            		return callBet(me, money, bank);
            	} else {
            		return 0;
            	}
            }       
    	}   	
    	if(card>=0.9){
    		return money[me];
    	}   	
    	return 0;
    }
    public void finishHand(double[] allOpenedCards) {
    }

	public String playerName() {
		return "zurikoo";
	}
}
