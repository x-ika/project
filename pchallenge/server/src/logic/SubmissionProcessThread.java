package logic;

import com.simplejcode.commons.misc.util.*;
import message.Submission;
import model.TestResult;

import java.util.concurrent.*;
import java.io.*;

public class SubmissionProcessThread {

    private final String workingDirectory;

    private BlockingQueue<Submission> queue = new ArrayBlockingQueue<>(1 << 20);

    public SubmissionProcessThread(String workingDirectory) {
        this.workingDirectory = workingDirectory;
        startHandlerThread();
    }

    public void test(Submission submission) {
        queue.offer(submission);
    }

    private void startHandlerThread() {
        ThreadUtils.executeInNewThread(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    Submission submission = queue.take();
                    process(submission);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void process(Submission submission) throws Exception {

        File compiled = compile(submission);
        if (!compiled.exists()) {
            SystemManager.getInstance().testingFinished(submission, TestResult.CE, -1);
            return;
        }
        Problem problem = SystemManager.getInstance().getProblem(submission.getProblem());

        // obtain input files
        File[] inputs = new File(problem.getDataPath()).listFiles((dir, name) -> name.endsWith(".in") || name.endsWith(".txt"));

        File solutionInput = new File(workingDirectory + problem.getFileName() + ".in");

        TestResult result = TestResult.OK;
        int test = 0;
        for (File input : inputs) {
            test++;
            // run solution
            FileSystemUtils.copy(input, solutionInput);
            int exitValue = ProcessUtils.execute(compiled, problem.getTimeLimit());
            // tle, re
            if (exitValue != 0) {
                result = exitValue == ProcessUtils.TIME_LIMIT_ERROR_CODE ? TestResult.TLE : TestResult.RE;
                break;
            }
            // run tester
            exitValue = ProcessUtils.execute(
                    new File(problem.getTesterExec()),
                    problem.getTesterTimeLimit(),
                    f(input.getAbsolutePath()),
                    f(getOutput(solutionInput).getAbsolutePath()),
                    f(getOutput(input).getAbsolutePath()));
            // 0 - ok, 1 - wa, 2 - pe
            if (exitValue != 0) {
                result = exitValue == 1 ? TestResult.WA : TestResult.PE;
                break;
            }
        }

        if (result == TestResult.OK) {
            test = -1;
        }
        SystemManager.getInstance().testingFinished(submission, result, test);
        FileSystemUtils.delete(new File(workingDirectory), true);

    }

    //-----------------------------------------------------------------------------------

    private File compile(Submission submission) throws Exception {
        String name = submission.getFileName();
        String exec = name.substring(0, name.lastIndexOf('.'));
        File source = new File(workingDirectory + name);
        FileSystemUtils.writeBytes(source.getAbsolutePath(), submission.getBuff());
        String compile = null, output = null, format;
        switch (submission.getLanguage()) {
            case CPP_MINGW:
                output = workingDirectory + exec + ".exe";
                format = SystemManager.getInstance().getProperty("compile.cpp_g++");
                compile = String.format(format, output, source.getAbsolutePath());
                break;
            case CPP_VS:
                output = workingDirectory + exec + ".exe";
                format = SystemManager.getInstance().getProperty("compile.cpp_vs");
                compile = String.format(format, output, source.getAbsolutePath());
                break;
            case JAVA:
                output = workingDirectory + exec + ".class";
                format = SystemManager.getInstance().getProperty("compile.java");
                compile = String.format(format, source.getAbsolutePath());
                break;
        }
        String compilationTimeLimit = SystemManager.getInstance().getProperty("compilationtimelimit");
        ProcessUtils.execute(compile, Integer.parseInt(compilationTimeLimit));
        source.delete();
        return new File(output);
    }

    private static File getOutput(File input) {
        return new File(input.getParent() + File.separator + input.getName().replace(".in", ".out"));
    }

    private static String f(String s) {
        return "\"" + s + "\"";
    }

}
