package simplepoker.player.students.kote;

import java.util.Collection;

import simplepoker.controller.Player;

/**
 *
 * @author Kote.
 *         Created Dec 14, 2012.
 */
public class PlayerKote implements Player{

	protected double card;
	protected int sdacha = 0;
	protected int n;
	protected int[]betting;
	protected boolean[]raised;

	public void startHand(double card) {
		this.card = card;
		
	}
	public int makeBet(int me, int diller, int round, int[] money, int[] bank, Collection<Integer> inGame) {
		initDatas(money.length);
		saveBettingStats(round, money, me, bank, inGame);
			
		if(card == 0.0)return 0;
		if(card > 0.92)return money[me];
		int max = 0, maxPlayer = 0;
	    for (int x = 0; x < bank.length; x++) {
	    	if(bank[x] > max){
	    		max = bank[x];
	    		maxPlayer = x;
	    	}
	    }
		if(noBets()){
			if(card > 0.7){
				return BetMoney(bank, round, 1, me, money, max);
			} else {
				return 0;
			}
		} else {
			for(int i = 0; i < inGame.size(); i++){
				double encard = countCardStrength(i);
				if(i != me && card < encard)return 0;
			}
			
		    if(maxPlayer != me && (money[maxPlayer] == max || max > 50)){
		    	if(card > 0.85){
		    		return BetMoney(bank, round, 0, me, money, max);
		    	} else {
		    		return 0;
		    	}
		    }
		    if(card > 0.6){
		    	return BetMoney(bank, round, 8, me, money, max);
		    } else {
		    	return BetMoney(bank, round, 0, me, money, max);
		    }
			
		}
	}
	
	/**
	 * vinaxav vin ramden darigebaze shemodis bets.
	 * aseve vigeb vin areisebs/betavs
	 */
	private void saveBettingStats(int round, int[] money, int me, int[] bank, Collection<Integer> inGame) {
		if(round == 0){
			raised = new boolean[money.length];
			for(int i = 0; i < bank.length; i++){
				if(bank[i] > 1){
					betting[i]++;
					raised[i] = true;
				}
			}
			sdacha++;
		} else if(round == 1){
			for(int i = 0; i < bank.length; i++){
				if(inGame.contains(i) && !raised[i] &&  i != me && bank[i] > 1){
					betting[i]++;
					raised[i] = true;
				}
			}
		}
		
	}

	/**
	 */
	private void initDatas(int players) {
		if(sdacha == 0){
			betting = new int[players];
		}
	}

	/**
	 * when we want "call"  - amount is 0. when bet/raise - amount is positive integer
	 */
	private int BetMoney(int[] bank, int round, int amount, int me, int[] money,int max) {
		
	    if (round < 3) {
            return Math.min(money[me], max - bank[me] + amount);
        } else {
            return Math.min(money[me], max - bank[me]);
        }

	}

	private boolean noBets() {
		for(int i = 0; i < raised.length; i++){
			if(raised[i])return false;
		}
		return true;
	}

	private double countCardStrength(int player) {
		double percent = (double)betting[player] / sdacha;
		return 1-percent;
	}



	public void finishHand(double[] allOpenedCards) {
//		for(int i = 0; i < allOpenedCards.length; i++){
//			System.out.println("player N: " + i + " has card: " + allOpenedCards[i]);
//		}
		
	}

	public String playerName() {
		return "Kote";
	}

}
