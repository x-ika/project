package examchecker.processor.paper;

import com.simplejcode.commons.algo.GeometryUtils;

public class PlanarTransformation {

    private double alpha, scale, dx, dy;
    private double _sin, _cos;

    private double X, Y;

    public PlanarTransformation(int ax1, int ay1, int ax2, int ay2,
                                int bx1, int by1, int bx2, int by2)
    {
        int adx = ax2 - ax1;
        int ady = ay2 - ay1;
        int bdx = bx2 - bx1;
        int bdy = by2 - by1;

        long scalar = GeometryUtils.scalar(adx, ady, bdx, bdy);
        long vector = GeometryUtils.vector(adx, ady, bdx, bdy);
        alpha = Math.atan2(vector, scalar);
        double d1 = adx * adx + ady * ady;
        double d2 = bdx * bdx + bdy * bdy;
        scale = Math.sqrt(d2 / d1);
        cache();

        transform(ax1, ay1);
        dx = bx1 - X;
        dy = by1 - Y;
    }

    public PlanarTransformation(double alpha, double scale, double dx, double dy) {
        this.alpha = alpha;
        this.scale = scale;
        this.dx = dx;
        this.dy = dy;
        cache();
    }

    private void cache() {
        _sin = Math.sin(alpha);
        _cos = Math.cos(alpha);
    }


    public void transform(int x, int y) {
        X = x * _cos - y * _sin;
        Y = x * _sin + y * _cos;
        X *= scale;
        Y *= scale;
        X += dx;
        Y += dy;
    }

    public void inverse(int x, int y) {
        x -= dx;
        y -= dx;
        x /= scale;
        y /= scale;
        X = x * _cos + y * _sin;
        Y = y * _cos - x * _sin;
    }

    public double getX() {
        return X;
    }

    public double getY() {
        return Y;
    }

    @Override
    public String toString() {
        String format = "[rotation(%.1f) o scale(%.3f) o translation(%.1f,%.1f)]";
        return String.format(format, Math.toDegrees(alpha), scale, dx, dy);
    }

}
