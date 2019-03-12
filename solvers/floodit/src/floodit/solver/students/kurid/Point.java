package floodit.solver.students.kurid;

public class Point {
	public int i;
	public int j;
	public int c;
	public Fragment fragment;

	public Point(int i, int j, int c) {
		this.i=i;
		this.j=j;
		this.c=c;
	}
	
	public void setFragment(Fragment fragment){
		this.fragment = fragment;
	}
}
