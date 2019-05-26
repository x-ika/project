package pool.gui;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private final Color background = new Color(32, 100, 128);

    public GamePanel(Component pt, JProgressBar pb, JPanel p1, JPanel p2) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 1000, 50));
        setBackground(background);
        pt.setBackground(background);
        add(createToolBar(p1, p2, pb), BorderLayout.NORTH);
        add(pt, BorderLayout.CENTER);
    }

    private JPanel createToolBar(JPanel first, JPanel second, JProgressBar middle) {
        JPanel myToolBar = new JPanel();
        myToolBar.setBackground(background);
        myToolBar.add(first);
        myToolBar.add(middle);
        myToolBar.add(second);
        return myToolBar;
    }
}
