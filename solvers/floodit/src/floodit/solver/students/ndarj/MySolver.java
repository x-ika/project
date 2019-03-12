package floodit.solver.students.ndarj;

import floodit.solver.Solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;

public class MySolver implements Solver {
    public static final int START_ROW = 0;
    public static final int START_COL = 0;
    private int[][] board;

    
    private HashSet<Node> nodes = new HashSet<>();
    private HashSet<Cell> cells = new HashSet<>();

    public int[] solve(int[][] board) { // assume that size is min 1, 1
	this.board = board;
	createCells();
	createNodes();
	connectNodes();
	testNodes();
	setDistance();
	return solve();
    }

    

    private int[] solve() {
	Node startNode = Cell.getCell(START_ROW, START_COL).getParentNode();
	PriorityQueue<Path> pqueue = new PriorityQueue<>(100, new Comparator<>() {
        public int compare(Path p1, Path p2) {
            int result = p1.getWeight() - p2.getWeight();
            return result;
        }
    });
	
	Path path = new Path(startNode);
	pqueue.add(path);
	Path result = path;
	
	while(!pqueue.isEmpty()) {
	    Path currentPath = pqueue.poll();
	    if (currentPath.nNodes() == nodes.size()) {
		//		if (currentPath.getNetWeight() > 16)
		 //		    continue;
		result = currentPath;
		/*
		System.out.println("finished");
		System.out.println("printing:");
		ArrayList<Integer> colors = result.colors();
		System.out.println(colors.size());
		for (int i = 0; i < colors.size(); i++) {
		    System.out.print(colors.get(i) + " ");
		}
		System.out.println("");
		*/
		break;
	    }
	    HashSet<Integer> colors = currentPath.neighbourColors();
	    Iterator<Integer> itr = colors.iterator();
	    while(itr.hasNext()) {
		Integer i = itr.next();
		Path newPath = new Path(currentPath);
		newPath.changeColor(i);
		pqueue.add(newPath);
	    }
	}
	ArrayList<Integer> colors = result.colors();
	int[] arr = new int[colors.size()];
	for (int i = 0; i < colors.size(); i++) {
	    arr[i] = colors.get(i);
	}
	return arr;
    }
    


    private void createCells() {
	for (int i = 0; i < this.board.length; i++) {
	    for (int j = 0; j < this.board[0].length; j++) {
		this.cells.add(new Cell(i, j, board[i][j]));
	    }
	}
    }
    
    private void createNodes() {
	Iterator<Cell> itr = cells.iterator();
	HashSet<Cell> removed = new HashSet<>();
	while(itr.hasNext()) {
	    Cell cell = itr.next();
	    if (removed.contains(cell))
		continue;
	    Node node = new Node();
    	    node.setColor(cell.getInitialColor());
	    fillNode(node, cell, removed);
	    nodes.add(node);
	}
    }

    private void fillNode(Node node, Cell cell, HashSet<Cell> removed) {
	if (cell.getInitialColor() != node.getColor())
	    return;
	if (!removed.add(cell))
	    return;
	if (!node.addCell(cell))
	    return;
	
	for (int i = -1; i <= 1; i++) {
	    for (int j = -1; j <= 1; j++) {
		if (i != 0 && j != 0) // diagonals
		    continue;
		int newRow = cell.getRow() + i;
		int newCol = cell.getCol() + j;
		Cell newCell = Cell.getCell(newRow, newCol);
		if (newCell != null)
		    fillNode(node, newCell, removed);
	    }
	}
    }
    
    private void connectNodes() {
	Iterator<Cell> itr = cells.iterator();
	while(itr.hasNext()) {
	    Cell cell = itr.next();
	    Node node1 = cell.getParentNode();
	    for (int i = -1; i <= 1; i++) {
		for (int j = -1; j <= 1; j++) {
		    if (i != 0 && j != 0) // diagonals
			continue;
		    int newRow = cell.getRow() + i;
		    int newCol = cell.getCol() + j;
		    Cell neighbourCell = Cell.getCell(newRow, newCol);
		    if (neighbourCell != null) {
			Node node2 = neighbourCell.getParentNode();
			if (node1 != node2)
			    Node.makeNeighbours(node1, node2);
		    }
		}
	    }
	}
    }
    
    private void testNodes() {
	Iterator<Node> itr = nodes.iterator();
	while(itr.hasNext()) {
	    HashSet<Node> seen = new HashSet<>();
	    Node node = itr.next();
	    HashSet<Node> neighbours = node.getNeighbours();
	    HashSet<Cell> neighbourCells = node.getNeighbourCells();
	    Iterator<Cell> itr1 = neighbourCells.iterator();
	    while(itr1.hasNext()) {
		Cell cell = itr1.next();
		Node parent = cell.getParentNode();
		if (neighbours.contains(parent)) {
		    seen.add(parent);
		} else {
		    System.out.println("error");
		}
	    }
	    if (seen.size() != neighbours.size()) {
		System.out.println("error2");
	    }
	}
	
    }
    
    private void setDistance() {
	Node start = Cell.getCell(START_ROW, START_COL).getParentNode();
	Node farthest = start.findFarthest();

	HashSet<Node> seen = new HashSet<>();
	HashSet<Node> last = new HashSet<>();
	last.add(farthest);
	int n = 0;
	while(!last.isEmpty()) {
	    Iterator<Node> itr1 = last.iterator();
	    while(itr1.hasNext()) {
		Node node = itr1.next();
		node.setDistance(n);
	    }
	    n++;
	    seen.addAll(last);
	    
	    HashSet<Node> newLast = new HashSet<>();
	    Iterator<Node> itr = last.iterator();
	    while(itr.hasNext()) {
		Node node = itr.next();
		newLast.addAll(node.getNeighbours());
	    }
	    newLast.removeAll(seen);
	    last = newLast;
	}
	
	
    }

    private void check() {
	for (int i = 0; i < this.board.length; i++) {
	    for (int j = 0; j < this.board[0].length; j++) {
		System.out.print(this.board[i][j] + " ");
	    }
	    System.out.println("");
	}
	
	Iterator<Node> itr = nodes.iterator();
	while (itr.hasNext()) {
	    Node node = itr.next();
	    node.print();
	}
	
    }
    
    



}
