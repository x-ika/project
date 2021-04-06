package gui;

import com.simplejcode.commons.misc.util.ThreadUtils;
import main.*;
import player.*;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class CheckersTableView extends Frame implements ActionListener, MouseListener, ItemListener {

    private static int SIZE = 50, dX = 40, dY = 100, d3 = SIZE / 10;
    private int x, y;
    private int[][] desk;
    private Color[] color = new Color[6];
    private String result = null;
    private boolean gameOver;
    private CheckersController controller;

    private ArrayList<TableListener> listeners = new ArrayList<>();

    public CheckersTableView(CheckersController controller) throws HeadlessException {
        super("Checkers");
        this.controller = controller;
        setMenuBar(createManuBar());
        addMouseListener(this);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                dispose();
            }
        });
    }

    private MenuBar createManuBar() {
        MenuItem mi2 = new MenuItem("Exit", new MenuShortcut(KeyEvent.VK_X));
        MenuItem mi3 = new MenuItem("Back", new MenuShortcut(KeyEvent.VK_Z));
        MenuItem mi4 = new MenuItem("8 X 8", new MenuShortcut(KeyEvent.VK_A));
        MenuItem mi5 = new MenuItem("10 X 10", new MenuShortcut(KeyEvent.VK_B));
        MenuItem mi6 = new MenuItem("Player vs Computer", new MenuShortcut(KeyEvent.VK_1));
        MenuItem mi7 = new MenuItem("Computer vs Player", new MenuShortcut(KeyEvent.VK_2));
        MenuItem mi8 = new MenuItem("Player vs Player", new MenuShortcut(KeyEvent.VK_3));
        CheckboxMenuItem cmi = new CheckboxMenuItem("Music");
        mi2.addActionListener(this);
        mi3.addActionListener(this);
        mi4.addActionListener(this);
        mi5.addActionListener(this);
        mi6.addActionListener(this);
        mi7.addActionListener(this);
        mi8.addActionListener(this);
        cmi.addItemListener(this);
        Menu m1 = new Menu("File");
        Menu m2 = new Menu("Game");
        Menu m3 = new Menu("Size");
        Menu m4 = new Menu("New Game");
        m1.add(m4);
        m1.add(cmi);
        m1.addSeparator();
        m1.add(mi2);
        m2.add(mi3);
        m2.add(m3);
        m3.add(mi4);
        m3.add(mi5);
        m4.add(mi6);
        m4.add(mi7);
        m4.add(mi8);
        MenuBar mb = new MenuBar();
        mb.add(m1);
        mb.add(m2);
        return mb;
    }

    public void addListener(TableListener listener) {
        listeners.add(listener);
    }

    public void clearListeners() {
        listeners.clear();
    }

    public void updateView(int[][] desk, int nC) {
        this.desk = desk;
        gameOver = false;
        setSize(100 + nC * SIZE, 150 + nC * SIZE);
        setLocation(200, 50);
        setVisible(true);
        repaint();
    }

    public void updateDesk(Turn t) {
        Graphics g = getGraphics();
        int i1 = t.first.row, j1 = t.first.col, i2 = t.last.row, j2 = t.last.col;
        desk[i1][j1] = 0;
        desk[i2][j2] = t.last.value;
        g.setColor(Color.lightGray);
        g.fillRect(j1 * SIZE + dX, i1 * SIZE + dY, SIZE, SIZE);
        if (t.killed != null) {
            for (Checker ch : t.killed) {
                desk[ch.row][ch.col] = 0;
                pause(100);
                g.fillRect(ch.col * SIZE + dX, ch.row * SIZE + dY, SIZE, SIZE);
                pause(100);
            }
        }
        g.setColor(color[t.last.value]);
        g.fillOval(j2 * SIZE + dX + d3, i2 * SIZE + dY + d3, SIZE - 2 * d3, SIZE - 2 * d3);
    }

    public void vis(Checker a, Checker b) {
        Graphics g = getGraphics();
        g.setColor(Color.RED);
        g.drawLine(a.col * SIZE + dX + SIZE / 2, a.row * SIZE + dY + SIZE / 2, b.col * SIZE + dX + SIZE / 2, b.row * SIZE + dY + SIZE / 2);
    }

    public void paint(Graphics g) {
        if (desk == null) {
            return;
        }
        int N = desk.length;
        color[1] = new Color(200, 0, 50);
        color[2] = new Color(50, 0, 200);
        color[4] = new Color(255, 0, 0);
        color[5] = new Color(0, 0, 255);
        String[] st1 = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        String[] st2 = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        setBackground(Color.white);
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                g.setColor(i % 2 != j % 2 ? Color.lightGray : Color.white);
                g.fillRect(j * SIZE + dX, i * SIZE + dY, SIZE, SIZE);
                if (desk[i][j] != 0) {
                    g.setColor(color[desk[i][j]]);
                    g.fillOval(j * SIZE + dX + d3, i * SIZE + dY + d3, SIZE - 2 * d3, SIZE - 2 * d3);
                }
            }
        g.setColor(Color.black);
        for (int i = 0; i < N; i++) {
            g.drawString(st2[N - i - 1], dX - 15, dY + SIZE / 2 + i * SIZE);
            g.drawString(st1[i], dX + SIZE / 2 + i * SIZE - 5, dY + N * SIZE + 15);
        }
        g.drawRect(dX - 1, dY - 1, N * SIZE + 1, N * SIZE + 1);
        if (gameOver) {
            g.setColor(Color.black);
            g.setFont(new Font("Serif", Font.PLAIN, 35));
            g.drawString(result, dX, dY - 10);
        }
    }

    public void gameOver(String result) {
        this.result = result;
        gameOver = true;
        repaint();
    }

    public static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void mousePressed(MouseEvent e) {
        x = (e.getX() - dX) / SIZE;
        y = (e.getY() - dY) / SIZE;
    }

    public void mouseReleased(MouseEvent e) {
        int lx = (e.getX() - dX) / SIZE;
        int ly = (e.getY() - dY) / SIZE;

        for (TableListener listener : listeners) {
            listener.mouseDragged(y, x, ly, lx);
        }
    }

    public void actionPerformed(ActionEvent e) {
        ThreadUtils.executeInNewThread(() -> handle(e));
    }

    private void handle(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Exit")) {
            System.exit(0);
        } else if (command.equals("Back")) {
            controller.back();
        } else if (command.equals("8 X 8")) {
            controller.playNewGame(8);
        } else if (command.equals("10 X 10")) {
            controller.playNewGame(10);
        } else if (command.equals("Player vs Computer")) {
            controller.playNewGame(new User(), new Computer());
        } else if (command.equals("Computer vs Player")) {
            controller.playNewGame(new Computer(), new User());
        } else if (command.equals("Player vs Player")) {
            controller.playNewGame(new User(), new User());
        }
    }

    public void itemStateChanged(ItemEvent e) {
        String s = (String) e.getItem();
        if (s.equals("Music")) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                CheckersController.Song.play();
            } else {
                CheckersController.Song.stopPlay();
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

}
