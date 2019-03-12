package sudoku.solver.students.gpata11;
import java.util.*;

import tester.Solver;

public class Sudoku{
	public static final int[][] myTest = Sudoku.stringsToGrid(
	"0 0 0 0 9 0 0 5 0", 
	"0 1 0 0 0 0 0 3 0", 
	"0 0 2 3 0 0 7 0 0", 
	"0 0 4 5 0 0 0 7 0", 
	"8 0 0 0 0 0 2 0 0", 
	"0 0 0 0 0 6 4 0 0", 
	"0 9 0 0 1 0 0 0 0", 
	"0 8 0 0 6 0 0 0 0", 
	"0 0 5 4 0 0 0 0 7" );
	
    public static final int SIZE = 9;  // size of the whole 9x9 puzzle
    public static final int PART = 3;  // size of each 3x3 part

	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row<rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
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
	private class Spot{
		private Set<Integer> s;
		private int value = 0;
		private int x,y;
		public Spot(int a,int x,int y){
			s = new HashSet<>();
			this.value = a;
			this.x = x;
			this.y = y;
			if(this.value==0){
				for(int i=1;i<=9;i++){
					s.add(i);
				}
			}
		}
		private int getValue(){
			return value;
		}
		private void setValue(int value){
			this.value = value;
		}
		private void possibilities(){
			s.clear();
			for(int i=1;i<=9;i++){
				s.add(i);
			}
			if(this.value==0){
				for(int i=(this.x/3)*3;i<(this.x/3+1)*3;i++){
					for(int j=(this.y/3)*3;j<(this.y/3+1)*3;j++){
						if(s.contains(Sudoku.this.grid[i][j].getValue()) && 
								(this.x!=i && this.y!=j)){
							s.remove(Sudoku.this.grid[i][j].value);
						}
					}
				}
				for(int i=0;i<Sudoku.SIZE;i++){
					if(s.contains(Sudoku.this.grid[i][this.y].value) && i!=this.x){
						s.remove(Sudoku.this.grid[i][this.y].value);
					}
				}
				for(int j=0;j<Sudoku.SIZE;j++){
					if(s.contains(Sudoku.this.grid[this.x][j].value) && j!=this.y){
						s.remove(Sudoku.this.grid[this.x][j].value);
					}
				}
			}
			else
				s.clear();
		}
		public Iterator<Integer> getPossibilities(){
			return s.iterator();
		}
	}
	private Spot [][] grid;
	private List<Spot> l;
	private long elapsed = 0;
	private String solution = "";
	
	public static void main(String[] args) {
		Sudoku sudoku;
		sudoku = new Sudoku(myTest);
		
		System.out.println(sudoku); // print the raw problem
		int count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
	}
	public Sudoku(int[][] ints){
		grid = new Spot [SIZE][SIZE];
		l = new ArrayList<>();
		for(int i = 0;i<ints.length;i++){
			for(int j=0;j<ints[0].length;j++){
				grid[i][j] = new Spot(ints[i][j],i,j);
			}
		}
		countPossibilities();
		for(int i = 0;i<ints.length;i++){
			for(int j=0;j<ints[0].length;j++){
				l.add(grid[i][j]);
			}
		}
		sort();
	}
	//second constructor for soduko
	public Sudoku(String text){
		this(textToGrid(text));
	}
	
	private void sort(){
		Collections.sort(l, new Comparator<>() {
            public int compare(Spot o1, Spot o2) {
                return o1.s.size() - o2.s.size();
            }
        });
	}
	private void countPossibilities(){
		for(int i=0;i<grid.length;i++){
			for(int j=0;j<grid[0].length;j++){
				grid[i][j].possibilities();
			}
		}
	}
	
	public int solve() {
		long first = System.currentTimeMillis();
		int ans = solveSudoku(0);
		elapsed = System.currentTimeMillis() - first;
		return ans;
	}
	// recursive method for solvin sudoku
	private int solveSudoku(int j){
		int ans = 0;
		if(j==l.size()-1){
			if(l.get(j).s.size()==1){
				int a = l.get(j).getValue();
				Iterator<Integer> it = l.get(j).getPossibilities();
				l.get(j).setValue(it.next());
				solution = toString();
				l.get(j).setValue(a);
			}
			return l.get(j).s.size();
		}
		if(l.get(j).s.size()==0 && l.get(j).getValue()==0 || (j == l.size()-1))
			return 0;
		if(l.get(j).s.size()>0){
			Iterator<Integer> it = l.get(j).getPossibilities();
			while(it.hasNext()){
				l.get(j).setValue(it.next());
				l.get(j+1).possibilities();
				ans += solveSudoku(j+1);
				l.get(j).setValue(0);
				l.get(j+1).possibilities();
			}
		}
		else if(l.get(j).getValue()>0){
			return solveSudoku(j+1);
		}
		return ans;
	}
	public String getSolutionText() {
		return solution;
	}
	public long getElapsed() {
		return elapsed;
	}
	public String toString(){
		StringBuilder b = new StringBuilder();
		for(int i = 0;i<this.grid.length;i++){
			for(int j = 0;j<this.grid[0].length;j++){
				b.append(grid[i][j].getValue());
				b.append(" ");
			}
			b.append('\n');
		}
		return b.toString();
	}
}