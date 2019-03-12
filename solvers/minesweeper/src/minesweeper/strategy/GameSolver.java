package minesweeper.strategy;

import minesweeper.Solver;

import java.util.*;

public class GameSolver implements Solver {

    private int n, m, p, q;
    private int[] I, J;
    private int[][] f, g;
    private int[] r;

    private IntBitSet[] x, comps;

    private IntBitSet[] curVars;
    private IntBitSet[] curSol;
    private IntBitSet[] added;
    private IntBitSet[] set;
    private IntBitSet[] vars;
    private IntBitSet[] sol;

    private void initSets() {
        curVars = new IntBitSet[p + 1];
        curSol = new IntBitSet[p + 1];
        added = new IntBitSet[p + 1];
        set = new IntBitSet[p + 1];
        vars = new IntBitSet[p + 1];
        sol = new IntBitSet[p + 1];
        for (int i = 0; i <= p; i++) {
            curVars[i] = new IntBitSet(q);
            curSol[i] = new IntBitSet(q);
            added[i] = new IntBitSet(q);
            set[i] = new IntBitSet(q);
            vars[i] = new IntBitSet(q);
            sol[i] = new IntBitSet(q);
        }
    }

    public double[][] getProbs(int[][] desk, int bombs) {

        n = desk.length;
        m = desk[0].length;

        double[][] base = solve(desk, bombs);
        minesweeper.gui.Table.mem = base;

        double min = getMin(base, desk);
        if (min <= 10) {
            return base;
        }

//        int ii = 0, jj = 0;
//        double opt = 1e9;
//
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < m; j++) {
//                if (base[i][j] != min) {
//                    continue;
//                }
//
//                double s = 0;
//                for (int a = Math.max(0, i - 1); a < Math.min(n, i + 2); a++) {
//                    for (int b = Math.max(0, j - 1); b < Math.min(m, j + 2); b++) {
//                        s += base[a][b];
//                    }
//                }
//
//                int from = (int) Math.floor(s);
//                int to = (int) Math.ceil(s);
//
//                double cur = 0;
//                for (int t = from; t <= to; t++) {
//                    desk[i][j] = t;
//                    double[][] d = solve(desk, bombs);
//                    cur += (1 - Math.abs(t - s)) * getMin(d, desk);
//                }
//                desk[i][j] = -1;
//
//                if (opt > cur) {
//                    opt = cur;
//                    ii = i;
//                    jj = j;
//                }
//
//            }
//        }
//        base[ii][jj] -= 1e-3;

        return base;
    }

    private double[][] solve(int[][] desk, int bombs) {
        double[][] prob = new double[n][m];

        getMatrix(desk, n, m);

        if (p == 0) {
            // first turn
            prob[n - 1][m - 1] = -1;
            return prob;
        }

        comps = getComponents(x);
        int free = q;
        for (IntBitSet comp : comps) {
            free -= comp.bitCount();
        }

        int k = comps.length;
        f = new int[k][bombs << 1];
        g = new int[q][bombs << 1];
        initSets();
        for (int i = 0; i < k; i++) {
            findSolutions(i, 0);
        }

        double[] C = new double[bombs + 1];
        double[] V = new double[bombs + 1];
        for (int i = 0; i <= bombs; i++) {
            C[i] = comb(free, i);
            V[i] = i == 0 ? 0 : comb(free - 1, i - 1);
        }
        double[][] dp = new double[k + 1][bombs + 1];
        double total = getNumberOfSolutions(k, bombs, C, dp);
        M:
        for (int j = 0; j < q; j++) {
            for (int t = 0; t < k; t++) {
                if (comps[t].cont(j)) {
                    int[] tmp = f[t];
                    f[t] = g[j];
                    prob[I[j]][J[j]] = getNumberOfSolutions(k, bombs, C, dp) / total;
                    f[t] = tmp;
                    continue M;
                }
            }
            prob[I[j]][J[j]] = getNumberOfSolutions(k, bombs, V, dp) / total;
        }

        return prob;
    }

    private void findSolutions(int t, int i) {
        if (i == p) {
            int size = sol[i].bitCount();
            f[t][size]++;
            for (int j = 0; j < q; j++) {
                if (sol[i].cont(j)) {
                    g[j][size]++;
                }
            }
            return;
        }

        IntBitSet tCurVars = curVars[i].copy(comps[t]).and(x[i]);
        if (tCurVars.isEmpty()) {
            vars[i + 1].copy(vars[i]);
            sol[i + 1].copy(sol[i]);
            findSolutions(t, i + 1);
            return;
        }

        IntBitSet tAdded = added[i].copy(tCurVars).andNot(vars[i]);
        IntBitSet tSet = set[i].copy(tAdded);
        do {

            IntBitSet tCurSol = curSol[i].copy(tSet).or(sol[i]).and(tCurVars);
            if (tCurSol.bitCount() == r[i]) {
                vars[i + 1].copy(vars[i]).or(tCurVars);
                sol[i + 1].copy(sol[i]).or(tCurSol);
                findSolutions(t, i + 1);
            }

        } while (tSet.nextSubset(tAdded));

    }

    private double getNumberOfSolutions(int k, int bombs, double[] C, double[][] dp) {
        dp[0] = C;
        for (int i = 0; i < k; i++) {
            for (int s = 0; s <= bombs; s++) {
                double a = 0;
                for (int j = 0; j <= s; j++) {
                    a += f[i][j] * dp[i][s - j];
                }
                dp[i + 1][s] = a;
            }
        }
        return dp[k][bombs];
    }

    private void getMatrix(int[][] desk, int n, int m) {
        int[][] f = new int[n][m];
        p = 0;
        q = 0;
        I = new int[n * m];
        J = new int[n * m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (desk[i][j] > 0) {
                    p++;
                }
                if (desk[i][j] == -1) {
                    I[q] = i;
                    J[q] = j;
                    f[i][j] = q++;
                }
            }
        }

        x = new IntBitSet[p];
        r = new int[p];
        p = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int k = desk[i][j];
                if (k <= 0) {
                    continue;
                }
                r[p] = k;
                x[p] = new IntBitSet(q);
                for (int a = Math.max(0, i - 1); a < Math.min(n, i + 2); a++) {
                    for (int b = Math.max(0, j - 1); b < Math.min(m, j + 2); b++) {
                        if (desk[a][b] == -1) {
                            x[p].set(f[a][b]);
                        }
                    }
                }
                p++;
            }
        }
    }

    private double getMin(double[][] x, int[][] desk) {
        double min = 1e9;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (desk[i][j] == -1) {
                    min = Math.min(min, x[i][j]);
                }
            }
        }
        return min;
    }

    //-----------------------------------------------------------------------------------

    private IntBitSet[] getComponents(IntBitSet[] x) {
        int n = x.length;
        IntBitSet[] comps = new IntBitSet[n];
        IntBitSet b = new IntBitSet(q);
        int z = 0;
        for (int i = 0; i < n; i++) {
            if (x[i].isEmpty() || x[i].secs(b)) {
                continue;
            }
            IntBitSet c = go(i, x, new IntBitSet(q));
            comps[z++] = c;
            b.or(c);
        }
        return Arrays.copyOf(comps, z);
    }

    private IntBitSet go(int i, IntBitSet[] x, IntBitSet c) {
        c.or(x[i]);
        for (int j = 0; j < x.length; j++) {
            if (c.secs(x[j]) && !c.cont(x[j])) {
                c.or(go(j, x, c));
            }
        }
        return c;
    }

    private static double comb(int i, int j) {
        double p = 1;
        for (int t = 1; t <= j; t++) {
            p *= i--;
            p /= t;
        }
        return p;
    }

}
