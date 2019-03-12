package floodit.solver.students.gkoch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Node { 
	
	public static HashMap<Integer, Node> map;
	
	public HashSet<Integer> neigbours = new HashSet<>();
	public ArrayList<PointT> arr = new ArrayList<>();
	public int color,number;
	
	public Node(ArrayList<PointT> arr, int color, int number){
		this.arr = arr;
		this.color = color;
		this.number = number;
	}
	
	public String toString(){
		String str = "number: "+ number;
		str += "  color: " + color;
		str += "neigbours: [";
		for(Integer i: neigbours){
			str += i + " ";
		}
		str += " ]";
		return str;
	}
	
}
