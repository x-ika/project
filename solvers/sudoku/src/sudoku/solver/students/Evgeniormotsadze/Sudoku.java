package sudoku.solver.students.Evgeniormotsadze;

import tester.Solver;

public  class Sudoku implements Solver<int[][], int[][]> {

	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	

	private int[][] grid = new int[SIZE][SIZE];
	private int[][] cpy  = new int[SIZE][SIZE];
	private int count = 0;
	
	private  String firstSolution = "";
	

public int [][] solve(int[][] ints) {	
		for(int i = 0; i < SIZE; i++)
			for(int j = 0; j < SIZE; j++)
				grid[i][j] = ints[i][j];	
		cpyGrid();
		solveRecurs(0,0);	
		cpy= textToGrid(firstSolution);
		return cpy;
	}
	
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


/*
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



 

//then copy information which we have to another grid becase in recurion we need to change our grid
private  void cpyGrid(){
	for(int i = 0; i < SIZE; i++)
		for(int j = 0; j < SIZE; j++)
			cpy[i][j] = grid[i][j];
}



private void solveRecurs(int row, int col){
	if(count >= 1) return;  // first base case if solution find;
	if(row == 0 && col ==9){    
		firstSolution = firstSolution(); // i save this solution becase down i use backtracking and i willnot show infromation about solution
		count++;
		return ;
	}
	int dx = 0; int dy = col; // at start row start for 0 and colum what we have 
	if(row == SIZE-1)dy++;		// when current row already find then go next colum 
	else dx = row + 1;			// in other case row go to niobour row 
	if(cpy[row][col] != 0){  // case which point we dont want and this case we call neibor point 
		solveRecurs(dx, dy); 
	}else{
		for(int i = 1; i <= SIZE; i++){		// to choose which number are suite
			if(checkSudoku(row, col, i)){
				cpy[row][col] = i;
				solveRecurs(dx, dy);   // backtracking to return old meaning this point
				cpy[row][col] = 0;
			}
		}
	}	
}


private boolean checkSudoku(int row, int col, int num) {	
	return checkCol(col,num) && checkRow(row,num) && checkBox((row/3) *3, (col/3) *3, num);
}

private boolean checkRow(int row,  int num){
	for(int col = 0; col < SIZE; col++)
		if(cpy[row][col] == num) return false;
	return true;
}

private boolean checkCol(int col, int num){
	for(int row = 0; row < SIZE; row++)
		if(cpy[row][col] == num) return false;
	return true;
}

private boolean checkBox(int row, int col, int num){	
	for(int i = 0; i < PART; i++)
		for(int j = 0; j < PART; j++)
			if(cpy[row+i][col+j] == num) return false;
	return true;
}



private String firstSolution(){
	String tmp = "";
	for(int i = 0; i < SIZE; i++){
		for(int j = 0; j < SIZE; j++){
			tmp += " " +cpy[i][j];
		}
		tmp += "\n";
	}
	return tmp;
}





	
}