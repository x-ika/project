package examchecker.view;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

class HeaderCellRenderer implements TableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column)
    {

        JPanel panel = new JPanel();
        panel.setBackground(Color.white);

        JLabel label = new JLabel();
        label.setFont(label.getFont().deriveFont(Font.BOLD, 15));

        label.setText(value.toString());
        panel.add(label);
        return panel;
    }

}
