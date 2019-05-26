package pool.geom;

public class Vector2D {
    public double x, y;

    public Vector2D(double x, double y) {
        place(x, y);
    }

    public Vector2D(Vector2D v) {
        this(v.x, v.y);
    }

    public Vector2D() {
        this(0, 0);
    }

    void place(double x, double y) {
        this.x = x;
        this.y = y;
    }

    static Vector2D add(Vector2D v1, Vector2D v2) {
        return new Vector2D(v1.x + v2.x, v1.y + v2.y);
    }

    static Vector2D sub(Vector2D v1, Vector2D v2) {
        return new Vector2D(v1.x - v2.x, v1.y - v2.y);
    }

    static Vector2D mul(Vector2D v, double k) {
        return new Vector2D(k * v.x, k * v.y);
    }

    Vector2D plus(Vector2D v) {
        x += v.x;
        y += v.y;
        return this;
    }

    Vector2D minus(Vector2D v) {
        x -= v.x;
        y -= v.y;
        return this;
    }

    Vector2D multiply(double k) {
        x *= k;
        y *= k;
        return this;
    }

    Vector2D normal() {
        double t = x;
        x = -y;
        y = t;
        return this;
    }

    public double distanceTo(Vector2D v) {
        return distanceTo(v.x, v.y);
    }

    public double distanceTo(double x, double y) {
        x -= this.x;
        y -= this.y;
        return Math.sqrt(x * x + y * y);
    }

    double scalar(Vector2D v) {
        return x * v.x + y * v.y;
    }

    double lengthSq() {
        return x * x + y * y;
    }

    double length() {
        return Math.sqrt(x * x + y * y);
    }

    Vector2D[] proection(Vector2D xAxis) {
        Vector2D yAxis = new Vector2D(xAxis).normal();
        double l = xAxis.lengthSq(),
                kx = scalar(xAxis) / l,
                ky = scalar(yAxis) / l;
        return new Vector2D[]{mul(xAxis, kx), mul(yAxis, ky)};
    }

    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
