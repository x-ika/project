package tetris;

import static java.lang.Math.*;

import com.simplejcode.commons.av.improc.ImageProcessor;
import com.simplejcode.commons.gui.Console;
import com.simplejcode.commons.misc.util.ThreadUtils;
import tetris.logic.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.File;
import java.util.Arrays;

public class VisualPort implements TetrisPort {

    public VisualPort() throws Exception {
        robot = new Robot();
        bgpixels = ImageIO.read(new File("resources/start.jpg")).getRGB(0, 0, BW, BH, null, 0, BW);
        start = ImageIO.read(new File("resources/bg.jpg")).getRGB(0, 0, BW, BH, null, 0, BW);
        norm(bgpixels);
        norm(start);
        console = Console.createInstance();
        console.setLocation(1200, 600);
        bgpixels = new int[BW * BH];
        capture = new int[BW * BH];
        board = new BitRectangle(C, new int[R]);
    }

    //-----------------------------------------------------------------------------------

    private Turn turn;
    private PortListener listener;
    private Console console;

    public void setListener(PortListener listener) {
        this.listener = listener;
    }

    public synchronized void apply(Turn turn) {
        this.turn = turn;
        notify();
    }

    public void start() {

        console.writeLine("to start fboard...");
        console.readLine();
        findBoard(start);
        console.writeLine("to start ?...");
        console.readLine();
        console.dispose();
        robot.delay(1000);

        ThreadUtils.executeInNewThread(() -> {
            synchronized (VisualPort.this) {
                while (true) {
                    turn = null;
                    getBoard();
                    getPile();
                    listener.eventOcuured(new PortEvent(1, board, pile));
                    while (turn == null) {
                        try {
                            wait();
                        } catch (InterruptedException ignore) {
                        }
                    }
                    int n = turn.commands.size(), j = n;
                    while (j > 0 && turn.commands.get(j - 1) == Command.DOWN) {
                        j--;
                    }
                    for (int i = 0; i < j; i++) {
                        doit(turn.commands.get(i));
                    }
                    if (j < n) {
//                        press(Command.DOWN.press);
//                        robot.delay(100 * (n - j));
//                        release(Command.DOWN.press);
                    }
                }
            }
        });

    }

    //-----------------------------------------------------------------------------------

    private static final int MSK = (1 << 24) - 1;
    private static final int DELAY = 50;

    // appliacation specific constants
    private static final int D = 32;
    private static final int R = 20;
    private static final int C = 10;
    private static final int BW = D * C;
    private static final int BH = D * R;


    // region which will be matched with background to find exact offset of the appliacation
    private static final int STX = 200;
    private static final int STY = 100;
    private static final int STW = 1000;
    private static final int STH = 1000;

    private int xoff, yoff;
    private int[] start, bgpixels, capture;
    private Robot robot;
    private BitRectangle board;
    private Pile pile;

    private void findBoard(int[] pattern) {
        BufferedImage ci = robot.createScreenCapture(new Rectangle(STX, STY, STW, STH));
        int[] cur = ci.getRGB(0, 0, STW, STH, null, 0, STW);
        norm(cur);

        double mindst = 1e9;
        mindst = search(pattern, cur, mindst, 20);
        mindst = search(pattern, cur, mindst, 5);
        mindst = search(pattern, cur, mindst, 1);
        console.writeLine(String.format("Board found %.3f", mindst));
    }

    private double search(int[] pattern, int[] cur, double mindst, int d) {
        int cc = 10;
        int minx = max(0, (xoff != 0 ? xoff : 0) - STX - cc * d);
        int maxx = min(STW - BW, (xoff != 0 ? xoff : 999) - STX + cc * d);
        int miny = max(0, (yoff != 0 ? yoff : 0) - STY - cc * d);
        int maxy = min(STH - BH, (yoff != 0 ? yoff : 999) - STY + cc * d);
        for (int x = minx; x <= maxx; x += d) {
            for (int y = miny; y <= maxy; y += d) {
                double dist = 0;
                for (int i = 0; i < BW; i++) {
                    for (int j = 0; j < BH; j++) {
                        dist += ImageProcessor.dist(pattern[j * BW + i], cur[(y + j) * STW + x + i]);
                    }
                }
                dist /= BW * BH;
                if (mindst > dist) {
                    mindst = dist;
                    xoff = STX + x;
                    yoff = STY + y;
                }
            }
        }
        return mindst;
    }

    private void getBoard() {
        int[] t = board.getBitsetPrepresentation();
        Arrays.fill(t, 0);
        robot.createScreenCapture(new Rectangle(xoff, yoff, BW, BH)).getRGB(0, 0, BW, BH, capture, 0, BW);
        norm(capture);
        for (int i = 0; i < R; i++) {
            for (int j = 0; j < C; j++) {
                int dst = 0;
                for (int a = 0; a < D; a++) {
                    for (int b = 0; b < D; b++) {
                        int x = j * D + b, y = i * D + a;
                        dst += ImageProcessor.dist(bgpixels[y * BW + x], capture[y * BW + x]);
                    }
                }
                if (dst > 13e3 * D * D) {
                    t[R - i - 1] |= 1 << j;
                }
            }
        }
    }

    private void getPile() {
        int[] t = board.getBitsetPrepresentation();
        int min = R, max = 0;
        for (int i = TetrisUtils.top(t); ++i < R; ) {
            if (t[i] != 0) {
                min = min(min, i);
                max = i;
            }
        }
        pile = null;
        if (min < R) {
            pile = new Pile(min, Arrays.copyOfRange(t, min, max + 1));
            Arrays.fill(t, min, max + 1, 0);
        }
    }

    private void doit(Command command) {
//        if (command.keep != 0) {
//            press(command.keep);
//        }
//        type(command.press);
//        if (command.keep != 0) {
//            release(command.keep);
//        }
    }

    private void type(int key) {
        press(key);
        release(key);
    }

    private void press(int key) {
        robot.keyPress(key);
        robot.delay(DELAY);
    }

    private void release(int key) {
        robot.keyRelease(key);
        robot.delay(DELAY);
    }

    private void norm(int[] p) {
        for (int i = 0; i < p.length; i++) {
            p[i] &= MSK;
        }
    }

}
