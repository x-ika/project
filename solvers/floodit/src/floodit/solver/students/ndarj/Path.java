package floodit.solver.students.ndarj;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

public class Path {
    
    ArrayList<Integer> colors = new ArrayList<>();
    ArrayList<HashSet<Node>> neighbours = new ArrayList<>();
    HashSet<Node> nodes = new HashSet<>();
    private int distance = 0;

    private void grow(ArrayList<HashSet<Node>> neighbours, int color) {
	while(color >= neighbours.size()) {
	    neighbours.add(new HashSet<>());
	}
    }

    public Path(Node startNode) {
	nodes.add(startNode);
	
	Iterator<Node> itr = startNode.getNeighbours().iterator();
	while (itr.hasNext()) {
	    Node node = itr.next();
	    grow(neighbours, node.getColor());
	    neighbours.get(node.getColor()).add(node);
	}
	distance = startNode.getDistance();
    }
    
    private Path() {

    }

    public Path(Path path) {
	this.colors = new ArrayList<>(path.colors);
	this.nodes = new HashSet<>(path.nodes);
	this.neighbours = new ArrayList<>();
	for (int i = 0; i < path.neighbours.size(); i++) {
	    this.neighbours.add(new HashSet<>(path.neighbours.get(i)));
	}
	this.distance = path.distance;
    }
    

    public void changeColor(int color) {
	colors.add(color);
	grow(neighbours, color);
	HashSet<Node> merged = neighbours.get(color);
	nodes.addAll(merged);
	Iterator<Node> itr = merged.iterator();
	while(itr.hasNext()) {
	    Node node = itr.next();
	    grow(neighbours, node.getColor());
	    Iterator<Node> itr1 = node.getNeighbours().iterator();
	    while(itr1.hasNext()) {
		Node node1 = itr1.next();
		node1.test();
		if (!nodes.contains(node1)) {
		    grow(neighbours, node1.getColor());
		    neighbours.get(node1.getColor()).add(node1);
		}
	    }
	}
	Iterator<Node> itr1 = merged.iterator();
	while(itr1.hasNext()) {
	    Node node = itr1.next();
	    if (this.distance > node.getDistance()) {
		this.distance = node.getDistance();
	    }
	}


	merged.clear();
    }

    public HashSet<Integer> neighbourColors() {
	HashSet<Integer> result = new HashSet<>();
	for (int i = 0; i < neighbours.size(); i++) {
	    if (neighbours.get(i).size() > 0) {
		result.add(i);
	    }
	}
	return result;
    }


    public int getWeight() {
	/*
	if (this.distance < 1) {
	    for (int i = 0; i < this.colors.size(); i++) {
		System.out.print(this.colors.get(i));
	    }
	    System.out.println("");
	}
	*/
	return this.colors.size() + this.distance;
    }

    public int getNetWeight() {
	return this.colors.size();
    }

    
    public int nNodes() {
	return nodes.size();
    }

    public HashSet<Node> nodes() {
	return this.nodes;
    }
    
    public ArrayList<Integer> colors() {
	return this.colors;
    }


}
