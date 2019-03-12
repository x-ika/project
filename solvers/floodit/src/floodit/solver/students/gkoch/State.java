package floodit.solver.students.gkoch;

import java.util.ArrayList;
import java.util.HashSet;

public class State {

	public HashSet<Integer> neigbours = new HashSet<>();
	public HashSet<Integer> innerNodes = new HashSet<>();
	public int color,gx,fx;
	public ArrayList<Integer> path = new ArrayList<>();
	
	public String toString(){
		String str = "color: " + color + "  innerNodes: ";
		for(Integer i : innerNodes){
			str += i + " ";
		}
		str += "\n neigbours: ";
		for(Integer i: neigbours){
			str += i + " ";
		}
		str += " path: " + path.size() + " : ";
		for(Integer i: path){
			str += i + " ";
		}
		return str;
	}
}
