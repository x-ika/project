import com.simplejcode.commons.gui.*;
import com.simplejcode.commons.gui.Console;
import com.simplejcode.commons.misc.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PaletteVisualizer {

    private CustomFrame frame;

    private void init() throws Exception {
        frame = new CustomFrame("Palette Visualizer");
        frame.setJMenuBar(
                new String[][]{
                        {
                                "File",
                                "Load File",
                                "Show Whole Image",
                                "Animate",
                                "Show Single Column",
                                null,
                                "Exit"
                        },
                        {"Help", "About"},
                },
                this,
                ReflectionUtils.getMethod("handle", Throwable.class),

                ReflectionUtils.getMethod("loadFile"),
                ReflectionUtils.getMethod("showAll"),
                ReflectionUtils.getMethod("showWithPause"),
                ReflectionUtils.getMethod("showColumn"),
                ReflectionUtils.getMethod("actionOnExit"),

                ReflectionUtils.getMethod("actionOnAbout")
        );

        JPanel anim = new JPanel() {
            public void paint(Graphics g) {
                try {
                    PaletteVisualizer.this.paint(this, g);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        anim.setPreferredSize(frame.getContentPane().getPreferredSize());
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        field = new LabelAndField("Animation time in milliseconds", Color.lightGray, 5);
        field.setMaximumSize(new Dimension(300, 50));
        panel.add(field);
        panel.add(anim);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

    public void actionOnExit() {
        System.exit(0);
    }

    public void actionOnAbout() throws Exception {
        AboutDialogPanel aboutDialogPanel = new AboutDialogPanel(
                "RGB Vis", ImageIO.read(FileSystemUtils.getResource("resources/logo.jpg")));
        aboutDialogPanel.init();
        aboutDialogPanel.showDialog(frame);
    }

    public void handle(Throwable e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(frame, "Error occurred when processing your command!", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void reloadSettings() throws IOException {
        settings = new Properties();
        settings.load(new FileReader("resources/init.properties"));
    }

    private void saveSettings() throws IOException {
        settings.store(new FileOutputStream("resources/init.properties"), "");
    }

    //-----------------------------------------------------------------------------------

    private Properties settings;

    private LabelAndField field;
    private int n, m, mode, loaded, preDiff;
    private BufferedImage img, pre[], cols[];

    private int getLast(int row) {
        return row - row % preDiff;
    }

    private void paint(Component c, Graphics g) {

        if (mode == 0 || img == null) {
            return;
        }

        if (mode == 1) {

            int x = c.getWidth() - img.getWidth() >> 1;
            int y = c.getHeight() - img.getHeight() >> 1;
            g.drawImage(img, x, y, null);

        }

        if (mode == 2) {

            int x = c.getWidth() - img.getWidth() >> 1;
            int y = c.getHeight() - img.getHeight() >> 1;
            int from = getLast(loaded);
            if (from > 0) {
                g.drawImage(pre[from], x, y, null);
                x += pre[from].getWidth();
            }
            for (int i = from; i < loaded; i++) {
                g.drawImage(cols[i], x, y, null);
                x += cols[i].getWidth();
            }

        }

        if (mode == 3) {

            int x = c.getWidth() - img.getWidth() >> 1;
            int y = c.getHeight() - img.getHeight() >> 1;
            x += 2 * loaded;
            g.drawImage(cols[loaded], x, y, null);

        }

    }

    public void loadFile() throws Exception {

        reloadSettings();
        String path = settings.getProperty("path", ".");
        File file = GraphicUtils.chooseFile(frame, path, JFileChooser.FILES_ONLY, "Text Files", "txt");

        if (file == null) {
            return;
        }

        settings.put("path", file.getParent());
        saveSettings();

        BufferedReader in = new BufferedReader(new FileReader(file));
        List<String> list = new ArrayList<>();
        for (String s; (s = in.readLine()) != null && !s.isEmpty(); ) {
            list.add(s);
        }
        n = list.size();
        m = list.get(0).split(",").length / 3;
        img = new BufferedImage(2 * n, 2 * m, BufferedImage.TYPE_INT_RGB);
        cols = new BufferedImage[n];
        pre = new BufferedImage[n];
        preDiff = Math.max((int) (1d * m * m * n / 1e6), 1);
        long startTime = System.nanoTime();
        for (int i = 0; i < cols.length; i++) {
            cols[i] = new BufferedImage(2, 2 * m, BufferedImage.TYPE_INT_RGB);
            if (i > 0 && i % preDiff == 0) {
                pre[i] = new BufferedImage(2 * i, 2 * m, BufferedImage.TYPE_INT_RGB);
            }
        }

        for (int i = 0; i < n; i++) {
            String[] p = list.get(i).split(",");
            for (int j = 0; j < m; j++) {
                String rs = p[3 * j].substring(1);
                String gs = p[3 * j + 1];
                String bs = p[3 * j + 2].substring(0, p[3 * j + 2].length() - 1);
                int r = Integer.parseInt(rs);
                int g = Integer.parseInt(gs);
                int b = Integer.parseInt(bs);
                int rgb = r << 16 | g << 8 | b;
                for (int di = 0; di < 2; di++) {
                    for (int dj = 0; dj < 2; dj++) {

                        int ii = 2 * i + di;
                        int jj = 2 * j + dj;
                        img.setRGB(ii, jj, rgb);
                        cols[i].setRGB(di, jj, rgb);
                        int from = getLast(i) + preDiff;
                        for (int t = from; t < n; t += preDiff) {
                            pre[t].setRGB(ii, jj, rgb);
                        }

                    }
                }
            }
        }
        System.out.println("preDiff = " + preDiff);
        System.out.println("Time: " + (System.nanoTime() - startTime) / 1e9);
        mode = 0;
        frame.repaint();
        System.gc();
        JOptionPane.showMessageDialog(frame, "Command complete", "Info", JOptionPane.INFORMATION_MESSAGE);

    }

    public void showAll() {

        mode = 1;
        frame.repaint();

    }

    public void showWithPause() {

        int delay = Integer.parseInt(field.getText()) / m;

        GraphicUtils.pause(100);

        mode = 2;
        for (int i = 0; i < n; i++) {
            loaded = i;
            frame.repaint();
            GraphicUtils.pause(delay);
        }

        mode = 1;
        frame.repaint();

    }

    public void showColumn() {

        mode = 3;
        String s = Console.readString("Enter column index from 1 to " + n);
        loaded = Integer.parseInt(s) - 1;
        if (loaded < 0 || n <= loaded) {
            loaded = 0;
            JOptionPane.showMessageDialog(frame, "Index should be from 1 to " + n, "Error", JOptionPane.ERROR_MESSAGE);
        }
        frame.repaint();

    }

    //-----------------------------------------------------------------------------------

    public static void main(String[] args) throws Exception {
        new PaletteVisualizer().init();

    }

}
