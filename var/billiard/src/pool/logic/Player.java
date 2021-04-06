package pool.logic;

import pool.geom.Vector2D;
import pool.gui.PlayerInfoPanel;
import pool.utils.Consts;

public class Player {
    private int number;

    private int nBalls;

    private boolean smashFlag;

    private boolean my;

    private boolean notMy;

    private boolean foul;

    private boolean black;

    private GameController game;

    public PlayerInfoPanel info;

    Player next;

    Player(GameController view, PlayerInfoPanel infoPanel, int num) {
        game = view;
        nBalls = Consts.N_BALLS / 2 - 1;
        number = num;
        info = infoPanel;
    }

    void init() {
        info.stopTimer();
        smashFlag = false;
        my = false;
        notMy = false;
        foul = false;
        black = false;
    }

    Player whoIsNext() {
        if (black) {
            game.gameOver();
            return null;
        }
        if (foul |= !smashFlag) {
            game.getModel().getBalls()[0].init(
                    Consts.X_MIN + 150, (Consts.Y_MIN + Consts.Y_MAX) / 2);
            game.setFoulMode(true);
        }
        return !my || notMy || foul ? next : this;
    }

    void smash(int ballNumber) {
        if (smashFlag) {
            return;
        }
        smashFlag = true;
        foul |= nBalls == 0 ? !isBlack(ballNumber) : !isMyBall(ballNumber);
    }

    void inPocket(int num) {
        game.getModel().getBalls()[num].setSpeed(new Vector2D());
        game.getModel().getBalls()[num].init(-20, -20);
        if (isWhite(num)) {
            foul = true;
        } else if (isBlack(num)) {
            black = true;
        } else {
            if (isMyBall(num)) {
                nBalls--;
                my = true;
            } else {
                notMy = true;
                next.nBalls--;
            }
        }
    }

    private boolean isMyBall(int ballNumber) {
        if (isBlack(ballNumber) || isWhite(ballNumber)) {
            return false;
        }
        switch (number) {
            case 1:
                return ballNumber < 8;
            case 2:
                return ballNumber > 8;
            default:
                return false;
        }
    }

    private static boolean isWhite(int ballNumber) {
        return ballNumber == 0;
    }

    private static boolean isBlack(int ballNumber) {
        return ballNumber == 8;
    }
}
