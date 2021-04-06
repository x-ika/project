package ika.games.gomoku.controller.player;

import ika.games.gomoku.controller.GomokuLogic;
import ika.games.gomoku.controller.action.GomokuMove;

@Deprecated
public class StaticComp implements GomokuPlayer {

    private final int LMAX = GomokuLogic.WIN_SEQUENCE_LENGTH;
    private int x1, x2, y1, y2, xmin, xmax, ymin, ymax, SIZE;
    private int[][] list;
    private GomokuMove answer = null;

    public GomokuMove getMove(int[][] desk, int value) {
        if (desk.length != desk[0].length) {
            System.out.println("arra kvadratuli!!!");
            return null;
        }

        int t = init(desk, value);
        if (t == 0) {
            return new GomokuMove(SIZE / 2, SIZE / 2);
        }

        if (endOfGame1(1, 2)) {
            return answer;
        }
        if (endOfGame1(1, 1)) {
            return answer;
        }
        ////////////////////////
        if (endOfGame2(1, 2)) {
            return answer;
        }
        if (endOfGame2(2, 2)) {
            return answer;
        }
        ///////////////////////
        if (endOfGame2(2, 1) && !endOfGame2(1, 1)) {
            return answer;
        }
        if (endOfGame2(1, 1)) {
            return answer;
        }
        //////////////////////
        if (endOfGame1(4, 2)) {
            return answer;
        }
        //
        if (endOfGame1(6, 2)) {
            return answer;
        }
        if (endOfGame1(6, 1)) {
            return answer;
        }
        if (endOfGame1(8, 2)) {
            return answer;
        }
        if (endOfGame1(10, 1)) {
            return answer;
        }
        return null;
    }

    protected int init(int[][] desk, int value) {
        SIZE = desk.length;
        xmin = SIZE;
        xmax = 0;
        ymin = SIZE;
        ymax = 0;
        list = new int[SIZE + 2 * LMAX][SIZE + 2 * LMAX];
        int t = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                switch (desk[i][j]) {
                    case 0:
                        list[i + LMAX][j + LMAX] = 0;
                        break;
                    case 2:
                        list[i + LMAX][j + LMAX] = value == 2 ? 2 : 1;
                        break;
                    case 1:
                        list[i + LMAX][j + LMAX] = value == 1 ? 2 : 1;
                        break;
                }
                if (desk[i][j] != 0) {
                    t++;
                    xmin = Math.min(Math.max(LMAX, i + LMAX - LMAX + 2), xmin);
                    xmax = Math.max(Math.min(SIZE + LMAX, i + LMAX + LMAX - 2), xmax);
                    ymin = Math.min(Math.max(LMAX, j + LMAX - LMAX + 2), ymin);
                    ymax = Math.max(Math.min(SIZE + LMAX, j + LMAX + LMAX - 2), ymax);
                }
            }
        }
        return t;
    }

    protected void setMove(int i, int j) {
        answer = new GomokuMove(i - LMAX, j - LMAX);
    }

    protected boolean condintion(int n, int i1, int j1, int i2, int j2, int t) {
        switch (n) {
            case 1:
                return is(i1, j1, LMAX, t);
            case 2:
                return (is(i1, j1, LMAX - 1, t) && list[x1][y1] == 0 && list[x2][y2] == 0 &&
                        (!is(i2, j2, LMAX - 1, 3 - t) || (list[x1][y1] > 0 && list[x2][y2] > 0))) ||
                        is(i1, j1, LMAX, t);
            case 4:
                return is(i1, j1, LMAX - 1, t) && (list[x1][y1] == 0 || list[x2][y2] == 0);
            case 6:
                return is(i1, j1, LMAX - 2, t) && list[x1][y1] == 0 && list[x2][y2] == 0;
            case 8:
                return is(i1, j1, LMAX - 3, t) && list[x1][y1] == 0 && list[x2][y2] == 0 && x1 != x2;
            case 10:
                return is(i1, j1, 2, t) && y1 == y2;
            default:
                return false;
        }
    }

    protected boolean endOfGame1(int n, int t) {
        int i1, j1;
        for (i1 = xmin; i1 < xmax; i1++)
            for (j1 = ymin; j1 < ymax; j1++)
                if (list[i1][j1] == 0) {
                    list[i1][j1] = t;
                    if (condintion(n, i1, j1, 0, 0, t)) {
                        list[i1][j1] = 0;
                        setMove(i1, j1);
                        return true;
                    }
                    list[i1][j1] = 0;
                }
        return false;
    }

    protected boolean endOfGame2(int n, int t) {
        int i1, j1, i2, j2, i3, j3,
                min1, min2, max1, max2;
        boolean b1, b2;
        for (i1 = xmin; i1 < xmax; i1++)
            for (j1 = ymin; j1 < ymax; j1++)
                if (list[i1][j1] == 0) {
                    min1 = Math.max(LMAX, i1 - LMAX);
                    min2 = Math.max(LMAX, j1 - LMAX);
                    max1 = Math.min(LMAX + SIZE, i1 + LMAX);
                    max2 = Math.min(LMAX + SIZE, j1 + LMAX);
                    b1 = true;
                    list[i1][j1] = t;
                    for (i2 = xmin; i2 < xmax; i2++)
                        for (j2 = ymin; j2 < ymax; j2++)
                            if (list[i2][j2] == 0) {
                                b2 = true;
                                list[i2][j2] = 3 - t;
                                for (i3 = min1; i3 < max1; i3++)
                                    for (j3 = min2; j3 < max2; j3++)
                                        if (list[i3][j3] == 0) {
                                            list[i3][j3] = t;
                                            if (condintion(n, i3, j3, i2, j2, t)) {
                                                b2 = false;
                                                list[i3][j3] = 0;
                                                break;
                                            }
                                            list[i3][j3] = 0;
                                        }
                                list[i2][j2] = 0;
                                if (b2) {
                                    b1 = !b2;
                                    break;
                                }
                            }
                    list[i1][j1] = 0;
                    if (b1) {
                        setMove(i1, j1);
                        return true;
                    }
                }
        return false;
    }

    protected boolean is(int i, int j, int l, int t) {
        int length1, length2;

        for (int signI = 0; signI < 2; signI++)
            for (int signJ = -1; signJ < 2; signJ++)
                if (signI != 0 || signJ == -1) {
                    length1 = length2 = 1;
                    while (list[i + signI * length1][j + signJ * length1] == t ||
                            list[i - signI * length2][j - signJ * length2] == t) {
                        if (list[i + signI * length1][j + signJ * length1] == t) {
                            length1++;
                        } else {
                            length2++;
                        }
                        if (length1 + length2 > l) {
                            x1 = i - signI * length2;
                            y1 = j - signJ * length2;
                            x2 = i + signI * length1;
                            y2 = j + signJ * length1;
                            return true;
                        }
                    }
                }
        return false;
    }

}
