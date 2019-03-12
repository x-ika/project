package spamdetector.detector;

public class Classifier {

    private int[] ruleIds;
    private double[] coefs;

    public int[] getRuleIds() {
        return ruleIds;
    }

    public void setRuleIds(int[] ruleIds) {
        this.ruleIds = ruleIds;
    }

    public double[] getCoefs() {
        return coefs;
    }

    public void setCoefs(double[] coefs) {
        this.coefs = coefs;
    }

}
