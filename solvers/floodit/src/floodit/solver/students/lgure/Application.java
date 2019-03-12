package floodit.solver.students.lgure;

import floodit.solver.Solver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Application{
	private static int[][] initialData;
	
	public static void main(String[] args) throws IOException {
		readData();
		Solver solver = new MySolver();
		int[] path = solver.solve(initialData);
		for(int n : path) {
			System.out.print(n + " ");
		}
	}
	
	private static void readData() throws IOException {
		BufferedReader f = new BufferedReader(new FileReader("resources/005_12-12-4.txt"));
		String[] line = f.readLine().split(" ");
		int n = Integer.parseInt(line[0]);
		int m = Integer.parseInt(line[1]);
		initialData = new int[n][m];
		for(int i = 0; i < n; i++) {
			line = f.readLine().split(" ");
			for ( int j = 0; j < m; j++) {
				initialData[i][j] = Integer.parseInt(line[j]);
			}
		}
		f.close();
	}
	
}
