package minesweeper.simulator;

import minesweeper.Simulator;

import java.util.Arrays;

public abstract class AbstractSimulator implements Simulator {

    protected int n, m, k, opened;

    protected boolean[][] hasBomb;
    protected int[][] visiblePart;
    protected int[][] numberOfNeighboors;

    public void init() {
        opened = 0;
        hasBomb = new boolean[n][m];
        visiblePart = new int[n][m];
        numberOfNeighboors = new int[n][m];

        // init player visible desk
        for (int i = 0; i < n; i++) {
            Arrays.fill(visiblePart[i], -1);
        }
    }

    public int[][] getDesk() {
        return visiblePart;
    }

    public int getEmptyCells() {
        return n * m - k;
    }

    public int getBombs() {
        return k;
    }

    public int getOpenedCells() {
        return opened;
    }

    public boolean check(int i, int j) {
        return hasBomb[i][j];
    }

    public void finish() {

    }

}
