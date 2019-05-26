package pool.geom;

import pool.utils.Consts;

public class Pocket extends TableObject {
    public Pocket(int x, int y) {
        super(x, y);
    }

    public boolean check(Ball ball) {
        return check(ball, Consts.POCKET_RADIUS);
    }
}
