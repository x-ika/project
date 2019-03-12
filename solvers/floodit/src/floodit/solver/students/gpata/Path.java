package floodit.solver.students.gpata;

import java.util.ArrayList;

import floodit.solver.students.gpata.MySolver.Group;

public class Path{
	private int fx;
	private ArrayList<Group> arr;
	public boolean[] been;
	public int leftCol;
	public int[] cols;
	public int filled = 0;
	public Path(){
		this.fx = 0;
		this.arr = new ArrayList<>();
	}
	public ArrayList<Group> getPassed(){
		return this.arr;
	}
	public Group getLast(){
		return this.arr.get(arr.size()-1);
	}
	public void addManyGroups(ArrayList<Group> arr){
		this.arr.addAll(arr);
	}
	public void addGroup(Group g){
		this.arr.add(g);
	}
	public int getFx(){
		return this.fx;
	}
	public void setFx(int fx){
		this.fx = fx;
	}
}