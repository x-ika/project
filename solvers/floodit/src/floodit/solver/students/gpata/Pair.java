package floodit.solver.students.gpata;

public class Pair{
	private int a,b;
	public Pair(int a,int b){
		this.a = a;
		this.b = b;
	}
	public int getA(){
		return this.a;
	}
	public int getB(){
		return this.b;
	}
	@Override
	public boolean equals(Object obj){
		Pair other = (Pair)obj;
		if((other.a == this.a && other.b == this.b) ||
				(this.b == other.a && this.a == other.b)){
			return true;
		}
		return false;
	}
}
