package sudoku.solver.students.lashagureshidzesudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import tester.Solver;

public class LashasSolver implements Solver<int[][], int[][]>{
	
	private int[][] grid;
	
	private List<Spot> sortedSpots;
	
	private boolean ret = false;

	@Override
	public int[][] solve(int[][] t) {
		this.grid = t;
		this.sortedSpots = new ArrayList<>();
		constructSpotsArray(this.grid,sortedSpots);
		
		List<Spot> spots = sortedSpots;
		rec(spots);
		return this.grid;
	}
	
	
	/**
	 *recursion, that solves sudoku problem
	 */
	private void rec(List<Spot> spots) {
		if(spots.size() == 0){					
			ret = true;
			return ;
		}else{
 			Spot sp  = spots.get(0);
			List<Integer> assValues = computeAssignableValues(sp);
			for(int i = 0; i < assValues.size();i++){
				sp.setValue(assValues.get(i));
				List<Spot> spots1 = new ArrayList<>();
				constructSpotsArray(this.grid,spots1);     //computes new sortedSpots array
				rec(spots1);
				if(ret) return;
				sp.setValue(0);
			}
		}
	}
	
/**
 * creates 81 Spot type objects. then sorts this object into "sortedSpots" Array, whith spot.getAssValCount()
 */
	private void constructSpotsArray(int[][] grid,List<Spot> sortedSpots){
		for(int i=0;i<9;i++){
			for(int j=0;j<9;j++){
				if(grid[i][j] == 0){
					Spot sp = new Spot();
					sp.setValue(grid[i][j]);
					sp.setRow(i);
					sp.setCol(j);
					sp.setAssValCount(computeAssignableValCount(sp));
					sortedSpots.add(sp);
				} 
			}
		}
		
		Collections.sort(sortedSpots, new Comparator<>() {
            public int compare(Spot o1, Spot o2) {
                return o1.getAssValCount() - o2.getAssValCount();
            }
        });
	}
// computes which number are assignable for "sp"
	private List<Integer> computeAssignableValues(Spot sp){
		List<Integer> list = new ArrayList<>();
		for(int v = 1; v <= 9;v++){
			if(!isInRow(v,sp) && !isInCol(v,sp) && !isInBlock(v,sp)){
				list.add(v);
			}
		}
		return list;
	}
	
	
/**
 * computes assignable values count for Spot parameter
 */
	private int computeAssignableValCount(Spot sp){
		int count = 0;
		for(int v = 1; v <= 9;v++){
			if(!isInRow(v,sp) && !isInCol(v,sp) && !isInBlock(v,sp)){
				count++;
			}
		}
		return count;
	}
	
	private boolean isInRow(int v, Spot sp){
		for(int i = 0 ; i < 9;i++){
			if(grid[sp.getRow()][i] == v) return true;
		}
		return false;
	}
	
	private boolean isInCol(int v, Spot sp){
		for(int i = 0;i <9;i++){
			if(grid[i][sp.getCol()] == v) return true;
		}
		return false;
	}
	
	private boolean isInBlock(int v,Spot sp){
		int blockX = (sp.getRow() / 3) * 3;	
		int blockY = (sp.getCol() / 3) * 3;
		for(int i = blockX;i < blockX + 3; i++){
			for(int j = blockY;j < blockY + 3;j++){
				if(grid[i][j] == v) return true;
			}
		}
		return false;
	}


	
	/**
	 * inner class of Sudoku. Representing each cube on board
	 * Spot , itself contains information about where it is. 
	 * @author lasha.gureshidze
	 *
	 */
		private class Spot{
			private int value;
			
			private int row = -1;
			
			private int col = -1;
			//assignableValus Count. 
			private int assValCount;
			
			private Spot() {
				
			}
			private int getValue() {
				return value;
			}
			private void setValue(int value) {
				this.value = value;
				if(row != -1 && col != -1) grid[row][col] = value;
			}
			private int getRow() {
				return row;
			}
			private void setRow(int row) {
				this.row = row;
			}
			private int getCol() {
				return col;
			}
			private void setCol(int col) {
				this.col = col;
			}
			private int getAssValCount() {
				return assValCount;
			}
			private void setAssValCount(int assValCount) {
				this.assValCount = assValCount;
			}
		}

}
