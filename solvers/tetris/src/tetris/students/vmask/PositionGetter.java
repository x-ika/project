package tetris.students.vmask;

import java.util.ArrayList;
import java.util.List;

import tetris.logic.Pile;

/*
 * Iterates through all possible positions and returns objects (depends on passed BoardRater)
 */
public class PositionGetter {
	private int bWidth;

	private BoardRater boardRater;
	private BoardHelper boardHelper;

	public PositionGetter(int bWidth, int bHeight, BoardRater boardRater) {
		this.bWidth = bWidth;
		this.boardHelper = new BoardHelper(bWidth, bHeight);
		this.boardRater = boardRater;
	}

	// For input pile and board returns best possible position estimated by boardRater
	public Drop getBestDrop(int[] bf, Pile pile) {
		Drop bestDrop = null;
		double bestScore = Double.MAX_VALUE;

		int[] pf = pile.getBitsetPrepresentation();

		// gets number of all different rotations for figure
		int rotNum = BoardHelper.getRotNum(pf);

		// for every rotation
		for (int i = 0; i < rotNum; i++, pile = pile.rotate()) {
			int cBound = bWidth - pile.getWidth() + 1;

			// for every column
			for (int col = 0; col < cBound; col++) {
				// put pile on cloned board
				Drop drp = boardHelper.drop(col, bf.clone(), pile);

				drp.score = boardRater.rateBoard(drp.bf, drp);

				if (drp.score < bestScore) {
					bestScore = drp.score;
					drp.rotAmount = i;
					bestDrop = drp;
				}
			}
		}
		return bestDrop;
	}

	// Returns pile's all possible positions
	public List<Drop> getDrops(int[] bf, Pile pile) {
		List<Drop> drops = new ArrayList<>();

		int[] pf = pile.getBitsetPrepresentation();

		// gets number of all different rotations for figure
		int rotNum = BoardHelper.getRotNum(pf);

		// for every rotation
		for (int i = 0; i < rotNum; i++, pile = pile.rotate()) {
			int cBound = bWidth - pile.getWidth() + 1;

			// for every column
			for (int col = 0; col < cBound; col++) {
				// put pile on cloned board
				Drop drp = boardHelper.drop(col, bf.clone(), pile);

				drp.score = boardRater.rateBoard(drp.bf, drp);

				drops.add(drp);
			}
		}
		return drops;
	}
}