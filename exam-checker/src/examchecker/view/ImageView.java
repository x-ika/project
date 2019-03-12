package examchecker.view;

import javax.swing.*;
import java.awt.*;

public class ImageView extends JFrame {

    public ImageView(Image image) {
        super("Visualization");

        JComponent imgComponent = new JComponent() {
            protected void paintComponent(Graphics g) {
                g.drawImage(image, 0, 0, this);
            }
        };
        imgComponent.setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(imgComponent));

        setContentPane(panel);
        pack();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

    }

}
