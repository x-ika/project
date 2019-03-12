package simplepoker.player.students.alpenidze;

import java.util.Collection;
import java.util.Random;

import simplepoker.controller.Player;

public class MainPlayer implements Player{

	private double card;
	private double hisCard;
	private int all=0;
	private int first_bet=0;
	private Random rgn=new Random();
	private boolean shouldBlef;
	private Helper help=new Helper();
	private int myIndex;
	private int counting=0;
	
	public void startHand(double card) {
		this.card=card;
		if(rgn.nextDouble()<0.1)shouldBlef=true;
		else shouldBlef=false;
		counting++;
		
	}


    
    // Checks if we have money available for bet and caps the bet if not
    protected int capBet(int me, int[] money, int bet) {
    	
    	return Math.min(money[me], bet);
    }
    
    protected int raiseBet(int me, int[] money, int[] bank, int amountToRaise) {
    	
    	// Get max bet that we need to match to call
    	int max_bet = 0;
        for (int cur_bet : bank) {
            max_bet = Math.max(max_bet, cur_bet);
        }
        
        // Calculate bet needed to call and add amountToRaise
        // Also, check not to exceed our money
        return capBet(me, money, max_bet - bank[me] + amountToRaise);
    }
    
    protected int callBet(int me, int[] money, int[] bank) {
    	
    	return raiseBet(me, money, bank, 0);
    }
    
    // Raises to targetBet or calls if bets are higher
    protected int raiseToTarget(int me, int[] money, int[] bank, int targetBet) {
    	
    	return capBet(me, money, Math.max(callBet(me, money, bank), targetBet - bank[me]));
    }
    
    // Raises to targetBet or folds if bets are higher
    protected int raiseUpTo(int me, int[] money, int[] bank, int targetBet) {
    	
    	if (targetBet < callBet(me, money, bank) + bank[me]) {
    		return 0;
    	}
    	return capBet(me, money, targetBet - bank[me]);
    }
	
	
	public int makeBet(int me, int diller, int round, int[] money, int[] bank,
			Collection<Integer> inGame) {
	
		myIndex = me;
		 int max = 0;
	        for (int x : bank) {
	            max = Math.max(max, x);
	        }// max aris axla gasacoli fsoni
	        
	        help.bet(round, max-bank[me]);//statistikis shevseba
			if(card <= 0.7&&card > 0.4){//10 %shi iblefebs
				if(shouldBlef)card=0.71;				
			}
			if(card<0.2)return 0;
	        
	        if(card > 0.95 && round == 2) return money[me];
	        
	    	if(counting>100){
				double enemy=help.getEstimateCard(round, max-bank[me]);
				hisCard=enemy;
				if(enemy>card)return 0;
				else if(round<3){
					if(enemy+0.2<=card)return max+3;
					if(enemy<card)return max+1;
				}
			}
	    	
	        if (round < 1) {
	        	first_bet = raiseUpTo(me, money, bank, (int) (2 - Math.log(1-card)) ); 
	        	return first_bet;
	        } else {
	        	
	        	if (callBet(me, money, bank) < (int) (1.5 * first_bet) ) {
	        		return callBet(me, money, bank);
	        	} else {
	        		return 0;
	        	}
	        }
	    	
	    	
	    	
	}

	public void finishHand(double[] allOpenedCards) {
		all++;
		int he=0;
		if(myIndex==0)he=1;
		double enemysCard=allOpenedCards[he];
		if(enemysCard==0.0){
			enemysCard=card-card/4;//tu gafolda savaraudod chemze dabali karti yavs
		}
		help.update(enemysCard);
	//	System.out.println(hisCard+" "+allOpenedCards[he]+" esaa rac yavda");
	}

	public String playerName() {
		return "alpenidze";
	}
	
}

