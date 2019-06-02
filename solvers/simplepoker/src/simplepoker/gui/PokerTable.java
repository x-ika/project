package simplepoker.gui;

import com.simplejcode.commons.misc.util.ThreadUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.Collection;

public class PokerTable extends JFrame implements MouseListener {

    private boolean debugMode;
    private boolean wait;
    private int nPlayers;
    private String[] playerNames;
    private PTable table;

    private int[] money;
    private int[] bank;
    private double[] cards;
    private Collection<Integer> red;
    private String message;

    public PokerTable(boolean debugMode, String[] playerNames) {
        super("Simple Poker");
        this.debugMode = debugMode;
        wait = debugMode;
        this.playerNames = playerNames;
        nPlayers = playerNames.length;
        table = new PTable();
        getContentPane().add(table);
        table.addMouseListener(this);
        pack();

        Rectangle r = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getMaximumWindowBounds();
        setLocation(((int) r.getWidth() - getWidth()) / 2,
                ((int) r.getHeight() - getHeight()) / 2);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public synchronized void update(int[] money, int[] bank, double[] cards, Collection<Integer> in, String s) {
        message = s;
        red = in;
        this.money = money;
        this.bank = bank;
        this.cards = cards;
        table.repaint();
        if (wait) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (debugMode) {
            wait = true;
        } else {
            ThreadUtils.sleep(100);
        }
    }

    //-----------------------------------------------------------------------------------

    public synchronized void mouseClicked(MouseEvent e) {
        wait = !wait;
        if (!wait) {
            notify();
        }
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    //-----------------------------------------------------------------------------------

    private class PTable extends JComponent {

        public PTable() {
            setPreferredSize(new Dimension(900, 600));
        }

        protected void paintComponent(Graphics g) {

            int ox = getWidth() / 2;
            int oy = getHeight() / 2;

            int r = Math.min(ox, oy) - 50;
            int tot = 0;
            for (int x : bank) {
                tot += x;
            }

            g.setFont(new Font("Serif", Font.PLAIN, 18));
            for (int i = 0; i < nPlayers; i++) {
                double a = 2 * Math.PI * i / nPlayers;
                int x = (int) (ox + r * Math.cos(a));
                int y = (int) (oy + r * Math.sin(a));

                g.setColor(Color.black);
                g.drawString(playerNames[i], x - 100, y - 50);
                g.drawString("M:" + money[i], x, y);
                g.drawString("B:" + bank[i], x + 60, y);
                g.drawString(String.format("(C:%.1f)", 1e2 * cards[i]), x - 100, y);

                if (red.contains(i)) {
                    g.setColor(Color.red);
                    g.fillOval(x - 20, y - 10, 10, 10);
                }

            }

            g.setColor(Color.red);
            g.setFont(new Font("Serif", Font.BOLD, 22));
            g.drawString("" + tot, ox, oy);
            g.drawString(message, ox - 5 * message.length(), oy - 25);

        }

    }

}
