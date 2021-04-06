package pool.logic;

import com.simplejcode.commons.misc.util.ThreadUtils;
import pool.gui.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import static pool.utils.Consts.*;
import static pool.utils.ImageFactory.*;

public class GameController implements MouseListener, MouseMotionListener {

    private int translateForCue;

    private double mouseX;
    private double mouseY;

    private boolean needCue;
    private boolean moving;
    private boolean foulMode;
    private boolean gameOver;

    private BufferedImage image;
    private JProgressBar strengthBar;
    private TableModel model;
    private Component view;
    private Player current;

    public void startGame(String firstName, String secondName) {
        strengthBar = new JProgressBar();
        strengthBar.setValue(50);
        strengthBar.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                strengthBar.setValue(e.getX() / 2 + 1);
            }
        });
        strengthBar.setBackground(Color.white);
        strengthBar.setStringPainted(true);
        strengthBar.setPreferredSize(new Dimension(200, 40));

        needCue = true;
        model = new TableModel();
        view = new Canvas();
        view.setIgnoreRepaint(true);
        view.setPreferredSize(new Dimension(700, 400));
        image = PoolAPI.getDevice().getDefaultConfiguration().createCompatibleImage(700, 400);
        view.addMouseMotionListener(this);
        view.addMouseListener(this);
        Point hotSpot = new Point(BALL_RADIUS, BALL_RADIUS);
        view.setCursor(view.getToolkit().createCustomCursor(cursor, hotSpot, null));

        PlayerInfoPanel info1 = new PlayerInfoPanel(firstName, colourBall);
        PlayerInfoPanel info2 = new PlayerInfoPanel(secondName, stripedBall);
        current = new Player(this, info1, 1);
        current.next = new Player(this, info2, 2);
        current.next.next = current;
        current.info.startTimer();

        new GamePanel(view, strengthBar, info1, info2);
    }

    private void render(Graphics2D g) {
        g.drawImage(desk, 0, 0, null);
//                BufferedImage img = new BufferedImage(
//                        2 * BALL_RADIUS, 2 * BALL_RADIUS, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < N_BALLS; i++) {
            int x = (int) model.getCenter(i).x - BALL_RADIUS;
            int y = (int) model.getCenter(i).y - BALL_RADIUS;
//                    Vector2D d = new Vector2D(model.getBalls()[i].point);
//                    d.y = -d.y;
//                    for (int a = 0; a < 2 * BALL_RADIUS; a++) {
//                        for (int b = 0; b < 2 * BALL_RADIUS; b++) {
//                            if (balls[i].getRGB(a, b) == 0) {
//                                img.setRGB(a, b, 0);
//                                continue;
//                            }
//                            Vector2D v = Sphere.move(new Vector2D(a - 10, b - 10), d);
//                            v.x = Math.max(Math.min(v.x + 10, 19), 0);
//                            v.y = Math.max(Math.min(v.y + 10, 19), 0);
//                            int rgb  = balls[i].getRGB((int) v.x, (int) v.y);
//                            img.setRGB(a, b, rgb);
//                        }
//                    }
            g.drawImage(balls[i], x, y, null);
        }
        if (needCue) {
            int x = (int) model.getCenter(0).x, y = (int) model.getCenter(0).y;
            double theta = Math.atan2(mouseY - y, mouseX - x);
            g.rotate(theta, x, y);
            g.translate(translateForCue, 0);
            g.drawImage(cue, x - 300 - 2 * BALL_RADIUS, y - 2, null);
            g.translate(-translateForCue, 0);
            g.rotate(-theta, x, y);
        }
        if (gameOver) {
            g.setColor(Color.darkGray);
            g.setFont(new Font("Curier", Font.BOLD, 90));
            for (int i = 1; i < 8; i++) {
                g.drawString("GAME OVER", 60 + i / 2, 200 + i);
            }
            g.setColor(Color.blue);
            g.drawString("GAME OVER", 60, 200);
        }
    }

    public Component getView() {
        return view;
    }

    public TableModel getModel() {
        return model;
    }

    void setTranslateForQue(int translate) {
        translateForCue = translate;
    }

    void setMoving(boolean moving) {
        this.moving = moving;
    }

    void setNeedCue(boolean needCue) {
        this.needCue = needCue;
    }

    void setFoulMode(boolean foulMode) {
        this.foulMode = foulMode;
    }

    void update() {
        render(image.createGraphics());
        view.getGraphics().drawImage(image, 0, 0, null);
    }

    boolean isMoving() {
        return moving;
    }

    void gameOver() {
        view.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        gameOver = true;
        update();
    }

    void smash(int ballNumber) {
        current.smash(ballNumber);
    }

    void inPocket(int num) {
        current.inPocket(num);
    }

    public void mouseMoved(MouseEvent e) {
        if (!moving && !gameOver) {
            mouseX = e.getX();
            mouseY = e.getY();
            update();
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (!moving && !gameOver && foulMode &&
                e.getY() > Y_MIN + BALL_RADIUS &&
                e.getY() < Y_MAX - BALL_RADIUS &&
                model.getCenter(0).distanceTo(e.getX(), e.getY()) < BALL_RADIUS)
        {
            mouseMoved(e);
            model.getBalls()[0].init(model.getBalls()[0].getCenter().x, e.getY());
            update();
        }
    }

    public void mousePressed(MouseEvent e) {
        if (moving || gameOver || e.getButton() != 1) {
            return;
        }
        final int value = strengthBar.getValue();
        ThreadUtils.executeInNewThread(() -> {
            moving = true;
            model.getBalls()[0].speedChanged(e.getX(), e.getY(),
                    (double) strengthBar.getValue() / strengthBar.getMaximum());
            current.init();
            new Motion(GameController.this, value).start();
            moving = false;
            needCue = true;
            Player next = current.whoIsNext();
            if (next != null) {
                (current = next).info.startTimer();
            } else {
                PoolAPI.getInstance().theWinnerIs(current);
            }
        });
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }
}
