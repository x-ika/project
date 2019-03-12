package examchecker.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public abstract class TableView<T> extends JPanel {

    protected T model;

    protected JScrollPane scrollPane;
    protected JTable table;
    protected DefaultTableModel tableModel;

    public void setModel(T model) {
        this.model = model;
    }

    public void createContent() {

        removeAll();

        customizeTable();

        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        table.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));

        table.setIntercellSpacing(new Dimension(1, 1));

        JComponent tableContainer = new Box(BoxLayout.Y_AXIS);
        tableContainer.add(table.getTableHeader());
        tableContainer.add(table);

        scrollPane = new JScrollPane(tableContainer,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(scrollPane);

    }

    //-----------------------------------------------------------------------------------

    public abstract void updateTable();

    protected abstract void customizeTable();

    protected abstract void setValue(int row, int column, String text);

    //-----------------------------------------------------------------------------------

    static void customize(TableColumn column, String header, int width) {
        column.setMinWidth(width);
        column.setMaxWidth(width);
        column.setHeaderRenderer(new HeaderCellRenderer());
        column.setCellRenderer(new LabelCellRenderer());
        column.setHeaderValue(header);
        column.setResizable(false);
    }

    static Integer integerValue(String text) {
        return text == null || text.trim().isEmpty() ? null : Integer.parseInt(text);
    }

}
