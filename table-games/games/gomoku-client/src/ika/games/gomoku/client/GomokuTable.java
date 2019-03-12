package ika.games.gomoku.client;

import ika.games.base.BasicGameAction;
import com.simplejcode.commons.net.util.ByteMessageBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

public class GomokuTable extends JComponent implements MouseListener {

    private static final String FLAG_X = "X";

    private static final String FLAG_0 = "O";

    private int cellSize;

    private Move last;

    private Move[] win;

    private int[][] desk;

    private Image deskImage;

    public GomokuTable(int cellSize) {
        this.cellSize = cellSize;
        addMouseListener(this);
    }

    public void update(int[][] desk, Move last, Move[] win) {
        this.desk = desk;
        this.last = last;
        this.win = win;

        final int w = desk[0].length * cellSize + 1, h = desk.length * cellSize + 1;

        setSize(new Dimension(w, h));
        drawDesk(w, h);
        repaint();
    }

    //-----------------------------------------------------------------------------------

    private void drawDesk(int w, int h) {
        deskImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics g = deskImage.getGraphics();
        g.setColor(Color.gray);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.gray);
        for (int i = 0; i <= desk[0].length; i++) {
            g.drawLine(i * cellSize, 0, i * cellSize, h);
        }
        for (int i = 0; i <= desk.length; i++) {
            g.drawLine(0, i * cellSize, w, i * cellSize);
        }
        for (int i = 0; i < desk[0].length; i++) {
            for (int j = 0; j < desk.length; j++) {
                g.setColor(Color.white);
                g.fillRoundRect(i * cellSize + 1, j * cellSize + 1, cellSize - 2, cellSize - 2, 12, 12);
                for (int d = 1; d < 4; d++) {
                    int v = (int) (64 + 192 * Math.pow(2d * d / cellSize, 0.3));
                    g.setColor(new Color(v, v, v));
                    g.drawRoundRect(i * cellSize + d, j * cellSize + d, cellSize - 2 * d, cellSize - 2 * d, 12, 12);
                }
            }
        }
    }

    protected void paintComponent(Graphics g) {
        g.drawImage(deskImage, 0, 0, this);
        if (desk == null) {
            return; // nothing more to paint
        }

        g.setFont(new Font("Serif", Font.BOLD, 20));
        FontMetrics metrics = g.getFontMetrics();
        int h = (cellSize + 15) / 2;
        int w = (cellSize - metrics.getWidths()['X']) / 2;

        if (win == null && last != null) {
            g.setColor(Color.lightGray);
            g.fillRect(last.column * cellSize + 5, last.row * cellSize + 5, cellSize - 10, cellSize - 10);
        }

        if (win != null) {
            g.setColor(new Color(255, 0, 0, 128));
            for (Move p : win) {
                g.fillRect(p.column * cellSize + 5, p.row * cellSize + 5, cellSize - 10, cellSize - 10);
            }
        }

        for (int i = 0; i < desk.length; i++) {
            for (int j = 0; j < desk[0].length; j++) {
                if (desk[i][j] != 0) {
                    String s = "";
                    switch (desk[i][j]) {
                        case 2:
                            s = FLAG_0;
                            g.setColor(Color.BLUE);
                            break;
                        case 1:
                            s = FLAG_X;
                            g.setColor(Color.RED);
                            break;
                    }
                    g.drawString(s, j * cellSize + w, i * cellSize + h);
                }
            }
        }
    }

    //-----------------------------------------------------------------------------------

    public void mousePressed(MouseEvent e) {
        int row = e.getY() / cellSize;
        int col = e.getX() / cellSize;

        ByteMessageBuilder builder = new ByteMessageBuilder(32, ',');
        builder.writeInt(BasicGameAction.MAKE_MOVE.getId());
        builder.writeInt(row);
        builder.writeInt(col);
        GomokuClient.client.sendMessage(builder.getMessage());
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

}
