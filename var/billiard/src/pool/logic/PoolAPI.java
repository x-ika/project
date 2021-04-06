package pool.logic;

import pool.gui.MainFrame;
import pool.gui.menu.BestResults;
import pool.utils.*;

import javax.swing.*;
import java.awt.*;

public final class PoolAPI {

    private static PoolAPI instance;

    public static PoolAPI getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        SoundManager.createAudioClips();
        SoundManager.getInstance().setMusicFlag(true);
        ImageFactory.createImages();
        instance = new PoolAPI();
    }

    public static GraphicsDevice getDevice() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    }


    private MainFrame mainFrame;

    private GameController controller;

    private PoolAPI() {
        mainFrame = new MainFrame();
        //mainFrame.getContentPane().repaint();
    }

    public void startGame(String firstName, String secondName) {
        if (controller != null) {
        }
        controller = new GameController();
        controller.startGame(firstName, secondName);

        mainFrame.setContent((JPanel) controller.getView().getParent());
        //if (mainFrame.getContentPane() != controller.getView()) {
        //}
    }

    public void theWinnerIs(Player player) {
        new BestResults(mainFrame).addNewPlayer(player);
    }

}
