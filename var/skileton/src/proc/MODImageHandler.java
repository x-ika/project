package proc;

import com.simplejcode.commons.misc.improc.*;
import gui.*;
import util.ImageHandler;

import java.awt.*;
import java.util.List;
import java.util.*;

import static java.lang.Math.*;

/**
 * The main class, which is processed images extracted from video.
 */
public final class MODImageHandler implements ImageHandler {

    private static final int N = 5;
    private static final Visualizer[] VIS = new Visualizer[5];

    private int index;
    private double height;
    private int[] C, V, BG;
    private int[][] IN, OUT;
    private List<Rect> list = new ArrayList<>((int) 1e5);
    private List<Double> hs = new ArrayList<>((int) 1e3);

    private ImageProcessor processor;
    private int[][] P;

    public void init(int w, int h) {
        processor = new ImageProcessor(w, h);
        index = 0;
        C = new int[processor.S];
        V = new int[processor.S];
        IN = new int[N][processor.S];
        OUT = new int[N][processor.S];
        for (int i = 0; i < VIS.length; i++) {
            if (VIS[i] != null) {
//                VIS[i].dispose();
            }
            VIS[i] = new Visualizer(processor.W, processor.H);
        }
        P = new int[h][w];
        for (int p = 0; p < processor.S; p++) {
            P[p / processor.W][p % processor.W] = p;
        }
    }

    public void processImage(int[] image) {

        System.out.println("index = " + index);
        if (index == 0) {
            BG = image.clone();
        }
        ImageProcessor.rotate(IN);
        ImageProcessor.rotate(OUT);
        System.arraycopy(image, 0, IN[0], 0, processor.S);
        Main.main.show(IN[0]);
        if (++index < N) {
            return;
        }
        long startTime = System.nanoTime();
        process();
        System.out.println("Time: " + (System.nanoTime() - startTime) / 1e9);
//        try {
//            ImageIO.write(VIS[0].getImage(), "jpg", new File(String.format("out/%04d.jpg", index)));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void process() {
        System.arraycopy(IN[0], 0, OUT[0], 0, processor.S);

        // calculate "speed" of each pixel
        for (int p = 0; p < processor.S; p++) {
            C[p] = ImageProcessor.fdist(IN[0][p], BG[p]) > 1 ? 1 : 0;
        }

        // detect parts of the moving objects
        list.clear();
        processor.buildRects(C, 1, 100, list);
        clusterize();
        processor.drawRects(OUT[0], list, 255 << 16);

        // process each moving object separately
        height = 0;
        for (Rect r : list) {
            if (r.i1 > 2 && r.i2 < processor.H - 2 && r.j1 > 2 && r.j2 < processor.W - 2) {
                processObject(r);
            }
        }

        VIS[0].show(OUT[0]);
        if (height != 0) {
            java.awt.Graphics g = VIS[0].getImage().getGraphics();
            g.setColor(java.awt.Color.black);
            g.setFont(new java.awt.Font("Serif", Font.PLAIN, 30));
            g.drawString(String.format("Height: %.3f", height), 550, 50);
            hs.add(height);
            double mid = 0;
            Collections.sort(hs);
            int sz = 9 * hs.size() / 10;
            for (int i = 0; i < sz; i++) {
                mid += hs.get(i);

            }
            System.out.println(mid / sz);
        }
    }

    private void processObject(Rect r) {
        int n = r.i2 - r.i1;
        int m = r.j2 - r.j1;
        int[][] tmp = new int[n][m];
        Arrays.fill(V, 0);

        for (int i = r.i1; i < r.i2; i++) {
            for (int j = r.j1; j < r.j2; j++) {
                int p = P[i][j];
                C[p] = (int) (100 * ImageProcessor.fdist(IN[0][p], BG[p]));
            }
        }
        //noinspection PrimitiveArrayArgumentToVariableArgMethod
        processor.calculateRowColumnSums(C);
        for (int i = r.i1; i < r.i2; i++) {
            for (int j = r.j1; j < r.j2; j++) {
                int p = P[i][j];
                if (processor.getSum(P[i - 2][j - 2], P[i + 2][j + 2]) > 2e3) {
                    mark(p, 255 << 16);
                }
            }
        }

        for (int i = 0; i < n; i++) {
            tmp[i][0] = C[P[r.i1 + i][r.j1]];
            for (int j = 1; j < m; j++) {
                tmp[i][j] = tmp[i][j - 1] + V[P[r.i1 + i][r.j1 + j]];
            }
        }

        M:
        for (int a = 0; a < n; a++) {
            for (int b = 3; b < m; b++) {
                if (tmp[a][b] - tmp[a][b - 3] == 3) {

                    for (int c = n; c-- > 1; ) {
                        for (int d = 3; d < m; d++) {
                            if (tmp[c][d] - tmp[c][d - 3] == 3) {

                                for (int j = 0; j < m; j++) {
                                    mark(P[r.i1 + a][r.j1 + j], 255 << 8);
                                    mark(P[r.i1 + c][r.j1 + j], 255 << 8);
                                }

                                double h = Math.hypot(a - c, b - d);
                                height = ImageMetrics.getHeight(r.i1 + c, r.j1 + d, h);
//                                System.out.println("h = " + h);

                                int min = 100, best = 0;
                                for (int t = (int) (a + h / 8); t <= a + h / 8; t++) {
                                    if (min > tmp[t][m - 1]) {
                                        min = tmp[t][m - 1];
                                        best = t;
                                    }
                                }
                                int bbest = best + c >> 1;
                                for (int j = 0; j < m; j++) {
                                    mark(P[r.i1 + best][r.j1 + j], 255);
                                    mark(P[r.i1 + bbest][r.j1 + j], 255);
                                }


                                break M;

                            }
                        }
                    }

                }
            }
        }

    }

    //-----------------------------------------------------------------------------------

    private void mark(int p, int c) {
        V[p] = 1;
        OUT[0][p] = c;
    }

    private void clusterize() {
        // clusterize parts
        for (int i = list.size(); i-- > 0; ) {
            Rect r1 = list.get(i);
            while (true) {
                boolean done = true;
                for (int j = i; ++j < list.size(); ) {
                    Rect r2 = list.get(j);
                    if (r1.dist(r2) < 20) {
                        r1.union(r2);
                        list.remove(j--);
                        done = false;
                    }
                }
                if (done) {
                    break;
                }
            }
        }

        // sample filtering
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).area() < 2000) {
                list.remove(i--);
            }
        }

        // +/- 10 pixels
        int d = 10;
        for (Rect r : list) {
            r.i1 = max(r.i1 - d, 0);
            r.j1 = max(r.j1 - d, 0);
            r.i2 = min(r.i2 + d, processor.H);
            r.j2 = min(r.j2 + d, processor.W);
        }
    }

}
