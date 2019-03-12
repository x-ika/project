package tetris.students.vmask;

import tetris.logic.TetrisUtils;

/*
 * Helper class, needed to compute board parameters for heuristic.
 */
public class BoardData {
	// Computes altitude (height) of input column
	private static int getColHeight(int col, int bHeight, int[] bf) {
		for (int i = bHeight; i >= 0; i--) {
			if ((bf[i] & (1 << col)) > 0) { // if is not empty
				return i + 1;
			}
		}
		return 0;
	}

	public int maxRow;
	public int holeAmount = 0;
	public int connectedHoleAmount = 0;
	public int heightDiff = 0;
	public int maxWellDepth = 0;
	public int sumWells = 0;
	public int cellAmount = 0;
	public int heightCellAmount = 0;
	public int rowTransitions = 0;
	public int colTransitions = 0;

	public BoardData(int bWidth, int bHeight, int[] bf) {
		maxRow = TetrisUtils.top(bf);

		// count
		int[] colHeights = new int[bWidth];
		for (int col = 0; col < bWidth; col++) {
			colHeights[col] = getColHeight(col, bHeight, bf);
		}

		// count amount of cells
		for (int row = 0; row < bHeight; row++) {
			cellAmount += Integer.bitCount(bf[row]);
		}

		int minHeight = Integer.MAX_VALUE;
		
		// count amount of holes
		for (int col = 0; col < bWidth; col++) {
			int colH = colHeights[col];

			boolean areConnectedHoles = false;
			for (int y = colH - 2; y >= 0; y--) {
				if ((bf[y] & (1 << col)) == 0) {
					if (!areConnectedHoles) connectedHoleAmount++;
					areConnectedHoles = true;
					holeAmount++;
				} else {
					areConnectedHoles = false;
				}
			}
			if (colH < minHeight) minHeight = colH;
		}

		for (int col = 0; col < bWidth; col++) {
			boolean prevCell = (bf[0] & (1 << col)) == 0;

			for (int row = 0; row < bHeight; row++) {
				if (((bf[row] & (1 << col)) == 0) != prevCell) {
					colTransitions++;
					prevCell = !prevCell;
				}
			}

			for (int row = bHeight; row >= 0; row--) {
				if ((bf[row] & (1 << col)) != 0) { // if is not empty
					heightCellAmount += row + 1;
				}
			}
		}

		for (int row = 0; row < bHeight; row++) {
			boolean prevCell = (bf[row] & (1 << 0)) == 0;
			for (int col = 1; col < bWidth; col++) {
				if (((bf[row] & (1 << col)) == 0) != prevCell) {
					rowTransitions++;
					prevCell = !prevCell;
				}
			}
		}

		for (int col = 2; col < bWidth; col++) {
			int well = 0;

			int colH1 = colHeights[col - 2];
			int colH2 = colHeights[col - 1];
			int colH3 = colHeights[col];

			// boarders
			if (col == 2) {
				if (colH2 > colH1) {
					well = colH2 - colH1;
					sumWells += well;
					if (well > maxWellDepth) maxWellDepth = well;
				}
			} else if (col == bWidth - 1) {
				if (colH2 > colH3) {
					well = colH2 - colH3;
					sumWells += well;
					if (well > maxWellDepth) maxWellDepth = well;
				}
			}
			if (colH2 < colH3 && colH2 < colH1) {
				well = Math.min(colH1, colH3) - colH2;
				sumWells += well;
				if (well > maxWellDepth) maxWellDepth = well;
			}
		}

		heightDiff = maxRow + 1 - minHeight;
	}
}