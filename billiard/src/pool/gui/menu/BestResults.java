package pool.gui.menu;

import pool.utils.Consts;
import pool.logic.Player;
import pool.gui.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class BestResults extends BasicDialog {
    private static int MAX_SIZE = 10;

    public BestResults(MainFrame owner) {
        super(owner, "Best Results");
        setVisible(true);
    }

    protected void setContent() {
        addTable(readRecords());
    }

    private class Record {
        private String name;
        private int time;

        private Record(String n, int t) {
            name = n;
            time = t;
        }

        public String toString() {
            return "\n" + name + "\n" + time + "\n";
        }
    }

    public void addNewPlayer(Player player) {
        Vector<Record> records = readRecords();
        addNewRecord(records, player.info.getTime(), player.info.getName());
        writeRecords(records);
        addTable(records);
    }

    private Vector<Record> readRecords() {
        Vector<Record> records = new Vector<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(Consts.DOCS_PATH + "results.txt")));
            while (br.readLine() != null) {
                records.add(new Record(br.readLine(), Integer.parseInt(br.readLine())));
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    private void addNewRecord(Vector<Record> records, int newTime, String newName) {
        int indexForNewRecord = 0;
        while (indexForNewRecord < records.size() && records.get(indexForNewRecord).time <= newTime) {
            indexForNewRecord++;
        }
        if (indexForNewRecord < MAX_SIZE) {
            records.insertElementAt(new Record(newName, newTime), indexForNewRecord);
            if (records.size() > MAX_SIZE) {
                records.removeElementAt(MAX_SIZE);
            }
        }
    }

    private void writeRecords(Vector<Record> records) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(Consts.DOCS_PATH + "results.txt")));
            for (Record record : records) {
                bw.write(record.toString());
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addTable(Vector<Record> records) {
        if (records.isEmpty()) {
            return;
        }
        String[][] recs = new String[records.size()][2];
        for (int index = 0; index < records.size(); index++) {
            Record record = records.get(index);
            recs[index][0] = record.name;
            recs[index][1] = String.valueOf(record.time);
        }
        JTable table = new JTable(recs, new String[]{"Name", "Result"});
        table.setEnabled(false);
        table.setFont(new Font("Serif", Font.BOLD, 20));
        table.getColumnModel().setColumnMargin(100);
        table.setRowHeight(50);
        table.setIntercellSpacing(new Dimension(10, 10));
        table.setShowVerticalLines(true);
        getContentPane().add(table);
        repaint();
    }

    public static void setMAX_SIZE(int size) {
        MAX_SIZE = size;
    }
}
