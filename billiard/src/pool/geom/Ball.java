package pool.geom;

import static pool.utils.Consts.*;

public class Ball extends TableObject {
    private static final double ACCELERATION;

    static {
        ACCELERATION = MAX_BALL_SPEED / MAX_CADRES;
    }

    private Vector2D speed;

    public Vector2D point;

    public Ball(double x, double y) {
        super(x, y);
        init(x, y);
    }

    public void init(double x, double y) {
        speed = new Vector2D();
        point = new Vector2D();
        center.place(x, y);
    }

    public boolean move() {
        double length = speed.length();
        if (length == 0) {
            return false;
        }
        point = Sphere.move(point, speed);
        center.plus(speed);
        Vector2D acc = Vector2D.mul(speed, ACCELERATION / Math.pow(length, 0.6));
        speed.minus(acc);
        return length > acc.length();
    }

    public boolean check(Ball ball) {
        boolean answer = !check(ball, 2 * BALL_RADIUS) &&
                Vector2D.add(ball.center, ball.speed).distanceTo(
                        Vector2D.add(center, speed)) <= 2 * BALL_RADIUS;
        if (answer) {
            changeSpeeds(ball);
        }
        return answer;
    }

    private void changeSpeeds(Ball ball) {
        Vector2D axis = Vector2D.sub(ball.center, center);
        Vector2D[] proections0 = speed.proection(axis);
        Vector2D[] proections1 = ball.speed.proection(axis);
        speed = proections0[1].plus(proections1[0]);
        ball.speed = proections0[0].plus(proections1[1]);
    }

    public Vector2D getSpeed() {
        return speed;
    }

    public void setSpeed(Vector2D v) {
        speed = v;
    }

    public void speedChanged(int x, int y, double strength) {
        Vector2D delta = new Vector2D(x, y).minus(center);
        double distance = delta.length();
        if (distance == 0) {
            return;
        }
        speed = delta.multiply(MAX_BALL_SPEED * strength / distance);
    }
}
