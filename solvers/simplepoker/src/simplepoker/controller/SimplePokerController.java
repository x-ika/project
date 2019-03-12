package simplepoker.controller;

import simplepoker.gui.PokerTable;

import java.util.*;

public class SimplePokerController {

    private static final int START_MONEY = 200;
    private static final int N_ROUNDS = 3;

    private int runMode;
    private int n;
    private int[] money;
    private Player[] playerNames;
    private PokerTable pokerTable;
    private Random rnd;

    public int playNewGame(int runMode, Player[] players, String[] playerNames) {

        // init variables
        this.runMode = runMode;
        n = players.length;
        money = new int[n];
        this.playerNames = players;
        if (runMode > 0) {
            pokerTable = new PokerTable(runMode > 1, playerNames);
        }
        rnd = new Random(1);

        // shuffle players
        for (int i = 0; i < 100; i++) {
            int a = rnd.nextInt(n);
            int b = rnd.nextInt(n);
            Player t = players[a];
            players[a] = players[b];
            players[b] = t;
        }

        // money assignment
        for (int i = 0; i < n; i++) {
            money[i] = START_MONEY;
        }

        // play the game
        for (int diller = 0; ; diller = next(diller)) {
            if (money[diller] > 0 && playHand(diller)) {
                if (runMode > 0) {
                    pokerTable.dispose();
                }
                return diller;
            }
        }

    }

    private boolean playHand(int diller) {
        update(money, new int[n], new double[n], new ArrayList<>(), "new hand");
        // finish criteria
        if (money[diller] == n * START_MONEY) {
            return true;
        }

        // card assignment
        double[] cards = new double[n];
        for (int i = 0; i < n; i++) {
            if (money[i] > 0) {
                cards[i] = rnd.nextDouble();
                try {
                    playerNames[i].startHand(cards[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // make blinds
        int[] bank = new int[n];
        for (int i = 0; i < n; i++) {
            if (money[i] > 0) {
                bank[i]++;
                money[i]--;
            }
        }

        // current maximal bet
        int maxBet = 0;

        // who is still in game
        Collection<Integer> set = new HashSet<>();
        for (int i = 0; i < n; i++) {
            if (bank[i] > 0) {
                set.add(i);
            }
        }
        update(money, bank, cards, set, "blinds are taken");

        // 4 rounds
        M:
        for (int round = 0; round < N_ROUNDS + 1; round++) {

            for (int i = diller, j = 0; i != diller || j == 0; j++, i = next(i)) {

                if (!set.contains(i)) {
                    // already fold
                    continue;
                }

                int bet = 0;
                try {
                    bet = playerNames[i].makeBet(i, diller, round, money.clone(), bank.clone(), new ArrayList<>(set));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (!checkBet(i, bet, money, bank, maxBet, round)) {
                    // fold
                    update(money, bank, cards, set, "player " + i + " fold");
                    set.remove(i);
                    if (set.size() == 1) {
                        break M;
                    }
                } else {
                    // update bank
                    money[i] -= bet;
                    bank[i] += bet;
                    maxBet = Math.max(maxBet, bank[i]);
                    update(money, bank, cards, set, "player " + i + " make bet");
                }

            }
        }

        // distribute bank among winners
        for (int i = 0; i < n; i++) {
            if (!set.contains(i)) {
                cards[i] = 0;
            }
        }
        while (!set.isEmpty()) {

            // the player with the highest card
            int best = -1;
            for (int i : set) {
                if (best == -1 || cards[best] < cards[i]) {
                    best = i;
                }
            }

            // peek required amount of money from the other players
            int my = bank[best];
            if (my == 0) {
                set.remove(best);
                continue;
            }
            for (int i = 0; i < n; i++) {
                int peek = Math.min(my, bank[i]);
                bank[i] -= peek;
                money[best] += peek;
            }
            update(money, bank, cards, set, "player " + best + " take bank");
            set.remove(best);

        }
        for (int i = 0; i < n; i++) {
            money[i] += bank[i];
            bank[i] = 0;
        }

        // finish hand
        for (int i = 0; i < n; i++) {
            try {
                playerNames[i].finishHand(cards.clone());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private boolean checkBet(int i, int bet, int[] money, int[] bank, int maxBet, int round) {
        // bet more then have
        if (bet > money[i]) {
            return false;
        }
        // not enough
        if (bank[i] + bet < maxBet && bet < money[i]) {
            return false;
        }
        // raise is not allowed in the last round
        if (round == N_ROUNDS && bank[i] + bet > maxBet) {
            return false;
        }
        // seems that everything is ok
        return true;
    }

    private int next(int i) {
        return (i + 1) % n;
    }

    private void update(int[] money, int[] bank, double[] cards, Collection<Integer> in, String s) {
        if (runMode > 0) {
            pokerTable.update(money, bank, cards, in, s);
        }
    }

}
