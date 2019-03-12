package tetris.logic;

import javax.swing.*;
import java.awt.*;

public class TetrisVisualizer extends JFrame {

    public static final int BLOCK_SIZE = 30;
    public static final int X = 20, Y = 20;

    private int w, h, score;
    private BitRectangle rectangle;

    public TetrisVisualizer() throws HeadlessException {
        setContentPane(new JPanel() {
            protected void paintComponent(Graphics gr) {
                if (rectangle == null) {
                    return;
                }
                Graphics2D g = (Graphics2D) gr;

                g.setColor(Color.black);
                g.setStroke(new BasicStroke(2));
                g.drawRect(X, Y, BLOCK_SIZE * w, BLOCK_SIZE * h);
                g.setStroke(new BasicStroke(1));
                g.setColor(Color.gray);
                for (int i = 0; i <= h; i++) {
                    g.drawLine(X, Y + i * BLOCK_SIZE, X + w * BLOCK_SIZE, Y + i * BLOCK_SIZE);
                }
                for (int i = 0; i <= w; i++) {
                    g.drawLine(X + i * BLOCK_SIZE, Y, X + i * BLOCK_SIZE, Y + h * BLOCK_SIZE);
                }

                for (int i = 0; i < h; i++) {
                    for (int j = 0; j < w; j++) {
                        if (rectangle.isOccupied(i, j)) {
                            fill(g, j, h - i - 1);
                        }
                    }
                }
            }
        });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(200, 100);
        setVisible(true);
    }

    public void setRectangle(BitRectangle rectangle, int score) {
        this.rectangle = rectangle;
        this.score = score;
        w = rectangle.getWidth();
        h = rectangle.getHeight();
        setSize(new Dimension(BLOCK_SIZE * w + 3 * X + 300, BLOCK_SIZE * h + 4 * Y ));
        repaint();
    }

    private void fill(Graphics g, int i, int j) {
        i *= BLOCK_SIZE;
        j *= BLOCK_SIZE;
        i += X;
        j += Y;
        g.setColor(Color.blue);
        g.fillRoundRect(i + 1, j + 1, BLOCK_SIZE - 2, BLOCK_SIZE - 2, 12, 12);
        for (int d = 1; d < 4; d++) {
            int v = (int) (64 + 192 * Math.pow(2d * d / BLOCK_SIZE, 0.3));
            g.setColor(new Color(v, v, 200));
            g.drawRoundRect(i + d, j + d, BLOCK_SIZE - 2 * d, BLOCK_SIZE - 2 * d, 12, 12);
        }
        g.setColor(Color.black);
        g.setFont(new Font("Serif", Font.BOLD, 50));
        g.drawString("Score: " + score, BLOCK_SIZE * w + 3 * X, Y + 50);

    }

}
