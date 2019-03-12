package tetris.students.vmask;

/*
 * This class is needed for storing data for every position that
 * pile can be put on: pile's final row and column,
 * how many rows where deleted when pile was dropped and etc.
 */
public class Drop {
	public double score;

	public int rotAmount;
	public int row;
	public int col;

	public int rowsCleaned = 0;
	public int rowHeight;

	public int[] bf;
}