package pool.gui.menu;

import pool.utils.Consts;
import pool.gui.MainFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

public class About extends BasicDialog {
    private static BufferedImage content;

    public About(MainFrame mainFrame) {
        super(mainFrame, "About Program");
        setVisible(true);
    }

    public static void init() {
        content = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = content.createGraphics();
        g.setColor(new Color(150, 200, 255));
        g.fillRect(0, 0, 400, 120);
        g.setColor(Color.black);
        g.setFont(new Font("Curier", Font.PLAIN, 14));
        g.drawString("Program Version 1.0", 10, 20);
        g.drawString("© Irakli Merabishvili", 10, 50);
        g.drawString("All rights reserved.", 10, 100);

        Point p1 = new Point(0, 120);
        Point p2 = new Point(400, 130);

        Paint mem = g.getPaint();
        g.setPaint(new GradientPaint(p1, Color.yellow, p2, Color.blue));
        g.fillRect(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y);
        g.setPaint(mem);

        g.setColor(new Color(0xFFFAEBD7));
        final int x = 0, y = 130, w = 400, h = 170;
        g.fillRect(x, y, w, h);


        final String name = "POOL";
        g.setFont(new Font("Arial", Font.BOLD, 80));
        Rectangle2D r = g.getFontMetrics().getStringBounds(name, g);
        for (int i = 2; i < 3; i++) {
            g.setColor(new Color(0, 0, 0, 100));
            g.drawString(name, (int) (x + 2 * i - r.getX() + (w - r.getWidth()) / 2),
                    (int) (y + 3 * i - r.getY() + (h - r.getHeight()) / 2));
        }
        g.setColor(new Color(100, 0, 100));
        g.drawString(name, (int) (x - r.getX() + (w - r.getWidth()) / 2),
                (int) (y - r.getY() + (h - r.getHeight()) / 2));

        try {
            Image logo = ImageIO.read(new File(Consts.IMAGE_PATH + "logo.jpeg"));
            g.drawImage(logo, 200, 10, new Canvas());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void setContent() {
        setMinimumSize(new Dimension(400, 300));
        setContentPane(new JComponent() {
            public void paint(Graphics gr) {
                gr.drawImage(content, 0, 0, this);
            }
        });
    }
}
