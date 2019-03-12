package sudoku.solver;

import tester.Solver;

public class IkasSolver implements Solver<int[][], int[][]> {

    private static final int[] F = new int[512];
    private static final int[][] G = new int[512][];
    private static final int[][] D = new int[9][9];

    private int[] row = new int[9];
    private int[] col = new int[9];
    private int[] div = new int[9];
    private int[][] a = new int[9][9];

    static {
        for (int i = 0; i < F.length; i++) {
            F[i] = Integer.bitCount(i);
            G[i] = new int[F[i]];
            for (int j = 0, k = 0; j < 9; j++) {
                if ((i & 1 << j) > 0) {
                    G[i][k++] = j + 1;
                }
            }
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                D[i][j] = 3 * (i / 3) + j / 3;
            }
        }
    }

    public int[][] solve(int[][] sudoku) {
        a = sudoku;

        for (int i = 0; i < 9; i++) {
            row[i] = col[i] = div[i] = 0;
        }
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (a[i][j] != 0) {
                    put(i, j, a[i][j]);
                }
            }
        }

        search();

        return a;
    }

    private boolean search() {
        int min = 11, x = 0, y = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (a[i][j] == 0) {
                    int d = domain(i, j);
                    if (d == 0) {
                        return false;
                    }
                    if (min > F[d]) {
                        min = F[d];
                        x = i;
                        y = j;
                    }
                }
            }
        }
        if (min == 11) {
            return true;
        }
        int[] cur = G[domain(x, y)];
        for (int v : cur) {
            for (int t = 0; t < 9; t++) {
                if (t!=y&&a[x][t]==v) {
                    System.out.println("bva");
                }
            }
            put(x, y, v);
            if (search()) {
                return true;
            }
            rem(x, y, v);
        }
        return false;
    }

    private int domain(int i, int j) {
        return 511 & ~row[i] & ~col[j] & ~div[D[i][j]];
    }

    private void put(int i, int j, int k) {
        a[i][j] = k--;
        row[i] |= 1 << k;
        col[j] |= 1 << k;
        div[D[i][j]] |= 1 << k;
    }

    private void rem(int i, int j, int k) {
        k--;
        a[i][j] = 0;
        row[i] ^= 1 << k;
        col[j] ^= 1 << k;
        div[D[i][j]] ^= 1 << k;
    }

}
