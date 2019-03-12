package player.students.chakvetadze;

import main.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class MyPlayer implements Player {
    public void gameOver() {}

	private static int[][] d;
	private static int value;

	public static void main(String[] args) throws FileNotFoundException {
		int[][] d1 = new int[8][8];
		Scanner s = new Scanner(new FileReader("input.txt"));
		for (int i = 0; i < d1.length; i++) {
			for (int j = 0; j < d1.length; j++) {
				d1[i][j] = s.nextInt();
			}
		}
//		MyPlayer p = new MyPlayer(d1, 1);
//		System.out.println(p.makeTurne(d1, 1));
	}
	
	private static int boardScore(){
		int score = 0;
		for(int i=0; i<d.length; i++){
			for(int j=0; j<d.length;j++){
				switch (d[i][j]) {
				case 0:
					break;
				case 1:
					score++;
					break;
				case 2:
					score--;
					break;
				case 4:
					score +=3;
					break;
				case 5:
					score -= 3;
					break;
				default:
					break;
				}
			}
		}
//		System.out.println(score);
		return score;
	}

	public Turn makeTurne(int[][] d, int value) {
		this.d = d;
		this.value = value;
		int score =  minMax(6);
		ArrayList<Turn> moves = generateMoves(value);
		for (int i = moves.size()-1; i >= 0; i--) {
			applyMove(moves.get(i));
			if(boardScore() == score){
				undoMove(moves.get(i));
				return moves.get(i);
			}
			undoMove(moves.get(i));
		}
		return null;
	}

	private static int minMax(int depth) {
		return maxMove(depth);
	}

	private static int maxMove(int depth) {
//		System.out.println(tString());
		ArrayList<Turn> moves = generateMoves(value);
		if (moves.isEmpty() || depth == 0) {
			return boardScore();
		} else {
			int bestMove = Integer.MIN_VALUE;

			for (int i = moves.size()-1; i >= 0; i--) {
				applyMove(moves.get(i));
				int move = minMove(depth - 1);
				undoMove(moves.get(i));
				if (move > bestMove) {
					bestMove = move;
				}
			}
			return bestMove;
		}
	}

	private static int minMove(int depth) {
//		System.out.println(tString());
		ArrayList<Turn> moves = generateMoves((1 % value) + 1);
		if (moves.isEmpty() || depth == 0) {
			return boardScore();
		} else {
			int bestMove = Integer.MIN_VALUE;
			for (int i = moves.size()-1; i >= 0; i--) {
				applyMove(moves.get(i));
				int move = maxMove(depth - 1);
				undoMove(moves.get(i));
				if (move > bestMove) {
					bestMove = move;
				}
			}
			return bestMove;
		}
	}

	private static void applyMove(Turn turn) {
		d[turn.last.row][turn.last.col] = turn.last.value;
		d[turn.first.row][turn.first.col] = 0;
		for (int i = turn.killed.size() - 1; i >= 0; i--) {
			d[turn.killed.get(i).row][turn.killed.get(i).col] = 0;
		}
	}

	private static void undoMove(Turn turn) {
		d[turn.last.row][turn.last.col] = 0;
		d[turn.first.row][turn.first.col] = turn.first.value;
		for (int i = turn.killed.size() - 1; i >= 0; i--) {
			Checker c = turn.killed.get(i);
			d[c.row][c.col] = c.value;
		}
	}

	private static ArrayList<Turn> generateMoves(int value) {
		ArrayList<Turn> moves = new ArrayList<>();
		ArrayList<Turn> queenKillMoves = getQueensWhichCanKill(value + 3);
		ArrayList<Turn> checkerKillMoves = getCheckersWhickCanKill(value);

		if (queenKillMoves.size() == 0 && checkerKillMoves.size() == 0) {
			ArrayList<Turn> queenMoves = getQueensWhichCanMove(value + 3);
			ArrayList<Turn> checkerMoves = getCheckersWhickCanMove(value);
			if (queenMoves.size() != 0)
				moves.addAll(queenMoves);
			if (checkerMoves.size() != 0)
				moves.addAll(checkerMoves);
		} else {
			if (queenKillMoves.size() != 0)
				moves.addAll(queenKillMoves);
			if (checkerKillMoves.size() != 0)
				moves.addAll(checkerKillMoves);
		}
		return moves;
	}

	private static ArrayList<Turn> getQueensWhichCanKill(int value) {
		ArrayList<Turn> turns = new ArrayList<>();
		for (int i = 0; i < d.length; i++) {
			for (int j = 0; j < d.length; j++) {
				if (d[i][j] == value) {
					iFirst = i;
					jFirst = j;
					fillTurnesRecursivelyForQueen(i, j, value);
				}
			}
		}
		turns = copyOfArrayListTurnes();
		turnes.clear();
		return turns;
	}

	private static void fillTurnesRecursivelyForQueen(int i, int j, int value) {
		if (value == 4) {
			boolean kill = false;
			int tempI = i;
			int tempJ = j;
			while (ifInBounds(tempI + 1, tempJ + 1)
					&& d[tempI + 1][tempJ + 1] == 0) {
				tempI++;
				tempJ++;
			}
			if (ifInBounds(tempI + 2, tempJ + 2)
					&& (d[tempI + 1][tempJ + 1] == 2 || d[tempI + 1][tempJ + 1] == 5)
					&& d[tempI + 2][tempJ + 2] == 0) {
				kill = true;
				Checker c = new Checker(tempI + 1, tempJ + 1,
						d[tempI + 1][tempJ + 1]);
				killed.add(c);
				int undo = d[tempI - 1][tempJ + 1];
				applyTurnOnBoard(i, j, tempI + 2, tempJ + 2, tempI + 1,
						tempJ + 1);
				fillTurnesRecursivelyForQueen(tempI + 2, tempJ + 2, value);
				undoBoard(i, j, tempI + 2, tempJ + 2, tempI + 1, tempJ + 1,
						undo);
				int tempII = tempI + 2;
				int tempJJ = tempJ + 2;
				while (ifInBounds(tempII + 1, tempJJ + 1)
						&& d[tempII + 1][tempJJ + 1] == 0) {
					tempII++;
					tempJJ++;
					applyTurnOnBoard(i, j, tempII, tempJJ, tempI + 1,
							tempJ + 1);
					fillTurnesRecursivelyForQueen(tempII, tempJJ, value);
					undoBoard(i, j, tempII, tempJJ, tempI + 1,
							tempJ + 1, undo);
				}
				killed.remove(c);
			}
			tempI = i;
			tempJ = j;

			while (ifInBounds(tempI - 1, tempJ + 1)
					&& d[tempI - 1][tempJ + 1] == 0) {
				tempI--;
				tempJ++;
			}
			if (ifInBounds(tempI - 2, tempJ + 2)
					&& (d[tempI - 1][tempJ + 1] == 2 || d[tempI - 1][tempJ + 1] == 5)
					&& d[tempI - 2][tempJ + 2] == 0) {
				kill = true;
				Checker c = new Checker(tempI - 1, tempJ + 1, 2);
				killed.add(c);
				int undo = d[tempI - 1][tempJ + 1];
				applyTurnOnBoard(i, j, tempI - 2, tempJ + 2, tempI - 1,
						tempJ + 1);
				fillTurnesRecursivelyForQueen(tempI - 2, tempJ + 2, value);
				undoBoard(i, j, tempI - 2, tempJ + 2, tempI - 1, tempJ + 1,
						undo);
				int tempII = tempI - 2;
				int tempJJ = tempJ + 2;
				while (ifInBounds(tempII - 1, tempJJ + 1)
						&& d[tempII - 1][tempJJ + 1] == 0) {
					tempII--;
					tempJJ++;
					applyTurnOnBoard(i, j, tempII, tempJJ, tempI - 1,
							tempJ + 1);
					fillTurnesRecursivelyForQueen(tempII, tempJJ, value);
					undoBoard(i, j, tempII, tempJJ, tempI - 1,
							tempJ + 1, undo);
				}
				killed.remove(c);
			}
			tempI = i;
			tempJ = j;

			while (ifInBounds(tempI + 1, tempJ - 1)
					&& d[tempI + 1][tempJ - 1] == 0) {
				tempI++;
				tempJ--;
			}
			if (ifInBounds(tempI + 2, tempJ - 2)
					&& (d[tempI + 1][tempJ - 1] == 2 || d[tempI + 1][tempJ - 1] == 5)
					&& d[tempI + 2][tempJ - 2] == 0) {
				kill = true;
				Checker c = new Checker(tempI + 1, tempJ - 1, 2);
				int undo = d[tempI - 1][tempJ + 1];
				applyTurnOnBoard(i, j, tempI + 2, tempJ - 2, tempI + 1,
						tempJ - 1);
				killed.add(c);
				fillTurnesRecursivelyForQueen(tempI + 2, tempJ - 2, value);
				undoBoard(i, j, tempI + 2, tempJ - 2, tempI + 1, tempJ - 1,
						undo);
				int tempII = tempI + 2;
				int tempJJ = tempJ - 2;
				while (ifInBounds(tempII + 1, tempJJ - 1)
						&& d[tempII + 1][tempJJ - 1] == 0) {
					tempII++;
					tempJJ--;
					applyTurnOnBoard(i, j, tempII, tempJJ, tempI + 1,
							tempJ - 1);
					fillTurnesRecursivelyForQueen(tempII, tempJJ, value);
					undoBoard(i, j, tempII, tempJJ, tempI + 1,
							tempJ - 1, undo);
				}
				killed.remove(c);
			}
			tempI = i;
			tempJ = j;
			while (ifInBounds(tempI - 1, tempJ - 1)
					&& d[tempI - 1][tempJ - 1] == 0) {
				tempI--;
				tempJ--;
			}
			if (ifInBounds(tempI - 2, tempJ - 2)
					&& (d[tempI - 1][tempJ - 1] == 2 || d[tempI - 1][tempJ - 1] == 5)
					&& d[tempI - 2][tempJ - 2] == 0) {
				kill = true;
				Checker c = new Checker(tempI - 1, tempJ - 1, 2);
				killed.add(c);
				int undo = d[tempI - 1][tempJ + 1];
				applyTurnOnBoard(i, j, tempI - 2, tempJ - 2, tempI - 1,
						tempJ - 1);
				fillTurnesRecursivelyForQueen(tempI - 2, tempJ - 2, value);
				undoBoard(i, j, tempI - 2, tempJ - 2, tempI - 1, tempJ - 1,
						undo);
				int tempII = tempI - 2;
				int tempJJ = tempJ - 2;
				while (ifInBounds(tempII - 1, tempJJ - 1)
						&& d[tempII - 1][tempJJ - 1] == 0) {
					tempII--;
					tempJJ--;
					applyTurnOnBoard(i, j, tempII, tempJJ, tempI - 1,
							tempJ - 1);
					fillTurnesRecursivelyForQueen(tempII, tempJJ, value);
					undoBoard(i, j, tempII, tempJJ, tempI - 1,
							tempJ - 1, undo);
				}
				killed.remove(c);
			}
			if (!kill && (i != iFirst || j != jFirst)) {
				Checker before = new Checker(iFirst, jFirst, value);
				Checker after = new Checker(i, j, value);
				turnes.add(new Turn(before, after, copyOflinkedListKilled()));
			}
		} else {
			boolean kill = false;
			int tempI = i;
			int tempJ = j;
			while (ifInBounds(tempI + 1, tempJ + 1)
					&& d[tempI + 1][tempJ + 1] == 0) {
				tempI++;
				tempJ++;
			}
			if (ifInBounds(tempI + 2, tempJ + 2)
					&& (d[tempI + 1][tempJ + 1] == 1 || d[tempI + 1][tempJ + 1] == 4)
					&& d[tempI + 2][tempJ + 2] == 0) {
				kill = true;
				Checker c = new Checker(tempI + 1, tempJ + 1, 2);
				killed.add(c);
				int undo = d[tempI - 1][tempJ + 1];
				applyTurnOnBoard(i, j, tempI + 2, tempJ + 2, tempI + 1,
						tempJ + 1);
				fillTurnesRecursivelyForQueen(tempI + 2, tempJ + 2, value);
				undoBoard(i, j, tempI + 2, tempJ + 2, tempI + 1, tempJ + 1,
						undo);
				int tempII = tempI + 2;
				int tempJJ = tempJ + 2;
				while (ifInBounds(tempII + 1, tempJJ + 1)
						&& d[tempII + 1][tempJJ + 1] == 0) {
					tempII++;
					tempJJ++;
					applyTurnOnBoard(i, j, tempII, tempJJ, tempI + 1,
							tempJ + 1);
					fillTurnesRecursivelyForQueen(tempII, tempJJ, value);
					undoBoard(i, j, tempII, tempJJ, tempI + 1,
							tempJ + 1, undo);
				}
				killed.remove(c);
			}
			tempI = i;
			tempJ = j;
			while (ifInBounds(tempI - 1, tempJ + 1)
					&& d[tempI - 1][tempJ + 1] == 0) {
				tempI--;
				tempJ++;
			}
			if (ifInBounds(tempI - 2, tempJ + 2)
					&& (d[tempI - 1][tempJ + 1] == 1 || d[tempI - 1][tempJ + 1] == 4)
					&& d[tempI - 2][tempJ + 2] == 0) {
				kill = true;
				Checker c = new Checker(tempI - 1, tempJ + 1, 2);
				killed.add(c);
				int undo = d[tempI - 1][tempJ + 1];
				applyTurnOnBoard(i, j, tempI - 2, tempJ + 2, tempI - 1,
						tempJ + 1);
				fillTurnesRecursivelyForQueen(tempI - 2, tempJ + 2, value);
				undoBoard(i, j, tempJ - 2, tempJ + 2, tempI - 1, tempJ + 1,
						undo);
				int tempII = tempI - 2;
				int tempJJ = tempJ + 2;
				while (ifInBounds(tempII - 1, tempJJ + 1)
						&& d[tempII - 1][tempJJ + 1] == 0) {
					tempII--;
					tempJJ++;
					applyTurnOnBoard(i, j, tempII, tempJJ, tempI - 1,
							tempJ + 1);
					fillTurnesRecursivelyForQueen(tempII, tempJJ, value);
					undoBoard(i, j, tempII, tempJJ, tempI - 1,
							tempJ + 1, undo);
				}
				killed.remove(c);
			}
			tempI = i;
			tempJ = j;
			while (ifInBounds(tempI + 1, tempJ - 1)
					&& d[tempI + 1][tempJ - 1] == 0) {
				tempI++;
				tempJ--;
			}
			if (ifInBounds(tempI + 2, tempJ - 2)
					&& (d[tempI + 1][tempJ - 1] == 1 || d[tempI + 1][tempJ - 1] == 4)
					&& d[tempI + 2][tempJ - 2] == 0) {
				kill = true;
				Checker c = new Checker(tempI + 1, tempJ - 1, 2);
				killed.add(c);
				int undo = d[tempI - 1][tempJ + 1];
				applyTurnOnBoard(i, j, tempI + 2, tempJ - 2, tempI + 1,
						tempJ - 1);
				fillTurnesRecursively(tempI + 2, tempJ - 2, value);
				undoBoard(i, j, tempI + 2, tempJ - 2, tempI + 1, tempJ - 1,
						undo);
				int tempII = tempI + 2;
				int tempJJ = tempJ - 2;
				while (ifInBounds(tempII + 1, tempJJ - 1)
						&& d[tempII + 1][tempJJ - 1] == 0) {
					tempII++;
					tempJJ--;
					applyTurnOnBoard(i, j, tempII, tempJJ, tempI + 1,
							tempJ - 1);
					fillTurnesRecursively(tempII, tempJJ, value);
					undoBoard(i, j, tempII, tempJJ, tempI + 1,
							tempJ - 1, undo);
				}
				killed.remove(c);
			}
			tempI = i;
			tempJ = j;
			while (ifInBounds(tempI - 1, tempJ - 1)
					&& d[tempI - 1][tempJ - 1] == 0) {
				tempI--;
				tempJ--;
			}
			if (ifInBounds(tempI - 2, tempJ - 2)
					&& (d[tempI - 1][tempJ - 1] == 1 || d[tempI - 1][tempJ - 1] == 4)
					&& d[tempI - 2][tempJ - 2] == 0) {
				kill = true;
				Checker c = new Checker(tempI - 1, tempJ - 1, 2);
				killed.add(c);
				int undo = d[tempI - 1][tempJ + 1];
				applyTurnOnBoard(i, j, tempI - 2, tempJ - 2, tempI - 1,
						tempJ - 1);
				fillTurnesRecursively(tempI - 2, tempJ - 2, value);
				undoBoard(i, j, tempI - 2, tempJ - 2, tempI - 1, tempJ - 1,
						undo);
				int tempII = tempI - 2;
				int tempJJ = tempJ - 2;
				while (ifInBounds(tempII - 1, tempJJ - 1)
						&& d[tempII - 1][tempJJ - 1] == 0) {
					tempII--;
					tempJJ--;
					applyTurnOnBoard(i, j, tempII, tempJJ, tempI - 1,
							tempJ - 1);
					fillTurnesRecursively(tempII, tempJJ, value);
					undoBoard(i, j, tempII, tempJJ, tempI - 1,
							tempJ - 1, undo);
				}
				killed.remove(c);
			}
			if (!kill && (i != iFirst || j != jFirst)) {
				Checker before = new Checker(iFirst, jFirst, value);
				Checker after;
				after = new Checker(i, j, value);
				turnes.add(new Turn(before, after, copyOflinkedListKilled()));
			}

		}
	}

	private static ArrayList<Turn> getQueensWhichCanMove(int value) {
		ArrayList<Turn> turns = new ArrayList<>();
		if (value == 4) {
			for (int i = 0; i < d.length; i++) {
				for (int j = 0; j < d.length; j++) {
					if (d[i][j] == value) {
						int tempI = i;
						int tempJ = j;
						while (ifInBounds(tempI - 1, tempJ - 1)
								&& d[tempI - 1][tempJ - 1] == 0) {
							tempI--;
							tempJ--;
							if (tempI != i) {
								Checker beforeMove = new Checker(i, j, value);
								Checker afterMove = new Checker(tempI, tempJ,
										value);
								turns.add(new Turn(beforeMove, afterMove, null));
							}
						}
						tempI = i;
						tempJ = j;
						while (ifInBounds(tempI - 1, tempJ + 1)
								&& d[tempI - 1][tempJ + 1] == 0) {
							tempI--;
							tempJ++;
							if (tempI != i) {
								Checker beforeMove = new Checker(i, j, value);
								Checker afterMove = new Checker(i - 1, j + 1,
										value);
								turns.add(new Turn(beforeMove, afterMove, null));
							}
						}

						tempI = i;
						tempJ = j;
						while (ifInBounds(tempI + 1, tempJ - 1)
								&& d[tempI + 1][tempJ - 1] == 0) {
							tempI++;
							tempJ--;
							if (tempI != i) {
								Checker beforeMove = new Checker(i, j, value);
								Checker afterMove = new Checker(i + 1, j - 1,
										value);
								turns.add(new Turn(beforeMove, afterMove, null));
							}
						}

						tempI = i;
						tempJ = j;
						while (ifInBounds(tempI + 1, tempJ + 1)
								&& d[tempI + 1][tempJ + 1] == 0) {
							tempI++;
							tempJ++;
							if (tempI != i) {
								Checker beforeMove = new Checker(i, j, value);
								Checker afterMove = new Checker(i + 1, j + 1,
										value);
								turns.add(new Turn(beforeMove, afterMove, null));
							}
						}
					}
				}
			}
		}
		return turns;
	}

	static ArrayList<Checker> killed = new ArrayList<>();
	static ArrayList<Turn> turnes = new ArrayList<>();
	static int iFirst;
	static int jFirst;

	private static ArrayList<Turn> getCheckersWhickCanKill(int value) {
		ArrayList<Turn> turns = new ArrayList<>();
		for (int i = 0; i < d.length; i++) {
			for (int j = 0; j < d.length; j++) {
				if (d[i][j] == value) {
					iFirst = i;
					jFirst = j;
					fillTurnesRecursively(i, j, value);
				}
			}
		}
		turns = copyOfArrayListTurnes();
		turnes.clear();
		return turns;
	}

	private static void fillTurnesRecursively(int i, int j, int value) {
		if (value == 1) {
			if (i == 0) {
				if (i != iFirst || j != jFirst) {
					Checker before = new Checker(iFirst, jFirst, value);
					Checker after = new Checker(i, j, value + 3);
					turnes.add(new Turn(before, after, copyOflinkedListKilled()));
					return;
				}
			}
			boolean kill = false;
			if (ifInBounds(i + 2, j + 2)
					&& (d[i + 1][j + 1] == 2 || d[i + 1][j + 1] == 5)
					&& d[i + 2][j + 2] == 0) {
				kill = true;
				Checker c = new Checker(i + 1, j + 1, 2);
				killed.add(c);
				applyTurnOnBoard(i, j, i + 2, j + 2, i + 1, j + 1);
				fillTurnesRecursively(i + 2, j + 2, value);
				undoBoard(i, j, i + 2, j + 2, i + 1, j + 1, 2);
				killed.remove(c);
			}
			if (ifInBounds(i - 2, j + 2)
					&& (d[i - 1][j + 1] == 2 || d[i - 1][j + 1] == 5)
					&& d[i - 2][j + 2] == 0) {
				kill = true;
				Checker c = new Checker(i - 1, j + 1, 2);
				killed.add(c);
				applyTurnOnBoard(i, j, i - 2, j + 2, i - 1, j + 1);
				fillTurnesRecursively(i - 2, j + 2, value);
				undoBoard(i, j, i - 2, j + 2, i - 1, j + 1, 2);
				killed.remove(c);
			}
			if (ifInBounds(i + 2, j - 2)
					&& (d[i + 1][j - 1] == 2 || d[i + 1][j - 1] == 5)
					&& d[i + 2][j - 2] == 0) {
				kill = true;
				Checker c = new Checker(i + 1, j - 1, 2);
				applyTurnOnBoard(i, j, i + 2, j - 2, i + 1, j - 1);
				killed.add(c);
				fillTurnesRecursively(i + 2, j - 2, value);
				undoBoard(i, j, i + 2, j - 2, i + 1, j - 1, 2);
				killed.remove(c);
			}
			if (ifInBounds(i - 2, j - 2)
					&& (d[i - 1][j - 1] == 2 || d[i - 1][j - 1] == 5)
					&& d[i - 2][j - 2] == 0) {
				kill = true;
				Checker c = new Checker(i - 1, j - 1, 2);
				killed.add(c);
				applyTurnOnBoard(i, j, i - 2, j - 2, i - 1, j - 1);
				fillTurnesRecursively(i - 2, j - 2, value);
				undoBoard(i, j, i - 2, j - 2, i - 1, j - 1, 2);
				killed.remove(c);
			}
			if (!kill && (i != iFirst || j != jFirst)) {
				Checker before = new Checker(iFirst, jFirst, value);
				Checker after = new Checker(i, j, value);
				turnes.add(new Turn(before, after, copyOflinkedListKilled()));
			}
		} else {
			if (i == 7) {
				if (i != iFirst || j != jFirst) {
					Checker before = new Checker(iFirst, jFirst, value);
					Checker after = new Checker(i, j, value + 3);
					turnes.add(new Turn(before, after, copyOflinkedListKilled()));
					return;
				}
			}
			boolean kill = false;
			if (ifInBounds(i + 2, j + 2)
					&& (d[i + 1][j + 1] == 1 || d[i + 1][j + 1] == 4)
					&& d[i + 2][j + 2] == 0) {
				kill = true;
				Checker c = new Checker(i + 1, j + 1, 2);
				killed.add(c);
				applyTurnOnBoard(i, j, i + 2, j + 2, i + 1, j + 1);
				fillTurnesRecursively(i + 2, j + 2, value);
				undoBoard(i, j, i + 2, j + 2, i + 1, j + 1, 1);
				killed.remove(c);
			}
			if (ifInBounds(i - 2, j + 2)
					&& (d[i - 1][j + 1] == 1 || d[i - 1][j + 1] == 4)
					&& d[i - 2][j + 2] == 0) {
				kill = true;
				Checker c = new Checker(i - 1, j + 1, 2);
				killed.add(c);
				applyTurnOnBoard(i, j, i - 2, j + 2, i - 1, j + 1);
				fillTurnesRecursively(i - 2, j + 2, value);
				undoBoard(i, j, j - 2, j + 2, i - 1, j + 1, 1);
				killed.remove(c);
			}
			if (ifInBounds(i + 2, j - 2)
					&& (d[i + 1][j - 1] == 1 || d[i + 1][j - 1] == 4)
					&& d[i + 2][j - 2] == 0) {
				kill = true;
				Checker c = new Checker(i + 1, j - 1, 2);
				killed.add(c);
				applyTurnOnBoard(i, j, i + 2, j - 2, i + 1, j - 1);
				fillTurnesRecursively(i + 2, j - 2, value);
				undoBoard(i, j, i + 2, j - 2, i + 1, j - 1, 1);
				killed.remove(c);
			}
			if (ifInBounds(i - 2, j - 2)
					&& (d[i - 1][j - 1] == 1 || d[i - 1][j - 1] == 4)
					&& d[i - 2][j - 2] == 0) {
				kill = true;
				Checker c = new Checker(i - 1, j - 1, 2);
				killed.add(c);
				applyTurnOnBoard(i, j, i - 2, j - 2, i - 1, j - 1);
				fillTurnesRecursively(i - 2, j - 2, value);
				undoBoard(i, j, i - 2, j - 2, i - 1, j - 1, 1);
				killed.remove(c);
			}
			if (!kill && (i != iFirst || j != jFirst)) {
				Checker before = new Checker(iFirst, jFirst, value);
				Checker after;
				after = new Checker(i, j, value);
				turnes.add(new Turn(before, after, copyOflinkedListKilled()));
			}

		}
	}

	private static void undoBoard(int iAfter, int jAfter, int iBefore,
			int jBefore, int iKilled, int jKilled, int value) {
		d[iAfter][jAfter] = d[iBefore][jBefore];
		d[iBefore][jBefore] = 0;
		d[iKilled][jKilled] = value;
	}

	private static void applyTurnOnBoard(int iBefore, int jBefore, int iAfter,
			int jAfter, int iKilled, int jKilled) {
		d[iAfter][jAfter] = d[iBefore][jBefore];
		d[iBefore][jBefore] = 0;
		d[iKilled][jKilled] = 0;
	}

	private static ArrayList<Turn> copyOfArrayListTurnes() {
		ArrayList<Turn> l = new ArrayList<>();
		for (int i = turnes.size() - 1; i >= 0; i--) {
			l.add(turnes.get(i));
		}
		return l;
	}

	private static ArrayList<Checker> copyOflinkedListKilled() {
		ArrayList<Checker> l = new ArrayList<>();
		for (int i = killed.size() - 1; i >= 0; i--) {
			l.add(killed.get(i));
		}
		return l;
	}

	private static ArrayList<Turn> getCheckersWhickCanMove(int value) {
		ArrayList<Turn> turns = new ArrayList<>();
		if (value == 1) {
			for (int i = 0; i < d.length; i++) {
				for (int j = 0; j < d.length; j++) {
					if (d[i][j] == value) {
						if (ifInBounds(i - 1, j - 1) && d[i - 1][j - 1] == 0) {
							Checker beforeMove = new Checker(i, j, value);
							Checker afterMove;
							if (i == 0) {
								afterMove = new Checker(i - 1, j - 1, value + 3);
							} else {
								afterMove = new Checker(i - 1, j - 1, value);
							}
							turns.add(new Turn(beforeMove, afterMove, new LinkedList<>()));
						}
						if (ifInBounds(i - 1, j + 1) && d[i - 1][j + 1] == 0) {
							Checker beforeMove = new Checker(i, j, value);
							Checker afterMove;
							if (i == 0) {
								afterMove = new Checker(i - 1, j - 1, value + 3);
							} else {
								afterMove = new Checker(i - 1, j + 1, value);
							}
							turns.add(new Turn(beforeMove, afterMove, new ArrayList<>()));
						}

					}
				}
			}
		} else {
			for (int i = 0; i < d.length; i++) {
				for (int j = 0; j < d.length; j++) {
					if (d[i][j] == value) {
						if (ifInBounds(i + 1, j - 1) && d[i + 1][j - 1] == 0) {
							Checker beforeMove = new Checker(i, j, value);
							Checker afterMove;
							if (i == 7) {
								afterMove = new Checker(i + 1, j - 1, value + 3);
							} else {
								afterMove = new Checker(i + 1, j - 1, value);
							}
							turns.add(new Turn(beforeMove, afterMove, new ArrayList<>()));
						}
						if (ifInBounds(i + 1, j + 1) && d[i + 1][j + 1] == 0) {
							Checker beforeMove = new Checker(i, j, value);
							Checker afterMove;
							if (i == 7) {
								afterMove = new Checker(i + 1, j - 1, value + 3);
							} else {
								afterMove = new Checker(i + 1, j + 1, value);
							}
							turns.add(new Turn(beforeMove, afterMove, new ArrayList<>()));
						}

					}
				}
			}

		}
		return turns;
	}

	private static boolean ifInBounds(int i, int j) {
		if (i < 0 || i > 7 || j < 0 || j > 7)
			return false;
		return true;
	}
	
	private static String tString() {
		String str = "";
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				str += d[i][j] + " ";
			}
			str += "\n";
		}
		return str;
	}
}