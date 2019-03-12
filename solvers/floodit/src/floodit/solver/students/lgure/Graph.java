package floodit.solver.students.lgure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Graph {
	private   int SIZE = 100;
	
	private int gScore = Integer.MAX_VALUE; //current cost from start possition to here
	
	private int fScore = 0; // f function
	
	private List<Node> nodes;
	
	private boolean[][] graph;
	
	public Graph() {
	}
	
	public Graph(int[][] data) {
		graph = new boolean[SIZE][SIZE];
		gScore = 0;
		nodes = new ArrayList<>();
		nodes.add(new Node());	//carieli Node. raTa node-is ID emTxveodes mis indexs araylistshi
		
		//empty grid
		int[][]  grid = new int[data.length][data[0].length];
		
		for (int i = 0; i < data.length; i++) {
			for(int j = 0; j < data[0].length; j++) {
				if (grid[i][j] == 0) {
					Node newNode = createNode(i,j,grid,data);
					nodes.add(newNode);
				}
				if (((i - 1 >= 0) && (grid[i-1][j] != 0)) && (grid[i-1][j] != grid[i][j])) {
					graph[grid[i-1][j]][grid[i][j]] = true;
					graph[grid[i][j]][grid[i-1][j]] = true;
				}
				if (((j - 1 >= 0) && (grid[i][j-1] != 0)) && (grid[i][j-1] != grid[i][j])) {
                    try {
                        graph[grid[i][j-1]][grid[i][j]] = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    graph[grid[i][j]][grid[i][j-1]] = true;
				}
				if (((i + 1 < grid.length) && (grid[i+1][j] != 0)) && (grid[i+1][j] != grid[i][j])) {
					graph[grid[i+1][j]][grid[i][j]] = true;
					graph[grid[i][j]][grid[i+1][j]] = true;
				}
				if (((j + 1 <grid[0].length) && (grid[i][j+1] != 0)) && (grid[i][j+1] != grid[i][j])) {
					graph[grid[i][j+1]][grid[i][j]] = true;
					graph[grid[i][j]][grid[i][j+1]] = true;
				}
			}
		}
	}


	private Node createNode(int a,int b,int[][] grid, int[][] data) {
		List<String> l = new ArrayList<>();
		l.add(a + " " + b);
		Node newOne = new Node();
		newOne.setColor(data[a][b]);
		
		for(int i = 0; i < l.size(); i++ ){
			String[] coordinates = l.get(i).split(" ");
			int x = Integer.parseInt(coordinates[0]);		
			int y = Integer.parseInt(coordinates[1]);
			grid[x][y] = newOne.getId();
			if (x - 1 >= 0 && grid[x-1][y] == 0) {
				if (data[x-1][y] == data[x][y]) 
					l.add((x-1)  + " " + (y));
			}
			if (y - 1 >= 0 && grid[x][y-1] == 0) {
				if (data[x][y-1] == data[x][y]) 
					l.add((x) + " " + (y-1));
			}
			if (x + 1 < data.length && grid[x+1][y] == 0) {
				if (data[x+1][y] == data[x][y]) 
					l.add((x+1) + " " + (y));
			}
			if (y + 1 < data[0].length && grid[x][y+1] == 0) {
				if (data[x][y+1] == data[x][y]) 
					l.add((x) + " " + (y+1));
			}
		}
		return newOne;
	}

	public boolean goalIsReached() {
		int sum = 0;
		for(Node n :nodes) {
			if(n.getId() != 0) sum++;
		}
		return sum == 1;
	}
	
	public int getfScore() {
		return fScore;
	}


	public void setfScore(int fScore) {
		this.fScore = fScore;
	}
	
	public void setgScore(int gScore) {
		this.gScore = gScore;
	}

	public int getgScore() {
		return gScore;
	}
	
	public int getHeuristic() {
		List<Node> queue = new ArrayList<>();
		List<Integer> deep = new ArrayList<>(); //BFS is sigrme
		queue.add(nodes.get(1));
		int index = 0;
		deep.add(0);
		while(index < queue.size()) {
			Node cur = queue.get(index);
			for (int i = 2; i < nodes.size(); i++) {
				if (graph[cur.getId()][i] && (!queue.contains(nodes.get(i)))) {
					queue.add(nodes.get(i));
					deep.add(deep.get(index) + 1);
				}
			}
			index++;
		}
		return deep.get(index - 1);
	}
	
	public List<Graph> getNeighbors() {
		List<Graph> result = new ArrayList<>();
		Set<Integer> considered = new HashSet<>();
		for(int i = 1; i < nodes.size();i++){
			if ((graph[1][i]) && (!considered.contains(i))) {
				//clone graph
				Graph newGraph = new Graph();
				List<Node> nl = new ArrayList<>();
				for(Node n: nodes) {
					Node newOne = new Node();
					newOne.setColor(n.getColor());
					newOne.setId(n.getId());
					nl.add(newOne);
				}
				
				boolean[][] ng = new boolean[SIZE][SIZE];
				for(int a = 0; a < nodes.size();a++) {
					for(int b = 0; b < nodes.size(); b++) {
						ng[a][b] = graph[a][b];
					}
				}
				
				
				//aagebs axal graps
				for(int x = 1; x < nodes.size(); x++) {
					if ((ng[1][x] && nl.get(x).getColor()== nl.get(i).getColor()) && (!considered.contains(x))) {
						considered.add(x);
						nl.get(x).setId(0);
						for(int y = 2; y < nodes.size(); y++){
							if(ng[x][y]) {
								
								ng[1][y] = true;
							}
						}
						ng[1][x] = false;
					}
				}
				nl.get(1).setColor(nl.get(i).getColor());
				newGraph.setNodes(nl);
				newGraph.setGraph(ng);
				result.add(newGraph);
				
			}
		}
		return result;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public void setGraph(boolean[][] graph) {
		this.graph = graph;
	}
	
	public int getColor() {
		return nodes.get(1).getColor();
	}
}
