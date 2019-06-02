package floodit.solver.students.eakhv;

import floodit.solver.Solver;

import java.util.*;

public class FloodIt implements Solver {
	private int n, m, k;
	public  int[][] arr;
	private String boardstr;
	private HashSet<String> visited = new HashSet<>();
	private HashMap<String, String> cameFrom = new HashMap<>();
	private HashMap<String, Integer> gscore = new HashMap<>();
	public FloodIt() {
		/*try {
			readFile();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}
	private int Colors(String board) {
		Set<Character> a = new HashSet<>();
		for (int i = 0; i < board.length(); i++) {
			if (!a.contains(board.charAt(i)))
				a.add(board.charAt(i));
		}
		return a.size();
	}


	private  String AStar() {
		String s = boardstr;
		cameFrom.put(s, "stop");
		gscore.put(s, 0);
		PriorityQueue<node> que = new PriorityQueue<>(1000, new cmp());
		node start = new node(s, heuristic_cost_estimate(copy(arr)));
		que.add(start);
		while (!que.isEmpty()) {
			node curr = que.poll();
			String a= curr.board;

			if (Colors(a) == 1) {

				return curr.board;
			}
			if (!visited.contains(curr.board)) {

				visited.add(curr.board);

				int[][] current = stringToArray(curr.board);

				for (int i = 0; i < k; i++) {

					if (i == current[0][0])
						continue;
					int[][] neighbours = neighbourRoad(current, i);
					String neighbour = arrayToString(neighbours);
					int g_score = gscore.get(curr.board) + 1;
					if (gscore.containsKey(neighbour)) {
						if (g_score < gscore.get(neighbour)) {
							gscore.put(neighbour, g_score);
							cameFrom.put(neighbour, curr.board + i);
						}

					} else {
						gscore.put(neighbour, g_score);
						int heuristics = heuristic_cost_estimate(neighbours);
						que.add(new node(neighbour, g_score + heuristics));
						cameFrom.put(neighbour, curr.board + i);

					}
				}
			}
		}
		return null;

	}


	private int[] result(String answer, long l) {
		if(answer==null){
			//System.out.println("No solution found");
			return null;
		}
	//	System.out.println("Time: " + l + "ms    Num Steps: "+ gscore.get(answer));
		String finish = cameFrom.get(answer);

		ArrayList<String> array = new ArrayList<>();
		while (!finish.equals("stop")) {
		//	System.out.println(finish);
			array.add("" + finish.charAt(finish.length() - 1));
			finish = cameFrom.get(finish.substring(0, finish.length() - 1));

		}
		int[] res=new int[ gscore.get(answer)];


		for (int i = array.size() - 1; i >= 0; i--) {

			res[array.size() - 1-i]=Integer.parseInt("" + array.get(i));

		}
		return res;

	}

	private int[][] neighbourRoad(int[][] arr2, int color) {

		int[][] copy = copy(arr2);
		findNeighbour(0, 0, copy, color, arr2[0][0]);

		return copy;
	}

	private int[][] copy(int[][] arr2) {
		int copy[][] = new int[arr2.length][arr2[0].length];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < m; j++)
				copy[i][j] = arr2[i][j];
		return copy;
	}

	static int[][] direction = { { 0, 0, -1, 1 }, { -1, 1, 0, 0 } };

	private void findNeighbour(int x, int y, int[][] arr, int color,
			int first) {
		if (arr[x][y] != first)
			return;
		arr[x][y] = color;
		for (int i = 0; i < direction[0].length; i++) {
			int a = x + direction[0][i], b = y + direction[1][i];
			if (isInBound(a, b))
				findNeighbour(a, b, arr, color, first);
		}
	}

	private int heuristic_cost_estimate(int[][] arr) {
		return Colors(arrayToString(arr));
	}

	private boolean isInBound(int x, int y) {
		if (x < 0 || x >= n || y < 0 || y >= m)
			return false;
		return true;

	}

	private String arrayToString(int[][] arr) {
		String str = "";
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[0].length; j++) {
				str += arr[i][j];
			}
		}

		return str;
	}

	private  int[][] stringToArray(String str) {
		int[][] arr = new int[n][m];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				arr[i][j] = Integer.parseInt("" + str.charAt(i * m + j));

			}

		}

		return arr;
	}

	public int[] solve(int[][] board) {

		arr=copy(board);
		n=arr.length;
		m=arr[0].length;
		boardstr=arrayToString(board);
		k=Colors(boardstr);
		long start = System.currentTimeMillis();
		String answer = AStar();
		long end = System.currentTimeMillis();
		return result(answer, end - start);


	}

}

class point {
	public int x = 0;
	public int y = 0;

	public point(int x, int y) {
		this.x = x;
		this.y = y;
	}

}

class node {
	String board;
	int length;

	public node(String str, int length) {
		board = str;
		this.length = length;
	}
}

class cmp implements Comparator<node> {

	@Override
	public int compare(node a, node b) {
		return a.length - b.length;
	}

}
