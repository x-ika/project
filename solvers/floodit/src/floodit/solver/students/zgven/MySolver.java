package floodit.solver.students.zgven;

import floodit.solver.Solver;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;


public class MySolver implements Solver {
	
	private static String filename = "resources/000_12-12-4.txt";
	private static long startTime;
	
	private ArrayList<Integer> solveProcess(PriorityQueue<Config> pq, Set<Config> used) {
		startTime = System.currentTimeMillis();
		while (!pq.isEmpty()) {
			Config current = pq.poll();
			if (current.getComponents().size() == 1) return current.getCameColors();
			ArrayList<Config> moves = current.getMoves();
			for (int i = 0; i < moves.size(); i++) {
				if (!alreadyUsed(used, moves.get(i))) {
					pq.add(moves.get(i));
					used.add(moves.get(i));
				}
			}
		}
		return null;
	}
	
	private boolean alreadyUsed(Set<Config> used, Config config) {
		for (Config current: used) {
			if (current.getCameColors().size() == config.getCameColors().size()
					&& config.getComponents().keySet().containsAll(current.getComponents().keySet())) return true;
		}
		return false;
	}

	public int [] solve(int[][] board) {
		Config startConfig = new Config(board, 0);
		Set<Config> used = new HashSet<>();
		PriorityQueue<Config> pq = new PriorityQueue<>(10, new Comparator<>() {
            public int compare(Config c1, Config c2) {
                return c1.getFx() - c2.getFx();
            }
        });
		used.add(startConfig);
		pq.add(startConfig);
		ArrayList<Integer> list = solveProcess(pq,used);
		int [] result = new int[list.size()];
		for (int i = 0; i < list.size(); i++) result[i] = list.get(i);
		return result;
	}
	
	public static int [][] readData() {
		int[][] board = null;
		try {
			Scanner sc = new Scanner(new File(filename));
			int row = sc.nextInt();
			int column = sc.nextInt();
			board = new int [row][column];
			for (int i = 0; i < row; i++)
				for (int j = 0; j < column; j++)
					board[i][j] = sc.nextInt();
			sc.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return board;
	}
	
	public static void printAnswer(int[] result) {
		System.out.println(filename);
		System.out.println(result.length + " " + (System.currentTimeMillis()-startTime)/1000);
		for (int i = 0; i < result.length; i++) System.out.print(result[i] + " ");
	}
	
	
	public static void main(String[] args) {
		int [][] board = readData();
		printAnswer(new MySolver().solve(board));
	}

}
