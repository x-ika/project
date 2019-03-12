package tetris.students.vmask;

import tetris.logic.Pile;

/*
 * Class is Decorator of BoardRater. It computes board score by iterating
 * through all possible piles putting them on board, counting each board's score
 * (which is returned by boardRater implementation that is provided by user), and
 * taking average of all scores.
 */
public class BoardRaterAllPiles implements BoardRater {
	private static final int[][] POSSIBLE_PILES = {
//		{3, 3},  //block
//		{15},    //brick
		{3, 6},  //zig-zag
		{6, 3},  //zag-zig
		{7, 1},  // L
		{7, 4},  // !L
		{2, 7, 2},  // +
//		{7, 5},  // |_|_
		{7, 2},  // _|_
	};

	private Pile[] allPiles;
	private BoardRater boardRater;
	private int bWidth;
	private int bHeight;

	private PositionGetter possitionGetter;

	public BoardRaterAllPiles(BoardRater boardRater) {
		this.boardRater = boardRater;
		bWidth = boardRater.getBWidth();
		bHeight = boardRater.getBHeight();

		// will get one best drop
		possitionGetter = new PositionGetter(bWidth, bHeight, boardRater);

		// instantiate all possible piles
		allPiles = new Pile[POSSIBLE_PILES.length];
		for (int i = 0; i < allPiles.length; i++) {
			allPiles[i] = new Pile(bHeight, POSSIBLE_PILES[i]);
		}
	}

	public int getBWidth() { return bWidth; }
	public int getBHeight() { return bHeight; }

	public double rateBoard(int[] bf, Drop drp) {
		double sumScores = 0;
		for (int i = 0; i < allPiles.length; i++) {
			sumScores += possitionGetter.getBestDrop(bf, allPiles[i]).score;
		}
		return boardRater.rateBoard(bf, drp) + sumScores;
	}
}