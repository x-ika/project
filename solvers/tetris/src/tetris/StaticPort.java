package tetris;

import com.simplejcode.commons.gui.GraphicUtils;
import com.simplejcode.commons.misc.util.ThreadUtils;
import tetris.logic.*;

import java.util.*;

public class StaticPort implements TetrisPort {

    private int delayBetweenTurns;
    private int delayBetweenCommands;
    private PortListener listener;

    public void setListener(PortListener listener) {
        this.listener = listener;
    }

    public synchronized void apply(Turn turn) {
        this.turn = turn;
        notify();
    }

    public StaticPort() {
        random = new Random();
    }

    private int score;
    private Random random;
    private BitRectangle board;
    private Turn turn;
    private TetrisVisualizer visualizer;

    public void setParams(long seed, boolean vis, int tdelay, int mdelay) {
        random.setSeed(seed);
        delayBetweenTurns = tdelay;
        delayBetweenCommands = mdelay;
        if (vis) {
            visualizer = new TetrisVisualizer();
        }
    }

    public void start() {

        score = 0;
        final int w = 10 + random.nextInt(5);
        final int h = 20 + random.nextInt(10);
        board = new BitRectangle(w, new int[h]);
//        int[] bb = new int[h];
//        int i = -3;
//        for (int[] possiblePile : POSSIBLE_PILES) {
//            TetrisUtils.xor(bb, possiblePile, i+=3, 2 + i%8);
//        }
//        visualizer = new TetrisVisualizer();
//        visualizer.setRectangle(new BitRectangle(12, bb), 0);
//        try {
//            Thread.sleep(1000000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        ThreadUtils.executeInNewThread(() -> {
            synchronized (StaticPort.this) {
                while (true) {
                    turn = null;
                    Pile pile = getPile();
                    listener.eventOcuured(new PortEvent(PortEvent.TYPE_MAKE_TURN, board, pile));
                    while (turn == null) {
                        try {
                            StaticPort.this.wait();
                        } catch (InterruptedException ignore) {
                        }
                    }
                    if (!apply(pile)) {
//                        score = 0;
                        break;
                    }
                    if (visualizer != null) {
                        visualizer.setRectangle(board, score);
                        GraphicUtils.pause(delayBetweenTurns);
                    }
                    if (TetrisUtils.top(board.getBitsetPrepresentation()) >= h - 5) {
                        break;
                    }
                }
                listener.eventOcuured(new PortEvent(PortEvent.TYPE_GAME_OVER, board, null));
            }
        });

    }

    private boolean apply(Pile pile) {
        int[] t = board.getBitsetPrepresentation();
        int[] pf = pile.getBitsetPrepresentation();
        int row = pile.row, col = pile.col;
        for (Command command : turn.commands) {
            switch (command) {
                case DOWN:
                    row--;
                    break;
                case LEFT:
                    col--;
                    break;
                case RIGHT:
                    col++;
                    break;
                case ROTATION:
                    col = 0;
                    pile = pile.rotate();
                    pf = pile.getBitsetPrepresentation();
                    break;
            }
            if (row < 0 || col < 0 || col > board.getWidth() - pile.getWidth()) {
                System.out.println("You are trying to move pile outside of the board");
                return false;
            }
            if (TetrisUtils.intersecs(t, pf, row, col)) {
                System.out.println("You are trying to put pile on the occupied region");
                return false;
            }
            if (visualizer != null) {
                TetrisUtils.xor(t, pf, row, col);
                visualizer.setRectangle(new BitRectangle(board.getWidth(), t.clone()), score);
                TetrisUtils.xor(t, pf, row, col);
                GraphicUtils.pause(delayBetweenCommands);
            }
        }
        if (!TetrisUtils.checkContact(t, pf, row, col)) {
            System.out.println("You should put pile on the ground");
            return false;
        }
        TetrisUtils.xor(t, pf, row, col);
        for (int i = 0; i < t.length; i++) {
            if (t[i] == (1 << board.getWidth()) - 1) {
                System.arraycopy(t, i + 1, t, i, t.length - i - 1);
                i--;
                score++;
            }
        }
        board = new BitRectangle(board.getWidth(), t);
        return true;
    }

    public int getScore() {
        return score;
    }

    private static final int[][] POSSIBLE_PILES = {
            {3, 3},  //block
            {15},    //brick
            {3, 6},  //zig-zag
            {6, 3},  //zag-zig
            {7, 1},  // L
            {7, 4},  // !L
            {7, 2},  // _|_
//            {2, 7, 2},  // +
//            {7, 5},  // |_|
    };

    private Pile getPile() {
        return new Pile(board.getHeight() - 5, POSSIBLE_PILES[random.nextInt(POSSIBLE_PILES.length)]);
    }

}
