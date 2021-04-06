package ika.games.domino.client.gui;

import ika.games.domino.base.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Map;

class DominoTable extends JComponent implements MouseListener {

    private final DominoRoom room;
    private DominoTree tree;

    public DominoTable(DominoRoom room) {
        this.room = room;
        addMouseListener(this);
    }

    public DominoTree getTree() {
        return tree;
    }

    void update(DominoTree tree) {
        this.tree = tree;
        rearrangeDominoes();
        repaint();
    }

    private void rearrangeDominoes() {
        removeAll();

        BufferedImage original = DominoRoom.getImage(0);
        int[] sz = {original.getWidth(), original.getHeight()};

        Map<Node, int[]> d2 = DominoTopology.get2DRepresentation(tree, sz[0], sz[1]);
        int inf = (int) 1e9, minx = inf, maxx = -inf, miny = inf, maxy = -inf;
        for (int[] a : d2.values()) {
            maxx = Math.max(maxx, a[0] + sz[a[2] % 2]);
            minx = Math.min(minx, a[0]);
            maxy = Math.max(maxy, a[1] + sz[1 - a[2] % 2]);
            miny = Math.min(miny, a[1]);
        }
        double sx = 1d * (getWidth() - 10) / (maxx - minx);
        double sy = 1d * (getHeight() - 10) / (maxy - miny);
        double s = Math.min(1, Math.min(sx, sy));
        d2 = DominoTopology.get2DRepresentation(tree, (int) (s * sz[0]), (int) (s * sz[1]));

        int w = (int) (getWidth() / s);
        int h = (int) (getHeight() / s);

        int dx = (w - maxx + minx >> 1) - minx;
        int dy = (h - maxy + miny >> 1) - miny;
        dx *= s;
        dy *= s;

        for (Node node : d2.keySet()) {
            int[] a = d2.get(node);
            Domino domino = new Domino(0);
            domino.addMouseListener(this);
            domino.setLocation(dx + a[0], dy + a[1]);
            add(domino);
            domino.update(node.getStone(), a[2], false, false, false, room.isPossible(node.getStone()), s);
        }
    }

    protected void paintComponent(Graphics g) {
        if (tree == null) {
            return;
        }
        g.setColor(new Color(200, 150, 100));
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    //-----------------------------------------------------------------------------------

    public void mousePressed(MouseEvent e) {
        room.makeDominoMove(e.getSource() == this ? -1 : ((Domino) e.getSource()).stone);
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
