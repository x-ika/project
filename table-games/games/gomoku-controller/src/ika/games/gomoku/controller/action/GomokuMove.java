package ika.games.gomoku.controller.action;

import ika.games.base.controller.action.UserActionParam;

public final class GomokuMove extends UserActionParam {

    public final int i, j;

    public GomokuMove(int i, int j) {
        this.i = i;
        this.j = j;
    }
}
