package pool.gui;

import pool.utils.Consts;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class HeadPanel extends JPanel {

    public final String RESUME = "Resume";
    public final String NEW_GAME = "New Game";
    public final String REGISTRATION = "Registration";
    public final String BEST_RESULTS = "Best Results";
    public final String ALL_RESULTS = "All Results";
    public final String SETTINGS = "Settings";
    public final String QUIT = "Quit";

    private BufferedImage image;

    public MenuComponent resume;

    public HeadPanel(MainFrame mainFrame) {
        try {
            image = ImageIO.read(new File(Consts.IMAGE_PATH + "picture.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        resume = new MenuComponent(mainFrame, RESUME, false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(50, 100, 50, 0));
        setOpaque(false);
        MenuComponent game = new MenuComponent(mainFrame, NEW_GAME);
        MenuComponent players = new MenuComponent(mainFrame, REGISTRATION);
        MenuComponent results = new MenuComponent(mainFrame, BEST_RESULTS);
        MenuComponent rating = new MenuComponent(mainFrame, ALL_RESULTS);
        MenuComponent options = new MenuComponent(mainFrame, SETTINGS);
        MenuComponent quit = new MenuComponent(mainFrame, QUIT);
        add(resume);
        add(game);
        add(players);
        add(results);
        add(rating);
        add(options);
        add(quit);
    }

    public void paint(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;
        double sx = 1d * getWidth() / image.getWidth();
        double sy = 1d * getHeight() / image.getHeight();
        g.scale(sx, sy);
        g.drawImage(image, 0, 0, this);
        g.scale(1 / sx, 1 / sy);
        super.paint(g);
    }

}
