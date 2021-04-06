package ika.games.gomoku.controller.player;

import ika.games.gomoku.controller.action.GomokuMove;

import java.util.Arrays;

public class MiniMaxPlayer implements GomokuPlayer {

    private static final int N = 20, M = N * N;
    private static final int MAX = 1000;

    private static final class MoveSet {

        int[] b = new int[M + 31 >> 5];
        int[] a = new int[M];
        int z = 0;

        void init(MoveSet set) {
            System.arraycopy(set.b, 0, b, 0, b.length);
            System.arraycopy(set.a, 0, a, 0, z = set.z);
        }

        void add(int x) {
            if ((b[x >> 5] & 1 << x) == 0) {
                b[x >> 5] |= 1 << x;
                a[z++] = x;
            }
        }

        void print() {
            for (int i = 0; i < z; i++) {
                System.out.print("(" + a[i] / N + "," + a[i] % N + ") ");
            }
            System.out.println();
        }

    }

    private MoveSet[] sets;
    private int bestRow, bestCol;
    private int[][] by4;

    public GomokuMove getMove(int[][] desk, int value) {

        by4 = new int[M][M];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                int z = 0;
                for (int a = 0; a < N; a++) {
                    for (int b = 0; b < N; b++) {
                        if (Math.abs(i - a) + Math.abs(j - b) < 3) {
                            by4[i * N + j][z++] = a * N + b;
                        }
                    }
                }
                by4[i * N + j] = Arrays.copyOf(by4[i * N + j], z);
            }
        }

        sets = new MoveSet[99];
        for (int i = 0; i < sets.length; i++) {
            sets[i] = new MoveSet();
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (desk[i][j] != 0) {
                    for (int k = 0; k < sets.length; k++) {
                        addBy(sets[k], desk, i, j);
                    }
                }
            }
        }

        rec(desk, 4, -MAX, MAX);

        return new GomokuMove(bestRow, bestCol);
    }

    private void addBy(MoveSet set, int[][] desk, int i, int j) {
        for (int x : by4[i * N + j]) {
            if (desk[x / N][x % N] == 0) {
                set.add(x);
            }
        }
    }

    private int rec(int[][] desk, int d, int a, int b) {
        if (d == 0) {
            return evaluate(desk, 2);
        }
        int z = sets[d].z, t[] = sets[d].a;
//        System.out.println(z);
        int val = d & 1;
        int v = (2 * val - 1) * MAX;
        for (int i = 0; i < z; i++) {
            int r = t[i] / N, c = t[i] % N;
            if (desk[r][c] == 0) {
                desk[r][c] = 2 - val;
                if (d > 1) {
                    MoveSet set = sets[d - 1];
                    set.init(sets[d]);
                    addBy(set, desk, r, c);
                }
                if (val == 0) {
                    int old = v;
                    v = Math.max(v, rec(desk, d - 1, a, b));
                    if (d == 4 && old < v) {
                        bestRow = r;
                        bestCol = c;
                    }
                    a = Math.max(a, v);
                } else {
                    v = Math.min(v, rec(desk, d - 1, a, b));
                    b = Math.min(b, v);
                }
                desk[r][c] = 0;
                if (a >= b) {
                    break;
                }
            }
        }
        return v;
    }

    private int evaluate(int[][] desk, int value) {
        int[] d = new int[N * N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                d[i * N + j] = desk[i][j];
            }
        }
        return evaluateState(d);
    }

    //-----------------------------------------------------------------------------------

    private int evaluateState(int[] board) {

        // Player
        int p2nwse = nwseDiagonalCheck(board, 2);
        if (p2nwse == 10000)
            return -10000;

        int p2nesw = neswDiagonalCheck(board, 2);
        if (p2nesw == 10000)
            return -10000;

        int p2v = verticalCheck(board, 2);
        if (p2v == 10000)
            return -10000;

        int p2h = horizontalCheck(board, 2);
        if (p2h == 10000)
            return -10000;


        // AI
        int p1nwse = nwseDiagonalCheck(board, 1);
        if (p1nwse == 10000)
            return 10000;

        int p1nesw = neswDiagonalCheck(board, 1);
        if (p1nesw == 10000)
            return 10000;

        int p1v = verticalCheck(board, 1);
        if (p1v == 10000)
            return 10000;

        int p1h = horizontalCheck(board, 1);
        if (p1h == 10000)
            return 10000;

        return Math.max(p1h, Math.max(p1v, Math.max(p1nesw, p1nwse))) - Math.max(p2h, Math.max(p2v, Math.max(p2nesw, p2nwse)));
    }

    private int verticalCheck(int[] sketchBoard, int player) {
        int score = 0;
        int counter = 0;
        int startPos = -1;
        int endPos = -1;
        int index;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                index = j * N + i;
                if (sketchBoard[index] == player) {
                    if (j == 0)
                        counter = 0;

                    counter++;
                    startPos = (j - counter + 1) * N + i;
                } else {

                    if (startPos != -1) {
                        endPos = index;

                        // Determine edge status
                        boolean startAtEdge = startPos < N;
                        boolean endAtEdge = endPos < N;

                        /**
                         * .xxx. - Free from both sides
                         * oxxx. - Free from one side
                         * oxxxo - Closed from both sides
                         * xxx.. - Edge from one side => Free from one side
                         */
                        boolean startFree = !startAtEdge && sketchBoard[(startPos / N - 1) * N + startPos % N] == 0;
                        boolean endFree = !endAtEdge && sketchBoard[endPos] == 0;

                        // If greater, assign calculated score for given position
                        score = Math.max(score, calculateScore(counter, startFree, endFree));

                        startPos = -1;
                    }
                    counter = 0;
                }
            }
        }
        return score;
    }

    private int horizontalCheck(int[] sketchBoard, int player) {
        int score = 0;
        int counter = 0;
        int startPos = -1;
        int endPos = -1;
        int bigN = N * N;
        for (int index = 0; index < bigN; index++) {
            if (sketchBoard[index] == player) {
                if (index % N == 0)
                    counter = 0;

                counter++;
                startPos = index - counter + 1;
            } else {

                if (startPos != -1) {
                    endPos = index;

                    // Determine status
                    boolean startAtEdge = startPos % N == 0;
                    boolean endAtEdge = endPos % N == 0;

                    boolean startFree = !startAtEdge && sketchBoard[startPos - 1] == 0;
                    boolean endFree = !endAtEdge && sketchBoard[endPos] == 0;


                    // If greater, assign calculated score for given position
                    score = Math.max(score, calculateScore(counter, startFree, endFree));


                    startPos = -1;
                }
                counter = 0;
            }
        }

        return score;
    }

    // North-East to South-West diagonal check (/)
    private int neswDiagonalCheck(int[] sketchBoard, int player) {
        boolean startsAtEdge = true;
        boolean endsAtEdge = true;

        boolean startFree;
        boolean endFree;

        int score = 0;
        int counter = 0;
        int startEdge = -1;
        int endEdge = -1;
        int index;


        for (int slice = 0; slice < 2 * N - 1; ++slice) {
            int z = slice < N ? 0 : slice - N + 1;
            for (int j = z; j <= slice - z; ++j) {
                index = j * N + slice - j;
                if (sketchBoard[index] == player) {
                    if (j == 0)
                        counter = 0;

                    counter++;

                    startsAtEdge = j - counter + 1 == 0;
                    startEdge = startsAtEdge ? 0 : (j - counter) * N + slice - (j - counter);
                } else {

                    if (startEdge != -1) {
                        endEdge = index;


                        // Determine status
                        endsAtEdge = j == z;


                        startFree = !startsAtEdge && sketchBoard[startEdge] == 0;
                        endFree = !endsAtEdge && sketchBoard[endEdge] == 0;

                        // If greater, assign calculated score for given position
                        score = Math.max(score, calculateScore(counter, startFree, endFree));


                        startEdge = -1;
                    }
                    counter = 0;
                }

            }
        }
        return score;
    }


    // North-West to South-East diagonal check (\)
    private int nwseDiagonalCheck(int[] sketchBoard, int player) {

        boolean startsAtEdge = true;
        boolean endsAtEdge = true;

        boolean startFree;
        boolean endFree;

        int score = 0;
        int counter = 0;
        int startEdge = -1;
        int endEdge = -1;
        int index;


        for (int slice = 0; slice < 2 * N - 1; ++slice) {
            int z = slice < N ? 0 : slice - N + 1;
            for (int j = z; j <= slice - z; ++j) {
                index = j * N + N - 1 - slice + j;
                if (sketchBoard[index] == player) {
                    if (j == 0)
                        counter = 0;

                    counter++;

                    startsAtEdge = j - counter + 1 == 0;
                    startEdge = startsAtEdge ? 0 : (j - counter) * N + N - 1 - slice + (j - counter);
                } else {

                    if (startEdge != -1) {
                        endEdge = index;
                        endsAtEdge = j == z;


                        // determine position status
                        startFree = !startsAtEdge && sketchBoard[startEdge] == 0;
                        endFree = !endsAtEdge && sketchBoard[endEdge] == 0;

                        // If greater, assign calculated score for given position
                        score = Math.max(score, calculateScore(counter, startFree, endFree));

                        startEdge = -1;
                    }
                    counter = 0;
                }

            }
        }
        return score;
    }

    private int calculateScore(int counter, boolean startFree, boolean endFree) {
        if (counter >= 5) {
            return 10000;
        } else if (startFree && endFree) {
            if (counter == 5 - 1)
                return 900;
            else if (counter > 1)
                return counter * 100;

        } else if (startFree || endFree) {
            if (counter == 5 - 1)
                return 500;
            if (counter > 1)
                return counter * 10;
        }
        return 0;
    }

    public static void main(String[] args) {
        String[] d = {
                "0...X",
                ".....",
                "..X..",
                ".....",
                "X...0",
        };

        int[][] desk = new int[N][N];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (d[i].charAt(j) == 'X') {
                    desk[i + 8][j + 8] = 1;
                }
                if (d[i].charAt(j) == '0') {
                    desk[i + 8][j + 8] = 2;
                }
            }
        }

        long startTime = System.nanoTime();
        int t = new MiniMaxPlayer().getMove(desk, 2).i;
        System.out.println("Time: " + (System.nanoTime() - startTime) / 1e9);

        System.out.println("t = " + t);

    }

}
