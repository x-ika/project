public class Move {
    public int desk[][], x1, y1, x2, y2, value1, value2, result;

    public Move() {}
    public Move(int[][] d, int kx1, int ky1, int kx2, int ky2) {
        desk = d;
        x1 = kx1; y1 = ky1;
        x2 = kx2; y2 = ky2;
        value1 = desk[x1][y1];
        value2 = desk[x2][y2];
        result = Figure.COST_OF[Math.abs(value2)];
    }

    public String toString() {
        return "" + x1 + y1 + x2 + y2;
    }

    public boolean isCilledTurne() {
        return value2 != 0;
    }

    public int getCilledX() {
        return x2;
    }

    public int getCilledY() {
        return y2;
    }

    public void forward() {
        desk[x2][y2] = value1;
        desk[x1][y1] = 0;
    }

    public void back() {
        desk[x1][y1] = value1;
        desk[x2][y2] = value2;
    }
}

class HMove extends Move {
    public int casedValue;

    public HMove(int[][] d, int kx1, int ky1, int kx2, int ky2, int cased) {
        super(d, kx1, ky1, kx2, ky2);
        casedValue = cased;
        result += Figure.COST_OF[Math.abs(cased)] - Figure.COST_OF[Math.abs(value1)];
    }

    public String toString() {
        return super.toString() + casedValue;
    }

    public void forward() {
        desk[x2][y2] = casedValue;
        desk[x1][y1] = 0;
    }

    public void back() {
        desk[x1][y1] = value1;
        desk[x2][y2] = value2;
    }
}

class DMove extends Move {
    public DMove(int[][] d, int kx1, int ky1, int kx2, int ky2) {
        desk = d;
        x1 = kx1; y1 = ky1;
        x2 = kx2; y2 = ky2;
        value1 = desk[x1][y1];
        value2 = desk[x2][y1];
        result = Figure.COST_OF[Math.abs(value2)];
    }

    public boolean isCilledTurne() {
        return true;
    }

    public int getCilledX() {
        return x2;
    }

    public int getCilledY() {
        return y1;
    }

    public void forward() {
        desk[x2][y2] = value1;
        desk[x1][y1] = desk[x2][y1] = 0;
    }

    public void back() {
        desk[x1][y1] = value1;
        desk[x2][y1] = value2;
        desk[x2][y2] = 0;
    }
}

class Rook extends Move {
    public int kingX2;

    Rook(int[][] d, int y, int signX) {
        desk = d;
        y1 = y2 = y;
        x1 = signX > 0? 7 : 0;
        x2 = 4 + signX;
        kingX2 = 4 + 2 * signX;
        value1 = desk[x1][y];
        value2 = desk[4][y];
    }

    public String toString() {
        return "" + 4 + y1 + kingX2 + y1;
    }

    public boolean isCilledTurne() {
        return false;
    }

    public void forward() {
        desk[x2][y1] = value1;
        desk[kingX2][y1] = value2;
        desk[x1][y1] = desk[4][y1] = 0;
    }

    public void back() {
        desk[x1][y1] = value1;
        desk[4][y1] = value2;
        desk[x2][y1] = desk[kingX2][y1] = 0;
    }
}
