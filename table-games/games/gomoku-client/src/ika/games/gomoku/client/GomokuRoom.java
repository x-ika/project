package ika.games.gomoku.client;

import com.simplejcode.commons.gui.GraphicUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

public class GomokuRoom extends JComponent implements ComponentListener {

    private GomokuTable table;

    public GomokuRoom(int cellSize) {
        setLayout(null);
        setPreferredSize(new Dimension(900, 600));
        setBackground(Color.orange);
        add(table = new GomokuTable(cellSize));
        addComponentListener(this);
    }

    public void update(int[][] desk, Move last, Move[] win) {
        table.update(desk, last, win);
        GraphicUtils.centerOnParent(table);
    }

    //-----------------------------------------------------------------------------------

    public void componentResized(ComponentEvent e) {
        GraphicUtils.centerOnParent(table);
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }

}
