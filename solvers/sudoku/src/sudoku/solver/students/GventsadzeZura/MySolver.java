package sudoku.solver.students.GventsadzeZura;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import tester.Solver;

public class MySolver implements Solver<int[][], int[][]> {
	
	private static int[][] grid;
	private ArrayList<Spot> arr;
	public static final int SIZE = 9;
	private Comparator<Spot> cmp;
	
	public int[][] solve(int[][] board) {
		grid = board;
		setupSpots();
		recursiveSolve();
		return grid;
	}
	
	private void setupSpots() {
		arr = new ArrayList<>();
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (grid[i][j] == 0) arr.add(new Spot(i, j, true));
			}
		}
		cmp = new Comparator<>() {
            public int compare(Spot a, Spot b) {
                return a.getPossibleNums().size() - b.getPossibleNums().size();
            }
        };
		Collections.sort(arr, cmp);
	}
	
	private boolean recursiveSolve() {
		if (arr.size() == 0) return true;
		ArrayList<Spot> saved = getDeepCopy(arr);
		Spot curr = saved.get(0);
		Iterator<Integer> it = curr.getPossibleNums().iterator();
		while (it.hasNext()) {
			int value = it.next();
			curr.set(value);
			arr.remove(0);
			recomputePossibleNums(curr, value);
			Collections.sort(arr, cmp);
			if (recursiveSolve()) return true;
			curr.set(0);
			arr = getDeepCopy(saved);
		}
		return false;
	}
	
	private ArrayList<Spot> getDeepCopy(ArrayList<Spot> arr) {
		ArrayList<Spot> result = new ArrayList<>();
		for (int i = 0; i < arr.size(); i++) {
			Spot spot = arr.get(i);
			Spot newSpot = new Spot(spot.getRow(), spot.getCol(), false);
			newSpot.possibleNums.addAll(spot.getPossibleNums());
			result.add(newSpot);
		}
		return result;
	}
	
	private void recomputePossibleNums(Spot curr, int value) {
		for (Spot spot: arr) {
			if (spot.getRow() == curr.getRow() || spot.getCol() == curr.getCol() || spot.isInsideSameBox(curr)) {
				spot.getPossibleNums().remove(value);
			}
		}
	}

	
	// Spot class - abstraction of one cell in the sudoku grid.
	private class Spot {
		private int row, col;
		private HashSet<Integer> possibleNums;
		
		public Spot(int row, int col, boolean compute) {
			this.row = row;
			this.col = col;
			possibleNums = new HashSet<>();
			if (compute) computePossibleNums();
		}
		
		public void computePossibleNums() {
			possibleNums.clear();
			for (int i = 1; i <= SIZE; i++) possibleNums.add(i);
			for (int i = 0; i < SIZE; i++) {
				possibleNums.remove(grid[row][i]);
				possibleNums.remove(grid[i][col]);
			}
			computeFromSquare();
		}
		
		private void computeFromSquare() {
			int upRow = row - row % 3;
			int upCol = col - col % 3;
			for (int i = upRow; i < upRow + 3; i++) {
				for (int j = upCol; j < upCol + 3; j++) {
					possibleNums.remove(grid[i][j]);
				}
			}
		}
		
		public boolean isInsideSameBox(Spot spot) {
			int upRow = spot.row - spot.row % 3;
			int upCol = spot.col - spot.col % 3;
			return (((row - row % 3) == upRow) && ((col - col % 3) == upCol));
		}
		
		public void set(int value) {
			grid[getRow()][getCol()] = value;
		}
		
		public int getRow() {
			return row;
		}
		
		public int getCol() {
			return col;
		}
		
		public HashSet<Integer> getPossibleNums() {
			return possibleNums;
		}
	}
	
}
