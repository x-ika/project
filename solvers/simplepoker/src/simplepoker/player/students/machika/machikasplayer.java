package simplepoker.player.students.machika;

import simplepoker.controller.Player;

import java.util.Collection;

public class machikasplayer implements Player {

	
	
    protected double card;
    
    
    public void startHand(double card) {
        this.card = card;
    }

    public int makeBet(int me, int diller, int round, int[] money, int[] bank, Collection<Integer> inGame) {


    	int max = 0;
        int pot=0;
        
        
        
        for (int x : bank) {
            max = Math.max(max, x);
            pot+=x;
        }
        
    	if(card< (double)12/150){   // bluff raise
    		if(round <2) {
    			if(max<2*pot/3 +6) return Math.min(money[me], max - bank[me]+pot/2+2);
    			return 0;
    		}
    		else return 0;
    	}
    	
    	
    	if(card< (double) 80/150){ // check fold
    		if(round==1 && pot ==2  && card < (double) 30/150) return 2;
    		return 0;
    	}
    	
    	
    	if(card< (double) 132/150){ // check  call;
    	
    		if(max <2*pot/3+6) return Math.min(money[me], max - bank[me]); 
    		
    		return 0;
    	
    	}
        
    	
    	
    	if(card< (double) 140/150){        // Raise    (fold when raised big)
    		if(round ==1 || round ==2) { if(max <=2*pot/3+6) return Math.min(money[me], max - bank[me]+2*pot/3+6); else return 0;}
    		if(max < 2*pot/3+2) return Math.min(money[me], max - bank[me]);
    	}
    	
    	
    	

    	if(round <3) return Math.min(money[me], max - bank[me]+2*pot/3+6);
    	 return Math.min(money[me], max - bank[me]);
 
    	 
    }

    public void finishHand(double[] allOpenedCards) {
    }
}
