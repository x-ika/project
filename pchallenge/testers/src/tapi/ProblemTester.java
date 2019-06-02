package tapi;

import com.simplejcode.commons.misc.DataReader;

import java.io.*;
import java.util.*;

public abstract class ProblemTester {

    protected DataReader in;
    protected DataReader out;
    protected DataReader expectedOut;

    protected Random random;
    protected PrintWriter inputWriter;
    protected PrintWriter outputWriter;

    public void run(String[] args) {
        if (args.length == 0) {
            generateTests();
        } else {
            in = new DataReader(args[0]);
            out = new DataReader(args[1]);
            expectedOut = new DataReader(args[2]);
            int exitValue;
            try {
                exitValue = test();
            } catch (Exception e) {
                exitValue = 2;
            }
            in.close();
            out.close();
            expectedOut.close();
            System.exit(exitValue);
        }
    }

    protected void startWriting(int test) {
        random = new Random(test);
        try {
            inputWriter = new PrintWriter(String.format("%s%02d.in", getFileName(), test));
            outputWriter = new PrintWriter(String.format("%s%02d.out", getFileName(), test));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void endWriting() {
        inputWriter.close();
        outputWriter.close();
    }

    protected abstract String getFileName();

    public abstract void generateTests();

    /**
     * @return 0 - OK, 1 - WA, 2 - PE.
     */
    public abstract int test();

    //-----------------------------------------------------------------------------------

    protected int nextInt(int abs) {
        return random.nextInt(2 * abs + 1) - abs;
    }

    protected int nextNonNegative(int max) {
        return random.nextInt(max + 1);
    }

    protected int nextPositive(int max) {
        return random.nextInt(max) + 1;
    }
}
