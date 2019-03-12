package floodit.solver.students.vmask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Path {
	public Set<Node> nodes;
	public List<Integer> moves;
	public int weight, heuristics;
	public Map<Integer, Set<Node> > leftNodes, neighbours;

	public Path(Map<Integer, Set<Node> > leftNodes, Map<Integer, Set<Node> > neighbours){
		this.nodes = new HashSet<>();
		this.moves = new ArrayList<>();
		this.leftNodes = leftNodes;
		this.neighbours = neighbours;
	}

	public Path(Path path) {
		this.nodes = new HashSet<>(path.nodes);
		this.moves = new ArrayList<>(path.moves);
		this.leftNodes = MySolver.copyNodesMap(path.leftNodes);
		this.neighbours = MySolver.copyNodesMap(path.neighbours);
	}

	public void addNeighbour(Node node) {
		if (!neighbours.containsKey(node.color))
			neighbours.put(node.color, new HashSet<>());
		neighbours.get(node.color).add(node);
	}

	public void removeNeighbour(Node node) {
		neighbours.get(node.color).remove(node);
		if (neighbours.get(node.color).isEmpty())
			neighbours.remove(node.color);
	}

	public void removeLeftNode(Node node) {
		leftNodes.get(node.color).remove(node);
		if (leftNodes.get(node.color).isEmpty())
			leftNodes.remove(node.color);
	}

	@Override
	public boolean equals(Object obj) {
		return nodes.equals(((Path) obj).nodes);
	}
}