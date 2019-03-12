import tapi.ProblemTester;

public class StringCompareTester extends ProblemTester {
    public static void main(String[] args) throws Exception {
        new StringCompareTester().run(args);
    }

    protected String getFileName() {
        return "strcmp";
    }

    public void generateTests() {

        for (int i = 1; i <= 10; i++) {
            int l = i < 6 ? 20 : 100000, q = i < 6 ? 1000 : 100000;
            startWriting(i);
            inputWriter.println(nextString(l));
            inputWriter.println(nextString(l));
            inputWriter.println(q);
            for (int j = 0; j < q; j++) {
                int k = nextNonNegative(l);
                inputWriter.printf("%d %d %d\n", nextNonNegative(l - k), nextNonNegative(l - k), k);
            }
            endWriting();
        }

    }

    public int test() {
        long P = (int) 1e9 + 9;
        long[] pow = new long[100100];
        for (int i = 0; i < pow.length; i++) {
            pow[i] = i == 0 ? 1 : P * pow[i - 1];
        }

        String s1 = in.nextToken();
        String s2 = in.nextToken();
        long[] h1 = getHash(s1, pow);
        long[] h2 = getHash(s2, pow);

        int q = in.nextInt();
        for (int i = 0; i < q; i++) {

            int a = in.nextInt();
            int b = in.nextInt();
            int k = in.nextInt();

            int min = 0, max = k + 1;
            while (max - min > 1) {
                int mid = min + max >> 1;
                if (equals(h1, a, a + mid, h2, b, b + mid, pow)) {
                    min = mid;
                } else {
                    max = mid;
                }
            }
            int res = min == k ? 0 : Integer.signum(s1.charAt(a + min) - s2.charAt(b + min));

            if (res != out.nextInt()) {
                return 1;
            }

        }

        return 0;
    }

    private boolean equals(long[] h1, int a1, int b1, long[] h2, int a2, int b2, long[] p) {
        return (h1[b1] - h1[a1]) * p[a2] == (h2[b2] - h2[a2]) * p[a1];
    }

    private long[] getHash(String s, long[] p) {
        int n = s.length();
        long[] h = new long[n + 1];
        for (int i = 0; i < n; i++) {
            h[i + 1] = h[i] + s.charAt(i) * p[i];
        }
        return h;
    }

    private String nextString(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n / 2; i++) {
            sb.append('x');
        }
        for (int i = 0; i < n / 2; i++) {
            sb.append((char) ('a' + random.nextInt(26)));
        }
        return sb.toString();
    }
}
