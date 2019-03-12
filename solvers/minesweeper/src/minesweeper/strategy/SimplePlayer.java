package minesweeper.strategy;

import minesweeper.Solver;
import minesweeper.Cell;

import java.util.*;

public class SimplePlayer {

    private Solver solver;

    private Random rnd = new Random(1);

    public SimplePlayer(Solver solver) {
        this.solver = solver;
    }

    public Cell makeTurn(int[][] desk, int bombs) {
        double[][] prob = solver.getProbs(desk, bombs);

        double p = 1e9;
        List<Cell> list = new ArrayList<>();
        for (int i = 0; i < desk.length; i++) {
            for (int j = 0; j < desk[i].length; j++) {
                if (desk[i][j] != -1) {
                    continue;
                }
                double cur = prob[i][j];
                if (p >= cur) {
                    if (p > cur) {
                        list.clear();
                    }
                    p = cur;
                    list.add(new Cell(i, j));
                }
            }
        }
        return list.get(0);
    }

}
