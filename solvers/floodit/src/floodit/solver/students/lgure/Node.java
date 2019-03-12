package floodit.solver.students.lgure;

public class Node {

	public static int generatedId = -1;

	private int id;

	private int color; // optimize

	public Node() {
		generatedId++;
		id = generatedId;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getId() {
		return id;
	}
}
