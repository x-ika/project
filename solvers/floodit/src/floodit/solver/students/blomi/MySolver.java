package floodit.solver.students.blomi;

import floodit.solver.Solver;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Comparator;
//import floodit.solver.students.blomi.Solver;

public class MySolver implements Solver {
	private int w, h, colors;
	private static int[][] visited;
	

	public int[] solve(int[][] board) {
		//Date d = new Date();
		//System.out.println(d);
		w = board[0].length;
		h = board.length;
		colors = getColors(board);
		visited = new int[h][w];
		State first = new State(board);
		first.setEur(H(first));
		Queue<State> pq = new PriorityQueue<>(7, depthComparator2);
		pq.add(first);
		while(pq.size() != 0){
			State curr = pq.peek();
			pq.poll();
			if(curr.getEur() == 0) {
    			ArrayList <Integer> ans = curr.getSequence();
    			int[] toReturn = new int[ans.size()];
    			for(int i = 0; i < ans.size(); i++) {
    			//	System.out.println(ans.get(i));
    				toReturn[i] = ans.get(i);
    			}
    			//Date b = new Date();
    			//System.out.println(b);
    			return toReturn;
    		}
			for(int i = 0; i <= colors; i++){
    			if(curr.getColor() != i){
    				State copy = new State(curr);
    		    	copy.changeColor(i);
    		    	copy.setEur(H(copy));
    		    	copy.setDepth(curr.getDepth() + 1);
    		    	pq.add(copy);
    			}
    		}
		}
		return null;
	}

	public static Comparator<State> depthComparator2 = new Comparator<>() {


        public int compare(State s1, State s2) {
            return (int) (s1.getTotal() - s2.getTotal());
        }
    };
	
	private int H(State s) {
		Queue<Point> pq = new PriorityQueue<>(7, depthComparator);
		for(int i = 0; i < h; i++){
			for(int j = 0; j < w; j++){
				visited[i][j] = State.maxInt;
			}
		}
		Point f = new Point(0,0);
		pq.add(f);
		while(pq.size() != 0){
			Point curr = pq.peek();
			pq.poll();
			int I = curr.getI();
			int J = curr.getJ();
			if(visited[I][J] >= curr.getDep()){
				visited[I][J] = curr.getDep();
				Point p;
				if(I > 0){
					if(s.getColor(I, J) == s.getColor(I - 1, J)){
						if(visited[I - 1][J] > visited[I][J]){
							p = new Point(I - 1, J);
							p.setDep(curr.getDep());
							pq.add(p);
							visited[I - 1][J] = visited[I][J];
						}
					}else{
						if(visited[I - 1][J] > visited[I][J] + 1){
							p = new Point(I - 1, J);
							p.setDep(curr.getDep() + 1);
							pq.add(p);
							visited[I - 1][J] = visited[I][J] + 1;
						}
					}
				}
				if(I < h - 1){
					if(s.getColor(I, J) == s.getColor(I + 1, J)){
						if(visited[I + 1][J] > visited[I][J]){
							p = new Point(I + 1, J);
							p.setDep(curr.getDep());
							pq.add(p);
							visited[I + 1][J] = visited[I][J];
						}
					}else{
						if(visited[I + 1][J] > visited[I][J] + 1){
							p = new Point(I + 1, J);
							p.setDep(curr.getDep() + 1);
							pq.add(p);
							visited[I + 1][J] = visited[I][J] + 1;
						}
					}
				}
				if(J > 0){
					if(s.getColor(I, J) == s.getColor(I, J - 1)){
						if(visited[I][J - 1] > visited[I][J]){
							p = new Point(I, J - 1);
							p.setDep(curr.getDep());
							pq.add(p);
							visited[I][J - 1] = visited[I][J];
						}
					}else{
						if(visited[I][J - 1] > visited[I][J] + 1){
							p = new Point(I, J - 1);
							p.setDep(curr.getDep() + 1);
							pq.add(p);
							visited[I][J - 1] = visited[I][J] + 1;
						}
					}
				}
				if(J < w - 1){
					if(s.getColor(I, J) == s.getColor(I, J + 1)){
						if(visited[I][J + 1] > visited[I][J]){
							p = new Point(I, J + 1);
							p.setDep(curr.getDep());
							pq.add(p);
							visited[I][J + 1] = visited[I][J];
						}
					}else{
						if(visited[I][J + 1] > visited[I][J] + 1){
							p = new Point(I, J + 1);
							p.setDep(curr.getDep() + 1);
							pq.add(p);
							visited[I][J + 1] = visited[I][J] + 1;
						}
					}
				}
			}
		}
		int max = 0;
		for(int i = 0; i < visited.length; i++){
			for(int j = 0; j < w; j++){
				if(visited[i][j] > max) max = visited[i][j];
			}
		}
		return max;
	}
	
	public static Comparator<Point> depthComparator = new Comparator<>() {


        public int compare(Point p1, Point p2) {
            return (int) (visited[p1.getI()][p1.getJ()] - visited[p2.getI()][p2.getJ()]);
        }
    };
	

	private int getColors(int[][] board){
		int max = 0;
		for(int i = 0; i < h; i++){
			for(int j = 0; j < w; j++){
				if(max < board[i][j]) max = board[i][j];
			}
		}
		return max;
	}
	
}


