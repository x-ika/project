package floodit.solver.students.adoli;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class AStar {
	public static int[] solve(Map<Integer, Set<Group>> groups, Group start) {
		PriorityQueue<State> agenda = new PriorityQueue<>();
		
		fillStartGroups(groups);
		
		State state = new State(groups, start.neighbours);
		Set<State> states = new HashSet<>();
		
		state.groups.add(start);
		state.removeOtherGroup(start);

		agenda.add(state);
		states.add(state);
		
		while (!agenda.isEmpty()) {
			state = agenda.poll();
			states.remove(state);

			if (state.otherGroups.size() == 0)
				return toIntArray(state.choices);
			
			for (int color: state.neighbours.keySet()) {
				State newState = new State(state);
			
				joinSameColor(state, newState, color);
				newState.choices.add(color);

				if (newState.containsState(states))
					continue;
				
				states.add(newState);

				int heuristics = Math.max(BreadthFirstSearch.solve(newState), newState.otherGroups.size());
				newState.weight = newState.choices.size() + heuristics;

				agenda.add(newState);
			}
		}
		
		return null;
	}
	
	private static int[] toIntArray(List<Integer> list) {
		int[] res = new int[list.size()];
		
		for (int i = 0; i<list.size(); i++)
			res[i] = list.get(i);
		
		return res;
	}
	
	private static void joinSameColor(State state, State newState, int color) {
		for (Group neighbour: state.neighbours.get(color)) {
			newState.removeOtherGroup(neighbour);
			newState.removeNeighbour(neighbour);
			
			for (Set<Group> neighboursSet: neighbour.neighbours.values())
				for (Group otherNeighbour : neighboursSet)
					if (!newState.groups.contains(otherNeighbour))
						newState.addNeighbour(otherNeighbour);
			
			newState.groups.add(neighbour);
		}
	}
	
	private static void fillStartGroups(Map<Integer, Set<Group>> groups) {
		for (Group cur: Group.good) {
			if (!groups.containsKey(cur.color))
				groups.put(cur.color, new HashSet<>());
			
			groups.get(cur.color).add(cur);
		}
	}
}
