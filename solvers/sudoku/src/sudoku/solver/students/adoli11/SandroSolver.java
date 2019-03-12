package sudoku.solver.students.adoli11;

import java.util.*;
import tester.Solver;

public class SandroSolver implements Solver<int[][], int[][]>{
	static final int[][] G = new int[1234][];
	static int[] F = new int[1234];
	
	private int[] verMask, horMask, boxMask;
	private int[][] sudoku;
	private int[][] result;
	
	static {
		for (int i = 0; i < G.length; i++) {
			G[i] = new int[F[i] = Integer.bitCount(i)];
			for (int j = 0, z = 0; j < 9; j++) {
				if ((i & 1 << j) != 0) G[i][z++] = j;
			}
		}
	}
	
	private class Spot {
		private int ver, hor, box, ac3;
		public boolean checked = false;

		public Spot(int hor, int ver) {
			this.ver = ver;
			this.hor = hor;
			this.box = 3*(hor/3) + ver/3;
			this.ac3 = 0x1ff;
		}

		public final void set(int num) {
			verMask[ver] &= (~(1 << num-1));
			horMask[hor] &= (~(1 << num-1));
			boxMask[box] &= (~(1 << num-1));
			sudoku[hor][ver] = num;
		}
		
		public final void unset() {
			int prev = sudoku[hor][ver];
			verMask[ver] |= (1 << (prev-1));
			horMask[hor] |= (1 << (prev-1));
			boxMask[box] |= (1 << (prev-1));
			sudoku[hor][ver] = 0;
		}

		public final int getPossibleNumbers() {
			return (verMask[ver] & horMask[hor] & boxMask[box] & ac3);
		}
						
		public final int getPossibleNumbersCount() {
			return F[getPossibleNumbers()];
		}
	}
		
	private final boolean solveRecursion(Spot[] spots, int left) {
		if (left == 0) {
			result = new int[sudoku.length][sudoku[0].length];
			for (int i = 0; i < result.length; i++)
				result[i] = Arrays.copyOf(sudoku[i], sudoku[i].length);
			return true;
		}
		
		Spot cur = getMin(spots);
		cur.checked = true;
		int mask = cur.getPossibleNumbers();

		for (int i: G[mask]) {
			cur.set(i+1);
			if (solveRecursion(spots, left-1))
				return true;
			cur.unset();
		}
		
		cur.checked = false;
		
		return false;
	}
	
	public final Spot getMin(Spot[] spots) {
		int min = 10;
		Spot res = null;
		
		for (int i=0; i<spots.length; i++) {
			int cur = spots[i].getPossibleNumbersCount();
			if (!spots[i].checked && cur < min) {
				res = spots[i];
				min = cur;
			}
		}
		
		return res;
	}
	
	public final void start(int[][] sudoku) {
		verMask = new int[9];
		horMask = new int[9];
		boxMask = new int[9];
		
		for (int i=0; i<9; i++) {
			for (int j=0; j<9; j++) {
				if (sudoku[i][j] != 0) {
					int k = 3*(i/3) + j/3;
					int num = sudoku[i][j] - 1;
					verMask[j] |= (1 << num);
					horMask[i] |= (1 << num);
					boxMask[k] |= (1 << num);
				}
			}
		}
		
		for (int i=0; i<9; i++) {
			verMask[i] = ~verMask[i] & 0x1ff;
			horMask[i] = ~horMask[i] & 0x1ff;
			boxMask[i] = ~boxMask[i] & 0x1ff;
		}
	}
	
	public final void ac3(Spot[] spots) {
		for (int i=0; i<spots.length; i++) {
			Spot cur = spots[i];
			int mask = cur.getPossibleNumbers();
			for (int j : G[mask]) {
				cur.set(j+1);
				for (int k=0; k<spots.length; k++)
					if (spots[k] != cur && spots[k].getPossibleNumbersCount() == 0)
						cur.ac3 &= (~(1 << j) & 0x1ff);
				cur.unset();
			}
		}
			
	}
	
    public int[][] solve(int[][] input) {
    	sudoku = input;
    	start(input);
		ArrayList<Spot> spots = new ArrayList<>();
		
		for (int i=0; i<9; i++)
			for (int j=0; j<9; j++)
				if (input[i][j] == 0)
					spots.add(new Spot(i, j));
		
		Spot[] emptySpots = spots.toArray(new Spot[0]);
		//ac3(emptySpots);
		solveRecursion(emptySpots, emptySpots.length);	
		
		return result;
    }
}