package sudoku.solver.students.Gmarg10Solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

import tester.Solver;

public class Gmarg10Solver implements Solver<int[][], int[][]> {
	private int[][] finalsolution;
	
    public int[][] solve(int[][] grid) {
    	Sudoku sudoku;
    	finalsolution = new int[9][9];
    	sudoku = new Sudoku(grid);
		
		sudoku.solveIt();
   	
    	return finalsolution;
    }

    class Sudoku{
    	public static final int SIZE = 9;  
    	public static final int PART = 3; 
    	private int[][] sol;
    	private boolean found;
    	private int[][] grid;
    	private ArrayList<Spot> spots;
    	private ArrayList<HashSet<Integer>> squares;
    
    	private class Spot {
    		private int row,col;

    		public Spot(int row, int col) {
    			this.row = row;
    			this.col = col;
    		}
    		
    		public void SetVal(int v){
    			grid[row][col] = v;
    		}
    		
    		public boolean IsGoodForSpot(int v){
    			int sqr = GetSquareIndex(row, col);
    			if (squares.get(sqr).contains(v)) return false;
    			
    			for (int i = 0; i < SIZE; i ++){
    				if(grid[i][col] == v) return false;
    				if(grid[row][i] == v) return false;
    			}
    			
    			return true;
    		}
    		
    		public ArrayList<Integer> GetGoodNumbers(){
    			ArrayList<Integer> res = new ArrayList<>();
    			for (int i = 0; i < SIZE; i++)
    				if (IsGoodForSpot(i + 1)) res.add(i + 1);
    						
    			return res;
    		}
    	}
    	
    	public Sudoku(int[][] ints) {
    		
    		grid = new int[ints.length][];
    		spots = new ArrayList<>();
    		squares = new ArrayList<>();
    		found = false;
    		sol = new int[9][9];
    		
    		for (int i = 0; i < SIZE; i++)
    			squares.add(new HashSet<>());
    		
    		for (int i = 0; i < ints.length; i++){
    			grid[i] = Arrays.copyOf(ints[i], ints[i].length);
    			
    			for(int j = 0; j < grid[i].length; j++){
    				if (ints[i][j] != 0) squares.get(GetSquareIndex(i, j)).add(ints[i][j]);
    				else {
    					Spot temp = new Spot(i, j);
    					temp.SetVal(ints[i][j]);
    					spots.add(temp);
    				}
    			}
    		}
    		Collections.sort(spots, new Comparator<>() {

                public int compare(Spot o1, Spot o2) {

                    return o1.GetGoodNumbers().size() - o2.GetGoodNumbers().size();
                }
            });
     	}
    	
    	private int GetSquareIndex(int row, int col){
    		if(row < 3)
    			if (col < 3) return 0;
    			else if (col < 6) return 1;
    			else return 2;
    		
    		if(row < 6)
    			if (col < 3) return 3;
    			else if (col < 6) return 4;
    			else return 5;
    		
    		if (col < 3) return 6;
    		else if (col < 6) return 7;
    				
    		return 8;
    	}
    	
  
    	private void solveIt(){
    		if (found) return;
    		
    		if (spots.size() == 0){
    			for (int i = 0; i < grid.length; i++)
    				finalsolution[i] = Arrays.copyOf(grid[i], 9);
    			
    			found = true;
    			return;
    		}
   
    		Collections.sort(spots, new Comparator<>() {

                public int compare(Spot o1, Spot o2) {

                    return o1.GetGoodNumbers().size() - o2.GetGoodNumbers().size();
                }
            });
    		
    		Spot temp = spots.get(0);
    		spots.remove(0);
    		ArrayList<Integer> goods = temp.GetGoodNumbers();

    		for (int i = 0; i < goods.size(); i++){
    			temp.SetVal(goods.get(i));
    			squares.get(GetSquareIndex(temp.row, temp.col)).add(goods.get(i));
    			solveIt();
    			squares.get(GetSquareIndex(temp.row, temp.col)).remove(goods.get(i));
    			
    		}
    		temp.SetVal(0);
    		spots.add(0, temp);
    	}
    	
     	

    }
    
}
