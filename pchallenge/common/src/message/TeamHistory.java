package message;

import com.simplejcode.commons.net.csbase.Message;
import model.TestResult;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.Serializable;

public class TeamHistory extends Message {

    public static class Attempt implements Serializable, Comparable<Attempt> {

        // in seconds from start of the contest
        public final int time;

        public final int problem;

        public final TestResult result;

        public final int failedTest;

        public Attempt(int time, int problem, TestResult result, int failedTest) {
            this.time = time;
            this.problem = problem;
            this.result = result;
            this.failedTest = failedTest;
        }

        public int compareTo(Attempt o) {
            return time - o.time;
        }
    }

    private String teamName;

    private List<Attempt> attempts;

    public TeamHistory(Object sender) {
        this(sender, null);
    }

    public TeamHistory(Object sender, String teamName) {
        super(sender);
        setTeamName(teamName);
        attempts = new ArrayList<>();
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public List<Attempt> getAttempts() {
        return attempts;
    }

    public void addAttempt(int time, int problem, TestResult result, int failedTest) {
        attempts.add(new Attempt(time, problem, result, failedTest));
    }

    //-----------------------------------------------------------------------------------

    private transient int solvedSet;
    private transient int totalTime;
    private transient int[] nAttempts;
    private transient int[] solveTime;

    public void summarize() {

        Collections.sort(attempts);

        totalTime = 0;
        nAttempts = new int[32];
        solveTime = new int[32];
        for (Attempt attempt : attempts) {
            if (solved(attempt.problem)) {
                continue;
            }
            if (attempt.result == TestResult.OK) {
                solvedSet |= 1 << attempt.problem;
                solveTime[attempt.problem] = attempt.time;
                totalTime += TimeUnit.MINUTES.toSeconds(20 * nAttempts[attempt.problem]) + attempt.time;
            } else {
                nAttempts[attempt.problem]++;
            }
        }

    }

    public int getSolved() {
        return Integer.bitCount(solvedSet);
    }

    public int getTotalTime() {
        return (int) TimeUnit.SECONDS.toMinutes(totalTime);
    }

    public int getResult(int problem) {
        return nAttempts[problem];
    }

    public int getSolveTime(int problem) {
        return solveTime[problem];
    }

    public boolean solved(int problem) {
        return (solvedSet & 1 << problem) != 0;
    }

    public int hashCode() {
        return teamName.hashCode();
    }

    public boolean equals(Object obj) {
        return obj instanceof TeamHistory && teamName.equals(((TeamHistory) obj).teamName);
    }
}
