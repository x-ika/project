package floodit.solver.students.gmarg;

import floodit.solver.Solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Solv implements Solver {
	private int[][] vertexBoard;
	private int maxid;
	private int[] ans;

	public int[] solve(int[][] board) {
		vertexBoard = new int[board.length][board[0].length];
		maxid = 0;
		Vertex start = initGraph(board, true, 0, true);
		start.setIdPath("0 ");
		solveIt(start, board);

		return ans;
	}

	private void solveIt(Vertex start, int[][] board) {
		
		PriorityQueue<Vertex> myQue = new PriorityQueue<>(7, new Comparator<>() {


            public int compare(Vertex o1, Vertex o2) {

                return (o1.getDistance() + o1.getHeurist()) - (o2.getDistance() + o2.getHeurist());
            }
        }
        );
		
		myQue.add(start);
		
		while (!myQue.isEmpty()){
			boolean found = false;
			Vertex curr = myQue.remove();
			HashSet<Vertex> neighs = curr.getNeighbours();
			Iterator<Vertex> it = neighs.iterator();
			ArrayList<Vertex> answers = new ArrayList<>();
			int minHeur = -1;
			
			HashSet<Integer> seenColors = new HashSet<>();
			while (it.hasNext()){
				
				Vertex tmp = it.next();
				if (seenColors.contains(tmp.getColor())) continue;
				seenColors.add(tmp.getColor());
				Vertex newOne = union(tmp, curr, board);
				
				
				newOne.addPath(curr.getPath() + newOne.getColor());
				
				if(newOne.getPath().substring(0, newOne.getPath().length()).equals("3023013202")){
					System.out.println("aaaaaaa");
				}
					
				
				if (newOne.getHeurist() == 0) {
					String rogorcIkna = newOne.getPath();
					ans = new int[rogorcIkna.length()];
					for(int i=0; i< rogorcIkna.length(); i++)
						ans[i] = Character.getNumericValue(rogorcIkna.charAt(i));
					found = true;
					break;
				}
				
				int currHeuristic = newOne.getDistance() + newOne.getHeurist();
				
				if (minHeur == -1 || minHeur >= currHeuristic){
					minHeur = newOne.getDistance() + newOne.getHeurist();
					answers.add(newOne);
				} 
				//if (minHeur > currHeuristic){
				//	answers.clear();
				//	answers.add(newOne);
				//}
			}
			if (found) break;
			for (int i = 0; i < answers.size(); i++)
				myQue.add(answers.get(i));
		
			
		}
		
		
	}

	

	private Vertex union(Vertex newOne, Vertex curr, int[][] board) {
		
		int [][] newBoard = new int[board.length][board[0].length];	
		int newId = newOne.getId();
		String newIdPath = "";
		int distance = curr.getDistance();
		
		for (int i = 0; i < board.length; i++){
			for (int j = 0; j < board[0].length; j++){
				newBoard[i][j] = board[i][j];
			}
		}
		
		String path = curr.getIdPath();
		String[] strArr = path.split(" ");
		
		int [] currPath = new int[strArr.length];
		
		for (int i = 0; i < strArr.length ; i++) {
			currPath[i] = Integer.parseInt(strArr[i]);
		}
		
		for (int i = 0; i < board.length; i++){
			for (int j = 0; j < board[0].length; j++){
				for(int k = 0; k < currPath.length; k++){
					if (currPath[k] == vertexBoard[i][j]){
						newBoard[i][j] = newOne.getColor();
					}
				}
			}
		}
		
		Iterator<Vertex> it = curr.getNeighbours().iterator();
		while(it.hasNext()){
			Vertex temp = it.next();
			if (temp.getColor() == newOne.getColor()) newIdPath = newIdPath + temp.getId() +" ";
		}
		
		
		newOne = initGraph(newBoard, false, newOne.getId(), curr.isVisited());
		
		newOne.setIdPath(curr.getIdPath() + newId + " " + newIdPath);
		newOne.setDistance(distance + 1);
		return newOne;
	}



	private Vertex initGraph(int [] [] board, boolean fx, int k, boolean fix) {
		int [] []vertBoard = new int[board.length][board[0].length];
		ArrayList<Vertex> vertexes = new ArrayList<>();
		
		makeNeg(vertBoard);
		
		int startId = k;
		for (int i = 0; i < vertBoard.length; i++){
			for (int j = 0; j < vertBoard[0].length; j ++){
				if (vertBoard[i][j] == -1){
					Vertex temp = new Vertex(startId, board[i][j]);
					makeGraph(i, j, startId, temp, vertBoard, board);
					startId++;
					vertexes.add(temp);
					if (temp.getId() + 1 > maxid) maxid = temp.getId() + 1;
				}
			}
		}
		
		if(!fx){
			correctVertexes(vertBoard, k, vertexes);
		}
		
		Vertex start = vertexes.get(0);
		start.setDepth(0);

				
		countNeighs(vertBoard, maxid, vertexes);
		calcDepth(start, fix);
		calcHeuristic(start);
		
		if(fx){
			for (int i = 0; i < vertBoard.length; i++){
				for (int j = 0; j < vertBoard[0].length; j++){
					vertexBoard[i][j] = vertBoard[i][j];
					 
				}
			}
		}
		
		return start;
	}
	
	


	private void correctVertexes(int[][] vertBoard, int k, ArrayList<Vertex> vertexes) {
		
		for(int i = 0; i < vertBoard.length; i++){
			for (int j = 0; j < vertBoard[0].length; j++){
				
				if(vertBoard[i][j] != vertexBoard[i][j]){
					
					if (vertBoard[i][j] != k ){
						for(int p = 0; p < vertexes.size(); p++){
							if (vertexes.get(p).getId() == vertBoard[i][j] && !vertexes.get(p).isVisited()){
								vertexes.get(p).setId(vertexBoard[i][j]);
								vertexes.get(p).setVisited(true);
							}
							
						}
						vertBoard[i][j] = vertexBoard[i][j];
					}
					
					
					
				}
			}
		}
		
		for(int i = 0; i < vertexes.size(); i++)
			vertexes.get(i).setVisited(false);	
		
	}

	private void calcHeuristic(Vertex vrt) {
		Queue<Vertex> que = new LinkedList<>();
		que.add(vrt);
		HashSet<Vertex> neighs = new HashSet<>();
		HashSet<Vertex> visitedNeis = new HashSet<>();
		int max = 0;
		int currDepth = 0;
		
		while(!que.isEmpty()){
			Vertex curr = que.remove();		
			neighs.addAll(curr.getNeighbours());
			neighs.removeAll(visitedNeis);
			visitedNeis.addAll(neighs);
			
			Iterator<Vertex> it = neighs.iterator();
			
			while (it.hasNext()){
				Vertex tmp = it.next();
				que.add(tmp);
				if (tmp.getDepth() > currDepth) currDepth = tmp.getDepth();
			}
			if (currDepth > max) max = currDepth;
		}
		
		vrt.setHeurist(max);
		
	}

	private void calcDepth(Vertex cmp, boolean fix) {
		Queue<HashSet<Vertex>> que= new LinkedList<>();
		HashSet<Vertex> neis= cmp.getNeighbours();
		que.add(neis);
		int depth = 0;
		cmp.setDepth(depth);
		cmp.changeVisited();
		depth++;
		
		while (!que.isEmpty()){
			HashSet<Vertex> newNeighs = new HashSet<>();
			neis = que.remove(); 
			Iterator<Vertex> it =  neis.iterator();
			boolean found = false;
			while(it.hasNext()){
				Vertex tmp = it.next();
				if (tmp.isVisited() == fix) continue;
		
				found = true;
				tmp.setDepth(depth);
				tmp.changeVisited();
				newNeighs.addAll(tmp.getNeighbours());
			}
			
			if (found){
				
				depth++;
				que.add(newNeighs);
			}
		}
		
	}

	private void countNeighs(int[][] vertBoard, int vertNumb, ArrayList<Vertex> vertexes) {
		boolean [][] fix = new boolean[vertNumb][vertNumb];
		
		for (int i = 0; i < vertBoard.length; i++){
			for (int j = 0; j < vertBoard[0].length; j++){
				
				if(i > 0){
					if (vertBoard[i][j] != vertBoard[i - 1][j]){
						fix[vertBoard[i][j]][vertBoard[i - 1][j]] = true;
						fix[vertBoard[i - 1][j]][vertBoard[i][j]] = true;
					}
				}
				
				if (j > 0){
					if (vertBoard[i][j] != vertBoard[i][j - 1]){
						fix[vertBoard[i][j]][vertBoard[i][j - 1]] = true;
						fix[vertBoard[i][j - 1]][vertBoard[i][j]] = true;
					}
				}
				
				if (i < vertBoard.length - 1){
					if (vertBoard[i][j] != vertBoard[i + 1][j]){
						fix[vertBoard[i][j]][vertBoard[i + 1][j]] = true;
						fix[vertBoard[i + 1][j]][vertBoard[i][j]] = true;
					}
				}
				
				if (j < vertBoard[0].length - 1){
					if (vertBoard[i][j] != vertBoard[i][j + 1]){
						fix[vertBoard[i][j]][vertBoard[i][j + 1]] = true;
						fix[vertBoard[i][j + 1]][vertBoard[i][j]] = true;
					}
				}
			}
		}
	
		
		
		
		for (int i = 0; i < fix.length; i++){
			for (int j = 0; j < fix[0].length; j++){
				if (fix[i][j]){
					
					for (int k = 0; k < vertexes.size(); k++){
						boolean found = false;
						if (vertexes.get(k).getId() == j ){
							Vertex tmp = vertexes.get(k);
							for (int p = 0; p < vertexes.size(); p++){
								if (vertexes.get(p).getId() == i){
									vertexes.get(p).addNeighbour(tmp);
									tmp.addNeighbour(vertexes.get(p));
									found = true;
									break;
								}
							}
							if (found) break;
						}
					}
					
				}
			}
		}
		
	}


	private void makeGraph(int i, int j, int id, Vertex temp, int[][] vertBoard, int[][] board) {
		vertBoard[i][j] = id;
		if(i > 0){
			if (board[i][j] == board[i - 1][j] && vertBoard[i - 1][j] == -1){
				temp.incCount();
				makeGraph(i - 1 , j, id, temp, vertBoard, board);
			}
		}
		
		if (j > 0){
			if (board[i][j] == board[i][j - 1] && vertBoard[i][j - 1] == -1){
				temp.incCount();
				makeGraph(i, j - 1, id, temp, vertBoard, board);
			}
		}
		
		if (i < board.length - 1){
			if (board[i][j] == board[i + 1][j] && vertBoard[i + 1][j] == -1){
				temp.incCount();
				makeGraph(i + 1 , j, id, temp,vertBoard, board);
			}
		}
		
		if (j < board[0].length - 1){
			if (board[i][j] == board[i][j + 1] && vertBoard[i][j + 1] == -1){
				temp.incCount();
				makeGraph(i , j + 1, id, temp,vertBoard, board);
			}
		}
	}

	private void makeNeg(int[][] vertBoard) {
		for (int i = 0; i < vertBoard.length; i++){
			for (int j = 0; j < vertBoard[0].length; j++){
				vertBoard[i][j] = -1;
			}
		}
		
	}
}
