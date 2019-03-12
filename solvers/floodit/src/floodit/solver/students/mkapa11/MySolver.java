package floodit.solver.students.mkapa11;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

import floodit.solver.Solver;

public class MySolver implements Solver{

	private Vector<Node> nodes = new Vector<>();
	private Node root;
	private int numOfNodes;
	private Way optimalWay;
	private Point lastPoint;

	private int diff(int a, int b){
		if(a>=b) return a-b;
		if(b>a) return b-a;
		return 0;
	}

	private boolean isNeighbourPoints(Point one, Point two){
		boolean res = false;
		if(diff(one.x, two.x) == 1 && diff(one.y, two.y) ==0) res = true;
		if(diff(one.x, two.x) ==0 && diff(one.y, two.y) ==1) res = true;
		return res;
	}

	private void splitIntoNodes(int[][] board){
		for(int i=0; i < board.length; i++){
			for(int j=0; j < board[i].length; j++){
				Point p = new Point(i, j);
				int colour = board[i][j];
				Node tmp = neighbourWithSameColour(p, colour);
				if(tmp != null){
					tmp.addPoint(p);
				}else{
					Node n = new Node();
					n.setColour(colour);
					n.addPoint(p);
					nodes.add(n);
				}
			}
		}
	}


	private Node join(Vector<Integer> tmp, Point p) {
		Node res = new Node();
		for(int i=0; i < tmp.size(); i++){
			Node t = nodes.get(tmp.get(i));
			res.setColour(t.getColour());
			for(int j=0; j < t.getPoints().size();j++){
				res.addPoint(t.getPoints().get(j));
			}
		}
		for(int i=tmp.size()-1; i >= 0; i--){
			int k = tmp.get(i);
			nodes.remove(k);
		}
		nodes.add(res);
		return res;
	}

	private Node neighbourWithSameColour(Point p, int colour) {
		Vector<Integer> res = new Vector<>();
		for(int i=0; i < nodes.size(); i++){
			Node tempNode = nodes.get(i);
			Vector<Point> tempPoints = tempNode.getPoints();
			for(int j = 0; j < tempPoints.size(); j++){
				if(tempNode.getColour() == colour && isNeighbourPoints(tempPoints.get(j), p)){
					res.add(i);
					break;
				}
			}
		}
		Node joined;
		if(res.size() != 0){
			joined = join(res, p);
		}else{
			return null;
		}
		return joined;
	}

	public int[] solve(int[][] board) {
		splitIntoNodes(board);
		addNeighbouring();
		root = getRoot();
		numOfNodes = nodes.size();
		lastPoint = new Point(board.length-1, board[0].length-1);
		setHeuristics();
		getOptimalPath(root);
		Vector<Integer> a = optimalWay.path;
		int[] ress = new int[a.size()-1];
		for (int i = 0; i < a.size()-1; i++) {
			ress[i] =  a.get(i+1);
		}

		return ress;
	}

	private Node getRoot() {
		Node res = null;
		for (int i = 0; i < nodes.size(); i++) {
			for (int j = 0; j < nodes.get(i).getPoints().size(); j++) {
				if(nodes.get(i).getPoints().get(j).x == 0 && nodes.get(i).getPoints().get(j).y == 0){
					res = nodes.get(i);
				}
			}
		}
		return res;
	}

	private void addNeighbouring() {
		for(int i=0; i < nodes.size(); i++){
			Node curr = nodes.get(i);
			for(int j=i+1; j < nodes.size(); j++){
				Node currNei = nodes.get(j);
				if(isNeighbourNodes(curr, currNei)){
					curr.addNeighbourNode(currNei);
					currNei.addNeighbourNode(curr);
				}
			}
		}
	}


	private boolean isNeighbourNodes(Node node, Node node2) {
		for(int i=0; i < node.getPoints().size(); i++){
			for(int j=0; j < node2.getPoints().size(); j++){
				if(isNeighbourPoints(node.getPoints().get(i), node2.getPoints().get(j))){
					return true;
				}
			}
		}
		return false;
	}


	private void getOptimalPath(Node root){
		PriorityQueue<Way> ways = new PriorityQueue<>(10, new Comparator<>() {
            public int compare(Way o1, Way o2) {
                return o1.Heuristic(nodes) + o1.length - o2.Heuristic(nodes) - o2.length;
            }
        });
		Search(ways, root);
	}

	private void Search(PriorityQueue<Way> ways, Node root2) {
		Way w = new Way();
		w.addNode(root2);
		ways.add(w);
		while (true) {
			Way t = ways.poll();
			if(t.getNumberOfNodes() == numOfNodes){
				optimalWay = t;
				return;
			}
			Vector<Node> neighbours = t.getNeighbourNodes();
			Vector<Vector<Node>> neighbours1 = getSameColourNeigbours(neighbours);
			for(int i=0; i < neighbours1.size(); i++){
				Way newWay = new Way();
				for(int j=0; j < t.getNodes().size(); j++){
					newWay.addNode(t.getNodes().get(j));
					newWay.length = t.length;
					newWay.path = new Vector<>();
					for (int j2 = 0; j2 < t.path.size(); j2++) {
						newWay.path.add(t.path.get(j2));
					}
				}
				newWay.addAllNode(neighbours1.get(i));
				ways.add(newWay);
			}
		}
	}

	private Vector<Vector<Node>> getSameColourNeigbours(Vector<Node> neighbours) {
		Vector<Vector<Node>> result = new Vector<>();
		HashSet<Integer> set = new HashSet<>();
		for (int i = 0; i < neighbours.size(); i++) {
			if(!set.contains(neighbours.get(i).getColour())){
				set.add(neighbours.get(i).getColour());
				Node iNode = neighbours.get(i);
				Vector<Node> tmp = new Vector<>();
				tmp.add(iNode);
				for (int j = 0; j < neighbours.size(); j++) {
					if(neighbours.get(j) != iNode && neighbours.get(j).getColour()==iNode.getColour()){
						tmp.add(neighbours.get(j));
					}
				}
				result.add(tmp);
			}
		}
		return result;
	}



	public void setHeuristics()
	{
		Node root = getFarNode();
		Queue<Node> queue = new LinkedList<>();
		queue.add(root);
		int heuristic = 0;
		root.heuristic = heuristic;
		HashSet<Node> visited = new HashSet<>();
		visited.add(root);
		while(!queue.isEmpty()) {
			Node node = (Node)queue.remove();
			heuristic = node.heuristic+1;
			Iterator<Node> it = node.getNeighbours().iterator();
			while(it.hasNext()) {
				Node neighbour = it.next();
				if (!visited.contains(neighbour)) {
					visited.add(neighbour);
					neighbour.heuristic = heuristic;
					queue.add(neighbour);
				}
			}
		}
	}


	private Node getFarNode() {
		Node result = null;
		for (int i = nodes.size()-1; i >= 0 ; i--) {
			for (int j = 0; j < nodes.get(i).getPoints().size(); j++) {
				int x = nodes.get(i).getPoints().get(j).x;
				int y = nodes.get(i).getPoints().get(j).y;
				if(x == lastPoint.x && y == lastPoint.y){
					result = nodes.get(i);
				}
			}
		}
		return result;
	}

}
