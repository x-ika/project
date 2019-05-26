package pool.gui.primitive;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LabelAndField extends JPanel {
    private JLabel label;

    private JTextField field;

    public LabelAndField(String text, ImageIcon image, int columns, boolean passwordFlag, Color bgColor) {
        label = new JLabel(text, image, JLabel.RIGHT);
        field = passwordFlag ? new JPasswordField(columns) : new JTextField(columns);
        setBackground(bgColor);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(5, 10, 5, 10));
        add(label, BorderLayout.WEST);
        add(Box.createHorizontalStrut(10));
        add(field, BorderLayout.EAST);
    }

    public LabelAndField(String text, ImageIcon image, int columns, boolean passwordFlag) {
        this(text, image, columns, passwordFlag, Color.white);
    }

    public LabelAndField(String text, int columns, boolean passwordFlag, Color bgColor) {
        this(text, null, columns, passwordFlag, bgColor);
    }

    public LabelAndField(String text, int columns, Color bgColor) {
        this(text, null, columns, false, bgColor);
    }

    public JTextField getField() {
        return field;
    }

    public JLabel getLabel() {
        return label;
    }

    public String getText() {
        return field.getText();
    }

    public void setText(String text) {
        field.setText(text);
    }
}
