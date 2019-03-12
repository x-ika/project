package gui;

import model.RequestType;
import model.Language;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import com.simplejcode.commons.gui.GraphicUtils;
import com.simplejcode.commons.misc.ReflectionUtils;
import com.simplejcode.commons.net.csbase.Message;
import com.simplejcode.commons.net.sockets.SocketConnection;
import api.Client;
import message.Submission;
import message.ContestInfo;
import message.Request;

public class SolutionSubmitForm extends BaseListener {

    private JComboBox problems;
    private JComboBox languages;
    private JTextField pathField;
    private JTextArea solutionTextArea;

    private JFileChooser fileChooser = new JFileChooser();

    public SolutionSubmitForm(SocketConnection<Message> socketConnection, String title) {
        super(socketConnection, title);
        socketConnection.sendMessage(new Request(this, RequestType.CONTEST_INFO));
    }

    private JPanel createContent(ContestInfo info) {

        languages = new JComboBox();
        for (Language language : Language.values()) {
            languages.addItem(language);
        }
        problems = new JComboBox();
        for (int i = 0; i < info.getNProblems(); i++) {
            problems.addItem(info.getProblemNames()[i]);
        }

        JPanel cf = new JPanel(new BorderLayout());
        cf.add(pathField = new JTextField(20), BorderLayout.WEST);
        JButton button = new JButton();
        button.setAction(GraphicUtils.createAction(
                "Browse",
                this,
                ReflectionUtils.getMethod("openFileChooser")));
        cf.add(button, BorderLayout.EAST);

        JButton sendButton = new JButton(GraphicUtils.createAction(
                "Submit Solution!",
                this,
                ReflectionUtils.getMethod("sendCommand")));

        JPanel upper = new JPanel();
        upper.setLayout(new BoxLayout(upper, BoxLayout.Y_AXIS));
        upper.setOpaque(false);
        upper.add(create("Problem", problems));
        upper.add(create("Language", languages));
        upper.add(create("File", cf));
        upper.add(create("Send", sendButton));

        solutionTextArea = new JTextArea(20, 20);
        solutionTextArea.setBackground(Color.white);

        JPanel lower = new JPanel(new BorderLayout());
        lower.setOpaque(false);
        lower.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        lower.add(getScrollPane(solutionTextArea), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.add(upper, BorderLayout.NORTH);
        panel.add(lower, BorderLayout.SOUTH);

        return panel;
    }

    private JScrollPane getScrollPane(Component c) {
        return new JScrollPane(c, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    private static JComponent create(String text, JComponent component) {
        JComponent s = new Box(BoxLayout.X_AXIS);
        JLabel label = new JLabel(text);
        label.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
        label.setPreferredSize(new Dimension(100, 25));
        label.setMaximumSize(new Dimension(100, 25));
        s.add(label);
        component.setPreferredSize(new Dimension(300, 25));
        component.setMaximumSize(new Dimension(300, 25));
        s.add(component);
        s.add(Box.createHorizontalGlue());
        s.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        return s;
    }

    //-----------------------------------------------------------------------------------

    public void openFileChooser() {
        final Window window = new JDialog((Frame) null, "Choose File", true);

        fileChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
                    pathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
                window.dispose();
                fileChooser.removeActionListener(this);
            }
        });

        window.add(fileChooser);
        window.pack();
        GraphicUtils.centerOnScreen(window);
        window.setVisible(true);
    }

    public synchronized void sendCommand() {
        String path = pathField.getText();
        Submission message;
        if (path == null || path.isEmpty()) {

            String content = solutionTextArea.getText();
            if (content == null || content.isEmpty()) {
                return;
            }

            message = new Submission(this);
            byte[] buf = new byte[content.length()];
            for (int i = 0; i < buf.length; i++) {
                char c = content.charAt(i);
                if (c >= 128) {
                    // non ascii symbol
                    return;
                }
                buf[i] = (byte) c;
            }

            message.setBuff(buf);

        } else {
            message = new Submission(this, new File(path));
            if (message.getFileName() == null) {
                return;
            }
        }

        Language language = (Language) languages.getSelectedItem();

        message.setFileName(language.getDefaultFileName());
        message.setSenderTeam(Client.getInstance().getTeamName());
        message.setProblem(problems.getSelectedIndex());
        message.setLanguage(language);

        socketConnection.sendMessage(message);
        pathField.setText(null);
        System.gc();
    }

    public void messageReceived(SocketConnection<Message> source, Message message) throws Exception {
        if (message instanceof ContestInfo) {
            loadContent((ContestInfo) message);
        }
    }

    public void messageSent(SocketConnection<Message> source, Message message) throws Exception {
        if (message.getSender() == this && message instanceof Submission) {
            JOptionPane.showMessageDialog(this, "Submission was sent, Good Luck!");
        }
    }

    private synchronized void loadContent(ContestInfo info) {
        if (problems != null) {
            return;
        }
        loadContent(createContent(info));
    }

}
