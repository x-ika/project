package pool.utils;

import pool.utils.Consts;

import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.*;

public final class ImageFactory {
    private static final int GREEN = -16744448;

    public static BufferedImage desk;

    public static BufferedImage cue;

    public static BufferedImage cursor;

    public static BufferedImage[] balls;

    public static ImageIcon colourBall;

    public static ImageIcon stripedBall;

    public static void createImages() {
        desk = filter(readImage("desk.png"), -1);
        cursor = filter(readImage("cursor.png"), GREEN);
        cue = filter(readImage("cue.png"), GREEN);
        balls = new BufferedImage[Consts.N_BALLS];
        for (int num = 0; num < balls.length; num++) {
            balls[num] = filter(readImage("ball" + num + ".png"), GREEN);
        }
        colourBall = new ImageIcon(Consts.IMAGE_PATH + "ball1.png");
        stripedBall = new ImageIcon(Consts.IMAGE_PATH + "ball9.png");
    }

    private static BufferedImage filter(BufferedImage source, int value) {
        BufferedImage image = new BufferedImage(
                source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < source.getWidth(); i++) {
            for (int j = 0; j < source.getHeight(); j++) {
                int rgb = source.getRGB(i, j);
                boolean needFiltering = rgb == value &&
                        (value != -1 || (i < 10 || i > 690) && (j < 10 || j >= 390));
                image.setRGB(i, j, needFiltering ? 0 : rgb);
            }
        }
        return image;
    }

    private static BufferedImage readImage(String fileName) {
        try {
            return ImageIO.read(new File(Consts.IMAGE_PATH + fileName));
        } catch (IOException e) {
            return null;
        }
    }
}
