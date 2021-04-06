import com.simplejcode.commons.gui.*;
import com.simplejcode.commons.misc.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

public class Loto extends JPanel {

    private static class Cell {
        int x;
        int y;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private class CellRenderer extends JPanel implements TableCellRenderer {

        private JLabel label = new JLabel();

        public CellRenderer() {
            add(label);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       final int row, int column)
        {
            setBackground(rowColors[row]);
            label.setBackground(getBackground());
            if (column == 0) {
                setBorder(BorderFactory.createEmptyBorder(5, 15, 0, 0));
                setLayout(new BorderLayout());
                add(label);
            } else {
                setBorder(BorderFactory.createEmptyBorder());
            }
            if (value != null) {
                if (value instanceof Cell) {
                    Cell cell = (Cell) value;
                    int num = cell.x;
                    int time = cell.y;

                    String text;
                    if (num == -1) {
                        JButton button = new JButton("View");
                        button.setFont(new Font("Serif", Font.BOLD, 12));
                        button.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                new NumbersDialog(frame, data.getRow(row));
                            }
                        });
                        buttons[row] = button;
                        return button;
                    }
                    text = num + " (" + time + ")";
                    label.setText(text);
                } else {
                    if (column == 2) {
                        label.setForeground(value.equals("1") ? Color.red : new Color(0, 128, 0));
                    }
                    label.setText(value.toString());
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

    //-----------------------------------------------------------------------------------

    private CustomFrame frame;

    private JTable table;
    private JScrollPane scrollPane;
    private DefaultTableModel model;
    private Model data = new Model();

    private Color[] rowColors = new Color[1 << 16];
    public JButton[] buttons = new JButton[1 << 16];

    private Loto() {
        setPreferredSize(new Dimension(900, 600));
        frame = new CustomFrame("Loto");
        frame.setContentPane(this);
        frame.setVisible(true);

        frame.setJMenuBar(
                new String[][]{
                        {"File", "Start New Simulation", null, "Exit"},
                        {"Help", "About"}
                },
                this,
                ReflectionUtils.getMethod("handle", Throwable.class),

                ReflectionUtils.getMethod("actionOnStartSimulation"),
                ReflectionUtils.getMethod("actionOnExit"),

                ReflectionUtils.getMethod("actionOnAbout")
        );

        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                updateAll();
            }
        });
    }

    public void createContent() {
        removeAll();

        model = new DefaultTableModel(0, data.getK() + 3);
        table = new JTable(model) {
            public boolean isCellEditable(int row, int column) {
                return column == 1 && row < getRowCount() - 1 ||
                        column == getColumnCount() - 1 && data.getNumOfRareNums(row) > data.getK();
            }

            public void setValueAt(Object aValue, int row, int column) {
                setValue(row, aValue.toString());
            }
        };
        table.setRowHeight(30);
        table.setRowMargin(0);
        table.getColumnModel().setColumnMargin(0);


        int index = 0;
        customize(table.getColumnModel().getColumn(index++), "Time", 60);
        customize(table.getColumnModel().getColumn(index++), "Number", 60);
        customize(table.getColumnModel().getColumn(index++), "Last Mod.", 80);
        for (int i = 0; i < data.getK(); i++) {
            customize(table.getColumnModel().getColumn(index++), String.valueOf(i + 1), 70);
        }
        table.getColumnModel().getColumn(data.getK() + 2).setCellEditor(new ButtonEditor(this));

//        table.setEnabled(false);
//        table.setShowGrid(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        table.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));

        JComponent tableContainer = new Box(BoxLayout.Y_AXIS);
        tableContainer.add(table.getTableHeader());
        tableContainer.add(table);

        scrollPane = new JScrollPane(tableContainer,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);


        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(scrollPane);
        panel.add(create("New Number", new JTextField(5)), BorderLayout.SOUTH);
        add(panel);
        updateAll();
    }

    private JPanel create(String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setDisplayedMnemonic(labelText.charAt(0));
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.add(label);
        panel.add(field);
        field.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && setValue(data.getSize(), field.getText())) {
                    field.setText("");
                }
            }
        });
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
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

    private void updateAll() {
        if (getComponentCount() == 0) {
            return;
        }
        getComponent(0).setPreferredSize(new Dimension(frame.getWidth() - 20, frame.getHeight() - 60));
        table.invalidate();
        validate();
    }

    //-----------------------------------------------------------------------------------

    private boolean setValue(int row, String text) {
        try {
            int num = Integer.parseInt(text);
            if (1 <= num && num <= data.getN()) {
                if (row == data.getSize()) {
                    data.add(num);
                } else {
                    data.replace(row, num);
                }
                return true;
            } else {
                String msg = String.format("Entered number should be between 1 and %d inclusive!", data.getN());
                JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            String msg = "Enter the number!";
            JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
        }
        return false;
    }

    public void updateTable() {
        EventQueue.invokeLater(ThreadUtils.createThread(() -> {
            int pos = scrollPane.getVerticalScrollBar().getValue();
            model.setRowCount(0);
            Color current = new Color(208, 240, 255);
            for (int i = 0; i < data.getSize(); i++) {
                addRow(i, current);
            }
            scrollPane.getVerticalScrollBar().setValue(pos);
        }));
    }

    public void rowAdded() {
        EventQueue.invokeLater(ThreadUtils.createThread(() -> {
            int pos = scrollPane.getVerticalScrollBar().getValue();
            Color current = new Color(208, 240, 255);
            addRow(model.getRowCount(), current);
            scrollPane.getVerticalScrollBar().setValue(pos);
        }));

    }

    private void addRow(int i, Color current) {
        rowColors[i] = i % 2 == 0 ? current : darker(current, 8);

        // create table row
        Object[] row = new Object[table.getColumnCount()];
        int index = 0;
        row[index++] = String.valueOf(i);
        row[index++] = String.valueOf(data.getNumber(i));
        row[index++] = String.valueOf(data.getIdleTime(i));
        for (int j = 0; j < data.getK(); j++) {
            row[index++] = new Cell(data.getRareNumber(i, j), data.getTimePassed(i, j));
        }
        if (data.getNumOfRareNums(i) > data.getK()) {
            row[index - 1] = new Cell(-1, -1);
        }

        model.addRow(row);
    }

    public void setMax() {
        GraphicUtils.pause(200);
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        validate();
    }

    private static Color darker(Color c, int d) {
        return new Color(c.getRed() - d, c.getGreen() - d, c.getBlue() - d);
    }

    //-----------------------------------------------------------------------------------

    private NewSimulationForm form;

    public void actionOnStartSimulation() {

        form = new NewSimulationForm(
                GraphicUtils.createAction(
                        "OK",
                        this,
                        ReflectionUtils.getMethod("startSimulation")),

                GraphicUtils.createAction(
                        "Cancel",
                        this,
                        ReflectionUtils.getMethod("cancel")));

    }

    public void startSimulation() {
        try {
            data.setN(Integer.parseInt(form.getN()));
            data.setK(Integer.parseInt(form.getK()));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Wrong format of the entered parameters!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        form.dispose();
        createContent();
        data.clear();
    }

    public void cancel() {
        form.dispose();
    }


    public void actionOnExit() {
        System.exit(0);
    }

    public void actionOnAbout() throws Exception {
        AboutDialogPanel aboutDialogPanel = new AboutDialogPanel(
                "XLoto", ImageIO.read(FileSystemUtils.getResource("resources/logo.jpg")));
        aboutDialogPanel.init();
        aboutDialogPanel.showDialog(frame);
    }

    public void handle(Throwable e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(frame, "Error occurred when processing your command!", "Error", JOptionPane.ERROR_MESSAGE);
    }


    public static Loto instance;

    public static void main(String[] args) {
        instance = new Loto();
    }
}
