package ika.games.domino.client.gui;

import java.awt.*;
import javax.swing.*;

class PlayerArea extends JComponent {

    private int x, y, rot;

    private UsernameAndScore user;
    private Dominoes dominoes;
    private MoveTimer timer;

    public PlayerArea() {
        setBackground(Color.orange);
        user = new UsernameAndScore();
        dominoes = new Dominoes();
        timer = new MoveTimer();
        timer.update(-1, 0);
        add(user);
        add(dominoes);
        add(timer);
    }

    public void update(String username, int score, int rest, int total, int[] stones, int canMove) {
        user.update(username, score);
        dominoes.update(rot, stones, canMove);
        timer.update(rest, total);
        align();
        repaint();
    }

    public void setCenterAndOrientation(int x, int y, int rot) {
        this.x = x;
        this.y = y;
        this.rot = rot;
        align();
    }

    private void align() {
        int h = 0, w = 0;

        user.setLocation(0, h);
        h += user.getHeight();
        w = Math.max(w, user.getWidth());

        dominoes.setLocation(0, h);
        h += dominoes.getHeight();
        w = Math.max(w, dominoes.getWidth());

        timer.setLocation(0, h);
        h += timer.getHeight();
        w = Math.max(w, timer.getWidth());

        setSize(w, h);
        setLocation(x - w / 2, y - h / 2);
    }

    public int getSelectedInd() {
        return dominoes.getSelectedInd();
    }

    public int getSelectedStone() {
        return dominoes.getSelectedStone();
    }

}
