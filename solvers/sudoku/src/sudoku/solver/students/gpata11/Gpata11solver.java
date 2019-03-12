package sudoku.solver.students.gpata11;


import java.util.Scanner;

import tester.Solver;

public class Gpata11solver implements Solver<int[][], int[][]>{

	@Override
	public int[][] solve(int[][] t) {
		Sudoku s = new Sudoku(t);
		s.solve();
		return Sudoku.textToGrid(s.getSolutionText());
	}
	public static void main(String args[]){
		Scanner sc = new Scanner(System.in);
		String cur = sc.next();
		StringBuilder b = new StringBuilder();
		for(int i=0;i<cur.length();i++){
			if(cur.charAt(i)!='.')
				b.append('0');
			else
				b.append(cur.charAt(i));
		}
		Sudoku s = new Sudoku(Sudoku.stringsToGrid(b.toString()));
	}
}
