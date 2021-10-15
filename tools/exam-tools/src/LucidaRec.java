import com.simplejcode.commons.av.improc.*;
import com.simplejcode.commons.gui.Console;
import com.simplejcode.commons.gui.*;
import com.simplejcode.commons.misc.util.*;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.xml.bind.JAXBElement;
import java.awt.Color;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.*;

@SuppressWarnings({"unused"})
public class LucidaRec {

    private CustomFrame frame;
    private Properties settings;
    private PatternRecognizer patternRecognizer;

    private void init() throws Exception {
        frame = new CustomFrame("Exam Tools");
        frame.setJMenuBar(
                new String[][]{
                        {
                                "File",
                                "Reload Settings",
                                null,
                                "Rename Scanned Files",
                                "Generate Labeled .doc Files",
                                "Clean Name, Surname, etc.",
                                null,
                                "Show Search Area",
                                "Change model",
                                null,
                                "Exit"
                        },
                        {"Help", "About"},
                },
                this,
                ReflectionUtils.getMethod("handle", Throwable.class),

                ReflectionUtils.getMethod("reloadSettings"),

                ReflectionUtils.getMethod("renameFiles"),
                ReflectionUtils.getMethod("generateDocs"),
                ReflectionUtils.getMethod("cleanTitles"),

                ReflectionUtils.getMethod("showSearchArea"),
                ReflectionUtils.getMethod("updateModel"),
                ReflectionUtils.getMethod("actionOnExit"),

                ReflectionUtils.getMethod("actionOnAbout")
        );
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        reloadSettings();

        patternRecognizer = new PatternRecognizer();
        patternRecognizer.setBlackWhiteThreshold(Double.parseDouble(settings.getProperty("black_white_threshold")));

        patternRecognizer.setPaterns(createExactModel());

    }

    private float[][][] createExactModel() throws Exception {

        BufferedImage image = ImageIO.read(new File("resources/lucida_console72.png"));
        int w = image.getWidth(), h = image.getHeight();
        int[] bw = ImageProcessor.getBlackWhiteImage(image);

        List<Rect> list = new ArrayList<>();
        ImageProcessor imageProcessor = new ImageProcessor(w, h);
        imageProcessor.initComponentAnalysisArrays();
        imageProcessor.initSumCalculationArrays();
        imageProcessor.buildRects(bw, 2, 25, list);
        list.sort((o1, o2) -> 1000 * ((o1.i1 - o2.i1) / 10) + o1.j1 - o2.j1);
        // {[digit], 'M', 'F'}
        for (int i = 36; i-- > 10; ) {
            if (i != 22 && i != 15) {
                list.remove(i);
            }
        }
        float[][][] data = new float[list.size()][][];
        for (int i = 0; i < list.size(); i++) {
            Rect rect = list.get(i);
            data[i] = new float[rect.h()][rect.w()];
            float[] tmp = ImageProcessor.getBrightnessMap(image, rect.j1, rect.i1, rect.w(), rect.h());
            for (int row = 0; row < rect.h(); row++) {
                System.arraycopy(tmp, row * rect.w(), data[i][row], 0, rect.w());
            }
        }

        return data;

    }

    public void actionOnExit() {
        System.exit(0);
    }

    public void actionOnAbout() throws Exception {
        AboutDialogPanel aboutDialogPanel = new AboutDialogPanel("ExTools", ImageIO.read(new File("resources/logo.jpeg")));
        aboutDialogPanel.init();
        aboutDialogPanel.showDialog(frame);
    }

    public void handle(Throwable e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(frame, "Error occurred when processing your command!", "Error", JOptionPane.ERROR_MESSAGE);
    }

    //-----------------------------------------------------------------------------------

    public void updateModel() throws Exception {

        File dir = chooseDir(settings.getProperty("ocr_path")), files[];
        if (dir == null || (files = dir.listFiles()) == null) {
            return;
        }

        String[] recognize = settings.getProperty("recognize_area").split(",");
        double px = Double.parseDouble(recognize[0]);
        double py = Double.parseDouble(recognize[1]);
        double pw = Double.parseDouble(recognize[2]);
        double ph = Double.parseDouble(recognize[3]);

        List<Part> data = new ArrayList<>();
        for (File file : files) {
            if (file.isFile()) {
                BufferedImage im = ImageIO.read(file);
                int rx = (int) (im.getWidth() * px), rw = (int) (im.getWidth() * pw);
                int ry = (int) (im.getHeight() * py), rh = (int) (im.getHeight() * ph);
                data.addAll(getParts(im, rx, ry, rw, rh));
            }
        }

        int k = 12;

        Part[] clusters = kmeans(data, k, createExactModel());

        int hh = 0, ww = 0;
        for (int ind = 0; ind < k; ind++) {
            Part p = clusters[ind];
            ww += p.w;
            hh = Math.max(hh, p.h);
        }
        float[] ff = new float[ww * hh];
        Arrays.fill(ff, 0.99f);
        int cw = 0;
        for (int ind = 0; ind < k; ind++) {
            Part p = clusters[ind];
            for (int i = 0; i < p.h; i++) {
                System.arraycopy(p.f, i * p.w, ff, i * ww + cw, p.w);
            }
            cw += p.w;
        }
        GraphicUtils.showPixels(ff, ww, hh);

        float[][][] model = new float[k][][];
        for (int ind = 0; ind < k; ind++) {
            Part p = clusters[ind];
            model[ind] = new float[p.h][p.w];
            for (int i = 0; i < p.h; i++) {
                for (int j = 0; j < p.w; j++) {
                    model[ind][i][j] = p.f[i * p.w + j];
                }
            }
        }
        patternRecognizer.setPaterns(model);

        JOptionPane.showMessageDialog(frame, "Command complete", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private static final class Part {
        int w, h;
        float[] f;

        public Part(int w, int h, float[] f) {
            this.w = w;
            this.h = h;
            this.f = f;
        }
    }

    private List<Part> getParts(BufferedImage image, int x, int y, int w, int h) {
        List<Part> list = new ArrayList<>();
        for (Rect rect : patternRecognizer.getRects(image, x, y, w, h)) {
            float[] data = ImageProcessor.getBrightnessMap(image, x + rect.j1, y + rect.i1, rect.w(), rect.h());
            list.add(new Part(rect.w(), rect.h(), data));
        }
        return list;
    }

    private static Part[] kmeans(List<Part> list, int k, float[][][] init) {

        int n = list.size();

        Part[] centers = new Part[k];
        for (int ind = 0; ind < k; ind++) {
            int h = init[ind].length, w = init[ind][0].length;
            float[] f = new float[h * w];
            centers[ind] = new Part(w, h, f);
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    f[i * w + j] = init[ind][i][j];
                }
            }
        }
        int[] assignment = new int[n];

        for (boolean more = true; more; ) {

            more = false;

            for (int i = 0; i < n; i++) {
                Part p = list.get(i);
                int old = assignment[i];
                double min = 1e9;
                for (int j = 0; j < k; j++) {
                    double cur = dist(p, centers[j]);
                    if (min > cur) {
                        min = cur;
                        assignment[i] = j;
                    }
                }
                more |= assignment[i] != old;
            }

            for (int i = 0; i < k; i++) {
                Arrays.fill(centers[i].f, 0);
                int t = 0;
                for (int j = 0; j < n; j++) {
                    if (assignment[j] == i) {
                        t++;
                        add(centers[i], list.get(j));
                    }
                }
                if (t != 0) {
                    div(centers[i], t);
                }
            }

        }

        return centers;

    }

    private static double dist(Part p1, Part p2) {
        double sum = 0;
        int h1 = p1.h, w1 = p1.w;
        int h2 = p2.h, w2 = p2.w;
        float fi = 1f * h2 / h1, fj = 1f * w2 / w1;
        float[] f1 = p1.f, f2 = p2.f;
        for (int i = 0; i < h1; i++) {
            for (int j = 0; j < w1; j++) {
                int ii = (int) (i * fi), jj = (int) (j * fj);
                float d = f2[ii * w2 + jj] - f1[i * w1 + j];
                sum += d * d;
            }
        }
        return sum;
    }

    private static void add(Part p1, Part p2) {
        int h1 = p1.h, w1 = p1.w;
        int h2 = p2.h, w2 = p2.w;
        float fi = 1f * h2 / h1, fj = 1f * w2 / w1;
        float[] f1 = p1.f, f2 = p2.f;
        for (int i = 0; i < h1; i++) {
            for (int j = 0; j < w1; j++) {
                int ii = (int) (i * fi), jj = (int) (j * fj);
                f1[i * w1 + j] += f2[ii * w2 + jj];
            }
        }
    }

    private static void div(Part p, int k) {
        float[] f = p.f;
        for (int i = 0; i < f.length; i++) {
            f[i] /= k;
        }
    }

    //-----------------------------------------------------------------------------------

    public void reloadSettings() throws Exception {
        settings = new Properties();
        settings.load(new FileReader("resources/init.properties"));
    }

    //-----------------------------------------------------------------------------------

    public void renameFiles() throws Exception {

        File dir = chooseDir(settings.getProperty("ocr_path")), files[];
        if (dir == null || (files = dir.listFiles()) == null) {
            return;
        }

        String[] recognize = settings.getProperty("recognize_area").split(",");
        double px = Double.parseDouble(recognize[0]);
        double py = Double.parseDouble(recognize[1]);
        double pw = Double.parseDouble(recognize[2]);
        double ph = Double.parseDouble(recognize[3]);

        Map<String, Integer> paging = new HashMap<>();
        int ind = 0;
        String prev = null;
        for (File file : files) {
            if (!file.isFile()) {
                continue;
            }

            BufferedImage im = ImageIO.read(file);
            int rx = (int) (im.getWidth() * px), rw = (int) (im.getWidth() * pw);
            int ry = (int) (im.getHeight() * py), rh = (int) (im.getHeight() * ph);
            String label = ind++ % 2 == -1 ? prev : recognize(im, rx, ry, rw, rh);
            prev = label;
            System.out.println(label);
            boolean ok = check(label);

            // add page number
            int pageNum = paging.getOrDefault(label, 1);
            paging.put(label, pageNum + 1);
            label += "_" + pageNum;

            // rename
            String name = file.getName(), ext = name.substring(name.lastIndexOf('.'));
            String in = ok ? File.separator : "/ND/";
            File dest = new File(file.getParent() + in + label + ext);
            dest.getParentFile().mkdir();
            file.renameTo(dest);

        }

        JOptionPane.showMessageDialog(frame, "Command complete", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private String recognize(BufferedImage image, int x, int y, int w, int h) throws Exception {
        Map<Rect, Integer> map = new TreeMap<>((o1, o2) -> 1000 * (o1.j1 - o2.j1) + o1.i1 - o2.i1);
        Map<Rect, float[]> matches = patternRecognizer.recognizePatterns(image, x, y, w, h);
        double threshold = Double.parseDouble(settings.getProperty("match_threshold"));
        for (Rect rect : matches.keySet()) {
            float[] p = matches.get(rect);
            int ind = maxInd(p);
            if (p[ind] > threshold) {
                map.put(rect, ind);
            }
        }
        int minAcceptedArea = 0;
        for (Rect rect : map.keySet()) {
            minAcceptedArea = Math.max(minAcceptedArea, rect.area() / 4);
        }
        String s = "";
        for (Rect rect : map.keySet()) {
            if (rect.area() > minAcceptedArea) {
                int p = map.get(rect);
                s += (char) (p < 10 ? '0' + p : p == 10 ? 'F' : 'M');
            }
        }
        return s;
    }

    private int maxInd(float[] a) {
        int ind = -1;
        for (int i = 0; i < a.length; i++) {
            if (ind == -1 || a[ind] < a[i]) {
                ind = i;
            }
        }
        return ind;
    }

    private boolean check(String label) {
        String[] limits = settings.getProperty("code_limits").split(",");
        int min = Integer.parseInt(limits[0]);
        int max = Integer.parseInt(limits[1]);
        return label != null && min <= label.length() && label.length() <= max;
    }

    //-----------------------------------------------------------------------------------

    public void generateDocs() throws Exception {

        File dir = chooseDir(settings.getProperty("doc_path")), files[];
        if (dir == null || (files = dir.listFiles()) == null) {
            return;
        }
        String[] params = Console.readString("Enter prefix for labeling and number of documents to be generated", "M0713 200").split(" ");
        if (params.length != 2) {
            JOptionPane.showMessageDialog(frame, "You should enter two space separated strings!", "Error", JOptionPane.ERROR_MESSAGE);
            generateDocs();
            return;
        }
        int num = Integer.parseInt(params[1]);


        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(files[0]);
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();

        List<Text> codeHolders = new ArrayList<>();
        List<Text> dateHolders = new ArrayList<>();

        for (Object o1 : documentPart.getContent()) {
            if (o1.toString().contains("#")) {
                P p = (P) o1;
                for (Object o2 : p.getContent()) {
                    R r = (R) o2;
                    for (Object o3 : r.getContent()) {
                        Text t = (Text) ((JAXBElement<?>) o3).getValue();
                        if (t.getValue().equals("#")) {
                            codeHolders.add(t);
                        }
                        if (t.getValue().equals("$")) {
                            dateHolders.add(t);
                        }
                    }
                }
            }
        }
        if (codeHolders.size() == 0) {
            JOptionPane.showMessageDialog(frame, "Invalid .docx template file!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String date = DateUtils.currentTime();
        for (Text text : dateHolders) {
            text.setValue(date);
        }
        for (int i = 0; i < num; ) {
            for (Text text : codeHolders) {
                String end = String.format("%03d", ++i);
                text.setValue(params[0] + end);
            }
            wordMLPackage.save(new File(dir.getAbsolutePath() + "/" + String.format("%03d", i) + ".docx"));
        }

        JOptionPane.showMessageDialog(frame, "Command complete", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    //-----------------------------------------------------------------------------------

    public void cleanTitles() throws Exception {

        File dir = chooseDir(settings.getProperty("cln_path")), files[];
        if (dir == null || (files = dir.listFiles()) == null) {
            return;
        }
        String[] cleanDims = settings.getProperty("clean_rect").split(",");
        double pw = Double.parseDouble(cleanDims[0]);
        double ph = Double.parseDouble(cleanDims[1]);
        for (File file : files) {
            if (file.isFile()) {
                BufferedImage im = ImageIO.read(file);
                Graphics g = im.getGraphics();
                g.setColor(Color.white);
                g.fillRect(0, 0, (int) (im.getWidth() * pw), (int) (im.getHeight() * ph));
                ImageIO.write(im, "jpeg", file);
            }
        }

        JOptionPane.showMessageDialog(frame, "Command complete", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    //-----------------------------------------------------------------------------------

    public void showSearchArea() throws Exception {
        String[] recognize = settings.getProperty("recognize_area").split(",");
        double px = Double.parseDouble(recognize[0]);
        double py = Double.parseDouble(recognize[1]);
        double pw = Double.parseDouble(recognize[2]);
        double ph = Double.parseDouble(recognize[3]);

        File file = GraphicUtils.chooseFile(frame, settings.getProperty("ocr_path"), JFileChooser.FILES_ONLY);
        if (file == null) {
            return;
        }
        BufferedImage im = ImageIO.read(file);
        int rx = (int) (im.getWidth() * px), rw = (int) (im.getWidth() * pw);
        int ry = (int) (im.getHeight() * py), rh = (int) (im.getHeight() * ph);
        BufferedImage sub = im.getSubimage(rx, ry, rw, rh);
        int nx = 100, ny = 100;

        Graphics g = sub.getGraphics();
        g.setColor(Color.black);
        g.setFont(new Font("Serif", Font.BOLD, 12));
        for (int x = 2; x < nx * pw; x++) {
            int cx = im.getWidth() * x / nx;
            g.drawLine(cx, 0, cx, 20);
            if (x % 2 == 0) {
                g.drawString(String.format("%.2f", px + 1d * x / nx), cx + 5, 20);
            }
        }
        for (int y = 2; y < ny * ph; y++) {
            int cy = im.getHeight() * y / ny;
            g.drawLine(0, cy, 20, cy);
            if (y % 2 == 0) {
                g.drawString(String.format("%.2f", py + 1d * y / ny), 25, cy);
            }
        }
        GraphicUtils.showImage(sub);
    }

    //-----------------------------------------------------------------------------------

    public synchronized File chooseDir(String def) {
        return GraphicUtils.chooseFile(frame, def, JFileChooser.DIRECTORIES_ONLY);
    }

    public static void main(String[] args) throws Exception {
        new LucidaRec().init();

    }

}
