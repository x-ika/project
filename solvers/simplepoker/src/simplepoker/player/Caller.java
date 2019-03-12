package simplepoker.player;

import simplepoker.controller.Player;

import java.util.Collection;

public class Caller implements Player {
	
	/*
	 * This player always calls and never raises, whatever the strength of his cards.
	 */

    protected double card;
    
	public String playerName() {
		return "Caller";
	}

    public void startHand(double card) {
        this.card = card;
    }
        
    private int callBet(int me, int[] money, int[] bank) {
    	
    	// Get max bet that we need to match to call
    	int max_bet = 0;
        for (int cur_bet : bank) {
            max_bet = Math.max(max_bet, cur_bet);
        }
        
        // Calculate bet needed to call
        return Math.min(money[me], max_bet - bank[me]);
    }

    public int makeBet(int me, int diller, int round, int[] money, int[] bank, Collection<Integer> inGame) {

        return callBet(me, money, bank);
    }

    public void finishHand(double[] allOpenedCards) {
    }
}
