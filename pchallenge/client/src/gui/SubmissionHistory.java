package gui;

import com.simplejcode.commons.net.csbase.Message;
import com.simplejcode.commons.net.sockets.SocketConnection;
import model.RequestType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;

import api.Client;
import message.TeamHistory;
import message.ContestInfo;
import message.Request;

public class SubmissionHistory extends BaseListener {

    private static final int N_COLUMNS = 5;

    private JTable table;
    private DefaultTableModel model;
    private TeamHistory myHistory;
    private ContestInfo contestInfo;

    public SubmissionHistory(SocketConnection<Message> socketConnection, String title) {
        super(socketConnection, title);
        setPreferredSize(new Dimension(450, 400));
        loadContent(createContent());
        socketConnection.sendMessage(new Request(this, RequestType.CONTEST_INFO));
    }

    private JPanel createContent() {

        model = new DefaultTableModel(0, N_COLUMNS);
        table = new JTable(model);
        table.setRowHeight(20);
        table.setRowMargin(0);
        table.getColumnModel().setColumnMargin(0);


        int index = 0;
        customize(table.getColumnModel().getColumn(index++), "Number", 80);
        customize(table.getColumnModel().getColumn(index++), "Time", 80);
        customize(table.getColumnModel().getColumn(index++), "Problem", 80);
        customize(table.getColumnModel().getColumn(index++), "Result", 80);
        customize(table.getColumnModel().getColumn(index++), "Failed test", 80);

        table.setEnabled(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        table.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));

        JComponent tableContainer = new Box(BoxLayout.Y_AXIS);
        tableContainer.setOpaque(false);
        tableContainer.add(table.getTableHeader());
        tableContainer.add(table);

        JScrollPane scrollPane = new JScrollPane(tableContainer,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(scrollPane);
        return panel;
    }

    private void customize(TableColumn column, String header, int width) {
        column.setMinWidth(width);
        column.setMaxWidth(width);
        column.setHeaderValue(header);
        column.setResizable(false);
    }

    //-----------------------------------------------------------------------------------

    public void messageReceived(SocketConnection<Message> source, Message message) throws Exception {
        if (message instanceof ContestInfo) {
            saveInfo((ContestInfo) message);
        }
        if (message instanceof TeamHistory) {
            updateHistory((TeamHistory) message);
        }
    }

    private synchronized void saveInfo(ContestInfo contestInfo) {
        if (this.contestInfo != null) {
            return;
        }
        this.contestInfo = contestInfo;
        socketConnection.sendMessage(new Request(this, RequestType.HISTORIES));
    }

    private synchronized void updateHistory(TeamHistory history) {
        if (table == null) {
            return;
        }
        if (!Client.getInstance().getTeamName().equals(history.getTeamName())) {
            return;
        }
        myHistory = history;
        updateTable();
    }

    private void updateTable() {
        EventQueue.invokeLater(new Thread() {
            public void run() {

                model.setRowCount(0);
                int ind = 0;
                for (TeamHistory.Attempt attempt : myHistory.getAttempts()) {

                    Object[] row = new Object[N_COLUMNS];
                    int index = 0;
                    String space = "          ";
                    row[index++] = space + String.format("%d", ++ind);
                    row[index++] = space + String.format("%02d:%02d", attempt.time / 60, attempt.time % 60);
                    row[index++] = space + String.format("%s", contestInfo.getProblemNames()[attempt.problem].substring(0, 1));
                    row[index++] = space + String.format("%s", attempt.result);
                    row[index++] = space + String.format("%d", attempt.failedTest);
                    model.addRow(row);

                    validate();
                }
            }
        });
    }

}
