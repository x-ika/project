package floodit.solver.students.zgven;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Component {
	
	private Set<Point> set;
	private int color;
	private Set<Integer> neighbours;
	private int id;
	
	public Component(Point p, int color, int id) {
		neighbours = new HashSet<>();
		this.color = color;
		this.id = id;
		if (p != null) {
			set = new HashSet<>();
			set.add(p);
		}
	}
	
	public void union(Component c, HashMap<Point, Component> map, HashMap<Integer, Component> components, boolean deepUnion) {
		if (deepUnion) {
			Set<Point> cSet = c.getSet();
			Iterator<Point> it = cSet.iterator();
			while (it.hasNext()) {
				Point p = it.next();
				map.put(p, this);
				set.add(p);
			}
		}
		color = c.getColor();
		neighbours.remove(c.id);
		c.neighbours.remove(id);
		Iterator<Integer> it = c.neighbours.iterator();
		while (it.hasNext()) {
			int n = it.next();
			Component current = components.get(n);
			addNeighbour(current);
			current.neighbours.remove(c.id);
		}
	}
	
	public void addNeighbour(Component neighbour) {
		neighbours.add(neighbour.id);
		neighbour.neighbours.add(id);
	}
	
	public Set<Integer> getNeighbours() {
		return neighbours;
	}
	
	public void setNeighbours(Set<Integer> neighbours) {
		this.neighbours = neighbours;
	}
	
	public void add(Point p) {
		set.add(p);
	}

	public Set<Point> getSet() {
		return set;
	}

	public void setSet(Set<Point> set) {
		this.set = set;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
	
	@Override
	public boolean equals(Object o) {
		Component that = (Component)o;
		return (this == that);
	}
	
	@Override
	public int hashCode() {
		return id;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}
	
}
