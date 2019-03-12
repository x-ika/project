package tetris.logic;

/**
 * int[] represents the black-white board or pile where each row is represented by one integer
 * for example: array {5, 6, 4, 5, 0, 0} consists from 6 rows
 * bottom row is 5 =   101 (in binary)
 *               6 =  1010
 *               4 =   100
 *              17 = 10001
 *               0 = 0
 *               0 = 0
 * if board width is 6 then we should pad all binary representations by zeroes to get board
 * (0 --> '.', 1 --> '#'):
 * ......
 * ......
 * .#...#
 * ...#..
 * ..#.#.
 * ...#.#
 */
public class BitRectangle {

    protected int width;
    protected int[] filled;

    public BitRectangle(int width, int[] filled) {
        this.width = width;
        this.filled = filled;
    }

    public boolean isOccupied(int row, int col) {
        if (row < 0 || row >= filled.length || col < 0 || col >= width) {
            throw new RuntimeException("Block is outside of the board");
        }
        return (filled[row] & 1 << col) != 0;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return filled.length;
    }

    public int area() {
        return width * filled.length;
    }

    public int filledArea() {
        return TetrisUtils.area(filled);
    }

    public int[] getBitsetPrepresentation() {
        return filled.clone();
    }

    public BitRectangle copy() {
        return new BitRectangle(width, filled.clone());
    }

}
