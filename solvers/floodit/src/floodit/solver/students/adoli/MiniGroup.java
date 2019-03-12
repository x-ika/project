package floodit.solver.students.adoli;

import java.util.Map;
import java.util.Set;

public class MiniGroup {
	public Map<Integer, Set<Group>> neighbours;
	public int soFar;

	public MiniGroup(Map<Integer, Set<Group>> neighbours, int soFar) {
		this.neighbours = neighbours;
		this.soFar = soFar;
	}
}