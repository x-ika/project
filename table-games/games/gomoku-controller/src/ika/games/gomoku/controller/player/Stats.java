package ika.games.gomoku.controller.player;

import static java.lang.Math.*;
import java.util.*;

@SuppressWarnings({"StatementWithEmptyBody"})
class Stats {

    private final int n, m, s;
    private final int[] d;

    /**
     * This array Stores indexes of the sets for each cell.
     */
    private final int[][] f;
    private final int[] z;

    /**
     * Winning sets and thir sizes.
     */
    private final int[][] winSets;
    private final int[] num;

    /**
     * The nighbors[i] contains neighbors of the i-th cell in terms of winning sets.
     */
    private final int[][] nighbors;


    Stats(int[] desk, int v, int n, int m, int winLength) {
        this.n = n;
        this.m = m;
        s = n * m;
        d = desk;
        f = new int[s][99];
        nighbors = new int[s + 1][];
        z = new int[s];
        num = new int[4 * s];
        winSets = new int[4 * s][];

        int wz = 0;
        int[] t = new int[s];
        boolean[] b = new boolean[3 * s];

        // save wining sets
        int[] dp = {-m + 1, 1, m + 1, m};
        for (int i = 0; i < s; i++) {
            if (desk[i] != 0) {
                continue;
            }
            // i is possible upper left corner of the set
            for (int j = 0; j < s; j++) {
                b[s + j] = max(abs(i / m - j / m), abs(i % m - j % m)) <= winLength;
            }
            for (int d : dp) {

                // number of picked cells
                int c = 0;
                for (int j = i - d; b[j + s] && desk[j] == v; j -= d, c++) ;
                for (int j = i, tz = 0; tz < winLength; j += d, c++) {
                    if (!b[j + s] || desk[j] != v) {
                        // if we picked enough cells and i is unnecessary
                        if (c >= winLength && (j - i) / d <= winLength) {
                            winSets[wz] = Arrays.copyOf(t, tz);
                            // num is size of the set but can be updated
                            num[wz] = tz;
                            for (int p : winSets[wz]) {
                                f[p][z[p]++] = wz;
                            }
                            wz++;
                            break;
                        }
                        if (c >= winLength || !b[j + s] || desk[j] != 0) {
                            break;
                        }
                        t[tz++] = j;
                    }
                }

            }
        }

        nighbors[s] = new int[s];
        for (int i = 0; i < s; i++) {
            nighbors[s][i] = i;
            int tz = 0;
            Arrays.fill(b, 0, s, false);
            for (int j = 0; j < z[i]; j++) {
                for (int p : winSets[f[i][j]]) {
                    if (!b[p]) {
                        b[p] = true;
                        t[tz++] = p;
                    }
                }
            }
            nighbors[i] = Arrays.copyOf(t, tz);
        }
    }


    /**
     * Wining trees
     */

    private final List<int[]> trees = new ArrayList<>();

    int getStrength(int i) {
        return z[i];
    }

    /**
     * Finds the all winning trees.
     * @param max upper bound for index of the tree.
     * @return Returns the number of trees found.
     */
    int findAllTrees(int max) {
        trees.clear();
        int[] q = new int[2 * max];
        Arrays.fill(q, -1);
        q[0] = s;
        findTrees(q, 1, d, max);
        return trees.size();
    }

    /**
     * Finds the all winning trees.
     * @param p index of the additional cell.
     * @param v value for additional cell.
     * @param max upper bound for index of the tree.
     * @return Returns the number of trees found.
     */
    int findAllTrees(int p, int v, int max) {
        d[p] = v;
        adjust(p, -1);
        int ret = findAllTrees(max);
        d[p] = 0;
        adjust(p, 1);
        return ret;
    }

    /**
     * Finds the all winning trees.
     * @param q tree, children of i are 2*i and 2*i+1.
     * @param c index of the current node.
     * @param d desk array.
     * @param max upper bound for index of the tree.
     */
    private void findTrees(int[] q, int c, int[] d, int max) {
        if (c >= max) {
            return;
        }
        M:
        for (int i : nighbors[q[c >> 1]]) {
            if (d[i] == 0 && ((c & 1) == 0 || i < q[c - 1])) {
                d[i] = -1;
                q[c] = i;
                int[] a = f[i];
                for (int j = 0; j < z[i]; j++) {
                    if (--num[a[j]] == 0) {
                        // mark c as leaf node
                        q[2 * c] = q[2 * c + 1] = -1;
                        while (j >= 0) num[a[j--]]++;
                        int n = c;
                        while ((n & 1) == 1) {
                            n >>= 1;
                        }
                        if (n == 0) {
                            trees.add(Arrays.copyOf(q, q.length));
                        } else {
                            for (j = c; (j >>= 1) >= n; adjust(q[j], 1)) ;
                            findTrees(q, n + 1, d, max);
                            for (j = c; (j >>= 1) >= n; adjust(q[j], -1)) ;
                        }
                        d[i] = 0;
                        continue M;
                    }
                }
                findTrees(q, c << 1, d, max);
                adjust(i, 1);
                d[i] = 0;
            }
        }
    }

    private void adjust(int i, int d) {
        for (int j = 0, a[] = f[i]; j < z[i]; num[a[j++]] += d) ;
    }

    int[] getTree(int index) {
        return trees.get(index);
    }

}
