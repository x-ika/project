package spamdetector.detector;

import tester.Solver;

import java.util.Arrays;

public class IkasDetector implements Solver<TestCase, Classifier> {
    public Classifier solve(TestCase test) {
        int n = test.getUsefulEmails().length;
        int m = test.getSpamEmails().length;
        double[] w = new double[n + m];
        Arrays.fill(w, 1d / (n + m));

        String[] rules = test.getWeakClassifiers().clone();
        int k = test.getWeakClassifiers().length;
        double[] coefs = new double[k];
        int[] ids = new int[k];
        boolean[] used = new boolean[k];
        for (int iter = 0; iter < k; iter++) {
            double err = 0.5, cur;
            int id = 0;
            for (int i = 0; i < k; i++) {
                if (!used[i] && err > (cur = score(test, rules[i], w))) {
                    err = cur;
                    id = i;
                }
            }
            if (err >= 0.5) {
                ids = Arrays.copyOf(ids, iter);
                coefs = Arrays.copyOf(coefs, iter);
                break;
            }
            used[id] = true;
            double a = 0.5 * Math.log((1 - err) / err);
            update(test, rules[id], w, err);

            ids[iter] = id;
            coefs[iter] = a;
        }
        Classifier result = new Classifier();
        result.setRuleIds(ids);
        result.setCoefs(coefs);
        return result;
    }

    private static double score(TestCase test, String rule, double[] w) {
        double err = 0;
        int n = test.getUsefulEmails().length;
        int m = test.getSpamEmails().length;
        for (int i = 0; i < n + m; i++) {
            String text = i < n ? test.getUsefulEmails()[i] : test.getSpamEmails()[i - n];
            if (i < n ^ !apply(text, rule)) {
                err += w[i];
            }
        }
        return err;
    }

    private static void update(TestCase test, String rule, double[] w, double e) {
        int n = test.getUsefulEmails().length;
        int m = test.getSpamEmails().length;
        for (int i = 0; i < n + m; i++) {
            String text = i < n ? test.getUsefulEmails()[i] : test.getSpamEmails()[i - n];
            w[i] /= i < n ^ !apply(text, rule) ? 2 * e : 2 * (1 - e);
        }
    }

    private static boolean apply(String text, String rule) {
        int ind = rule.indexOf('&');
        if (ind != -1) {
            return apply(text, rule.substring(0, ind)) && apply(text, rule.substring(ind + 1));
        }
        return text.matches(".* (" + rule + ") .*");
    }
}
