package examchecker.processor.paper;

import com.simplejcode.commons.av.improc.*;
import com.simplejcode.commons.misc.PropertyManager;
import examchecker.core.Constants;
import examchecker.processor.*;
import examchecker.view.ImageView;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public final class PaperProcessor {

    private static PaperProcessor instance = new PaperProcessor();

    public static PaperProcessor getInstance() {
        return instance;
    }

    private PaperProcessor() {
    }

    //-----------------------------------------------------------------------------------
    /*
    Variables
     */

    private List<Rect> template;

    //-----------------------------------------------------------------------------------
    /*
    Fully Temporal
     */

    private double blackWhiteThreshold;

    private Polygon content;
    private int D;
    private int minSquareSize;
    private int maxSquareSize;
    private double maxEdgeRatio;
    private double edgeFillCoefficient;
    private double maxSquareSectionArea;

    private double squareCoefficient;

    private double debugFrameScale;


    private int[] pixelData;
    private int[] bw;

    private ImageProcessor processor;

    //-----------------------------------------------------------------------------------
    /*
    API
     */

    public void loadTemplate(IPaperDefinition def, File file) throws Exception {

        template = null;

        List<IPaperDiv> divs = def.getPaperDivs();
        int expected = divs.stream().map(IPaperDiv::getLength).reduce((a, b) -> a + b).orElse(0);

        BufferedImage image = ImageIO.read(file);

        init(image.getWidth(), image.getHeight());

        // 1. components
        List<Rect> components = getComponents(image);
        // 2. squares
        List<Rect> squares = getSquares(components);
        // 3. pattern
        squares = detectSquarePattern(squares, divs, expected);

        template = squares.size() == expected ? squares : null;

    }

    public IRecResult analyze(IPaperDefinition def, File file, DetectionMode detectionMode) throws Exception {

        System.out.println("Analyzing " + file.getName());

        long startTime = System.nanoTime();

        List<IPaperDiv> divs = def.getPaperDivs();
        int expected = divs.stream().map(IPaperDiv::getLength).reduce((a, b) -> a + b).orElse(0);

        BufferedImage image = ImageIO.read(file);

        init(image.getWidth(), image.getHeight());

        // 1. components
        List<Rect> components = getComponents(image);
        // 2. squares
        List<Rect> squares = getSquares(components);
        // 3. pattern
        squares = detectSquarePattern(squares, divs, expected);
        // 4. filling
        IRecResultImpl result = getResult(file, divs, expected, squares);

        switch (detectionMode) {
            case NORMAL:
                break;
            case BLACK_WHITE:
                showBlackWhiteImage(processor.W, processor.H, bw, components);
                break;
            case DEBUG_INFO:
                showDebugInfo(processor.W, processor.H, bw, expected, squares, result);
                break;
        }

        System.out.printf("Analyze time: %.3f\n", (System.nanoTime() - startTime) / 1e9);

        return result;

    }

    //-----------------------------------------------------------------------------------
    /*
    Initialization
     */

    private void init(int w, int h) {

        PropertyManager manager = PropertyManager.getInstance();

        blackWhiteThreshold = manager.getPropertyDouble(Constants.KEY_RECOGNIZER_BLACK_WHITE_THRESHOLD);

        String contentPage = manager.getProperty(Constants.KEY_RECOGNIZER_CONTENT);
        String[] p = contentPage.split(" ");
        content = new Polygon();
        for (String s : p) {
            String[] q = s.split(",");
            content.addPoint(Integer.parseInt(q[0]) * w / 100, Integer.parseInt(q[1]) * h / 100);
        }

        int lineThikness = manager.getPropertyInt(Constants.KEY_RECOGNIZER_LINE_THICKNESS);
        D = (w + lineThikness / 2) / lineThikness;

        int maxSquaresOnPage = manager.getPropertyInt(Constants.KEY_RECOGNIZER_MAX_SQUARES_ON_PAGE);
        minSquareSize = w / maxSquaresOnPage;

        int minSquaresOnPage = manager.getPropertyInt(Constants.KEY_RECOGNIZER_MIN_SQUARES_ON_PAGE);
        maxSquareSize = w / minSquaresOnPage;

        maxEdgeRatio = manager.getPropertyDouble(Constants.KEY_RECOGNIZER_MAX_EDGE_RATIO);

        edgeFillCoefficient = manager.getPropertyDouble(Constants.KEY_RECOGNIZER_MIN_EDGE_FILL_PERCENTAGE);

        maxSquareSectionArea = manager.getPropertyDouble(Constants.KEY_RECOGNIZER_MAX_SQUARE_SECTION_AREA);

        squareCoefficient = manager.getPropertyDouble(Constants.KEY_RECOGNIZER_MIN_SQUARE_FILL_PERCENTAGE);

        debugFrameScale = manager.getPropertyDouble(Constants.KEY_DEBUG_FRAME_SCALE);

    }

    //-----------------------------------------------------------------------------------
    /*
    Building Components
     */

    private List<Rect> getComponents(BufferedImage image) {

        int w = image.getWidth();
        int h = image.getHeight();

        checkArraySizes(w, h);
        image.getRGB(0, 0, w, h, pixelData, 0, w);

        if (blackWhiteThreshold == 0) {
            int d = 1000;
            int[] count = new int[d + 1];
            for (int i = 0; i < w * h; i++) {
                count[(int) (d * ImageProcessor.getBrightness(pixelData[i]))]++;
            }
            int max = 0, ind;
            for (ind = 0; ind < 8 * d / 10; ind++) {
                max = Math.max(max, count[ind]);
            }
            while (count[ind] < max) {
                ind++;
            }
            blackWhiteThreshold = 1d * ind / d;
            blackWhiteThreshold -= 0.01;
        }

        for (int i = 0; i < w * h; i++) {
            bw[i] = ImageProcessor.getBrightness(pixelData[i]) < blackWhiteThreshold ? 1 : 0;
        }
        processor.calculateRowColumnSums(bw);
        List<Rect> list = new ArrayList<>();
        processor.deleteSmallComponents(bw, 1, 1, 50);
        processor.buildRects(bw, 1, 50, list);
        list.removeIf(t -> !content.contains(t.j1, t.i1) || !content.contains(t.j2, t.i2));
        return list;
    }

    private void checkArraySizes(int w, int h) {
        if (processor == null || processor.W != w || processor.H != h) {
            processor = new ImageProcessor(w, h);
            processor.initComponentAnalysisArrays();
            processor.initSumCalculationArrays();
        }
        int n = processor.S;
        if (pixelData == null || pixelData.length < n) {
            pixelData = new int[2 * n];
            bw = new int[2 * n];
        }
    }

    //-----------------------------------------------------------------------------------
    /*
    Square Detection
     */

    private List<Rect> getSquares(List<Rect> list) {

        list.removeIf(r -> r.area() > maxSquareSize * maxSquareSize << 6);

        List<Rect> squares = new ArrayList<>();
        Rect[] exclude = new Rect[999];
        for (Rect rect : list) {
            int z = 0;
            for (Rect ext; (ext = extract(rect, bw, edgeFillCoefficient, exclude, z)) != null; ) {
                squares.add(ext);
                exclude[z++] = ext;
            }
        }

        for (int i = 0; i < squares.size(); i++) {
            Rect p = squares.get(i);
            for (int j = 0; j < i; j++) {
                Rect q = squares.get(j);
                if (p.sarea(q) > maxSquareSectionArea * Math.min(p.area(), q.area())) {
                    if (p.area() > q.area()) {
                        squares.remove(j--);
                    } else {
                        squares.remove(i);
                    }
                    i--;
                }
            }
        }

        return squares;
    }

    private Rect extract(Rect rect, int[] data, double k, Rect[] exclude, int z) {

        int w = processor.W;

        if (rect.area() < minSquareSize * minSquareSize) {
            return null;
        }

        Rect best = null;

        int ri1 = rect.i1, rj1 = rect.j1;
        int ri2 = rect.i2, rj2 = rect.j2;
        for (int i1 = ri1; i1 < ri2; i1++) {
            for (int j1 = rj1; j1 < rj2; j1++) {
                // opt1 - check top left corner
                if (data[i1 * w + j1] == 0) {
                    continue;
                }
                for (int i2 = i1 + minSquareSize; i2 < Math.min(ri2, i1 + maxSquareSize); i2++) {
                    // opt2 - check left edge
                    if (processor.getColSum(j1, i1, i2) <= k * (i2 - i1 + 1)) {
                        continue;
                    }
                    M:
                    for (int j2 = j1 + minSquareSize; j2 < Math.min(rj2, j1 + maxSquareSize); j2++) {

                        int ww = j2 - j1 + 1, hh = i2 - i1 + 1;
                        if (ww > maxEdgeRatio * hh || hh > maxEdgeRatio * ww) {
                            continue;
                        }

                        for (int i = 0; i < z; i++) {
                            Rect r = exclude[i];
                            if (r.hsec(j1, j2 + 1) * r.vsec(i1, i2 + 1) > maxSquareSectionArea * Math.min(r.area(), ww * hh)) {
                                continue M;
                            }
                        }
                        boolean top = processor.getRowSum(i1, j1, j2) > k * ww;
                        boolean bot = processor.getRowSum(i2, j1, j2) > k * ww;

                        boolean lt = processor.getColSum(j1, i1, i2) > k * hh;
                        boolean rt = processor.getColSum(j2, i1, i2) > k * hh;

                        if (top && bot && lt && rt) {
                            if (best == null || best.area() < ww * hh) {
                                best = new Rect(i1, j1, i2 + 1, j2 + 1);
                            }
                        }

                    }
                }
            }
        }

        return best;

    }

    //-----------------------------------------------------------------------------------
    /*
    Pattern Detection & Sorting
     */

    private List<Rect> detectSquarePattern(List<Rect> squares, List<IPaperDiv> divs, int expected) {
        if (template != null) {
            return detectByTemplate(squares);
        }
        if (squares.size() < expected) {
            return squares;
        }
        return customDetection(squares, divs);
    }

    //-----------------------------------------------------------------------------------

    private List<Rect> detectByTemplate(List<Rect> squares) {
        int[] ax = new int[template.size()];
        int[] ay = new int[template.size()];
        for (int i = 0; i < template.size(); i++) {
            Rect rect = template.get(i);
            ax[i] = rect.i1;
            ay[i] = rect.j1;
        }
        int[] bx = new int[squares.size()];
        int[] by = new int[squares.size()];
        for (int i = 0; i < squares.size(); i++) {
            Rect rect = squares.get(i);
            bx[i] = rect.i1;
            by[i] = rect.j1;
        }
        PlanarTransformation ans = new PointPatternMatcher().match(ax, ay, bx, by, 10);
        if (ans == null) {
            return squares;
        }
        return transformList(template, ans);
    }

    private static List<Rect> transformList(List<Rect> src, PlanarTransformation transformation) {
        List<Rect> dst = new ArrayList<>();
        for (Rect rect : src) {
            transformation.transform(rect.i1, rect.j1);
            int i1 = (int) Math.round(transformation.getX()), j1 = (int) Math.round(transformation.getY());
            transformation.transform(rect.i2, rect.j2);
            int i2 = (int) Math.round(transformation.getX()), j2 = (int) Math.round(transformation.getY());
            dst.add(new Rect(i1, j1, i2, j2));
        }
        return dst;
    }

    //-----------------------------------------------------------------------------------

    private List<Rect> customDetection(List<Rect> squares, List<IPaperDiv> divs) {
        List<List<Rect>> lines = new ArrayList<>();

        M:
        for (IPaperDiv div : divs) {
            int len = div.getColumns();
            for (int i = 0; i < div.getRows(); i++) {

                squares.sort(Comparator.comparingInt(o -> o.i1));
                List<Rect> line = null;
                for (int it = 0; it < squares.size(); it++) {
                    line = new ArrayList<>(squares.subList(0, len));
                    line.sort(Comparator.comparingInt(o -> o.j1));
                    if (getVariance(line) < 2e2) {
                        break;
                    }
                    // move to end of the list
                    Rect rect = worstMember(line);
                    squares.remove(rect);
                    squares.add(rect);
                }

                if (line == null || getVariance(line) > 2e2) {
                    break M;
                }
                lines.add(line);
                squares.removeAll(line);

            }
        }

        for (int i = 0; i < lines.size() - 1; i++) {
            List<Rect> a = lines.get(i);
            List<Rect> b = lines.get(i + 1);

            boolean join = false;
            int minDist = Integer.MAX_VALUE;
            for (Rect r1 : a) {
                for (Rect r2 : b) {
                    int hor = Math.abs(r1.j1 - r2.j1);
                    if (minDist > hor) {
                        minDist = hor;
                        join = r1.vsec(r2) > 0;
                    }
                }
            }

            if (join) {
                lines.remove(i + 1);
                a.addAll(b);
                a.sort(Comparator.comparingInt(o -> o.j1));
            }
        }

        return lines.stream().flatMap(List::stream).collect(Collectors.toList());
    }

    private Rect worstMember(List<Rect> line) {
        Rect ret = null;
        double minVariance = 1e9;
        for (int i = 0; i < line.size(); i++) {
            Rect r = line.remove(i);
            double cur = getVariance(line);
            if (minVariance > cur) {
                minVariance = cur;
                ret = r;
            }
            line.add(i, r);
        }
        return ret;
    }

    private double getVariance(List<Rect> line) {
        double ave = 0;
        int to = line.size() - 1;
        for (int j = 0; j < to; j++) {
            ave += dst(line, j);
        }
        ave /= to;
        double variance = 0;
        for (int j = 0; j < to; j++) {
            variance += Math.pow(dst(line, j) - ave, 2);
        }
        variance /= to;
        return variance;
    }

    private static double dst(List<Rect> list, int ind) {
        Rect r1 = list.get(ind);
        Rect r2 = list.get(ind + 1);
        double d1 = Math.pow(r2.j1 - r1.j1, 2);
        double d2 = Math.pow(r2.w() - r1.w(), 2);
        return Math.sqrt(d1 + d2);
    }

    //-----------------------------------------------------------------------------------
    /*
    Fill Detection
     */

    private IRecResultImpl getResult(File file, List<IPaperDiv> divs, int expected, List<Rect> squares) {

        boolean[] filled = new boolean[squares.size()];

        for (int ind = 0; ind < squares.size(); ind++) {
            Rect rect = squares.get(ind);

            int w = processor.W;
            int p = (rect.i1 + D) * w + rect.j1 + D;
            int q = (rect.i2 - D - 1) * w + rect.j2 - D - 1;
            filled[ind] = processor.getSum(p, q) > squareCoefficient * (rect.w() - 2 * D) * (rect.h() - 2 * D);

        }

        Map<String, Integer> indexById = new HashMap<>();
        boolean[] rec = new boolean[divs.size()];
        int[] off = new int[divs.size() + 1];
        for (int i = 0; i < divs.size(); i++) {
            IPaperDiv div = divs.get(i);
            indexById.put(div.getId(), i);
            rec[i] = expected == filled.length;
            off[i + 1] = off[i] + div.getLength();
        }

        return new IRecResultImpl(file, indexById, rec, off, filled);
    }

    //-----------------------------------------------------------------------------------
    /*
    Show Detection Info
     */

    private void showBlackWhiteImage(int w, int h, int[] bw, List<Rect> components) {

        Arrays.fill(pixelData, -1);
        drawBlackContent(w, bw, components);
        BufferedImage img = createImage(w, h);

        int spacing = w / 25;
        Graphics2D graphics = createGraphics(img, spacing);

        graphics.drawString("blackWhiteThreshold " + blackWhiteThreshold, spacing, spacing);

        show(img);

    }

    private void showDebugInfo(int w, int h, int[] bw, int expected, List<Rect> squares, IRecResultImpl result) {

        Arrays.fill(pixelData, -1);
        drawBlackContent(w, bw, squares);
        drawRedEdges(w, squares);
        BufferedImage img = createImage(w, h);

        int spacing = w / 25;
        Graphics2D graphics = createGraphics(img, spacing);

        for (int i = 0; i < squares.size(); i++) {
            Rect r = squares.get(i);
            if (result.getFilled()[i]) {
                graphics.drawRect(r.j1 - D, r.i1 - D, r.w() + 2 * D, r.h() + 2 * D);
            }
        }

        int[] xx = content.xpoints, yy = content.ypoints;
        graphics.setColor(Color.blue);
        for (int i = 0; i < content.npoints; i++) {
            int j = (i + 1) % content.npoints;
            graphics.drawLine(xx[i], yy[i], xx[j], yy[j]);
        }

        graphics.setColor(Color.black);
        int x = spacing;
        int y = 0;

        y += 2 * spacing;
        graphics.drawString("min square", x, y);
        graphics.fillRect(x, y + 5, minSquareSize, minSquareSize);

        y += 2 * spacing;
        graphics.drawString("max square", x, y);
        graphics.fillRect(x, y + 5, maxSquareSize, maxSquareSize);

        y += 4 * spacing;
        graphics.drawString("tot. found: " + squares.size(), x, y);
        y += 2 * spacing;
        graphics.drawString("expected  : " + expected, x, y);

        // draw indexes
        graphics.setFont(new Font(Font.SERIF, Font.BOLD, spacing / 4));
        for (int i = 0; i < squares.size(); i++) {
            Rect r = squares.get(i);
            graphics.drawString("" + i, r.j1, r.i1 - spacing / 8);
        }

        show(img);

    }

    //-----------------------------------------------------------------------------------

    private BufferedImage createImage(int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, w, h, pixelData, 0, w);
        return img;
    }

    private Graphics2D createGraphics(BufferedImage img, int fontSize) {
        Graphics2D graphics = img.createGraphics();
        graphics.setFont(new Font(Font.SERIF, Font.BOLD, fontSize));
        graphics.setStroke(new BasicStroke(D));
        graphics.setColor(Color.black);
        return graphics;
    }

    private void drawBlackContent(int w, int[] bw) {
        int h = bw.length / w;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                if (bw[i * w + j] > 0) {
                    pixelData[i * w + j] = 0;
                }
            }
        }
    }

    private void drawBlackContent(int w, int[] bw, List<Rect> list) {
        for (Rect rect : list) {
            for (int i = rect.i1; i < rect.i2; i++) {
                for (int j = rect.j1; j < rect.j2; j++) {
                    if (bw[i * w + j] > 0) {
                        pixelData[i * w + j] = 0;
                    }
                }
            }
        }
    }

    private void drawRedEdges(int w, List<Rect> list) {
        for (Rect rect : list) {
            for (int i = rect.i1; i < rect.i2; i++) {
                for (int j = rect.j1; j < rect.j2; j++) {
                    if (i < rect.i1 + D || i >= rect.i2 - D || j < rect.j1 + D || j >= rect.j2 - D) {
                        pixelData[i * w + j] = 255 << 16;
                    }
                }
            }
        }
    }

    private void show(BufferedImage img) {
        int w = (int) (debugFrameScale * img.getWidth());
        int h = (int) (debugFrameScale * img.getHeight());
        new ImageView(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
    }

}
