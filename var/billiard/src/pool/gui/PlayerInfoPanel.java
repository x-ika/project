package pool.gui;

import pool.gui.primitive.LabelAndField;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class PlayerInfoPanel extends JPanel implements ActionListener {
    private static final int SECONDS_IN_MINUTE = 60;

    private static final int MILLIS_IN_SECOND = 1000;

    private int time;

    private Timer timer;

    private LabelAndField laf;

    public PlayerInfoPanel(String playerName, ImageIcon image) {
        time = 0;
        timer = new Timer(MILLIS_IN_SECOND, this);
        setBorder(new CompoundBorder(
                new SoftBevelBorder(BevelBorder.RAISED), new EmptyBorder(50, 50, 50, 50)));
        laf = new LabelAndField(playerName, image, 5, false);
        laf.setBackground(new Color(0, 128, 0));
        laf.getField().setFont(new Font("Curier", Font.BOLD, 20));
        laf.getLabel().setFont(new Font("Curier", Font.ITALIC, 20));
        laf.getField().setEnabled(false);
        add(laf);
        laf.setText("0 : 00");
    }

    public void paintComponent(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;
        g.setPaint(new GradientPaint(0, 0, Color.yellow, getWidth(), getHeight(), Color.blue));
        g.fillOval(0, 0, getWidth(), getHeight());
    }

    public void setText(String text) {
        laf.setText(text);
    }

    public void stopTimer() {
        timer.stop();
    }

    public void startTimer() {
        timer.start();
    }

    public boolean isMyTurne() {
        return timer.isRunning();
    }

    public int getTime() {
        return time;
    }

    public String getName() {
        return laf.getLabel().getText();
    }

    public void actionPerformed(ActionEvent e) {
        laf.setText(++time / SECONDS_IN_MINUTE + " : " +
                time % SECONDS_IN_MINUTE / 10 + time % 10);
    }
}
