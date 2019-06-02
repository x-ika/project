package ika.games.admin;

import com.simplejcode.commons.gui.*;
import com.simplejcode.commons.misc.util.ReflectionUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.awt.*;

public class AdminPanel {

    public static AdminPanel instance;

    private DesktopFrame frame;
    private AboutDialogPanel aboutDialogPanel;
    private LoginForm loginForm;

    public AdminPanel() {
        try {
            aboutDialogPanel = new AboutDialogPanel("JAdmin", ImageIO.read(new File("resources/logo.png")));
            aboutDialogPanel.init();
            frame = new DesktopFrame("Client", ImageIO.read(new File("resources/background.jpg")), ImageIO.read(new File("resources/foreground.jpg")));
            frame.setMinimumSize(new Dimension(900, 600));
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame.setJMenuBar(GraphicUtils.createMenuBar(
                new String[][] {
                        {"Game Server", "Login", null, "Exit"},
                        {"Help", "About"}},
                this,
                null,
                ReflectionUtils.getMethod("actionOnLogin"),
                ReflectionUtils.getMethod("actionOnExit"),
                ReflectionUtils.getMethod("actionOnAbout")));

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

        loadConsole();
    }

    private void loadConsole() {
        try {
            CustomInternalFrame internalFrame = new LiveAdmin();
            frame.addInternalFrame(internalFrame, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DesktopFrame getFrame() {
        return frame;
    }

    //-----------------------------------------------------------------------------------

    public void actionOnLogin() {
        loginForm = new LoginForm(
                GraphicUtils.createAction(
                        "OK",
                        this,
                        ReflectionUtils.getMethod("login")),

                GraphicUtils.createAction(
                        "Exit",
                        this,
                        ReflectionUtils.getMethod("closeLogin")));

        loginForm.setServerAddress("localhost");
        loginForm.setLogin("ika");
        loginForm.setPassword("paracalo");
    }

    public void actionOnExit() {
        System.exit(0);
    }

    public void actionOnAbout() {
        aboutDialogPanel.showDialog(frame);
    }

    public void notImplemented() {
    }

    public void login() {
        try {
            frame.getComponent(LiveAdmin.class).sendLogin(loginForm.getServerAddress(), loginForm.getLogin(), loginForm.getPassword());
            loginForm.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeLogin() {
        loginForm.dispose();
    }

    //-----------------------------------------------------------------------------------

    public static void main(String[] args) {
        instance = new AdminPanel();
    }

}
