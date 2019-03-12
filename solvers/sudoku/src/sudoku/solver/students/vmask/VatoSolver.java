package sudoku.solver.students.vmask;

import java.util.Arrays;

import tester.Solver;

public class VatoSolver implements Solver<int[][], int[][]> {
	public static final int GRID_SIZE = 9, // size of the whole 9x9 puzzle
		EMPTY_VAL = 0;

	private int[][] grid;
	
	private Cell usedCells[];
	private int usedCellsSize;
	
	private int[] rowConstraints,
		colConstraints,
		boxConstraints;
	
	/**
	 * Takes parameter grid of assigned integers solves
	 * sudoku and returns right answer.
	 * @param sudoku - grid of integers
	 * @return solved sudoku
	 */
	@Override
	public int[][] solve(int[][] sudoku) {
		initData(sudoku);
		AC3();
		recSolve(0);
		return grid;
	}

	// makes rows, columns and variable cell grids
	private void initData(int[][] sudoku) {
		grid = new int[GRID_SIZE][GRID_SIZE];
		for (int i = 0; i < GRID_SIZE; i++)
			grid[i] = Arrays.copyOf(sudoku[i], GRID_SIZE);
		rowConstraints = new int[GRID_SIZE];
		colConstraints = new int[GRID_SIZE];
		boxConstraints = new int[GRID_SIZE];
		usedCells = new Cell[GRID_SIZE * GRID_SIZE];
		usedCellsSize = 0;
		for (int i = 0; i < GRID_SIZE; i++)
			for (int j = 0; j < GRID_SIZE; j++)
				if (grid[i][j] == EMPTY_VAL)
					usedCells[usedCellsSize++] = new Cell(i, j);
				else
					setConstraint(i, j, 3 * (i / 3) + (j / 3), grid[i][j]);
	}

	// if any of cell's assignments is 0, unassignes current value
	private void reduceDomain(Cell curCell, int val) {
		for (int k = 0; k < usedCellsSize; k++)
			if (usedCells[k].canUse) { // check if this isn't current cell
				if (Integer.bitCount(usedCells[k].computeAssignments()) == 0)
					curCell.assignments &= (~(1 << val));
					return;
				}
	}
	
	// for every cell assigns one of possible assignments to it and reduces it's domain
	private void AC3() {
		for (int i = 0; i < usedCellsSize; i++) {
			Cell cell = usedCells[i];
			cell.canUse = false;
			cell.computeAssignments();
			for (int val = 1; val <= GRID_SIZE; val++)
				if ((cell.assignments & (1 << val)) == 0) { // check if this value can be assigned
					cell.set(val);
					reduceDomain(cell, val);
					cell.removeConstraint(val);
				}
			cell.canUse = true;
		}
	}
	
	private class Cell {
		private int row,
			col,
			boxPos,
			assignments; // cell's all possible assignments
		public boolean canUse;

		// initializes cell
		public Cell(int row, int col) {
			this.row = row;
			this.col = col;
			boxPos = 3 * (row / 3) + (col / 3);
			canUse = true;
		}

		// computes all possible assignment values for cell
		public int computeAssignments() {
			assignments = rowConstraints[row] | colConstraints[col] | boxConstraints[boxPos];
			return assignments;
		}

		// assigns input value to grid and sets it as constraint
		public void set(int val) {
			setConstraint(row, col, boxPos, val);
			grid[row][col] = val;
		}
		
		// removes input value constraint
		public void removeConstraint(int val) {
			rowConstraints[row] &= (~(1 << val));
			colConstraints[col] &= (~(1 << val));
			boxConstraints[boxPos] &= (~(1 << val));
			grid[row][col] = 0;
		}
	}

	// solves sudoku recursively
	private boolean recSolve(int pos) {
		if (pos >= usedCellsSize)
			return true;
		int bestAssign = 0, p = 0;
		for (int i = 0; i < usedCellsSize; i++)
			if (usedCells[i].canUse) {
				int size = Integer.bitCount(usedCells[i].computeAssignments());
				if (size > bestAssign) {
					bestAssign = size;
					p = i;
				}			
			}
		Cell cell = usedCells[p];
		cell.canUse = false;
		for (int val = 1; val <= GRID_SIZE; val++)
			if ((cell.assignments & (1 << val)) == 0) { // check if this value can be assigned
				cell.set(val);
				if (recSolve(pos + 1))
					return true;
				cell.removeConstraint(val);
			}
		cell.canUse = true;
		return false;
	}
	
	private void setConstraint(int row, int col, int boxPos, int val) {
		rowConstraints[row] |= (1 << val);
		colConstraints[col] |= (1 << val);
		boxConstraints[boxPos] |= (1 << val);		
	}
}