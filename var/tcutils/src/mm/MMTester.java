package mm;

import com.simplejcode.commons.algo.struct.DynamicMap;

import java.lang.reflect.Method;
import java.util.ArrayList;

public final class MMTester {

    private MMTester() {
    }

    public static final int ABSOLUTE_SCORING = 0;
    public static final int RELATIVE_KEEP_MAX = 1;
    public static final int RELATIVE_KEEP_MIN = 2;

    public static void test(Method test, String file, int type,
                            String[] args, int s, Class... classes) throws Exception
    {
        ArrayList<Long> seeds = new ArrayList<>();
        if (args[s].equals("a")) {
            for (long i = Long.parseLong(args[s + 1]); i < Long.parseLong(args[s + 2]); i++) {
                seeds.add(i);
            }
        } else {
            for (int i = s; i < args.length; i++) {
                seeds.add(Long.parseLong(args[i]));
            }
        }
        int n = classes.length;
        double[] sum = new double[n + 1];
        double[] cur = new double[n + 1];
        DynamicMap<Long> map = new DynamicMap<>(type == RELATIVE_KEEP_MIN, file);
        for (long seed : seeds) {
            cur[n] = map.get(seed);
            for (int i = 0; i < n; i++) {
                map.update(seed, cur[i] = (Double) test.invoke(null, seed, classes[i]));
            }
            double best = map.get(seed);
            System.out.println("Seed: " + seed);
            for (int i = 0; i <= n; i++) {
                sum[i] += cur[i] = calc(cur[i], best, type);
            }
            System.out.println(getScoreString(cur));
            System.out.println();
        }
        map.close();
        System.out.println(getScoreString(sum));
    }

    public static double calc(double score, double best, int type) {
        return type == ABSOLUTE_SCORING ? score :
                type == RELATIVE_KEEP_MAX ?
                        best == 0 ? 1 : score / best :
                        score == 0 ? 1 : best / score;
    }

    public static String getScoreString(double[] score) {
        StringBuilder sb = new StringBuilder(24 * score.length + 25);
        sb.append("                        ");
        for (int i = 0; i < score.length; i++) {
            sb.append(getScoreString(score[i]));
        }
        return sb.toString();
    }

    public static String getScoreString(double score) {
        String s = String.valueOf(score);
        return s + "                        ".substring(s.length());
    }

}
