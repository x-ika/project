package ika.games.domino.base;

import java.util.*;

import static ika.games.domino.base.DominoTopology.*;

public class DominoTree {

    Node root;
    List<Node> nodes;

    public DominoTree() {
        nodes = new ArrayList<>();
    }

    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<DominoMove> getPossibleMoves(int[] stones, int numToBuy, boolean firstMove) {
        List<DominoMove> list = new ArrayList<>();
        if (nodes.isEmpty()) {
            for (int stone : stones) {
                if (firstMove) {
                    if (getTop(stone) == 6 && getBottom(stone) == 6) {
                        list.add(new DominoMove(null, createNode(stone), -1, -1));
                    }
                } else {
                    list.add(new DominoMove(null, createNode(stone), -1, -1));
                }
            }
            return list;
        }

        for (int stone : stones) {
            for (Node node : nodes) {
                for (int s1 = 0; s1 < 4; s1++) {
                    for (int s2 = 0; s2 < 4; s2++) {
                        if (node.isFree(s1) && hasSide(stone, s2) && get(node.stone, s1) == get(stone, s2)) {
                            list.add(new DominoMove(node, createNode(stone), s1, s2));
                        }
                    }
                }
            }
        }
        return list;
    }

    private Node createNode(int stone) {
        return new Node(stone, getSidesMask(stone));
    }

    public void makeTurn(DominoMove t) {
        if (t.basic != null) {
            for (Node node : nodes) {
                if (node.stone == t.basic.stone) {
                    node.next[t.side1] = t.newNode;
                    t.newNode.next[t.side2] = node;
                }
            }
        }
        if (nodes.isEmpty() && t.newNode.isDouble()) {
            root = t.newNode;
        }
        if (root == null && t.basic != null && t.basic.isDouble() && t.basic.numFree() == 0) {
            root = t.basic;
        }
        nodes.add(t.newNode);

        // extend root
        if (root != null && root.numFree() == 0) {
            root.sides |= 5;
        }
    }

    public int getScore() {
        if (nodes.size() == 1) {
            return root == null ? 0 : root.getSum();
        }
        int score = 0;
        for (Node node : nodes) {
            if (node.numNeighbours() == 1) {
                score += node.isDouble() ? node.getSum() : node.freeSide();
            }
        }
        return score;
    }

    public void clear() {
        root = null;
        nodes.clear();
    }

}
