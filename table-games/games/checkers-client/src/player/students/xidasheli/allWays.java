package player.students.xidasheli;

import main.*;

import java.util.ArrayList;

public class allWays implements Player {
    public void gameOver() {
    }

    private static boolean isKillRU(Checker c, int[][] d) {
        int secPlayer = 10;
        if (c.value == 1)
            secPlayer = 2;
        else if (c.value == 2)
            secPlayer = 1;
        if (c.row + 2 < 8 && c.col + 2 < 8 && d[c.row + 2][c.col + 2] == 0
                && d[c.row + 1][c.col + 1] == secPlayer)
        {
            return true;
        }
        return false;
    }

    private static boolean isKillLU(Checker c, int[][] d) {
        int secPlayer = 10;
        if (c.value == 1)
            secPlayer = 2;
        else if (c.value == 2)
            secPlayer = 1;
        if (c.row + 2 < 8 && c.col - 2 >= 0 && d[c.row + 2][c.col - 2] == 0
                && d[c.row + 1][c.col - 1] == secPlayer)
        {
            return true;
        }
        return false;
    }

    private static boolean isKillLD(Checker c, int[][] d) {
        int secPlayer = 10;
        if (c.value == 1)
            secPlayer = 2;
        else if (c.value == 2)
            secPlayer = 1;
        if (c.row - 2 >= 0 && c.col - 2 >= 0 && d[c.row - 2][c.col - 2] == 0
                && d[c.row - 1][c.col - 1] == secPlayer)
        {
            return true;
        }
        return false;
    }

    private static boolean isKillRD(Checker c, int[][] d) {
        int secPlayer = 10;
        if (c.value == 1)
            secPlayer = 2;
        else if (c.value == 2)
            secPlayer = 1;
        if (c.row - 2 >= 0 && c.col + 2 < 8 && d[c.row - 2][c.col + 2] == 0
                && d[c.row - 1][c.col + 1] == secPlayer)
        {
            return true;
        }
        return false;
    }

    private static boolean killable(Checker c, int[][] d) {
        if (isKillLD(c, d) || isKillLU(c, d) || isKillRD(c, d)
                || isKillRU(c, d))
        {
            return true;
        }
        return false;
    }

    private static boolean canMoveR(int[][] d, Checker c) {
        int dxdy = 0;
        if (c.value == 0)
            return false;
        else if (c.value == 1)
            dxdy = -1;
        else if (c.value == 2)
            dxdy = 1;

        if (c.row + dxdy < 0 || c.row + dxdy >= 8 || c.col + dxdy < 0
                || c.col + dxdy >= 8)
            return false;

        if (d[c.row + dxdy][c.col + dxdy] != 0)
            return false;
        return true;
    }

    private static boolean canMoveL(int[][] d, Checker c) {
        int dxdy = 0;
        if (c.value == 0)
            return false;
        else if (c.value == 1)
            dxdy = -1;
        else if (c.value == 2)
            dxdy = 1;

        if (c.row + dxdy < 0 || c.row + dxdy >= 8 || c.col - dxdy < 0
                || c.col - dxdy >= 8)
            return false;

        if (d[c.row + dxdy][c.col - dxdy] != 0)
            return false;
        return true;
    }

    private static void getQueensWhichCanMove(Checker c, ArrayList<Turn> turns,
                                              int d[][])
    {
        if (c.value == 4 || c.value == 5) {
            for (int i = 0; i < d.length; i++) {
                for (int j = 0; j < d.length; j++) {
                    if (d[i][j] == c.value) {
                        int I = i;
                        int J = j;
                        while ((I - 1) >= 0 && J - 1 >= 0
                                && d[I - 1][J - 1] == 0) {
                            I--;
                            J--;
                            if (I != i) {
                                turns.add(new Turn(new Checker(i, j, c.value),
                                        new Checker(I, J, c.value), null));
                            }
                        }
                        I = i;
                        J = j;
                        while (I - 1 >= 0 && J + 1 < 8 && d[I - 1][J + 1] == 0) {
                            I--;
                            J++;
                            if (I != i) {
                                turns.add(new Turn(new Checker(i, j, c.value),
                                        new Checker(I, J, c.value), null));
                            }
                        }
                        I = i;
                        J = j;
                        while (I + 1 < 8 && J - 1 >= 0 && d[I + 1][J - 1] == 0) {
                            I++;
                            J--;
                            if (I != i) {
                                turns.add(new Turn(new Checker(i, j, c.value),
                                        new Checker(I, J, c.value), null));
                            }
                        }
                        I = i;
                        J = j;
                        while (I + 1 < 8 && J + 1 < 8 && d[I + 1][J + 1] == 0) {
                            I++;
                            J++;
                            if (I != i) {
                                turns.add(new Turn(new Checker(i, j, c.value),
                                        new Checker(I, J, c.value), null));
                            }
                        }
                    }
                }
            }
        }
    }

    private static ArrayList<Turn> getAllWays(int[][] d, int value) {
        ArrayList<Turn> turns = new ArrayList<>();
        Checker c;
        if (value == 1 && value == 2)
            return null;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (d[row][col] == value) {
                    c = new Checker(row, col, value);
                    if (!killable(c, d)) {
                        if (value == 1) {
                            if (canMoveR(d, c))
                                turns.add(new Turn(c, new Checker(row - 1,
                                        col - 1, value),
                                        new ArrayList<>()));
                            if (canMoveL(d, c))
                                turns.add(new Turn(c, new Checker(row - 1,
                                        col + 1, value),
                                        new ArrayList<>()));
                        }
                        if (value == 2) {
                            if (canMoveR(d, c))
                                turns.add(new Turn(c, new Checker(row + 1,
                                        col + 1, value),
                                        new ArrayList<>()));
                            if (canMoveL(d, c))
                                turns.add(new Turn(c, new Checker(row + 1,
                                        col - 1, value),
                                        new ArrayList<>()));
                        }
                    } else {
                        checkSimpleKills(c, d, turns);
                    }
                }
                if (d[row][col] == value + 3) {
                    c = new Checker(row, col, value + 3);
                    if (!forKingKillable(c, d)) {
                        getQueensWhichCanMove(c, turns, d);
                    } else {
                        kingCheckSimpleKills(c, d, turns);
                    }
                }
            }
        }
        return turns;
    }

    private static int kisIndex = 0;

    private static boolean forKingIsKillRU(Checker c, int[][] d) {
        for (int t = 0; t < d.length; t++) {
            if (c.row + t + 2 < 8 && c.col + t + 2 < 8) {
                if (d[c.row + t + 1][c.col + t + 1] != 0
                        && d[c.row + t + 1][c.col + t + 1] != c.value
                        && d[c.row + t + 1][c.col + t + 1] != c.value - 3)
                {
                    if (d[c.row + t + 2][c.col + t + 2] == 0) {
                        kisIndex = t + 2;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean forKingIsKillLU(Checker c, int[][] d) {
        for (int t = 0; t < d.length; t++) {
            if (c.row + t + 2 < 8 && c.col - t - 2 >= 0) {
                if (d[c.row + t + 1][c.col - t - 1] != 0
                        && d[c.row + t + 1][c.col - t - 1] != c.value
                        && d[c.row + t + 1][c.col - t - 1] != c.value - 3)
                {
                    if (d[c.row + t + 2][c.col - t - 2] == 0) {
                        kisIndex = t + 2;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean forKingIsKillLD(Checker c, int[][] d) {
        for (int t = 0; t < d.length; t++) {
            if (c.row - t - 2 >= 0 && c.col - t - 2 >= 0) {
                if (d[c.row - t - 1][c.col - t - 1] != 0
                        && d[c.row - t - 1][c.col - t - 1] != c.value
                        && d[c.row - t - 1][c.col - t - 1] != c.value - 3)
                {
                    if (d[c.row - t - 2][c.col - t - 2] == 0) {
                        kisIndex = t + 2;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean forKingIsKillRD(Checker c, int[][] d) {
        for (int t = 0; t < d.length; t++) {
            if (c.row - t - 2 >= 0 && c.col + t + 2 < 8) {
                if (d[c.row - t - 1][c.col + t + 1] != 0
                        && d[c.row - t - 1][c.col + t + 1] != c.value
                        && d[c.row - t - 1][c.col + t + 1] != c.value - 3)
                {
                    if (d[c.row - t - 2][c.col + t + 2] == 0) {
                        kisIndex = t + 2;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean forKingKillable(Checker c, int[][] d) {
        if (forKingIsKillLD(c, d) || forKingIsKillLU(c, d)
                || forKingIsKillRD(c, d) || forKingIsKillRU(c, d))
        {
            return true;
        }
        return false;
    }

    static ArrayList<Checker> killed = new ArrayList<>();

    static Checker first = new Checker(0, 0, 0);

    private static void checkSimpleKills(Checker c, int[][] d,
                                         ArrayList<Turn> moves)
    {
        int[][] b = new int[8][8];
        if (isKillRU(c, d)) {
            for (int i = 0; i < d.length; i++) {
                for (int j = 0; j < d.length; j++) {
                    b[i][j] = d[i][j];
                }
            }
            if (killed.isEmpty())
                first = c;
            killed.add(new Checker(c.row + 1, c.col + 1, d[c.row + 1][c.col + 1]));
            b[c.row + 2][c.col + 2] = b[c.row][c.col];
            b[c.row][c.col] = 0;
            b[c.row + 1][c.col + 1] = 0;
            if (!killable(new Checker(c.row + 2, c.col + 2, c.value), b)) {
                if (b[c.row + 2][c.col + 2] == 2 && c.row + 2 == 7) {
                    b[c.row + 2][c.col + 2] += 3;
                }
                moves.add(new Turn(first, new Checker(c.row + 2, c.col + 2, c.value),
                        killed));
                killed.remove(killed.size() - 1);
            } else {
                checkSimpleKills(new Checker(c.row + 2, c.col + 2, c.value), b, moves);
            }
        }
        if (isKillLU(c, d)) {
            for (int i = 0; i < d.length; i++) {
                for (int j = 0; j < d.length; j++) {
                    b[i][j] = d[i][j];
                }
            }
            if (killed.isEmpty())
                first = c;
            killed.add(new Checker(c.row + 1, c.col - 1, d[c.row + 1][c.col - 1]));
            b[c.row + 2][c.col - 2] = b[c.row][c.col];
            b[c.row][c.col] = 0;
            b[c.row + 1][c.col - 1] = 0;
            if (!killable(new Checker(c.row + 2, c.col - 2, c.value), b)) {
                if (b[c.row + 2][c.col - 2] == 2 && c.row + 2 == 7) {
                    b[c.row + 2][c.col - 2] += 3;
                }
                moves.add(new Turn(first, new Checker(c.row + 2, c.col - 2, c.value),
                        killed));
                killed.remove(killed.size() - 1);
            } else {
                checkSimpleKills(new Checker(c.row + 2, c.col - 2, c.value), b, moves);
            }
        }
        if (isKillLD(c, d)) {
            for (int i = 0; i < d.length; i++) {
                for (int j = 0; j < d.length; j++) {
                    b[i][j] = d[i][j];
                }
            }
            if (killed.isEmpty())
                first = c;
            killed.add(new Checker(c.row - 1, c.col - 1, d[c.row - 1][c.col - 1]));
            b[c.row - 2][c.col - 2] = b[c.row][c.col];
            b[c.row][c.col] = 0;
            b[c.row - 1][c.col - 1] = 0;
            if (!killable(new Checker(c.row - 2, c.col - 2, c.value), b)) {
                if (b[c.row - 2][c.col - 2] == 2 && c.row - 2 == 7) {
                    b[c.row - 2][c.col - 2] += 3;
                }
                moves.add(new Turn(first, new Checker(c.row - 2, c.col - 2, c.value),
                        killed));
                killed.remove(killed.size() - 1);
            } else {
                checkSimpleKills(new Checker(c.row - 2, c.col - 2, c.value), b, moves);
            }
        }
        if (isKillRD(c, d)) {
            for (int i = 0; i < d.length; i++) {
                for (int j = 0; j < d.length; j++) {
                    b[i][j] = d[i][j];
                }
            }
            if (killed.isEmpty())
                first = c;
            killed.add(new Checker(c.row - 1, c.col + 1, d[c.row - 1][c.col + 1]));
            b[c.row - 2][c.col + 2] = b[c.row][c.col];
            b[c.row][c.col] = 0;
            b[c.row - 1][c.col + 1] = 0;
            if (!killable(new Checker(c.row - 2, c.col + 2, c.value), b)) {
                if (b[c.row - 2][c.col + 2] == 2 && c.row - 2 == 7) {
                    b[c.row - 2][c.col + 2] += 3;
                }
                moves.add(new Turn(first, new Checker(c.row - 2, c.col + 2, c.value),
                        killed));
                killed.remove(killed.size() - 1);
            } else {
                checkSimpleKills(new Checker(c.row - 2, c.col + 2, c.value), b, moves);
            }
        }
        return;
    }

    private static void kingCheckSimpleKills(Checker c, int[][] d,
                                             ArrayList<Turn> moves)
    {
        int[][] b = new int[8][8];
        if (forKingIsKillRU(c, d)) {
            for (int i = 0; i < d.length; i++) {
                for (int j = 0; j < d.length; j++) {
                    b[i][j] = d[i][j];
                }
            }
            if (killed.isEmpty())
                first = c;
            killed.add(new Checker(c.row + kisIndex - 1, c.col + kisIndex - 1,
                    d[c.row + kisIndex - 1][c.col + kisIndex - 1]));
            b[c.row + kisIndex][c.col + kisIndex] = b[c.row][c.col];
            b[c.row][c.col] = 0;
            b[c.row + kisIndex][c.col + kisIndex] = 0;
            if (!forKingKillable(new Checker(c.row + kisIndex + 1, c.col + kisIndex + 1,
                    c.value), b))
            {
                if (b[c.row + kisIndex][c.col + kisIndex] == 2
                        && c.row + kisIndex == 7)
                {
                    b[c.row + kisIndex][c.col + kisIndex] += 3;
                }
                moves.add(new Turn(first, new Checker(c.row + kisIndex, c.col
                        + kisIndex, c.value), killed));
                killed.remove(killed.size() - 1);
            } else {
                kingCheckSimpleKills(new Checker(c.row + kisIndex,
                        c.col + kisIndex, c.value), b, moves);
            }
        }
        if (forKingIsKillLU(c, d)) {
            for (int i = 0; i < d.length; i++) {
                for (int j = 0; j < d.length; j++) {
                    b[i][j] = d[i][j];
                }
            }
            if (killed.isEmpty())
                first = c;
            killed.add(new Checker(c.row + kisIndex, c.col - kisIndex + 1, d[c.row
                    + kisIndex][c.col - kisIndex + 1]));
            b[c.row + kisIndex][c.col - kisIndex] = b[c.row][c.col];
            b[c.row][c.col] = 0;
            b[c.row + kisIndex - 1][c.col - kisIndex + 1] = 0;
            if (!forKingKillable(new Checker(c.row + kisIndex, c.col - kisIndex,
                    c.value), b))
            {
                if (b[c.row + kisIndex][c.col - kisIndex] == 2
                        && c.row + kisIndex == 7)
                {
                    b[c.row + kisIndex][c.col - kisIndex] += 3;
                }
                moves.add(new Turn(first, new Checker(c.row + kisIndex, c.col
                        - kisIndex, c.value), killed));
                killed.remove(killed.size() - 1);
            } else {
                kingCheckSimpleKills(new Checker(c.row + kisIndex,
                        c.col - kisIndex, c.value), b, moves);
            }
        }
        if (forKingIsKillLD(c, d)) {
            for (int i = 0; i < d.length; i++) {
                for (int j = 0; j < d.length; j++) {
                    b[i][j] = d[i][j];
                }
            }
            if (killed.isEmpty())
                first = c;
            killed.add(new Checker(c.row - kisIndex + 1, c.col - kisIndex + 1,
                    d[c.row - kisIndex + 1][c.col - kisIndex + 1]));
            b[c.row - kisIndex][c.col - kisIndex] = b[c.row][c.col];
            b[c.row][c.col] = 0;
            b[c.row - kisIndex + 1][c.col - kisIndex + 1] = 0;
            if (!forKingKillable(new Checker(c.row - kisIndex, c.col - kisIndex,
                    c.value), b))
            {
                if (b[c.row - kisIndex][c.col - kisIndex] == kisIndex
                        && c.row - kisIndex == 7)
                {
                    b[c.row - kisIndex][c.col - kisIndex] += 3;
                }
                moves.add(new Turn(first, new Checker(c.row - kisIndex, c.col
                        - kisIndex, c.value), killed));
                killed.remove(killed.size() - 1);
            } else {
                kingCheckSimpleKills(new Checker(c.row - kisIndex,
                        c.col - kisIndex, c.value), b, moves);
            }
        }
        if (forKingIsKillRD(c, d)) {
            for (int i = 0; i < d.length; i++) {
                for (int j = 0; j < d.length; j++) {
                    b[i][j] = d[i][j];
                }
            }
            if (killed.isEmpty())
                first = c;
            killed.add(new Checker(c.row - kisIndex + 1, c.col + kisIndex - 1,
                    d[c.row - kisIndex + 1][c.col + kisIndex - 1]));
            b[c.row - kisIndex][c.col + kisIndex] = b[c.row][c.col];
            b[c.row][c.col] = 0;
            b[c.row - kisIndex + 1][c.col + kisIndex - 1] = 0;
            if (!forKingKillable(new Checker(c.row - kisIndex, c.col + kisIndex,
                    c.value), b))
            {
                if (b[c.row - kisIndex][c.col + kisIndex] == 2
                        && c.row - kisIndex == 7)
                {
                    b[c.row - kisIndex][c.col + kisIndex] += 3;
                }
                moves.add(new Turn(first, new Checker(c.row - kisIndex, c.col
                        + kisIndex, c.value), killed));
                killed.remove(killed.size() - 1);
            } else {
                kingCheckSimpleKills(new Checker(c.row - kisIndex,
                        c.col + kisIndex, c.value), b, moves);
            }
        }
        return;
    }

    public Turn makeTurne(int[][] d, int value) {

        return null;
    }

    public static void main(String[] args) {
        int[][] d1 = {{0, 0, 0, 0, 0, 0, 0, 0}, {0, 4, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 2, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0, 0, 0}};

//		forKingIsKillRD(new Checker(0,0,4), d1);

//		for (int i = 0; i < getAllWays(d1, 1).size(); i++) {
//			System.out.println(getAllWays(d1, 1).get(i).first.row + " "
//					+ getAllWays(d1, 1).get(i).first.col + " "
//					+ getAllWays(d1, 1).get(i).last.row + " "
//					+ getAllWays(d1, 1).get(i).last.col);
//		}
//		System.out.println("____________________");
//		for (int i = 0; i < getAllWays(d1, 2).size(); i++) {
//			System.out.println(getAllWays(d1, 2).get(i).first.row + " "
//					+ getAllWays(d1, 2).get(i).first.col + " "
//					+ getAllWays(d1, 2).get(i).last.row + " "
//					+ getAllWays(d1, 2).get(i).last.col);
//		}
    }

}
