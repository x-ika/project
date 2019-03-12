package ika.games.domino.base;

public class DominoMove {

    public final Node basic;
    public final Node newNode;
    public final int side1, side2;

    public DominoMove(Node basic, Node add, int side1, int side2) {
        this.basic = basic;
        this.newNode = add;
        this.side1 = side1;
        this.side2 = side2;
    }

}
