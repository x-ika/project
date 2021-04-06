import static java.lang.Math.*;

import java.util.*;

public class BW {
    public static void main(String[] args) {

        // test all 10-stone cases
        int n = 10;
        int[] a = new int[n];
        for (int i = 0; i < 1 << 2 * n; i++) {
            if (Integer.bitCount(i) != n) {
                continue;
            }
            int k = i;
            for (int j = 0; j < n; j++) {
                a[j] = (j == 0 ? -1 : a[j - 1]) + Integer.numberOfTrailingZeros(k) + 1;
                k >>= Integer.numberOfTrailingZeros(k) + 1;
            }
            check(a);
        }

        // random 100-stone tests
        n = 100;
        a = new int[n];
        Random r = new Random(0);
        for (int iter = 0; iter < 10000; iter++) {
            for (int i = 0; i < n; i++) {
                a[i] = r.nextInt(2 * n);
            }
            Arrays.sort(a);
            for (int i = 1; i < n; i++) {
                if (a[i] <= a[i - 1]) {
                    a[i] = a[i - 1] + 1;
                }
            }
            if (a[n - 1] >= 2 * n) {
                continue;
            }
            check(a);
        }

        System.out.println("OK");

    }

    private static void check(int[] a) {
        if (solve1(a) != solve2(a)) {
            System.out.println(solve1(a));
            System.out.println(solve2(a));
            System.out.println(Arrays.toString(a));
            System.exit(0);
        }
    }

    static int solve1(int[] a) {
        int n = a.length;

        int min = n * n;
        for (int i = 0; i < 2 * n; i++) {

            int cur = get(abs(a[0] - i), n);
            for (int j = 1; j < n; j++) {
                cur += get(abs(a[j] - (i + 2 * j) % (2 * n)), n);
            }
            min = min(min, cur);

        }

        return min;

    }

    static int solve2(int[] a) {
        int n = a.length;
        for (int i = 0; i < n; i++) {
            a[i] -= 2 * i;
        }
        Arrays.sort(a);
        int min = 0;
        for (int i = 0; i < n; i++) {
            min += abs(a[i] - a[n / 2]);
        }
        return min;
    }

    static int get(int d, int n) {
        return min(d, 2 * n - d);
    }
}
