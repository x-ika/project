package pool.geom;

import pool.utils.Consts;

public final class Sphere {
    private static final int R = Consts.BALL_RADIUS;

    public static Vector2D move(Vector2D point, Vector2D d) {
        double length = d.length();
        if (length == 0) {
            return point;
        }
        Vector2D[] v = point.proection(d);
        double r = Math.sqrt(R * R - v[1].lengthSq());
        double len = length % (2 * Math.PI * r);
        double x = Math.signum(v[0].scalar(d)) * Math.min(v[0].length(), r);
        double nlen = r * Math.asin(x / r) + len;

        Vector2D p = Vector2D.mul(d, r / length);
        if (nlen >= r * Math.PI / 2) {
            nlen -= r * Math.PI / 2;
            v[1].plus(p.multiply(Math.cos(nlen / r)));
            return v[1].normal().normal();
        }
        return v[1].plus(p.multiply(Math.sin(nlen / r)));
    }
}
