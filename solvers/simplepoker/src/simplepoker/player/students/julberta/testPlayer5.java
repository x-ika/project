package simplepoker.player.students.julberta;

import simplepoker.controller.Player;

import java.util.Collection;

public class testPlayer5 implements Player {

    protected double card;

    public void startHand(double card) {
        this.card = card;
    }

    private double potOdds(double amount, double pot){
    	return amount / (amount + pot);
    }
    
    private double ROR(double crd, double ratio){
    	return crd / ratio;
    }
    
    private double callValue (int bet, int [] bank, int me){
    	return 1 - (bet / bank[me]);
    }
    
    private double allInValue(int [] bank, int pot, int me){
    	return 1 - (bank[me] / pot);
    }
    
    public int makeBet(int me, int diller, int round, int[] money, int[] bank, Collection<Integer> inGame) {
    	int bet = 0;
		int pot = 0;
        for (int cur_bet : bank) {
            bet = Math.max(bet, cur_bet);
            pot += cur_bet;
        }
        bet = bet - bank[me];
       
        double odds = potOdds(bet, pot);
        double RR = ROR(card, odds);
      
        if(round < 3){
			if (card > 0.95) {
				return money[me];
			} else if (card > 0.9) {
				if(callValue(bet, bank, me) <= card){
					return money[me];
				}else{
					return Math.min(bet, money[me]);
				}
			} else if (card > 0.1) {
				if (odds != 0) {
					if (RR > 2) {
						if(allInValue(bank, pot, me) <= card){
							return Math.min(pot/2, money[me]);
						}else
							return Math.min(bet, money[me]);
					} else {
						double rand = Math.random();
						if (rand > 0.9) {
							return Math.min(pot / 3, money[me]);
						}
					}
				} else {
					double rand = Math.random();
					if (rand > 0.9) {
						return Math.min(pot / 3, money[me]);
					}
				}
			}
        }else{
        	if(card > 0.9){
	        	return Math.min(bet, money[me]);
	        }else if(card > 0.1){
	        	if(odds != 0){
		        	if(RR > 2){
		        		return Math.min(bet, money[me]);
		        	}
	        	}
	        }
        }
        return 0;
    }

    public void finishHand(double[] allOpenedCards) {
    //	System.out.println(allOpenedCards[0]+"  "+allOpenedCards[1]);
    }

}
