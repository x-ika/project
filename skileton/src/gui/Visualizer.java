package gui;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;

/**
 * This class is used to show on screen image, represented as array of pixels.
 */
public class Visualizer extends JPanel {
    protected BufferedImage image;

    public Visualizer(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
    }

    public void show(int pixels[]) {
        image.setRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
        if (!isVisible()) {
            setVisible(true);
        }
        repaint();
    }

    public void getImage(int[] pixels) {
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
    }

    public BufferedImage getImage() {
        return image;
    }

    public Graphics2D graphics() {
        return image.createGraphics();
    }

    protected void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, this);
    }

}
