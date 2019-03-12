package tetris.students.vmask;

import tetris.logic.BitRectangle;
import tetris.logic.Command;
import tetris.logic.Pile;
import tetris.logic.Player;
import tetris.logic.Turn;

/**
 * Main class of tetris AI application. To find the best position for input pile, class
 * iterates through pile's all possible locations, puts pile on board and for each of
 * those boards computes their score.
 * Position that had a board with the best score is chosen obviously.
 *
 * Score of a given board is computed via BoardRater interface implementation so
 * we can choose different system of board ranks.
 *
 * @author Vato Maskhulia
 */
public class VatoPlayer implements Player {
	// Given commands array stored in Drop object returns formed Turn class
	private static Turn computeTurn(Drop drp, int bHeight) {
		Turn turn = new Turn();
		for (int i = 0; i < drp.rotAmount; i++) {
			turn.add(Command.ROTATION);
		}
		for (int i = 0; i < drp.col; i++) {
			turn.add(Command.RIGHT);
		}
		for (int i = bHeight; i > drp.row ; i--) {
			turn.add(Command.DOWN);
		}
		return turn;
	}

	public Turn play(BitRectangle board, Pile pile) {
		int bWidth = board.getWidth();
		int bHeight = pile.row;
		int[] bf = board.getBitsetPrepresentation();
		
//		BoardRater boardRater = new BoardRaterSimple(bWidth, bHeight); // RATER FOR NORMAL PILES!
		BoardRater boardRater = new BoardRaterAllPiles(new BoardRaterExp(bWidth, bHeight)); // RATER FOR HARD PILES!
		
		PositionGetter pg = new PositionGetter(bWidth, bHeight, boardRater);
		
		return computeTurn(pg.getBestDrop(bf, pile), bHeight);
	}
}