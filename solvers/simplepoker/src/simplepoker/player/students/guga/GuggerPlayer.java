package simplepoker.player.students.guga;

import simplepoker.player.PredictablePlayer;

import java.util.Collection;

public class GuggerPlayer extends PredictablePlayer {

	private double[] aggression;
	private double[] strength;
	private double playersAmount;
	private double percent;
	private int lastMax;
	private int number = 0;

	public void startHand(double card) {
		this.card = card;
	}


	public int makeBet(int me, int diller, int round, int[] money, int[] bank, Collection<Integer> inGame) {
        if (aggression == null) {
            this.playersAmount = money.length;
            aggression = new double[(int)playersAmount];
            strength = new double[(int)playersAmount];
            for (int i = 0; i < playersAmount; i++) {
                strength[i] = 1 / playersAmount;
                aggression[i] = 1;
            }
            lastMax = 0;
        }
        countProbableStrengths(money, bank, inGame, me);
		double currentAgg = countMyAggression(inGame);
		double pot=0;
//		System.out.println(number++);
		for (int i = 0; i < playersAmount; i++)
			pot += bank[i];
		
        if (round < 1) {
        	first_bet = raiseUpTo(me, money, bank, (int) (2 - Math.log(1-card)) );
        	return first_bet;
        } else {

            if (card < 0.6) {
                return 0;
            }
            int max = 0;
            for (int x : bank) {
                max = Math.max(max, x);
            }
            if (round < 1) {
                return Math.min(money[me], max - bank[me]);
            }

            int n = -1;
            for (int x : bank) {
                if (x > 1) {
                    n++;
                }
            }

            double d = (card - 0.6) / 0.4;
            d = Math.pow(d, n);
            if (d < callBet(me, money, bank)/pot) {
                return 0;
            }

            if (round < 3) {
                return Math.min(money[me], max - bank[me] + (int) ((pot+10) * d));
            } else {
                return Math.min(money[me], max - bank[me]);
            }
        }
	}

	private double countMyAggression(Collection<Integer> inGame) {
		double meanAggression = 0;
		for (int i = 0; i < playersAmount; i++)
			if (inGame.contains(i))
				meanAggression += aggression[i];
		meanAggression /= inGame.size();
		double myAggr = 1 / meanAggression;
		return myAggr * (1 + Math.random() * 0.2);
	}

	private void countProbableStrengths(int[] money, int[] bank, Collection<Integer> inGame, int me) {
		int pot = 0;
		int bet;
		for (int i = 0; i < playersAmount; i++)
			pot += bank[i];

		for (int i = 0; i < playersAmount; i++) {
			bet = bank[i] - lastMax;
//			System.out.println(money[i]+" "+pot+" "+aggression[i]);
			if (inGame.contains(i) && pot!=0)
				strength[i] += Math.min(bet, pot)/pot*aggression[i];
			else
				strength[i] = 0;
		}
		for (int i = 0; i < playersAmount; i++) {
			lastMax = Math.max(lastMax, bank[i]);
		}
		
		normaliseStrengths();
	}

	private void normaliseStrengths() {
		int sum = 0;
		for (int i = 0; i < playersAmount; i++)
			sum += strength[i];

		if (sum != 0)
			for (int i = 0; i < playersAmount; i++)
				strength[i] /= sum;
		
	}


	public void finishHand(double[] allOpenedCards) {
		initData();
	}

	private void initData() {
		for (int i = 0; i < playersAmount; i++) {
			strength[i] = 1 / playersAmount;
		}
		lastMax = 0;
	}


	public String playerName() {
		return "Guga";
	}
}