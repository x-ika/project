package gui;

import com.simplejcode.commons.gui.Console;
import com.simplejcode.commons.gui.*;
import com.simplejcode.commons.misc.improc.ImageMetrics;
import com.simplejcode.commons.misc.util.ReflectionUtils;
import proc.MODImageHandler;
import util.CameraVEC245;
import util.FrameAccess;
import util.ImageHandler;
import util.VideoWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * Main frame of the application. Provides the primitive tool, allowing users to mark
 * the contour of the object in the image.
 */
public class Main implements MouseMotionListener {

    private static final String PATH = "D:\\ika\\Media\\Video\\res";
    public static Main main;

    private CustomFrame frame;
    private AboutDialogPanel aboutDialogPanel;
    private FrameAccess fa;
    private ImageHandler handler = new MODImageHandler();

    private Main() {
        createMainFrame();
    }

    public synchronized void createMainFrame() {
        aboutDialogPanel = new AboutDialogPanel("SKILETON", getImage("resources/logo.jpeg"));
        aboutDialogPanel.init();
        frame = new CustomFrame("Skileton");

        frame.setSize(900, 600);

        frame.setJMenuBar(GraphicUtils.createMenuBar(
                new String[][]{
                        {"File", "Open Video", "Process Images", null, "Exit"},
                        {"Tools", "Write Video", "Pause", "Start"},
                        {"Help", "About"},
                },
                this,
                null,
                ReflectionUtils.getMethod("processOpenVideo"),
                ReflectionUtils.getMethod("processProcessImages"),
                ReflectionUtils.getMethod("processExit"),

                ReflectionUtils.getMethod("processWriteVideo"),
                ReflectionUtils.getMethod("processPause"),
                ReflectionUtils.getMethod("processStart"),

                ReflectionUtils.getMethod("processAbout")));

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);

    }

    public void setContentPane(Container pane) {
        pane.addMouseMotionListener(this);
        frame.setContentPane(pane);
    }

    //--------------------------------- Menu --------------------------------------------

    public void processOpenVideo() {
        File f = GraphicUtils.chooseFile(frame, PATH, JFileChooser.FILES_ONLY, "Video Files", "avi");
        if (f == null) {
            return;
        }
        try {
            if (fa != null) {
                fa.stop();
            }
            fa = new FrameAccess(handler);
            fa.open(f.getPath());
            handler.init(fa.getImageWidth(), fa.getImageHeight());
            setContentPane(new Visualizer(fa.getImageWidth(), fa.getImageHeight()));
            fa.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processProcessImages() {
        try {
            for (File file : new File(Console.readString("Enter path to images", PATH)).listFiles()) {
                if (file.getPath().endsWith(".bmp")) {
                    BufferedImage img = ImageIO.read(file);
                    int w = img.getWidth(), h = img.getHeight();
                    ImageMetrics.processImage(w, h, img.getRGB(0, 0, w, h, new int[w * h], 0, w));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processExit() {
        System.exit(0);
    }

    public void processWriteVideo() throws Exception {
        int rate = Integer.parseInt(Console.readString("Enter frame rate", "12"));
        new VideoWriter().writeMovie(rate);
    }

    public void processPause() {
        frame.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        wait = true;
    }

    public synchronized void processStart() {
        wait = false;
        frame.getContentPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        notify();
    }

    public void processAbout() {
        aboutDialogPanel.showDialog(frame);
    }

    //--------------------------------- Mouse Motion ------------------------------------

    private int mouseX, mouseY;
    private boolean wait;

    public synchronized void show(int pixels[]) {
        ((Visualizer) frame.getContentPane()).show(pixels);
        if (wait) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (wait) {
            Graphics g = ((Visualizer) frame.getContentPane()).graphics();
            g.setColor(Color.RED);
            g.drawLine(mouseX, mouseY, e.getX(), e.getY());
            frame.repaint();
            mouseMoved(e);
        }
    }

    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    //-----------------------------------------------------------------------------------

    public static void main(String[] args) throws Exception {
        CameraVEC245.main(args);
        main = new Main();
    }

    public static BufferedImage getImage(String path) {
        try {
            return ImageIO.read(getResource(path));
        } catch (IOException e) {
            return null;
        }
    }

    public static InputStream getResource(String path) {
        try {
            InputStream stream = Main.class.getClassLoader().getResourceAsStream(path);
            return stream != null ? stream : new FileInputStream(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
