import tapi.ProblemTester;

import java.util.*;

public class GO2013_3E_Tester extends ProblemTester {

    public static void main(String[] args) throws Exception {
        new GO2013_3E_Tester().run(args);
    }

    protected String getFileName() {
        return "board";
    }

    public void generateTests() {
        startWriting(1);
        inputWriter.println("1 2");
        inputWriter.println("3 1 1 2");
        inputWriter.println("4 2 1 1");
        endWriting();
        for (int tc = 2; tc <= 99; tc++) {
            startWriting(tc);

            int n = 2 * nextPositive(5), m = 2 * nextPositive(5);
            int[][] a = new int[n][m];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    a[i][j] = nextPositive(10);
                }
            }
            inputWriter.printf("%d %d\r\n", n / 2, m / 2);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    inputWriter.printf(j == 0 ? "%d" : " %d", a[i][j]);
                }
                inputWriter.println();
            }
            build(a);
            endWriting();

        }

    }

    public int test() {
        int n = in.nextInt();
        int m = in.nextInt();
        int[][] a = new int[2 * n][2 * m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                a[i][j] = in.nextInt();
            }
        }
//        return solve(n, a) == out.nextInt() ? 0 : 1;
        return 0;
    }

    private List<String> build(int[][] a) {
        int n = a.length, m = a[0].length;
        int k = n * m / 2;
        List<String> ret = new ArrayList<>();
        for (int iter = 0; iter < k; iter++) {
            int i = iter / (m / 2);
            int j = 2 * (iter % (m / 2)) + i % 2;

            int fi = -1, fj = 0;
            for (int p = 0; p < n; p++) {
                for (int q = 0; q < m; q++) {
                    if (((p + q & 1) == 1 || p > i || p == i && q >= j) && (fi == -1 || a[p][q] > a[fi][fj])) {
                        fi = p;
                        fj = q;
                    }
                }
            }

            // (fi, fj) --> (i, j)

            if (i != fi) {
                // THEY DIFFER IN ROW - ALGO1
                for (int t = j; t != fj; t = (t + 1) % m) {
                    row(a, i, ret);
                }
                for (int t = fi; t != i; t = (t + 1) % n) {
                    col(a, fj, ret);
                }
                for (int t = fj; t != j; t = (t + 1) % m) {
                    row(a, i, ret);
                }
                if ((i + fj & 1) == 0) {
                    row(a, i, ret);
                }
                for (int t = i; t != fi; t = (t + 1) % n) {
                    col(a, fj, ret);
                }
                if ((i + fj & 1) == 0) {
                    for (int t = 0; t < m - 1; t++) {
                        row(a, i, ret);
                    }
                }
            } else if (j != fj) {
                // THEY DIFFER IN COL - ALGO2
                for (int col = j % 2; col < j; col += 2) {
                    col(a, col, ret);
                }
                for (int t = fj; t != j; t = (t + 1) % m) {
                    row(a, i, ret);
                }
                for (int col = j % 2; col < j; col += 2) {
                    for (int t = 0; t < n - 1; t++) {
                        col(a, col, ret);
                    }
                }
            }

            //print(a);

        }

        int s = 0;
        for (int p = 0; p < n; p++) {
            for (int q = 0; q < m; q++) {
                if ((p + q & 1) == 0) {
                    s += a[p][q];
                }
            }
        }
        if (s != solve(a)) {
            System.out.println("FAILURE");
        }
        return ret;
    }

    private static void col(int[][] a, int j, List<String> ret) {
        ret.add("C" + (j + 1));
        int t = a[a.length - 1][j];
        for (int i = a.length; i-- > 1; ) {
            a[i][j] = a[i - 1][j];
        }
        a[0][j] = t;
    }

    private static void row(int[][] a, int i, List<String> ret) {
        ret.add("R" + (i + 1));
        int t = a[i][a[0].length - 1];
        for (int j = a[0].length; j-- > 1; ) {
            a[i][j] = a[i][j - 1];
        }
        a[i][0] = t;
    }

    private static void print(int[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                System.out.printf("%d ", a[i][j]);
            }
            System.out.println("");
        }
        System.out.println("-----------------------------");
    }

    private int solve(int[][] a) {
        int n = a.length, m = a[0].length;
        int[] b = new int[n * m];
        for (int i = 0; i < n; i++) {
            System.arraycopy(a[i], 0, b, i * m, m);
        }
        Arrays.sort(b);
        int s = 0;
        for (int i = 0; i < b.length / 2; i++) {
            s += b[n * m - i - 1];
        }
        return s;
    }

}
