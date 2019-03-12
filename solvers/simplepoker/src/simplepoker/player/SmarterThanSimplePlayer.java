package simplepoker.player;

import simplepoker.controller.Player;

import java.util.Collection;

public class SmarterThanSimplePlayer implements Player {

    protected double card;

    public void startHand(double card) {
        this.card = card;
    }

    public int makeBet(int me, int diller, int round, int[] money, int[] bank, Collection<Integer> inGame) {

        if (card < 0.7) {
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

        double d = (card - 0.7) / 0.3;
        d = Math.pow(d, n);
        if (d < 0.5) {
            return 0;
        }

        if (round < 3) {
            return Math.min(money[me], max - bank[me] + (int) (10 * d));
        } else {
            return Math.min(money[me], max - bank[me]);
        }

    }

    public void finishHand(double[] allOpenedCards) {
    }
}
