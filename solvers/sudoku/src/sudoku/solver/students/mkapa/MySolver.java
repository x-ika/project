package sudoku.solver.students.mkapa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import tester.Solver;

public class MySolver implements Solver<int[][], int[][]>{
	
	private int[][] grid;

	public static final int SIZE = 9; // constant size of sudoku puzzle
	private ArrayList<Spot> spots;
	private int numOfSolutions;
	private int solution[][];
	private long timeStart;
	private long timeEnd;

	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 */
	public int[][] solve(int[][] ints) {
		initialize(ints);
		Collections.sort(spots, new Comparator<>() {
            public int compare(Spot o1, Spot o2) {
                return o1.getPossibleNums().size() - o2.getPossibleNums().size();
            }
        });
		timeStart = System.currentTimeMillis();
		recursiveMethodForSolve(0);
		timeEnd = System.currentTimeMillis();
		return solution;
	}

	/**
	 * Sets up based on the given ints.
	 */
	public void initialize(int[][] ints) {
		spots = new ArrayList<>();
		grid = ints;
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (grid[i][j] == 0) {
					Spot newOne = new Spot(i, j);
					newOne.set(grid[i][j]);
					spots.add(newOne);
				}
			}
		}
		solution = new int[SIZE][SIZE];
	}

	/**
	 * Recursive method to solve sudoku.
	 * start with the spot which has a minimum size of pissible values
	 */
	private void recursiveMethodForSolve(int num) {
		if(numOfSolutions == 1) return; // first base case
		if (num == spots.size()) { // second base case
			if (solution[0][0] == 0) { // first solution
				copyToSolution();
			}
			numOfSolutions++;
			return;
		}
		
		// gets unvisited Spots
		ArrayList<Spot> tmp = new ArrayList<>();
		for (int i = num; i < spots.size(); i++) {
			tmp.add(spots.get(i));
		}
		
		// sorts unvisited Spots
		Collections.sort(tmp, new Comparator<>() {
            public int compare(Spot o1, Spot o2) {
                return o1.getPossibleNums().size() - o2.getPossibleNums().size();
            }
        });
		// sets sorted spots to main set of the Spots
		for (int i = num; i < spots.size(); i++) {
			spots.set(i, tmp.get(i-num));
		}
		Spot spot = spots.get(num);
		if (spot.getPossibleNums().size() == 0) { // third base case
			return;
		}
		HashSet<Integer> st = spot.getPossibleNums();
		Iterator<Integer> it = st.iterator();
		while (it.hasNext()) {
			spot.set(it.next());
			recursiveMethodForSolve(num + 1);
			spot.set(0);  // backtracking
		}
	}
	
	private void copyToSolution() {
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				solution[i][j] = grid[i][j];
			}
		}
	}

	// inner class Spot. 
	// A Spot is on cell on the sudoku grid.
	// it has it's value, if the value is 0, it has possible values.
	// it has small 3X3 square, to which it belongs
	private class Spot {
		private int r; // row number
		private int c;  // column number
		private int val;
		private HashSet<Integer> res;  // possible values
		
		// coordinates of small square, to which that spot belongs.
		private int smallSquareRow; 
		private int smallSquareCol;

		/*
		 * initialize the spot
		 */
		public Spot(int row, int col) {
			r = row;
			c = col;
			val = 0;
			smallSquareRow = (row / 3) * 3;
			smallSquareCol = (col / 3) * 3;
		}

		public void set(int num) {
			val = num;
			if(num!=0){
				res = new HashSet<>();
			}
			grid[r][c] = val;
		}

		public HashSet<Integer> getPossibleNums() {
			res = new HashSet<>();
			updatePossibleNums();
			return res;
		}

		private void updatePossibleNums() {
			for (int i = 1; i <= 9; i++) {
				res.add(i);
			}
			// rows and columns
			for (int i = 0; i < 9; i++) {
				if (res.contains(grid[r][i])) {
					res.remove(grid[r][i]);
				}
				if (res.contains(grid[i][c])) {
					res.remove(grid[i][c]);
				}
			}
			// small square
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (res.contains(grid[smallSquareRow + i][smallSquareCol + j])) {
						res.remove(grid[smallSquareRow + i][smallSquareCol + j]);
					}
				}
			}
		}
	}
}
