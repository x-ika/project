package player.students.laki;
import main.*;
import java.util.*;

public class PlayerByLashaLakirbaia implements Player {
    public void gameOver() {}
	
	public Turn makeTurne(int[][] d, int value) {
		initialBoard = new Board(d, value);
	//	int best = findBest(initialBoard, MAX_DEPTH, true, value);
		int best = findBestAlfaBeta(initialBoard, MAX_DEPTH, true, value, -100000001, 100000001);
//		System.out.println("found best = "+best);
		ArrayList<Board> moves = initialBoard.getAllBoardsAfterSingleMove();
		for(int i=0; i<moves.size(); i++){
			Board afterMove = moves.get(i);
//			if(afterMove.getValue()==initialBoard.getValue()) System.out.println("fu mogityan");
		//	if(findBest(afterMove, MAX_DEPTH-1, false, value)==best)
			if(findBestAlfaBeta(afterMove, MAX_DEPTH-1, false, value, -100000001, 100000001)==best)
				return Board.reconstructTurn(initialBoard, afterMove);
		}
//		System.out.println("returning null");
		return null;
	}
	
	static int findBest(Board b, int depth, boolean max, int initValue){
		if(depth==0) return b.score(initValue);
		ArrayList<Board> allMoves = b.getAllBoardsAfterSingleMove();
		int best = max? Board.VERY_BAD: Board.VERY_GOOD;
		for(int i=0; i<allMoves.size(); i++){
			Board cur = allMoves.get(i);
			int bestFor = findBest(cur, depth-1, !max, initValue);
			best = (max? Math.max(best, bestFor): Math.min(best, bestFor));
		}
		return best;
	}
	
	static int findBestAlfaBeta(Board b, int dep, boolean max, int initValue, int alfa, int beta){
		if(dep==0) return b.score(initValue);
		ArrayList<Board> allMoves = b.getAllBoardsAfterSingleMove();
		if(max){
			for(int i=0; i<allMoves.size(); i++){
				alfa = Math.max(alfa, findBestAlfaBeta(allMoves.get(i), dep-1, !max, initValue, alfa, beta));
				if(beta<=alfa) break;
			}
			return alfa;
		}
		else{
			for(int i=0; i<allMoves.size(); i++){
				beta = Math.min(beta, findBestAlfaBeta(allMoves.get(i), dep-1, !max, initValue, alfa, beta));
				if(beta<=alfa) break;
			}
			return beta;
		}
	}

	static final int MAX_DEPTH = 10;
	
	static Board initialBoard;
	
	static final int EMPTY = 0, WHITE = 1, BLACK = 2, WHITE_QUEEN = 4, BLACK_QUEEN = 5;
	
}
