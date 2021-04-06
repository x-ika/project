import tapi.ProblemTester;

import java.util.Arrays;

public class GapsOnLineTester extends ProblemTester {
    public static void main(String[] args) throws Exception {
        new GapsOnLineTester().run(args);
    }

    protected String getFileName() {
        return "gaps";
    }

    public void generateTests() {
        for (int i = 1; i <= 15; i++) {
            int n, k, gap;
            if (i < 6) {
                n = 1000;
                k = 5;
                gap = 100;
            } else if (i < 11) {
                n = 1000000;
                k = 10;
                gap = 10000;
            } else {
                n = 1000000;
                k = 10;
                gap = 1000000 / 2;
            }


            startWriting(i);

            inputWriter.printf("%d %d\n", n, k);
            while (k-- > 0) {
                inputWriter.println(nextPositive(gap));
            }

            endWriting();
        }

        for (int i = 16; i <= 20; i++) {
            startWriting(i);
            inputWriter.printf("1000000 1\n%d", i - 15);
            endWriting();
        }
        startWriting(21);
        inputWriter.println("999 5");
        inputWriter.println("1\n28\n36\n72\n90");
        endWriting();
//        for (int i = 0; i < 21; i++) {
//            in = new DataReader("input" + (i+1) + ".txt");
//            test();
//        }

    }

    public int test() {

        int n = in.nextInt();
        int k = in.nextInt();
        int[] a = new int[k];
        for (int i = 0; i < k; i++) {
            a[i] = in.nextInt();
        }

        int[] q = new int[n + 1];
        int[] d = new int[n + 1];
        Arrays.fill(d, n + 1);
        boolean[] b = new boolean[n + 1];
        d[0] = 0;
        b[0] = true;

        for (int r = 0, w = 1; r < w; ) {
            int x = q[r++];
            for (int i : a) {

                if (x + i <= n && !b[x + i]) {
                    d[x + i] = Math.min(d[x + i], d[x] + 1);
                    b[x + i] = true;
                    q[w++] = x + i;
                }
            }
        }
        d[n] = d[n] <= n ? d[n] : -1;

        return d[n] == out.nextInt() ? 0 : 1;
//        System.out.println(d[n]);
//        return 0;
    }
}
