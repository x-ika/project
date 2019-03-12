package floodit;

import java.util.*;
import java.io.*;

public final class TestGenerator {
    private static final Random rnd = new Random(1);

    public static int[][] generateTest() {
        return generateTest(rnd.nextInt(10) + 15, rnd.nextInt(10) + 15, rnd.nextInt(2) + 5);
    }

    public static int[][] generateTest(int n, int m, int c) {
        int[][] t = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                t[i][j] = rnd.nextInt(c);
            }
        }
        return t;
    }

    public static int[][] readTest(int ind) throws Exception {
        File[] all = new File("resources/").listFiles();
        Scanner in = new Scanner(all[ind]);
        int n = in.nextInt();
        in.next();
        int[][] t = new int[n][n];
        for (int i = 0; i < n; i++) {
            char[] c = in.next().toCharArray();
            for (int j = 0; j < n; j++) {
                t[i][j] = c[j] - 'A';
            }
        }
        return t;
    }

}
