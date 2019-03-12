package sudoku.solver.students.a3;

import java.util.*;

/*
 * Encapsulates a Sudoku grid to be solved.
 * CS108 Stanford.
 */
public class Sudoku {
	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;

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
	 * (provided code)
	 * @param text string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		int[] nums = stringToInts(text);
		if (nums.length != SIZE*SIZE)
			throw new RuntimeException("Needed 81 numbers, but got:" + nums.length);

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

	int[][] grid;

	public Sudoku (int[][] grid)
	{
		this.grid = transformForeignGrid(grid);
	}

	public static int[][] transformForeignGrid(int[][] foreign)
	{
		//foreign form is really relative to the top
		int[][] ints = new int[9][9];
		for (int i=0; i < SIZE; i++)
			for (int j=0; j < SIZE; j++)
			{
				int foreignForI = SIZE - 1 - j;
				int foreignForJ = i;
				ints[i][j] = foreign[foreignForI][foreignForJ];
			}
		return ints;
	}

	public Sudoku (String text)
	{
		this.grid = transformForeignGrid(textToGrid(text));
	}

	public int getGridValue(int x, int y)
	{
		return grid[x-1][y-1];
	}

	public void setGridValue(int x, int y, int num)
	{
		grid[x-1][y-1] = num;
	}

	public int[] getRow(int n)
	{
		int[] row = new int[9];
		for (int i=1; i < 10; i++)
			row[i] = grid[i][n];
		return row;
	}

	public int[] getCol(int n)
	{
		int[] col = new int[9];
		for (int i=1; i < 10; i++)
			col[i] = grid[n][i];
		return col;
	}

	public String toString() {
		String out = "";
		for (int i=SIZE; i != 0; i--)
		{
			String[] row = new String[SIZE];
			for (int j=1; j <= SIZE; j++)
				row[j-1] = Integer.toString(getGridValue(j, i));

			//out += RedUtil.join(" ", row) + "\n";
		}
		/*
		for (int j=SIZE -1; j != 0; j--)
		{
			String[] row = new String[SIZE];
			for (int i=0; i < SIZE; i++)
				row[i] = Integer.toString(grid[i][j]);

			out += RedUtil.join(" ", row) + "\n";
		}
		*/
		return out;
	}

	protected int getWidth() { return grid.length; }
	protected int getHeight() { return grid[0].length; }


	public static void main(String[] args) {
		Sudoku sudoku = new Sudoku("");

		System.out.println(sudoku); // print the raw problem
		System.out.println("solutions:" + sudoku.solve());
		System.out.println(sudoku.getSolutionText());
	}

	public String getSolutionText()
	{
		return lastSolution.toString();
	}


	public Stack<Spot> computeSpotsStack()
	{
		List<Spot> thespots = new LinkedList<>();

		for (int i=0; i < SIZE; i++)
			for (int j=0; j < SIZE; j++)
				thespots.add(new Spot(i+1, j+1));

		Collections.sort(thespots, new Comparator<>() {
            public int compare(Spot s1, Spot s2) {
                return s1.possibleNumbers().size() - s2.possibleNumbers().size();
            }
        });

		Stack<Spot> spotstack = new Stack<>();
		spotstack.addAll(thespots);
		return spotstack;

	}


	Sudoku lastSolution;
	protected int solve()
	{
		Stack<Spot> spotstack = computeSpotsStack();
		lastSolution = new Sudoku(new int[9][9]);
		return spotstack.pop().solveRepeatedlyWhileStoringLastCorrectBoard
				  (100, lastSolution, spotstack);
	}


	protected Spot[][] constructSpotsGrid() {

		Spot[][] spots = new Spot[9][9];

		for (int i=0; i < spots.length; i++)
		{
			for (int j=0; j < spots[i].length; j++)
			{
				spots[i][j] = new Spot(i, j);
			}
		}
		return spots;
	}

	/**
	 * warning: this is a debug function made public for testing purposes
	 * @return
	 */
	public boolean isBoardValid()
	{
		int filled = filledSpaces();
		int lacking = spacesLackingPossibilities();
		return lacking == SIZE*SIZE - filled;
		//return boardFilled() && lacking == 0 || lacking > 0;
	}

	protected boolean boardFilled() {
		for (int i=1; i < 10; i++)
			for (int j=1; j < 10; j++)
				if (getGridValue(i, j) == 0)
					return false;
		return true;
	}

	public int spacesLackingPossibilities()
	{
		int count = 0;
		for (Spot spot : computeSpotsStack())
		{
			Collection<Integer> possibilities = spot.possibleNumbers();
			if (possibilities.size() == 0 && getGridValue(spot.x, spot.y) == 0)
				count++;
		}
		return count;
	}

	public int filledSpaces() {
		int count = 0;
		for (int i=1; i < 10; i++)
			for (int j=1; j < 10; j++)
				if (getGridValue(i, j) != 0)
					count++;
		return count;
	}

	public Spot createSpot(int x, int y)
	{
		return new Spot(x, y);
	}

	protected class Spot {
		int x; int y; // from 1 to 9

		/**
		 *
		 * @param x position 1-9
		 * @param y position 1-9
		 */
		public Spot(int x, int y)
		{
			this.x = x; this.y = y;
		}

		/**
		 * the name explains it all
		 * solves until maxSolves is reached, storing successful boards in fillBoard.
		 */
		public int solveRepeatedlyWhileStoringLastCorrectBoard(int maxSolves, Sudoku fillBoard, List<Spot> subsequentSpots)
		{
			/*
			 * to test if spot solves for any value of the current spot:
			 * 	  for each valid spot number
			 *       change board state to account for our attempt
			 *       test if the board solves for the given state
			 *           true: increment spot counter
			 *           false:
			 *       revert board state
			 */
			int numsolutions = 0;
			int oldvalue = getGridValue(x, y);
			List<Spot> substack = subsequentSpots.size() ==0 ?
									null : subsequentSpots.subList(1, subsequentSpots.size());;
			if (oldvalue == 0)
			{
				for (Integer possibility : possibleNumbers())
				{
					if (numsolutions > maxSolves)
						break;
					setGridValue(x, y, possibility);
					if (boardFilled())
					{
						numsolutions++;
						for (int i=1; i < SIZE+1; i++)
							for (int j=1; j < SIZE+1; j++)
								fillBoard.setGridValue(i, j, getGridValue(i, j));
					}
					else if (!subsequentSpots.isEmpty())
					{
						Spot testSpot = subsequentSpots.get(0);
						numsolutions += testSpot.solveRepeatedlyWhileStoringLastCorrectBoard
												(maxSolves - numsolutions, fillBoard, substack);
					}
				}
			}
			else if (!subsequentSpots.isEmpty())
			{
				Spot testSpot = subsequentSpots.get(0);
				numsolutions += testSpot.solveRepeatedlyWhileStoringLastCorrectBoard
										(maxSolves - numsolutions, fillBoard, substack);
			}
			setGridValue(x, y, oldvalue);
			return numsolutions;
		}

		protected boolean rowLacksNumber(int n)
		{
			for (int i=1; i <= 9; i++)
			{
				if (getGridValue(i, y) == n)
					return false;
			}
			return true;
		}

		protected boolean colLacksNumber(int n)
		{
			for (int i=1; i <= 9; i++)
			{
				if (getGridValue(x, i) == n)
					return false;
			}
			return true;
		}


		protected int snapTo1or4or7(int n)
		{
			if (n < 4)       return 1;
			else if ( n < 7) return 4;
			else             return 7;
		}

		protected boolean squareLacksNumber(int n)
		{
			//get closest bottom of square
			int snapi = snapTo1or4or7(x);
			int snapj = snapTo1or4or7(y);
			for (int i=snapi; i < snapi + 3; i++)
			{
				for (int j = snapj; j < snapj + 3; j++)
				if (getGridValue(i, j) == n)
					return false;
			}
			return true;
		}

		public String toString()
		{
			return "(" + x + ", " + y + ": " + getGridValue(x, y) + ")";
		}

		/**
		 * @return the possible numbers that could occupy this space given
		 * the row and column and square of the spot
		 */
		public Collection<Integer> possibleNumbers()
		{

			ArrayList<Integer> possibilities = new ArrayList<>();
			if (getGridValue(x, y) == 0)
			{
				for (int i=1; i < 10; i++)
				{
					if (rowLacksNumber(i) &&
						colLacksNumber(i) &&
						squareLacksNumber(i))
						possibilities.add(i);
				}
			}
			return possibilities;
		}
	}

}
