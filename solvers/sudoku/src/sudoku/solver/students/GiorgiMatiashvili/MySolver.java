package sudoku.solver.students.GiorgiMatiashvili;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tester.Solver;

public class MySolver implements Solver<int[][], int[][]> {

	private static final int SIZE = 9;
	private int sudoku[][];
	private ArrayList<Spot> spots;
	private int[][] solution = null;

	public int[][] solve(int[][] sudoku) {
		this.sudoku = sudoku;
		spots = new ArrayList<>();
		initSpots();
		solve();
		return solution;
	}

	private void initSpots() {
		for (int i = 0; i < sudoku.length; i++) {
			for (int j = 0; j < sudoku[0].length; j++) {
				if (sudoku[i][j] == 0) {
					Spot sp = new Spot(i, j);
					addPossibleValues(sp, sudoku);
					spots.add(sp);
				}
			}
		}
	}

	private void addPossibleValues(Spot sp, int[][] sudoku) {
		sp.makeAllPossible();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				int value = sudoku[sp.getX() / 3 * SIZE / 3 + i][sp.getY() / 3
						* SIZE / 3 + j];
				if (value != 0) {
					sp.removePossibleValue(value);
				}
			}
		}

		for (int i = 0; i < SIZE; i++) {
			sp.removePossibleValue(sudoku[sp.getX()][i]);
			sp.removePossibleValue(sudoku[i][sp.getY()]);
		}
	}

	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 */
	public void solve() {
		Collections.sort(spots);
		mySolve(0, sudoku, spots);
	}

	private boolean mySolve(int pos, int[][] sudoku, List<Spot> sp) {
		if (solution != null)
			return true;
		if (sp.size() == 0) {
			solution = sudoku;
			return true;
		}
		Spot cur = sp.remove(0);
		boolean[] arr = cur.getPossibleValues();
		if (cur.getPosValueCount() == 0) {
			return false;
		}
		for (int i = 1; i < arr.length; i++) {
			if (arr[i]) {
				sudoku[cur.getX()][cur.getY()] = i;
				List<Spot> newList = new ArrayList<>();
				newList.addAll(sp);
				for (int j = 0; j < newList.size(); j++) {
					newList.get(j).initPossibleValues(sudoku);
				}
				Collections.sort(newList);
				if (mySolve(pos + 1, sudoku, newList)) {
					return true;
				}
				sudoku[cur.getX()][cur.getY()] = 0;
			}
		}
		return false;
	}

	private class Spot implements Comparable<Spot> {
		private int x, y;
		private boolean[] possibleValues;
		private int posValueCount;

		public Spot(int x, int y) {
			this.x = x;
			this.y = y;
			possibleValues = new boolean[10];
			posValueCount = 0;
		}

		public void initPossibleValues(int[][] sudoku) {
			addPossibleValues(this, sudoku);

		}

		public void makeAllPossible() {
			for (int i = 1; i < possibleValues.length; i++)
				possibleValues[i] = true;
			posValueCount = 9;

		}

		public int getPosValueCount() {
			return posValueCount;
		}

		/*
		 * return X coordinate of the spot
		 */
		public int getX() {
			return x;
		}

		/*
		 * return Y coordinate of the spot
		 */
		public int getY() {
			return y;
		}

		public boolean[] getPossibleValues() {
			return possibleValues;
		}

		public void removePossibleValue(int value) {
			if (value > 0 && possibleValues[value]) {
				possibleValues[value] = false;
				posValueCount--;
			}
		}

		@Override
		public int compareTo(Spot a) {
			return this.getPosValueCount() - a.getPosValueCount();
		}
	}
}
