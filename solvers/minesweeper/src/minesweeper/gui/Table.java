package minesweeper.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import java.util.*;

public class Table extends JComponent implements MouseListener {

    public static double[][] mem;

    private Vector<TableClickListener> listeners = new Vector<>();

    private int cellSize;

    private int[][] desk;

    private Image deskImage;

    public Table(int cellSize) {
        this.cellSize = cellSize;
        addMouseListener(this);
    }

    //-----------------------------------------------------------------------------------
    // Public API

    public void init(int[][] desk) {
        this.desk = desk;
        listeners.clear();

        final int w = desk[0].length * cellSize + 1, h = desk.length * cellSize + 1;

        setPreferredSize(new Dimension(w, h));
        drawDesk(w, h);
        invalidate();
        repaint();
    }

    public void addListener(TableClickListener listener) {
        listeners.add(listener);
    }

    public void update() {
        repaint();
    }

    //-----------------------------------------------------------------------------------
    // Drawing

    private void drawDesk(int w, int h) {
        deskImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics g = deskImage.getGraphics();
        g.setColor(Color.gray);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.gray);
        for (int i = 0; i <= desk[0].length; i++) {
            g.drawLine(i * cellSize, 0, i * cellSize, h);
        }
        for (int i = 0; i <= desk.length; i++) {
            g.drawLine(0, i * cellSize, w, i * cellSize);
        }
        for (int i = 0; i < desk[0].length; i++) {
            for (int j = 0; j < desk.length; j++) {
                g.setColor(Color.white);
                g.fillRoundRect(i * cellSize + 1, j * cellSize + 1, cellSize - 2, cellSize - 2, 12, 12);
                for (int d = 1; d < 4; d++) {
                    int v = (int) (64 + 192 * Math.pow(2d * d / cellSize, 0.3));
                    g.setColor(new Color(v, v, v));
                    g.drawRoundRect(i * cellSize + d, j * cellSize + d, cellSize - 2 * d, cellSize - 2 * d, 12, 12);
                }
            }
        }
    }

    protected void paintComponent(Graphics g) {
        g.drawImage(deskImage, 0, 0, this);
        if (desk == null) {
            return; // nothing more to paint
        }

        g.setFont(new Font("Serif", Font.BOLD, 30));
        FontMetrics metrics = g.getFontMetrics();
        int h = (cellSize + 15) / 2;
        int w = (cellSize - metrics.getWidths()['X']) / 2;

        for (int i = 0; i < desk.length; i++) {
            for (int j = 0; j < desk[0].length; j++) {
                if (desk[i][j] != -1) {
                    g.setFont(new Font("Serif", Font.BOLD, 20));
                    g.drawString(String.valueOf(desk[i][j]), j * cellSize + w, i * cellSize + h);
                }
                g.setFont(new Font("Serif", Font.BOLD, 12));
                g.drawString(String.format("%.3f", mem[i][j]), j * cellSize + 5, i * cellSize + h + 10);
            }
        }
    }

    /**
     * MouseListener
     */

    public synchronized void mousePressed(MouseEvent e) {
        int row = e.getY() / cellSize;
        int col = e.getX() / cellSize;

        for (TableClickListener listener : listeners) {
            listener.mouseClicked(row, col);
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    //-----------------------------------------------------------------------------------

    public Container createContentPane() {
        Color bgColor = Color.blue;

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        //panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(bgColor);
        panel.setPreferredSize(new Dimension(800, 600));
        panel.add(this);
        return panel;
    }

}
