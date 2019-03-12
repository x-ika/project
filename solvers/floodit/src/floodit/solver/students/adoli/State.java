package floodit.solver.students.adoli;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class State implements Comparable<State> {
	public int weight;
	public Set<Group> groups;
	public List<Integer> choices;
	public Map<Integer, Set<Group>> otherGroups, neighbours;
	
	private static Map<Integer, Set<Group>> copyGroups(Map<Integer, Set<Group>> groups) {
		Map<Integer, Set<Group>> res = new HashMap<>(groups);
		
		for (int color: groups.keySet())
			res.put(color, new HashSet<>(groups.get(color)));
		
		return res;
	}

	public State(Map<Integer, Set<Group>> otherGroups, Map<Integer, Set<Group>> neighbours){
		this.groups = new HashSet<>();
		this.choices = new ArrayList<>();
		this.otherGroups = otherGroups;
		this.neighbours = neighbours;
	}

	public State(State state) {
		this.groups = new HashSet<>(state.groups);
		this.choices = new ArrayList<>(state.choices);
		this.otherGroups = copyGroups(state.otherGroups);
		this.neighbours = copyGroups(state.neighbours);
	}

	public void addNeighbour(Group group) {
		if (!neighbours.containsKey(group.color))
			neighbours.put(group.color, new HashSet<>());
		
		neighbours.get(group.color).add(group);
	}

	public void removeNeighbour(Group group) {
		neighbours.get(group.color).remove(group);
		
		if (neighbours.get(group.color).isEmpty())
			neighbours.remove(group.color);
	}

	public void removeOtherGroup(Group group) {
		otherGroups.get(group.color).remove(group);
		
		if (otherGroups.get(group.color).isEmpty())
			otherGroups.remove(group.color);
	}
	
	public boolean containsState(Set<State> states) {
		for (State p: states)
			if (p.choices.size() == choices.size() && p.groups.containsAll(groups))
				return true;
		
		return false;
	}

	public boolean equals(Object other) {
		return groups.equals(((State) other).groups);
	}

	public int compareTo(State other) {
		int res = weight - other.weight;
		
		if (res == 0)
			return other.choices.size() - choices.size();
		
		return res;
	}
}