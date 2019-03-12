package ika.games.domino.base;

public class Node {

    int stone, sides;
    Node[] next = new Node[4];

    public Node(int stone, int sides) {
        this.stone = stone;
        this.sides = sides;
    }

    public int getStone() {
        return stone;
    }

    public int getTop() {
        return DominoTopology.getTop(stone);
    }

    public int getBottom() {
        return DominoTopology.getBottom(stone);
    }

    public int getSum() {
        return DominoTopology.getSum(stone);
    }

    public int getSides() {
        return sides;
    }

    public Node[] getNext() {
        return next;
    }

    boolean isDouble() {
        return DominoTopology.isDouble(stone);
    }

    boolean isFree(int side) {
        return (sides & 1 << side) != 0 && next[side] == null;
    }

    int numFree() {
        int count = 0;
        for (int side = 0; side < 4; side++) {
            if (isFree(side)) {
                count++;
            }
        }
        return count;
    }

    int numNeighbours() {
        int count = 0;
        for (Node node : next) {
            if (node != null) {
                count++;
            }
        }
        return count;
    }

    public int freeSide() {
        for (Node node : next) {
            if (node != null) {
                return getTop() == node.getTop() || getTop() == node.getBottom() ? getBottom() : getTop();
            }
        }
        return -1;
    }
}
