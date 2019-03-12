package floodit.solver.students.zgven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class Config {
	
	private int [][] board;
	private int gx, hx;
	private HashMap<Point, Component> map;
	private HashMap<Integer, Component> components;
	private ArrayList<Integer> cameColors;
	
	public Config() {
		components = new HashMap<>();
		cameColors = new ArrayList<>();
	}
	
	public Config(int [][] board, int gx) {
		this.board = board;
		this.gx = gx;
		map = new HashMap<>();
		components = new HashMap<>();
		cameColors = new ArrayList<>();
		generateComponents();
		countHx();
	}
	
	public ArrayList<Config> getMoves() {
		ArrayList<Config> moves = new ArrayList<>();
		Set<Integer> moved = new HashSet<>();
		Iterator<Integer> it = components.get(0).getNeighbours().iterator();
		while (it.hasNext()) {
			int n = it.next();
			Component neighbour = components.get(n);
			if (!moved.contains(neighbour.getColor())) {
				moved.add(neighbour.getColor());
				moves.add(nextMove(n));
			}
		}
		return moves;
	}
	
	private Config nextMove(int next) {
		Config config = new Config();
		deepCopy(config);
		Component to = config.components.get(next);
		config.gx = gx + 1;
		config.cameColors.addAll(cameColors);
		config.cameColors.add(to.getColor());
		makeMove(config, to);
		config.countHx();
		return config;
	}
	
	private void deepCopy(Config config) {
		Iterator<Component> it = components.values().iterator();
		while (it.hasNext()) {
			Component current = it.next();
			Component newComp = new Component(null, current.getColor(), current.getID());
			newComp.getNeighbours().addAll(current.getNeighbours());
			config.components.put(newComp.getID(), newComp);
		}
	}
	
	private static void makeMove(Config config, Component to) {
		Component start = config.getComponents().get(0);
		Set<Integer> tempNeighbours = new HashSet<>();
		tempNeighbours.addAll(start.getNeighbours());
		Iterator<Integer> it = tempNeighbours.iterator();
		while (it.hasNext()) {
			Component next = config.components.get(it.next());
			if (next.getColor() == to.getColor()) {
				start.union(next, null, config.components, false);
				config.components.remove(next.getID());
			}
		}
	}
	
	public HashMap<Point, Component> getMap() {
		return map;
	}

	private void countHx() {
		hx = 0;
		Queue<Component> q = new LinkedList<>();
		Set<Integer> visited = new HashSet<>();
		q.add(components.get(0));
		visited.add(0);
		if (visited.size() == components.size()) return;
		int pollNum = 1;
		while (!q.isEmpty()) {
			int currentNum = 0;
			for (int j = 0; j < pollNum; j++) {
				Component c = q.poll();
				Iterator<Integer> it = c.getNeighbours().iterator();
				while (it.hasNext()) {
					int n = it.next();
					Component neighbour = components.get(n);
					if (!visited.contains(n)) {
						visited.add(n);
						q.add(neighbour);
						currentNum++;
					}
				}
			}
			hx++;
			if (visited.size() == components.size()) return;
			pollNum = currentNum;
		}
	}
	
	private void generateComponents() {
		int index = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				Point p = new Point(i,j);
				Point upper = new Point(p.getX()-1, p.getY());
				Point left = new Point(p.getX(), p.getY()-1);
				int [] added = new int[1];
				added[0] = 0;
				checkForComponent(p, upper, "upper", added);
				checkForComponent(p, left, "left", added);
				if (added[0] == 0) {
					Component newComp = new Component(p, board[i][j], index++);
					components.put(newComp.getID(), newComp);
					Component c = map.get(upper);
					if (c != null) c.addNeighbour(newComp);
					c = map.get(left);
					if (c != null) c.addNeighbour(newComp);
					map.put(p, newComp);
				}
			}
		}
	}
	
	private void checkForComponent(Point p, Point neighbour, String st, int [] added) {
		if (pointInBounds(neighbour)) {
			Component c = map.get(neighbour);
			if (c.getColor() == board[p.getX()][p.getY()]) {
				Point upper = new Point(p.getX()-1, p.getY());
				if (added[0] == 1) {
					if (map.get(neighbour) != map.get(upper)) {
						map.get(p).union(c, map, components, true);
						components.remove(c.getID());
					}
				} else {
					c.add(p);
					map.put(p, c);
					added[0] = added[0] + 1;
					if (st.equals("left")) {
						if (pointInBounds(upper)) map.get(upper).addNeighbour(c);
					}
				}
			} else if (added[0] == 1) c.addNeighbour(map.get(p));
		}
	}
	
	private boolean pointInBounds(Point p) {
		return (p.getX() >= 0 && p.getX() < board.length &&
				p.getY() >= 0 && p.getY() < board[0].length);
	}
	
	public int [][] getBoard() {
		return board;
	}
	
	public void setBoard(int [][] board) {
		this.board = board;
	}
	
	public int getFx() {
		return gx + hx;
	}

	public int getGx() {
		return gx;
	}

	public void setGx(int gx) {
		this.gx = gx;
	}

	public int getHx() {
		return hx;
	}

	public void setHx(int hx) {
		this.hx = hx;
	}
	
	@Override
	public boolean equals(Object o) {
		Config that = (Config)o;
		return (this == that);
	}

	public HashMap<Integer, Component> getComponents() {
		return components;
	}

	public void setComponents(HashMap<Integer, Component> components) {
		this.components = components;
	}

	public ArrayList<Integer> getCameColors() {
		return cameColors;
	}

	public void setCameColors(ArrayList<Integer> cameColors) {
		this.cameColors = cameColors;
	}
}
