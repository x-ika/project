package gui.primitive;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class JButtons extends JPanel {
    private JButton first;

    private JButton second;

    public JButtons(ActionListener al, String text1, String text2, Color bgColor) {
        first = new JButton(text1);
        second = new JButton(text2);
        first.addActionListener(al);
        second.addActionListener(al);
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setBackground(bgColor);
        panel.add(first);
        panel.add(second);

        setBackground(bgColor);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(new EmptyBorder(5, 10, 5, 10));
        for (int i = 0; i < 10; i++) {
            add(Box.createHorizontalGlue());
        }
        add(panel);
        for (int i = 0; i < 10; i++) {
            add(Box.createHorizontalGlue());
        }
    }

    public JButtons(ActionListener al, String text1, String text2) {
        this(al, text1, text2, Color.white);
    }

    public JButtons(ActionListener al) {
        this(al, "Ok", "Cancel", Color.white);
    }

    public JButton getFirst() {
        return first;
    }

    public JButton getSecond() {
        return second;
    }
}
