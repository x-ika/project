package paata;

import static com.simplejcode.commons.gui.GraphicUtils.*;

import static java.lang.Math.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;

public class P1 {
    private static final int N = 17, M = 7140, K = 5, R = 34;
    private static final int[] A = new int[K];
    private static final int[][] S = new int[M][K];

    public static void main(String[] args) {
        System.out.println(go(new int[4], 0, 0));

        int[] s = new int[R];
        Random r = new Random(1);
        s[0] = r.nextInt(M);
        int[] cur = new int[K];
        M:
        for (int i = 0; ;) {
            if (i > 18) {
                s[i] += Math.min(M - s[i] - 1, 9);
            }
            int[] x = S[s[i]++];
            if (check(x, cur)) {
                if (++i == R) {
                    paint(s);
                    pause((int) 1e7);
                    System.exit(0);
                }
                s[i] = 0;
            } else {
                while (s[i] == M) {
                    if (i-- == 0) {
                        break M;
                    }
                    x = S[--s[i]];
                    for (int j = 0; j < K; j++) {
                        cur[j] ^= x[j]; 
                    }
                }
            }
        }
    }

    private static boolean check(int[] x, int[] cur) {
        for (int i = 0; i < K; i++) {
            if ((x[i] & cur[i]) != 0) {
                return false;
            }
        }
        for (int i = 0; i < K; i++) {
            cur[i] |= x[i];
        }
        return true;
    }

    private static int go(int[] a, int i, int z) {
        if (i == a.length) {
            Arrays.fill(A, 0);
            for (int j = 0; j < i; j++) {
                for (int k = 0; k < j; k++) {
                    int x = getIndex(a[j], a[k]);
                    A[x >> 5] |= 1 << x;
                }
            }
            for (int j = 1; j < i; j++) {
                int x = getIndex(a[0], a[j]), y = getIndex(a[j % 3 + 1], a[(j + 1) % 3 + 1]);
                System.arraycopy(A, 0, S[z], 0, A.length);
                S[z][x >> 5] &= ~(1 << x);
                S[z][y >> 5] &= ~(1 << y);
                z++;
            }
        } else for (a[i] = 0; a[i] < (i == 0 ? N : a[i - 1]); a[i]++) {
            z = go(a, i + 1, z);
        }
        return z;
    }

    private static void paint(final int... s) {
        JFrame f = new JFrame("V");
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setSize(900, 600);
        f.setContentPane(new JComponent() {
            public void paint(Graphics g) {
                int R = 200;
                int[] x = new int[N];
                int[] y = new int[N];
                for (int i = 0; i < N; i++) {
                    x[i] = (int) (400 + R * cos(PI / N * 2 * i));
                    y[i] = (int) (300 + R * sin(PI / N * 2 * i));
                    g.drawRect(x[i] - 1, y[i] - 1, 2, 2);
                }
                for (int i = 0; i < s.length; i+=3) {
                    g.setColor(new Color(color(i * 1d / s.length)));
                    ((Graphics2D) g).setStroke(new BasicStroke(2 * (i % 2) + 1));
                    int[] o = S[s[i] -1];
                    for (int a = 0; a < N; a++) {
                        for (int b = 0; b < a; b++) {
                            int ind = getIndex(a, b);
                            if ((o[ind >> 5] & 1 << ind) != 0) {
                                System.out.printf("%d %d %d\n", i, a, b);
                                g.drawLine(x[a], y[a], x[b], y[b]);
                            }
                        }
                    }
                }
            }
        });
        f.setVisible(true);
    }
}
