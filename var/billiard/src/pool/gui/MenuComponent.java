package pool.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import pool.utils.SoundManager;

public class MenuComponent extends JComponent implements MouseListener {
    private static final Color ACTIVE_COLOR = new Color(255, 200, 0);

    private static final Color PASSIVE_COLOR = new Color(255, 150, 0);

    private boolean enable;

    private String label;

    private Color labelColor;

    private MainFrame mainFrame;

    MenuComponent(MainFrame main, String s, boolean enableFlag) {
        mainFrame = main;
        label = s;
        enable = enableFlag;
        labelColor = PASSIVE_COLOR;
        addMouseListener(this);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    MenuComponent(MainFrame main, String s) {
        this(main, s, true);
    }

    public void paint(Graphics g) {
        if (!enable) {
            return;
        }
        g.setColor(labelColor);
        g.setFont(new Font("Serif", Font.BOLD, 20));
        g.drawString(label, 0, 40);
    }

    public void setEnabled(boolean enableFlag) {
        enable = enableFlag;
    }

    public void mousePressed(MouseEvent e) {
        if (!enable) {
            return;
        }
        SoundManager.playZoom();
        mainFrame.onMenuClicked(label);
    }

    public void mouseEntered(MouseEvent e) {
        labelColor = ACTIVE_COLOR;
        repaint();
    }

    public void mouseExited(MouseEvent e) {
        labelColor = PASSIVE_COLOR;
        repaint();
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }
}
