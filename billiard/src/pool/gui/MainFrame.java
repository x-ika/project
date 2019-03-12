package pool.gui;

import pool.gui.menu.*;
import pool.logic.PoolAPI;
import pool.utils.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Method;

public class MainFrame extends JFrame implements ActionListener {
    private static final String SEPARATOR = "separator";

    private HeadPanel head;

    //private GamePanel gamePanel;

    private String[][] MENU_BAR = {
            {"File", "New Game", "Play Again", "Main Menu", SEPARATOR, "Exit"},
            {"Settings", "On", "Off", "#RB_Sound"},
            {"Edit", "Xxx"},
            {"View", "Xxx"},
            {"Help", "About"},
    };

    public MainFrame() {
        super("Pool");
        setContentPane(head = new HeadPanel(this));
        pack();

        Rectangle r = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setLocation(((int) r.getWidth() - getWidth()) / 2,
                ((int) r.getHeight() - getHeight()) / 2);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);

        About.init();
    }

    private JMenuBar createMenubar() {
        JMenuBar menuBar = new JMenuBar();
        for (String[] menuStrings : MENU_BAR) {
            JMenu menu = new JMenu(menuStrings[0]);
            for (int i = 1; i < menuStrings.length; i++) {
                if (menuStrings[i].equals(SEPARATOR)) {
                    menu.addSeparator();
                    continue;
                }
                JMenuItem item;
                if (menuStrings[i].startsWith("#RB_")) {
                    item = new JRadioButtonMenuItem(menuStrings[i].substring(4));
                    item.setSelected(true);
                } else {
                    item = new JMenuItem(menuStrings[i]);
                }
                menu.add(item).addActionListener(this);
            }
            menuBar.add(menu);
        }
        return menuBar;
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        Object source = event.getSource();

        if (source instanceof JMenuItem) {
            executeMethod(command, event);
        } else {
            // other action
        }
    }

    private void executeMethod(String command, ActionEvent event) {
        try {
            String methodName = "process";
            for (String s : command.split(" ")) {
                methodName += s;
            }
            Method method = getClass().getMethod(methodName, ActionEvent.class);
            method.invoke(this, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processNewGame(ActionEvent e) {
        new NewGame(this, null, 0);
    }

    public void processPlayAgain(ActionEvent e) {
        // same players
    }

    public void processMainMenu(ActionEvent e) {
        head.resume.setEnabled(true);
        setContent(head);
    }

    public void processExit(ActionEvent e) {
        SoundManager.getInstance().dispose();
        dispose();
    }

    public void processSound(ActionEvent e) {
        SoundManager.getInstance().setMusicFlag(((JMenuItem) e.getSource()).isSelected());
    }

    public void processXxx(ActionEvent e) {
        // xxx
    }

    public void processAbout(ActionEvent e) {
        new About(this);
    }

    public void onMenuClicked(String label) {
        if (label.equals(head.RESUME)) {
            //setContent(gamePanel);
        } else if (label.equals(head.NEW_GAME)) {
            processNewGame(null);
        } else if (label.equals(head.REGISTRATION)) {
            new RegistrationManager(this);
        } else if (label.equals(head.BEST_RESULTS)) {
            new BestResults(this);
        } else if (label.equals(head.ALL_RESULTS)) {
            //
        } else if (label.equals(head.SETTINGS)) {
            new Settings(this);
        } else if (label.equals(head.QUIT)) {
            processExit(null);
        }
    }

    public void dialogDone(Object... args) {
        PoolAPI.getInstance().startGame(args[0].toString(), args[1].toString());
        //PoolAPI game = new PoolAPI(args[0].toString(), args[1].toString());
        //setContent(gamePanel = (GamePanel) game.getView().getParent());
    }

    public void setContent(JPanel panel) {
        if (panel == head) {
            getRootPane().getLayeredPane().remove(getJMenuBar());
        } else {
            setJMenuBar(createMenubar());
        }
        setContentPane(panel);
        validate();
    }
}
