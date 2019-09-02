import com.simplejcode.commons.misc.util.ThreadUtils;
import com.simplejcode.commons.net.sockets.SocketConnection;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

public class OperatorsPanel extends JPanel {

    private class CellRenderer extends JPanel implements TableCellRenderer {

        private JTextArea text = new JTextArea();

        public CellRenderer() {
            add(text);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column)
        {
            setBackground(rowColors[row]);
            text.setBackground(getBackground());
            if (column == 0) {
                setBorder(BorderFactory.createEmptyBorder(5, 15, 0, 0));
                setLayout(new BorderLayout());
                add(text);
            } else {
                setBorder(BorderFactory.createEmptyBorder());
            }
            if (value != null && value instanceof String) {
                text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 23));
                text.setForeground(Color.black);
                text.setText(value.toString());
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
                                                       int row, int column)
        {
            if (column == 0) {
                setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
                setLayout(new BorderLayout());
                add(label);
            }
            label.setText(value.toString());
            return this;
        }

    }

    private class ConnectionComparator implements Comparator<SocketConnection> {
        public int compare(SocketConnection o1, SocketConnection o2) {
            return o1.getHost().toString().compareTo(o2.getHost().toString());
        }
    }

    //-----------------------------------------------------------------------------------

    private JTable table;
    private DefaultTableModel model;
    private Color[] rowColors = new Color[1 << 12];

    public OperatorsPanel(Action nextAction) {
        super(new BorderLayout());

        model = new DefaultTableModel(0, 2);
        table = new JTable(model);
        table.setRowHeight(50);
        table.setRowMargin(0);
        table.getColumnModel().setColumnMargin(0);


        customize(table.getColumnModel().getColumn(0), "Operator", 250);
        customize(table.getColumnModel().getColumn(1), "ID", 50);

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


        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(scrollPane);
        add(new JButton(nextAction), BorderLayout.SOUTH);
    }

    private void customize(TableColumn column, String header, int width) {
        column.setMinWidth(width);
        column.setMaxWidth(width);
        column.setHeaderRenderer(new HeaderCellRenderer());
        column.setCellRenderer(new CellRenderer());
        column.setHeaderValue(header);
        column.setResizable(false);
    }

    public void updateTable(Map<SocketConnection, OperatorData> map) {
        EventQueue.invokeLater(ThreadUtils.createThread(() -> {
            model.setRowCount(0);
            SocketConnection[] t = map.keySet().toArray(new SocketConnection[0]);
            Arrays.sort(t, new ConnectionComparator());
            for (int i = 0; i < t.length; i++) {
                OperatorData data = map.get(t[i]);
                rowColors[i] = data.isFree() ? Color.red : Color.white;
                String[] row = new String[2];
                row[0] = t[i].getHost().toString();
                row[1] = data.isFree() ? "" : "" + data.getTicket();
                model.addRow(row);
            }
        }));
    }

}
