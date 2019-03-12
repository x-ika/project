package minesweeper.simulator;

import minesweeper.gui.TableClickListener;
import minesweeper.gui.Table;

import java.util.*;

public class TestSimulator extends AbstractSimulator implements TableClickListener {

    private static Random rnd = new Random(1);

    public TestSimulator(int n, int m, int k) {
        this.n = n;
        this.m = m;
        this.k = k;
    }

    public void init() {

        super.init();

        // place bombs
        int t = k;
        while (t > 0) {
            int i = rnd.nextInt(n);
            int j = rnd.nextInt(m);
            if (!hasBomb[i][j]) {
                hasBomb[i][j] = true;
                t--;
            }
        }

        // calculate number of neghboor bombs for each cell
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {

                for (int a = Math.max(0, i - 1); a < Math.min(n, i + 2); a++) {
                    for (int b = Math.max(0, j - 1); b < Math.min(m, j + 2); b++) {
                        numberOfNeighboors[i][j] += hasBomb[a][b] ? 1 : 0;
                    }
                }

            }
        }

    }

    public void openCell(int i, int j) {
        if (hasBomb[i][j]) {
            visiblePart[i][j] = 13;
            return;
        }

        visiblePart[i][j] = numberOfNeighboors[i][j];
        opened++;

        if (numberOfNeighboors[i][j] > 0) {
            return;
        }

        for (int a = Math.max(0, i - 1); a < Math.min(n, i + 2); a++) {
            for (int b = Math.max(0, j - 1); b < Math.min(m, j + 2); b++) {
                if (visiblePart[a][b] == -1) {
                    openCell(a, b);
                }
            }
        }

    }

    private void updateAndWate(Table table) {
        table.update();
        try {
            wait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void mouseClicked(int row, int column) {
        notify();
    }

}
