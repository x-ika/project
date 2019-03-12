package brianbubs;

import com.simplejcode.commons.gui.Console;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

public class FaceBrianBubs {

    // application offset approximately
    private static final int X_OFF = 120;
    private static final int Y_OFF = 300;

    // rectangular area containing the whole grid and objects
    private static final int DX = 100;
    private static final int DY = 50;
    private static final int MAX_W = 660;
    private static final int MAX_H = 440;

    // need to detect some special objects
    private static final int MSK = (1 << 24) - 1;
    private static final int LINE = 115 << 16 | 190 << 8 | 25;
    private static final int YES = 125 << 16 | 177 << 8 | 1;
    private static final int NO = 204 << 16;

    // region which will be matched with background to find exact offset of the appliacation
    private static final int stx = 600;
    private static final int sty = 0;
    private static final int w = 100;
    private static final int h = 100;

    private int xoff, yoff, n, m;
    private int[] x, y;
    private int[][] p, q, c;

    private Console console;
    private BufferedImage bg;
    private Robot robot;

    public FaceBrianBubs() throws Exception {
        init();

//        console.readLine();
//        findBoard(bg.getRGB(stx, sty, w, h, null, 0, w), 5);
        xoff = 121;
        yoff = 301;

        console.readLine();
        console.dispose();
        start();
    }

    private void init() throws Exception {
        robot = new Robot();
        bg = ImageIO.read(new File("resources\\bg.png"));
        console = Console.createInstance();
        console.setLocation(900, 300);

        x = new int[22];
        y = new int[22];

        p = new int[MAX_W][MAX_H];
        q = new int[MAX_W][MAX_H];
        c = new int[MAX_W][MAX_H];

        for (int i = 0; i < MAX_W; i++) {
            for (int j = 0; j < MAX_H; j++) {
                p[i][j] = bg.getRGB(DX + i, DY + j) & MSK;
            }
        }
    }

    private void start() {

        long startTime = System.nanoTime();
        while (System.nanoTime() - startTime < 1e12) {

            findGrid();
            markObjects();

            while (!findAnswer()) {
                robot.delay(30);
            }
            while (findAnswer()) {
                robot.delay(30);
            }
            robot.delay(250);

        }

    }

    private void findBoard(int[] pixels, int d) {

        BufferedImage ci = robot.createScreenCapture(new Rectangle(X_OFF + stx - d, Y_OFF + sty - d, w + 2 * d, h + 2 * d));

        double mindst = 1e9;
        for (int x = 0; x < 2 * d; x++) {
            for (int y = 0; y < 2 * d; y++) {

                double dist = 0;
                for (int i = 0; i < w; i++) {
                    for (int j = 0; j < h; j++) {
                        dist += distance(pixels[j * w + i], ci.getRGB(x + i, y + j));
                    }
                }
                dist /= w * h;
                if (mindst > dist) {
                    mindst = dist;
                    xoff = X_OFF + x - d;
                    yoff = Y_OFF + y - d;
                }
            }
        }
        if (mindst > 10) {
            findBoard(pixels, d);
            return;
        }
    }

    private void findGrid() {
        BufferedImage ci = robot.createScreenCapture(new Rectangle(xoff + DX, yoff + DY, MAX_W, MAX_H));
        for (int i = 0; i < MAX_W; i++) {
            for (int j = 0; j < MAX_H; j++) {
                c[i][j] = distance(LINE, q[i][j] = ci.getRGB(i, j) & MSK) < 500 ? 1 : 0;
            }
        }
        precalculate(c);
        n = m = -1;
        for (int i = 0; i < MAX_W; i++) {
            if (get(i, 0, 1, MAX_H) > 150) {
                x[++n] = i;
                i += 50;
            }
        }
        for (int i = 0; i < MAX_H; i++) {
            if (get(0, i, MAX_W, 1) > 150) {
                y[++m] = i;
                i += 50;
            }
        }
        System.out.println(Arrays.toString(Arrays.copyOf(x, n + 1)));
        System.out.println(Arrays.toString(Arrays.copyOf(y, m + 1)));
        System.out.println("");
    }

    private void markObjects() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                int s = 0;
                for (int a = x[i] + 5; a < x[i + 1] - 5; a++) {
                    for (int b = y[j] + 5; b < y[j + 1] - 5; b++) {
                        s += distance(p[a][b], q[a][b]);
                    }
                }
                if (s > 1e3 * (x[i + 1] - x[i] - 10) * (y[j + 1] - y[j] - 10)) {
                    click(x[i] + x[i + 1] >> 1, y[j] + y[j + 1] >> 1);
                }
            }
        }
        robot.mouseMove(10, 10);
    }

    private boolean findAnswer() {
        BufferedImage bi = robot.createScreenCapture(new Rectangle(xoff + 320, yoff + 240, 115, 115));
        for (int i = 0, k = 0; i < bi.getWidth(); i++) {
            for (int j = 0; j < bi.getHeight(); j++) {
                int c = bi.getRGB(i, j) & MSK;
                if ((distance(c, NO) < 500 || distance(c, YES) < 500) && ++k > 1000) {
                    return true;
                }
            }
        }
        return false;
    }

    private void click(int x, int y) {
        robot.mouseMove(xoff + DX + x, yoff + DY + y);
        robot.mousePress(16);
        robot.mouseRelease(16);
    }

    private int distance(int c1, int c2) {
        int dr = (c1 >> 16) - (c2 >> 16);
        int dg = (c1 >> 8 & 255) - (c2 >> 8 & 255);
        int db = (c1 & 255) - (c2 & 255);
        return dr * dr + dg * dg + db * db;
    }

    //-----------------------------------------------------------------------------------

    private static final int[][] F = new int[999][999];
    private static final int[][] G = new int[999][999];

    public static void precalculate(int[][] f) {
        for (int i = 0; i < f.length; i++) {
            for (int j = 0; j < f[0].length; j++) {
                G[i + 1][j] = G[i][j] + f[i][j];
            }
        }
        for (int i = 0; i <= f.length; i++) {
            for (int j = 0; j < f[0].length; j++) {
                F[i][j + 1] = F[i][j] + G[i][j];
            }
        }
    }

    public static int get(int i, int j, int w, int h) {
        return F[i + w][j + h] + F[i][j] - F[i][j + h] - F[i + w][j];
    }

    //-----------------------------------------------------------------------------------

    public static void main(String[] args) throws Exception {
        new FaceBrianBubs();
    }
}
