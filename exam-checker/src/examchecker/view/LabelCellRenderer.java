package examchecker.view;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

class LabelCellRenderer implements TableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   final int row, int column)
    {

        JPanel panel = new JPanel();

        JLabel label = new JLabel();

        Color current = new Color(208, 240, 255);
        panel.setBackground(row % 2 == 0 ? current : darker(current, 8));
        label.setBackground(panel.getBackground());
        if (value != null) {
            label.setText(value.toString());
        }
        panel.add(label);
        return panel;
    }

    private static Color darker(Color c, int d) {
        return new Color(c.getRed() - d, c.getGreen() - d, c.getBlue() - d);
    }

}
