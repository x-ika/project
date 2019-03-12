package main;

public class Checker {
    public int row, col, value;

    public Checker(int row, int col, int value) {
        this.row = row;
        this.col = col;
        this.value = value;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Checker)) return false;
        Checker checker = (Checker) o;
        return col == checker.col && row == checker.row && value == checker.value;

    }
}
