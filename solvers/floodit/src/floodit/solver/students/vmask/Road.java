package floodit.solver.students.vmask;

import java.util.Map;
import java.util.Set;

public class Road {
	public Map<Integer, Set<Node> > neighbours;
	public int soFar;

	public Road(Map<Integer, Set<Node> > neighbours, int soFar) {
		this.neighbours = neighbours;
		this.soFar = soFar;
	}
}