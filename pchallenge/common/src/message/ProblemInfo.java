package message;

import java.io.File;

public class ProblemInfo extends FileMessage {

    protected int problem;

    protected int timeLimit;

    public ProblemInfo(Object sender) {
        super(sender);
    }

    public ProblemInfo(Object sender, File file) {
        super(sender, file);
    }

    public ProblemInfo(Object sender, File file, int problem, int timeLimit) {
        super(sender, file);
        this.problem = problem;
        this.timeLimit = timeLimit;
    }

    public int getProblem() {
        return problem;
    }

    public void setProblem(int problem) {
        this.problem = problem;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }
}
