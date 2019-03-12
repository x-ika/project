package floodit.solver.students.adoli;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Graph {
	public static Group[][] groups;
	
	public static class Point {
		public int x;
		public int y;
		
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
		
		public boolean equals(Object other) {
			Point another = (Point) other;
			return x == another.x && y == another.y;
		}
	}
	
	public static Group create(int[][] board) {
		ArrayList<Set<Point>> ref = new ArrayList<>(); // this is rubbish!!!
		
		for (int i=0; i<board.length * board[0].length+100000; i++)
			ref.add(new HashSet<>());
		
		board = prepare(board);
		
		for (int i=1; i<board.length; i++) {
			for (int j=1; j<board[0].length; j++) {
				int color = board[i][j];
				Group top = groups[i-1][j];
				Group left = groups[i][j-1];
				
				if (color == top.color && color == left.color) {
					if (left == top) {
						groups[i][j] = left;
						ref.get(left.id).add(new Point(i, j));
					} else {
						top.joinNeighbour(left);
						groups[i][j] = top;
						
						for (Point point: ref.get(left.id))
							groups[point.x][point.y] = top;
					}
				} else if (color != top.color && color != left.color) {
					groups[i][j] = new Group(color);
					ref.get(groups[i][j].id).add(new Point(i, j));
					
					top.addNeighbour(groups[i][j]);
					groups[i][j].addNeighbour(top);
					left.addNeighbour(groups[i][j]);
					groups[i][j].addNeighbour(left);
				} else if (color == top.color) {
					groups[i][j] = top;
					ref.get(top.id).add(new Point(i, j));
					
					left.addNeighbour(top);
					top.addNeighbour(left);
				} else {					
					groups[i][j] = left;
					ref.get(left.id).add(new Point(i, j));
					
					top.addNeighbour(left);
					left.addNeighbour(top);
				}
			}
		}
		
		return groups[1][1];
	}
	
	private static int[][] prepare(int[][] board) {
		int[][] res = new int[board.length+1][board[0].length+1];
		groups = new Group[board.length+1][board[0].length+1];
		Group dummy = new Group(-1);
		
		for (int i=1; i<res.length; i++)
			for (int j=1; j<res[0].length; j++)
				res[i][j] = board[i-1][j-1];
		
		for (int i=0; i<res.length; i++) {
			groups[i][0] = dummy;
			res[i][0] = -1;
		}
		
		for (int i=0; i<res[0].length; i++) {
			groups[0][i] = dummy;
			res[0][i] = -1;
		}
		
		return res;
	}
}
