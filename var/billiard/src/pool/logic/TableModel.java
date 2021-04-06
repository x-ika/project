package pool.logic;

import pool.geom.*;

import static pool.utils.Consts.*;

class TableModel {
    private Ball[] balls;

    private Border[] borders;

    private Pocket[] pockets;

    public TableModel() {
        createBalls();
        createBorders();
        creatPockets();
    }

    private void createBalls() {
        int kx = 10, dx = 2 * BALL_RADIUS, dy = 15;
        balls = new Ball[N_BALLS];
        balls[0] = new Ball(X_MIN + 150, (Y_MIN + Y_MAX) / 2);
        balls[1] = new Ball(X_MAX - kx * dx, (Y_MIN + Y_MAX) / 2);
        balls[2] = new Ball(X_MAX - kx * dx + dx, (Y_MIN + Y_MAX) / 2 - dy);
        balls[3] = new Ball(X_MAX - kx * dx + dx, (Y_MIN + Y_MAX) / 2 + dy);
        balls[4] = new Ball(X_MAX - kx * dx + 2 * dx, (Y_MIN + Y_MAX) / 2 - 2 * dy);
        balls[5] = new Ball(X_MAX - kx * dx + 2 * dx, (Y_MIN + Y_MAX) / 2);
        balls[6] = new Ball(X_MAX - kx * dx + 2 * dx, (Y_MIN + Y_MAX) / 2 + 2 * dy);
        balls[7] = new Ball(X_MAX - kx * dx + 3 * dx, (Y_MIN + Y_MAX) / 2 - 3 * dy);
        balls[8] = new Ball(X_MAX - kx * dx + 3 * dx, (Y_MIN + Y_MAX) / 2 - dy);
        balls[9] = new Ball(X_MAX - kx * dx + 3 * dx, (Y_MIN + Y_MAX) / 2 + dy);
        balls[10] = new Ball(X_MAX - kx * dx + 3 * dx, (Y_MIN + Y_MAX) / 2 + 3 * dy);
        balls[11] = new Ball(X_MAX - kx * dx + 4 * dx, (Y_MIN + Y_MAX) / 2 - 4 * dy);
        balls[12] = new Ball(X_MAX - kx * dx + 4 * dx, (Y_MIN + Y_MAX) / 2 - 2 * dy);
        balls[13] = new Ball(X_MAX - kx * dx + 4 * dx, (Y_MIN + Y_MAX) / 2);
        balls[14] = new Ball(X_MAX - kx * dx + 4 * dx, (Y_MIN + Y_MAX) / 2 + 2 * dy);
        balls[15] = new Ball(X_MAX - kx * dx + 4 * dx, (Y_MIN + Y_MAX) / 2 + 4 * dy);
    }

    private void createBorders() {
        borders = new Border[N_BORDERS];
        borders[0] = new Border(70, 50, 335, 50);       //hor  top
        borders[1] = new Border(365, 50, 630, 50);       //hor  top
        borders[2] = new Border(70, 350, 335, 350);      //hor  low
        borders[3] = new Border(365, 350, 630, 350);      //hor  low
        borders[4] = new Border(50, 70, 50, 330);      //vert left
        borders[5] = new Border(650, 70, 650, 330);      //vert right
        borders[6] = new Border(335, 50, 335, 0);        //vert *top
        borders[7] = new Border(335, 350, 335, 400);      //vert *low
        borders[8] = new Border(365, 50, 365, 0);        //vert *top
        borders[9] = new Border(365, 350, 365, 400);      //vert *low
        borders[10] = new Border(50, 70, 30, 50);       //1
        borders[11] = new Border(70, 50, 50, 30);       //1
        borders[12] = new Border(50, 330, 30, 350);      //2
        borders[13] = new Border(70, 350, 50, 370);      //2
        borders[14] = new Border(630, 50, 650, 30);       //3
        borders[15] = new Border(650, 70, 670, 50);       //3
        borders[16] = new Border(630, 350, 650, 370);      //4
        borders[17] = new Border(650, 330, 670, 350);      //4
    }

    private void creatPockets() {
        pockets = new Pocket[N_POCKETS];
        pockets[0] = new Pocket(45, 45);
        pockets[1] = new Pocket(45, 354);
        pockets[2] = new Pocket(350, 35);
        pockets[3] = new Pocket(350, 364);
        pockets[4] = new Pocket(654, 45);
        pockets[5] = new Pocket(654, 354);
    }

    public Ball[] getBalls() {
        return balls;
    }

    public Vector2D getCenter(int i) {
        return balls[i].getCenter();
    }

    public boolean moveBalls() {
        boolean moving = false;
        for (int i = 0; i < N_BALLS; i++) {
            moving |= balls[i].move();
        }
        return moving;
    }

    public boolean isInPocket(int i) {
        for (Pocket pocket : pockets) {
            if (pocket.check(balls[i])) {
                return true;
            }
        }
        return false;
    }

    public boolean isOutOfBorders(int i) {
        for (Border border : borders) {
            if (border.check(balls[i])) {
                return true;
            }
        }
        return false;
    }

    public boolean isSmash(int i, int j) {
        return balls[i].check(balls[j]);
    }
}
