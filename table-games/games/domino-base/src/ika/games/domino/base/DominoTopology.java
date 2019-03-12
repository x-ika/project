package ika.games.domino.base;

import java.util.Map;
import java.util.HashMap;

public final class DominoTopology {

    /**
     * *******************************
     * Domino sides numbering:
     *     0
     *   +---+
     *   |   |
     * 3 |---| 1
     *   |   |
     *   +---+
     *     2
     *********************************
     * Dominoes int representation:
     * bottom << 3 | top
     * *******************************
     */

    private DominoTopology() {
    }


    public static int getSum(int stone) {
        return getBottom(stone) + getTop(stone);
    }

    public static boolean isDouble(int stone) {
        return getBottom(stone) == getTop(stone);
    }

    public static boolean hasSide(int stone, int side) {
        return (getSidesMask(stone) & 1 << side) != 0;
    }

    public static int getSidesMask(int stone) {
        return isDouble(stone) ? 10 : 5;
    }


    public static int get(int stone, int side) {
        return side == 0 ? getTop(stone) : getBottom(stone);
    }

    public static int getBottom(int stone) {
        return stone >> 3;
    }

    public static int getTop(int stone) {
        return stone & 7;
    }


    public static Map<Node, int[]> get2DRepresentation(DominoTree tree, int w, int h) {

        Map<Node, int[]> map = new HashMap<>();
        if (tree.nodes.isEmpty()) {
            return map;
        }
        rec(null, 0, tree.nodes.get(0), w, h, map);
        return map;

    }

    private static void rec(Node prev, int side, Node node, int w, int h, Map<Node, int[]> map) {

        int p = -1;
        if (prev == null) {
            map.put(node, new int[]{0, 0, 0});
        } else {

            for (int i = 0; i < 4; i++) {
                if (node.next[i] == prev) {
                    p = i;
                }
            }

            int[] c = map.get(prev);
            int x = c[0], y = c[1], r = c[2];
            int t = r + side & 3, w1 = r % 2 == 0 ? w : h, h1 = w + h - w1;
            int k = t - p + 6 & 3, w2 = k % 2 == 0 ? w : h, h2 = w + h - w2;
            int d = 1;
            int nx = t % 2 == 0 ? x + (w1 - w2 >> 1) : t == 1 ? x + w1 + d : x - w2 - d;
            int ny = t % 2 == 1 ? y + (h1 - h2 >> 1) : t == 0 ? y - h2 - d : y + h1 + d;

            map.put(node, new int[]{nx, ny, k});

        }

        for (int i = 0; i < 4; i++) {
            if (i != p && node.next[i] != null) {
                rec(node, i, node.next[i], w, h, map);
            }
        }

    }

}
