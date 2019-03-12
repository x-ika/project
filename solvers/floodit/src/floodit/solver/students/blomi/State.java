package floodit.solver.students.blomi;

import java.util.ArrayList;

public class State {
	int [][] board;
	private ArrayList<Integer> sequence;
	private int color, eur, depth;
	public static final int maxInt = 2000000000;
	
	public State(int [][] b){
		board = new int[b.length][b[0].length];
		for(int i = 0; i < b.length; i++){
			for(int j = 0; j < b[i].length; j++){
				board[i][j] = b[i][j];
			}
		}
		sequence = new ArrayList<>();
		color = board[0][0];
		depth = 0;
	}
	
	public State(State other){
		sequence = new ArrayList<>();
		for(int i = 0; i < other.getSequence().size(); i++) 
			this.sequence.add(other.getSequence().get(i));
		board = new int[other.board.length][other.board[0].length];
		for(int i = 0; i < other.board.length; i++){
			for(int j = 0; j < other.board[0].length; j++){
				board[i][j] = other.board[i][j];
			}
		}
		color = other.getColor();
		eur = other.getEur();
		depth = other.getDepth();
	}
	
	
	public void changeColor(int c){
		sequence.add(c);
		change(0, 0, c);
		color = c;
	}
	
	private void change(int i, int j, int c){
		if(board[i][j] == color){
			board[i][j] = c;
			if(i > 0) change(i - 1, j, c);
			if(i < board.length - 1) change(i + 1, j, c);
			if(j > 0) change(i, j - 1, c);
			if(j < board[0].length - 1) change(i, j + 1, c);
		}
	}
	
	public String toString(){
		String ans = "";
		for(int i = 0; i < board.length; i++){
			for(int j = 0; j < board[i].length; j++){
				ans = ans + board[i][j] + " ";
			}
			ans = ans + '\n';
		}
		return ans;
	}
	
	public int getColor(){
		return color;
	}
	
	public ArrayList<Integer> getSequence(){
		return sequence;
	}
	
	public void setDepth(int d){
		depth = d;
	}
	
	public int getDepth(){
		return depth;
	}
	
	public void setEur(int eur){
		this.eur = eur;
	}
	
	public int getEur(){
		return eur;
	}
	
	public int getTotal(){
		return depth + eur;
	}
	
	public int getColor(int i, int j) {
		return board[i][j];
	}
}
