package player.students.guga;

import main.*;

import java.util.Vector;


public class GuggerPlayer implements Player {
    public void gameOver() {
    }

    private int initialDepth = 7;
    private Turn t;

    public Turn makeTurne(int[][] d, int player) {
        t = null;
        alphabeta(d, initialDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, player);
//		if(t!= null && t.killed.size()>1)
//			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        // ika
        int h = t.first.row;
        t.first.row = t.first.col;
        t.first.col = h;
        h = t.last.row;
        t.last.row = t.last.col;
        t.last.col = h;
        for (Checker f : t.killed) {
            h = f.row;
            f.row = f.col;
            f.col = h;
        }
        // ika
        return t;
    }

    public double alphabeta(int[][] d, int depth, double alpha, double beta, int player) {
        GuggerTurnsGetter tg = new GuggerTurnsGetter(d, player);
        Vector<Turn> turns = tg.getAllPossibleTurns();

        if (depth == 0 || turns.size() == 0) {
            return heuristic(d);
        }
        if (player == 1) {
            for (int i = 0; i < turns.size(); i++) {
                perform(turns.get(i), d);
                double alphaN = alphabeta(d, depth - 1, alpha, beta, 2);
                undo(turns.get(i), d);
                if (alphaN > alpha) {
                    alpha = alphaN;
                    if (depth == initialDepth)
                        t = turns.get(i);
                }

                if (beta < alpha)
                    break;
            }
            return alpha;
        } else {
            for (int i = 0; i < turns.size(); i++) {
                perform(turns.get(i), d);
                double betaN = alphabeta(d, depth - 1, alpha, beta, 1);
                undo(turns.get(i), d);
                if (betaN < beta) {
                    beta = betaN;
                    if (depth == initialDepth)
                        t = turns.get(i);
                }

                if (beta < alpha)
                    break;
            }
            return beta;
        }
    }

    private double heuristic(int[][] b) {
        double heu = 0;
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (b[i][j] == 1)
                    heu += 1;
                if (b[i][j] == 2)
                    heu += -1;
                if (b[i][j] == 4)
                    heu += 3;
                if (b[i][j] == 5)
                    heu += -3;

                if (b[i][j] == 1 && i == 1)
                    heu += 1;
                if (b[i][j] == 2 && i == 6)
                    heu += -1;
            }
        }
        return heu;
    }

    private void perform(Turn t, int[][] d) {
        d[t.last.col][t.last.row] = t.last.value;
        d[t.first.col][t.first.row] = 0;
        for (int i = 0; i < t.killed.size(); i++) {
            d[t.killed.get(i).col][t.killed.get(i).row] = 0;
        }
    }

    private void undo(Turn t, int[][] d) {
        d[t.first.col][t.first.row] = t.first.value;
        d[t.last.col][t.last.row] = 0;
        for (int i = 0; i < t.killed.size(); i++) {
            d[t.killed.get(i).col][t.killed.get(i).row] = t.killed.get(i).value;
        }
    }
}
