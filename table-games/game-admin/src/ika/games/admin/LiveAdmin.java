package ika.games.admin;

import com.simplejcode.commons.gui.*;
import com.simplejcode.commons.misc.util.ThreadUtils;
import com.simplejcode.commons.net.sockets.*;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class LiveAdmin extends CustomInternalFrame implements ConnectionListener<byte[]> {

    private SocketConnection<byte[]> connection;
    private Console console;

    public LiveAdmin() throws Exception {
        super("Live Admin");

        loadContent(console = new Console());
        console.setFont(Font.MONOSPACED, Font.PLAIN, 14);
    }

    private void start() {
        ThreadUtils.executeInNewThread(() -> {
            while (console != null) {
                sendCommand(console.readLine());
            }
        });
    }

    public void sendLogin(String host, String user, String pass) {
        if (connection == null) {
            try {
                Socket socket = new Socket(host, 4444);
                connection = new FastConnection(socket, 0, 0, '2', '1', '@', 0);
                connection.addConnectionListener(this);
                connection.start();
            } catch (IOException e) {
                return;
            }
        }
        connection.sendMessage(String.format("1,%s,%s", user, pass).getBytes());
        start();
    }

    private void sendCommand(String cmd) {
        connection.sendMessage(String.format("2,%s", cmd).getBytes());
    }

    private void broadcast(String msg) {
        console.setColor(Color.red);
        console.writeLine(msg);
        console.setColor(Color.black);
    }


    public void messageSent(SocketConnection<byte[]> socketConnection, byte[] bytes) throws Exception {
    }

    public void messageReceived(SocketConnection<byte[]> socketConnection, byte[] bytes) throws Exception {
        String msg = new String(bytes);
        if (msg.equals("LOGIN_OK")) {
            start();
        }
        broadcast(">>>> " + msg);
    }

    public void sendingFailed(SocketConnection<byte[]> socketConnection, byte[] bytes, Exception e) throws Exception {
    }

    public void receivingFailed(SocketConnection<byte[]> socketConnection, Exception e) throws Exception {
    }

    public void disconnected(SocketConnection<byte[]> socketConnection) throws Exception {
        broadcast("Connection closed");
    }

}
