package pool.gui.menu;

import pool.gui.MainFrame;
import pool.gui.primitive.*;
import pool.utils.Consts;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Properties;

public class RegistrationManager extends BasicDialog implements ActionListener {
    private final String REGISTRATION = "Registration";

    private final String REMOVE = "Remove";

    private final String MANAGER_PASSWORD = "123";

    private LabelAndField nameField;

    private LabelAndField passwordField;

    private LabelAndField retipePasswordField;

    private LabelAndField answerField;

    public RegistrationManager(MainFrame owner) {
        super(owner, "Registration");
        setVisible(true);
    }

    protected void setContent() {
        Color bgColor = Color.yellow;
        nameField = new LabelAndField("Name", 30, bgColor);
        nameField.setBackground(bgColor);
        passwordField = new LabelAndField("Password", 30, true, bgColor);
        passwordField.setBackground(bgColor);
        retipePasswordField = new LabelAndField("Retipe Password", 30, true, bgColor);
        retipePasswordField.setBackground(bgColor);
        answerField = new LabelAndField("Answer", 30, bgColor);
        answerField.setBackground(bgColor);
        answerField.setEnabled(false);
        JPanel sixth = new JButtons(this, REGISTRATION, REMOVE, bgColor);
        Container panel = getContentPane();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(nameField);
        panel.add(passwordField);
        panel.add(retipePasswordField);
        panel.add(answerField);
        panel.add(sixth);
    }

    public void actionPerformed(ActionEvent ev) {
        String comand = ev.getActionCommand();
        if (comand.equals(REGISTRATION)) {
            registrate();
        } else if (comand.equals(REMOVE)) {
            removePlayer();
        }
    }

    public void registrate() {
        if (nameField.getText().length() == 0 || passwordField.getText().length() == 0) {
            answerField.setText("The fields must be filled");
            return;
        }
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(Consts.DOCS_PATH + "players.txt"));
            if (properties.getProperty(nameField.getText()) != null) {
                answerField.setText("This name is registered");
                return;
            }
            if (!passwordField.getText().equals(retipePasswordField.getText())) {
                answerField.setText("Retiped password is not right");
                return;
            }
            properties.setProperty(nameField.getText(), passwordField.getText());
            answerField.setText("Player \"" + nameField.getText() + "\" is registered");
            properties.store(new FileOutputStream(Consts.DOCS_PATH + "players.txt"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removePlayer() {
        if (nameField.getText() == null || passwordField.getText() == null) {
            answerField.setText("The fields must be filled");
            return;
        }
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(Consts.DOCS_PATH + "players.txt"));
            if (!passwordField.getText().equals(retipePasswordField.getText())) {
                answerField.setText("Retiped password is not right");
                return;
            }
            if (!passwordField.getText().equals(MANAGER_PASSWORD)) {
                answerField.setText("You have not rights to remove player");
                return;
            }
            if (properties.remove(nameField.getText()) == null) {
                answerField.setText("Player \"" + nameField.getText() + "\" is non-existent");
            } else {
                answerField.setText("Player \"" + nameField.getText() + "\" is removed");
            }
            properties.store(new FileOutputStream(Consts.DOCS_PATH + "players.txt"), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
