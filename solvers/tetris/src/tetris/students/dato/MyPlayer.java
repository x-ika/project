package tetris.students.dato;

import static tetris.logic.TetrisUtils.*;

import tetris.logic.*;

public class MyPlayer implements Player {
	private double score;
	private double futureScore;

	public Turn play(BitRectangle board, Pile pile) {
        score = Integer.MAX_VALUE;
		futureScore = score;
		int optimalRow = 0, optimalColumn = 0, optimalRotation = 0;
		if (pile == null || pile.filledArea() < 4) {
			return new Turn();
		}
        if (TetrisUtils.top(board.getBitsetPrepresentation()) > board.getHeight() - 9) {
            Turn t = new Turn();
            int r = pile.row;
            while (!checkContact(board.getBitsetPrepresentation(), pile.getBitsetPrepresentation(), r, pile.col)) {
                t.add(Command.DOWN);
                r--;
            }
            return t;
        }
        for (int i = 0; i < 4; i++) {
			// puts rotated pile to all possible locations
			for (int c = 0; c < board.getWidth() - pile.getWidth() + 1; c++) {
				int[] myboard = board.getBitsetPrepresentation();
				for (int j = board.getHeight() - 5 - pile.getHeight() + 1; j >= 0; j--) {
					if (TetrisUtils.checkContact(myboard,
							pile.getBitsetPrepresentation(), j, c)) {
						xor(myboard, pile.getBitsetPrepresentation(), j, c);
						int cleared = 0;
						for (int k = j + pile.getHeight(); k >= 0; k--) {
							if (Integer.bitCount(myboard[k]) == board
									.getWidth()) {
								for (int x = k; x < myboard.length - 5; x++) {
									myboard[x] = myboard[x + 1];
								}
								cleared++;
							}
						}

						if (getScore(myboard, board, cleared) < score) {
							score = getScore(myboard, board, cleared);
							optimalColumn = c;
							optimalRow = j;
							optimalRotation = i;
//							System.out.println(cleared);
						}
						break;

					}
				}
			}
			pile = pile.rotate();
		}
		// now we know optimal rotation number and destination of pile, so we
		// construct
		// a path of the pile
		// pile isn't gonna intersect occupied spots while moving
		// System.out.print(optimalColumn + " " + optimalRotation + " " +
		// optimalRow);

		Turn turn = new Turn();
		for (int i = 0; i < optimalRotation; i++) {
			turn.commands.add(Command.ROTATION);
		}
		for (int i = 0; i < optimalColumn; i++) {
			turn.commands.add(Command.RIGHT);
		}
		for (int i = board.getHeight() - 5; i > optimalRow; i--) {
			turn.commands.add(Command.DOWN);
		}
		return turn;
	}

	// heuristic estimate of the board
	// http://luckytoilet.wordpress.com/2011/05/27/coding-a-tetris-ai-using-a-genetic-algorithm/
	private double getScore(int[] boardBites, BitRectangle board2, int cleared) {
		int maxHeight = TetrisUtils.top(boardBites);
		int filledBlocks = TetrisUtils.area(boardBites);
		double avHeight = 0;
		int[] allHeights = new int[board2.getWidth()];
		// computing average height of the board
		for (int i = 0; i < allHeights.length; i++) {
			allHeights[i] = 0;
			for (int j = maxHeight + 1; j >= 0; j--) {
				if ((boardBites[j] & 1 << i) != 0) {
					allHeights[i] = j + 1;
				}
				avHeight += allHeights[i];
			}
		}
		avHeight /= allHeights.length;
		// computing numholes
		int numHoles = 0;
		for (int i = 0; i < allHeights.length; i++) {
			for (int j = allHeights[i]; j >= 0; j--) {
				if ((boardBites[j] & 1 << i) == 0) {
					numHoles++;
				}
			}
		}
		return avHeight + 20 * numHoles + 10 * maxHeight + filledBlocks - 30
				* cleared - getWallTouchingEdges(boardBites, board2);
	}

	// counting edges which touches walls or floor
	private int getWallTouchingEdges(int[] boardBites, BitRectangle board2) {
		int result = 0;
		for (int i = 0; i < boardBites.length; i++) {
			if ((boardBites[i] & 1 << board2.getWidth()) == 1
					|| (boardBites[i] & 1 >> board2.getWidth()) == 1)
				result++;
		}
		result += Integer.bitCount(boardBites[0]);
		return result;
	}
}