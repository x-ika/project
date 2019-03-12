package message;

import com.simplejcode.commons.net.csbase.Message;

public class ContestInfo extends Message {

    private int nProblems;

    private int duration;

    private String[] problemNames;

    public ContestInfo(Object sender, int nProblems, int duration) {
        super(sender);
        this.nProblems = nProblems;
        this.duration = duration;
    }

    public int getNProblems() {
        return nProblems;
    }

    public void setNProblems(int nProblems) {
        this.nProblems = nProblems;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String[] getProblemNames() {
        return problemNames;
    }

    public void setProblemNames(String[] problemNames) {
        this.problemNames = problemNames;
    }
}
