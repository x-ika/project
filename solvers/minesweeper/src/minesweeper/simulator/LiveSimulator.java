package minesweeper.simulator;

import com.simplejcode.commons.gui.GraphicUtils;
import com.simplejcode.commons.misc.util.ThreadUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class LiveSimulator extends AbstractSimulator {

//    public static final int SCREEN_OFF_X = 650;
//    public static final int SCREEN_OFF_Y = 250;

    private Robot robot;

    private BufferedImage screen;
    private BufferedImage cell;
    private BufferedImage pattern;

    private int boardX, boardY, cellWidth, cellHeight;

    public void init() {
        try {

            robot = new Robot();
            cell = ImageIO.read(new File("resources/ms/cell.png"));
            pattern = ImageIO.read(new File("resources/ms/pattern.bmp"));
            cellWidth = cell.getWidth();
            cellHeight = cell.getHeight();

            M:
            while (true) {
                robot.delay(1000);
                screen = robot.createScreenCapture(new Rectangle(650, 250, 900, 500));
                for (int i = 0; i < 100; i++) {
                    for (int j = 50; j < 150; j++) {

                        if (matches(screen, i, j, cell, 0, 0)) {
                            boardX = i;
                            boardY = j;
                            break M;
                        }

                    }
                }
            }

            n = m = 0;
            while (matches(screen, boardX, boardY + n * cellHeight, cell, 0, 0)) {
                n++;
            }
            while (matches(screen, boardX + m * cellWidth, boardY, cell, 0, 0)) {
                m++;
            }
            boardX += 650;
            boardY += 250;

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
        System.out.println(String.format("Opening [%d, %d]", i, j));
        click(i, j);
        robot.delay(50);
        screen = robot.createScreenCapture(new Rectangle(boardX, boardY, m * cellWidth, n * cellHeight));
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
        robot.mouseMove(boardX + m * cellWidth / 2, boardY - 30);
        robot.mousePress(16);
        robot.mouseRelease(16);
    }

    //-----------------------------------------------------------------------------------

    private void click(int i, int j) {
        robot.mouseMove(boardX + j * cellWidth + cellWidth / 2, boardY + i * cellHeight + cellHeight / 2);
        robot.mousePress(16);
        robot.mouseRelease(16);
        robot.mouseMove(boardX - 32, boardY - 32);
    }

    private void read(int i, int j) {
        if (matches(screen, j * cellWidth, i * cellHeight, pattern, 9 * cellWidth, 0)) {
            System.out.println(String.format("[%d, %d] identified as bomb", i, j));
            hasBomb[i][j] = true;
            return;
        }
        for (int t = 0; t < 7; t++) {
            if (matches(screen, j * cellWidth, i * cellHeight, pattern, t * cellWidth, 0)) {
                numberOfNeighboors[i][j] = t;
                System.out.println(String.format("[%d, %d] identified as %d", i, j, t));
                return;
            }
        }
        System.out.println(String.format("[%d, %d] not identified", i, j));
    }

    private boolean matches(BufferedImage screen, int xs, int ys, BufferedImage cell, int xc, int yc) {
        int dist = 0;
        for (int i = 0; i < cellWidth; i++) {
            for (int j = 0; j < cellHeight; j++) {
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

    private void debug(BufferedImage image) {
        GraphicUtils.showImage(image);
        ThreadUtils.sleep(100_000);

    }

}
