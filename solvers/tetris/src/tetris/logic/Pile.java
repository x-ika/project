package tetris.logic;

public class Pile extends BitRectangle {

    public final int row, col;

    public Pile(int row, int[] filled) {
        super(0, filled);
        this.row = row;
        this.col = TetrisUtils.leftmost(filled);
        this.filled = TetrisUtils.normalize(filled);
        width = TetrisUtils.rightmost(filled) + 1;
    }

    public Pile rotate() {
        return new Pile(row, TetrisUtils.rotate(filled));
    }
}
