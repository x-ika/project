package floodit.solver.students.adoli;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class BreadthFirstSearch {
	public static int solve(State path) {
		Queue<MiniGroup> agenda = new LinkedList<>();
		Set<Group> groups = new HashSet<>(path.groups);
		
		MiniGroup visited = new MiniGroup(path.neighbours, 0);
		agenda.add(visited);
		
		while (!agenda.isEmpty()) {
			visited = agenda.poll();
			
			for (Set<Group> neighboursSet: visited.neighbours.values())
				for (Group neighbour: neighboursSet)
					if (!groups.contains(neighbour)) {
						agenda.add(new MiniGroup(neighbour.neighbours, visited.soFar + 1));
						groups.add(neighbour);
					}
		}
		
		return visited.soFar;
	}
}