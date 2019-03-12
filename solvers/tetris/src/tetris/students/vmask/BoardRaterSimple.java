package tetris.students.vmask;

/*
 * This class is one of BoardRater implementations. Given board and some additional
 * data (via Drop class), computes board's score.
 * Heuristic is
 *		[ Maximum filled cell's row value,
 *			Amount of holes,
 * 			Amount of holes that are horizontally connected,
 * 			Amount of removed lines,
 * 			Sum of height differences,
 * 			Maximum depth of well,
 * 			Sum of all wells,
 * 			Height of column where pile was dropped,
 * 			Amount of filled cells,
 * 			Amount of filled cells multiplied by their heights,
 * 			Number of row transitions,
 * 			Number of column transitions ]
 *
 * This vector is multiplied by weight's vector (found by research
 * 	Departments of Computer Science 8 and 2, University of Erlangen-Nuremberg)
 */
public class BoardRaterSimple implements BoardRater {
	// weights for heuristic
	public static final int[] weights = new int[]
		{ 2382, 135028, 48491, 74343, -59739, 72528, 61591, -13344, 19737, 47653, 50015, 144688 };

	private int bWidth;
	private int bHeight;

	public BoardRaterSimple(int bWidth, int bHeight) {
		this.bWidth = bWidth;
		this.bHeight = bHeight;
	}

	public int getBWidth() { return bWidth; }
	public int getBHeight() { return bHeight; }

	// Gives input board it's score. Depends on heuristic
	public double rateBoard(int[] bf, Drop drp) {
		BoardData data = new BoardData(bWidth, bHeight, bf);

		if (data.maxRow >= bHeight) return 10000000000000.0;

		return weights[0] * data.maxRow +
			weights[1] * data.holeAmount +
			weights[2] * data.connectedHoleAmount +
			weights[3] * drp.rowsCleaned +
			weights[4] * data.heightDiff +
			weights[5] * data.maxWellDepth +
			weights[6] * data.sumWells +
			weights[7] * drp.rowHeight +
			weights[8] * data.cellAmount +
			weights[9] * data.heightCellAmount +
			weights[10] * data.rowTransitions +
			weights[11] * data.colTransitions;
	}
}