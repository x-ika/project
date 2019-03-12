package sudoku.solver.students.gkoch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import tester.Solver;

public class GkochSolver implements Solver<int[][], int[][]> {

	private static final int SIZE = 9;
	private int[][] board;
	private int[] row, col, box;
	private Spot[] list;

	private class Spot {

		public int x, y, pos;
		public boolean used;
	/*
	 * 	if AC_3 is used then using this variable is wise
	 * 	public int ac_3 = 511;
	 */
		 

		public Spot(int x, int y) {
			pos = 3 * (x / 3) + y / 3;
			this.x = x;
			this.y = y;
			used = false;
		}

		public int getDomains() {
			return (box[pos] & row[x] & col[y]);
		}
	
		/*
		 * this method is called if using AC_3
		 * this method will include ac-3 reduction 
		 * during counting possible values 
		 * 
		public int getDomains() {
			return (box[pos] & row[x] & col[y] & ac_3);
		}
		*/
		

	}

	public int[][] solve(int[][] sudoku) {
		List<Spot> set = new ArrayList<>();
		Init(sudoku, set);
	//	AC_3(set);
		constructArray(set);
		solveRec(0);
		return board;
	}

	/*
	 * put all spots in array from List
	 * because array is much faster
	 */
	private void constructArray(List<Spot> set) {
		list = new Spot[set.size()];
		for (int i = 0; i < set.size(); i++)
			list[i] = set.get(i);
	}

	private boolean solveRec(int index) {
		if (index == list.length)
			return true;
		/*
		 * find best spot in array
		 */
		int pos = -1;
		int dom = 10;
		for (int i = 0; i < list.length; i++) {
			if (!list[i].used) {
				int count = Integer.bitCount(list[i].getDomains());
				if (count < dom) {
					pos = i;
					dom = count;
				}
			}
		}
		//mark spot as used
		list[pos].used = true;
		Spot best = list[pos];
		int domains = best.getDomains();
		for (int i = 0; i < SIZE; i++) {
			//if domains include i value
			if ((domains & (1 << i)) > 0) {
				board[best.x][best.y] = i + 1;
				//make changes in constraints
				row[best.x] ^= (1 << i);
				col[best.y] ^= (1 << i);
				box[best.pos] ^= (1 << i);
				if (solveRec(index + 1))
					return true;
				//return at start point of constraints
				board[best.x][best.y] = 0;
				row[best.x] |= (1 << i);
				col[best.y] |= (1 << i);
				box[best.pos] |= (1 << i);
			}
		}
		//unmark spot
		list[pos].used = false;
		return false;
	}

	/**
	 * initialize sudoku structures
	 */
	private void Init(int[][] sudoku, List<Spot> set) {
		board = sudoku;
		row = new int[SIZE];
		col = new int[SIZE];
		box = new int[SIZE];
		for (int i = 0; i < sudoku.length; i++) {
			for (int j = 0; j < sudoku.length; j++) {
				if (sudoku[i][j] != 0) {
					/*
					 * make constraints
					 */
					int boxPos = 3 * (i / 3) + j / 3;
					int mask = (1 << (sudoku[i][j] - 1));
					row[i] |= mask;
					col[j] |= mask;
					box[boxPos] |= mask;
				} else {
					/*
					 * add zero spot in collection
					 */
					set.add(new Spot(i, j));
				}
			}
		}
		for (int i = 0; i < sudoku.length; i++) {
			row[i] ^= 511;
			col[i] ^= 511;
			box[i] ^= 511;
		}
	}

	
	/*
	 * iterate over spots
	 
	private void AC_3(List<Spot> set) {
		for (Spot spot : set) {
			reduce(spot, set);
		}
	}

	*reduce some domains for concrete spot
	*and then check other domains
	
	private void reduce(Spot spot, List<Spot> set) {
		for (int i = 0; i < SIZE; i++) {
			if ((spot.getDomains() & (1 << i)) > 0) {
				row[spot.x] ^= (1 << i);
				col[spot.y] ^= (1 << i);
				box[spot.pos] ^= (1 << i);
				if (checkIfReduced(spot, set)) {
					spot.ac_3 ^= (1 << i);
 				}
				row[spot.x] |= (1 << i);
				col[spot.y] |= (1 << i);
				box[spot.pos] |= (1 << i);
			}
		}
	}

	*check if reduction made changes over spots

	private boolean checkIfReduced(Spot best, List<Spot> set) {
		boolean reduced = false;
		for (Spot spot : set) {
			if (spot != best) {
				if (spot.getDomains() == 0){
					reduced = true;
				}
			}
		}
		return reduced;
	}
	*/
}