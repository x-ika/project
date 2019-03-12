package floodit.solver.students.kurid;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Queue;

public class Node implements Comparable{
	public ArrayList<Fragment> fragments;
	public int color;
	public int[] moves;
	public int h;
	public HashSet<Fragment> have;
	
	public Node(ArrayList<Fragment> fragments, int color, int[] moves){
		this.fragments = fragments;
		this.color = color;
		this.moves = moves;
		have = new HashSet<>(fragments);
	}
	
	public Node colorIt(int color){
		ArrayList<Fragment> res = new ArrayList<>(fragments);
		HashSet<Fragment> added = new HashSet<>();
		for (Fragment i: fragments)
			for (Fragment j:i.neibs)
				if (!have.contains(j) && j.points.get(0).c == color && !added.contains(j)){
					res.add(j);
					added.add(j);
				}
		int[] next = new int[moves.length+1];
		for (int i=0; i<moves.length; i++)
			next[i]=moves[i];
		next[next.length-1]=color;
		return new Node(res,color,next);
	}
	
	public void heuristics(){
		h=0;
		int n=Board.fragments.size();
		int[] distance = new int[n];
		Arrays.fill(distance, Integer.MAX_VALUE);
		Queue<Fragment> Q = new ArrayDeque<>();
		for (Fragment i:fragments){
			distance[Board.to.get(i)]=0;
			Q.add(i);
		}
		Fragment v;
		while (Q.size()>0){
			v = Q.poll();
			int iv = Board.to.get(v);
			for (Fragment i:v.neibs){
				int ii = Board.to.get(i);
				if (distance[iv]+1<distance[ii]){
					distance[ii] = distance[iv]+1; 
					Q.add(i);
				}
			}
		}
		for (int i=0; i<n; i++)
				if (distance[i]>h) h=distance[i];
	}
	

	public int compareTo(Object arg0) {
		Node t = (Node) arg0;
		int res = moves.length+h-t.moves.length-t.h;
		if (res==0)
			return t.moves.length-moves.length;
		return res;
	}
}
