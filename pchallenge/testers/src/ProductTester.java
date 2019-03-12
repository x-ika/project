import tapi.ProblemTester;

public class ProductTester extends ProblemTester {
    public static void main(String[] args) throws Exception {
        new ProductTester().run(args);
    }

    protected String getFileName() {
        return "product";
    }

    public void generateTests() {
        startWriting(1);
        int n = 1000;
        inputWriter.println(n);
        for (int i = 0; i < n; i++) {
            inputWriter.printf("%d %d\n", nextInt(10000), nextInt(10000));
        }
        endWriting();
    }

    public int test() {
        int n = in.nextInt();
        while (n-- > 0) {
            int x = in.nextInt();
            int y = in.nextInt();
            int z = out.nextInt();
            if (x * y != z) {
                return 1;
            }
        }
        return 0;
    }
}
