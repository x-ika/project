package player;

import main.*;

import java.util.*;

public abstract class BasicPlayer implements Player {

    protected int[][] desk;
    protected int result;

    private Checker[][][] all;

    public void copyDesk(int[][] d) {
        desk = new int[d.length][d.length];
        for (int i = 0; i < d.length; i++) {
            System.arraycopy(d[i], 0, desk[i], 0, d.length);
        }
        all = new Checker[d.length][d.length][6];
        for (int i = 0; i < d.length; i++) {
            for (int j = 0; j < d.length; j++) {
                for (int k = 0; k < 6; k++) {
                    all[i][j][k] = new Checker(i, j, k);
                }
            }
        }
    }

    public List<Turn> countTurnes(int number) {
        List<Turn> turns = new ArrayList<>();
        for (int i = 0; i < desk.length; i++) {
            for (int j = (i + 1) % 2; j < desk.length; j += 2) {
                if (desk[i][j] % 3 == number) {
                    int t = desk[i][j];
                    desk[i][j] = 0;
                    recursiveCount(all[i][j][t], i, j, t, 0, 0, new ArrayList<>(), turns);
                    desk[i][j] = t;
                }
            }
        }
        if (!turns.isEmpty()) {
            return turns;
        }
        for (int i = 0; i < desk.length; i++) {
            for (int j = (i + 1) % 2; j < desk.length; j += 2) {
                if (desk[i][j] % 3 == number) {
                    count(turns, i, j);
                }
            }
        }
        return turns;
    }

    private void count(List<Turn> turns, int fi, int fj) {
        int val = desk[fi][fj];
        if (val < 3) {
            int si = 2 * (val % 3) - 3;
            for (int sj = -1; sj < 2; sj += 2) {
                if (isEmpty(fi + si, fj + sj)) {
                    turns.add(new Turn(all[fi][fj][val], all[fi + si][fj + sj][getValue(fi + si, val)], null));
                }
            }
        } else {
            for (int si = -1; si < 2; si = si + 2) {
                for (int sj = -1; sj < 2; sj = sj + 2) {
                    int i = fi, j = fj;
                    while (isEmpty(i += si, j += sj)) {
                        turns.add(new Turn(all[fi][fj][val], all[i][j][val], null));
                    }
                }
            }
        }
    }

    protected void recursiveCount(Checker f, int i, int j, int val, int si, int sj, List<Checker> v, List<Turn> list) {
        for (int nsi = -1; nsi < 2; nsi += 2) {
            for (int nsj = -1; nsj < 2; nsj += 2) {
                if (nsi == -si && nsj == -sj) {
                    continue;
                }
                Checker killed = canKill(i, j, val, nsi, nsj);
                if (killed == null) {
                    continue;
                }
                v.add(killed);
                int t = killed.value;
                desk[killed.row][killed.col] = -3;
                if (val < 3) {
                    int ii = i + 2 * nsi;
                    int jj = j + 2 * nsj;
                    if (canKillInAnyDirection(ii, jj, getValue(ii, val), nsi, nsj)) {
                        recursiveCount(f, ii, jj, getValue(ii, val), nsi, nsj, v, list);
                    } else {
                        list.add(new Turn(f, all[ii][jj][getValue(ii, val)], new ArrayList<>(v)));
                    }
                } else {
                    int ii = killed.row;
                    int jj = killed.col;
                    boolean cantKill = true;
                    while (isEmpty(ii += nsi, jj += nsj)) {
                        if (canKillInAnyDirection(ii, jj, val, nsi, nsj)) {
                            cantKill = false;
                            recursiveCount(f, ii, jj, val, nsi, nsj, v, list);
                        }
                    }
                    if (cantKill) {
                        ii = killed.row;
                        jj = killed.col;
                        ArrayList<Checker> copy = new ArrayList<>(v);
                        while (isEmpty(ii += nsi, jj += nsj)) {
                            list.add(new Turn(f, all[ii][jj][val], copy));
                        }
                    }
                }
                desk[killed.row][killed.col] = t;
                v.remove(v.size() - 1);
            }
        }
    }

    private boolean canKillInAnyDirection(int i, int j, int v, int si, int sj) {
        for (int nsi = -1; nsi < 2; nsi += 2) {
            for (int nsj = -1; nsj < 2; nsj += 2) {
                if (nsi == -si && nsj == -sj) {
                    continue;
                }
                if (canKill(i, j, v, nsi, nsj) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    protected Checker canKill(int i, int j, int v, int si, int sj) {
        if (v >= 3) {
            while (isEmpty(i + si, j + sj)) {
                i += si;
                j += sj;
            }
        }
        return killedChecker(i + si, j + sj, v, si, sj);
    }

    private Checker killedChecker(int i, int j, int v, int si, int sj) {
        return isEmpty(i + si, j + sj) && (desk[i][j] + v) % 3 == 0 ? all[i][j][desk[i][j]] : null;
    }

    //-----------------------------------------------------------------------------------

    protected void forward(Turn turn) {
        int s = turn.last.value - turn.first.value;
        desk[turn.first.row][turn.first.col] = 0;
        desk[turn.last.row][turn.last.col] = turn.last.value;
        if (turn.killed != null) {
            for (Checker ch : turn.killed) {
                s += ch.value;
                desk[ch.row][ch.col] = 0;
            }
        }
        result += (2 * (turn.last.value % 3) - 3) * s;
    }

    protected void back(Turn turn) {
        int s = turn.last.value - turn.first.value;
        desk[turn.first.row][turn.first.col] = turn.first.value;
        desk[turn.last.row][turn.last.col] = 0;
        if (turn.killed != null) {
            for (Checker ch : turn.killed) {
                s += ch.value;
                desk[ch.row][ch.col] = ch.value;
            }
        }
        result -= (2 * (turn.last.value % 3) - 3) * s;
    }

    private int getValue(int i, int v) {
        return i == (CheckersController.N - 1) * (v % 3 - 1) ? v % 3 + 3 : v;
    }

    private boolean isEmpty(int i, int j) {
        return isOnTheDesk(i, j) && desk[i][j] == 0;
    }

    private boolean isOnTheDesk(int i, int j) {
        return 0 <= i && i < CheckersController.N && 0 <= j && j < CheckersController.N;
    }

    //-----------------------------------------------------------------------------------

    public static boolean equals(Turn t1, Turn t2) {
        return t1.first.equals(t2.first) && t1.last.equals(t2.last) && equalsTo(t1.killed, t2.killed);
    }

    public static boolean equalsTo(List<? extends Checker> a, List<? extends Checker> b) {
        if (a != null && a.size() == 0) {
            a = null;
        }
        if (b != null && b.size() == 0) {
            b = null;
        }
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null || a.size() != b.size()) {
            return false;
        }
        List<Checker> bb = new ArrayList<>(b);
        M:
        for (Checker x : a) {
            for (int i = 0; i < bb.size(); i++) {
                if (x.equals(bb.get(i))) {
                    bb.remove(i);
                    continue M;
                }
            }
            return false;
        }
        return true;
    }

    public void gameOver() {
        // do nothing
    }

}
