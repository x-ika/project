import java.awt.event.*;
import java.awt.*;
import java.io.*;

public class Chess extends Frame implements ActionListener, MouseListener {

    public static final int FONT_SIZE = 15, SIZE = 50, N = 8, dX = 40, dY = 100;
    private static final int[] VALUE = new int[]{3, 5, 4, 2, 1, 4, 5, 3};
    private int number, nPlayers, desk[][];
    private String result;
    private boolean writing = false;
    private BufferedWriter bw1, bw2;
    private Player player;

    private Chess() {
        super("Chess");
        if (writing) {
            try {
                bw1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Debuts for whites.txt")));
                bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Debuts for blacks.txt")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        setMenuBar(createMenuBar());
        addMouseListener(this);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                dispose();
            }
        });
        renew(1);
    }

    private MenuBar createMenuBar() {
        MenuItem mi1 = new MenuItem("1 Player", new MenuShortcut(KeyEvent.VK_N));
        mi1.addActionListener(this);
        MenuItem mi2 = new MenuItem("2 Player", new MenuShortcut(KeyEvent.VK_M));
        mi2.addActionListener(this);
        MenuItem mi3 = new MenuItem("Exit", new MenuShortcut(KeyEvent.VK_X));
        mi3.addActionListener(this);
        Menu newGameMenu = new Menu("New Game");
        newGameMenu.add(mi1);
        newGameMenu.add(mi2);
        Menu fileMenu = new Menu("File");
        fileMenu.add(newGameMenu);
        fileMenu.addSeparator();
        fileMenu.add(mi3);
        MenuBar menuBar = new MenuBar();
        menuBar.add(fileMenu);
        return menuBar;
    }

    public void renew(int n) {
        removeAll();
        setSize(100 + N * SIZE, 150 + N * SIZE);
        setLocation(250, 50);
        setVisible(true);
        number = -1;
        nPlayers = n;
        result = null;
        desk = new int[N][N];
        player = new Player(desk);
        for (int i = 0; i < N; i++) {
            add(new Figure(this, VALUE[i], i * SIZE + dX, (N - 1) * SIZE + dY));
            add(new Figure(this, 6, i * SIZE + dX, (N - 2) * SIZE + dY));
            add(new Figure(this, -6, i * SIZE + dX, SIZE + dY));
            add(new Figure(this, -VALUE[i], i * SIZE + dX, dY));
            desk[i][0] = -VALUE[i];
            desk[i][1] = -6;
            desk[i][N - 2] = 6;
            desk[i][N - 1] = VALUE[i];
        }
    }

    public void paint(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());
        String[] st1 = {"1", "2", "3", "4", "5", "6", "7", "8"};
        String[] st2 = {"A", "B", "C", "D", "E", "F", "G", "H"};
        setBackground(Color.white);
        g.setColor(Color.black);
        g.setFont(new Font("Serif", Font.BOLD, FONT_SIZE));
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                drawCage(i, j);
            }
            g.drawString(st1[N - i - 1], dX - FONT_SIZE, dY + i * SIZE + SIZE / 2 + FONT_SIZE / 2);
            g.drawString(st2[i], dX + i * SIZE + SIZE / 2 - FONT_SIZE / 2, dY + N * SIZE + FONT_SIZE);
        }
        g.drawRect(dX - 1, dY - 1, N * SIZE + 1, N * SIZE + 1);
        g.drawRect(dX - 2, dY - 2, N * SIZE + 3, N * SIZE + 3);
        if (result != null) {
            g.setFont(new Font("Serif", Font.BOLD, 30));
            g.drawString(result, dX, dY - 10);
        }
    }

    private void drawCage(int x, int y) {
        Graphics g = getGraphics();
        g.setColor(x % 2 != y % 2 ? Color.lightGray : Color.white);
        g.fillRect(x * SIZE + dX, y * SIZE + dY, SIZE, SIZE);
    }

    public boolean changeDesk(Move move) {
        if (move == null || result != null) {
            return false;
        }
        if (writing) {
            writeInDebuts(move);
        }
        move.forward();
        if (move.isCilledTurne()) {
            getComponentAt(dX + SIZE * move.getCilledX(), dY + SIZE * move.getCilledY()).setSize(0, 0);
            drawCage(move.getCilledX(), move.getCilledY());
        }
        Component current = getComponentAt(dX + SIZE * move.x1, dY + SIZE * move.y1);
        if (move instanceof HMove) {
            ((Figure) current).setImage(((HMove) move).casedValue);
        }
        current.setLocation(dX + move.x2 * SIZE, dY + move.y2 * SIZE);
        current.getGraphics().drawImage(((Figure) current).getImage(), 0, 0, current);
        drawCage(move.x1, move.y1);
        if (move instanceof Rook) {
            Rook rook = (Rook) move;
            current = getComponentAt(dX + SIZE * 4, dY + SIZE * rook.y1);
            current.setLocation(dX + rook.kingX2 * SIZE, dY + rook.y1 * SIZE);
            current.getGraphics().drawImage(((Figure) current).getImage(), 0, 0, current);
            drawCage(4, rook.y1);
        }
        return true;
    }

    private void writeInDebuts(Move move) {
        BufferedWriter bw = move.value1 > 0 ? bw1 : bw2;
        try {
            for (int i = 0; i < N * N; i++) {
                bw.write(Player.CODE_OF[desk[i / 8][i % 8] + 6]);
            }
            bw.write(move.toString() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mousePressed(MouseEvent e) {
        int x, y;
        if (e.getComponent().equals(this)) {
            x = (e.getX() - dX) / SIZE;
            y = (e.getY() - dY) / SIZE;
        } else {
            x = (e.getComponent().getX() - dX) / SIZE;
            y = (e.getComponent().getY() - dY) / SIZE;
        }
        if (!changeDesk(player.setXY(x, y, number))) {
            return;
        }
        int pos = player.isWinner(number);
        if (pos > 0) {
            result = pos == 1 ? "DRAW" : (number > 0 ? "SECOND PLAYER" : "FIRST PLAYER") + " WIN";
            repaint();
            return;
        }
        number = -number;
        if (nPlayers == 1) {
            try {
                Thread.sleep(500);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            changeDesk(player.makeTurne(number));
            pos = player.isWinner(number);
            if (pos > 0) {
                result = pos == 1 ? "DRAW" : (number > 0 ? "SECOND PLAYER" : "FIRST PLAYER") + " WIN";
                repaint();
                return;
            }
            number = -number;
        }
    }

    public void actionPerformed(ActionEvent ev) {
        String command = ev.getActionCommand();
        if (command.equals("1 Player")) {
            renew(1);
        } else if (command.equals("2 Player")) {
            renew(2);
        } else if (command.equals("Exit")) {
            if (writing) {
                try {
                    bw1.close();
                    bw2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            dispose();
        }
    }

    public static void main(String[] args) {
        new Chess();
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

}

class Figure extends Canvas {
    public static final int[] COST_OF = new int[]{0, 50, 10, 5, 3, 3, 1},
            LENGTH = new int[]{1, 7, 7, 7, 1};
    public static final int[][] X_ARROW = new int[][]{{0, 1, 1, 1, 0, -1, -1, -1},
            {0, 1, 1, 1, 0, -1, -1, -1},
            {0, 1, 0, -1},
            {1, 1, -1, -1},
            {1, 2, 2, 1, -1, -2, -2, -1}},
            Y_ARROW = new int[][]{{-1, -1, 0, 1, 1, 1, 0, -1},
                    {-1, -1, 0, 1, 1, 1, 0, -1},
                    {-1, 0, 1, 0},
                    {-1, 1, 1, -1},
                    {-2, -1, 1, 2, 2, 1, -1, -2}};
    private static final String[] NAME = new String[]{"King", "Queen", "Rook", "Bishop", "Knight", "Pawn"};
    public Image whiteImage, blackImage;

    Figure(Chess game, int v, int x, int y) {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setSize(Chess.SIZE, Chess.SIZE);
        addMouseListener(game);
        setLocation(x, y);
        setImage(v);
    }

    public Image getImage() {
        return ((getX() - Chess.dX) / Chess.SIZE) % 2 == ((getY() - Chess.dY) / Chess.SIZE) % 2 ?
                whiteImage : blackImage;
    }

    public void setImage(int value) {
        String name = NAME[Math.abs(value) - 1];
        name += value > 0 ? "1" : "2";
        whiteImage = getToolkit().getImage("resources\\figures\\" + name + "1.gif");
        blackImage = getToolkit().getImage("resources\\figures\\" + name + "2.gif");
    }

    public void paint(Graphics g) {
        g.drawImage(getImage(), 0, 0, this);
    }
}
