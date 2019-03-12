package floodit.solver.students.vmask;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GraphGenerator {
	private static Node createNode(int color, Cell cell, int nodeNum) {
		Set<Cell> cells = new HashSet<>();
		cells.add(cell);
		return new Node(color, cells, new HashMap<>(), nodeNum);
	}

	private static void becomeNeighbours(Node n1, Node n2) {
		n1.addNeighbour(n2);
		n2.addNeighbour(n1);
	}

	private int width, height, nodeNum;
	private int[][] board;
	private Map<Cell, Node> cellsNodes;
	private Map<Integer, Set<Node> > nodes;

	public GraphGenerator(int[][] board) {
		nodeNum = 1;
		this.board = board;
		width = board.length;
		height = board[0].length;
		cellsNodes = new HashMap<>();
	}

	public Node getData(Map<Integer, Set<Node> > nodes) {
		this.nodes = nodes;
		Cell cell = new Cell(0, 0);
		Node node = createNode(board[0][0], cell, nodeNum++);
		cellsNodes.put(cell, node);
		construct(node, 0, 0);
		fillNodes();
		return node;
	}

	private void fillNodes() {
		for (Node n : cellsNodes.values()) {
			if (!nodes.containsKey(n.color))
				nodes.put(n.color, new HashSet<>());
			nodes.get(n.color).add(n);
		}
	}

	private void construct(Node node, int i, int j) {
		addCell(node, i + 1, j);
		addCell(node, i, j + 1);
	}

	private void addCell(Node node, int i, int j) {
		Cell cell = new Cell(i, j);
		if (i >= 0 && j >= 0 && i < width && j < height) {
			int cellColor = board[i][j];
			Node node2 = cellsNodes.get(cell);
			if (node2 == null) {
				if (node.color != cellColor) {
					Node neighbour = createNode(cellColor, cell, nodeNum++);
					becomeNeighbours(node, neighbour);
					cellsNodes.put(cell, neighbour);
					construct(neighbour, i, j);
				} else {
					node.cells.add(cell);
					cellsNodes.put(cell, node);
					construct(node, i, j);
				}
			} else {
				if (node.color != cellColor) {
					becomeNeighbours(node, node2);
				} else if (node != node2) { // union 2 nodes
					for (Set<Node> nnSet : node2.neighbours.values())
						for (Node neighbour : nnSet) {
							neighbour.neighbours.get(node.color).remove(node2);
							becomeNeighbours(node, neighbour);
						}
					for (Cell nCell : node2.cells)
						cellsNodes.put(nCell, node);
					node.cells.addAll(node2.cells);
				}
			}
		}
	}
}