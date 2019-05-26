package pool.geom;

import pool.utils.Consts;

public class Border {
    private Vector2D begin;

    private Vector2D end;

    private double length;

    public Border(int x1, int y1, int x2, int y2) {
        begin = new Vector2D(x1, y1);
        end = new Vector2D(x2, y2);
        length = begin.distanceTo(end);
    }

    public boolean check(Ball ball) {
        if (outType(ball.getCenter()) != 0) {
            return false;
        }
        switch (outType(Vector2D.add(ball.getCenter(), ball.getSpeed()))) {
            case 1:
                Vector2D[] proections = ball.getSpeed().proection(Vector2D.sub(begin, end));
                ball.setSpeed(proections[0].minus(proections[1]));
                return true;
            case 2:
                changeSpeed(ball, begin);
                return true;
            case 3:
                changeSpeed(ball, end);
                return true;
            default:
                return false;
        }
    }

    private int outType(Vector2D center) {
        double distance1 = center.distanceTo(begin),
                distance2 = center.distanceTo(end),
                area = Math.abs(center.x * (begin.y - end.y) + begin.x *
                        (end.y - center.y) + end.x * (center.y - begin.y));
        if (area < Consts.BALL_RADIUS * length && length > distance1 && length > distance2) {
            return 1;
        }
        if (distance1 < Consts.BALL_RADIUS) {
            return 2;
        }
        if (distance1 < Consts.BALL_RADIUS) {
            return 3;
        }
        return 0;
    }

    private void changeSpeed(Ball ball, Vector2D v) {
        double arg = v.y != ball.getCenter().x ? 2 * Math.atan((v.x - ball.getCenter().x) /
                (v.y - ball.getCenter().y)) : Math.PI,
                cos = Math.cos(arg), sin = Math.sin(arg),
                vx = ball.getSpeed().x, vy = ball.getSpeed().y;
        ball.setSpeed(new Vector2D(+cos * vx - sin * vy, -sin * vx - cos * vy));
    }
}
