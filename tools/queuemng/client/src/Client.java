import com.simplejcode.commons.gui.*;
import com.simplejcode.commons.misc.ReflectionUtils;
import com.simplejcode.commons.net.csbase.*;
import com.simplejcode.commons.net.sockets.*;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.net.Socket;
import java.io.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class Client extends ConnectionAdapter {

    public static Client client;

    private SocketConnection socketConnection;

    private LoginForm loginForm;
    private AboutDialogPanel aboutDialogPanel;
    private MainPanel mainPanel;
    private CustomFrame frame;

    private String host;
    private String userName;
    private String password;

    public Client() {
        showLoginForm();
    }

    //-----------------------------------------------------------------------------------

    public synchronized void showLoginForm() {
        loginForm = new LoginForm(
                GraphicUtils.createAction(
                        "OK",
                        this,
                        ReflectionUtils.getMethod("login")),

                GraphicUtils.createAction(
                        "Exit",
                        this,
                        ReflectionUtils.getMethod("actionOnExit")));
        try {
            Properties properties = new Properties();
            properties.load(getResource("resources/client.properties"));
            loginForm.setServerAddress(properties.getProperty("connection.host"));
        } catch (IOException e) {
            loginForm.setServerAddress("127.0.0.1");
        }
        loginForm.setLogin("ika");
        loginForm.setPassword("1");
    }

    public synchronized void createMainFrame() {
        aboutDialogPanel = new AboutDialogPanel("QMNG", getImage("resources/logo.jpeg"));
        aboutDialogPanel.init();
        frame = new CustomFrame("Queue Manager");

        frame.setSize(900, 600);

        frame.setJMenuBar(GraphicUtils.createMenuBar(
                new String[][]{
                        {"File", "Reconnect", null, "Exit"},
//                        {"Open", "Chat Window", "Standings", "Submit Form", "Submission History", "Problem Statements"},
                        {"Help", "About"}
                },
                this,
                ReflectionUtils.getMethod("handle", Throwable.class),

                ReflectionUtils.getMethod("actionOnReconnect"),
                ReflectionUtils.getMethod("actionOnExit"),

//                ReflectionUtils.getMethod("loadChatWindow"),
//                ReflectionUtils.getMethod("loadStandings"),
//                ReflectionUtils.getMethod("loadSubmitForm"),
//                ReflectionUtils.getMethod("loadSubmissionHistory"),
//                ReflectionUtils.getMethod("loadProblemStatementViewer"),

                ReflectionUtils.getMethod("actionOnAbout"))
        );

        frame.setContentPane(mainPanel = new MainPanel(GraphicUtils.createAction("Finish", this, ReflectionUtils.getMethod("actionOnFinish"))));

        frame.setSize(220, 220);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    }

    //-----------------------------------------------------------------------------------

    public void login() {
        host = loginForm.getServerAddress();
        userName = loginForm.getLogin();
        password = loginForm.getPassword();
        requestLogin();
    }

    public void loginSuccessful() {
        if (frame != null) {
            frame.removeAll();
            frame.dispose();
        }
        loginForm.dispose();
        createMainFrame();
    }

    public void actionOnReconnect() {
        socketConnection.closeConnection();
        requestLogin();
    }

    public void actionOnFinish() {
        MapMessage record = new MapMessage(this);
        record.put("type", "finish");
        socketConnection.sendMessage(record);
    }

    public void actionOnExit() {
        if (socketConnection != null) {
            socketConnection.closeConnection();
        }
        System.exit(0);
    }

    public void actionOnAbout() {
        aboutDialogPanel.showDialog(frame);
    }

    public void handle(Throwable e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(frame, "Error occurred when processing your command!", "Error", JOptionPane.ERROR_MESSAGE);
    }

    //-----------------------------------------------------------------------------------

    private void requestLogin() {
        if (socketConnection != null) {
            socketConnection.removeConnectionListener(this);
            socketConnection.closeConnection();
        }
        try {
            Properties properties = new Properties();
            properties.load(getResource("resources/client.properties"));
            Socket socket = new Socket(host, Integer.parseInt(properties.getProperty("connection.port")));
            socketConnection = new NetMessanger(socket, 0, 5000);
            socketConnection.addConnectionListener(this);
        } catch (IOException e) {
            System.out.println("Can not connect to the server.");
        }
        if (socketConnection != null) {
            socketConnection.sendMessage(createLoginRequest());
        }
    }

    private MapMessage createLoginRequest() {
        MapMessage record = new MapMessage(this);
        record.put("type", "login");
        record.put("user", userName);
        record.put("pass", password);
        return record;
    }

    //-----------------------------------------------------------------------------------

    public void sendMessage(Message message) {
        socketConnection.sendMessage(message);
    }

    //-----------------------------------------------------------------------------------

    public void messageSent(SocketConnection source, Message message) {
    }

    public void messageReceived(SocketConnection source, Message message) {
        final MapMessage record = (MapMessage) message;
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    handle(record);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handle(MapMessage record) {
        if (record.get("type").equals("login") && record.get("user").equals(userName)) {
            loginSuccessful();
        }
        if (record.get("type").equals("assign")) {
            mainPanel.setTicket(record.getInt("ticket"));
            mainPanel.repaint();
        }
    }

    //-----------------------------------------------------------------------------------

    public static BufferedImage getImage(String path) {
        try {
            return ImageIO.read(getResource(path));
        } catch (IOException e) {
            return null;
        }
    }

    public static InputStream getResource(String path) {
        try {
            InputStream stream = Client.class.getClassLoader().getResourceAsStream(path);
            return stream != null ? stream : new FileInputStream(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        client = new Client();
    }

}
