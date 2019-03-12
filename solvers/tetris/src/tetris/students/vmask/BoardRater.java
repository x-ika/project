package tetris.students.vmask;

/*
 * Given board, returns it's score.
 */
public interface BoardRater {
	int getBWidth();
	int getBHeight();

	double rateBoard(int[] bf, Drop drp);
}