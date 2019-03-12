package spamdetector;

import tester.*;

import java.util.*;

import spamdetector.detector.Classifier;
import spamdetector.detector.TestCase;
import spamdetector.detector.IkasDetector;
import spamdetector.detector.students.assign3.SalomesSolver;
import spamdetector.detector.students.dato.DatosSolver;
import spamdetector.detector.students.eakhv.ElenesSolver;
import spamdetector.detector.students.eormo.EvgoDetector;
import spamdetector.detector.students.gigi.GigiSolver;
import spamdetector.detector.students.Giglema.GiglemaSolver;
import spamdetector.detector.students.gkoch.gkochSpamDetector;
import spamdetector.detector.students.gmati.GiosDetector;
import spamdetector.detector.students.kurid.KuridDetector;
import spamdetector.detector.students.lgure.LashasDetector;
import spamdetector.detector.students.mkapa.MyDetector;
import spamdetector.detector.students.sggol.SabasDetector;
import spamdetector.detector.students.tamuna.tamunasSolver;
import spamdetector.detector.students.vmask11.VatoDetector;
import spamdetector.detector.students.zgven.ZurasDetector;
import spamdetector.detector.students.adoli11.SandroDetector;

public class DetectorChecker extends AbstractTester<TestCase, Classifier> {

    public DetectorChecker() {
        super(new DChecker());
    }

    private static class DChecker implements Checker<TestCase, Classifier> {
        public boolean isCorrect(TestCase test, Classifier result) {

            int n = test.getUsefulEmails().length;
            int m = test.getSpamEmails().length;
            double[] w = new double[n + m];
            Arrays.fill(w, 1d / (n + m));

            String[] rules = test.getWeakClassifiers().clone();
            boolean[] used = new boolean[rules.length];
            for (int i = 0; i < result.getRuleIds().length; i++) {
                int id = result.getRuleIds()[i];
                if (used[id]) {
                    return false;
                }
                double err = score(test, rules[id], w);
                if (err >= 0.5) {
                    return false;
                }
                used[id] = true;
                for (int j = 0; j < rules.length; j++) {
                    if (!used[j] && err > score(test, rules[j], w) + 1e-9) {
                        return false;
                    }
                }

                if (i >= result.getCoefs().length) {
                    return false;
                }
                double a = 0.5 * Math.log((1 - err) / err);
                if (Math.abs(a - result.getCoefs()[i]) > 1e-9) {
                    return false;

                }
                update(test, rules[id], w, err);
            }
//            System.out.println(Arrays.toString(test.getUsefulEmails()));
//            System.out.println(Arrays.toString(test.getSpamEmails()));
//            System.out.println(Arrays.toString(test.getWeakClassifiers()));
//
//            for (int i = 0; i < rules.length; i++) {
//                System.out.printf("%.3f ", score(test, rules[i], null));
//            }
//            System.out.println("\n\n");
//
//            System.out.println(Arrays.toString(result.getRuleIds()));
//            System.out.println(Arrays.toString(result.getCoefs()));
//            System.out.println(score(test, result));
//
//            System.out.println("-------------------\n");


            return true;
        }

        public TestCase clone(TestCase testCase) {
            TestCase data = new TestCase();
            data.setUsefulEmails(testCase.getUsefulEmails().clone());
            data.setSpamEmails(testCase.getSpamEmails().clone());
            data.setWeakClassifiers(testCase.getWeakClassifiers().clone());
            return data;
        }
    }

    private static double score(TestCase test, Classifier result) {
        double err = 0;
        int n = test.getUsefulEmails().length;
        int m = test.getSpamEmails().length;
        for (int i = 0; i < n + m; i++) {
            String text = i < n ? test.getUsefulEmails()[i] : test.getSpamEmails()[i - n];
            double s = 0;
            for (int j = 0; j < result.getCoefs().length; j++) {
                String rule = test.getWeakClassifiers()[result.getRuleIds()[j]];
                s += (apply(text, rule) ? 1 : -1) * result.getCoefs()[j];
            }
            if (i < n ^ s < 0) {
                err += 1d / (n + m);
            }
        }
        return err;
    }

    private static double score(TestCase test, String rule, double[] w) {
        double err = 0;
        int n = test.getUsefulEmails().length;
        int m = test.getSpamEmails().length;
        for (int i = 0; i < n + m; i++) {
            String text = i < n ? test.getUsefulEmails()[i] : test.getSpamEmails()[i - n];
            if (i < n ^ !apply(text, rule)) {
                err += w == null ? 1d / (n + m) : w[i];
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

    public void visualize(TestCase data) {
    }


    private static TestCase generate(int seed) {

        Random rnd = new Random(seed);

        int n = seed <= 50 ? 20 : 100;
        int good = 90, bad = 10, tot = good + bad;
        String[] dict = new String[tot];
        for (int i = 0; i < tot; i++) {
            dict[i] = "" + i;
        }

        String[] texts = new String[2 * n];
        Arrays.fill(texts, " ");
        double p1 = 0.2 + 0.2 * rnd.nextDouble(), q1 = 0.2;
        double p2 = 0.4 + 0.4 * rnd.nextDouble(), q2 = q1 - (p2 - p1) * bad / good;
        if (seed > 20) {
            p1 = 0.4;
            q1 = 0.2;
            p2 = 0.3;
            q2 = q1 - (p2 - p1) * bad / good;
        }
        for (int i = 0; i < 2 * n; i++) {

            double p = i < n ? p1 : p2;
            double q = i < n ? q1 : q2;
            for (int j = 0; j < tot; j++) {
                double t = j < good ? q : p;
                if (rnd.nextDouble() < t) {
                    texts[i] += dict[j] + " ";
                }
            }

        }

        List<String> classifiers = new ArrayList<>();
        for (int i = good; i < tot; i++) {
            classifiers.add(dict[i]);
            for (int j = good; j < i && seed <= 50; j++) {
                classifiers.add(dict[i] + "|" + dict[j]);
                classifiers.add(dict[i] + "&" + dict[j]);
            }
        }

        TestCase test = new TestCase();
        test.setUsefulEmails(Arrays.copyOfRange(texts, 0, n));
        test.setSpamEmails(Arrays.copyOfRange(texts, n, 2 * n));
        test.setWeakClassifiers(classifiers.toArray(new String[0]));
        return test;
    }

    public static void main(String[] args) throws Exception {

        Class[] classes = {
                IkasDetector.class,
                SandroDetector.class,
                SalomesSolver.class,
                DatosSolver.class,
                ElenesSolver.class,
                EvgoDetector.class,
                GigiSolver.class,
                GiglemaSolver.class,
                gkochSpamDetector.class,
                GiosDetector.class,
                KuridDetector.class,
                LashasDetector.class,
                MyDetector.class,
                SabasDetector.class,
                tamunasSolver.class,
                VatoDetector.class,
                ZurasDetector.class,
        };

        List<TestCase> tests = new ArrayList<>();
        for (int t = 1; t <= 100; t++) {
            tests.add(generate(t));
        }

        long startTime = System.nanoTime();
        new DetectorChecker().test(classes, tests, 100000);
        System.out.println("Time: " + (System.nanoTime() - startTime) / 1e9);

    }

}
