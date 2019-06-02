package floodit.solver.students.gpata;

import floodit.solver.Solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

public class MySolver implements Solver {
	private HashMap<Pair, Group> map;
	private ArrayList<Group> gr;
	private Pair[][] p;
	private boolean[][]b;
	private int groupCount;
	private int colorCount = 0;
	private int[] cols = new int [7];
	public int[] solve(int[][] board) {
		groupCount=0;
		map = new HashMap<>();
		gr = new ArrayList<>();
		p = new Pair[board.length][board[0].length];
		b = new boolean[p.length][p[0].length];
		buildGraph(board);
		/*for(int i=0;i<gr.size();i++){
			for(int j=0;j<gr.get(i).members.size();j++){
				System.out.println(gr.get(i).members.get(j).getA()+" "+
						gr.get(i).members.get(j).getB());
			}
			System.out.println("-------------------------------------------------");
			for(int j=0;j<gr.get(i).neighbours.size();j++){
				System.out.print(gr.get(i).neighbours.get(j).color+" ");
			}
			System.out.println("//////////////////////////////////////////////");
		}*/
		ArrayList<Group> ans = getAns(gr.get(0));
		if(ans!=null){
			int[] ansi = new int[ans.size()-1];
			for(int i=1;i<ans.size();i++)
				ansi[i-1] = ans.get(i).color;
			return ansi;
		}
		return null;
	}
	private ArrayList<Group> getAns(Group start){
		Comparator<Path> com = new PathComparator();
		PriorityQueue<Path> pq = new PriorityQueue<>(10, com);
		boolean[] b = new boolean[gr.size()];
		for(int i=0;i<b.length;i++){
			b[i] = false;
		}
		b[0] = true;
		Path pro = new Path();
		pro.cols = this.cols.clone();
		pro.cols[gr.get(0).color]-=gr.get(0).members.size();
		pro.addGroup(start);
		pro.been = b;
		pro.filled+=start.members.size();
		pq.add(pro);
		while(!pq.isEmpty()){
			Path cur = pq.poll();
			ArrayList<Group> curar = (ArrayList<Group>)cur.getLast().neighbours.clone();
			for(int i=0;i<=colorCount;i++){
				Group curg = new Group(i,-1);
				Path newp = new Path();
				newp.cols = cur.cols.clone();
				newp.been = cur.been.clone();
				curg.quickChek = cur.getLast().quickChek.clone();
				int count = 0;
				boolean checker = false;
				for(int j=0;j<curar.size();j++){
					if(!cur.been[curar.get(j).groupNum] &&
							curar.get(j).color == i){
						for(int p=0;p<curar.get(j).neighbours.size();p++){
							if(!curg.quickChek[curar.get(j).neighbours.get(p).groupNum]
									&& !cur.been[curar.get(j).neighbours.get(p).groupNum]){
								curg.neighbours.add(curar.get(j).neighbours.get(p));
								curg.quickChek[curar.get(j).neighbours.get(p).groupNum] = true;
							}
						}
						checker = true;
						newp.been[curar.get(j).groupNum] = true;
						count+=curar.get(j).members.size();
						newp.cols[curar.get(j).color]-=curar.get(j).members.size();
					}
					else  if(!cur.been[curar.get(j).groupNum]){
						curg.neighbours.add(curar.get(j));
					}
				}
				if(checker){
					newp.addManyGroups(cur.getPassed());
					newp.addGroup(curg);
					int bla = countFx(newp);
					newp.setFx(bla+newp.getPassed().size()-1);
					newp.filled+=count;
					newp.filled+=cur.filled;
					if(newp.filled == p.length * p[0].length){
						return newp.getPassed();
					}
					pq.add(newp);
				}
			}
		}
		return null;
	}
	private int countFx(Path pl){
		int count = 0;
		for(int i=0;i<=colorCount;i++){
			if(pl.cols[i]>0)
				count++;
		}
		return count;
	}
	private void buildGraph(int[][] board){
		for(int i=0;i<p.length;i++){
			for(int j=0;j<p[0].length;j++){
				b[i][j] = false;
				p[i][j] = new Pair(i, j);
			}
		}
		for(int i=0;i<board.length;i++){
			for(int j=0;j<board[0].length;j++){
				if(board[i][j]>colorCount){
					colorCount = board[i][j];
				}
				cols[board[i][j]]++;
				if(!b[i][j]){
					b[i][j] = true;
					Group cur = new Group(board[i][j],groupCount);
					groupCount++;
					gr.add(cur);
					cur.members.add(p[i][j]);
					ArrayList<Pair> arr = getCurrentNeighbours(board, b, i, j);
					for(int k=0;k<arr.size();k++){
						map.put(p[arr.get(k).getA()][arr.get(k).getB()], cur);
						cur.members.add(p[arr.get(k).getA()][arr.get(k).getB()]);
					}
					map.put(p[i][j], cur);
				}
			}
		}
		for(int i=0;i<gr.size();i++){
			gr.get(i).quickChek = new boolean[gr.size()];
		}
		for(int i=0;i<gr.size();i++){
			Group cur = gr.get(i);
			for(int j=0;j<cur.members.size();j++){
				for(int x=-1;x<=1;x++){
					for(int y=-1;y<=1;y++){
						int curx = cur.members.get(j).getA()+x;
						int cury = cur.members.get(j).getB()+y;
						if(inBounds(board,curx,cury) && cur.color!=board[curx][cury] &&
								Math.abs(x+y)==1 && !cur.nei.contains(map.get(p[curx][cury]))){
							cur.nei.add(map.get(p[curx][cury]));
							cur.neighbours.add(map.get(p[curx][cury]));
							cur.quickChek[map.get(p[curx][cury]).groupNum] = true;
						}
					}
				}
			}
		}
	}
	private ArrayList<Pair> getCurrentNeighbours(int[][] board,boolean[][] b,int m,int n){
		ArrayList<Pair> arr = new ArrayList<>();
		for(int i=-1;i<=1;i++){
			for(int j=-1;j<=1;j++){
				if(inBounds(board, m+i, n+j) && board[m][n] == board[m+i][n+j] &&
						Math.abs(i+j)==1 && !b[m+i][n+j]){
					arr.add(new Pair(m+i, n+j));
					b[m+i][n+j]=true;
					arr.addAll(getCurrentNeighbours(board, b, m+i, n+j));
				}
			}
		}
		return arr;
	}
	private boolean inBounds(int[][] board,int a,int b){
		return (a>=0 && a<board.length && b>=0 && b<board[0].length);
	}
	public class Group{
		int color;
		int groupNum;
		boolean quickChek[];
		ArrayList<Pair> members;
		ArrayList<Group> neighbours;
		HashSet<Group> nei;
		public Group(int color,int groupNum){
			this.quickChek = new boolean[gr.size()];
			this.groupNum = groupNum;
			this.color = color;
			nei = new HashSet<>();
			this.members = new ArrayList<>();
			this.neighbours = new ArrayList<>();
		}
	}
	private class PathComparator implements Comparator<Path>{
		public int compare(Path o1, Path o2) {
			return o1.getFx()-o2.getFx();
		}
	}
}