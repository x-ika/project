package spamdetector.detector;

public class TestCase {

    private String[] usefulEmails;
    private String[] spamEmails;
    private String[] weakClassifiers;

    public String[] getUsefulEmails() {
        return usefulEmails;
    }

    public void setUsefulEmails(String[] usefulEmails) {
        this.usefulEmails = usefulEmails;
    }

    public String[] getSpamEmails() {
        return spamEmails;
    }

    public void setSpamEmails(String[] spamEmails) {
        this.spamEmails = spamEmails;
    }

    public String[] getWeakClassifiers() {
        return weakClassifiers;
    }

    public void setWeakClassifiers(String[] weakClassifiers) {
        this.weakClassifiers = weakClassifiers;
    }

}
