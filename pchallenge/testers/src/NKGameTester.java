import tapi.ProblemTester;

public class NKGameTester extends ProblemTester {
    public static void main(String[] args) throws Exception {
        new NKGameTester().run(args);
    }

    protected String getFileName() {
        return "nkgame";
    }

    public void generateTests() {

        String files =
                "6 9\n" +
                        "9 3\n" +
                        "20 26\n" +
                        "17 33\n" +
                        "4 1\n" +
                        "9 15\n" +
                        "1000000 151\n" +
                        "999999 3010\n" +
                        "123456 2524\n" +
                        "312345 988888\n" +
                        "257470121962 2\n" +
                        "1004844898 5998\n" +
                        "999999999929 9869\n" +
                        "1000000000000 7885\n" +
                        "369544242544 20\n" +
                        "999999999995 2961\n" +
                        "890630293137 9910\n" +
                        "312576262614 8689\n" +
                        "752744976641 12\n" +
                        "155104102185 4617";

        for (int i = 1; i <= 20; i++) {

            String[] s = files.split("\n")[i - 1].split(" ");

//            System.out.printf("%d %d\n", Long.parseLong(s[0]), Long.parseLong(s[1]));
            startWriting(i);
            inputWriter.printf("%d %d\n", Long.parseLong(s[0]), Long.parseLong(s[1]));
            endWriting();
        }

    }

    public int test() {
        long n = in.nextLong();
        long k = in.nextLong();

        return out.nextLong() == get(n, k) + 1 ? 0 : 1;
    }

    private long get(long n, long k) {
        if (n <= k) {
            return getx((int) n, (int) k);
        }
        long out = n / (k + 1);
        long x = get(n - out, k);
        long start = out * (k + 1) % n;
        if (x == 0) {
            return start;
        }


        long min = 0, max = n;
        while (max - min > 1) {

            long mid = min + max >> 1;
            long t = count(start, (start + mid) % n, k, n);
            if (mid <= t + x) {
                min = mid;
            } else {
                max = mid;
            }

        }

        return (start + min) % n;
    }

    private long getx(int n, int k) {
        int[] f = new int[n + 1];
        f[1] = 0;
        for (int i = 2; i <= n; i++) {
            int t = (k + 1) % i;
            f[i] = (f[i - 1] + t) % i;
        }
        return f[n];
    }

    private long count(long a, long b, long k, long n) {
        if (a < b) {
            return count(a, b, k);
        }
        return count(a, n, k) + count(0, b, k);
    }

    private long count(long a, long b, long k) {
        a = inc(a, k);
        b = inc(b, k);
        return (b - a) / (k + 1);
    }

    private long inc(long a, long k) {
        long t = a % (k + 1);
        return a + (t == k ? 0 : k - t);
    }
}
