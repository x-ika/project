import tapi.ProblemTester;

public class GO2013_3B_Tester extends ProblemTester {

    public static void main(String[] args) throws Exception {
        new GO2013_3B_Tester().run(args);
    }

    protected String getFileName() {
        return "astrapark";
    }

    public void generateTests() {
        for (int tc = 1; tc <= 50; tc++) {

            startWriting(tc);

            int[] a, b;
            if (tc == 1) {
                a = new int[]{20, 15, 30, 30};
                b = new int[]{20, 25, 20, 31};
            } else if (tc == 2) {
                a = new int[]{15, 17, 15};
                b = new int[]{16, 15, 18, 15};
            } else if (tc == 3) {
                a = new int[]{100};
                b = new int[]{15, 15, 15, 15};
            } else if (tc < 6) {
                a = new int[100];
                b = new int[100];
                for (int i = 0; i < 100; i++) {
                    a[i] = b[i] = tc == 4 ? 15 : 100;
                }
            } else if (tc < 8) {
                a = new int[100];
                b = new int[100];
                for (int i = 0; i < 100; i++) {
                    a[i] = i % 2 == 0 ? 98 : 100;
                    b[i] = i % 2 == 1 ? 98 : 100;
                }
                if (tc == 7) {
                    a[0] = 99;
                }
            } else {

                a = new int[nextPositive(20) + 80];
                b = new int[nextPositive(20) + 80];
                int mean = 5 * (nextNonNegative(17) + 3);
                int c = nextPositive(5);
                for (int i = 0; i < a.length; i++) {
                    a[i] = (int) (mean + 3 * random.nextGaussian());
                    a[i] = Math.max(a[i], 15);
                    a[i] = Math.min(a[i], 100);
                }
                for (int i = 0; i < b.length; i++) {
                    b[i] = (int) (mean + c * random.nextGaussian());
                    b[i] = Math.max(b[i], 15);
                    b[i] = Math.min(b[i], 100);
                }

            }

            inputWriter.printf("%d %d\r\n", a.length, b.length);
            for (int i = 0; i < a.length; i++) {
                inputWriter.printf(i == 0 ? "%d" : " %d", a[i]);
            }
            inputWriter.println();
            for (int i = 0; i < b.length; i++) {
                inputWriter.printf(i == 0 ? "%d" : " %d", b[i]);
            }
            int[] ans = solve(a, b);
            System.out.println(ans[0] + " " + ans[1]);
            outputWriter.printf("%d %d\r\n", ans[0], ans[1]);

            endWriting();
        }

    }

    public int test() {
        int n = in.nextInt();
        int m = in.nextInt();
        int[] a = new int[n];
        int[] b = new int[m];
        for (int i = 0; i < n; i++) {
            a[i] = in.nextInt();
        }
        for (int i = 0; i < m; i++) {
            b[i] = in.nextInt();
        }
        int[] ans = solve(a, b);
        return ans[0] == out.nextInt() && ans[1] == out.nextInt() ? 0 : 1;
    }

    private int[] solve(int[] a, int[] b) {
        int n = a.length, m = b.length;
        int x = 0, y = 0;
        int i = 0, j = 0, p = 0;
        while (i < n || j < m) {
            if (j == m || i < n && a[i] < b[j]) {
                if (j < m) {
                    b[j] -= a[i];
                }
                i++;
            } else if (i == n || j < m && b[j] < a[i]) {
                if (i < n) {
                    a[i] -= b[j];
                }
                j++;
            } else {
                // a[i] = b[j]
                i++;
                j++;
            }
            int q = Integer.signum(i - j);
            if (p <= 0 && q > 0) {
                p = 1;
                x++;
            }
            if (p >= 0 && q < 0) {
                p = -1;
                y++;
            }
        }
        return new int[]{x, y};
    }

}
