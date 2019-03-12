package floodit.solver.students.lgure;

import floodit.solver.Solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class MySolver implements Solver {


	public int[] solve(int[][] board) {
        Node.generatedId = -1;
        Queue<Graph> queue = new PriorityQueue<>(11, new Comparator<>() {

            public int compare(Graph g1, Graph g2) {
                return g1.getfScore() - g2.getfScore();
            }
        });
		
		Graph initialGraph = new Graph(board);
		queue.add(initialGraph);
		Set<Graph> closedSet = new HashSet<>();
		Map<Graph,Graph> cameFrom = new HashMap<>();
//solving problem with A* algoritmh
		while (!queue.isEmpty()) {
			Graph current = queue.poll();
			if (current.goalIsReached()) {
				ArrayList<Integer> path = new ArrayList<>();
				///path.add(current.getColor());
				recontructPath(cameFrom, current, path);
				int[] p = new int[path.size()];
				for(int i = path.size()-1; i >=0; i--){
					p[path.size() - i - 1] = path.get(i);
				}
				System.out.println(p.length);
				return p;
			}
			closedSet.add(current);
			List<Graph> neighbors = current.getNeighbors();
			for (Graph neighbor: neighbors) {
				int tentativeGScore = current.getgScore() + 1;
				if (closedSet.contains(neighbor) && tentativeGScore >= neighbor.getgScore()) {
					continue;
				}
				if (!closedSet.contains(neighbor) || tentativeGScore < neighbor.getgScore()) {
					cameFrom.put(neighbor, current);
					neighbor.setgScore(tentativeGScore);
					neighbor.setfScore(neighbor.getgScore() + neighbor.getHeuristic());
					if (!queue.contains(neighbor)) {
						queue.add(neighbor);
					}
				}
			}
		}
		
		return null;
	}

	private void recontructPath(Map<Graph,Graph> cameFrom,Graph cur,ArrayList<Integer> path) {
		if(cameFrom.containsKey(cur)) {
			path.add(cur.getColor());
			recontructPath(cameFrom, cameFrom.get(cur), path);
		}else return;
	}
}
