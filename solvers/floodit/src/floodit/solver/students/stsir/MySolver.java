package floodit.solver.students.stsir;

import floodit.solver.Solver;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

class Node {
	String boardState;
	int value = Integer.MAX_VALUE;
	int f_score = Integer.MAX_VALUE;
};

class NodeComparator implements Comparator<Object> {

	public int compare(Object o1, Object o2) {
		Node n1 = (Node) o1;
		Node n2 = (Node) o2;
		if (n1.f_score > n2.f_score)
			return 1;
		if (n1.f_score < n2.f_score)
			return -1;
		return 0;
	}

};

public class MySolver implements Solver {

	private static int[][] board;
	private static int height;
	private static int width;

	private Map<Node, Node> came_from = new HashMap<>();
	private Map<String, Integer> g_scoresMap = new HashMap<>();
	private PriorityQueue<Node> pqNodes = new PriorityQueue<>(1000, new NodeComparator());
	private HashSet<Point> sameColorPoints;
	private HashSet<String> closedSet = new HashSet<>();
	private boolean[][] alreadyVisited;

	public int[] solve(int[][] givenBoard) {

		board = givenBoard;
        height = board.length;
        width = board[0].length;

        ArrayList<Integer> ans = AStar();

		int[] result = new int[ans.size()];
		int j = 0;
		for (Integer integer : ans) {
			result[result.length - j - 1] = integer;
			j++;
		}
//		for (int i = 0; i < ans.size(); i++) {
//			System.out.print(ans.get(ans.size() - i - 1) + " ");
//		}
		return result;
	}

	private int[][] StringToMatrix(String str) {

		int[][] res = new int[height][width];
		StringTokenizer st = new StringTokenizer(str);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				res[i][j] = Integer.parseInt(st.nextToken());
			}
		}
		return res;
	}

	private String MatrixToString(int[][] matrix) {
		String res = "";
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				res += matrix[i][j] + " ";
			}
		}
		return res;
	}

	private ArrayList<Integer> AStar() {// pseudo code is from wikipedia

		Node startState = new Node();
		startState.boardState = MatrixToString(board);
		g_scoresMap.put(startState.boardState, 0); // Cost from start along best known path.
		startState.f_score = g_scoresMap.get(startState.boardState) + heuristic_cost_estimate(startState.boardState);
		pqNodes.add(startState);

		while(!pqNodes.isEmpty()){
			Node polled = pqNodes.poll();// the node in openset having the
										// lowest f_score[] value;
			int[][] currentBoard = StringToMatrix(polled.boardState);
			if(currentIsGoal(currentBoard)){
				return reconstruct_path(new ArrayList<>(), polled);
			}
			if(closedSet.contains(polled.boardState)) continue;
			closedSet.add(polled.boardState);
			HashSet<Integer> neighbors = getAllNeighbors(polled);
			for (Integer color : neighbors) {
				int[][]neighbor = StringToMatrix(polled.boardState);
				setColor(color, 0, 0, neighbor);
				String neighbour = MatrixToString(neighbor);
				int g_score_curr = g_scoresMap.get(polled.boardState) + 1;
				Node newNode = new Node();
				newNode.boardState = neighbour;
				if(g_scoresMap.containsKey(neighbour) && g_score_curr  < g_scoresMap.get(neighbour))
						g_scoresMap.put(neighbour, g_score_curr);
				else{
					g_scoresMap.put(neighbour, g_score_curr);
					newNode.f_score = g_score_curr + heuristic_cost_estimate(neighbour);
					pqNodes.add(newNode);
				}
				came_from.put(newNode, polled);
			}
		}
		return null;
	}

	private int heuristic_cost_estimate(String boardString) {
		HashSet<Integer> colors = new HashSet<>();
		StringTokenizer st = new StringTokenizer(boardString);
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				colors.add(Integer.parseInt(st.nextToken()));
			}
		}
		return colors.size();
	}

	private void setColor(int color, int i, int j, int[][] neighbor) {
		for (Point point : sameColorPoints) {
			neighbor[point.x][point.y] = color;
		}
	}

	private HashSet<Integer> getAllNeighbors(Node currNode) {

		int[][] matrix = StringToMatrix(currNode.boardState);
		HashSet<Integer> found = new HashSet<>();
		alreadyVisited = new boolean[height][width];
		sameColorPoints = new HashSet<>();
		sameColorPoints.add(new Point(0, 0));
		search(0, 0, found, matrix);
		return found;
	}

	private void search(int i, int j, HashSet<Integer> found, int[][] matrix) {

		if (!alreadyVisited[i][j]) {
			alreadyVisited[i][j] = true;
			if (i + 1 < height) {
				if (matrix[i + 1][j] == matrix[i][j]) {
					sameColorPoints.add(new Point(i + 1, j));
					search(i + 1, j, found, matrix);
				} else
					found.add((matrix[i + 1][j]));
			}
			if (i - 1 >= 0) {
				if (matrix[i - 1][j] == matrix[i][j]) {
					sameColorPoints.add(new Point(i - 1, j));
					search(i - 1, j, found, matrix);
				} else
					found.add((matrix[i - 1][j]));
			}
			if (j + 1 < width) {
				if (matrix[i][j + 1] == matrix[i][j]) {
					sameColorPoints.add(new Point(i, j + 1));
					search(i, j + 1, found, matrix);
				} else
					found.add((matrix[i][j + 1]));
			}
			if (j - 1 >= 0) {
				if (matrix[i][j - 1] == matrix[i][j]) {
					sameColorPoints.add(new Point(i, j - 1));
					search(i, j - 1, found, matrix);
				} else
					found.add((matrix[i][j - 1]));
			}
		}
	}

	private boolean currentIsGoal(int[][] current) {
		int color = current[0][0];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (current[i][j] != color)
					return false;
			}
		}
		return true;
	}

	private ArrayList<Integer> reconstruct_path(ArrayList<Integer> path_Back, Node current_node) {
		if (came_from.containsKey(current_node)) {
			String a = "";
			a += current_node.boardState.charAt(0);
			path_Back.add(Integer.parseInt(a));
			return reconstruct_path(path_Back, came_from.get(current_node));
		} else
			return path_Back;
	}

	private static void readFile(String filename) throws IOException {
		BufferedReader rd = new BufferedReader(new FileReader(filename));
		String line = rd.readLine();
		String[] s = line.split(" ");
		height = Integer.parseInt(s[0]);
		width = Integer.parseInt(s[1]);
		board = new int[height][width];
		int i = 0;
		while (true) {
			line = rd.readLine();
			if (line == null)
				break;
			String[] currLine = line.split(" ");
			for (int j = 0; j < width; j++) {
				board[i][j] = Integer.parseInt(currLine[j]);

			}
			i++;
		}
		rd.close();
	}

	public static void main(String[] args) throws IOException {


			readFile("000_12-12-4.txt");


			MySolver s = new MySolver();


			s.solve(board);
	}
}
