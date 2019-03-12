package sudoku.solver.students.ggigl;

import java.util.ArrayList;
import java.util.List;

import tester.Solver;

public class GiglemaSolver implements Solver<int[][], int[][]> {

	private int N;
	private int[][] board;
	private int[] row, col, block;
	private List<Square> arr;

	public int[][] solve(int[][] sudoku) {
		prepare(sudoku);
		rec(0);
		return board;
	}

	//preparing starting situation for solve.
	private void prepare(int[][] sudoku) {
		N = sudoku.length;
		// firstly copying sudoku. otherwise we lose the data.
		board= new int[N][N];
		for (int i=0; i<N; i++)
			for (int j=0; j<N; j++)
				board[i][j]= sudoku[i][j];
		arr = new ArrayList<>();
		row = new int[N];
		col = new int[N];
		block = new int[N];
		int mask;
		for (int i = 0; i < N; ++i) {
			for (int j = 0; j < N; ++j) {
				if (board[i][j] != 0) {
					mask = (1 << (board[i][j] - 1));
					row[i] |= mask;
					col[j] |= mask;
					block[3 * (i / 3) + j / 3] |= mask;
				} else
					arr.add(new Square(i, j));
			}
		}
		for (int i = 0; i < N; ++i) {
			row[i] ^= (1<<N)-1;
			col[i] ^= (1<<N)-1;
			block[i] ^= (1<<N)-1;
		}
	}

	private boolean rec(int index) {
		if (index == arr.size())
			return true;
		int minI = 0,min = N+1,temp;
		// startig solve with lowest domain size is better time-wise.
		for (int i = 0; i < arr.size(); ++i) {
			if (!arr.get(i).used) {
				temp = Integer.bitCount(arr.get(i).getDomains());
				if (temp < min) {
					minI = i;
					min = temp;
				}
			}
		}
		arr.get(minI).used = true;
		Square cur = arr.get(minI);
		int domainsMask = cur.getDomains();
		for (byte i = 0; i < N; ++i) {
			temp = (1<<i);
			if ((domainsMask & temp) > 0) {
				//trying to solve and reducing possible domains.
				board[cur.x][cur.y] = i + 1;
				row[cur.x] ^= temp;
				col[cur.y] ^= temp;
				block[cur.z] ^= temp;
				if (rec(index + 1))
					return true;
				//if not solved, undoing changes.
				board[cur.x][cur.y] = 0;
				row[cur.x] |= temp;
				col[cur.y] |= temp;
				block[cur.z] |= temp;
			}
		}
		arr.get(minI).used = false;
		return false;
	}

	private class Square {
		public int x, y, z;
		public boolean used;

		public Square(int x, int y) {
			this.x = x;
			this.y = y;
			z = 3 * (x / 3) + y / 3;
			used = false;
		}

		public int getDomains() {
			return (block[z] & row[x] & col[y]);
		}
	}
}
