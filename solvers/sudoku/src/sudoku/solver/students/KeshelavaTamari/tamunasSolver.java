package sudoku.solver.students.KeshelavaTamari;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tester.Solver;

public class tamunasSolver implements Solver<int[][], int[][]> {
    
	public final int SIZE = 9; // size of the whole 9x9 puzzle
	public final int PART = 3; // size of each 3x3 part
	
	private int[][] grid, result;

	private ArrayList<spot> sortList;
	
	private long time;

	public int[][] solve(int[][] sudoku) {
		this.grid = sudoku;

		sortList = new ArrayList<>();
		fillList(sudoku);
		recursion(0);

		return result;
	}

	private void fillList(int[][] ints) {
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (ints[i][j] == 0)
					sortList.add(new spot(i, j));
			}
		}
		Collections.sort(sortList, new SpotComparator());
	}

	private class SpotComparator implements Comparator<spot> {

		public int compare(spot o1, spot o2) {
			return o1.getNum().size() - o2.getNum().size();
		}

	}

	/**
	 * solc Solves the puzzle, invoking the underlying recursive search.
	 */

	// makes copy of the grid
	private int[][] moveGrids(int[][] grid) {
		int[][] gr = new int[SIZE][SIZE];
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				gr[i][j] = grid[i][j];
			}
		}
		return gr;

	}

	

	private spot find(ArrayList<spot> arr) {
		spot smallest = null;
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (grid[i][j] == 0) {
					spot sp = new spot(i, j);
					if (smallest == null)
						smallest = sp;
					else if (new SpotComparator().compare(sp, smallest) < 0)
						smallest = sp;

				}
			}
		}
		return smallest;
	}

	

	// finds number of solutions
	private boolean recursion(int x) {
		if (IsPlace()) {
			result = moveGrids(grid);
			return true;
		}
		// sortArray();
		spot sp = find(sortList);
		List<Integer> n = sp.getNum();
		if (n.size() == 0)	return false;
		for (Integer k : n) {
			sp.set(k);
			if (recursion(x + 1))
				return true;
			sp.set(0);
		}
		return false;
	}

	// checks if grid is already filled
	private boolean IsPlace() {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				if (grid[i][j] == 0)
					return false;
			}
		}
		return true;
	}

	private class spot {
		private int x, y;

		
		public spot(int x, int y) {
			this.x = x;
			this.y = y;
			
		}

		public void set(int k) {
			grid[x][y] = k;
		}

		private boolean column(int k) {
			for (int i = 0; i < 9; i++) {
				if (grid[x][i] == k)
					return false;
			}
			return true;
		}

		private boolean row(int k) {
			for (int i = 0; i < 9; i++) {
				if (grid[i][y] == k)
					return false;
			}
			return true;
		}

		private boolean squ(int k) {
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

		public List<Integer> getNum() {
			List<Integer> s = new ArrayList<>();
			for (int i = 1; i <= SIZE; i++) {
				if (column(i) && squ(i) && row(i))
					s.add(i);
			}
			return s;

		}

	}

}