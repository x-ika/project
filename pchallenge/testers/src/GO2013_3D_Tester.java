import tapi.ProblemTester;

import java.util.Arrays;

public class GO2013_3D_Tester extends ProblemTester {

    public static void main(String[] args) throws Exception {
        new GO2013_3D_Tester().run(args);
    }

    protected String getFileName() {
        return "roads";
    }

    public void generateTests() {
        startWriting(1);
        inputWriter.printf("4\r\n2 1 4 3\r\n");
        outputWriter.println("2");
        endWriting();
        for (int tc = 2; tc <= 30; tc++) {

            int n = tc == 30 ? 99 : tc < 6 ? 10 : tc < 20 ? 99 : 100;
            startWriting(tc);
            inputWriter.printf("%d\r\n", n);

            int[] sz = new int[n];
            int z = 0, s = 0;
            while (true) {
                while (s != n) {
                    if (n - s < 4) {
                        sz[z] = n - s;
                    } else {
                        sz[z] = random.nextInt(n - s - 2) + 2;
                        if (sz[z] == n - s - 1) {
                            sz[z]++;
                        }
                    }
                    s += sz[z++];
                }
                if (tc < 5 && z == 2) {
                    s = z = 0;
                    continue;
                }
                if (tc > 5 && tc < 20 && z < 15) {
                    s = z = 0;
                    continue;
                }
                if (tc > 5 && tc < 28 && nextPositive(10) < 8 && z < 4) {
                    s = z = 0;
                    continue;
                }
                if (tc == 28) {
                    for (int i = 0; i < 32; i++) {
                        sz[i] = 3;
                    }
                    sz[32] = 4;
                    z = 33;
                }
                if (tc == 29) {
                    sz[0] = n;
                    z = 1;
                }
                if (tc == 30) {
                    sz[0] = n - 2;
                    sz[1] = 2;
                    z = 2;
                }
                break;
            }
            System.out.println(Arrays.toString(Arrays.copyOf(sz, z)));

            int[] a = new int[n];
            boolean[] b = new boolean[n];
            for (int i = 0; i < z; i++) {
                int f = nextRandom(b, s--), p = f;
                b[f] = true;
                for (int j = 0; j < sz[i] - 1; j++) {
                    int c = nextRandom(b, s--);
                    b[c] = true;
                    a[p] = c;
                    p = c;
                }
                a[p] = f;
            }
            for (int i = 0; i < n; i++) {
                if (a[i] == i) {
                    System.out.println("TEST GENERATION FAILURE");
                }
                for (int j = 0; j < i; j++) {
                    if (a[i] == a[j]) {
                        System.out.println("TEST GENERATION FAILURE");
                    }
                }
                inputWriter.printf(i == 0 ? "%d" : " %d", a[i] + 1);
            }
            outputWriter.println(solve(n, a));
            endWriting();


        }


    }

    private int nextRandom(boolean[] b, int have) {
        int k = random.nextInt(have);
        for (int i = 0; i < b.length; i++) {
            if (!b[i] && k-- == 0) {
                return i;
            }
        }
        throw new RuntimeException("?");
    }

    public int test() {
        int n = in.nextInt();
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = in.nextInt() - 1;
        }
        return solve(n, a) == out.nextInt() ? 0 : 1;
    }

    private int solve(int n, int[] a) {
        boolean[] b = new boolean[n];
        int odd = 0;
        for (int i = 0; i < n; i++) {
            int c = 0;
            while (!b[i]) {
                b[i] = true;
                i = a[i];
                c++;
            }
            if (c % 2 == 1) {
                odd++;
            }
        }
        int p = n - odd >> 1;
        return p * (p - 1) + odd * (odd - 1) / 2;
    }
}
