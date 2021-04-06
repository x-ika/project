package logic;

import message.*;
import model.TestResult;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Contest implements Serializable {

    private ContestInfo info;

    private Problem[] problems;

    private long startTime;

    private Map<String, TeamHistory> histories = new HashMap<>();

    public void load(String fileName) {
        try {

            Properties properties = new Properties();
            properties.load(new FileReader(fileName));

            int nProblems = Integer.parseInt(properties.getProperty("problems"));
            int duration = (int) TimeUnit.MINUTES.toSeconds(Integer.parseInt(properties.getProperty("duration")));
            info = new ContestInfo(this, nProblems, duration);
            problems = new Problem[nProblems];

            String[] problemNames = new String[nProblems];
            for (int i = 0; i < nProblems; i++) {
                problems[i] = new Problem(properties.getProperty("problem" + (i + 1)));
                problemNames[i] = String.valueOf((char) ('A' + i)) + " - " + problems[i].getName();
            }
            info.setProblemNames(problemNames);
            startTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ContestInfo getInfo() {
        return info;
    }

    public Problem getProblem(int problem) {
        return problems[problem];
    }

    public long getEndTime() {
        return startTime + info.getDuration();
    }

    public Map<String, TeamHistory> getHistories() {
        return histories;
    }


    public TeamHistory addAttemp(Submission submission, TestResult result, int test) {
        TeamHistory history = histories.get(submission.getSenderTeam());
        int passed = (int) (submission.getSendTime() - startTime);
        history.addAttempt(passed, submission.getProblem(), result, test);
        return history;
    }

    public boolean started() {
        return startTime != 0;
    }

}
