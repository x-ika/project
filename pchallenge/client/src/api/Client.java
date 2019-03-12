package api;

import com.simplejcode.commons.net.csbase.*;
import com.simplejcode.commons.net.sockets.*;
import model.RequestType;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.net.Socket;
import java.io.IOException;
import java.io.File;

import com.simplejcode.commons.gui.LoginForm;
import gui.*;
import com.simplejcode.commons.gui.GraphicUtils;
import com.simplejcode.commons.gui.DesktopFrame;
import com.simplejcode.commons.gui.AboutDialogPanel;
import com.simplejcode.commons.misc.ReflectionUtils;
import message.ContestStatus;
import message.Login;
import message.Request;

public class Client extends ConnectionAdapter<Message> {

    private static Client instance;

    public static Client getInstance() {
        return instance;
    }

    private NetMessanger socketConnection;

    private AboutDialogPanel aboutDialogPanel;
    private DesktopFrame frame;

    private LoginForm loginForm;

    private String host;
    private String teamName;
    private String password;

    public Client() {

        loginForm = new LoginForm(
                GraphicUtils.createAction(
                        "OK",
                        this,
                        ReflectionUtils.getMethod("login")),

                GraphicUtils.createAction(
                        "Exit",
                        this,
                        ReflectionUtils.getMethod("actionOnExit")));

    }

    public String getTeamName() {
        return teamName;
    }

    public String getPassword() {
        return password;
    }

    //-----------------------------------------------------------------------------------

    public void createMainFrame() {
        try {
            aboutDialogPanel = new AboutDialogPanel("TS Client", ImageIO.read(new File("resources/logo.jpeg")));
            aboutDialogPanel.init();
            frame = new DesktopFrame("Client", ImageIO.read(new File("resources/background.jpg")), ImageIO.read(new File("resources/foreground.jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame.setJMenuBar(GraphicUtils.createMenuBar(
                new String[][] {
                        {"File", "Reconnect", null, "Exit"},
//                        {"Open", "Chat Window", "Standings", "Submit Form", "Submission History", "Problem Statements"},
                        {"Help", "About"}},
                this,
                null,
                ReflectionUtils.getMethod("actionOnReconnect"),
                ReflectionUtils.getMethod("actionOnExit"),
//                ReflectionUtils.getMethod("loadChatWindow"),
//                ReflectionUtils.getMethod("loadStandings"),
//                ReflectionUtils.getMethod("loadSubmitForm"),
//                ReflectionUtils.getMethod("loadSubmissionHistory"),
//                ReflectionUtils.getMethod("loadProblemStatementViewer"),
                ReflectionUtils.getMethod("actionOnAbout")));

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

    }

    public void loadChatWindow() {
        ChatWindow chatWindow = new ChatWindow(socketConnection, "Chat Window");

        chatWindow.setJMenuBar(GraphicUtils.createMenuBar(
                new String[][] {
                        {"File", "Not imlplemented", null, "Close"},
                        {"View", "Colors"},
                        {"Messages", ChatWindow.SEND_MESSAGE, ChatWindow.ATTACH_FILE, "Clear History"},
                },
                this,
                ReflectionUtils.getMethod("notImplemented"),
                ReflectionUtils.getMethod("notImplemented"),
                ReflectionUtils.getMethod("notImplemented"),
                ReflectionUtils.getMethod("sendMessage"),
                ReflectionUtils.getMethod("notImplemented"),
                ReflectionUtils.getMethod("clearChatHistory")));


        chatWindow.load();

        chatWindow.getAttachFileButton().setAction(GraphicUtils.createAction(
                ChatWindow.ATTACH_FILE,
                this,
                ReflectionUtils.getMethod("notImplemented")));

        chatWindow.getSendMessageButton().setAction(GraphicUtils.createAction(
                ChatWindow.SEND_MESSAGE,
                this,
                ReflectionUtils.getMethod("sendMessage")));

        frame.addInternalFrame(chatWindow, false);

    }

    public void loadStandings() {
        StandingsView standingsView = new StandingsView(socketConnection, "Standings");
        frame.addInternalFrame(standingsView, false);
    }

    public void loadSubmitForm() {
        SolutionSubmitForm submitForm = new SolutionSubmitForm(socketConnection, "Submit Solution");
        frame.addInternalFrame(submitForm, false);
    }

    public void loadSubmissionHistory() {
        SubmissionHistory submissionHistory = new SubmissionHistory(socketConnection, "Submission History");
        frame.addInternalFrame(submissionHistory, false);
    }

    public void loadProblemStatementViewer() {
        ProblemStatementViewer statementViewer = new ProblemStatementViewer(socketConnection, "Problem Statement");
        frame.addInternalFrame(statementViewer, false);
    }

    public void actionOnContestStarted() {
        loadStandings();
        loadSubmitForm();
        loadSubmissionHistory();
        loadProblemStatementViewer();
    }

    private void requestLogin() {
        try {
            socketConnection = new NetMessanger(new Socket(host, 4444), 999, 50000);
            socketConnection.addConnectionListener(this);
            socketConnection.start();
        } catch (IOException e) {
            System.out.println("Can not connect to the server.");
        }
        if (socketConnection != null) {
            socketConnection.sendMessage(new Login(this, teamName, password));
        }
    }

    //-----------------------------------------------------------------------------------

    public void login() {
        host = loginForm.getServerAddress();
        teamName = loginForm.getLogin();
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
        socketConnection.sendMessage(new Request(this, RequestType.CONTEST_STATUS));
    }

    public void actionOnReconnect() {
        socketConnection.closeConnection();
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


    public void notImplemented() {

    }

    public void closeChatWindow() {
        frame.getComponent(ChatWindow.class).dispose();
    }

    public void sendMessage() {
        frame.getComponent(ChatWindow.class).sendMessage();
    }

    public void sendFile() {
        frame.getComponent(ChatWindow.class).sendFile(null);
    }

    public void clearChatHistory() {
        frame.getComponent(ChatWindow.class).clearHistory();
    }

    //-----------------------------------------------------------------------------------

    public synchronized void messageReceived(SocketConnection source, Message message) throws Exception {
        if (message instanceof Login) {
            Login login = (Login) message;
            if (login.getLogin().equals(teamName)) {
                loginSuccessful();
            }
        }
        if (message instanceof ContestStatus && ((ContestStatus) message).isStarted()) {
            actionOnContestStarted();
        }
    }

    public static void main(String[] args) {
        instance = new Client();
    }

}
