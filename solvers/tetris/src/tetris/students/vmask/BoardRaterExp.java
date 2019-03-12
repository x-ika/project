package tetris.students.vmask;

/*
 * The same as BoardRaterSimple except that during heuristic calculation board parameters'
 * power with respect of exp array values are taken.
 */
public class BoardRaterExp implements BoardRater {
	// weights for heuristic
	public static final int[] weights = new int[]
		{ 2382, 135028, 48491, 74343, -59739, 72528, 61591, -13344, 19737, 47653, 50015, 144688 };
	public static final double[] exp = new double[]
		{ 0.935, 1.839, 0.905, 1.078, 0.858, 2.207, 1.565, 0.023, 0.117, 0.894, 1.474, 1.346 };

	private int bWidth;
	private int bHeight;

	public BoardRaterExp(int bWidth, int bHeight) {
		this.bWidth = bWidth;
		this.bHeight = bHeight;
	}

	public int getBWidth() { return bWidth; }
	public int getBHeight() { return bHeight; }

	// Gives input board it's score. Depends on heuristic
	public double rateBoard(int[] bf, Drop drp) {
		BoardData data = new BoardData(bWidth, bHeight, bf);

		if (data.maxRow >= bHeight) return 10000000000000.0;

		return weights[0] * Math.pow(data.maxRow, exp[0]) +
			weights[1] * Math.pow(data.holeAmount, exp[1]) +
			weights[2] * Math.pow(data.connectedHoleAmount, exp[2]) +
			weights[3] * Math.pow(drp.rowsCleaned, exp[3]) +
			weights[4] * Math.pow(data.heightDiff, exp[4]) +
			weights[5] * Math.pow(data.maxWellDepth, exp[5]) +
			weights[6] * Math.pow(data.sumWells, exp[6]) +
			weights[7] * Math.pow(drp.rowHeight, exp[7]) +
			weights[8] * Math.pow(data.cellAmount, exp[8]) +
			weights[9] * Math.pow(data.heightCellAmount, exp[9]) +
			weights[10] * Math.pow(data.rowTransitions, exp[10]) +
			weights[11] * Math.pow(data.colTransitions, exp[11]);
	}
}