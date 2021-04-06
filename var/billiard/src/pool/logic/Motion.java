package pool.logic;

import pool.utils.*;

import javax.swing.*;
import java.awt.event.*;

class Motion implements ActionListener {
    private double timeOfAim;

    private double timeOfReturn;

    private double timeOfStroke;

    private double returnDistance;

    private GameController game;

    private Timer timer;

    Motion(GameController game, int MAX_TIME) {
        timer = new Timer(1000 / Consts.CADRES_IN_SECOND, this);
        this.game = game;
        timeOfAim = 30 - MAX_TIME / 5;
        timeOfReturn = 5 + MAX_TIME / 4;
        timeOfStroke = 5 + MAX_TIME / 20;
        returnDistance = -5 - MAX_TIME / 2;
    }

    void start() {
        moveCue();
        game.setNeedCue(false);
        timer.start();
        while (timer.isRunning()) {
            sleep(500);
        }
    }

    private void moveCue() {
        for (int currentTime = 0; currentTime < 40 + 1; currentTime++) {
            game.setTranslateForQue((int) translate(currentTime));
            game.update();
            sleep(10);
        }
        game.setTranslateForQue(0);
        SoundManager.playPushcue();
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private double translate(double currentTime) {
        if (currentTime <= timeOfAim) {
            return (5 - 5 * Math.cos(Math.PI * currentTime / timeOfAim));
        }
        currentTime -= timeOfAim;
        if (currentTime <= timeOfReturn) {
            return (5 + returnDistance / 2 + (5 - returnDistance / 2) *
                    Math.cos(Math.PI * currentTime / timeOfReturn));
        }
        currentTime -= timeOfReturn;
        return (returnDistance + (10 - returnDistance) *
                Math.pow(currentTime / timeOfStroke, 2));
    }

    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < Consts.GAP; i++) {
            step();
            if (!game.getModel().moveBalls()) {
                timer.stop();
            }
        }
        game.update();
    }

    private void step() {
        boolean outOfBorders = false;
        boolean inPocket = false;
        boolean smash = false;
        for (int i = 0; i < Consts.N_BALLS; i++) {
            if (game.getModel().isInPocket(i)) {
                game.inPocket(i);
                inPocket = true;
            }
            if (game.getModel().isOutOfBorders(i)) {
                outOfBorders = true;
            }
            for (int j = i + 1; j < Consts.N_BALLS; j++) {
                if (game.getModel().isSmash(i, j)) {
                    game.smash(j);
                    smash = true;
                }
            }
        }
        if (smash) {
            SoundManager.playBalls();
        }
        if (outOfBorders) {
            SoundManager.playBorder();
        }
        if (inPocket) {
            SoundManager.playPocket();
        }
    }
}
