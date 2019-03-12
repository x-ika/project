package bluster;

import static java.lang.Math.*;
import java.io.*;
import java.awt.image.BufferedImage;

public final class AndroidBluster {

    private static int dist(int a, int b) {
        int x = a >>> 16 & 255;
        int y = a >>> 8 & 255;
        int z = a & 255;
        int u = b >>> 16 & 255;
        int v = b >>> 8 & 255;
        int w = b & 255;
        return abs(x - u) + abs(y - v) + abs(z - w);
    }

    private static void print(int[][] x) {
        System.out.println("------------------------------------------");
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                System.out.printf("%3d", x[i][j]);
            }
            System.out.println("");
        }
    }

    //-----------------------------------------------------------------------------------

    private static int[][] X = new int[6][5];
    private static int[][] Y = new int[6][5];

    private static void oneLevel() throws Exception {
        BufferedImage img = DeviceBridge.getScreen();
        int[][] x = getBoard(img);
        print(x);

        String[] s = null;
        for (int moves = 1; moves <= 7; moves++) {
            for (int i = 0; i < 6; i++) {
                System.arraycopy(x[i], 0, p[moves][i], 0, 5);
            }
            String p = solve(moves);
            if (p != null) {
                s = p.split(" ");
                break;
            }
        }
        if (s == null) {
            System.exit(0);
        }
        String fname = "resources\\android\\a.txt";
        PrintWriter out = DeviceBridge.createHeader(fname);
        for (int i = 0; i < s.length; i += 2) {
            int a = Integer.parseInt(s[i]);
            int b = Integer.parseInt(s[i + 1]);
            int t = click1(a, b, x);
            DeviceBridge.appendTouch(out, X[a][b], Y[a][b]);
            DeviceBridge.appendWait(out, 0.35 * t);
        }
        DeviceBridge.appendTouch(out, 500, 960);
        out.close();
        DeviceBridge.executeScript(fname);
        Thread.sleep(1000);
    }

    private static int[][] getBoard(BufferedImage img) {
        int si = 130;
        int sj = 15;

        int zi = 155;
        int zj = 140;
        int[] colors = {
                200 << 16,
                80 << 16 | 210 << 8,
                200 << 16 | 210 << 8,
                80 << 8 | 230,
        };

        int[][] board = new int[6][5];
        int[] t = new int[zi * zj];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {

                img.getRGB(sj + j * zj, si + i * zi, zj, zi, t, 0, zj);
                int[] s = new int[4];
                for (int f = 0; f < 4; f++) {
                    int c = colors[f];
                    for (int p : t) {
                        if (dist(c, p) < 80) {
                            s[f]++;
                        }
                    }
                }
                for (int f = 0; f < 4; f++) {
                    if (s[f] > 100) {
                        board[i][j] = f + 1;
                        int ax = 0, ay = 0, n = 0;
                        int c = colors[f];
                        for (int y = 0; y < zi; y++) {
                            for (int x = 0; x < zj; x++) {
                                if (dist(c, t[y * zj + x]) < 80) {
                                    ax += x;
                                    ay += y;
                                    n++;
                                }
                            }
                        }
                        X[i][j] = sj + j * zj + ax / n;
                        Y[i][j] = si + i * zi + ay / n;
                    }
                }

            }
        }
        return board;
    }

    private static int[][][] p = new int[11][6][5];

    private static String solve(int max) {
        int[][] x = p[max];
        int s = 0;
        for (int[] y : x) {
            for (int v : y) {
                s += v;
            }
        }
        if (s == 0) {
            return "";
        }
        if (max == 0) {
            return null;
        }

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                if (x[i][j] == 0) {
                    continue;
                }
                for (int t = 0; t < 6; t++) {
                    System.arraycopy(x[t], 0, p[max - 1][t], 0, 5);
                }
                click1(i, j, p[max - 1]);
                String ret = solve(max - 1);
                if (ret != null) {
                    System.out.printf("%d %d\n", i + 1, j + 1);
                    return i + " " + j + " " + ret;
                }
            }
        }

        return null;

    }

    //-----------------------------------------------------------------------------------

    private static int[] is = new int[111];
    private static int[] js = new int[111];
    private static int[] ds = new int[111];
    private static final int[] di = {-1, 0, 1, 0};
    private static final int[] dj = {0, 1, 0, -1};
    private static int[][] s = new int[6][5];

    private static int click2(int pi, int pj, int[][] x) {
        if (--x[pi][pj] > 0) {
            return 1;
        }
        int z = 0;
        z = add(z, pi, pj);
        int d = 0;
        while (z != 0) {
            d++;
            for (int t = 0; t < z; t++) {
                is[t] += di[ds[t]];
                js[t] += dj[ds[t]];
                if (is[t] < 0 || js[t] < 0 || is[t] > 5 || js[t] > 4) {
                    remove(t--, --z);
                    continue;
                }
                s[is[t]][js[t]]++;
            }
            for (int t = 0; t < z; t++) {
                if (x[is[t]][js[t]] != 0) {
                    remove(t--, --z);
                }
            }
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 5; j++) {
                    if (x[i][j] > 0 && (x[i][j] -= s[i][j]) <= 0) {
                        x[i][j] = 0;
                        s[i][j] = -1;
                    }
                }
            }
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 5; j++) {
                    if (s[i][j] < 0) {
                        z = add(z, i, j);
                    }
                    s[i][j] = 0;
                }
            }
        }
        return d;
    }

    private static int click1(int pi, int pj, int[][] x) {
        if (--x[pi][pj] > 0) {
            return 1;
        }
        int z = 0;
        z = add(z, pi, pj);
        int d = 0;
        int[] ord = {0};
        while (z != 0) {
            d++;
            for (int dir : ord) {
                for (int t = z; t-- > 0;) {
                    if (ds[t] != dir) {
//                        continue;
                    }
                    is[t] += di[ds[t]];
                    js[t] += dj[ds[t]];
                    if (is[t] < 0 || js[t] < 0 || is[t] > 5 || js[t] > 4) {
                        remove(t--, --z);
                        continue;
                    }
                    if (x[is[t]][js[t]] > 0) {
                        if (--x[is[t]][js[t]] == 0) {
                            s[is[t]][js[t]]++;
                        }
                        remove(t--, --z);
                    }
                }
            }
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 5; j++) {
                    if (s[i][j] == 1) {
                        s[i][j] = 0;
                        z = add(z, i, j);
                    }
                }
            }
        }
        return d;
    }

    private static int add(int z, int pi, int pj) {
        int[] ord = {1, 0, 3, 2};
        for (int t : ord) {
            is[z] = pi;
            js[z] = pj;
            ds[z++] = t;
        }
        return z;
    }

    private static void remove(int t, int z) {
        System.arraycopy(is, t + 1, is, t, z - t);
        System.arraycopy(js, t + 1, js, t, z - t);
        System.arraycopy(ds, t + 1, ds, t, z - t);
    }

    //-----------------------------------------------------------------------------------

    public static void main(String[] args) throws Exception {

        for (int i = 0; i < Integer.parseInt(args[0]); i++) {
            oneLevel();
        }

    }

}
