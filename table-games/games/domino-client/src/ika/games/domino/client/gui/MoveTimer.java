package ika.games.domino.client.gui;

import javax.swing.*;
import java.awt.*;

class MoveTimer extends JComponent {

    private int rest, total;

    public MoveTimer() {
        setSize(70, 20);
        setBackground(Color.gray);
        new Timer(1000, (e) -> {
            if (rest != -1) {
                rest--;
                repaint();
            }
        }).start();
    }

    public void update(int rest, int total) {
        this.rest = rest;
        this.total = total;
    }

    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
        if (rest == -1) {
            return;
        }
        int w = getWidth();
        int h = getHeight();
        ((Graphics2D) g).setStroke(new BasicStroke(2));
        g.setColor(Color.black);
        g.drawRect(4, 4, w - 8, h - 8);
        g.setColor(Color.green);
        g.fillRect(5, 5, (w - 10) * rest / total, h - 10);
    }

}
