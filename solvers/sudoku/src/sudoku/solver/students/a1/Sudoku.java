package sudoku.solver.students.a1;

import java.util.*;

/*
 * Encapsulates a Sudoku grid to be solved.
 * CS108 Stanford.
 */
public class Sudoku {
	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;
	private static final String tab = "\t";
	private final static String newline = "\n";
	private final static String quotes = "\"";
	private final static String quotesComma = "\",";
	private final static String charEnding = "\");";

	private static int[][] solution;
	private static int[][] grid;
	private static long time;
	private static int numSolutions;
	ArrayList<Spot> unassigned;

	// Provided various static utility methods to
	// convert data formats to int[][] grid.

	/**
	 * Returns a 2-d grid parsed from strings, one string per row.
	 * The "..." is a Java 5 feature that essentially
	 * makes "rows" a String[] array.
	 * (provided utility)
	 * @param rows array of row strings
	 * @return grid
	 */
	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row<rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
	}


	/**
	 * Given a single string containing 81 numbers, returns a 9x9 grid.
	 * Skips all the non-numbers in the text.
	 * (provided utility)
	 * @param text string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		int[] nums = stringToInts(text);
		if (nums.length != SIZE*SIZE) {
			throw new RuntimeException("Needed 81 numbers, but got:" + nums.length);
		}

		int[][] result = new int[SIZE][SIZE];
		int count = 0;
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				result[row][col] = nums[count];
				count++;
			}
		}
		return result;
	}


	/**
	 * Given a string containing digits, like "1 23 4",
	 * returns an int[] of those digits {1 2 3 4}.
	 * (provided utility)
	 * @param string string containing ints
	 * @return array of ints
	 */
	public static int[] stringToInts(String string) {
		int[] a = new int[string.length()];
		int found = 0;
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				a[found] = Integer.parseInt(string.substring(i, i+1));
				found++;
			}
		}
		int[] result = new int[found];
		System.arraycopy(a, 0, result, 0, found);
		return result;
	}


	// Provided -- the deliverable main().
	// You can edit to do easier cases, but turn in
	// solving hardGrid.
	public static void main(String[] args) {
		Sudoku sudoku = new Sudoku(null);

		System.out.println(sudoku);
		int count = sudoku.solve();


		System.out.println("solutions:" + count);
		System.out.println("elapsed: " + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
	}




	/**
	 * Sets up based on the given ints.
	 */
	public Sudoku(int[][] ints) {
		unassigned = getSpots(ints);
		numSolutions = 0;
		grid = ints;
		time = 0;
	}




	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 */
	public int solve() {
		numSolutions = 0;

		long startTime = System.currentTimeMillis();
		solveHelper(0);
		long endTime = System.currentTimeMillis();
		time = endTime - startTime;

		return numSolutions;
	}

	private void solveHelper(int index){
		if(index >= unassigned.size()){
			numSolutions++;
			if(numSolutions == 1){
				solution = new int[SIZE][SIZE];
				for(int i = 0; i < SIZE; i++)
					for(int j = 0; j < SIZE; j++)
						solution[i][j] = grid[i][j];
			}
		}else if(numSolutions != MAX_SOLUTIONS){
			Spot spot = unassigned.get(index);

			Set<Integer> set = spot.getPossibleValues();
			for(Integer val : set) {
				index++;
				spot.set(val);
				solveHelper(index);
				spot.set(0);
				index--;
			}
		}

	}


	private ArrayList<Spot> getSpots(int[][] grid){
		unassigned = new ArrayList<>();
		Spot spot;
			for(int i = 0; i < SIZE; i++)
			for(int j = 0; j < SIZE; j++)
				if(grid[i][j] == 0){
					spot = new Spot(i, j);
					unassigned.add(spot);
				}
		return unassigned;
	}

	@Override
	public String toString(){
		String text = "";
		for(int i = 0; i < SIZE; i++){
			text += tab;
			text+= quotes;
			for(int j = 0; j < SIZE; j++){
				 text += Integer.toString(grid[i][j]);
				 text += " ";
			}
			if(i != SIZE-1) text += quotesComma;
			else text += charEnding;
			text += newline;
		}
		return text;
	}

	public String getSolutionText() {
		if(solution == null) return "No Solution Found! \n";
		String text = "";
		for(int i = 0; i < SIZE; i++){
			for(int j = 0; j < SIZE; j++){
				 text += Integer.toString(solution[i][j]);
				 text += " ";
			}
			text += newline;
		}
		return text;
	}


	public long getElapsed() {
		if(solution == null) return 0;	//For use when there is no valid solution
		return time;
	}

	public int getNumSolutions(){
		return numSolutions;
	}


	public static class Spot {

		private int spotX;
		private int spotY;

		Spot(int x, int y){
			spotX = x;
			spotY = y;
		}

		private void set(int value){
			grid[spotX][spotY] = value;
		}

		private Set<Integer> getPossibleValues(){
			Set<Integer> set = new HashSet<>();
			for(int i = 1; i <= SIZE; i++)
				if(checkRow(i) && checkColumn(i) && checkSquare(i))
					set.add(i);
			return set;
		}

		private boolean checkRow(int value){
			for(int i = 0; i < SIZE; i++)
				if(grid[i][spotY] == value) return false;
			return true;
		}

		private boolean checkColumn(int value){
			for(int i = 0; i < SIZE; i++)
				if(grid[spotX][i] == value) return false;
			return true;
		}

		private boolean checkSquare(int value){
			int indexX = spotX/PART, indexY = spotY/PART;
			for(int row = indexX*PART; row < indexX*PART + PART; row++)
				for(int col = indexY*PART; col < indexY*PART + PART; col++)
					if(grid[row][col] == value) return false;
			return true;
		}

	}



}
