package minesweeper;

public interface Simulator {

    void init();

    int[][] getDesk();

    int getEmptyCells();

    int getBombs();

    int getOpenedCells();


    boolean check(int i, int j);

    void openCell(int i, int j);

    void finish();

}
