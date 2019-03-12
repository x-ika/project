package ika.games.gomoku.controller.player;

import ika.games.gomoku.controller.GomokuLogic;
import ika.games.gomoku.controller.action.GomokuMove;

/**
 * Based on winning trees
 */
public class GomokuAI implements GomokuPlayer {

    private static final int N = GomokuLogic.N_ROWS;
    private static final int M = GomokuLogic.N_COLUMNS;
    private static final int L = GomokuLogic.WIN_SEQUENCE_LENGTH;

    private final int[] desk = new int[N * M];

    private Stats myStats;
    private Stats hisStats;

    public GomokuMove getMove(int[][] d, int v) {
        int num = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                desk[i * M + j] = d[i][j];
                num += desk[i * M + j];
            }
        }

        // 1) First turn
        if (num == 0) {
            return new GomokuMove(N / 2, M / 2);
        }

        // 2) Templates
        if (Templates.instance.getMove(d) != null) {
            return Templates.instance.getMove(d);
        }

        // 3) Use statistics
        myStats = new Stats(desk, v, N, M, L);
        hisStats = new Stats(desk, 3 - v, N, M, L);

        // 3.1) Win or prevent defeat.
        for (int i = 1; i <= 6; i++) {
            if (myStats.findAllTrees(1 << i) > 0) {
                return getMove(myStats.getTree(0)[1]);
            }
            if (hisStats.findAllTrees(1 << i) > 0) {
                return getMove(hisStats.getTree(0)[1]);
            }
        }

        // 3.2) Position evaluation
        int result = 0;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < N * M; i++) {
            if (desk[i] != 0) {
                continue;
            }
            if (myStats.getStrength(i) + hisStats.getStrength(i) == 0) {
                continue;
            }
            int cur = getValue(i, v);
            if (max < cur) {
                max = cur;
                result = i;
            }
        }
        if (max == 0) {
            for (int i = 0; i < N * M; i++) {
                int cur = myStats.getStrength(i) + hisStats.getStrength(i);
                if (max < cur) {
                    max = cur;
                    result = i;
                }
            }
        }
        return getMove(result);
    }

    private int getValue(int p, int v) {
        int ret = 0;
        for (int i = 1; i <= 4; i++) {
            ret *= 2; // depth factor
            ret += myStats.findAllTrees(p, v, 1 << i);
            ret -= hisStats.findAllTrees(p, v, 1 << i);
        }
        return ret;
    }

    private GomokuMove getMove(int cell) {
        return new GomokuMove(cell / M, cell % M);
    }

}
