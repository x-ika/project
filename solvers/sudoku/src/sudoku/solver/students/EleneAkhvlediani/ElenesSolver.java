package sudoku.solver.students.EleneAkhvlediani;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tester.Solver;

public class ElenesSolver implements Solver<int[][], int[][]> {

	private int[][] grid, solution;

	private ArrayList<spot> arr;
	
	private long time;

	public int[][] solve(int[][] sudoku) {
		this.grid = sudoku;

		arr = new ArrayList<>();

		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				if (sudoku[i][j] == 0)
					arr.add(new spot(i, j));// fills arraylist for empty spots

			}
		}
		Collections.sort(arr, new SpotComparator());
		find(0);

		return solution;
	}

	private class SpotComparator implements Comparator<spot> {

		public int compare(spot o1, spot o2) {
			return o1.getPossibilities().size() - o2.getPossibilities().size();
		}

	}

	/**
	 * solc Solves the puzzle, invoking the underlying recursive search.
	 */

	// makes copy of the grid
	private int[][] copyGrids(int[][] grid) {
		int[][] newGrid = new int[grid.length][grid[0].length];
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid.length; j++) {
				newGrid[i][j] = grid[i][j];
			}
		}
		return newGrid;

	}

	

	private spot smallest(ArrayList<spot> arr) {
		spot small = null;
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				if (grid[i][j] == 0) {
					spot s = new spot(i, j);
					if (small == null)
						small = s;
					else if (new SpotComparator().compare(s, small) < 0)
						small = s;

				}
			}
		}
		return small;
	}

	

	// finds number of solutions
	private boolean find(int k) {
		if (IsFilled()) {

			solution = copyGrids(grid);

			return true;
		}
		// sortArray();
		spot sp = smallest(arr);
		List<Integer> s = sp.getPossibilities();
		if (s.size() == 0)
			return false;
		for (Integer j : s) {

			sp.set(j);
			if (find(k + 1))
				return true;
			sp.set(0);
		}
		return false;
	}

	// checks if grid is already filled
	private boolean IsFilled() {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				if (grid[i][j] == 0)
					return false;
			}
		}
		return true;
	}

	// returns solution into text form

	// returns elapsed time
	public long getElapsed() {
		return time;
	}

	private class spot {
		private int x, y;

		// constructor
		public spot(int x, int y) {
			this.x = x;
			this.y = y;
			// s=getPossibilities();
		}

		// sets value of spot
		public void set(int k) {
			grid[x][y] = k;
		}

		// finds if value k is in column
		private boolean findInColumn(int k) {
			for (int i = 0; i < 9; i++) {
				if (grid[x][i] == k)
					return false;
			}
			return true;
		}

		// finds if value k is in row
		private boolean findInRow(int k) {
			for (int i = 0; i < 9; i++) {
				if (grid[i][y] == k)
					return false;
			}
			return true;
		}

		// finds if value k is in square
		private boolean findInSquare(int k) {
			int row = square(x);
			int col = square(y);
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (grid[i + row][j + col] == k)
						return false;
				}
			}
			return true;
		}

		private int square(int x) {
			if (x < 3)
				return 0;
			if (x < 6)
				return 3;
			return 6;
		}

		// returns possible values for this spot
		public List<Integer> getPossibilities() {
			List<Integer> s = new ArrayList<>();
			for (int i = 1; i <= 9; i++) {
				if (findInColumn(i) && findInSquare(i) && findInRow(i))
					s.add(i);
			}
			return s;

		}

	}

}