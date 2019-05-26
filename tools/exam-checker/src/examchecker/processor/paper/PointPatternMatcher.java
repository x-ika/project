package examchecker.processor.paper;

import java.util.*;

public class PointPatternMatcher {

    private static final int INF = (int) 1e9;

    private int n, m, threshold;
    private int[] ax, ay, bx, by;

    private int missPenalty;
    private int minX, minY, maxX, maxY;
    private int[][] error, index;

    private double minError;
    private int[] tx, ty, terr;
    private PlanarTransformation result;

    //-----------------------------------------------------------------------------------

    public PlanarTransformation match(int[] ax, int[] ay, int[] bx, int[] by, int threshold) {
        this.ax = ax;
        this.ay = ay;
        this.bx = bx;
        this.by = by;
        this.threshold = threshold;
        n = ax.length;
        m = bx.length;
        minError = INF;
        tx = new int[n];
        ty = new int[n];
        terr = new int[m];
        result = null;

        defineErrorFunction();

        solve();

        System.out.println("ans = " + result);
        System.out.println("minError = " + minError);

        return result;
    }

    private void defineErrorFunction() {
        missPenalty = 10 * threshold * threshold;
        minX = minY = INF;
        maxX = maxY = -INF;

        for (int i = 0; i < m; i++) {
            minX = Math.min(minX, bx[i]);
            maxX = Math.max(maxX, bx[i]);
            minY = Math.min(minY, by[i]);
            maxY = Math.max(maxY, by[i]);
        }
        minX -= threshold;
        minY -= threshold;
        maxX += threshold;
        maxY += threshold;

        error = new int[maxX - minX + 1][maxY - minY + 1];
        index = new int[maxX - minX + 1][maxY - minY + 1];
        for (int[] f : error) {
            Arrays.fill(f, INF);
        }

        for (int i = 0; i < m; i++) {
            int ox = bx[i] - minX;
            int oy = by[i] - minY;
            for (int x = -threshold; x <= threshold; x++) {
                for (int y = -threshold; y <= threshold; y++) {
                    int xi = ox + x;
                    int yi = oy + y;
                    int t = x * x + y * y;
                    if (error[xi][yi] > t) {
                        error[xi][yi] = t;
                        index[xi][yi] = i;
                    }
                }
            }
        }
    }

    private void solve() {
        Random rnd = new Random(1);
        for (int iter = 0; iter < 10; iter++) {
            int bi = rnd.nextInt(m);
            int bj = rnd.nextInt(m);
            if (bi == bj) {
                continue;
            }
            for (int ai = 0; ai < n; ai++) {
                for (int aj = 0; aj < n; aj++) {
                    if (ai == aj) {
                        continue;
                    }
                    check(ai, bi, aj, bj);
                }
            }
        }
    }

    private void check(int ai, int bi, int aj, int bj) {

        PlanarTransformation transformation = new PlanarTransformation(
                ax[ai], ay[ai], ax[aj], ay[aj],
                bx[bi], by[bi], bx[bj], by[bj]
        );

        Arrays.fill(terr, missPenalty);
        for (int i = 0; i < n; i++) {
            transformation.transform(ax[i], ay[i]);
            tx[i] = (int) Math.round(transformation.getX());
            ty[i] = (int) Math.round(transformation.getY());
            int err = error(tx[i], ty[i]);
            int ind = index(tx[i], ty[i]);
            if (terr[ind] > err) {
                terr[ind] = err;
            }
        }

        int curError = 0;
        for (int i = 0; i < m; i++) {
            if ((curError += terr[i]) > minError) {
                break;
            }
        }

        if (minError > curError) {
            minError = curError;
            result = transformation;
        }

    }

    //-----------------------------------------------------------------------------------

    private int error(int x, int y) {
        return calc(x, y, error, INF);
    }

    private int index(int x, int y) {
        return calc(x, y, index, 0);
    }

    private int calc(int x, int y, int[][] f, int def) {
        if (x < minX || maxX < x || y < minY || maxY < y) {
            return def;
        }
        return f[x - minX][y - minY];
    }

    private int dist2(int i, int j) {
        int dx = ax[i] - bx[j];
        int dy = ay[i] - by[j];
        return dx * dx + dy * dy;
    }

}
