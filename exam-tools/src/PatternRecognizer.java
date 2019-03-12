import com.simplejcode.commons.av.improc.*;

import java.awt.image.BufferedImage;
import java.util.*;

public class PatternRecognizer {

    private double blackWhiteThreshold;
    private float[][][] patterns;

    public PatternRecognizer() {
    }

    public void setBlackWhiteThreshold(double blackWhiteThreshold) {
        this.blackWhiteThreshold = blackWhiteThreshold;
    }

    public void setPaterns(float[][][] patterns) {
        this.patterns = patterns;
        int max = 0;
        for (float[][] p : patterns) {
            max = Math.max(max, p.length * p[0].length);
        }
    }

    public Map<Rect, float[]> recognizePatterns(BufferedImage image, int x, int y, int w, int h) throws Exception {

        List<Rect> list = getRects(image, x, y, w, h);

        float[] original = ImageProcessor.getBrightnessMap(image, x, y, w, h);
        Map<Rect, float[]> map = new HashMap<>();
        for (Rect rect : list) {
            map.put(rect, match(original, w, rect));
        }
        return map;

    }

    public List<Rect> getRects(BufferedImage image, int x, int y, int w, int h) {
        int[] pixelData = image.getRGB(x, y, w, h, new int[w * h], 0, w);
        int[] bw = new int[w * h];
        for (int i = 0; i < w * h; i++) {
            bw[i] = ImageProcessor.getBrightness(pixelData[i]) < blackWhiteThreshold ? 1 : 0;
        }
        ImageProcessor processor = new ImageProcessor(w, h);
        List<Rect> list = new ArrayList<>();
        processor.buildRects(bw, 2, 25, list);
        return list;
    }

    // recognize a pattern in given rectangle
    private float[] match(float[] C, int cw, Rect r) {
        float[] ret = new float[patterns.length];
        float sum = 0;
        for (int pi = 0; pi < patterns.length; pi++) {
            float[][] p = patterns[pi];
            int ph = p.length, pw = p[0].length;

            int h = r.h(), w = r.w();
            float fx = 1f * w / pw, fy = 1f * h / ph;
            for (int i = 0; i < ph; i++) {
                for (int j = 0; j < pw; j++) {
                    int x = (int) (j * fx), y = (int) (i * fy);
                    float d = C[(y + r.i1) * cw + x + r.j1] - p[i][j];
                    ret[pi] += d * d;
                }
            }
            ret[pi] = 1f / ret[pi];
            sum += ret[pi];

        }
        for (int i = 0; i < patterns.length; i++) {
            ret[i] /= sum;
        }
        return ret;
    }

}
