package simplepoker.player.students.kikola;

import java.util.Collection;

import simplepoker.controller.Player;

public class MyPlayer3 implements Player {
	private double card;
	
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
	
	private int calcPot(int[] bank){
		int sum = 0;
		for (int i : bank) {
			sum += i;
		}
		return sum;
	}
	
	private double potOdds(int me, int[] money, int[] bank){
		double toCall = raiseBet(me, money, bank, 0);
		double pot = calcPot(bank);
		double odds = toCall/(toCall+pot);
		return odds;
	}
	
	private double RR(int me, int[] money, int[] bank){
		return card/potOdds(me, money, bank);
	}

	public int makeBet(int me, int diller, int round, int[] money, int[] bank,
			Collection<Integer> inGame) {
		double rr = RR(me, money, bank);
		if(card>0.9){
			return money[me];
		}else if(card>0.51 && rr>2 && rr<100){
			return raiseBet(me, money, bank, 0);
		}
		return 0;
	}

	public void finishHand(double[] allOpenedCards) {

	}

	public String playerName() {
		return "my3";
	}

}
