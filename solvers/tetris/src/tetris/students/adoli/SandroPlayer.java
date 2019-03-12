package tetris.students.adoli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tetris.logic.BitRectangle;
import tetris.logic.Command;
import tetris.logic.Pile;
import tetris.logic.Player;
import tetris.logic.TetrisUtils;
import tetris.logic.Turn;

public class SandroPlayer implements Player {

	/**
	 * same as Turn class, makes some things easier
	 */
	private class Move {
		public int rotation;
		public int right;
		public int down;
		
		public Move(int rotation, int right, int down) {
			this.rotation = rotation;
			this.right = right;
			this.down = down;
		}
		
		public Turn toTurn() {
			Turn turn = new Turn();
			
			for (int i=0; i<rotation; i++) {
				turn.add(Command.ROTATION);
			}
			
			for (int i=0; i<right; i++) {
				turn.add(Command.RIGHT);
			}
			
			for (int i=0; i<down; i++) {
				turn.add(Command.DOWN);
			}
			
			return turn;
		}
	}
	
	
	public Turn play(BitRectangle board, Pile pile) { 
		double bestScore = Double.MAX_VALUE;
		Move bestMove = new Move(0, 0, 0);
		
		for (Move move: getPossibleMoves(board, pile)) {
			double score = getScoreOfBoardByHeuristic(applyPileOnBoardWithTurn(board, pile, move));
			
			if (score < bestScore) {
				bestScore = score;
				bestMove = move;
			}
		}
		
		return bestMove.toTurn();
	}
	
	/**
	 * returns what legitimate moves are possible for given board and a pile.
	 * may return logically duplicate turns
	 */
	public List<Move> getPossibleMoves(BitRectangle board, Pile givenPile) {
		int row = board.getHeight() - 5;
		
		List<Move> moves = new ArrayList<>();

		List<Pile> rotations = Arrays.asList(
			givenPile,
			givenPile.rotate(),
			givenPile.rotate().rotate(),
			givenPile.rotate().rotate().rotate()
		);
		
		for (int rot = 0; rot<4; rot++) {
			Pile pile = rotations.get(rot);
			
			if (row + pile.getHeight() >= board.getHeight()) {
				continue;
			}
			
			for (int col=0; col <= board.getWidth() - pile.getWidth(); col++) {
				if (TetrisUtils.intersecs(board.getBitsetPrepresentation(), pile.getBitsetPrepresentation(), row, col)) {
					break;
				}
				
				int down = 0;
				
				while (!TetrisUtils.checkContact(board.getBitsetPrepresentation(), pile.getBitsetPrepresentation(), row-down, col)) {
					down++;
				}
				
				moves.add(new Move(rot, col, down));			
			}
		}
		
		return moves;
	}
	
	/**
	 * put pile on a board using given move, returns a new board (deep copy)
	 * also if certain rows are full, delete them
	 */
	public BitRectangle applyPileOnBoardWithTurn(BitRectangle board, Pile pile, Move move) {
		for (int i=0; i<move.rotation; i++) {
			pile = pile.rotate();
		}
		
		int row = board.getHeight() - 5 - move.down;
		int col = move.right;
		
		// borrowed from StaticPort :)
		int[] newBoard = board.getBitsetPrepresentation();
		TetrisUtils.xor(newBoard, pile.getBitsetPrepresentation(), row, col);
		
        for (int i = 0; i < newBoard.length; i++) {
            if (newBoard[i] == (1 << board.getWidth()) - 1) {
                System.arraycopy(newBoard, i + 1, newBoard, i, newBoard.length - i - 1);
                i--;
            }
        }

        return new BitRectangle(board.getWidth(), newBoard);
	}
	
	/**
	 * for a given board returns how good the given board it
	 *  smaller number is better
	 * ------------------------
	 * the score is computed using algorithm from this site:
	 * http://totologic.blogspot.com/2013/03/tetris-ai-explained.html
	 * Vato Maskhulia gave me this address :)
	 */
	public double getScoreOfBoardByHeuristic(BitRectangle board) {		
		return
			20*getSumOfEmptyHoles(board) +
			getSumOfSlopes(board) +
			getMaxSlope(board) + 
			board.filledArea() +
			TetrisUtils.top(board.getBitsetPrepresentation());
	}
	
	/**
	 * return how many empty holes are in a board
	 */
	public int getSumOfEmptyHoles(BitRectangle board) {
		int sum = 0;
		int[] bits = board.getBitsetPrepresentation();
		
		for (int i=0; i<board.getWidth(); i++) {
			for (int j=0; j<getHeightOfColumnOnBoard(board, i); j++) {
				if ((bits[j] & (1 << i)) == 0) {
					sum++;
				}
			}
		}
		
		return sum;
	}
	
	/**
	 * returns sum of all slopes
	 */
	public int getSumOfSlopes(BitRectangle board) {
		int slope = 0;

		for (int i=0; i<board.getWidth()-1; i++) {
			int diff = getHeightOfColumnOnBoard(board, i) - getHeightOfColumnOnBoard(board, i+1);
			
			if (diff < 0) {
				diff *= -1;
			}
			
			slope += diff;
		}
		
		return slope;
	}
	
	/**
	 * returns maximum slope in a board
	 */
	public int getMaxSlope(BitRectangle board) {
		int maxSlope = 0;

		for (int i=0; i<board.getWidth()-1; i++) {
			int cur = getHeightOfColumnOnBoard(board, i) - getHeightOfColumnOnBoard(board, i+1);
			
			if (cur < 0) {
				cur *= -1;
			}
			
			if (maxSlope < cur)
				cur = maxSlope;
		}
		
		return maxSlope;
	}
	
	/**
	 * returns height of given column on a board;
	 */
	public int getHeightOfColumnOnBoard(BitRectangle board, int col) {
		int[] bits = board.getBitsetPrepresentation();
		
		for (int i=board.getHeight()-5; i>=0; i--) {
			if ((bits[i] & 1 << col) > 0) {
				return i;
			}
		}
		
		return 0;
	}
}
