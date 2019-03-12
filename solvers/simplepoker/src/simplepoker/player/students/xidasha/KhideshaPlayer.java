package simplepoker.player.students.xidasha;

import java.util.Collection;

import simplepoker.controller.Player;

public class KhideshaPlayer implements Player {
	protected double card;

	private int handCounter = 0;

	public void finishHand(double[] allOpenedCards) {

	}
	
//es predictablis modifyed betebia orive
	
	private int bet(int me, int[] money, int[] bank, int amountToRaise) {
		int max_bet = 0;
		for (int x : bank) {
			max_bet = Math.max(max_bet, x);
		}
		return Math.min(money[me], max_bet - bank[me] + amountToRaise);
	}

	private int betTo(int me, int[] money, int[] bank, int targetBet) {

		if (targetBet < bet(me, money, bank, 0) + bank[me]) {
			return 0;
		}
		return Math.min(money[me], targetBet - bank[me]);
	}
	
	
	private double MyRate(int me, int diller, int round, int[] money,
			int[] bank, Collection<Integer> inGame) {
		int n = -1;
		for (int x : bank) {
			if (x > 1) {
				n++;
			}
		}

		double d = card;
		d = Math.pow(d, n);
		return d;
	}
//ara blefshi me dzaan
	private int bluffMaster(int me, int diller, int round, int[] money,
			int[] bank, Collection<Integer> inGame) {
		int playerCounter = -1;
		for (int x : bank) {
			if (x > 1) {
				playerCounter++;
			}
		}

		int max = 0;
		for (int x : bank) {
			max = Math.max(max, x);
		}
		if (playerCounter == 1) {
			if (round == 0) {
				if (max <= 3)
					return bet(me, money, bank, 0);
			}
			if (round == 1) {
				return Math.min(money[me], max - bank[me]
						+ (int) (1.8 * (int) (2 - (Math.log(0.3)))));
			}
		}
		return 0;
	}

	private int modifiedPredictablePlayer(int me, int diller, int round,
			int[] money, int[] bank, Collection<Integer> inGame) {
		int bet = 0;
		if (round < 1) {
			bet = betTo(me, money, bank, (int) (2 - Math.log(1 - card)));
			// System.err.println(bet);
			return bet;
		} else {

			if (bet(me, money, bank, 0) < (int) (1.5 * bet)) {
				// System.err.println(bet(me, money, bank, 0));
				return bet(me, money, bank, 0);
			} else {
				// System.err.println(0);
				return 0;
			}
		}
	}

	public int makeBet(int me, int diller, int round, int[] money, int[] bank,
			Collection<Integer> inGame) {
		if (card >= 0.9) {
			// System.err.println(money[me]);
			return money[me];
		}
		if (card < 0.4) {
			// System.err.println(bluffMaster(me, diller, round, money, bank,
			// inGame));
			return bluffMaster(me, diller, round, money, bank, inGame);
		}
		return modifiedPredictablePlayer(me, diller, round, money, bank, inGame);
	}

	public String playerName() {
		return "Khidesha";
	}

	public void startHand(double card) {
		handCounter++;
		this.card = card;
	}

}
