package ika.games.gomoku.controller.player;

import ika.games.gomoku.controller.action.GomokuMove;

public interface GomokuPlayer {
    GomokuMove getMove(int[][] desk, int value);
}
