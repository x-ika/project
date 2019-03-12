import tapi.ProblemTester;

import static java.lang.Math.*;

public class PointsInRectTester extends ProblemTester {
    protected String getFileName() {
        return "points";
    }

    public void generateTests() {

        for (int i = 1; i <= 10; i++) {

            int max, n, q;
            if (i < 6) {
                max = 10;
                n = 50;
                q = 1000;
            } else {
                max = 100000;
                n = 100000;
                q = 100000;
            }

            startWriting(i);

            inputWriter.println(n);
            while (n-- > 0) {
                inputWriter.printf("%d %d\n", nextInt(max), nextInt(max));
            }

            inputWriter.println(q);
            while (q-- > 0) {
                int x1 = nextInt(max);
                int y1 = nextInt(max);
                int x2 = nextInt(max);
                int y2 = nextInt(max);
                inputWriter.printf("%d %d %d %d\n", min(x1, x2), min(y1, y2), max(x1, x2), max(y1, y2));
            }

            endWriting();
        }

    }

    public int test() {
        // adding on segment!!!
        return 0;
    }
}
