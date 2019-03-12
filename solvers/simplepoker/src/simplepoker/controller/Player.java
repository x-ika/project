package simplepoker.controller;

import java.util.*;

public interface Player {

    void startHand(double card);

    /**
     *
     * @param me your index
     * @param diller index of the diller
     * @param round 0 <= round <= 3. You can't raise in the last round
     * @param money current money assignment.
     * @param bank contains bets the all players
     * @param inGame
     * @return new bet, it will be added to your current bet.
     * resulted bet should not be less than current maximal bet (or ALL-IN!!!).
     */
    int makeBet(int me, int diller, int round, int[] money, int[] bank, Collection<Integer> inGame);

    void finishHand(double[] allOpenedCards);

}
