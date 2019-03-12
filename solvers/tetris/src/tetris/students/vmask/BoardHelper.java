package tetris.students.vmask;

import java.util.Arrays;

import tetris.logic.Pile;
import tetris.logic.TetrisUtils;

/*
 * Class provides additional helper methods for main application:
 * 		1. choosing possible rotation amount for a given pile;
 * 		2. doping pile on board.
 */
public class BoardHelper {
	private static final int[][] TWO_ROTATION_PILES = {
		{15}, //brick
		{3, 6},  //zig-zag
		{6, 3}  //zag-zig
	};
	private static final int[][] ONE_ROTATION_PILES = {
		{3, 3},  //block
		{2, 7, 2},  // +
	};

	public static int getRotNum(int[] pf) {
		for (int[] pile : ONE_ROTATION_PILES) {
			if (Arrays.equals(pf, pile)) {
				return 1;
			}
		}
		for (int[] pile : TWO_ROTATION_PILES) {
			if (Arrays.equals(pf, pile)) {
				return 2;
			}
		}
		return 4;
	}

	private int filledRow; // value of filled row
	private int bHeight;

	public BoardHelper(int bWidth, int bHeight) {
		filledRow = (1 << bWidth) - 1;
		this.bHeight = bHeight;
	}

	// Drops pile on input board and returns Drop object that stores needed data
	public Drop drop(int col, int[] bf, Pile pile) {
		int[] pf = pile.getBitsetPrepresentation();

		Drop drp = new Drop();
		int row = bHeight;

		while (!TetrisUtils.checkContact(bf, pf, row, col)) {
			row--;
		}

		// place pile to computed position
		TetrisUtils.xor(bf, pf, row, col);

		// delete rows in case row got filled
		drp.rowHeight = row + 1;
		for (int i = row; i < row + pile.getHeight(); i++) {
			if (bf[i] == filledRow) {
				System.arraycopy(bf, i + 1, bf, i, bf.length - i - 1);
				drp.rowsCleaned++;
				drp.rowHeight--;
				i--;
			}
		}

		drp.row = row;
		drp.col = col;
		drp.bf = bf;

		return drp;
	}
}