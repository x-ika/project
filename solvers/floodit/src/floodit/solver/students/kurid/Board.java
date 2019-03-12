package floodit.solver.students.kurid;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class Board {
	public static Point[][] board;
	public static int h;
	public static int l;
	public static ArrayList<Fragment> fragments;
	public static Map<Fragment,Integer> to;

	public static void createBoard(int[][] board) {
		h = board.length;
		l = board[0].length;
		Board.board = new Point[h][l];
		for (int i = 0; i < h; i++)
			for (int j = 0; j < l; j++) {
				Board.board[i][j] = new Point(i, j, board[i][j]);
			}
	}

	public static void createFragments() {
		boolean[][] isVisited = new boolean[h][l];
		fragments = new ArrayList<>();
		Queue<Point> Q = new ArrayDeque<>();
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < l; j++) {
				if (!isVisited[i][j]) {
					Fragment cur = new Fragment();
					isVisited[i][j] = true;
					Q.add(board[i][j]);
					Point tmp;
					while (Q.size() > 0) {
						tmp = Q.poll();
						cur.points.add(tmp);
						tmp.setFragment(cur);
						if (tmp.i > 0) {
							if (!isVisited[tmp.i - 1][tmp.j]
									&& tmp.c == board[tmp.i - 1][tmp.j].c) {
								isVisited[tmp.i - 1][tmp.j] = true;
								Q.add(board[tmp.i - 1][tmp.j]);
							}
						}
						if (tmp.i < h - 1) {
							if (!isVisited[tmp.i + 1][tmp.j]
									&& tmp.c == board[tmp.i + 1][tmp.j].c) {
								isVisited[tmp.i + 1][tmp.j] = true;
								Q.add(board[tmp.i + 1][tmp.j]);
							}
						}
						if (tmp.j > 0) {
							if (!isVisited[tmp.i][tmp.j - 1]
									&& tmp.c == board[tmp.i][tmp.j - 1].c) {
								isVisited[tmp.i][tmp.j-1] = true;
								Q.add(board[tmp.i][tmp.j-1]);
							}
						}
						if (tmp.j < l - 1) {
							if (!isVisited[tmp.i][tmp.j + 1]
									&& tmp.c == board[tmp.i][tmp.j + 1].c) {
								isVisited[tmp.i][tmp.j+1] = true;
								Q.add(board[tmp.i][tmp.j+1]);
							}
						}
					} // end of while
					fragments.add(cur);
				}//end of if
			}//end of j
		}//end of i
		
		for (Fragment fr: fragments){
			for (Point p: fr.points){
				Point tmp;
				if (p.i>0){
					tmp = board[p.i-1][p.j]; 
					if (tmp.fragment != fr){
						if (!fr.neibs.contains(tmp.fragment)){
							fr.neibs.add(tmp.fragment);
							tmp.fragment.neibs.add(fr);
						}
					}
				}
				if (p.i<h-1){
					tmp = board[p.i+1][p.j]; 
					if (tmp.fragment != fr){
						if (!fr.neibs.contains(tmp.fragment)){
							fr.neibs.add(tmp.fragment);
							tmp.fragment.neibs.add(fr);
						}
					}
				}
				if (p.j>0){
					tmp = board[p.i][p.j-1]; 
					if (tmp.fragment != fr){
						if (!fr.neibs.contains(tmp.fragment)){
							fr.neibs.add(tmp.fragment);
							tmp.fragment.neibs.add(fr);
						}
					}
				}
				if (p.j<l-1){
					tmp = board[p.i][p.j+1]; 
					if (tmp.fragment != fr){
						if (!fr.neibs.contains(tmp.fragment)){
							fr.neibs.add(tmp.fragment);
							tmp.fragment.neibs.add(fr);
						}
					}
				}
			} //end of p
		}//end of fr
		
		to = new HashMap<>();
		for (int i=0; i<Board.fragments.size(); i++)
			to.put(Board.fragments.get(i), i);
	}
}
