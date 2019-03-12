package floodit.solver.students.vmask;

public class Cell {
	private String string;
	private int hashCode;

	public Cell(int i, int j) {
		string = "(" + i + " " + j + ")";
		hashCode = string.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || !(obj instanceof Cell))
			return false;
		return toString().equals(((Cell) obj).toString());
	}

	@Override
	public String toString() {
		return string;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}
}