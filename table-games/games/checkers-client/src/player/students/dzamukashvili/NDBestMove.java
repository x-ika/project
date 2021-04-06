package player.students.dzamukashvili;

import main.*;

import java.util.ArrayList;


public class NDBestMove implements Player {
    public void gameOver() {
    }

    private static int[][] board = new int[8][8];

    public NDBestMove() {
        board = new int[8][8];
    }

    private static String makeString(int[][] d) {
        String str = "";
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                str += d[i][j];
            }
        }
        return str;
    }

    private static void makeArray(String str, int[][] d) {
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d.length; j++) {
                d[i][j] = str.charAt(i * d.length + j) - 48;
            }
        }
    }

    private static void copyArray(int[][] from, int[][] to) {
        for (int i = 0; i < to.length; i++) {
            for (int j = 0; j < to.length; j++) {
                to[i][j] = from[i][j];
            }
        }
    }

    private static void makeOneStepMove(int i, int j, int[][] b, ArrayList<String> moves, int k) {
        if (i + k < 8 && i + k >= 0) {
            if (j - 1 >= 0 && b[i + k][j - 1] == 0) {
                int[][] d = new int[8][8];
                copyArray(b, d);
                d[i + k][j - 1] = d[i][j];
                d[i][j] = 0;
                if (i + k == 0 || i + k == 7) {
                    d[i + k][j - 1] += 3;
                }
                moves.add(makeString(d));
            }
            if (j + 1 < 8 && b[i + k][j + 1] == 0) {
                int[][] d = new int[8][8];
                copyArray(b, d);
                d[i + k][j + 1] = d[i][j];
                d[i][j] = 0;
                if (i + k == 0 || i + k == 7) {
                    d[i + k][j + 1] += 3;
                }
                moves.add(makeString(d));
            }
        }
    }

    private static void diagonalMove(int i, int j, int[][] d, ArrayList<String> moves, int n, int m) {
        int[][] b = new int[8][8];
        copyArray(d, b);
        for (int t = 0; t < 8; t++) {
            if (i + n < 8 && i + n >= 0 && j + m < 8 && j + m >= 0 && d[i + n][j + m] == 0) {
                b[i + n][j + m] = b[i][j];
                b[i][j] = 0;
                i += n;
                j += m;
                moves.add(makeString(b));
            } else {
                break;
            }
        }
    }

    private static void makeQueenMove(int i, int j, int[][] d, ArrayList<String> moves) {
        diagonalMove(i, j, d, moves, 1, 1);
        diagonalMove(i, j, d, moves, -1, 1);
        diagonalMove(i, j, d, moves, 1, -1);
        diagonalMove(i, j, d, moves, -1, -1);
    }

    private static void possibleMovesForCell(int i, int j, int[][] d, ArrayList<String> moves) {
        if (d[i][j] == 1) {
            makeOneStepMove(i, j, d, moves, -1);
        } else if (d[i][j] == 2) {
            makeOneStepMove(i, j, d, moves, 1);
        } else {
            makeQueenMove(i, j, d, moves);
        }
    }

    private static void checkSimpleKills(int i, int j, int[][] d, ArrayList<String> moves) {
        int[][] b = new int[8][8];
        if (i + 1 < 8 && j + 1 < 8) {
            if (d[i][j] != d[i + 1][j + 1] % 3 && d[i + 1][j + 1] != 0) {
                if (i + 2 < 8 && j + 2 < 8 && d[i + 2][j + 2] == 0) {
                    copyArray(d, b);
                    b[i + 2][j + 2] = b[i][j];
                    b[i][j] = 0;
                    b[i + 1][j + 1] = 0;
                    if (!hasKillingState(i + 2, j + 2, b, moves)) {
                        if (b[i + 2][j + 2] == 2 && i + 2 == 7) {
                            b[i + 2][j + 2] += 3;
                        }
                        moves.add(makeString(b));
                    } else {
                        checkSimpleKills(i + 2, j + 2, b, moves);
                    }
                }
            }
        }
        if (i + 1 < 8 && j - 1 >= 0) {
            if (d[i][j] != d[i + 1][j - 1] % 3 && d[i + 1][j - 1] != 0) {
                if (i + 2 < 8 && j - 2 >= 0 && d[i + 2][j - 2] == 0) {
                    copyArray(d, b);
                    b[i + 2][j - 2] = b[i][j];
                    b[i][j] = 0;
                    b[i + 1][j - 1] = 0;
                    if (!hasKillingState(i + 2, j - 2, b, moves)) {
                        if (b[i + 2][j - 2] == 2 && i + 2 == 7) {
                            b[i + 2][j - 2] += 3;
                        }
                        moves.add(makeString(b));
                    } else {
                        checkSimpleKills(i + 2, j - 2, b, moves);
                    }
                }
            }
        }
        if (i - 1 >= 0 && j + 1 < 8) {
            if (d[i][j] != d[i - 1][j + 1] % 3 && d[i - 1][j + 1] != 0) {
                if (i - 2 >= 0 && j + 2 < 8 && d[i - 2][j + 2] == 0) {
                    copyArray(d, b);
                    b[i - 2][j + 2] = b[i][j];
                    b[i][j] = 0;
                    b[i - 1][j + 1] = 0;
                    if (!hasKillingState(i - 2, j + 2, b, moves)) {
                        if (b[i - 2][j + 2] == 1 && i - 2 == 0) {
                            b[i - 2][j + 2] += 3;
                        }
                        moves.add(makeString(b));
                    } else {
                        checkSimpleKills(i - 2, j + 2, b, moves);
                    }
                }
            }
        }
        if (i - 1 >= 0 && j - 1 >= 0) {
            if (d[i][j] != d[i - 1][j - 1] % 3 && d[i - 1][j - 1] != 0) {
                if (i - 2 >= 0 && j - 2 >= 0 && d[i - 2][j - 2] == 0) {
                    copyArray(d, b);
                    b[i - 2][j - 2] = b[i][j];
                    b[i][j] = 0;
                    b[i - 1][j - 1] = 0;
                    if (!hasKillingState(i - 2, j - 2, b, moves)) {
                        if (b[i - 2][j - 2] == 1 && i - 2 == 0) {
                            b[i - 2][j - 2] += 3;
                        }
                        moves.add(makeString(b));
                    } else {
                        checkSimpleKills(i - 2, j - 2, b, moves);
                    }
                }
            }
        }
    }

    private static boolean hasKillingState(int i, int j, int[][] d, ArrayList<String> moves) {
        if (i + 1 < 8 && j + 1 < 8) {
            if (d[i][j] != d[i + 1][j + 1] % 3 && d[i + 1][j + 1] != 0) {
                if (i + 2 < 8 && j + 2 < 8 && d[i + 2][j + 2] == 0) {
                    return true;
                }
            }
        }
        if (i + 1 < 8 && j - 1 >= 0) {
            if (d[i][j] != d[i + 1][j - 1] % 3 && d[i + 1][j - 1] != 0) {
                if (i + 2 < 8 && j - 2 >= 0 && d[i + 2][j - 2] == 0) {
                    return true;
                }
            }
        }
        if (i - 1 >= 0 && j + 1 < 8) {
            if (d[i][j] != d[i - 1][j + 1] % 3 && d[i - 1][j + 1] != 0) {
                if (i - 2 >= 0 && j + 2 < 8 && d[i - 2][j + 2] == 0) {
                    return true;
                }
            }
        }
        if (i - 1 >= 0 && j - 1 >= 0) {
            if (d[i][j] != d[i - 1][j - 1] % 3 && d[i - 1][j - 1] != 0) {
                if (i - 2 >= 0 && j - 2 >= 0 && d[i - 2][j - 2] == 0) {
                    return true;
                }
            }
        }
        return false;
    }


    private static boolean hasDiagonalKill(int i, int j, int[][] d, ArrayList<String> moves, int n, int m) {
        for (int t = 0; t < 8; t++) {
            if (i + n < 8 && i + n >= 0 && j + m < 8 && j + m >= 0 && d[i + n][j + m] == 0) {
                i += n;
                j += m;
            } else if (i + n < 8 && i + n >= 0 && j + m < 8 && j + m >= 0 && d[i + n][j + m] % 3 != d[i][j] % 3 && d[i + n][j + m] != 0) {
                if (i + 2 * n < 8 && i + 2 * n >= 0 && j + 2 * m < 8 && j + 2 * m >= 0) {
                    if (d[i + 2 * n][j + 2 * m] == 0) {
                        return true;
                    }
                    return false;
                }
                return false;
            } else {
                return false;
            }
        }
        return false;
    }

    private static boolean hasQueenKill(int i, int j, int[][] d, ArrayList<String> moves) {
        return (hasDiagonalKill(i, j, d, moves, 1, 1) || hasDiagonalKill(i, j, d, moves, 1, -1) ||
                hasDiagonalKill(i, j, d, moves, -1, 1) || hasDiagonalKill(i, j, d, moves, -1, -1));
    }

    private static void kill(int i, int j, int[][] d, ArrayList<String> moves, int n, int m) {
        int k = i;
        int z = j;
        for (int t = 0; t < 8; t++) {
            if (k + n < 8 && k + n >= 0 && z + m < 8 && z + m >= 0 && d[k + n][z + m] == 0) {
                k += n;
                z += m;
                d[k][z] = d[k - n][z - m];
                d[k - n][z - m] = 0;
            } else if (k + n < 8 && k + n >= 0 && z + m < 8 && z + m >= 0 && d[k + n][z + m] % 3 != d[k][z] % 3) {
                if (k + 2 * n < 8 && k + 2 * n >= 0 && z + 2 * m < 8 && z + 2 * m >= 0) {
                    if (d[k + 2 * n][z + 2 * m] == 0) {
                        d[k + 2 * n][z + 2 * m] = d[k][z];
                        d[k][z] = 0;
                        d[k + n][z + m] = 0;
                        k += 2 * n;
                        z += 2 * m;
                        int tr = 0;
                        ArrayList<String> list = new ArrayList<>();
                        for (int r = 0; r < 8; r++) {
                            if (hasQueenKill(k, z, d, moves)) {
                                checkQueenKills(k, z, d, moves);
                                tr = 1;
                            }
                            list.add(makeString(d));
                            if (k + n >= 0 && k + n < 8 && z + m >= 0 && z + m < 8) {
                                d[k + n][z + m] = d[k][z];
                                d[k][z] = 0;
                                k += n;
                                z += m;

                            } else {
                                break;
                            }
                        }
                        if (tr == 0) {
                            for (int q = 0; q < list.size(); q++) {
                                moves.add(list.get(q));
                            }
                        }
                    }
                }
            } else {
                break;
            }
        }
    }

    private static void checkQueenKills(int i, int j, int[][] b, ArrayList<String> moves) {
        int[][] d = new int[8][8];
        copyArray(b, d);
        if (hasQueenKill(i, j, d, moves)) {
            if (hasDiagonalKill(i, j, d, moves, 1, 1)) {
                kill(i, j, d, moves, 1, 1);
            }
            if (hasDiagonalKill(i, j, d, moves, -1, 1)) {
                kill(i, j, d, moves, -1, 1);
            }
            if (hasDiagonalKill(i, j, d, moves, 1, -1)) {
                kill(i, j, d, moves, 1, -1);
            }
            if (hasDiagonalKill(i, j, d, moves, -1, -1)) {
                kill(i, j, d, moves, -1, -1);
            }
        }
    }


    private static void queenKills(int i, int j, int[][] b, ArrayList<String> moves) {
        checkQueenKills(i, j, b, moves);
    }

    private static void checkPossibleKills(int[][] d, ArrayList<String> moves, int turn) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (d[i][j] == turn) {
                    checkSimpleKills(i, j, d, moves);
                } else if (d[i][j] % 3 == turn) {
                    queenKills(i, j, d, moves);
                }
            }
        }
    }

    private static ArrayList<String> allPossibleMoves(int[][] d, int turn) {
        ArrayList<String> moves = new ArrayList<>();
        int[][] b = new int[8][8];
        copyArray(d, b);
        checkPossibleKills(b, moves, turn);
        if (!moves.isEmpty()) return moves;
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d.length; j++) {
                if (d[i][j] % 3 == turn) {
                    copyArray(d, b);
                    possibleMovesForCell(i, j, b, moves);
                }
            }
        }
        return moves;
    }

    private static String writeString(int[][] d) {
        String str = "";
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                str += d[i][j];
            }
            str += "\n";
        }
        return str;
    }

    private static boolean gameOver(String str) {
        int[][] d = new int[8][8];
        makeArray(str, d);
        return (allPossibleMoves(d, 1).size() == 0 || allPossibleMoves(d, 2).size() == 0);
    }

    private static int heuristicValue(String str) {
        int[][] b = new int[8][8];
        makeArray(str, b);
        if (gameOver(str) && allPossibleMoves(b, 1).size() > 0) {
            return 100;
        } else if (gameOver(str)) {
            return -100;
        }
        int result = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (b[i][j] % 3 == 1) {
                    result++;
                    if (b[i][j] > 2) result += 2;
                } else if (b[i][j] != 0) {
                    result--;
                    if (b[i][j] > 2) result -= 2;
                }
            }
        }
        return result;
    }

    private static String result;

    private static int alpha(String node, int depth, int a, int b, int player, int maxDepth) {
        if (depth == 0 || gameOver(node)) {
            return heuristicValue(node);
        }
        if (player == 1) {
            ArrayList<String> list = new ArrayList<>();
            int[][] br = new int[8][8];
            makeArray(node, br);
            list = allPossibleMoves(br, player);
            player = 2;
            for (int i = 0; i < list.size(); i++) {
                node = list.get(i);
                int k = alpha(node, depth - 1, a, b, player, maxDepth);
                if (k > a) {
                    if (depth == maxDepth) {
                        result = node;
                    }
                    a = k;
                }
                if (b <= a) break;
            }
            return a;
        } else {
            ArrayList<String> list = new ArrayList<>();
            int[][] br = new int[8][8];
            makeArray(node, br);
            list = allPossibleMoves(br, player);
            player = 1;
            for (int i = 0; i < list.size(); i++) {
                node = list.get(i);
                int k = alpha(node, depth - 1, a, b, player, maxDepth);
                if (k < b) {
                    if (depth == maxDepth) {
                        result = node;
                    }
                    b = k;
                }
                if (b <= a) break;
            }
            return b;
        }
    }

    public Turn makeTurne(int[][] d, int value) {
        alpha(makeString(d), 8, Integer.MIN_VALUE, Integer.MAX_VALUE, value, 8);
        makeArray(result, board);
        Checker first = null;
        Checker last = null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (d[i][j] % 3 == value && board[i][j] != d[i][j]) {
                    first = new Checker(i, j, d[i][j]);
                }
                if (board[i][j] % 3 == value && board[i][j] != d[i][j]) {
                    last = new Checker(i, j, board[i][j]);
                }
            }
        }
        ArrayList<Checker> list = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (d[i][j] % 3 != value && d[i][j] != 0 && board[i][j] == 0) {
                    Checker cur = new Checker(i, j, d[i][j]);
                    list.add(cur);
                }
            }
        }
        Turn res = new Turn(first, last, list);
        return res;
    }


}