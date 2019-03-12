package simplepoker.player.students.kervalasimplepoker;

import java.util.Collection;

import simplepoker.controller.Player;

public class myPlayer implements Player{
	protected double card;
	protected int first_bet;


	public void startHand(double card) {
		this.card = card;
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
		if(card < 0.23){
			return 0;
		}
		if(card>0.90){
			if(round == 0){
				int max = 0;
			    for (int x : bank) {
			    	max = Math.max(max,x);
			    }
			    if(max > money[me]/2){
			    	return money[me];
			    }
			    else{
			    	return money[me]/2;
			    }
			}
			if(round == 1){
				return money[me];
			}
		}
		if(round == 0){
			first_bet = raiseUpTo(me, money, bank,(int) (2 - Math.log(1 - card)));
		}
		if(card > 0.85 && card < 0.90){
			if(round < 2){
				int max = 0;
				int player =-1;
				for (int i = 0;i<bank.length; i++){
					if(max < bank[i]){
						player = i;
						max = bank[i];
					}
				}
				if(max < money[player] && max < money[me]){
					return max;
				}
				else{
					return 0;
				}
			}
			else{
				return bank[me];
			}
    	}
		if(card > 23 && card <35){ // blindebis moparva tu araperi uchiravs  da ar mizrdis .
			if(round <2){
				int max = 0;
			    for (int x : bank) {
			    	max = Math.max(max,x);
			    }
				if(max<2){
					if(me != diller){
						return money[me]/40;
					}
					else{
						return 0;
					}
				}
			}
			else{
				return 0;
			}
			
			
		}
		if(card > 40 && card < 60){
			return callBet(me, money, bank);
		}
		if (round < 1) {
			return first_bet;
		} 
		else{
			if (callBet(me, money, bank) < (int) (3 * first_bet)) {
				return callBet(me, money, bank);
			}
			else {
				return 0;
			}
		}
		
	}
	

	public void finishHand(double[] allOpenedCards) {		
	}


	public String playerName() {
		return "kervala";
	}
}
