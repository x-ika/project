package gui;

import com.simplejcode.commons.gui.CustomInternalFrame;
import com.simplejcode.commons.net.csbase.Message;
import com.simplejcode.commons.net.sockets.*;

import java.awt.*;

public class BaseListener extends CustomInternalFrame implements ConnectionListener<Message> {

    protected SocketConnection<Message> socketConnection;

    public BaseListener(SocketConnection<Message> socketConnection, String title) {
        this(socketConnection, title, null);
    }

    public BaseListener(SocketConnection<Message> socketConnection, String title, Container contentPane) {
        super(title, contentPane);
        setSocketConnection(socketConnection);
    }

    public void setSocketConnection(SocketConnection<Message> socketConnection) {
        if (socketConnection == null) {
            throw new NullPointerException("The connection should not be null");
        }
        if (this.socketConnection != null) {
            this.socketConnection.removeConnectionListener(this);
        }
        this.socketConnection = socketConnection;
        socketConnection.addConnectionListener(this);
    }

    //-----------------------------------------------------------------------------------

    public void disconnected(SocketConnection<Message> source) throws Exception {
    }

    public void sendingFailed(SocketConnection<Message> source, Message message, Exception e) throws Exception {
    }

    public void receivingFailed(SocketConnection<Message> source, Exception e) throws Exception {
    }

    public void messageSent(SocketConnection<Message> source, Message message) throws Exception {
    }

    public void messageReceived(SocketConnection<Message> source, Message message) throws Exception {
    }

}
