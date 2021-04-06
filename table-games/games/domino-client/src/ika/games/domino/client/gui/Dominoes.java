package ika.games.domino.client.gui;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;

class Dominoes extends JComponent implements MouseListener {

    private boolean active;
    private int selectedInd;
    private List<Domino> list;

    public Dominoes() {
        list = new ArrayList<>();
    }

    public void update(int rot, int[] stones, int canMove) {
        active = canMove != 0;
        for (int i = 0; i < stones.length; i++) {
            if (list.size() <= i) {
                Domino e = new Domino(i);
                list.add(e);
                add(e);
                e.addMouseListener(this);
            }
            list.get(i).update(stones[i], rot, true, (canMove & 1 << i) != 0, false, false, 1);
        }
        while (list.size() > stones.length) {
            remove(list.remove(list.size() - 1));
        }
        selectedInd = -1;

        int xoff = 0, yoff = 0;
        for (Domino domino : list) {
            domino.setLocation(xoff, yoff);
            if (rot % 2 == 0) {
                xoff += domino.getWidth();
            } else {
                yoff += domino.getHeight();
            }
        }
        if (!list.isEmpty()) {
            Domino d = list.get(0);
            int w = d.getWidth() * (rot % 2 == 0 ? list.size() : 1);
            int h = d.getHeight() * (rot % 2 == 1 ? list.size() : 1);
            setSize(w, h);
        }

        repaint();
    }

    public int getSelectedInd() {
        return selectedInd;
    }

    public int getSelectedStone() {
        return list.get(selectedInd).stone;
    }

    //-----------------------------------------------------------------------------------

    public void mousePressed(MouseEvent e) {
        if (active && e.getSource() instanceof Domino) {
            Domino d = (Domino) e.getSource();
            if (!d.light) {
                return;
            }
            if (selectedInd != -1) {
                list.get(selectedInd).selected = false;
                list.get(selectedInd).repaint();
            }
            selectedInd = d.index;
            d.selected = true;
            d.repaint();
            ((DominoRoom) getParent().getParent()).updateBoard();
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

}
