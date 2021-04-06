package message;

import com.simplejcode.commons.net.csbase.Message;
import model.RequestType;

public class Request extends Message {

    protected RequestType requestType;

    public Request(Object sender) {
        super(sender);
    }

    public Request(Object sender, RequestType requestType) {
        super(sender);
        this.requestType = requestType;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}
