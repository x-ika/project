package simplepoker.player.students.lmagh;

import java.util.Collection;

import simplepoker.controller.Player;

public class myPlayer implements Player{

	protected double card;
	

	public void startHand(double card) {
		this.card = card;
	}
	
	private static int raise (int me, int[] money, int[] bank, int mon){
		int max = 0;
		for (int i : bank) {
			max = Math.max(max, i);
		}
		return Math.min(money[me], max - bank[me] + mon);
	}
	
	private static boolean anyBetFirstRound(int round, int[] bank){
		for (int bet : bank) {
			if (round == 0) {
				if (bet > 1) {
					return true;
				}
			}
		}
		return false;
	}


	public int makeBet(int me, int diller, int round, int[] money, int[] bank,
			Collection<Integer> inGame) {
		// all-in
		if (card >= 0.9) {
			return money[me];
			// tu bandzi karti gvyavs da pirvel raundshi aravin dabeta, vblefaob ))
		}else if (card <= 0.5) {
			if (!anyBetFirstRound(round, bank)) {
//				return raise(me, money, bank, (int) (2 - Math.log(1-card)));
			}
		}else{
			if (round < 3) {
				return raise(me, money, bank, 4 );
			}
		}
		return 0;
	}


	public void finishHand(double[] allOpenedCards) {
		
	}


	public String playerName() {
		return "me";
	}

}
