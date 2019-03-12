package simplepoker.player.students.kushkusha;

import java.util.Collection;
import java.util.Random;

import simplepoker.controller.Player;

public class Nkhut10Player implements Player {

	protected double card;
	protected boolean bluff = false;

	public void startHand(double card) {
		this.card = card;
	}

	public int makeBet(int me, int diller, int round, int[] money, int[] bank,
			Collection<Integer> inGame) {
		int max = 0;
		for (int x : bank) {
			max = Math.max(max, x);
		}
		if(card > 0.98) return money[me];
		if(card > 0.95) return Math.min(money[me], (max-bank[me]) + (max-bank[me]) * 50);
		if (card < 0.85) {
			if(round > 2) bluff = false;
			Random r = new Random();
			if (card > 0.81) {
				if (bluff) return (int) Math.min(money[me], max - bank[me] + 2);
				int temp = r.nextInt(100);
				if (temp == 1) {
					bluff = true;
					return (int) Math.min(money[me], max - bank[me] + 2);
				}
			}
			if (card > 0.77) {
				if(round > 2) bluff = false;
				if (bluff) return (int) Math.min(money[me], max - bank[me] + 1);
				int temp = r.nextInt(200);
				if (temp == 1) {
					bluff = true;
					return (int) Math.min(money[me], max - bank[me] + 1);
				}
			} 
			return 0;
		} else {
			if (round < 1) return Math.min(money[me], max - bank[me]);
		    int n = -1;
		    for (int x : bank) {
		       if (x > 1) n++;
		    }
		    double k = Math.pow((double)((card - 0.7) / 0.3), n);
		    if (k < 0.5) return 0;
		    if (round < 3) return Math.min(money[me], max - bank[me] + (int) (2 * k));
		    else return Math.min(money[me], max - bank[me]);
		}
	}
	
	public void finishHand(double[] allOpenedCards) {

	}
	
	public String playerName() {
		return "kushkusha";
	}

}
