package simplepoker.player;

import simplepoker.controller.Player;

import java.util.Collection;

public class SimplePlayer implements Player {

    protected double card;

    public void startHand(double card) {
        this.card = card;
    }

    public int makeBet(int me, int diller, int round, int[] money, int[] bank, Collection<Integer> inGame) {

        int max = 0;
        for (int x : bank) {
            max = Math.max(max, x);
        }

        if (card > 0.7) {
            if (round < 3) {
                return Math.min(money[me], max - bank[me] + 1);
            } else {
                return Math.min(money[me], max - bank[me]);
            }
        }
        return 0;

    }

    public void finishHand(double[] allOpenedCards) {
    }
}
