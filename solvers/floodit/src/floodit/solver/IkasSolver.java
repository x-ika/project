package floodit.solver;

import java.util.*;

public class IkasSolver implements Solver {

    private static final int[] di = {-1, 0, 1, 0};
    private static final int[] dj = {0, 1, 0, -1};

    private boolean debug;
    private int nColors;
    private int[][] board;
    private int[][] neighbours;
    private long[][] comps, byColor;

    public IkasSolver(boolean debug) {
        this.debug = debug;
    }

    //--------------------------------- Init --------------------------------------------

    private void dfs(int i, int j, int ind, int[][] t, int[][] c) {
        c[i + 1][j + 1] = ind;
        for (int d = 0; d < 4; d++) {
            int ii = i + di[d], jj = j + dj[d];
            if (c[ii + 1][jj + 1] == -1 && t[i][j] == t[ii][jj]) {
                dfs(i + di[d], j + dj[d], ind, t, c);
            }
        }
    }

    public int[] solve(int[][] t) {

        // 1. init
        this.board = t;
        int n = t.length;
        int m = t[0].length;
        int[][] c = new int[n + 2][m + 2];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                nColors = Math.max(nColors, t[i][j] + 1);
                c[i + 1][j + 1] = -1;
            }
        }

        // 2. define components
        int ind = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (c[i + 1][j + 1] == -1) {
                    dfs(i, j, ++ind, t, c);
                }
            }
        }
        if (debug) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    System.out.printf("%3d", c[i + 1][j + 1]);
                }
                System.out.println("");
            }
        }

        // 3. memorize components by colors
        byColor = new long[nColors][calcSize(ind)];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                set(byColor[t[i][j]], c[i + 1][j + 1] - 1);
            }
        }

        // 4. adjacency matrix
        comps = new long[ind][calcSize(ind)];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                int x = c[i][j];
                for (int d = 0; d < 4; d++) {
                    int y = c[i + di[d]][j + dj[d]];
                    if (x != y && y != 0) {
                        set(comps[x - 1], y - 1);
                        if (debug) {
                            System.out.printf("%3d%3d\n", x, y);
                        }
                    }
                }
            }
        }
        neighbours = new int[ind][];
        for (int i = 0; i < ind; i++) {
            neighbours[i] = new int[cardinality(comps[i])];
            for (int j = 0, z = 0; j < ind; j++) {
                if (get(comps[i], j)) {
                    neighbours[i][z++] = j;
                }
            }
        }

        return solve();

    }

    //--------------------------------- Solve -------------------------------------------

    private static final class State implements Comparable<State> {
        State prev;
        long[] s;
        int c, g, h;

        public State(State prev, long[] s, int c) {
            this.prev = prev;
            this.s = s;
            this.c = c;
        }

        public int compareTo(State o) {
            return g + h - o.g - o.h;
        }

        public String toString() {
            String t = "";
            for (int i = 0; i < 64 * s.length; i++) {
                t = (get(s, i) ? "1" : "0") + t;
            }
            return String.format("%s %d %d %d", t, c, g, h);
        }
    }

    private static long hashCode(long[] s) {
        long h = 0;
        for (long v : s) {
            h *= 1000100009;
            h ^= v;
        }
        return h;
    }

    private State[] path;

    public int[] solve() {

        int n = comps.length;

        State start = new State(null, new long[calcSize(n)], board[0][0]);
        set(start.s, 0);

        HashMap<Long, Integer> seen = new HashMap<>();
        PriorityQueue<State> queue = new PriorityQueue<>(1 << 18);
        start.h = maxDistance(n, start.s);
        queue.add(start);

        long[] set = new long[calcSize(n)];
        int iters = 0;
        while (!queue.isEmpty()) {
            iters++;
            State s = queue.poll();
            Integer x = seen.put(hashCode(s.s), s.g);
            if (x != null && x <= s.g) {
                continue;
            }
            if (cardinality(s.s) == n) {
                path = new State[s.g];
                for (int i = s.g; i-- > 0;) {
                    path[i] = s;
                    s = s.prev;
                }
                break;
            }
            assign(set, comps[0]);
            for (int i = 1; i < n; i++) {
                if (get(s.s, i)) {
                    or(set, comps[i]);
                }
            }
            andNot(set, s.s);
            for (int color = 0; color < nColors; color++) {
                if (s.c == color) {
                    continue;
                }
                long[] newS = s.s.clone();
                for (int i = 0; i < newS.length; i++) {
                    newS[i] |= set[i] & byColor[color][i];
                }
                int heuristic = maxDistance(n, newS);
                State newState = new State(s, newS, color);
                newState.g = s.g + 1;
                newState.h = heuristic;
                queue.add(newState);
            }
        }
//        System.out.println("Can flood in " + path.length + " moves.");
//        System.out.println("iters = " + iters);

        int[] ret = new int[path.length];
        for (int i = 0; i < path.length; i++) {
            ret[i] = path[i].c;
        }
        return ret;

    }

    private int maxDistance(int n, long[] s) {
        int r = 0;
        long[] p = s.clone();
        while (cardinality(p) < n) {
            for (int i = 0; i < n; i++) {
                if (get(p, i)) {
                    or(p, comps[i]);
                }
            }
            r++;
        }
        return r - 1;
    }

    //--------------------------------- BitSet ------------------------------------------

    private static final int ADDRESS_BITS_PER_WORD = 6;
    private static final int BITS_PER_WORD = 1 << ADDRESS_BITS_PER_WORD;
    private static final int BIT_INDEX_MASK = BITS_PER_WORD - 1;
    private static final long WORD_MASK = 0xffffffffffffffffL;
    private static final long UWORD_MASK = 0x7fffffffffffffffL;

    private static int calcSize(int nBits) {
        return (nBits - 1 >> ADDRESS_BITS_PER_WORD) + 1;
    }

    private static long getLast(long x) {
        return x >>> BIT_INDEX_MASK & 1L;
    }

    public static int cardinality(long[] s) {
        int sum = 0, n = s.length;
        for (int i = 0; i < n; i++) {
            sum += Long.bitCount(s[i]);
        }
        return sum;
    }

    public static void set(long[] s, int i) {
        s[i >> ADDRESS_BITS_PER_WORD] |= 1L << (i & BIT_INDEX_MASK);
    }

    public static boolean get(long[] s, int i) {
        return (s[i >> ADDRESS_BITS_PER_WORD] & 1L << (i & BIT_INDEX_MASK)) != 0;
    }

    public static int nextSetBit(long[] s, int i) {
        int u = i >> ADDRESS_BITS_PER_WORD;
        if (u >= s.length) {
            return -1;
        }
        long w = s[u] & (WORD_MASK << i);
        while (true) {
            if (w != 0) {
                return (u * BITS_PER_WORD) + Long.numberOfTrailingZeros(w);
            }
            if (++u == s.length) {
                return -1;
            }
            w = s[u];
        }
    }

    public static long[] not(long[] s) {
        for (int i = 0; i < s.length; i++) {
            s[i] = ~s[i];
        }
        return s;
    }

    public boolean secs(long[] s1, long[] s2) {
        for (int i = 0; i < s1.length; i++) {
            if ((s1[i] & s2[i]) != 0) {
                return true;
            }
        }
        return false;
    }

    public long[] assign(long[] s1, long[] s2) {
        System.arraycopy(s2, 0, s1, 0, s1.length);
        return s1;
    }

    public static long[] or(long[] s1, long[] s2) {
        for (int i = 0; i < s1.length; i++) {
            s1[i] |= s2[i];
        }
        return s1;
    }

    public static long[] and(long[] s1, long[] s2) {
        for (int i = 0; i < s1.length; i++) {
            s1[i] &= s2[i];
        }
        return s1;
    }

    public static long[] xor(long[] s1, long[] s2) {
        for (int i = 0; i < s1.length; i++) {
            s1[i] ^= s2[i];
        }
        return s1;
    }

    public static long[] add(long[] s1, long[] s2) {
        long t = 0;
        for (int i = 0; i < s1.length; i++) {
            s1[i] += t;
            long x = getLast(s1[i]) + getLast(s2[i]) + getLast((s1[i] & UWORD_MASK) + (s2[i] & UWORD_MASK));
            s1[i] += s2[i];
            t = x > 1 ? 1 : 0;
        }
        return s1;
    }

    public static long[] andNot(long[] s1, long[] s2) {
        for (int i = 0; i < s1.length; i++) {
            s1[i] &= ~s2[i];
        }
        return s1;
    }

    public static long[] shiftLeft(long[] s, int shift) {
        long msk = (1L << shift) - 1;
        int rsh = BITS_PER_WORD - shift;
        for (int i = s.length; i-- > 1;) {
            s[i] = s[i] << shift | s[i - 1] >>> rsh & msk;
        }
        s[0] <<= shift;
        return s;
    }

    public static long[] shiftRight(long[] s, int shift) {
        int lsh = BITS_PER_WORD - shift;
        int last = s.length - 1;
        for (int i = 0; i < last; i++) {
            s[i] = s[i] >>> shift | s[i + 1] << lsh;
        }
        s[last] >>>= shift;
        return s;
    }

    public static String toString(long[] s) {
        StringBuilder ret = new StringBuilder(s.length * BITS_PER_WORD);
        for (int i = ret.capacity(); i-- > 0;) {
            ret.append(get(s, i) ? '1' : '0');
        }
        return ret.toString();
    }

    public static int compare(long[] s1, long[] s2) {
        int n = s1.length, d = n - s2.length;
        if (d != 0) {
            return d;
        }
        for (int i = 0; i < n; i++) {
            long x = s1[i], y = s2[i];
            if (x != y) {
                return x < y ? -1 : 1;
            }
        }
        return 0;
    }

}
