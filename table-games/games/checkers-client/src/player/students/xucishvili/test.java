package player.students.xucishvili;

import main.*;

import java.util.ArrayList;

public class test implements Player {
    public void gameOver() {
    }

    private static final int DIMENSION = 8;
    private static final int EMPTY = 0;
    private static final int WHITE = 1;
    private static final int BLACK = 2;
    private static final int WHITEKING = 4;
    private static final int BLACKKING = 5;

    private static int myColor, ox, oy;
    private static boolean killed;
    private static ArrayList<Turn> oneCheckerTurns = new ArrayList<>();

    public Turn makeTurne(int[][] d, int value) {
        myColor = value;
        ArrayList<Turn> turnsList = new ArrayList<>();
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = (i + 1) % 2; j < DIMENSION; j += 2) {
                if (d[i][j] == myColor || d[i][j] == myColor + 3) {
                    killed = false;
                    ox = i;
                    oy = j;
                    getAnsForThisChecker(d);
                    integrateInfo(turnsList, oneCheckerTurns);
                    oneCheckerTurns.clear();
                }
            }
        }
        if (killed) deleteSimpleTurns(turnsList);
        alphabeta(d, 4, Integer.MIN_VALUE, Integer.MAX_VALUE, WHITE);
        return null;
    }

    private static void getAnsForThisChecker(int[][] d) {
        if (d[ox][oy] == WHITEKING || d[ox][oy] == BLACKKING) kingTurns(d);
        else commonPlayerTurns(d);
    }

    private static void kingTurns(int[][] d) {
        for (int dX = -1; dX < 2; dX += 2) {
            for (int dY = -1; dY < 2; dY += 2) {
                if (isKillableByKing(d, ox, oy, dX, dY, null) >= 0) {
                    int[][] wasStone = new int[DIMENSION][DIMENSION];
                    wasStone[ox][oy] = 2;
                    recKing(d, ox, oy, 0, new ArrayList<>(), wasStone);
                    killed = true;
                    break;
                } else {
                    int curX = ox;
                    int curY = oy;
                    while (true) {
                        curX += dX;
                        curY += dY;
                        if (!inBounds(curX, curY) || d[curX][curY] != EMPTY) break;
                        Checker first = new Checker(ox, oy, myColor + 3);
                        Checker second = new Checker(curX, curY, myColor + 3);
                        Turn turn = new Turn(first, second, null);
                        oneCheckerTurns.add(turn);
                    }
                }
            }
            if (killed) break;
        }
    }

    private static int isKillableByKing(int[][] d, int x, int y, int dX, int dY
            , int[][] wasStone)
    {
        int step = 0;
        int curX = x;
        int curY = y;
        while (true) {
            curX += dX;
            curY += dY;
            if (!inBounds(curX, curY)) return -1;
            if (d[curX][curY] != EMPTY) {
                if (d[curX][curY] != myColor && d[curX][curY] != myColor + 3) {
                    int mustBeEmptyX = curX + dX;
                    int mustBeEmptyY = curY + dY;
                    if (inBounds(mustBeEmptyX, mustBeEmptyY)) {
                        if ((d[mustBeEmptyX][mustBeEmptyY] == EMPTY)
                                || (wasStone != null && wasStone[mustBeEmptyX][mustBeEmptyY] == 2))
                        {
                            return step;
                        }
                    }
                } else {
                    return -1;
                }
            } else {
                step++;
            }
        }
    }

    private static void recKing(int[][] d, int curX, int curY, int s,
                                ArrayList<Checker> l, int[][] wasStone)
    {
        boolean killable = false;
        for (int dX = -1; dX < 2; dX += 2) {
            for (int dY = -1; dY < 2; dY += 2) {
                int step = isKillableByKing(d, curX, curY, dX, dY, wasStone);
                int newX = curX + (step + 1) * dX;
                int newY = curY + (step + 1) * dY;
                if (step >= 0 && wasStone[newX][newY] != 1) {
                    killable = true;
                    int tX = newX + dX;
                    int tY = newY + dY;
                    while (true) {
                        if (!inBounds(tX, tY) || d[tX][tY] != EMPTY) {
                            break;
                        }
                        ArrayList<Checker> copyDeads = copyOfList(l);
                        int[][] copyBeen = copyOfArr(wasStone);
                        copyBeen[newX][newY] = 1;
                        copyDeads.add(new Checker(newX, newY, d[newX][newY]));
                        recKing(d, tX, tY, s + 1, copyDeads, copyBeen);
                        tX += dX;
                        tY += dY;
                    }
                } else if (s != 0 && dX == 1 && dY == 1 && !killable) {
                    Checker first = new Checker(ox, oy, myColor + 3);
                    Checker second = new Checker(curX, curY, myColor + 3);
                    Turn turn = new Turn(first, second, l);
                    oneCheckerTurns.add(turn);
                }
            }
        }
    }

    private static void commonPlayerTurns(int[][] d) {
//		System.out.println("player");
        for (int deltaX = -1; deltaX < 2; deltaX += 2) {
            for (int deltaY = -1; deltaY < 2; deltaY += 2) {
                int newX = ox + deltaX;
                int newY = oy + deltaY;
                if (isKillableByCommon(d, ox, oy, deltaX, deltaY, null)) {
                    int[][] wasStone = new int[DIMENSION][DIMENSION];
                    wasStone[ox][oy] = 2;
                    recCommonPl(d, ox, oy, 0, new ArrayList<>(), wasStone);
                    killed = true;
                    break;
                } else if (inBounds(newX, newY) && d[newX][newY] == EMPTY) {
                    if ((myColor == WHITE && deltaX == -1) || myColor == BLACK
                            && deltaX == 1)
                    {
                        Checker first = new Checker(ox, oy, myColor);
                        Checker last = new Checker(newX, newY, myColor);
                        Turn turn = new Turn(first, last, null);
                        oneCheckerTurns.add(turn);
                    }
                }
            }
            if (killed) break;
        }
    }

    private static boolean inBounds(int i, int j) {
        return i >= 0 && i < DIMENSION && j >= 0 && j < DIMENSION;
    }

    private static boolean isKillableByCommon(int[][] d, int x, int y, int deltaX,
                                              int deltaY, int[][] wasStone)
    {
        int victimX = x + deltaX;
        int victimY = y + deltaY;
        if (inBounds(victimX, victimY)) {
            if (d[victimX][victimY] != EMPTY && d[victimX][victimY] != myColor
                    && d[victimX][victimY] != myColor + 3)
            {
                int mustBeEmptyX = x + deltaX * 2;
                int mustBeEmptyY = y + deltaY * 2;
                if (inBounds(mustBeEmptyX, mustBeEmptyY)) {
                    if (d[mustBeEmptyX][mustBeEmptyY] == EMPTY ||
                            (wasStone != null && wasStone[mustBeEmptyX][mustBeEmptyY] == 2))
                        return true;
                }
            }
        }
        return false;
    }

    private static void recCommonPl(int[][] d, int curX, int curY, int s,
                                    ArrayList<Checker> l, int[][] wasStone)
    {
        boolean killable = false;
        for (int dX = -1; dX < 2; dX += 2) {
            for (int dY = -1; dY < 2; dY += 2) {
                int newX = curX + dX;
                int newY = curY + dY;
                if (isKillableByCommon(d, curX, curY, dX, dY, wasStone)
                        && wasStone[newX][newY] != 1)
                {
                    killable = true;
                    ArrayList<Checker> copyDeads = copyOfList(l);
                    int[][] copyBeen = copyOfArr(wasStone);
                    copyBeen[newX][newY] = 1;
                    copyDeads.add(new Checker(newX, newY, d[newX][newY]));
                    recCommonPl(d, curX + dX * 2, curY + dY * 2, s + 1, copyDeads,
                            copyBeen);
                } else if (s != 0 && dX == 1 && dY == 1 && !killable) {
                    Checker first = new Checker(ox, oy, myColor);
                    Checker second = new Checker(curX, curY, myColor);
                    Turn turn = new Turn(first, second, l);
                    oneCheckerTurns.add(turn);
                }
            }
        }
    }

    private static ArrayList<Checker> copyOfList(ArrayList<Checker> l) {
        ArrayList<Checker> k = new ArrayList<>();
        for (int i = 0; i < l.size(); i++) {
            k.add(l.get(i));
        }
        return k;
    }

    private static int[][] copyOfArr(int[][] a) {
        int[][] b = new int[DIMENSION][DIMENSION];
        for (int i = 0; i < DIMENSION; i++) {
            for (int j = 0; j < DIMENSION; j++) {
                b[i][j] = a[i][j];
            }
        }
        return b;
    }

    private static void integrateInfo(ArrayList<Turn> nest, ArrayList<Turn> temp) {
        for (int i = 0; i < temp.size(); i++) {
            nest.add(temp.get(i));
        }
    }

    private static void deleteSimpleTurns(ArrayList<Turn> list) {
        int length = list.size();
        for (int i = 0; i < length; i++) {
            if (list.get(i).killed == null) {
                list.remove(i);
                i--;
            }
        }
    }

    private static Turn alphabeta(int[][] d, int depth, int a, int b, int player) {
        if (depth == 0 || isTerminal(d)) return hVal();
//        if(player == WHITE) {
//        	for(each child of node) {
//            	a = Math.max(a, alphabeta(child, depth-1, a, b, 3-player));
//            	if(b <= a) break;
//        	}
//            return a;
//        } else {
//        	for(each child of node){
//            	b = Math.min(b, alphabeta(child, depth-1, a, b, 3-player));
//            	if(b <= a) break;
//        	}
//            return b;
//        }
        return null;
    }

    private static boolean isTerminal(int[][] d) {
        return false;
    }

    private static Turn hVal() {
        return null;
    }
}
