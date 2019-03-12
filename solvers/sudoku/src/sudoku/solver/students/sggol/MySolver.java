package sudoku.solver.students.sggol;

import java.util.ArrayList;
import java.util.Collections;
import tester.Solver;

public class MySolver implements Solver<int[][], int[][]> {

	public static final int SIZE = 9; // size of the whole 9x9 puzzle
	public static final int PART = 3; // size of each 3x3 part

	private int[][] sGrid;
	private boolean found;
	private int[][] finalSolve;

	@Override
	public int[][] solve(int[][] t) {
		sGrid = copyGrid(t);
		rec();
		return finalSolve;
	}

	private void rec() {
		if (found) {
			return;
		}

		// make an ArrayList of spots for blank points
		ArrayList<Spot> spots = new ArrayList<>();
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (sGrid[i][j] == 0) {
					spots.add(new Spot(i, j));
				}
			}
		}
		//
		if (spots.size() == 0) {
			found = true;
			finalSolve = copyGrid(sGrid);
			return;
		}

		// sort by the number of legal ciphers
		Collections.sort(spots);
		Spot s = spots.get(0);
		boolean[] legals = s.getLegals();

		for (int i = 0; i < 10; i++) {
			if (legals[i]) {
				s.set(i); // writes this legal cipher on this blank point
				rec();
			}
		}
		s.set(0);// if none of legal points were good on s spot erase this spot
					// and return

	}

	// copies grid
	private int[][] copyGrid(int[][] sorc) {
		int[][] dest = new int[sorc.length][sorc[0].length];
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				dest[i][j] = sorc[i][j];
			}
		}
		return dest;
	}

	/**
	 * inner class spot contains information about one point of sudoku contains
	 * ArrayList of legal ciphers for this point *
	 */
	private class Spot implements Comparable<Spot> {
		private int row, col;
		boolean[] legals;
		int legalsNum;

		// creates spot object
		public Spot(int row, int col) {
			legals = new boolean[10];
			legalsNum = 10;
			for (int i = 0; i < 10; i++) {
				legals[i] = true;
			}
			this.col = col;
			this.row = row;
			findLegals();
		}

		// finds legal ciphers for this spot
		public void findLegals() {
			for (int i = 0; i < SIZE; i++) {
				if (legals[sGrid[row][i]]) {
					legals[sGrid[row][i]] = false;
					legalsNum--; //legal ciphers became fewer
				}
				if (legals[sGrid[i][col]]) {
					legals[sGrid[i][col]] = false;
					legalsNum--;
				}
			}
			int sqx = (row / PART) * PART;
			int sqy = (col / PART) * PART;
			for (int i = sqx; i < sqx + 3; i++) {
				for (int j = sqy; j < sqy + 3; j++) {
					if (legals[sGrid[i][j]]) {
						legals[sGrid[i][j]] = false;
						legalsNum--;
					}
				}
			}
		}
		// returns set of illegal ciphers for this spot
		public boolean[] getLegals() {
			return legals;
		}

		// returns number of legal ciphers for this
		private int getLegalsNum() {
			return legalsNum;
		}

		// resets meaning of this spot
		public void set(int point) {
			sGrid[row][col] = point;
		}

		// compares two spots by the number of legal ciphers
		public int compareTo(Spot o) {
			return this.getLegalsNum() - o.getLegalsNum();
		}

	}

}
