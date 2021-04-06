package pool.gui.menu;

import pool.gui.MainFrame;
import pool.gui.primitive.*;
import pool.utils.Consts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Properties;

public class NewGame extends BasicDialog implements ActionListener {
    private static final String[] TITLES = new String[]{"First Player", "Second Player"};

    private final String OK = "Ok";

    private final String CANCEL = "Cancel";

    private final int numberOfPlayer;

    private final String previousName;

    private LabelAndField nameField;

    private LabelAndField passwordField;

    public NewGame(MainFrame owner, String previousName, int numberOfPlayer) {
        super(owner, TITLES[numberOfPlayer]);
        this.numberOfPlayer = numberOfPlayer;
        this.previousName = previousName;
        setVisible(true);
    }

    protected void setContent() {
        Color bgColor = Color.yellow;
        nameField = new LabelAndField("Name", 30, bgColor);
        nameField.setBackground(bgColor);
        passwordField = new LabelAndField("Password", 30, true, bgColor);
        passwordField.setBackground(bgColor);
        Container panel = getContentPane();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(bgColor);
        panel.add(nameField);
        panel.add(passwordField);
        panel.add(new JButtons(this, OK, CANCEL, bgColor));
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(OK)) {
            okPressed();
        } else if (command.equals(CANCEL)) {
            dispose();
        }
    }

    private void okPressed() {
        if (allRight()) {
            dispose();
            if (numberOfPlayer == 0) {
                new NewGame(mainFrame, nameField.getText(), 1);
            } else {
                done(previousName, nameField.getText());
            }
        }
    }

    private boolean allRight() {
        String name = nameField.getText(),
                password = passwordField.getText();
        if (name == null || password == null) {
            return false;
        }
        if (name.equals("") || password.equals("")) {
            return false;
        }
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(Consts.DOCS_PATH + "players.txt"));
            if (!properties.getProperty(name).equals(password)) {
                return false;
            }
            if (name.length() < 15 && password.length() < 20) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
