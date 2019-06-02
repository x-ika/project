package floodit.solver.students.adoli;

import floodit.solver.Solver;

import java.util.ArrayList;
import java.util.HashMap;

public class MySolver implements Solver {
	public int[] solve(int[][] board) {
		Group.good = new ArrayList<>();
		Group.prevId = -1;
		return AStar.solve(new HashMap<>(), Graph.create(board));
	}
}