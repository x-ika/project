package sudoku.solver.students.qetiuridiaassign2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import tester.Solver;

public class kuridSolver implements Solver<int[][], int[][]> {

	public static final int SIZE = 9; // size of the whole 9x9 puzzle
	public static final int PART = 3; // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 1;

	private int[][] sudokuGrid;
	private ArrayList<Spot> emptySpots;
	private int[][] solution;

	private class Spot {
		public boolean[] domain;
		public int x;
		public int y;
		public int size;

		public Spot(int x, int y) {
			size = 9;
			this.x = x;
			this.y = y;
			domain = new boolean[10];
		}

	}

	/**
	 * Finds domains for all empty spot
	 */
	private void domainsForAllSpot() {
		emptySpots = new ArrayList<>();
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (sudokuGrid[i][j] == 0) {
					Spot newSpot = new Spot(i, j);
					emptySpots.add(newSpot);
					findDomain(newSpot);
				}
			}
		}

	}

	private void findDomain(Spot newSpot) {
		for (int i = 0; i < SIZE; i++) {
			Integer num;
			if (sudokuGrid[newSpot.x][i] != 0) {
				num = sudokuGrid[newSpot.x][i];
				if(!newSpot.domain[num])newSpot.size--;
				newSpot.domain[num] = true;
			}

			if (sudokuGrid[i][newSpot.y] != 0){
				num = sudokuGrid[i][newSpot.y];
				if(!newSpot.domain[num])newSpot.size--;
				newSpot.domain[num] = true;
			}
		}
		rulesInSquare(newSpot);
	}

	private void rulesInSquare(Spot newSpot) {
		for (int i = -newSpot.x % PART; i <= 2 - (newSpot.x % PART); i++) {
			for (int j = -newSpot.y % PART; j <= 2 - (newSpot.y % PART); j++) {
				Integer num = sudokuGrid[newSpot.x + i][newSpot.y + j];
				if (num != 0){
					if(!newSpot.domain[num]) newSpot.size--;
					newSpot.domain[num] = true;
				}
			}
		}
	}

	public static class SpotComparator implements Comparator<Spot> {
		public int compare(Spot o1, Spot o2) {
			return o1.size - o2.size;
		}

	}

	private boolean isFound;

	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 */
	public void mySolver() {
		solution = new int[SIZE][SIZE];
		recSolve();
	}

	/**
	 * Tu X aris mnishvneloba im wertilis domeinidan romelsac ganvixilavt da Y
	 * danarcheni wertilebis mnishvnelobebi ertad ganxiluli. mashin rekurisa
	 * edzebs iset X romlistvisac moidzebneba iseti y rom iseni ertobliobashi
	 * wesebs, shezgudvebs(rules) ar argvevdnen.
	 */
	private void recSolve() {
		if (isFound){
			return;
		}
		domainsForAllSpot();
		Collections.sort(emptySpots, new SpotComparator());
		if (emptySpots.size() == 0) {
			isFound = true;
			saveFirstSolution();
			return;
		}
		Spot sp = emptySpots.get(0);
		for (int i = 1; i < 10; i++) {
			if (sp.domain[i]) continue;
			sudokuGrid[sp.x][sp.y] = i;
			recSolve();
			sudokuGrid[sp.x][sp.y] = 0;
		}
	}

	private void saveFirstSolution() {
		for (int i = 0; i < SIZE; i++)
			for (int j = 0; j < SIZE; j++)
				solution[i][j] = sudokuGrid[i][j];
	}

	

	/**
	 * main function, finds domains for all empty spots, then calls mySolver,
	 * which will solve sudoku
	 */
	public int[][] solve(int[][] sudoku) {
		sudokuGrid = new int[SIZE][SIZE];
		sudokuGrid = sudoku;
		domainsForAllSpot();
		mySolver();
		return solution;
	}

}
