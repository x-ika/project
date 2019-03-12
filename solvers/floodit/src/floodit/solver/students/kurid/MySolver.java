package floodit.solver.students.kurid;

import floodit.solver.Solver;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class MySolver implements Solver {


	public int[] solve(int[][] board) {
		Board.createBoard(board);
		Board.createFragments();
		int differents = 0;
		for (int i = 0; i < board.length; i++)
			for (int j = 0; j < board[0].length; j++)
				if (board[i][j] > differents)
					differents = board[i][j];
		ArrayList<Fragment> s = new ArrayList<>();
		s.add(Board.fragments.get(0));
		Node start = new Node(s, Board.board[0][0].c, new int[0]);
		start.heuristics();
		Queue<Node> Q = new PriorityQueue<>();
		Q.add(start);
		int[] res;
		while (true) {
			Node tmp = Q.poll();
			if (tmp.h == 0) {
				res = tmp.moves;
				break;
			}
			for (int i = 0; i <= differents; i++) {
				if (i != tmp.color) {
					Node neighbor = tmp.colorIt(i);
					neighbor.heuristics();
					Q.add(neighbor);
				}
			}
		}
		return res;
	}
}
