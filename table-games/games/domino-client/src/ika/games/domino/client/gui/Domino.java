package ika.games.domino.client.gui;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.*;

class Domino extends JComponent {

    int stone = -2, rot, index;
    double s;
    BufferedImage image, dark, pos;
    boolean rich, light, selected, blue;

    public Domino(int index) {
        this.index = index;
    }

    void update(int stone, int rot, boolean rich, boolean light, boolean selected, boolean blue, double s) {
        if (this.stone != stone || this.rot != rot) {
            this.stone = stone;
            this.rot = rot;
            image = DominoRoom.getImage(stone, rot);
            dark = DominoRoom.adjustRGB(image, 80 << 16 | 80 << 8 | 80);
            pos = DominoRoom.adjustRGB(image, 10 << 16 | 10 << 8 | 128);
        }
        this.rich = rich;
        this.light = light;
        this.selected = selected;
        this.blue = blue;
        this.s = s;
        int t = rich ? 5 : 0;
        setSize((int) (s * (image.getWidth() + t)), (int) (s * (image.getHeight() + t)));
        setCursor(Cursor.getPredefinedCursor(light || blue ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
        repaint();
    }

    protected void paintComponent(Graphics g) {

        if (image == null) {
            return;
        }
        if (selected) {
            ((Graphics2D) g).setStroke(new BasicStroke(5));
            g.setColor(Color.red);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
        }
        ((Graphics2D) g).scale(s, s);
        int off = rich ? 2 : 0;
        if (light) {
            g.drawImage(image, off, off, this);
        } else if (blue) {
            g.drawImage(pos, off, off, this);
        } else {
            g.drawImage(dark, off, off, this);
        }

    }

}
