package floodit.solver.students.ggigl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

public class Node implements Comparable{
	private byte[][] board;
	private int iSize,jSize;
	public int h=0;
	public byte[] before;
		
	public Node(byte[][] board, byte[] before){
		iSize = board.length;
		jSize = board[0].length;
		this.board=board;
		this.before = before;
	}
	
	public byte getColor(){
		return board[0][0];
	}
	
	public Node change(int color){
		byte c = (byte) color;
		byte initColor = board[0][0];
		boolean[][] isVisited = new boolean[iSize][jSize];
		byte[][] res = new byte[iSize][jSize];
		for (int i=0; i<iSize; i++)
			for (int j=0; j<jSize; j++)
				res[i][j]=board[i][j];
		Queue<Pair> Q = new ArrayDeque<>();
		Pair v = new Pair(0,0);
		isVisited[v.x][v.y] = true;
		Q.add(v);
		while (Q.size()>0){
			v = Q.poll();
			res[v.x][v.y]=c;
			if (v.x>0)
				if (board[v.x-1][v.y]==initColor && !isVisited[v.x-1][v.y]){
					isVisited[v.x-1][v.y]=true;
					Q.add(new Pair(v.x-1,v.y));
				}
			if (v.x<iSize-1)
				if (board[v.x+1][v.y]==initColor && !isVisited[v.x+1][v.y]){
					isVisited[v.x+1][v.y]=true;
					Q.add(new Pair(v.x+1,v.y));
				}
			if (v.y>0)
				if (board[v.x][v.y-1]==initColor && !isVisited[v.x][v.y-1]){
					isVisited[v.x][v.y-1]=true;
					Q.add(new Pair(v.x,v.y-1));
				}
			if (v.y<jSize-1)
				if (board[v.x][v.y+1]==initColor && !isVisited[v.x][v.y+1]){
					isVisited[v.x][v.y+1]=true;
					Q.add(new Pair(v.x,v.y+1));
				}
		}
		byte[] next = new byte[before.length+1];
		for (int i=0; i<before.length; i++)
			next[i]=before[i];
		next[next.length-1]=c;
		Node result = new Node(res,next);
		return result;
	}
	
	public void calculateHeuristics() {
		byte[][] dist = new byte[iSize][jSize];
		for (int i=0; i<iSize; i++)
			for (int j=0; j<jSize; j++)
				dist[i][j]=Byte.MAX_VALUE;
		Queue<Pair> Q = new ArrayDeque<>();
		Pair v = new Pair(0,0);
		dist[v.x][v.y]=0;
		Q.add(v);
		int max=0;
		while (Q.size()>0){
			v = Q.poll();
			//if (dist[v.x][v.y]>max) max=dist[v.x][v.y];
			if (v.x>0){
				int add=0;
				if (board[v.x][v.y]!=board[v.x-1][v.y])
					add++;
				if (dist[v.x-1][v.y]>dist[v.x][v.y]+add){
					dist[v.x-1][v.y]=(byte) (dist[v.x][v.y]+add);
					Q.add(new Pair(v.x-1,v.y));
				}
			}
			if (v.x<iSize-1){
				int add=0;
				if (board[v.x][v.y]!=board[v.x+1][v.y])
					add++;
				if (dist[v.x+1][v.y]>dist[v.x][v.y]+add){
					dist[v.x+1][v.y]=(byte) (dist[v.x][v.y]+add);
					Q.add(new Pair(v.x+1,v.y));
				}
			}
			if (v.y>0){
				int add=0;
				if (board[v.x][v.y]!=board[v.x][v.y-1])
					add++;
				if (dist[v.x][v.y-1]>dist[v.x][v.y]+add){
					dist[v.x][v.y-1]=(byte) (dist[v.x][v.y]+add);
					Q.add(new Pair(v.x,v.y-1));
				}
			}
			if (v.y<jSize-1){
				int add=0;
				if (board[v.x][v.y]!=board[v.x][v.y+1])
					add++;
				if (dist[v.x][v.y+1]>dist[v.x][v.y]+add){
					dist[v.x][v.y+1]=(byte) (dist[v.x][v.y]+add);
					Q.add(new Pair(v.x,v.y+1));
				}
			}
		}
		
		for (int i=0; i<iSize; i++)
			for (int j=0; j<jSize; j++)
				if (dist[i][j]>max) max=dist[i][j];
		h=max;
	}

	@Override
	public int compareTo(Object arg0) {
		Node t = (Node) arg0;
		int b1 = before.length;
		int b2 = t.before.length;
		int res = b1+h-b2-t.h;
		if (res==0)
			return b2-b1;
		return res;
	}
	
	@Override
	public String toString(){
		String res="";
		for (int i=0; i<iSize; i++){
			for (int j=0; j<jSize; j++){
				res+=board[i][j]+" ";
			}
			res+="\n";
		}
		return res;
	}
}
