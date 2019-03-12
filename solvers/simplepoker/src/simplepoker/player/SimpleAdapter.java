package simplepoker.player;

import java.util.Collection;

public class SimpleAdapter extends PredictablePlayer {
	
	/*
	 * This player tries to guess (from previous statistics) what card the opponent has and only
	 * bet +1 if he has a real chance of beating that. otherwise fold.
	 * 
	 * This player assumes that every opponent plays the same way, so is more suited for heads up.
	 */

    protected double card;
    protected int first_bet;
    
    protected class StatContainer {
    	private double[] avgCard;
    	public int[] oppBets; // set during the Make bet sessions
    	public int me; // to exclude from stat computations
    	
    	private int betIndex(int bet) {
    		int array_ind = (int) ( Math.log( (double) bet ) / Math.log(1.5) );
    		if (array_ind < 0) array_ind = 0;
    		if (array_ind > 9) array_ind = 9;
    		return array_ind;
    	}
    	
    	void updateStats(double[] openedCards) {
    		// use oppBets that was set before and openCards to update avgCard array
    		for (int i = 0; i < openedCards.length; i++) {
    			if (me != i) {
    				if (getExpectedCardForBet(oppBets[i]) <= openedCards[i]) {
    					// increase expected card values
    					for (int j = betIndex(oppBets[i]); j < 10; j++) {
							avgCard[j] += ((openedCards[i] - avgCard[j]) * 1.0) / 10.0;
						}
    				} else {
    					// decrease expected card values
    					for (int j = 0; j < betIndex(oppBets[i]) + 1; j++) {
    						avgCard[j] += ((openedCards[i] - avgCard[j]) * 1.0) / 10.0;
						}
    				}
    			}
			}
    	}
    	
    	double getExpectedCardForBet(int bet) {
    		return avgCard[betIndex(bet)];
    	}
    	
    	public StatContainer() {
    		avgCard = new double[10];
    		
    		// initialize with expected default opponent behavior 
    		for (int i = 0; i < 10; i++) {
    			avgCard[i] = (i * 1.0 / 10.0 + 0.05);
    		}
    	}
    };

    protected StatContainer opponentStats;
    
	public String playerName() {
		return "Adapter";
	}
	
	public int makeBet(int me, int diller, int round, int[] money, int[] bank, Collection<Integer> inGame) {
		double oppBestCard = 0.0;
		for (int i = 0; i < bank.length; i++) {
			if (opponentStats.getExpectedCardForBet(bank[i]) > oppBestCard) {
				oppBestCard = opponentStats.getExpectedCardForBet(bank[i]);
			}
		}
		
		// remember everyones bets
		opponentStats.oppBets = bank.clone();
		opponentStats.me = me;
		
		// fold if someone else has much better cards
		if (card < oppBestCard) return 0;
		
		// otherwise raise by 1 if possible;
		if (round < 3) return raiseBet(me, money, bank, 1);
		return callBet(me, money, bank);
    }

    public void finishHand(double[] allOpenedCards) {
    	opponentStats.updateStats(allOpenedCards);
    }
    
    public SimpleAdapter() {
    	opponentStats = new StatContainer();
    }
}
