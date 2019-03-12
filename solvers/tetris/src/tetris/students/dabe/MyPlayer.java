package tetris.students.dabe;


import static tetris.logic.TetrisUtils.*;

import tetris.logic.*;

public class MyPlayer implements Player {
	private double score;
	private int allSlopes;
	private BitRectangle board;

	public Turn play(BitRectangle board, Pile pile) {
		this.board = board;
		score = Integer.MAX_VALUE;
		int optimalRow = 0, optimalColumn = 0, optimalRotation = 0;
		if (pile == null || pile.filledArea() < 4) {
			return new Turn();
		}
		for (int i = 0; i < 4; i++) {
			// puts rotated pile to all possible locations
			for (int c = 0; c < board.getWidth() - pile.getWidth() + 1; c++) {
				int[] myboard = board.getBitsetPrepresentation();
				for (int j = pile.row; j >= 0; j--) {
					if(!TetrisUtils.intersecs(myboard, pile.getBitsetPrepresentation(), j, c)){
						if (TetrisUtils.checkContact(myboard, pile.getBitsetPrepresentation(), j, c)) {
//							System.out.println(j + "  " +c);
//							System.out.println(TetrisUtils.toString(board.getBitsetPrepresentation()));
							xor(myboard, pile.getBitsetPrepresentation(), j, c);
							int cleared = 0;
							for (int k = 0; k < myboard.length ; k++) {
								if (Integer.bitCount(myboard[k]) == board.getWidth()) {
									System.arraycopy(myboard, k+1, myboard, k, myboard.length-k-1);
									cleared++;
								}
							}
							if (getScore(myboard, cleared) < score) {
								score = getScore(myboard, cleared);
								optimalColumn = c;
								optimalRow = j;
								optimalRotation = i;
								//System.out.println(cleared);
							}
						}
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
	private double getScore(int[] boardBites, int cleared) {
		int maxHeight = TetrisUtils.top(boardBites);
		int filledBlocks = TetrisUtils.area(boardBites);
		double avgHeight = 0;
		int[] heights = getHeights(boardBites);
		for(int i=0;i<heights.length;i++)
			avgHeight+=heights[i];
		avgHeight /= heights.length;
		// computing numholes
		int numHoles = 0;
		for (int i = 0; i < heights.length; i++) {
			for (int j = heights[i]; j >= 0; j--) {
				if ((boardBites[j] & 1 << i) == 0) {
					numHoles++;
				}
			}
		}
		int maxSlope = getMaxSlope(boardBites);
		int sumOfFullBlocks = 0;
		for (int i = 0; i < board.getWidth(); i++) {
			for (int j = 0; j< board.getHeight(); j++) {
				if ((boardBites[j] & (1 << i)) != 0)
					sumOfFullBlocks += j;
			}
		}
		return 10*numHoles + maxHeight + avgHeight + 2*filledBlocks + maxSlope + allSlopes + sumOfFullBlocks - 20 * cleared;
	}

	// computing maximum height difference between neighbor columns
	private int getMaxSlope(int[] board1) {
		int res = 0;
		allSlopes = 0;
		int[] heights = getHeights(board1);
		/*for (int i = 0; i < heights.length; i++) {
			System.out.print(heights[i]+" ");
		}
		System.out.println(" heights ");*/
		for (int i = 0; i < board.getWidth() - 1; i++) {
			allSlopes += Math.abs(heights[i] - heights[i + 1]);
			if (Math.abs(heights[i] - heights[i + 1]) > res)
				res = Math.abs(heights[i] - heights[i + 1]);
		}
		return res;
	}

	// saving maximum heights of columns
	private int[] getHeights(int[] board2) {
		int[] heights = new int[board.getWidth()];
		for (int j = 0 ; j < board.getWidth(); j++){
			for (int i = board2.length-1; i >= 0; i--) {
				if ((board2[i] & (1 << j)) != 0){
					heights[j] = i + 1 ;
					break;
				}
			}
		}
		return heights;
	}
}