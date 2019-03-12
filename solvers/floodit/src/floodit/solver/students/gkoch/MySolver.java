package floodit.solver.students.gkoch;

import floodit.solver.Solver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

public class MySolver implements Solver {
	
	HashMap<Integer, HashSet<Integer>> bfsQueue = new HashMap<>();
	HashSet<Integer> bfsUsed = new HashSet<>();
	
	public int[] solve(int[][] board) {
		Node[][] backBoard = new Node[board.length][board[0].length];
		Node.map = new HashMap<>();
		ConstructNodes(board, backBoard);
		ConstructGraph(backBoard);

		PriorityQueue<State> pQueue = new PriorityQueue<>(10,
                new Comparator<>() {


                    public int compare(State o1, State o2) {
                        int tmp = (o1.fx + o1.gx) - (o2.fx + o2.gx);
                        if (tmp != 0) return tmp;
                        return o1.fx - o2.fx;
                    }
                });

		State s = new State();
		s.color = Node.map.get(0).color;
		s.innerNodes.add(0);
		s.neigbours.addAll(Node.map.get(0).neigbours);

		s.gx = 0;
		s.fx = bfs(s);
		pQueue.add(s);
		
		HashMap<Integer,HashSet<State>> set = new HashMap<>();
		set.put(s.path.size(), new HashSet<>());
		set.get(s.path.size()).add(s);
		
		HashMap<Integer, HashSet<Integer>> colorNeigbours = new HashMap<>();
		ArrayList<Integer> road;
		while (true) {
			State tmp = pQueue.poll();
			if (tmp.neigbours.isEmpty()) {
				road = tmp.path;
				break;
			}
			for (Integer i : tmp.neigbours) {
				if (!colorNeigbours.containsKey(Node.map.get(i).color))
					colorNeigbours.put(Node.map.get(i).color,
                            new HashSet<>());
				colorNeigbours.get(Node.map.get(i).color).add(i);
			}
			for (Integer j : colorNeigbours.keySet()) {
				State cur = new State();
				cur.color = j;
				cur.gx = tmp.gx + 1;
				cur.innerNodes.addAll(tmp.innerNodes);
				cur.neigbours.addAll(tmp.neigbours);
				for (Integer i : colorNeigbours.get(j)) {
					cur.innerNodes.add(i);
					cur.neigbours.addAll(Node.map.get(i).neigbours);
				}
				cur.neigbours.removeAll(cur.innerNodes);
				cur.path.addAll(tmp.path);
				cur.path.add(j);
				boolean bool = true;
				HashSet<State> hash = set.get(cur.path.size());
				if(hash != null){
					for(State state: hash){
						if(state.innerNodes.containsAll(cur.innerNodes)){
							bool=false;
							break;
						}
					}
				}
				if(bool){
					cur.fx = Math.max(LeftColors(cur),bfs(tmp));
					pQueue.add(cur);
					if(!set.containsKey(cur.path.size()))
						set.put(cur.path.size(), new HashSet<>());
					set.get(cur.path.size()).add(cur);
				}
			}
			colorNeigbours.clear();
		}

		return ArrayListToArray(road);
	}

	private int[] ArrayListToArray(ArrayList<Integer> road) {
		int[] arr = new int[road.size()];
		for(int i = 0 ; i < road.size(); i++)
			arr[i] = road.get(i);
		return arr;
	}

	private int LeftColors(State cur) {
		HashSet<Integer> hash = new HashSet<>();
		for(Integer i: Node.map.keySet()){
			if(!cur.innerNodes.contains(i))
				hash.add(Node.map.get(i).color);
		}
		return hash.size();
	}

	private int bfs(State s) {
		bfsQueue.put(1, s.neigbours);
		bfsUsed.addAll(s.innerNodes);
		bfsUsed.addAll(s.neigbours);

		int pos = 1;
		while (true) {
			if (bfsQueue.get(pos).isEmpty())
				break;
			bfsQueue.put(pos + 1, new HashSet<>());
			for (int i : bfsQueue.get(pos)) {
				for (int j : Node.map.get(i).neigbours) {
					if (!bfsUsed.contains(j)) {
						bfsQueue.get(pos + 1).add(j);
						bfsUsed.add(j);
					}
				}
			}
			pos++;
		}
		bfsQueue.clear();
		bfsUsed.clear();
		return pos - 1;
	}

	private void ConstructGraph(Node[][] backBoard) {
		for (int i = 0; i < backBoard.length; i++) {
			for (int j = 0; j < backBoard[0].length; j++) {
				addNeigbour(i, j, backBoard);
			}
		}
	}

	private void addNeigbour(int i, int j, Node[][] backBoard) {
		Node node = backBoard[i][j];
		for (PointT point : node.arr) {
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					if ((x + y) % 2 != 0 && x + i >= 0 && y + j >= 0
							&& x + i < backBoard.length
							&& y + j < backBoard[0].length) {
						if (backBoard[x + i][y + j] != backBoard[i][j]
								&& !backBoard[i][j].neigbours
										.contains(backBoard[i][j].number)) {
							backBoard[i][j].neigbours.add(backBoard[i + x][j
									+ y].number);
							backBoard[i + x][j + y].neigbours
									.add(backBoard[i][j].number);
						}
					}
				}
			}
		}
	}

	private void ConstructNodes(int[][] board, Node[][] backBoard) {
		int number = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] != -1) {
					int color = board[i][j];
					ArrayList<PointT> t = new ArrayList<>();
					findNode(i, j, board, color, t);
					Node node = new Node(t, color, number);
					for (PointT point : t) {
						backBoard[point.x][point.y] = node;
					}
					Node.map.put(number, node);
					number++;
				}
			}
		}
	}

	private void findNode(int i, int j, int[][] board, int color,
			ArrayList<PointT> t) {
		if (board[i][j] != color || board[i][j] == -1)
			return;
		t.add(new PointT(i, j));
		board[i][j] = -1;
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				if ((x + y) % 2 != 0 && i + x >= 0 && j + y >= 0
						&& i + x < board.length && j + y < board[0].length) {
					findNode(i + x, j + y, board, color, t);
				}
			}
		}
	}

	public static void main(String[] args) {
		MySolver solver = new MySolver();
		Scanner scanner;
		try {
			scanner = new Scanner(new File("resources/087_10-12-5.txt"));
			int length = scanner.nextInt();
			int width = scanner.nextInt();
			int[][] board = new int[length][width];
			for (int i = 0; i < length; i++) {
				for (int j = 0; j < width; j++) {
					board[i][j] = scanner.nextInt();
				}
			}
			long l1 = System.currentTimeMillis();
			solver.solve(board);
			System.out.println((System.currentTimeMillis() - l1) / 1000F);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
