package message;

import model.Language;

import java.io.File;

public class Submission extends FileMessage {

    protected String senderTeam;

    protected int problem;

    protected Language language;

    public Submission(Object sender) {
        super(sender);
    }

    public Submission(Object sender, File file) {
        super(sender, file);
    }

    public int getProblem() {
        return problem;
    }

    public void setProblem(int problem) {
        this.problem = problem;
    }

    public String getSenderTeam() {
        return senderTeam;
    }

    public void setSenderTeam(String senderTeam) {
        this.senderTeam = senderTeam;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }
}
