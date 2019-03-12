package floodit.solver.students.mkapa11;

import java.util.HashSet;
import java.util.Vector;

public class Way {
	Vector<Node> nodes;
	public int length = 0;
	public Vector<Integer> path;

	public Way() {
		nodes = new Vector<>();
		path = new Vector<>();
	}

	public Vector<Node> getNodes(){
		return nodes;
	}
	public void addNode(Node n){
		nodes.add(n);
		length  = length + 1;
		path.add(n.getColour());
	}

	public void addAllNode(Vector<Node> v){
		for (int i = 0; i < v.size(); i++) {
			nodes.add(v.get(i));
		}
		length = length + 1;
		path.add(v.get(0).getColour());
	}

	public int getNumberOfNodes(){
		return nodes.size();
	}
	
	public Vector<Node> getNeighbourNodes(){
		Vector<Node> neighbours = new Vector<>();
		HashSet<Node> set = new HashSet<>();
		for (int i = 0; i < nodes.size(); i++) {
			Vector<Node> neis = nodes.get(i).getNeighbours();
			for (int j = 0; j < neis.size(); j++) {
				Node tmp = neis.get(j);
				if(!nodes.contains(tmp) && !set.contains(tmp)){
					neighbours.add(tmp);
					set.add(tmp);
				}
			}
		}
		return neighbours;
	}

	public int Heuristic(Vector<Node> nodes1){
		int min = 1000000;
		for (int i = 0; i < nodes.size(); i++) {
			int k = nodes.get(i).heuristic;
			if(min > k){
				min = k;
			}
		}
		return min;
	}

}
