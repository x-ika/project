package gui;

import com.simplejcode.commons.net.csbase.Message;
import com.simplejcode.commons.net.sockets.SocketConnection;
import model.RequestType;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.io.*;

import message.ProblemInfo;
import message.ContestInfo;
import message.Request;

public class ProblemStatementViewer extends BaseListener {

    private JComboBox<String> problems;

    private Document doc;
    private RTFEditorKit editorKit;

    private ProblemInfo[] infos;

    public ProblemStatementViewer(SocketConnection<Message> socketConnection, String title) {
        super(socketConnection, title);
        socketConnection.sendMessage(new Request(this, RequestType.CONTEST_INFO));
    }

    private JPanel createContent(ContestInfo info) {

        infos = new ProblemInfo[info.getNProblems()];

        problems = new JComboBox<>();
        for (int i = 0; i < info.getNProblems(); i++) {
            problems.addItem(info.getProblemNames()[i]);
        }
        problems.addActionListener(event -> {
            try {
                updateStatement();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        JTextPane statementTextArea = new JTextPane();
        statementTextArea.setBackground(Color.white);
        statementTextArea.setEditable(false);

        editorKit = new RTFEditorKit();
        statementTextArea.setEditorKit(editorKit);
        doc = statementTextArea.getDocument();

        JPanel lower = new JPanel(new BorderLayout());
        lower.setLayout(new BorderLayout());
        lower.setOpaque(false);
        lower.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        lower.add(getScrollPane(statementTextArea), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.add(create("Problem", problems), BorderLayout.NORTH);
        panel.add(lower, BorderLayout.SOUTH);
        return panel;
    }

    private JScrollPane getScrollPane(Component c) {
        JScrollPane pane = new JScrollPane(c, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setPreferredSize(new Dimension(600, 400));
        return pane;
    }

    private static JComponent create(String text, JComponent component) {

        JLabel label = new JLabel(text);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        label.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
        label.setMinimumSize(new Dimension(100, 25));
        label.setMaximumSize(new Dimension(100, 25));

        component.setMaximumSize(component.getPreferredSize());

        JComponent s = new Box(BoxLayout.X_AXIS);
        s.add(label);
        s.add(component);
        s.add(Box.createHorizontalGlue());
        s.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        return s;
    }

    //-----------------------------------------------------------------------------------

    public void messageReceived(SocketConnection<Message> source, Message message) throws Exception {
        if (message instanceof ContestInfo) {
            loadContent((ContestInfo) message);
        }
        if (message instanceof ProblemInfo) {
            loadProblemStatement((ProblemInfo) message);
        }
    }

    private synchronized void loadContent(ContestInfo info) {
        if (problems != null) {
            return;
        }
        loadContent(createContent(info));
        socketConnection.sendMessage(new Request(this, RequestType.PROBLEM_STATEMENTS));
    }

    private synchronized void loadProblemStatement(ProblemInfo problemInfo) throws Exception {
        infos[problemInfo.getProblem()] = problemInfo;
        updateStatement();
    }

    private void updateStatement() throws Exception {
        ProblemInfo info = infos[problems.getSelectedIndex()];
        doc.remove(0, doc.getLength());
        byte[] data = info.getBuff();
        if (data != null) {
            editorKit.read(new ByteArrayInputStream(data), doc, 0);
        }
    }

}
