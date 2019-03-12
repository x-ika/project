package proc;

public class DayRecord {

    public String name;
    public long day;
    public long inTime;
    public long outTime;

    public String dayStr;
    public String inTimeStr;
    public String outTimeStr;

    public String preWorkTime;
    public String postWorkTime;

    public String preDelay;
    public String postDelay;

    public String worked;

    public DayRecord(String name, long day, long inTime, long outTime) {
        this.name = name;
        this.day = day;
        this.inTime = inTime;
        this.outTime = outTime;
    }

}
