package ika.games.domino.client.gui;

import javax.swing.*;
import java.awt.*;

class UsernameAndScore extends JComponent {

    private String username;
    private int score;

    public UsernameAndScore() {
        setSize(70, 40);
    }

    public void update(String username, int score) {
        this.username = username;
        this.score = score;
        repaint();
    }

    protected void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
        if (username == null || username.isEmpty()) {
            return;
        }
        g.setColor(Color.black);
        g.setFont(new Font("Serif", Font.BOLD, 20));
        g.drawString("" + score, 5, 15);
        g.drawString(username, 5, 35);
    }

}
