package ika.games.base.client;

import com.simplejcode.commons.gui.Console;
import com.simplejcode.commons.gui.*;
import com.simplejcode.commons.misc.util.*;
import com.simplejcode.commons.net.sockets.*;
import com.simplejcode.commons.net.util.ByteMessageBuilder;
import ika.games.base.BasicGameAction;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.*;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.*;

public abstract class GameClient extends ConnectionAdapter<byte[]> {

    public static GameClient client;

    //-----------------------------------------------------------------------------------

    private final int gameId;
    private final String name;

    private String host;
    private String userName;
    private String password;

    private SocketConnection<byte[]> socketConnection;

    protected LoginForm loginForm;
    protected DesktopFrame frame;
    protected AboutDialogPanel aboutDialogPanel;
    protected ChatPanel chatPanel;
    protected Map<String, Object> openedTables;

    public GameClient(int gameId, String name) {
        this.gameId = gameId;
        this.name = name;
        quickAndDirtyFixForProblemWithWebStartInJava7u25();
        showLoginForm();
        openedTables = new HashMap<>();
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
        loginForm.setServerAddress("46.49.1.162");
        loginForm.setServerAddress("127.0.0.1");
        loginForm.setLogin("ika");
        loginForm.setPassword("x");
        // todo
        login();
    }

    public synchronized void createMainFrame() {
        aboutDialogPanel = new AboutDialogPanel(name, getImage("resources/logo.jpeg"));
        aboutDialogPanel.init();
        frame = new DesktopFrame(name, getImage("resources/background.jpg"), getImage("resources/foreground.jpg"));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.setSize(1024, 768);

        frame.setJMenuBar(GraphicUtils.createMenuBar(
                new String[][]{
                        {"File", "Reconnect", null, "Exit"},
                        {"Help", "About"}},
                this,
                null,
                ReflectionUtils.getMethod("actionOnReconnect"),
                ReflectionUtils.getMethod("actionOnExit"),
                ReflectionUtils.getMethod("actionOnAbout")));

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

    }

    public synchronized void loadChatWindow() {
        CustomInternalFrame chatWindow = new CustomInternalFrame("Chat Window");

        chatWindow.setJMenuBar(GraphicUtils.createMenuBar(
                new String[][]{
                        {"File", "Not imlplemented", null, "Close"},
                        {"View", "Colors"},
                        {"Messages", "Send Message", "Clear Chat History"},
                },
                this,
                null,
                ReflectionUtils.getMethod("notImplemented"),
                ReflectionUtils.getMethod("notImplemented"),
                ReflectionUtils.getMethod("notImplemented"),
                ReflectionUtils.getMethod("sendChatMessage"),
                ReflectionUtils.getMethod("clearChatHistory")));


        chatPanel = new ChatPanel();
        chatWindow.loadContent(chatPanel);

        chatPanel.getAttachFileButton().setAction(GraphicUtils.createAction(
                "Attach File",
                this,
                ReflectionUtils.getMethod("notImplemented")));

        chatPanel.getSendMessageButton().setAction(GraphicUtils.createAction(
                "Send Message",
                this,
                ReflectionUtils.getMethod("sendChatMessage")));

        frame.addInternalFrame(chatWindow, false);

    }

    public synchronized void loadTable(String title, Container container) {
        CustomInternalFrame internalFrame = new CustomInternalFrame(title);
        internalFrame.loadContent(container);

        internalFrame.setJMenuBar(GraphicUtils.createMenuBar(
                new String[][]{
                        {"Action", "sit"},
                },
                this,
                null,
                ReflectionUtils.getMethod("sit")));

        frame.addInternalFrame(internalFrame, true);
        try {
            internalFrame.setMaximum(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
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
        loadChatWindow();

        // todo
        for (int i = 0; i < 4; i++) {
            sendMessage(createSitRequest(i));
        }
    }

    public void actionOnReconnect() {
        requestLogin();
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

    public void sit() {
        int place = Integer.parseInt(Console.readString("enter place [0..3]"));
        sendMessage(createSitRequest(place));
    }

    public void startRound() {
        sendMessage(createRequest(BasicGameAction.START_ROUND.getId()));
    }

    //-----------------------------------------------------------------------------------

    public void notImplemented() {
    }

    public void closeChatWindow() {
    }

    public void sendChatMessage() {
        if (socketConnection != null) {
            socketConnection.sendMessage(createTextMessage(userName + ": " + chatPanel.getInputText() + '\n'));
        }
    }

    public void sendFile() {
        chatPanel.sendFile(null);
    }

    public void clearChatHistory() {
        chatPanel.clearHistory();
    }

    //-----------------------------------------------------------------------------------

    private byte[] createRequest(int... a) {
        ByteMessageBuilder builder = new ByteMessageBuilder(128, ',');
        for (int x : a) {
            builder.writeInt(x);
        }
        return builder.getMessage();
    }

    private void requestLogin() {
        if (socketConnection != null) {
            socketConnection.removeConnectionListener(this);
            socketConnection.closeConnection();
        }
        try {
            Properties properties = new Properties();
            properties.load(getResource("resources/client.properties"));
            Socket socket = new Socket(host, Integer.parseInt(properties.getProperty("connection.port")));
            socketConnection = new FastConnection(socket, 0, 5000, '2', '1', '@', 0);
            socketConnection.addConnectionListener(this);
            socketConnection.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (socketConnection != null) {
            sendMessage(createLoginRequest());
        }
    }

    private byte[] createLoginRequest() {
        ByteMessageBuilder builder = new ByteMessageBuilder(16, ',');
        builder.writeInt(BasicGameAction.LOG_IN.getId());
        builder.writeInt(gameId);
        builder.writeInt(1); // room
        builder.writeInt(0); // user
        builder.writeString(password);
        return builder.getMessage();
    }

    private byte[] createTextMessage(String text) {
        ByteMessageBuilder builder = new ByteMessageBuilder(16, ',');
        builder.writeInt(0);
        return builder.getMessage();
    }

    private byte[] createSitRequest(int sit) {
        ByteMessageBuilder builder = new ByteMessageBuilder(16, ',');
        builder.writeInt(BasicGameAction.SIT_DOWN.getId());
        builder.writeInt(sit);
        return builder.getMessage();
    }

    //-----------------------------------------------------------------------------------

    public void sendMessage(byte[] message) {
        socketConnection.sendMessage(message);
    }

    //-----------------------------------------------------------------------------------

    public void messageSent(SocketConnection<byte[]> source, byte[] message) {
    }

    public void messageReceived(SocketConnection<byte[]> source, byte[] message) {
        try {
            SwingUtilities.invokeLater(() -> handle(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void handle(byte[] message);

    //-----------------------------------------------------------------------------------

    public static BufferedImage getImage(String path) {
        try {
            return ImageIO.read(getResource(path));
        } catch (IOException e) {
            return null;
        }
    }

    public static InputStream getResource(String path) {
        return FileSystemUtils.getResource(path);
    }

    private static void quickAndDirtyFixForProblemWithWebStartInJava7u25() {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    try {
                        // Change context in all future threads
                        final Field field = EventQueue.class.getDeclaredField("classLoader");
                        field.setAccessible(true);
                        final EventQueue eq = Toolkit.getDefaultToolkit().getSystemEventQueue();
                        field.set(eq, cl);
                        // Change context in this thread
                        Thread.currentThread().setContextClassLoader(cl);
                    } catch (Exception ex) {
                        // Call to java logging causes NPE :-( ...
                        ex.printStackTrace(System.err);
                        System.err.println("Unable to apply 'fix' for java 1.7u25");
                    }
                }
            });
        } catch (Exception ex) {
            // Same as serr above
            ex.printStackTrace(System.err);
            System.err.println("Unable to apply 'fix' for java 1.7u25");
        }
    }

}
