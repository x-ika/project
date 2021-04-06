import java.io.*;
import java.util.*;
import java.math.*;

public class Main implements Runnable {

    class SegmentList {
        double[] x = new double[99];
        int z = 0;

        void add(double a, double b) {
            if (z + 2 > x.length) {
                double[] t = new double[2 * z];
                System.arraycopy(x, 0, t, 0, z);
                x = t;
            }
            x[z++] = a;
            x[z++] = b;
        }

        boolean isEmpty() {
            return z == 0;
        }

        void expandInto(SegmentList s, double d) {
            int j = 0;
            for (int i = 0; i < z; i += 2) {
                while (true) {
                    if (s.x[j] <= x[i] && x[i + 1] <= s.x[j + 1]) {
                        //found
                        x[i] = Math.max(s.x[j], x[i] - d);
                        x[i + 1] = Math.min(s.x[j + 1], x[i + 1] + d);
                        break;
                    }
                    if ((j += 2) >= s.z) {
                        z = 0;
                        return;
                    }
                }
            }
            for (int i = 0; i < z - 2; i += 2) {
                if (x[i + 1] >= x[i + 2]) {
                    x[i + 1] = x[i + 3];
                    if (z > i + 4) {
                        System.arraycopy(x, i + 4, x, i + 2, z - i - 4);
                    }
                    z -= 2;
                    i -= 2;
                }
            }
        }

        void intersection(SegmentList s) {
            SegmentList p = new SegmentList();
            intersection(s, 0, 0, p);
            x = p.x;
            z = p.z;
        }

        void intersection(SegmentList s, int i, int j, SegmentList p) {
            if (i == z || j == s.z) {
                return;
            }
            if (x[i + 1] < s.x[j]) {
                intersection(s, i + 2, j, p);
                return;
            }
            if (s.x[j + 1] < x[i]) {
                intersection(s, i, j + 2, p);
                return;
            }
            double b = Math.min(x[i + 1], s.x[j + 1]);
            p.add(Math.max(x[i], s.x[j]), b);
            if (x[i + 1] <= b) {
                i += 2;
            }
            if (s.x[j + 1] <= b) {
                j += 2;
            }
            intersection(s, i, j, p);
        }
    }

    public void run() {
        long startTime = System.nanoTime();

        int WAY = nextInt();
        int Y = nextInt();
        int L = nextInt();
        int W = nextInt();
        int V = nextInt();
        int HV = nextInt();
        WAY -= W;

        int n = nextInt();
        int[] x = new int[n];
        int[] y = new int[n];
        int[] a = new int[n];
        int[] b = new int[n];
        int[] c = new int[n];

        Set<Double> events = new TreeSet<Double>();
        events.add(0d);
        for (int i = 0; i < n; i++) {
            x[i] = nextInt();
            y[i] = nextInt();
            a[i] = nextInt() + L;
            b[i] = nextInt() + W;
            c[i] = nextInt() - V;
            x[i] -= L;
            y[i] -= W;
            if (c[i] != 0) {
                addEvent(events, x[i], c[i]);
                addEvent(events, x[i] + a[i], c[i]);
            }
            for (int j = 0; j < i; j++) {
                if (b[i] < b[j]) {
                    swap(x, i, j);
                    swap(y, i, j);
                    swap(a, i, j);
                    swap(b, i, j);
                    swap(c, i, j);
                }
            }
        }

        double maxLiveTime = -1;
        SegmentList ans = new SegmentList();
        ans.add(Y, Y);
        double prev = 0;
        SegmentList prevPossible = get(0, WAY, x, y, a, b, c);

        for (double event : events) {

            SegmentList curPossible = get(event, WAY, x, y, a, b, c);
            ans.expandInto(prevPossible, (event - prev) * HV);
            ans.intersection(curPossible);
            if (ans.isEmpty()) {
                maxLiveTime = event;
                break;
            }
            for (int i = 0; i < ans.z; i++) {
                ans.x[i] += 1e-12;
                ans.x[i + 1] -= 1e-12;
            }

            prevPossible = curPossible;
            prev = event;

        }

        println(maxLiveTime);

        if (fileIOMode) {
            System.out.println((System.nanoTime() - startTime) / 1e9);
        }
        out.close();
    }

    private SegmentList get(double event, int WAY, int[] x, int[] y, int[] a, int[] b, int[] c) {
        int last = 0;
        SegmentList res = new SegmentList();
        for (int i = 0; i < x.length; i++) {
            double P = x[i] + event * c[i];
            double Q = P + a[i];
            double d = 1e-12 * c[i];
            if (P + d < 0 && Q + d > 0) {
                if (last <= y[i]) {
                    res.add(last, y[i]);
                }
                last = y[i] + b[i];
            }
        }
        if (last <= WAY) {
            res.add(last, WAY);
        }
        return res;
    }

    private void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    void addEvent(Collection<Double> events, int x, int v) {
        if ((long) x * v <= 0) {
            events.add(1d * -x / v);
        }
    }

    //-----------------------------------------------------------------------------------

    private static boolean fileIOMode;
    private static String problemName = "kamikadze";
    private static BufferedReader in;
    private static PrintWriter out;
    private static StringTokenizer tokenizer;

    public static void main(String[] args) throws Exception {
        fileIOMode = true;
        if (fileIOMode) {
            in = new BufferedReader(new FileReader(problemName + ".in"));
            out = new PrintWriter(problemName + ".out");
        } else {
            in = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintWriter(System.out);
        }
        tokenizer = new StringTokenizer("");

        new Thread(new Main()).start();
    }

    private static String nextLine() {
        try {
            return in.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    private static String nextToken() {
        while (!tokenizer.hasMoreTokens()) {
            tokenizer = new StringTokenizer(nextLine());
        }
        return tokenizer.nextToken();
    }

    private static int nextInt() {
        return Integer.parseInt(nextToken());
    }

    private static long nextLong() {
        return Long.parseLong(nextToken());
    }

    private static double nextDouble() {
        return Double.parseDouble(nextToken());
    }

    private static BigInteger nextBigInteger() {
        return new BigInteger(nextToken());
    }

    private static void print(Object o) {
        if (fileIOMode) {
            System.out.print(o);
        }
        out.print(o);
    }

    private static void println(Object o) {
        if (fileIOMode) {
            System.out.println(o);
        }
        out.println(o);
    }

    private static void printf(String s, Object... o) {
        if (fileIOMode) {
            System.out.printf(s, o);
        }
        out.printf(s, o);
    }
}
