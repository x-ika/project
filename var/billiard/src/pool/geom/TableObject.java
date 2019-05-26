package pool.geom;

public abstract class TableObject {
    protected Vector2D center;

    protected TableObject(double x, double y) {
        center = new Vector2D(x, y);
    }

    public Vector2D getCenter() {
        return center;
    }

    public boolean check(TableObject object, double minDist) {
        return center.distanceTo(object.center) <= minDist;
    }
}
