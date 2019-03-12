package floodit.solver.students.ndarj;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class Node {
    private HashSet<Cell> connected = new HashSet<>();
    private HashSet<Node> neighbours = new HashSet<>();
    private int color;
    
    public Node(){
	
    }
    
    public void setColor(int color) {
	this.color = color;
    }

    public int getColor() {
	return this.color;
    }

    public boolean addCell(Cell cell) {
	boolean result = connected.add(cell);
	if (result) {
	    cell.setParentNode(this);
	}
	return result;
    }
    
    public void test() {
	Iterator<Node> itr = neighbours.iterator();
	while(itr.hasNext()) {
	    Node node = itr.next();
	    if (node.getColor() == this.getColor()) {
		System.out.println("eminemineminem");
	    }
	}
    }

			
    public HashSet<Node> getNeighbours() {
	return this.neighbours;
    }
    
    public static void makeNeighbours(Node node1, Node node2) {
	node1.neighbours.add(node2);
	node2.neighbours.add(node1);
    }

    
    public Boolean areNeighbours(Node node) {
	return this.neighbours.contains(node);
    }

    public Boolean containsCell(Cell cell) {
	return connected.contains(cell);
    }


    public void print() {
	System.out.println("size: " + this.size() + ", color: " + this.getColor());
	Iterator<Cell> itr = connected.iterator();
	
	while(itr.hasNext()) {
	    Cell cell = itr.next();
	    System.out.println(cell.getRow() + " " + cell.getCol() + " " + cell.getInitialColor());
	}
    }
    
    public int size() {
	return this.connected.size();
    }
    
    
    int distanceFromFarthest = 0;
    int distanceFromOrigin = 0;

    public int getDistance() {
	return this.distanceFromFarthest;
    }
    
    public void setDistance(int distance) {
	this.distanceFromFarthest = distance;
    }

    public Node findFarthest() {
	Queue<Node> queue = new LinkedList<>();
	queue.add(this);
	HashSet<Node> visited = new HashSet<>();
	Node result = null;
	while(!queue.isEmpty()) {
	    Node node = queue.poll();
	    result = node;
	    visited.add(node);
	    Iterator<Node> itr = node.getNeighbours().iterator();
	    while(itr.hasNext()) {
		Node nxt = itr.next();
		if (!visited.contains(nxt)) {
		    queue.add(nxt);
		}
	    }
	}
	return result;
    }

    
    public static int DistanceBetween(Node source, Node dest) {
	Queue<Node> queue = new LinkedList<>();
	source.distanceFromOrigin = 0;
	queue.add(source);
	HashSet<Node> visited = new HashSet<>();
	while(!queue.isEmpty()) {
	    Node node = queue.poll();
	    if (node == dest) {
		return node.distanceFromOrigin;
	    }
	    visited.add(node);
	    Iterator<Node> itr = node.getNeighbours().iterator();
	    while(itr.hasNext()) {
		Node nxt = itr.next();
		if (!visited.contains(nxt)) {
		    nxt.distanceFromOrigin = node.distanceFromOrigin + 1;
		    queue.add(nxt);
		}
	    }
	}
	System.out.println("aq ar unda mosuliyo");
	return -1;
    }
    

    public HashSet<Cell> getNeighbourCells() {
	HashSet<Cell> result = new HashSet<>();
	Iterator<Cell> itr = connected.iterator();
	while(itr.hasNext()) {
	    Cell cell = itr.next();
	    for (int i = -1; i <= 1; i++) {
		for (int j = -1; j <= 1; j++) {
		    if (i != 0 && j !=0)
			continue;
		    int newRow = cell.getRow() + i;
		    int newCol = cell.getCol() + j;
		    Cell neighbourCell = Cell.getCell(newRow, newCol);
		    if (neighbourCell != null) {
			if (!connected.contains(neighbourCell)) {
			    result.add(neighbourCell);
			}
		    }
		}
	    }
	}
	return result;
    }
    

}

