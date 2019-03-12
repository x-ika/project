package floodit.solver.students.zgven;

public class Point {
	
	private int x, y;
	
	public Point(int x, int y) {
		this.setX(x);
		this.setY(y);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	@Override
	public boolean equals(Object other) {
		Point p = (Point)other;
		return (this.x == p.getX() && this.y == p.getY());
	}
	
	@Override
	public int hashCode() {
		int hash = 17;
        hash = 31 * hash + x;
        hash = 31 * hash + y;
        return hash;
	}
	
}
