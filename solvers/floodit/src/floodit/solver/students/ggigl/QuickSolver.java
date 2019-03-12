package floodit.solver.students.ggigl;

import java.util.*;

import floodit.solver.Solver;

public class QuickSolver implements Solver{

	@Override
	public int[] solve(int[][] board) {
		int max=0;
		byte[][] b=new byte[board.length][board[0].length];
		for (int i=0; i<board.length; i++)
			for (int j=0; j<board[0].length; j++){
				b[i][j]=(byte)board[i][j];
				if (board[i][j]>max) max=board[i][j];
			}
		Node ans;
		Node start = new Node(b,new byte[0]);
		start.calculateHeuristics();
		PriorityQueue<Node> Q = new PriorityQueue<>();
		Q.add(start);
		Random r = new Random();
		while (true){
			Node cur = Q.poll();
			if (cur.h==0){
				ans = cur;
				break;
			}
			int j = r.nextInt(max+1);
			for (int i=j; i<=j+max; i++){
				if (i%(max+1)!=cur.getColor()){
					Node neighbor = cur.change(i%(max+1));
					neighbor.calculateHeuristics();
					Q.add(neighbor);
				}
			}
		}
		int[] res = new int[ans.before.length];
		for (int i=0; i<ans.before.length; i++)
			res[i]=ans.before[i];
		/*for (int i=0; i<res.length; i++){
			start = start.change(res[i]);
			System.out.println(start);
		}*/
		return res;
	}
	
	
	
}