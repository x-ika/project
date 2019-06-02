package player.students.jilberta;

import java.util.ArrayList;

public class Board {
	private JChecker [][] mas;
	private ArrayList<Move> cameFrom = new ArrayList<>();
	private int [][] scoreBoard = new int [8][8];
//	private int score ;
	
	public Board(JChecker [][] mas){
		this.mas = mas;
	}
	
	public Board copy(){
		JChecker [][] newB = new JChecker [8][8];
		JChecker ch = null;
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				ch = this.getJChecker(new JChecker (j, i, 0).copy());
				newB[i][j] = ch;
			}
		}
		Board newBoard = new Board(newB);
		newBoard.setCameFrom(copyTheWay());
		return newBoard;
	}
	
	public ArrayList<Move> copyTheWay(){
		ArrayList<Move> newArr = new ArrayList<>();
		for(int i=0; i<this.cameFrom.size(); i++){
			newArr.add(this.cameFrom.get(i));
		}
		return newArr;
	}
	
	public int heuristic(){
		JChecker cur = null;
		int score = 0;
		initScoreBoard();
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				if(mas[i][j]!= null){
					cur = mas[i][j];
					if(cur.getColor() != 0){
						if(cur.getColor() == 1){
							score = score + 2 + scoreBoard[i][j];
						}else if(cur.getColor() == 2){
							score = score - 2 - scoreBoard[i][j];
						}else if(cur.getColor() == 4){
							score = score + 3 + scoreBoard[i][j];
						}else if(cur.getColor() == 5){
							score = score - 3 - scoreBoard[i][j];
						}
					}
				}
			}
		}
		return score;
	}
	
	private void initScoreBoard(){
		scoreBoard[0][1] = 4;
		scoreBoard[0][3] = 4;
		scoreBoard[0][5] = 4;
		scoreBoard[0][7] = 4;
		scoreBoard[1][0] = 4;
		scoreBoard[3][0] = 4;
		scoreBoard[5][0] = 4;
		scoreBoard[7][0] = 4;
		scoreBoard[7][2] = 4;
		scoreBoard[7][4] = 4;
		scoreBoard[7][6] = 4;
		scoreBoard[6][7] = 4;
		scoreBoard[4][7] = 4;
		scoreBoard[2][7] = 4;
		scoreBoard[4][7] = 4;
		scoreBoard[1][2] = 3;
		scoreBoard[1][4] = 3;
		scoreBoard[1][6] = 3;
		scoreBoard[2][1] = 3;
		scoreBoard[4][1] = 3;
		scoreBoard[6][1] = 3;
		scoreBoard[6][3] = 3;
		scoreBoard[6][5] = 3;
		scoreBoard[3][6] = 3;
		scoreBoard[5][6] = 3;
		scoreBoard[2][3] = 2;
		scoreBoard[2][5] = 2;
		scoreBoard[3][2] = 2;
		scoreBoard[5][2] = 2;
		scoreBoard[5][4] = 2;
		scoreBoard[4][5] = 2;
		scoreBoard[3][4] = 1;
		scoreBoard[4][3] = 1;	
	}
	
	public void setCameFrom(ArrayList<Move> cameFrom){
		this.cameFrom = cameFrom;
	}
	
	public ArrayList<Move> getCameFrom(){
		return cameFrom;
	}
	
	public void addMoveToCameFrom(Move move){
		cameFrom.add(move);
	}
	
	public void setJChecker (JChecker ch, JChecker to){
		ch.row = to.row;
		ch.col = to.col;
		mas[ch.col][ch.row] = ch;
	}
	
	public JChecker getJChecker(JChecker ch){
		return mas[ch.col][ch.row];
	}
	
	public void removeJChecker (JChecker ch){
		JChecker empty = new JChecker(ch.row, ch.col, 0);
//		System.out.println(ch.x+"  "+ch.y);
//		System.out.println(this);
		mas[ch.col][ch.row] = empty;
	}
	
	@Override
	public String toString(){
		String st = "";
		for(int i=0; i<8; i++){
			for(int j=0; j<8; j++){
				if((i%2 == 1 && j%2 == 0)){
					if(mas[i][j] != null){
						st += mas[i][j].value + " ";
					}
				}else if((i%2 == 0 && j%2 == 1)){
					if(mas[i][j] != null){
						st +=" "+mas[i][j].value;
					}
				}
			}
			st += "\n";
		}
		return st;
	}
}
