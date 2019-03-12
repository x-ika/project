package tetris.students.gqoch;

import static tetris.logic.TetrisUtils.xor;
import tetris.logic.BitRectangle;
import tetris.logic.Command;
import tetris.logic.Pile;
import tetris.logic.Player;
import tetris.logic.TetrisUtils;
import tetris.logic.Turn;

public class GkochPlayer implements Player {

	public class BestPile {
		int row = 0;
		int column = 0;
		int score = Integer.MAX_VALUE;
		int rotations = 0;
	}
	
	public Turn play(BitRectangle board, Pile pile) {
		if (pile == null || pile.filledArea() < 4) {
			return new Turn();
		}

		int[] grid = board.getBitsetPrepresentation();

		BestPile bestPile = new BestPile();
		/*
		 * simulate game for all possible pile configuration
		 * get smallest score and return right turn
		 */
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < board.getWidth() - pile.getWidth() + 1; j++) {
				int[] currGrid = grid.clone();
				int row = putPile(currGrid, board.getWidth(), j, pile);
				int score = getScore(currGrid, board.getWidth());
				if (score < bestPile.score) {
					bestPile.score = score;
					bestPile.rotations = i;
					bestPile.column = j;
					bestPile.row = row;
				}
			}
			pile = pile.rotate();
		}
		return getTurn(bestPile, board);
	}

	private Turn getTurn(BestPile bestPile, BitRectangle board) {
		Turn turn = new Turn();
		for (int i = 0; i < bestPile.rotations; i++)
			turn.add(Command.ROTATION);
		for (int i = 0; i < bestPile.column; i++)
			turn.add(Command.RIGHT);
		for (int i = board.getHeight() - 5; i > bestPile.row; i--)
			turn.add(Command.DOWN);
		return turn;
	}

	/*
	 * calculate maximum height and also store 
	 * all heights in array
	 */
	private int getMaxHeight(int[] currGrid){
		return TetrisUtils.top(currGrid);
	}
	
	private void fillHeights(int[] currGrid,int width, int[] heights){
		for (int j = 0; j < width; j++) {
			for (int i = currGrid.length - 1; i >= 0; i--) {
				if ((currGrid[i] & (1 << j)) != 0) {
					heights[j] = i+1;
					break;
				}
			}
		}
	}
	
	/*
	 * compute quantity full Cells 
	 */
	private int getFullCells(int[] currGrid) {
		int fullCells = 0;
		for (int i = 0; i < currGrid.length; i++) {
			fullCells += Integer.bitCount(currGrid[i]);
		}
		return fullCells;
	} 
	
	/*
	 * get score for particular board
	 * using heuristic, i was inspired from an article
	 * http://totologic.blogspot.com/2013/03/tetris-ai-explained.html
	 * it was very well explained why we should take this features
	 */
	private int getScore(int[] currGrid, int width) {
		int[] heights = new int[width];
		for (int i = 0; i < width; i++) {
			heights[i] = 0;
		}
		int maxHeight = getMaxHeight(currGrid);
		fillHeights(currGrid, width, heights);
		int fullCells = getFullCells(currGrid);
		
		/*
		 * compute holes and also sum
		 * height of all full blocks
		 */
		int clearCells = 0,weight = 0;
		for (int i = 0; i < width; i++) {
			for (int j = heights[i]; j>= 0; j--) {
				if ((currGrid[j] & (1 << i)) == 0) {
					clearCells++;
				}else{
					weight += j;
				}
			}
		}
		
		int highestSlope = 0,sumOfSlopes = 0;
		/*
		 * count max slope and also sum all slopes
		 */
		for (int i = 0; i < width - 1; i++) {
			int diff = Math.abs(heights[i] - heights[i + 1]);
			sumOfSlopes += diff;
			if (highestSlope < diff) {
				highestSlope = diff;
			}
		}
		
		return 10*clearCells + maxHeight + 2*fullCells + sumOfSlopes + highestSlope + (int)(0.5*weight);
	}

	/*
	 * find out where i can put pile
	 * then put it and return value of height
	 * where it was put
	 */
	private int putPile(int[] currGrid, int width, int col, Pile pile) {
		for (int row = pile.row; row >= 0; row--) {
			if (TetrisUtils.checkContact(currGrid,pile.getBitsetPrepresentation(), row, col)) {
				xor(currGrid, pile.getBitsetPrepresentation(), row, col);
				for (int i = 0; i < currGrid.length; i++) {
					if (Integer.bitCount(currGrid[i]) == width) {
						System.arraycopy(currGrid, i + 1, currGrid, i,currGrid.length - i - 1);
						currGrid[currGrid.length-1] = 0;
						i--;
					}
				}
				return row;
			}
		}
		return 0;
	}
}
