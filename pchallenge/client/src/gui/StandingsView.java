package gui;

import com.simplejcode.commons.net.csbase.Message;
import com.simplejcode.commons.net.sockets.SocketConnection;
import message.TeamHistory;
import message.ContestInfo;
import message.Request;
import model.RequestType;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

import api.Client;

public class StandingsView extends BaseListener {

    private static class Cell {
        boolean b;
        int n;
        int t;

        public Cell(boolean b, int n, int t) {
            this.b = b;
            this.n = n;
            this.t = t;
        }
    }

    private class CellRenderer extends JPanel implements TableCellRenderer {

        private JTextArea text = new JTextArea();

        public CellRenderer() {
            add(text);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setBackground(rowColors[row]);
            text.setBackground(getBackground());
            if (column == 0) {
                setBorder(BorderFactory.createEmptyBorder(5, 15, 0, 0));
                setLayout(new BorderLayout());
                add(text);
            } else {
                setBorder(BorderFactory.createEmptyBorder());
            }
            if (value != null) {
                if (value instanceof String) {
                    text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
                    text.setForeground(Color.black);
                    text.setText(value.toString());
                }
                if (value instanceof Cell) {
                    Cell cell = (Cell) value;
                    if (!cell.b) {
                        setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
                        text.setFont(Font.decode("arial").deriveFont(Font.PLAIN, 12));
                        text.setForeground(new Color(192, 96, 96));
                        text.setText(" -" + cell.n);
                    } else if (cell.n == 0) {
                        setBorder(BorderFactory.createEmptyBorder());
                        text.setFont(Font.decode("arial").deriveFont(Font.BOLD, 10));
                        text.setForeground(new Color(0, 128, 0));
                        text.setText("  +  \n" + String.format("%d:%02d", cell.t / 60, cell.t % 60));
                    } else {
                        setBorder(BorderFactory.createEmptyBorder());
                        text.setFont(Font.decode("arial").deriveFont(Font.BOLD, 10));
                        text.setForeground(new Color(0, 128, 0));
                        text.setText(" +" + cell.n + "\n" + String.format("%d:%02d", cell.t / 60, cell.t % 60));
                    }
                }
            }
            return this;
        }

    }

    private class HeaderCellRenderer extends JPanel implements TableCellRenderer {

        private JLabel label = new JLabel();

        public HeaderCellRenderer() {
            setBackground(Color.white);
            label.setFont(label.getFont().deriveFont(Font.BOLD, 15));
            add(label);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if (column == 0) {
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
                setLayout(new BorderLayout());
                add(label);
            }
            label.setText(value.toString());
            return this;
        }

    }

    private class TeamHistoryComparator implements Comparator<TeamHistory> {
        public int compare(TeamHistory o1, TeamHistory o2) {
            return o1.getSolved() != o2.getSolved() ?
                    o2.getSolved() - o1.getSolved() : o1.getTotalTime() - o2.getTotalTime();
        }
    }

    //-----------------------------------------------------------------------------------

    private TeamHistoryComparator comparator = new TeamHistoryComparator();

    private int nProblems;
    private JTable table;
    private DefaultTableModel model;

    private Color[] rowColors = new Color[1 << 12];

    private Map<TeamHistory, Object[]> teams = new Hashtable<>();

    public StandingsView(SocketConnection<Message> socketConnection, String title) {
        super(socketConnection, title);
        setPreferredSize(new Dimension(900, 600));
        socketConnection.sendMessage(new Request(this, RequestType.CONTEST_INFO));
    }

    private JPanel createContent(ContestInfo info) {

        this.nProblems = info.getNProblems();

        model = new DefaultTableModel(0, nProblems + 4);
        table = new JTable(model);
        table.setRowHeight(30);
        table.setRowMargin(0);
        table.getColumnModel().setColumnMargin(0);


        int index = 0;
        customize(table.getColumnModel().getColumn(index++), "Team", 300);
        for (int i = 0; i < nProblems; i++) {
            customize(table.getColumnModel().getColumn(index++), info.getProblemNames()[i].substring(0, 1), 60);
        }
        customize(table.getColumnModel().getColumn(index++), "=", 50);
        customize(table.getColumnModel().getColumn(index++), "Time", 50);
        customize(table.getColumnModel().getColumn(index), "Rank", 50);

        table.setEnabled(false);
        table.setShowGrid(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        table.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));

        JComponent tableContainer = new Box(BoxLayout.Y_AXIS);
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
        column.setHeaderRenderer(new HeaderCellRenderer());
        column.setCellRenderer(new CellRenderer());
        column.setHeaderValue(header);
        column.setResizable(false);
    }

    //-----------------------------------------------------------------------------------

    public void messageReceived(SocketConnection<Message> source, Message message) throws Exception {
        if (message instanceof ContestInfo) {
            loadStandings((ContestInfo) message);
        }
        if (message instanceof TeamHistory) {
            updateStandings((TeamHistory) message);
        }
    }

    private synchronized void loadStandings(ContestInfo contestInfo) {
        if (nProblems != 0) {
            return;
        }
        nProblems = contestInfo.getNProblems();
        loadContent(createContent(contestInfo));
        socketConnection.sendMessage(new Request(this, RequestType.HISTORIES));
    }

    private synchronized void updateStandings(TeamHistory history) {
        if (table == null) {
            return;
        }
        history.summarize();

        Object[] row = new Object[table.getColumnCount()];
        int index = 0;
        row[index++] = history.getTeamName();
        for (int i = 0; i < nProblems; i++) {
            int x = history.getResult(i);
            int y = history.getSolveTime(i);
            boolean b = history.solved(i);
            row[index++] = b || x > 0 ? new Cell(b, x, y) : ".";
        }
        row[index++] = String.valueOf(history.getSolved());
        row[index++] = String.valueOf(history.getTotalTime());

        teams.remove(history);
        teams.put(history, row);

        // update ranks
        for (TeamHistory th : teams.keySet()) {
            int rank = 1;
            for (TeamHistory t : teams.keySet()) {
                if (comparator.compare(th, t) > 0) {
                    rank++;
                }
            }
            teams.get(th)[index] = String.valueOf(rank);
        }

        updateTable();
    }

    private void updateTable() {
        EventQueue.invokeLater(new Thread() {
            public void run() {
                model.setRowCount(0);
                Color current = Color.white;
                TeamHistory[] t = teams.keySet().toArray(new TeamHistory[0]);
                Arrays.sort(t, comparator);
                for (int i = 0; i < t.length; i++) {
                    if (i != 0 && t[i].getSolved() < t[i - 1].getSolved()) {
                        current = current == Color.white ? new Color(208, 240, 255) : Color.white;
                    }
                    rowColors[i] = i % 2 == 0 ? current : darker(current, 8);
                    if (t[i].getTeamName().equals(Client.getInstance().getTeamName())) {
                        rowColors[i] = Color.yellow;
                    }
                    model.addRow(teams.get(t[i]));
                }
            }
        });
    }

    private static Color darker(Color c, int d) {
        return new Color(c.getRed() - d, c.getGreen() - d, c.getBlue() - d);
    }

}
