package floodit.solver.students.blomi;

public class Point {
	private int i, j, dep;
	public Point(int i, int j){
		this.i = i;
		this.j = j;
	}
	
	public int getI(){ return i; };
	
	public int getJ(){ return j; };
	
	public int getDep(){ return dep; }
	
	public void setDep(int dep){ this.dep = dep;}
}
