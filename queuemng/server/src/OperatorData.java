import java.util.*;

public class OperatorData {

    private List<Integer> tickets;
    private List<Long> startTimes;
    private List<Long> endTimes;

    public OperatorData() {
        tickets = new ArrayList<>();
        startTimes = new ArrayList<>();
        endTimes = new ArrayList<>();
    }

    public synchronized long getLastFinishTime() {
        return endTimes.isEmpty() ? 0 : endTimes.get(endTimes.size() - 1);
    }

    public synchronized boolean isFree() {
        return startTimes.size() == endTimes.size();
    }

    public synchronized int getTicket() {
        return isFree() ? 0 : tickets.get(tickets.size() - 1);
    }

    public synchronized boolean startService(int ticket) {
        if (!isFree()) {
            return false;
        }
        tickets.add(ticket);
        startTimes.add(System.currentTimeMillis());
        return true;
    }

    public synchronized boolean endService() {
        if (isFree()) {
            return false;
        }
        endTimes.add(System.currentTimeMillis());
        return true;
    }

}
