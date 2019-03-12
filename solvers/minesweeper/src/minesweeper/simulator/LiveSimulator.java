package minesweeper.simulator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class LiveSimulator extends AbstractSimulator {
    private Robot robot;

    private BufferedImage screen;
    private BufferedImage cell;
    private BufferedImage pattern;

    private int x, y, w, h;

    public void init() {
        try {

            robot = new Robot();
            cell = ImageIO.read(new File("resources/ms/cell.jpg"));
            pattern = ImageIO.read(new File("resources/ms/pattern.bmp"));
            w = cell.getWidth();
            h = cell.getHeight();

            M:
            while (true) {
                robot.delay(1000);
                screen = robot.createScreenCapture(new Rectangle(0, 0, 600, 400));
                for (int i = 0; i < 100; i++) {
                    for (int j = 50; j < 150; j++) {

                        if (matches(screen, i, j, cell, 0, 0)) {
                            x = i;
                            y = j;
                            break M;
                        }

                    }
                }
            }

            n = m = 0;
            while (matches(screen, x, y + n * h, cell, 0, 0)) {
                n++;
            }
            while (matches(screen, x + m * w, y, cell, 0, 0)) {
                m++;
            }

            if (n == 9 && m == 9) {
                k = 10;
            }
            if (n == 16 && m == 16) {
                k = 40;
            }
            if (n == 16 && m == 30) {
                k = 99;
            }

            super.init();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openCell(int i, int j) {
        click(i, j);
        robot.delay(5);
        screen = robot.createScreenCapture(new Rectangle(x, y, m * w, n * h));
        go(i, j);
    }

    private void go(int i, int j) {
        read(i, j);
        if (hasBomb[i][j]) {
            visiblePart[i][j] = 13;
            return;
        }

        visiblePart[i][j] = numberOfNeighboors[i][j];
        opened++;

        if (numberOfNeighboors[i][j] > 0) {
            return;
        }

        for (int a = Math.max(0, i - 1); a < Math.min(n, i + 2); a++) {
            for (int b = Math.max(0, j - 1); b < Math.min(m, j + 2); b++) {
                if (visiblePart[a][b] == -1) {
                    go(a, b);
                }
            }
        }
    }

    public void finish() {
        robot.mouseMove(x + m * w / 2, y - 30);
        robot.mousePress(16);
        robot.mouseRelease(16);
    }

    //-----------------------------------------------------------------------------------

    private void click(int i, int j) {
        robot.mouseMove(x + j * w + w / 2, y + i * h + h / 2);
        robot.mousePress(16);
        robot.mouseRelease(16);
//        robot.delay(16);
//        robot.mousePress(16);
//        robot.mouseRelease(16);
        robot.mouseMove(x - 32, y - 32);
    }

    private void read(int i, int j) {
        if (matches(screen, j * w, i * h, pattern, 9 * w, 0)) {
            hasBomb[i][j] = true;
            return;
        }
        for (int t = 0; t < 7; t++) {
            if (matches(screen, j * w, i * h, pattern, t * w, 0)) {
                numberOfNeighboors[i][j] = t;
                return;
            }
        }
    }

    private boolean matches(BufferedImage screen, int xs, int ys, BufferedImage cell, int xc, int yc) {
        int dist = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                dist += distance(screen.getRGB(xs + i, ys + j), cell.getRGB(xc + i, yc + j));
            }
        }
        return dist < 3e6;
    }

    private static int distance(int c1, int c2) {
        int dr = (c1 >> 16) - (c2 >> 16);
        int dg = (c1 >> 8 & 255) - (c2 >> 8 & 255);
        int db = (c1 & 255) - (c2 & 255);
        return dr * dr + dg * dg + db * db;
    }

}
