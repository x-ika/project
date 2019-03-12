package floodit.solver.students.vmask;

import floodit.solver.Solver;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class MySolver implements Solver {
	private Map<Integer, Set<Node> > nodes;

	private static int[] convertToArray(List<Integer> list) {
		int[] res = new int[list.size()];
		for (int i = 0; i < list.size(); i++)
			res[i] = list.get(i);
		return res;
	}

	public static Map<Integer, Set<Node> > copyNodesMap(Map<Integer, Set<Node> > map) {
		Map<Integer, Set<Node> > res = new HashMap<>(map);
		for (int color : map.keySet())
			res.put(color, new HashSet<>(map.get(color)));
		return res;
	}

	public int[] solve(int[][] board) { // assume that size is min 1, 1
		nodes = new HashMap<>();
		int[] res = AStar(new GraphGenerator(board).getData(nodes));
		return res;
	}

	private int getStepCount(Path path) {
		Queue<Road> agenda = new LinkedList<>();
		Set<Node> nodes = new HashSet<>(path.nodes);
		Road road = new Road(path.neighbours, 0);
		agenda.add(road);
		while (!agenda.isEmpty()) {
			road = agenda.poll();
			for (Set<Node> nNodes : road.neighbours.values())
				for (Node neighbour : nNodes)
					if (!nodes.contains(neighbour)) {
						agenda.add(new Road(neighbour.neighbours, road.soFar + 1));
						nodes.add(neighbour);
					}
		}
		return road.soFar;
	}

	private boolean alreadyContains(Set<Path> paths, Path path) {
		for (Path p : paths)
			if (p.moves.size() == path.moves.size() && p.nodes.containsAll(path.nodes))
				return true;
		return false;
	}

	private int[] AStar(Node node) {
		PriorityQueue<Path> agenda = new PriorityQueue<>(20, new Comparator<>() {
            public int compare(Path p1, Path p2) {
                int res = p1.weight - p2.weight;
                if (res == 0)
                    return p1.heuristics - p2.heuristics;
                return res;
            }
        });
		Path path = new Path(copyNodesMap(nodes), copyNodesMap(node.neighbours));
		path.nodes.add(node);
		path.removeLeftNode(node);

		agenda.add(path);

		Set<Path> paths = new HashSet<>();
		paths.add(path);
		while (!agenda.isEmpty()) {
			path = agenda.poll();

			paths.remove(path);
			
			if (path.leftNodes.size() == 0)
				return convertToArray(path.moves);
			for (int color : path.neighbours.keySet()) {
				Set<Node> nSet = path.neighbours.get(color);
				Path newPath = new Path(path);
				for (Node neighbour : nSet) {
					newPath.removeLeftNode(neighbour);
					newPath.removeNeighbour(neighbour);
					for (Set<Node> nnSet : neighbour.neighbours.values())
						for (Node nNeighbour : nnSet)
							if (!newPath.nodes.contains(nNeighbour))
								newPath.addNeighbour(nNeighbour);
					newPath.nodes.add(neighbour);
				}
				newPath.moves.add(color);
				
				if (alreadyContains(paths, newPath))
					continue;
				paths.add(newPath);

//				newPath.heuristics = getStepCount(newPath);
				newPath.heuristics = Math.max(newPath.leftNodes.size(), getStepCount(newPath));
				newPath.weight = newPath.moves.size() + newPath.heuristics;

				agenda.add(newPath);
			}
		}
		return null;
	}
}