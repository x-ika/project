import java.io.*;
import java.util.*;

public class Player {
    public static final char[] CODE_OF = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm'};
    private Vector<Move> moves;
    private int desk[][], number, signY, x1, y1;

    public Player(int[][] d) {
        x1 = y1 = -1;
        desk = d;
    }

    public Move makeTurne(int num) {
        number = num;
        Vector<Move> bestMoves = standartTurnes().isEmpty() ? findBestTurnes() : standartTurnes();
        return bestMoves.elementAt(new Random().nextInt(bestMoves.size()));
    }

    public Move setXY(int x, int y, int number) {
        if (isBegin("" + x + y, number)) {
            x1 = x;
            y1 = y;
            return null;
        }
        Move move = getTurne("" + x1 + y1 + x + y, number);
        x1 = y1 = -1;
        return move;
    }

    public int isWinner(int number) {
        if (countTurnes(-number).isEmpty()) {
            return 1;
        }
        boolean check = false;
        for (Move t0 : countTurnes(-number)) {
            t0.forward();
            boolean mat = false;
            for (Move t1 : countTurnes(number)) {
                if (t1.result == Figure.COST_OF[1]) {
                    mat = true;
                }
            }
            t0.back();
            if (!mat) {
                return 0;
            }
        }
        for (Move t : countTurnes(number)) {
            if (t.result == Figure.COST_OF[1]) {
                check = true;
            }
        }
        return check ? 2 : 1;
    }

    private boolean isBegin(String s, int number) {
        for (Move move : countTurnes(number)) {
            if (move.toString().startsWith(s)) {
                return true;
            }
        }
        return false;
    }

    private Move getTurne(String s, int number) {
        for (Move move : countTurnes(number)) {
            if (move.toString().equals(s)) {
                return move;
            } else if (move.toString().startsWith(s)) {
                return new HMove(desk, move.x1, move.y1, move.x2, move.y2, 2);
            }
        }
        return null;
    }

    private Vector<Move> standartTurnes() {
        Vector<Move> moves = new Vector<>();
        try {
            String name = number < 0 ? "Debuts for whites.txt" : "Debuts for blacks.txt";
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(name)));
            String s = br.readLine();
            while (s != null) {
                if (isCurrentPosition(s)) {
                    moves.add(getTurne(s.substring(Chess.N * Chess.N), number));
                }
                s = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return moves;
    }

    private Vector<Move> findBestTurnes() {
        Vector<Move> bestMoves = new Vector<>();
        int MIN = -1000, MAX = 1000, SHTRAF = 100, best0 = MIN;
        for (Move t0 : countTurnes(number)) {
            int result = t0.result;
            t0.forward();
            int best1 = MAX;
            for (Move t1 : countTurnes(-number)) {
                if (t1.result == Figure.COST_OF[1]) {
                    result -= SHTRAF;
                }
                result -= t1.result;
                t1.forward();
                int best2 = MIN;
                for (Move t2 : countTurnes(number)) {
                    result += t2.result;
                    t2.forward();
                    int best3 = MAX;
                    for (Move t3 : countTurnes(-number)) {
                        best3 = Math.min(best3, result - t3.result);
                    }
                    best2 = Math.max(best2, best3);
                    t2.back();
                    result -= t2.result;
                }
                best1 = Math.min(best1, best2);
                t1.back();
                if (t1.result == Figure.COST_OF[1]) {
                    result += SHTRAF;
                }
                result += t1.result;
            }
            if (best0 <= best1) {
                if (best0 < best1) {
                    best0 = best1;
                    bestMoves.removeAllElements();
                }
                bestMoves.add(t0);
            }
            t0.back();
        }
        return bestMoves;
    }

    private boolean isCurrentPosition(String s) {
        for (int i = 0; i < Chess.N * Chess.N; i++) {
            if (CODE_OF[desk[i / 8][i % 8] + 6] != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public Vector<Move> countTurnes(int num) {
        moves = new Vector<>();
        signY = num;
        for (int i = 0; i < Chess.N; i++)
            for (int j = 0; j < Chess.N; j++)
                if (desk[i][j] * signY < 0) {
                    int value = Math.abs(desk[i][j]) - 1;
                    if (value == 5) {
                        countPawnTurnes(i, j);
                    } else {
                        if (value == 0) {
                            countRooks(i, j);
                        }
                        for (int ind = 0; ind < Figure.X_ARROW[value].length; ind++) {
                            count(i, j, Figure.X_ARROW[value][ind], Figure.Y_ARROW[value][ind], Figure.LENGTH[value]);
                        }
                    }
                }
        return moves;
    }

    private void countPawnTurnes(int x, int y) {
        int horizontal = signY * (2 * y - 7);
        for (int signX = -1; signX < 2; signX += 2) {
            if (x + signX >= 0 && x + signX < Chess.N && desk[x + signX][y + signY] * signY > 0) {
                if (horizontal != 5) {
                    moves.add(new Move(desk, x, y, x + signX, y + signY));
                } else {
                    for (int i = 2; i < 6; i++) {
                        moves.add(new HMove(desk, x, y, x + signX, y + signY, -i * signY));
                    }
                }
            }
        }
        if (desk[x][y + signY] == 0) {
            if (horizontal != 5) {
                moves.add(new Move(desk, x, y, x, y + signY));
                if (horizontal == -5 && desk[x][y + 2 * signY] == 0) {
                    moves.add(new Move(desk, x, y, x, y + 2 * signY));
                }
            } else {
                for (int i = 2; i < 6; i++) {
                    moves.add(new HMove(desk, x, y, x, y + signY, -i * signY));
                }
            }
        }
    }

    private void countRooks(int x, int y) {
        if (y != (signY > 0 ? 0 : Chess.N - 1) || x != 4) {
            return;
        }
        if (desk[5][y] == 0 && desk[6][y] == 0 && desk[7][y] == -3 * signY) {
            moves.add(new Rook(desk, y, 1));
        }
        if (desk[3][y] == 0 && desk[2][y] == 0 && desk[1][y] == 0 && desk[0][y] == -3 * signY) {
            moves.add(new Rook(desk, y, -1));
        }
    }

    private void count(int x, int y, int signX, int signY, int length) {
        int x0 = x, y0 = y;
        for (int i = 0; i < length; i++) {
            x += signX;
            y += signY;
            if (x < 0 || y < 0 || x >= Chess.N || y >= Chess.N || desk[x][y] * this.signY < 0) {
                return;
            }
            moves.add(new Move(desk, x0, y0, x, y));
            if (desk[x][y] != 0) {
                return;
            }
        }
    }
}
