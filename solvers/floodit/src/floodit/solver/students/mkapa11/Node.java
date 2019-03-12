package floodit.solver.students.mkapa11;

import java.util.Vector;

public class Node {
	private int colour;
	private Vector<Point> points;
	private Vector<Node> neighbours; 
	public int heuristic;
	
	public Node() {
		points = new Vector<>();
		neighbours = new Vector<>();
	}
	
	public void addPoint(Point t){
		points.add(t);
	}
	
	public void setColour(int colour){
		this.colour = colour;
	}
	
	public int getColour(){
		return colour;
	}
	
	public Vector<Point> getPoints(){
		return points;
	}
	
	public Vector<Node> getNeighbours(){
		return neighbours;
	}

	public void addNeighbourNode(Node n){
		neighbours.add(n);
	}
	
	
}

