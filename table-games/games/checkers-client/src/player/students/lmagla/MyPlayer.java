package player.students.lmagla;

import main.*;

import java.util.ArrayList;

public class MyPlayer implements Player {
    public void gameOver() {
    }

    private final static int EMPTY = 0, WHITE_KING = 4, BLACK_KING = 5, WHITE = 1, BLACK = 2;

    public Turn makeTurne(int[][] d, int value) {

        return null;
    }

    private static boolean withinBounds(int len, int i, int j) {
        if (i < 0 || i >= len || j < 0 || j >= len)
            return false;
        return true;
    }

    /**
     * zemot marcxniv aqvs tu ara
     */
    private static boolean hasKill1(int[][] d, int value, int i, int j) {
        if (withinBounds(d.length, i - 2, j - 2)) {
            if (d[i - 1][j - 1] != EMPTY && d[i - 1][j - 1] != value
                    && d[i - 1][j - 1] != value + 3)
            {
                if (d[i - 2][j - 2] == EMPTY) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * zemot marjvniv aqvs tu ara
     */
    private static boolean hasKill2(int[][] d, int value, int i, int j) {
        if (withinBounds(d.length, i + 2, j - 2)) {
            if (d[i + 1][j - 1] != EMPTY && d[i + 1][j - 1] != value
                    && d[i + 1][j - 1] != value + 3)
            {
                if (d[i + 2][j - 2] == EMPTY) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * qvemot marcxniv aqvs tu ara
     */
    private static boolean hasKill3(int[][] d, int value, int i, int j) {
        if (withinBounds(d.length, i - 2, j + 2)) {
            if (d[i - 1][j + 1] != EMPTY && d[i - 1][j + 1] != value
                    && d[i - 1][j + 1] != value + 3)
            {
                if (d[i - 2][j + 2] == EMPTY) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * qvemot marjvniv aqvs tu ara
     */
    private static boolean hasKill4(int[][] d, int value, int i, int j) {
        if (withinBounds(d.length, i + 2, j + 2)) {
            if (d[i + 1][j + 1] != EMPTY && d[i + 1][j + 1] != value
                    && d[i + 1][j + 1] != value + 3)
            {
                if (d[i + 2][j + 2] == EMPTY) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasKill(int[][] d, int value, int i, int j) {
        if (hasKill1(d, value, i, j) || hasKill2(d, value, i, j) || hasKill3(d, value, i, j) || hasKill4(d, value, i, j)) {
            return true;
        }
        return false;
    }

    private static void allKillsForOne(Checker start, Checker end, ArrayList<Checker> victims, ArrayList<Turn> res, int[][] d, int value, int i, int j) {
        int enemy, deadX = -1, deadY = -1;
        if (value == WHITE)
            enemy = BLACK;
        else
            enemy = BLACK;

        if (!hasKill(d, value, i, j)) {
            end = new Checker(i, j, value);
            @SuppressWarnings("unchecked")
            ArrayList<Checker> ar = (ArrayList<Checker>) victims.clone();
            victims.clear();
            Turn t = new Turn(start, end, ar);
            res.add(t);
        }
        if (hasKill(d, value, i, j)) {
            if (start == null) {
                start = new Checker(i, j, value);
                victims = new ArrayList<>();
            }
        }
        if (hasKill1(d, value, i, j)) {
            Checker dead = new Checker(i - 1, j - 1, enemy);
            victims.add(dead);
            d[dead.row][dead.col] = EMPTY;
            deadX = dead.row;
            deadY = dead.col;
            d[i][j] = EMPTY;
            allKillsForOne(start, end, victims, res, d, value, i - 2, j - 2);
        }
        if (hasKill2(d, value, i, j)) {
            Checker dead = new Checker(i + 1, j - 1, enemy);
            victims.add(dead);
            d[dead.row][dead.col] = EMPTY;
            deadX = dead.row;
            deadY = dead.col;
            d[i][j] = EMPTY;
            allKillsForOne(start, end, victims, res, d, value, i + 2, j - 2);
        }
        if (hasKill3(d, value, i, j)) {
            Checker dead = new Checker(i - 1, j + 1, enemy);
            victims.add(dead);
            d[dead.row][dead.col] = EMPTY;
            deadX = dead.row;
            deadY = dead.col;
            d[i][j] = EMPTY;
            allKillsForOne(start, end, victims, res, d, value, i - 2, j + 2);
        }
        if (hasKill4(d, value, i, j)) {
            Checker dead = new Checker(i + 1, j + 1, enemy);
            victims.add(dead);
            d[dead.row][dead.col] = EMPTY;
            deadX = dead.row;
            deadY = dead.col;
            d[i][j] = EMPTY;
            allKillsForOne(start, end, victims, res, d, value, i + 2, j + 2);
        }
        if (withinBounds(d.length, deadX, deadY)) {
            d[deadX][deadY] = enemy;
            d[i][j] = value;
        }
    }

    /**
     * zemot marcxniv aqvs tu ara svla tetrs
     */
    private static boolean hasMoveW1(int[][] d, int i, int j) {
        if (withinBounds(d.length, i - 1, j - 1)) {
            if (d[i - 1][j - 1] == EMPTY) {
                return true;
            }
        }
        return false;
    }

    /**
     * zemot marjvniv aqvs tu ara svla tetrs
     */
    private static boolean hasMoveW2(int[][] d, int i, int j) {
        if (withinBounds(d.length, i - 1, j + 1)) {
            if (d[i - 1][j + 1] == EMPTY) {
                return true;
            }
        }
        return false;
    }

    /**
     * qvemot marcxniv aq tu ara shavs
     */
    private static boolean hasMoveB1(int[][] d, int i, int j) {
        if (withinBounds(d.length, i + 1, j - 1)) {
            if (d[i + 1][j - 1] == EMPTY) {
                return true;
            }
        }
        return false;
    }

    /**
     * qvemot marjvniv aq tu ara shavs
     */
    private static boolean hasMoveB2(int[][] d, int i, int j) {
        if (withinBounds(d.length, i + 1, j + 1)) {
            if (d[i + 1][j + 1] == EMPTY) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasMove(int[][] d, int i, int j, int value) {
        if (value == WHITE) {
            if (hasMoveW1(d, i, j) || hasMoveW2(d, i, j)) {
                return true;
            }
        } else {
            if (hasMoveB1(d, i, j) || hasMoveB2(d, i, j)) {
                return true;
            }
        }
        return false;
    }

    /**
     * yvela shesadzlo svlebis arailisti erti checkeristvis
     */
    private static void allMovesForOne(ArrayList<Turn> res, int[][] d, int value, int i, int j) {
        Checker start = null;
        Turn t = null;
        if (hasMove(d, i, j, value)) {
            start = new Checker(i, j, value);
        }
        if (value == WHITE) {
            Checker end = null;
            if (hasMoveW1(d, i, j)) {
                end = new Checker(i - 1, j - 1, value);
            }
            if (hasMoveW2(d, i, j)) {
                if (end != null) {
                    t = new Turn(start, end, null);
                    res.add(t);
                }
                end = new Checker(i - 1, j + 1, value);
            }
            t = new Turn(start, end, null);
            if (end == null) {
                t = null;
            }
        } else {
            Checker end = null;
            if (hasMoveB1(d, i, j)) {
                end = new Checker(i + 1, j - 1, value);
            }
            if (hasMoveB2(d, i, j)) {
                if (end != null) {
                    t = new Turn(start, end, null);
                    res.add(t);
                }
                end = new Checker(i + 1, j + 1, value);
            }
            t = new Turn(start, end, null);
            if (end == null) {
                t = null;
            }
        }
        if (t != null) {
            res.add(t);
        }
    }

    /**
     * yvela shesadzlo ertujriani svlebis arailisti mteli dafistvis
     */
    private static void allMoves(ArrayList<Turn> res, int[][] d, int value) {
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d.length; j++) {
                if (d[i][j] == value) {
                    allMovesForOne(res, d, value, i, j);
                }
            }
        }
    }

    /**
     * hasKill-ebis analogiurebi damkistvis
     */
    private static boolean hasKill1King(int[][] d, int value, int i, int j) {
        for (int t = 0; t < d.length; t++) {
            if (withinBounds(d.length, i - t - 2, j - t - 2)) {
                if (d[i - t - 1][j - t - 1] != EMPTY && d[i - t - 1][j - t - 1] != value
                        && d[i - t - 1][j - t - 1] != value - 3)
                {
                    if (d[i - t - 2][j - t - 2] == EMPTY) {
                        dist = t;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean hasKill2King(int[][] d, int value, int i, int j) {
        for (int t = 0; t < d.length; t++) {
            if (withinBounds(d.length, i + t + 2, j - t - 2)) {
                if (d[i + t + 1][j - t - 1] != EMPTY && d[i + t + 1][j - t - 1] != value
                        && d[i + t + 1][j - t - 1] != value - 3)
                {
                    if (d[i + t + 2][j - t - 2] == EMPTY) {
                        dist = t;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean hasKill3King(int[][] d, int value, int i, int j) {
        for (int t = 0; t < d.length; t++) {
            if (withinBounds(d.length, i - t - 2, j + t + 2)) {
                if (d[i - t - 1][j + t + 1] != EMPTY && d[i - t - 1][j + t + 1] != value
                        && d[i - t - 1][j + t + 1] != value - 3)
                {
                    if (d[i - t - 2][j + t + 2] == EMPTY) {
                        dist = t;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean hasKill4King(int[][] d, int value, int i, int j) {
        for (int t = 0; t < d.length; t++) {
            if (withinBounds(d.length, i + t + 2, j + t + 2)) {
                if (d[i + t + 1][j + t + 1] != EMPTY && d[i + t + 1][j + t + 1] != value
                        && d[i + t + 1][j + t + 1] != value - 3)
                {
                    if (d[i + t + 2][j + t + 2] == EMPTY) {
                        dist = t;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean hasKillKing(int[][] d, int value, int i, int j) {
        if (hasKill1King(d, value, i, j) || hasKill2King(d, value, i, j) || hasKill3King(d, value, i, j) || hasKill4King(d, value, i, j)) {
            return true;
        }
        return false;
    }

    private static boolean hasMoveKing(int[][] d, int value, int i, int j) {
        int enemy = 0;
        if (value - 3 == WHITE) {
            enemy = BLACK_KING;
        } else {
            enemy = WHITE_KING;
        }
        if (hasMove(d, i, j, value - 3) || hasMove(d, i, j, enemy)) {
            return true;
        }
        return false;
    }

    // hasMove-ebi damkebistvis
    private static void allMovesForOneKing(ArrayList<Turn> res, int[][] d, int value, int i, int j) {
        if (hasMoveKing(d, value, i, j)) {
            Checker start = new Checker(i, j, value);
            for (int t = 1; t < d.length; t++) {
                if (withinBounds(d.length, i - t, j - t)) {
                    if (d[i - t][j - t] == EMPTY) {
                        Checker end = new Checker(i - t, j - t, value);
                        Turn turn = new Turn(start, end, null);
                        res.add(turn);
                    }
                }
                if (withinBounds(d.length, i - t, j + t)) {
                    if (d[i - t][j + t] == EMPTY) {
                        Checker end = new Checker(i - t, j + t, value);
                        Turn turn = new Turn(start, end, null);
                        res.add(turn);
                    }
                }
                if (withinBounds(d.length, i + t, j - t)) {
                    if (d[i + t][j - t] == EMPTY) {
                        Checker end = new Checker(i + t, j - t, value);
                        Turn turn = new Turn(start, end, null);
                        res.add(turn);
                    }
                }
                if (withinBounds(d.length, i + t, j + t)) {
                    if (d[i + t][j + t] == EMPTY) {
                        Checker end = new Checker(i + t, j + t, value);
                        Turn turn = new Turn(start, end, null);
                        res.add(turn);
                    }
                }
            }
        }
    }

    /**
     * damkis yvela shesadzlo turn-ebi
     */
    private static void allMovesKings(ArrayList<Turn> res, int[][] d, int value) {
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d.length; j++) {
                allMovesForOneKing(res, d, value, i, j);
            }
        }
    }

    /**
     * erti damkis yvela shesadzlo mkvleloba
     */
    private static void allKillsForOneKing(Checker start, Checker end, ArrayList<Checker> victims, ArrayList<Turn> res, int[][] d, int value, int i, int j) {
        int enemy, deadX = -1, deadY = -1;
        if (value == WHITE)
            enemy = BLACK;
        else
            enemy = BLACK;

        if (!hasKillKing(d, value, i, j)) {
            end = new Checker(i, j, value);
            @SuppressWarnings("unchecked")
            ArrayList<Checker> ar = (ArrayList<Checker>) victims.clone();
            victims.clear();
            Turn t = new Turn(start, end, ar);
            res.add(t);
        }

        if (hasKillKing(d, value, i, j)) {
            if (start == null) {
                start = new Checker(i, j, value);
                victims = new ArrayList<>();
            }
        }
        if (hasKill1King(d, value, i, j)) {
            Checker dead = new Checker(i - dist - 1, j - dist - 1, enemy);
            victims.add(dead);
            d[dead.row][dead.col] = EMPTY;
            deadX = dead.row;
            deadY = dead.col;
            d[i][j] = EMPTY;
            allKillsForOneKing(start, end, victims, res, d, value, i - 2, j - 2);
        }
        if (hasKill2King(d, value, i, j)) {
            Checker dead = new Checker(i + dist + 1, j - dist - 1, enemy);
            victims.add(dead);
            d[dead.row][dead.col] = EMPTY;
            deadX = dead.row;
            deadY = dead.col;
            d[i][j] = EMPTY;
            allKillsForOneKing(start, end, victims, res, d, value, i + 2, j - 2);
        }
        if (hasKill3King(d, value, i, j)) {
            Checker dead = new Checker(i - dist - 1, j + dist + 1, enemy);
            victims.add(dead);
            d[dead.row][dead.col] = EMPTY;
            deadX = dead.row;
            deadY = dead.col;
            d[i][j] = EMPTY;
            allKillsForOneKing(start, end, victims, res, d, value, i - 2, j + 2);
        }
        if (hasKill4King(d, value, i, j)) {
            Checker dead = new Checker(i + dist + 1, j + dist + 1, enemy);
            victims.add(dead);
            d[dead.row][dead.col] = EMPTY;
            deadX = dead.row;
            deadY = dead.col;
            d[i][j] = EMPTY;
            allKillsForOneKing(start, end, victims, res, d, value, i + 2, j + 2);
        }
        if (withinBounds(d.length, deadX, deadY)) {
            d[deadX][deadY] = enemy;
            d[i][j] = value;
        }
    }

    /**
     * yvela mokvlis turn-ebis dabruneba
     */
    private static void allKills(ArrayList<Turn> res, int[][] d, int value) {
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d.length; j++) {
                if (d[i][j] == value) {
                    if (d[i][j] > 1) {
                        allKillsForOneKing(null, null, null, res, d, value, i, j);
                    } else {
                        allKillsForOne(null, null, null, res, d, value, i, j);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        int[][] d = new int[8][8];


//		testi orad gayofistvis
//		d[7][6] = WHITE;
//		d[6][5] = BLACK;
//		d[4][3] = BLACK;
//		d[2][1] = BLACK;
//		d[4][5] = BLACK;

//		testi
//		d[7][3] = WHITE;
//		d[6][2] = BLACK;
//		d[6][4] = BLACK;
//		d[4][2] = BLACK;

//		damkis mokvlis testi
        d[5][2] = WHITE;
        d[4][1] = BLACK;
        d[2][1] = BLACK_KING;
        d[2][3] = BLACK;
        d[4][3] = BLACK;

        ArrayList<Turn> res = new ArrayList<>();
//		allMovesForOneKing(res, d, WHITE_KING, 5, 4);
//		allKillsForOne(null, null, null, res, d, WHITE, 7, 6);
//		allKills(res, d, WHITE);
//		allKillsForOneKing(null, null, null, res, d, WHITE, 7, 0);
//		allKillsForOneKing(null, null, null, res, d, WHITE, 7, 0);
//		System.out.println(res.size());
//		for (int i = 0; i < res.size(); i++) {
//			System.out.println(res.get(i));
//		}
    }

    private static int dist = 0;
}