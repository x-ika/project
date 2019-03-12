package player.students.ujmajuridze;

import main.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements AI Checkers Player using MiniMax Algorithm with Alpha-Beta.
 *
 * @author Kote.
 *         Created Nov 18, 2012.
 */
public class ComputerPlayer implements Player {
    public void gameOver() {}

	private static final int DEPTH = 8;
	private static int color; // if 1 - white. if 2- black
	private static Turn bestTurn;
	static int[][] array = new int[][]{{-1, -1, 1, 1}, {-1, 1, 1, -1}};
	
	public Turn makeTurne(int[][] d, int value) {
		color = value;
		boolean max;
		if(value == 1)max =true;
		else max = false;
//		System.out.println(alphabeta(d, DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, max));
		return bestTurn;
	}
	
	private static int alphabeta(int[][] board, int depth, int alpha, int beta, boolean max) {
		if(depth == 0 || gameIsOver(board))return heuristic(board);
		int [][] childBoard;
		if(max){
			List<Turn> turns = allTurnsOfNode(board);
			for(int i = 0; i < turns.size(); i++){
				childBoard = copyArray(board);
				makeMoveOnBoard(childBoard, turns, i);
				/*for(int a = 0 ; a<childBoard.length; a++){
					for(int b = 0; b<childBoard.length; b++){
						System.out.print(childBoard[a][b]);
					}
					System.out.println();
				}
				System.out.println();*/
				if(color == 1)color = 2;
				else color = 1;
				int k = alphabeta(childBoard, depth-1, alpha, beta, !max);
				if(k >= alpha){
					if(depth == DEPTH){
						bestTurn = turns.get(i);
					}
					alpha = k;
				}
				if(beta<=alpha)break;
			}
			//System.out.println("alpha: " + alpha);
			return alpha;
		} else {
			List<Turn> turns = allTurnsOfNode(board);
			for(int i = 0; i < turns.size(); i++){
				childBoard = copyArray(board);
				makeMoveOnBoard(childBoard, turns, i);
				if(color == 1)color =2;
				else color = 1;
				int k = alphabeta(childBoard, depth-1, alpha, beta, !max);
				if(k < beta){
					if(depth == DEPTH){
						bestTurn = turns.get(i);
					}
					beta = k;
				}
				//beta = min(beta, );
				if(beta<=alpha)break;
			}
			//System.out.println("beta: " + beta);
			return beta;
		}
	}

	private static void makeMoveOnBoard(int[][] childBoard, List<Turn> turns, int a) {
		Turn curr = turns.get(a);
		List<? extends Checker> kill = curr.killed;
		for(int i = 0; i<kill.size(); i++){
			Checker killed = kill.get(i);
			childBoard[killed.row][killed.col] = 0;
		}
		childBoard[curr.first.row][curr.first.col] = 0;
		if((curr.last.row == 7  && color == 1)|| (curr.last.row == 0 && color == 2)){
			childBoard[curr.last.row][curr.last.col] = color + 3;
			
		} else {
			childBoard[curr.last.row][curr.last.col] = curr.first.value;
		}
	}


	/**
	 */
	private static int[][] copyArray(int[][] board) {
		int[][] clMat = new int[board.length][board.length];
		for(int i = 0; i < board.length; i++){
			clMat[i] = board[i].clone();
		}
		return clMat;
	}

	private static List<Turn> allTurnsOfNode(int[][] board) {
		int[][]cloneBoard;
		List<Turn> allTurns = new ArrayList<>();
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board.length; j++){
				if(board[i][j] == color){
					cloneBoard = copyArray(board);
					Checker first = new Checker(i, j, cloneBoard[i][j]);
					List<Turn> list = checkSimpleKills(i, j, cloneBoard, new ArrayList<>(), first, new ArrayList<>());
					for(int k = 0; k < list.size(); k++){
						Turn t = list.get(k);
						if(t.killed.size() != 0)allTurns.add(t);
					}
					
				} else if(board[i][j] == color + 3){
					cloneBoard = copyArray(board);
					Checker first = new Checker(i, j, cloneBoard[i][j]);
					List<Turn> list2 = checkKingKills(i, j, cloneBoard, new ArrayList<>(), first, new ArrayList<>());
					for(int k = 0; k < list2.size(); k++){
						Turn t = list2.get(k);
						if(t.killed.size() != 0)allTurns.add(t);
					}
					
				}
			}
		}
		if(allTurns.size() > 0){
			return allTurns;
		} else {
			for(int i = 0; i < board.length; i++){
				for(int j = 0; j < board.length; j++){
					if(board[i][j] == color){
						cloneBoard = copyArray(board);
						List<Turn> simpleMoves = checkSimpleMoves(i, j, cloneBoard);
						for(int m = 0; m < simpleMoves.size(); m++){
							Turn t = simpleMoves.get(m);
							allTurns.add(t);
						}
						
					} else if(board[i][j] == color + 3){
						cloneBoard = copyArray(board);
						List<Turn> simpleKingMoves = checkSimpleKingMoves(i, j, cloneBoard);
						for(int m = 0; m < simpleKingMoves.size(); m++){
							Turn t = simpleKingMoves.get(m);
							allTurns.add(t);
						}
					}
				}
			}
		}
		return allTurns;
	}

	private static List<Turn> checkSimpleKingMoves(int x, int y, int[][] board) {
		List<Turn> kingTurns = new ArrayList<>();
		Checker f = new Checker(x, y, board[x][y]);
		for(int a = 0; a < 4; a++){
			for(int j = y + array[1][a], i = x + array[0][a]; j < board.length && j>=0 && i >= 0 && i < board.length; j+=array[1][a], i+=array[0][a]){
				if(board[i][j] != 0)break;
				Checker l = new Checker(i, j, 0);
				Turn t = new Turn(f, l, new ArrayList<>());
				kingTurns.add(t);
				
			}
		}
		return kingTurns;
	}

	private static List<Turn> checkKingKills(int x, int y, int[][] board, ArrayList<Checker> killed, Checker first, ArrayList<Turn> turns) {
		boolean f = false;
		if(x == 2 && y == 4)f = true;
		boolean k = false;
		boolean hasKills = true;
		for(int a = 0; a < 4; a++){
			// icvleba mimartuleba
			int m = array[0][a];
			int n = array[1][a];
			for(int j = y + n, i = x + m; j < board.length && j>=0 && i >= 0 && i < board.length; j+=n, i+=m){
				// mivdivart romeligac mimartulebit, shegvxvda chveniani da gamovdivart
				if(board[i][j] == color || board[i][j] == color + 3)break; 
				// shegvxvda mteri da amavedros mis win ujra sazgvrebshia mashin..
				else if(isEnemy(i, j, board) && inBorders(i+m, j+n)){
					// am ujridan 4ive mimartulebas vixilavt
					for(int b = i+m, c=j+n; inBorders(b, c); b+=m, c+=n){
						if(board[b][c] == 0 && hasDiagonalKill(b,c, board)){
							Checker killedChecker = new Checker(i, j, board[i][j]);
							killed.add(killedChecker);
							board[i][j] = 0; // ??
							board[x][y] = 0;
							k = true;
							//if(hasDiagonalKill(b, c, board))need=true;
							checkKingKills(b, c, board, killed, first, turns);/*// 	 0 1 2 3 4 5 6 7  
																					{0,0,0,0,0,0,0,0},//0
																					{0,0,0,0,0,0,0,0},//1
																					{0,0,0,0,0,0,0,0},//2
																					{0,0,0,0,0,0,0,0},//3
																					{0,0,2,0,0,0,0,0},//4
																					{0,0,0,0,0,2,0,0},//5
																					{4,0,0,0,0,0,0,0},//6
																					{0,0,0,0,0,0,0,0} //7*/
							
						} else {
							break;
						}
					}
					break;
				}
			}
		}
		if(!k){
			Checker last = new Checker(x, y, 0);
			List<Checker> cloneKilled = new ArrayList<>(killed);
			Turn t = new Turn(first, last, cloneKilled);
			if(killed.size() > 0)killed.remove(killed.size() - 1);
			turns.add(t);
			//if(f)System.out.println("aqvar1");
		} 
		
		return turns;
	}

	private static boolean hasDiagonalKill(int x, int y, int[][] board) {
		for(int a = 0; a < 4; a++){
			int m = array[0][a];
			int n = array[1][a];
			for(int j = y + n, i = x + m; j < board.length && j>=0 && i >= 0 && i < board.length; j+=n, i+=m){
				if(board[i][j] == color || board[i][j] == color + 3)break;
				if(isEnemy(i, j, board)){
					if(inBorders(i+m, j+n) && board[i+m][j+n] == 0){
				
						return true;
					} else{
						return false;
					}
				}
			}
		}
		return false;
	}

	/**
	 */
	private static List<Turn> checkSimpleMoves(int i, int j, int[][] board) {
		List<Turn> simpleTurns = new ArrayList<>();
		Checker f = new Checker(i, j, board[i][j]);
		if(color == 1){
			if(inBorders(i-1, j+1) && board[i-1][j+1] == 0){
				Checker l = new Checker(i-1, j+1, 0);
				Turn t = new Turn(f, l, new ArrayList<>());
				simpleTurns.add(t);
			}
			if(inBorders(i-1, j-1) && board[i-1][j-1] == 0){
				Checker l = new Checker(i-1, j-1, 0);
				Turn t = new Turn(f, l, new ArrayList<>());
				simpleTurns.add(t);
			}
		} else if(color == 2){
			if(inBorders(i+1, j+1) && board[i+1][j+1] == 0){
				Checker l = new Checker(i+1, j+1, 0);
				Turn t = new Turn(f, l, new ArrayList<>());
				simpleTurns.add(t);
			}
			if(inBorders(i+1, j-1) && board[i+1][j-1] == 0){
				Checker l = new Checker(i+1, j-1, 0);
				Turn t = new Turn(f, l, new ArrayList<>());
				simpleTurns.add(t);
			}
		}
		return simpleTurns;
	}

	/**
	 * @param turns 
	 */
	
	private static ArrayList<Turn> checkSimpleKills(int i, int j, int [][] board, List<Checker> killed, Checker first, ArrayList<Turn> turns) {
		
		boolean k = false;
		for(int x = 0; x < 4; x++){
			int m = array[0][x];
			int n = array[1][x];
			if(inBorders(i+m+m, j+n+n) && isEnemy(i+m, j+n, board) && board[i+m+m][j+n+n] == 0){
				Checker killedChecker = new Checker(i+m, j+n, board[i+m][j+n]);
				killed.add(killedChecker);
				board[i+m][j+n] = 0;
				board[i][j] = 0;
				k = true;
				checkSimpleKills(i+m+m, j+n+n, board, killed, first, turns);
			}
		}
		if(!k){
			Checker last = new Checker(i, j, 0);
			List<Checker> cloneKilled = new ArrayList<>(killed);
			Turn t = new Turn(first, last, cloneKilled);
			if(killed.size() > 0)killed.remove(killed.size() - 1);
			turns.add(t);
			
		}
		return turns;	
	}

	/**
	 */
	private static boolean isEnemy(int i, int j, int[][] board) {
		if(board[i][j] != color  && board[i][j] != color + 3 && board[i][j] != 0)return true;
		return false;
	}

	/**

	 */
	private static boolean inBorders(int i, int j) {
		if(i >= 8 || j >= 8)return false;
		if(i < 0 || j < 0)return false;
		return true;
	}

	/**
	 * 
	 */
	private static int heuristic(int[][] board) {
		int we = 0;
		int enemy = 0;
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board.length; j++){
				if(board[i][j] == color)we++;
				else if(board[i][j] == color + 3)we+=2;
				else if(board[i][j] != 0 && board[i][j] < 4)enemy++;
				else if(board[i][j] != 0)enemy+=2;
			}
		}
		return we-enemy;
	}

	/**
	 */
	private static boolean gameIsOver(int[][] board) {
		boolean existsWhite = false;
		boolean existsBlack = false;
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board.length; j++){
				if((board[i][j] == 1) || board[i][j] == 4)existsWhite = true;
				else if((board[i][j] == 2) || board[i][j] == 5)existsBlack = true;
				if(existsWhite && existsBlack)return false;
			}
		}
		return true;
	}
		
	public static void main(String[] args){
		color = 1;
		int [][] matrix = new int[][]{
								// 	 0 1 2 3 4 5 6 7  
									{0,0,0,0,0,0,0,0},//0
									{0,0,0,0,0,0,0,0},//1
									{0,0,2,0,0,0,2,0},//2
									{0,0,0,0,0,0,0,0},//3
									{0,0,2,0,0,0,2,0},//4
									{0,0,0,0,0,2,0,0},//5
									{4,0,0,0,0,0,0,0},//6
									{0,0,0,0,0,0,0,0} //7
		};
		
		//System.out.println(hasDiagonalKill(5, 3, matrix));
		Checker first = new Checker(6, 0, matrix[6][0]);
		List<Turn> list = checkKingKills(6, 0, matrix, new ArrayList<>(), first , new ArrayList<>());
		for(int i = 0; i < list.size(); i++){
			Turn c = list.get(i);
			Checker f = c.first;
			Checker l = c.last;
			List<? extends Checker>killed = c.killed;
//			System.out.println("Moved From x: " + f.row + " y: " + f.col + " value: " + f.value);
//			System.out.println("Moved To x: " + l.row + " y: " + l.col + " value: " + l.value);
//			System.out.println("Killed: ");
			for(int j = 0; j < killed.size(); j++){
				Checker k = killed.get(j);
//				System.out.println(j+1 + ". x: " + k.row + " y: " + k.col + " value: " + k.value);
			}
//			System.out.println();
			
		}
		/*int h = alphabeta(matrix, DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
		System.out.println(h);
		System.out.println("Best Turn: First: x : " + bestTurn.first.x + " y: " + bestTurn.first.y);
		System.out.println("Last: x : " + bestTurn.last.x + " y: " + bestTurn.last.y);
		for(int j = 0; j < bestTurn.killed.size(); j++){
			Checker k = bestTurn.killed.get(j);
			System.out.println(j+1 + ". x: " + k.x + " y: " + k.y + " value: " + k.num);
		}*/
		
		}
}
