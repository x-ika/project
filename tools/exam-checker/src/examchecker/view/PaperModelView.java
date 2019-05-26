package examchecker.view;

import examchecker.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.stream.Collectors;

public class PaperModelView extends TableView<PaperModel> {

    public void updateTable() {
        tableModel.setRowCount(0);
        for (int i = 0; i < model.getDivs().size(); i++) {
            addRow(i);
        }
    }

    protected void customizeTable() {
        tableModel = new DefaultTableModel(0, 7);
        table = new JTable(tableModel) {
            public boolean isCellEditable(int row, int column) {
                return true;
            }

            public void setValueAt(Object value, int row, int column) {
                setValue(row, column, value.toString());
            }
        };

        table.setRowHeight(30);
        table.setRowMargin(0);
        table.getColumnModel().setColumnMargin(0);

        int index = 0;
        customize(table.getColumnModel().getColumn(index++), "Type", 120);
        customize(table.getColumnModel().getColumn(index++), "Length", 80);
        customize(table.getColumnModel().getColumn(index++), "Count", 80);
        customize(table.getColumnModel().getColumn(index++), "Tests in Column", 140);
        customize(table.getColumnModel().getColumn(index++), "Range Start", 100);
        customize(table.getColumnModel().getColumn(index++), "Order Number", 100);
        customize(table.getColumnModel().getColumn(index++), "Excel Header", 120);

        java.util.List<String> list = Arrays.stream(PaperDivType.values()).map(Enum::name).collect(Collectors.toList());
        table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(new JComboBox<>(new Vector<>(list))));
    }

    private void addRow(int i) {

        // create table row
        Object[] row = new Object[table.getColumnCount()];
        int index = 0;
        PaperDivModel div = model.getDivs().get(i);
        row[index++] = div.getType();
        row[index++] = div.getLength();
        row[index++] = div.getCount();
        row[index++] = div.getHeight();
        row[index++] = div.getRangeStart();
        row[index++] = div.getSequenceNumber();
        row[index++] = div.getExcelHeader();

        tableModel.addRow(row);
    }

    protected void setValue(int row, int column, String text) {
        PaperDivModel div = model.getDivs().get(row);
        try {
            int ind = 0;
            if (ind++ == column) {
                div.setType(PaperDivType.valueOf(text));
            }
            if (ind++ == column) {
                div.setLength(Integer.parseInt(text));
            }
            if (ind++ == column) {
                div.setCount(integerValue(text));
            }
            if (ind++ == column) {
                div.setHeight(integerValue(text));
            }
            if (ind++ == column) {
                div.setRangeStart(integerValue(text));
            }
            if (ind++ == column) {
                div.setSequenceNumber(integerValue(text));
            }
            if (ind++ == column) {
                div.setExcelHeader(text);
            }
            updateTable();
        } catch (Exception ex) {
            String msg = "The value you entered is not valid";
            JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

}
