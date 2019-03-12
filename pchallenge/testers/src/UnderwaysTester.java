import tapi.ProblemTester;

import java.util.*;

import com.simplejcode.commons.misc.structures.DataReader;

public class UnderwaysTester extends ProblemTester {
    public static void main(String[] args) throws Exception {
        new UnderwaysTester().run(args);
    }

    protected String getFileName() {
        return "war";
    }

    public void generateTests() {



        for (int test = 49; test <= 50; test++) {
            startWriting(test);
            random = new Random();
            int n = 8000;
            inputWriter.println(n);
            printa(n, test, 990000 - 5 + 14 * test - 560, 17, 0.9);
            printa(n, test, 990000, 1, 0.1);
            endWriting();

            try {
                in = new DataReader("war" + test + ".in");
                long x = solve();
                if (x == 0 || x == -1) {
                    test--;
                    continue;
                }
                System.out.println(x);
                outputWriter.println(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void printa(int n, int test, int d, int cc, double dens) {
        sb = new StringBuilder(1 << 16);
        int gs = test - 15;
        int m = 0;
        for (int i = 1; (i + 1) * gs * gs < n; i++) {
            m += generateFullGraph(i * gs * gs, gs, 7, 11, dens, m);
            int[] b = geninds(cc, gs * gs);
            for (int j = 0; j < b.length; j++) {
                sb.append(String.format("%d %d %d\r\n", 1, i * gs * gs + b[j], d + random.nextInt(1)));
                m++;
            }
            b = geninds(cc, gs * gs);
            for (int j = 0; j < b.length; j++) {
                sb.append(String.format("%d %d %d\r\n", n, i * gs * gs + b[j], d + random.nextInt(1)));
                m++;
            }
        }
        outputWriter.println(m);
        outputWriter.print(sb);
    }

    private int[] geninds(int gcount, int gs) {
        int[] b = new int[gcount];
        for (int t = 0; t < b.length; t++) {
            b[t] = random.nextInt(gs);
            for (int tt = 0; tt < t; tt++) {
                if (b[tt] == b[t]) {
                    t--;
                    break;
                }
            }
        }
        return b;
    }

    private StringBuilder sb;

    private int generateFullGraph(int from, int k, int da, int db, double dens, int m) {
        int res = 0;
        for (int i = 0; i < k * k; i++) {
            for (int j = 0; j < i; j++) {
                int x = i % k - j % k;
                int y = i / k - j / k;
                if (Math.abs(x) + Math.abs(y) < 5) {
                    sb.append(String.format("%d %d %d\r\n", from + j, from + i, da * x * x + db * y * y));
                    res++;
                    if (m + res >= 15000) {
                        return res;
                    }
                }
            }
//            for (int j : geninds((int) (i * dens), i)) {
//                res++;
//                sb.append(String.format("%d %d %d\r\n", from + j, from + i, random.nextInt(db - da + 1) + da));
//            }
        }
        return res;
    }

    public int test() {
        return solve() == out.nextInt() ? 0 : 1;
    }

    private long solve() {
        int n = in.nextInt();
        List<List<T>> g1 = read(n);
        List<List<T>> g2 = read(n);

        final long[] d = new long[n];
        final long[] f = new long[n];
        dj(n, 0, d, f, 0, g1);

        long min = -1, max = (long) 1e10;
        while (max - min > 1) {
            long mid = min + max >> 1;

            dj(n, n - 1, f, d, mid, g2);
            if (f[0] > d[n - 1]) {
                max = mid;
            } else {
                min = mid;
            }

        }

        if (max > 9e9) {
            max = -1;
        }
        return max;
    }

    private static class T {
        int j;
        long d;

        public T(int j, long d) {
            this.j = j;
            this.d = d;
        }
    }

    private static boolean[] t = new boolean[8888];
    private static void dj(int n, int s, final long[] f, long[] d, long add, List<List<T>> g) {
        PriorityQueue<Integer> q = new PriorityQueue<>(n, (o1, o2) -> Long.signum(f[o1] - f[o2]));
        Arrays.fill(t, false);
        Arrays.fill(f, (long) 1e15);
        f[s] = 0;
        q.add(s);
        while (!q.isEmpty()) {
            int x = q.poll();
            if (t[x]) {
                continue;
            }
            t[x] = true;
            if (f[x] > d[x] && x != 0) {
                f[x] += add;
            }
            for (T y : g.get(x)) {
                long c = f[x] + y.d;
                if (f[y.j] > c) {
                    f[y.j] = c;
                    q.add(y.j);
                }
            }
        }
    }

    private List<List<T>> read(int n) {
        List<List<T>> g1 = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            g1.add(new ArrayList<>());
        }
        int a = in.nextInt();
        for (int i = 0; i < a; i++) {
            int x = in.nextInt() - 1;
            int y = in.nextInt() - 1;
            int d = in.nextInt();
            g1.get(x).add(new T(y, d));
            g1.get(y).add(new T(x, d));
        }
        return g1;
    }
}
