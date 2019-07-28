import com.simplejcode.commons.gui.GraphicUtils;

import javax.swing.*;
import java.awt.*;

public class NumbersDialog extends JDialog {

    public NumbersDialog(Frame owner, int[][] numbers) {
        super(owner, "Numbers that do not appear", true);
        setSize(500, 30 + 30 * ((numbers[0].length + 9) / 10));
        setContentPane(new JPanel() {

            public void paint(Graphics g) {
                super.paint(g);

                g.setFont(new Font("Serif", Font.PLAIN, 20));

                int n = numbers[0].length;

                int y = 20;
                for (int i = 0; i < n; i += 10) {
                    String s = "";
                    for (int j = i; j < n && j < i + 10; j++) {
                        s += numbers[0][j] + (j == n - 1 ? "." : ", ");
                    }
                    g.drawString(s, 20, y);
                    y += 30;
                }

            }
        });
        GraphicUtils.centerOnScreen(this);
        setVisible(true);
    }

}
