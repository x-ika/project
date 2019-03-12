package floodit.solver.students.adoli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Group {
	public static int prevId = -1;
	public static List<Group> good = new ArrayList<>();
	
	public int id = prevId++;
	public int color;
	public Map<Integer, Set<Group>> neighbours = new HashMap<>();

	public Group(int color) {
		this.color = color;
		
		if (color != -1)
			good.add(this);
	}

	public void addNeighbour(Group other) {
		if (id == -1 || other.id == -1)
			return;
		
		if (!neighbours.containsKey(other.color))
			neighbours.put(other.color, new HashSet<>());
		
		neighbours.get(other.color).add(other);
	}
	
	public void removeNeighbour(Group other) {
		neighbours.get(other.color).remove(other);
		
		if (neighbours.get(other.color).size() == 0)
			neighbours.remove(other.color);
	}
	
	public void joinNeighbour(Group other) {
		Group.good.remove(other);
		
		for (Set<Group> neighbourSet: other.neighbours.values()) {
			for (Group neighbour: neighbourSet) {
				neighbour.removeNeighbour(other);
				neighbour.addNeighbour(this);
				addNeighbour(neighbour);
			}
		}
	}

	public boolean equals(Object other) {
		return id == ((Group) other).id;
	}
	
	public String toString() {
		return "(" + id + "," + color + ") -> " + neighbours.keySet(); 
	}
}