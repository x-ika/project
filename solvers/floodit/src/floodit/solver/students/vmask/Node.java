package floodit.solver.students.vmask;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Node {
	public Set<Cell> cells;
	public int color, nodeNum;
	public Map<Integer, Set<Node> > neighbours;

	public Node(int color, Set<Cell> cells, Map<Integer, Set<Node> > neighbours, int nodeNum) {
		this.color = color;
		this.cells = cells;
		this.neighbours = neighbours;
		this.nodeNum = nodeNum;
	}

	public void addNeighbour(Node node) {
		if (!neighbours.containsKey(node.color))
			neighbours.put(node.color, new HashSet<>());
		neighbours.get(node.color).add(node);
	}

	@Override
	public boolean equals(Object obj) {
		return nodeNum == ((Node) obj).nodeNum;
	}
}