package tester;

import java.util.*;

public abstract class AbstractTester<T, R> {

    protected R ret;
    protected long time;
    protected final Checker<T, R> checker;

    public AbstractTester(Checker<T, R> checker) {
        this.checker = checker;
    }

    public int[] test(Class<? extends Solver<T, R>> solver, Collection<T> tests, int wait) throws Exception {
        int correct = 0, tl = 0, exc = 0, mistake = 0;
        for (T test : tests) {
            long time = test(solver.getConstructor().newInstance(), checker.clone(test), wait, false);
            if (time >= wait) {
                tl++;
            } else if (time == -1) {
                exc++;
            } else if (time == -2) {
                mistake++;
            } else {
                correct++;
            }
        }
        return new int[]{correct, tl, exc, mistake};
    }

    public synchronized long test(final Solver<T, R> solver,
                                  final T test,
                                  int wait, boolean vis) throws Exception {

        Thread t = new Thread() {
            public void run() {
                try {
                    long startTime = System.nanoTime();
                    ret = solver.solve(checker.clone(test));
                    time = System.nanoTime() - startTime;
                } catch (Exception e) {
                    time = -1;
                }
            }
        };
        ret = null;
        t.start();
        t.join(wait);
        t.stop();
        if (time == -1) {
            return -1;
        }
        if (ret == null) {
            return wait;
        }
        if (!checker.isCorrect(test, ret)) {
            return -2;
        }
        if (vis) {
            visualize(checker.clone(test));
        }
        return time / 1000000;

    }

    public abstract void visualize(T t);

    public void test(Class<? extends Solver<T, R>>[] classes, Collection<T> tests, int wait) throws Exception {

        for (Class<? extends Solver<T, R>> c : classes) {
            long startTime = System.nanoTime();
            int[] p = test(c, tests, wait);
            double t = (System.nanoTime() - startTime) / 1e9;
            System.out.printf("%-60s %4d %4d %4d %4d %9.3f\r\n", c.toString(), p[0], p[1], p[2], p[3], t);
        }

    }

}
