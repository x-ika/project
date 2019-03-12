import tapi.ProblemTester;

public class PointsInTriangle extends ProblemTester {
    public static void main(String[] args) {
        new PointsInTriangle().run(args);
    }

    protected String getFileName() {
        return "triangle";
    }

    public void generateTests() {

        for (int i = 1; i <= 10; i++) {
            int max = i < 6 ? 100 : (int) 1e9;
            startWriting(i);
            inputWriter.printf("%d %d\n", nextPositive(max), nextPositive(max));
            endWriting();
        }
        for (int i = 11; i <= 15; i++) {
            int d = nextPositive((int) 1e6);
            int k = nextPositive((int) 9e8 / d);
            startWriting(i);
            inputWriter.printf("%d %d\n", d * k, d * k + k);
            endWriting();
        }
//        for (int i = 0; i < 15; i++) {
//            in = new DataReader("input" + (i+1) + ".txt");
//            test();
//        }

    }

    public int test() {

        long x = in.nextInt();
        long y = in.nextInt();

        long n = (x - 1) * (y - 1) - (gcd(x, y) - 1);
        n /= 2;

        return out.nextLong() == n ? 0 : 1;
//        System.out.println(n);
//        return 0;
    }

    private static long gcd(long a, long b) {
        return a == 0 ? b : gcd(b % a, a);
    }
}
