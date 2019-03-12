import tapi.ProblemTester;

import java.util.Arrays;
import static java.lang.Math.abs;

public class BWStonesTester extends ProblemTester {
    public static void main(String[] args) throws Exception {
        new BWStonesTester().run(args);
    }

    protected String getFileName() {
        return "stones";
    }

    public void generateTests() {
        for (int i = 1; i <= 2; i++) {
            startWriting(i);
            int n = nextNonNegative(0) + 10;
            generate(n);
        }
        for (int i = 3; i <= 10; i++) {
            startWriting(i);
            int n = nextNonNegative(100) + 900;
            generate(n);
        }
        for (int i = 11; i <= 18; i++) {
            startWriting(i);
            int n = nextNonNegative(10000) + 90000;
            generate(n);
        }
        for (int i = 19; i <= 20; i++) {
            startWriting(i);
            int[] a = new int[100000];
            inputWriter.println("100000");
            int z = 0;
            for (int j = 0; j < 200000; j++) {
                boolean b = nextPositive(100) < 100 && z < 100000;
                if (b) {
                    a[z++] = j;
                }
                inputWriter.print(b ? '1' : '0');
            }
            inputWriter.println();
            outputWriter.println(solve(a));
            endWriting();
        }

    }

    private void generate(int n) {
        inputWriter.printf("%d\n", n);
        char[] c = new char[2 * n];
        Arrays.fill(c, '0');
        for (int i = 0; i < n; i++) {
            int t = nextNonNegative(2 * n - 1);
            while (c[t] == '1') {
                t = (t + 1) % (2 * n);
            }
            c[t] = '1';
        }
        int[] a = new int[n];
        int z = 0;
        for (int i = 0; i < 2 * n; i++) {
            if (c[i] == '1') {
                a[z++] = i;
            }
        }
        inputWriter.println(new String(c));
        outputWriter.println(solve(a));
        endWriting();
    }

    public int test() {

        int n = in.nextInt();
        int[] a = new int[n];
        String s = in.nextToken();
        int z = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '1') {
                a[z++] = i;
            }
        }

        return solve(a) == out.nextLong() ? 0 : 1;
    }

    static long solve(int[] a) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            a[i] -= 2 * i;
        }
        Arrays.sort(a);
        long min = 0;
        for (int i = 0; i < n; i++) {
            min += abs(a[i] - a[n / 2]);
        }
        return min;
    }
}
