package ika.games.domino.client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;

import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

import com.simplejcode.commons.net.util.ByteMessageBuilder;
import ika.games.base.BasicGameAction;
import ika.games.domino.base.DominoMove;
import ika.games.domino.base.DominoTree;
import ika.games.domino.client.DominoClient;

public class DominoRoom extends JComponent implements ComponentListener {

    private static final BufferedImage[][] CACHE = new BufferedImage[64][4];

    public static BufferedImage getImage(int stone, int rotation) {
        int ind = stone == -1 ? 63 : stone;
        if (CACHE[ind][rotation] == null) {
            int i = stone >> 3;
            int j = stone & 7;
            int s = ind == 63 ? 0 : 29 - (7 - j) * (8 - j) / 2 + i - j;
            CACHE[ind][0] = DominoClient.getImage(String.format("resources/dominos/set/small/%02d.png", s));
            for (int t = 0; t < 3; t++) {
                int w = CACHE[ind][t].getWidth();
                int h = CACHE[ind][t].getHeight();
                CACHE[ind][t + 1] = new BufferedImage(h, w,  BufferedImage.TYPE_INT_RGB);
                for (int x = 0; x < w; x++) {
                    for (int y = 0; y < h; y++) {
                        //noinspection SuspiciousNameCombination
                        CACHE[ind][t + 1].setRGB(h - y - 1, x, CACHE[ind][t].getRGB(x, y));
                    }
                }
            }
        }
        return CACHE[ind][rotation];
    }

    public static BufferedImage getImage(int stone) {
        return getImage(stone, 0);
    }

    public static BufferedImage adjustRGB(BufferedImage image, int t) {
        int[] p = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
        int tr = t >> 16, tg = (t >> 8) & 255, tb = t & 255;
        for (int i = 0; i < p.length; i++) {
            int r = (p[i] >> 16) & 255;
            int g = (p[i] >> 8) & 255;
            int b = p[i] & 255;
            p[i] = max(min(r + tr - 128, 255), 0) << 16 | max(min(g + tg - 128, 255), 0) << 8 | max(min(b + tb - 128, 255), 0);
        }
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        copy.setRGB(0, 0, image.getWidth(), image.getHeight(), p, 0, image.getWidth());
        return copy;
    }

    //-----------------------------------------------------------------------------------

    private int place;
    private List<DominoMove> moves;
    private DominoTable table;
    private PlayerArea[] playerAreas;

    public DominoRoom() {
        setLayout(null);
        setPreferredSize(new Dimension(900, 600));
        setBackground(Color.orange);
    }

    public void update(int sit, int plays, int[][] stones, String[] users,
                       int[] score, int rest, int total, boolean first, DominoTree tree)
    {
        place = sit;

        boolean myMove = place != -1 && place == plays;

        if (!myMove) {
            moves = null;
        } else {
            moves = tree.getPossibleMoves(stones[place], 0, first);
        }

        if (playerAreas == null) {
            init();
        }
        for (int i = 0; i < 4; i++) {
            boolean curMoves = i == plays;
            int canMove = 0;
            if (myMove && curMoves) {
                for (int j = 0; j < stones[i].length; j++) {
                    for (DominoMove move : moves) {
                        if (move.newNode.getStone() == stones[i][j]) {
                            canMove |= 1 << j;
                        }
                    }
                }
            }
            playerAreas[i].update(users[i], score[i % 2], curMoves ? rest : -1, total, stones[i], canMove);
        }

        align();
        table.update(tree);

    }

    void makeDominoMove(int stone) {
        if (moves == null) {
            return;
        }
        int selected = playerAreas[place].getSelectedInd();
        if (selected == -1) {
            return;
        }
        for (DominoMove move : moves) {

            if (stone == (move.basic == null ? -1 : move.basic.getStone()) && move.newNode.getStone() == playerAreas[place].getSelectedStone()) {
                ByteMessageBuilder builder = new ByteMessageBuilder(32, ',');
                builder.writeInt(BasicGameAction.MAKE_MOVE.getId());
                builder.writeInt(stone);
                builder.writeInt(playerAreas[place].getSelectedStone());
                builder.writeInt(move.side1);
                builder.writeInt(move.side2);
                DominoClient.client.sendMessage(builder.getMessage());
            }

        }
    }

    boolean isPossible(int stone) {
        if (moves != null && !moves.isEmpty() && playerAreas[place].getSelectedInd() != -1) {
            for (DominoMove m : moves) {
                if (m.basic != null && m.basic.getStone() == stone && m.newNode.getStone() == playerAreas[place].getSelectedStone()) {
                    return true;
                }
            }
        }
        return false;
    }

    void updateBoard() {
        table.update(table.getTree());
    }

    private void init() {
        addComponentListener(this);
        playerAreas = new PlayerArea[4];
        for (int i = 0; i < 4; i++) {
            add(playerAreas[i] = new PlayerArea());
        }
        add(table = new DominoTable(this));
        align();
    }

    private void align() {

        int w = getWidth(), h = getHeight();

        for (int i = 0; i < playerAreas.length; i++) {
            playerAreas[i].setCenterAndOrientation(0, 0, i);
            int pw = playerAreas[i].getWidth();
            int ph = playerAreas[i].getHeight();
            int x = i % 2 == 0 ? w >> 1 : i == 1 ? pw / 2 : w - pw / 2;
            int y = i % 2 == 1 ? h >> 1 : i == 2 ? ph / 2 : h - ph / 2;
            playerAreas[i].setCenterAndOrientation(x, y, i);
        }

        int ww = w - playerAreas[1].getWidth() - playerAreas[3].getWidth();
        int hh = h - playerAreas[0].getHeight() - playerAreas[2].getHeight();
        table.setSize(ww, hh);
        table.setLocation(w - ww >> 1, h - hh >> 1);
        repaint();
    }

    //-----------------------------------------------------------------------------------

    public void componentResized(ComponentEvent e) {
        align();
        updateBoard();
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }

}
