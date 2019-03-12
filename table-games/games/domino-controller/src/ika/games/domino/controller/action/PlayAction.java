package ika.games.domino.controller.action;

import ika.games.base.controller.action.UserActionParam;

public final class PlayAction extends UserActionParam {

    public final int node, newNode, side1, side2;

    public PlayAction(int node, int newNode, int side1, int side2) {
        this.node = node;
        this.newNode = newNode;
        this.side1 = side1;
        this.side2 = side2;
    }
}
