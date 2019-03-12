package message;

import com.simplejcode.commons.net.csbase.Message;

public class ContestStatus extends Message {

    private boolean started;

    public ContestStatus(Object sender, boolean started) {
        super(sender);
        this.started = started;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
}
  