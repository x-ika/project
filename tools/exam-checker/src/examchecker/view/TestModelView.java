package examchecker.view;

import examchecker.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TestModelView extends TableView<TestModel> {

    public void updateTable() {
        tableModel.setRowCount(0);
        for (int i = 0; i < model.getDivs().size(); i++) {
            addRow(i);
        }
    }

    protected void customizeTable() {
        tableModel = new DefaultTableModel(0, 3);
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
        customize(table.getColumnModel().getColumn(index++), "Number", 100);
        customize(table.getColumnModel().getColumn(index++), "Score", 100);
        customize(table.getColumnModel().getColumn(index++), "Correct Answer", 120);
    }

    private void addRow(int i) {

        // create table row
        Object[] row = new Object[table.getColumnCount()];
        int index = 0;
        TestDivModel div = model.getDivs().get(i);
        row[index++] = i + 1;
        row[index++] = div.getScore();
        row[index++] = div.getCorrectAnswer();

        tableModel.addRow(row);
    }

    protected void setValue(int row, int column, String text) {
        TestDivModel div = model.getDivs().get(row);
        try {
            int ind = 0;
            if (ind++ == column) {
            }
            if (ind++ == column) {
                div.setScore(integerValue(text));
            }
            if (ind++ == column) {
                div.setCorrectAnswer(text);
            }
            updateTable();
        } catch (Exception ex) {
            String msg = "The value you entered is not valid";
            JOptionPane.showMessageDialog(this, msg, "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

}
