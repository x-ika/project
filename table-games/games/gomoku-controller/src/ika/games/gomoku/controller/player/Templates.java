package ika.games.gomoku.controller.player;

import ika.games.gomoku.controller.action.GomokuMove;

final class Templates {

    private static final char[] V = {'.', 'X', '0', '#'};

    private static final int[][][] M_INV = {
            {
                    {1, 0},
                    {0, 1},
            },
            {
                    {-1, 0},
                    {0, 1},
            },
            {
                    {1, 0},
                    {0, -1},
            },
            {
                    {-1, 0},
                    {0, -1},
            },
            {
                    {0, 1},
                    {1, 0},
            },
            {
                    {0, -1},
                    {1, 0},
            },
            {
                    {0, 1},
                    {-1, 0},
            },
            {
                    {0, -1},
                    {-1, 0},
            },
    };

    public static final Templates instance = new Templates();


    private final String[][] DATA = {

            {
                    "#",
                    "X",
            },
            {
                    ".0X",
                    ".X.",
                    "#..",
            },
            {
                    "....0",
                    "..0X.",
                    "..X..",
                    ".X...",
                    "#....",
            },


            {
                    "0#",
                    "X.",
            },
            {
                    ".0",
                    "X.",
                    ".#",
            },
            {
                    "0.0.",
                    ".X..",
                    "..X.",
                    "...#",
            },
            {
                    "...0",
                    ".0X.",
                    ".X..",
                    "#...",
            },

    };

    private final int[] N = new int[M_INV.length * DATA.length];
    private final int[] M = new int[M_INV.length * DATA.length];
    private final int[][][] COMPILED_DATA = new int[M_INV.length * DATA.length][][];


    public Templates() {

        for (int ind = 0; ind < DATA.length; ind++) {
            String[] template = DATA[ind];
            for (int d = 0; d < M_INV.length; d++) {
                int[][] x = M_INV[d];

                int p = M_INV.length * ind + d;

                N[p] = Math.abs(template.length * x[0][0] + template[0].length() * x[0][1]);
                M[p] = Math.abs(template.length * x[1][0] + template[0].length() * x[1][1]);
                COMPILED_DATA[p] = new int[N[p]][M[p]];
                for (int i = 0; i < template.length; i++) {
                    for (int j = 0; j < template[i].length(); j++) {

                        int r = scalar(i, j, x[0], N[p]);
                        int c = scalar(i, j, x[1], M[p]);

                        COMPILED_DATA[p][r][c] = template[i].charAt(j);

                    }
                }

            }
        }
    }

    private int scalar(int a, int b, int[] t, int max) {
        int x = a * t[0] + b * t[1];
        return t[0] + t[1] < 0 ? max + x - 1 : x;
    }

    public GomokuMove getMove(int[][] d) {

        int n = d.length;
        int m = d[0].length;

        int imin = n, imax = 0, jmin = m, jmax = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (d[i][j] != 0) {
                    imin = Math.min(imin, i);
                    imax = Math.max(imax, i);
                    jmin = Math.min(jmin, j);
                    jmax = Math.max(jmax, j);
                }
            }
        }

        for (int[][] t : COMPILED_DATA) {
            int tn = t.length;
            int tm = t[0].length;
            for (int i = Math.max(0, imax - tn + 1); i <= n - tn && i <= imin; i++) {
                M:
                for (int j = Math.max(0, jmax - tm + 1); j <= m - tm && j <= jmin; j++) {

                    int resi = 0, resj = 0;

                    for (int a = 0; a < tn; a++) {
                        for (int b = 0; b < tm; b++) {

                            if (t[a][b] == '#') {
                                if (d[i + a][j + b] != 0) {
                                    continue M;
                                }
                                resi = i + a;
                                resj = j + b;
                                continue;
                            }

                            if (t[a][b] != V[d[i + a][j + b]]) {
                                continue M;
                            }

                        }
                    }

                    return new GomokuMove(resi, resj);

                }
            }

        }

        return null;

    }

}
