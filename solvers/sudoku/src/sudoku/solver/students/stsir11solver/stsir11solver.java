package sudoku.solver.students.stsir11solver;

import java.util.*;


import tester.Solver;


/*
 * Encapsulates a Sudoku grid to be solved.
 * 
 */
public class stsir11solver implements Solver<int[][], int[][]> {

    
	
	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	
	private Spot [][] spots; //grid of spots were we do all our stuff
	private int[][] solution = null;
	
	//we give sudoku grid to this method and it returns correct answer of it
	public int[][] solve(int[][] sudoku) {
		
		spots = new Spot[sudoku.length][sudoku[0].length];
		for(int i = 0; i < sudoku.length; i++){
			for(int j = 0; j < sudoku[0].length; j++){
				int val = sudoku[i][j];
				Spot  s = new Spot(i, j, val);
				spots[i][j] = s;
			}
		}
		getSolution();
        return solution;
    }
	
	/**
	 * This method checks if all the spots are filled with non-0 numbers.
	 * It means that we have solution and returns true;
	 */
	private boolean weHaveSolution(){
		for(int i = 0; i < spots.length; i++){
			for(int j = 0; j < spots[0].length; j++){
				if(spots[i][j].getValue() == 0)return false;
			}
		}
		return true;
	}
	
	/**
	 * Recursion method for finding number of solutions.
	 */
	private void getSolution() {
		if(solution != null) return;
		if(weHaveSolution()){
			if(solution == null) solution = spotsToGrid(spots);
			return;
		}
		
		Spot curr = getFirst();
		Set<Integer> currSet = curr.getSet();
		if(currSet.size() == 9) return;
		for(int j = 1; j <= 9; j++){	
			if(!currSet.contains(j)){	
				curr.set(j);	
				getSolution();
				if(solution != null)return;
				curr.set(0);//backtracking
			}
		}
		
	}
	
	/**
	 * takes spots grid and writes it to the integer grid and returns
	 */
	private int[][] spotsToGrid(Spot[][] spots2) {
		int[][] res = new int[SIZE][SIZE];
		for(int i = 0; i < spots.length; i++){
			for(int j = 0; j < spots[0].length; j++){
				res[i][j] = spots[i][j].value;
			}
		}
		return res;
	}

	

	/**
	 * Finds the Spot that is easiest to fill (it means has the less 
	 * variants to write in.
	 */
	private Spot getFirst(){
		Spot res = null;
		for(int i = 0; i < spots.length; i++){
			for(int j = 0; j < spots[0].length; j++){
				if(spots[i][j].getValue() == 0){
					if(res == null)res = spots[i][j];
					Spot curr = spots[i][j];
					if( (new MyComparator().compare(res, curr) > 0))
						res = curr;
				}
			}
		}
		return res;
	}

	/**
	 * Comparison method for two spots, compares them according to the length of there sets of used numbers
	 */
	private class MyComparator implements Comparator<Spot>{
		
		public int compare(Spot o1, Spot o2) {
			return  o2.getSet().size() - o1.getSet().size();
		}
	}


	/**
	 * Inner class Spot, representing each point of the sudoku square.
	 */
	private class Spot{ //inner class
		
		private int x;
		private int y;
		private int value;
		
		
		public Spot(int x, int y, int value){
			this.x = x;
			this.y = y;
			set(value);
		}
		
		public Set<Integer> getSet(){
			Set<Integer> used = new HashSet<>();
			fillSet(used);
			return used;
		}
		
		public int getValue(){
			return value;
		}
		
		public void set(int value){
			this.value = value;
		}
		
		

		/**
		 * This method gives us a set of numbers that could not be used more for this current spot.
		 */
		public void fillSet(Set<Integer> used){
			
			for(int i = 0; i < spots.length; i++){
				if(spots[i][y].value != 0) used.add(spots[i][y].value);
			}
			for(int i = 0; i < spots[0].length; i++){
				if(spots[x][i].value != 0) used.add(spots[x][i].value);
			}
			
			int coordX = x - (x % PART);
			int coordY = y - ( y % PART);
				
			for(int i = 0; i < PART; i++){
				for(int j = 0; j < PART; j++){
					if(spots[coordX + i][coordY + j].value != 0) used.add(spots[coordX + i][coordY + j].value);
				}
			}
		}
	}
}
