package floodit.solver.students.sggol;

import floodit.solver.Solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class MySolver implements Solver {

	private class Possition {
		public Possition(int moveNum, boolean[][] painted, int h,
				ArrayList<Integer> path) {
			this.moveNum = moveNum;
			this.painted = painted;
			f = moveNum + h;
			this.path = path;
		}

		public boolean[][] painted;
		public int moveNum, f;
		public ArrayList<Integer> path;
	}

	private int[][] board;
	private int n, m, colNum;

	private boolean[][] paint(boolean[][] painted, int color) {
		boolean[][] res = new boolean[n][m];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				res[i][j] = painted[i][j];
			}
		}
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				if (!res[i][j]) {
					int b, c;
					b = i - 1;
					c = j;
					if (isValisdPoint(b, c)) {
						if (res[b][c] && board[i][j] == color) {
							res[i][j] = true;
							localPaint(i, j, color, res);
						}
					}
					b = i + 1;
					c = j;
					if (isValisdPoint(b, c)) {
						if (res[b][c] && board[i][j] == color) {
							res[i][j] = true;
							localPaint(i, j, color, res);
						}
					}
					b = i;
					c = j - 1;
					if (isValisdPoint(b, c)) {
						if (res[b][c] && board[i][j] == color) {
							res[i][j] = true;
							localPaint(i, j, color, res);
						}
					}
					b = i;
					c = j + 1;
					if (isValisdPoint(b, c)) {
						if (res[b][c] && board[i][j] == color) {
							res[i][j] = true;
							localPaint(i, j, color, res);
						}
					}
				}
			}
		}
		return res;
	}

	private void localPaint(int i, int j, int color, boolean[][] res) {
		int b, c;
		b = i - 1;
		c = j;
		if (isValisdPoint(b, c)) {
			if (res[b][c] == false && board[b][c] == color) {
				res[b][c] = true;
				localPaint(b, c, color, res);
			}
		}
		b = i + 1;
		c = j;
		if (isValisdPoint(b, c)) {
			if (res[b][c] == false && board[b][c] == color) {
				res[b][c] = true;
				localPaint(b, c, color, res);
			}
		}
		b = i;
		c = j - 1;
		if (isValisdPoint(b, c)) {
			if (res[b][c] == false && board[b][c] == color) {
				res[b][c] = true;
				localPaint(b, c, color, res);
			}
		}
		b = i;
		c = j + 1;
		if (isValisdPoint(b, c)) {
			if (res[b][c] == false && board[b][c] == color) {
				res[b][c] = true;
				localPaint(b, c, color, res);
			}
		}
	}

	private boolean isValisdPoint(int b, int c) {
		if (b >= 0 && b < n && c >= 0 && c < m) {
			return true;
		}
		return false;
	}


	public int[] solve(int[][] board) {
		n = board.length;
		m = board[0].length;
		this.board = board;
		colNum = 0;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				if (colNum < board[i][j]) {
					colNum = board[i][j];
				}
			}
		}
		colNum++;
		boolean[][] painted = new boolean[n][m];
		painted[0][0] = true;
		int firstColor = board[0][0];
		boolean[][] res = paint(painted, firstColor);
		ArrayList<Integer> path = new ArrayList<>();
		Possition firstPos = new Possition(path.size(), res, 0, path);
		Comparator<Possition> cmp = new PossitipnComparator();
		PriorityQueue<Possition> queue = new PriorityQueue<>(11, cmp);
		queue.add(firstPos);
		while (!queue.isEmpty()) {
			Possition pos = queue.remove();
			ArrayList<Possition> newPossitons = makePossitions(pos);
			for (int i = 0; i < newPossitons.size(); i++) {
				if (gameIsOver(newPossitons.get(i))) {
					int result[] = new int[newPossitons.get(i).path.size()];
					for (int j = 0; j < result.length; j++) {
						result[j] = newPossitons.get(i).path.get(j);
					}
					return result;
				} else {
					queue.add(newPossitons.get(i));
				}
			}
		}
		return null;
	}

	private boolean gameIsOver(Possition possition) {
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				if (possition.painted[i][j] == false) {
					return false;
				}
			}
		}
		return true;
	}

	private ArrayList<Possition> makePossitions(Possition pos) {
		boolean[] possibleCollors = new boolean[colNum];
		boolean[][] res = pos.painted;

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				if (!res[i][j]) {
					int b, c;
					b = i - 1;
					c = j;
					if (isValisdPoint(b, c)) {
						if (res[b][c]) {
							possibleCollors[board[i][j]] = true;
						}
					}
					b = i + 1;
					c = j;
					if (isValisdPoint(b, c)) {
						if (res[b][c]) {
							possibleCollors[board[i][j]] = true;
						}
					}
					b = i;
					c = j - 1;
					if (isValisdPoint(b, c)) {
						if (res[b][c]) {
							possibleCollors[board[i][j]] = true;
						}
					}
					b = i;
					c = j + 1;
					if (isValisdPoint(b, c)) {
						if (res[b][c]) {
							possibleCollors[board[i][j]] = true;
						}
					}
				}
			}
		}
		ArrayList<Possition> resultPos = new ArrayList<>();
		for (int i = 0; i < colNum; i++) {
			if (possibleCollors[i]) {
				boolean[][] newRes = paint(res, i);
				ArrayList<Integer> newPath = new ArrayList<>();
				for (int j = 0; j < pos.path.size(); j++) {
					newPath.add(pos.path.get(j));
				}
				newPath.add(i);
				resultPos.add(new Possition(pos.moveNum + 1, newRes,
						Evr(newRes), newPath));

			}

		}
		return resultPos;
	}

	private int Evr(boolean[][] newRes) {
		boolean[] possibleCollors = new boolean[colNum];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				if (newRes[i][j] == false) {
					possibleCollors[board[i][j]] = true;
				}
			}
		}
		int sum = 0;
		for (int i = 0; i < colNum; i++) {
			if (possibleCollors[i]) {
				sum++;
			}
		}
		return sum;
	}

	private class PossitipnComparator implements Comparator<Possition> {

		public int compare(Possition x, Possition y) {
			if (x.f == y.f) {
				return x.moveNum - y.moveNum;
			}
			return x.f - y.f;
		}
	}

//	public static void main(String[] args) {
//		long t = System.currentTimeMillis();
//		int a, b;
//		int[][] board = null;
//		try {
//			BufferedReader br = new BufferedReader(new FileReader(
//					"117_8-9-6.txt"));
//			String s = br.readLine();
//			StringTokenizer tk = new StringTokenizer(s);
//			a = Integer.parseInt(tk.nextToken());
//			b = Integer.parseInt(tk.nextToken());
//			board = new int[a][b];
//			for (int i = 0; i < a; i++) {
//				String s1 = br.readLine();
//				StringTokenizer tk1 = new StringTokenizer(s1);
//				for (int j = 0; j < b; j++) {
//					board[i][j] = Integer.parseInt(tk1.nextToken());
//				}
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		MySolver ms = new MySolver();
//		int[] result = ms.solve(board);
//		for (int i = 0; i < result.length; i++) {
//			System.out.print(result[i] + " ");
//		}
//		long t1 = System.currentTimeMillis();
//		System.out.println();
//		System.out.println(result.length);
//		System.out.println((t1 - t) / 1000F);
//
//	}

}