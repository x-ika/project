package player.students.kervala;

import main.*;
import java.util.ArrayList;

public class possibleTurns implements Player{
    public void gameOver() {}
	private ArrayList<Checker> killedCheckers = new ArrayList<>();
	private ArrayList<Checker> killingQueen = new ArrayList<>();
	private ArrayList<Turn> simpleTurns = new ArrayList<>();
	private ArrayList<Turn> simpleKills = new ArrayList<>();
	private ArrayList<Turn> queenMoves = new ArrayList<>();
	

	public Turn makeTurne(int[][] d, int value) {
		for(int i = 0; i<8; i++){
			for(int j = 0;j<8;j++){
				if(d[i][j] == value){
					Checker ch = new Checker(i, j, value);
					if(CanKill(d, ch)){
						killReqursion(d, ch, null);
					}
				}
				if(d[i][j] == value+3){   // damka
					//damkis kvla 
					
				}
			}
		}
		if(simpleKills.size()==0){
			if(value == 1){
				moveWhite(d,value);
			}
			else{
				moveBlack(d,value);
			}
		}
//		System.out.println(simpleTurns.size());
//		System.out.println(simpleKills.size());
//		System.out.println(queenMoves.size());
		return null;
	}
	
	
	
	private void moveWhite(int [][] board,int value){ // tetrebis cheulebrivi ertiani svla
		for(int i = 1; i<8; i++){
			for(int j = 0;j<8;j++){
				if(board[i][j] == value){
					if(j==0 && board[i-1][j+1]==0){ // if checker is on the left boarder
						Checker first = new Checker(i,j,value);
						Checker last = new Checker(i-1,j+1,value);
						Turn turn = new Turn(first,last,null);
						simpleTurns.add(turn);
					}
					if(j==7 && board[i-1][j-1]==0){ // if checker is on the right boarder
						Checker first = new Checker(i,j,value);
						Checker last = new Checker(i-1,j-1,value);
						Turn turn = new Turn(first,last,null);
						simpleTurns.add(turn);
					}
					if(j>0 && j<7) { // if checker is in the middle
						if (board[i-1][j-1]== 0) { // moves left
							Checker first = new Checker(i,j,value);
							Checker last = new Checker(i-1,j-1,value);
							Turn turn = new Turn(first,last,null);
							simpleTurns.add(turn);
							
						}
						if (board[i-1][j+1]==0){// moves right
							Checker first = new Checker(i,j,value);
							Checker last = new Checker(i-1,j+1,value);
							Turn turn = new Turn(first,last,null);
							simpleTurns.add(turn);
						}
					}
				}
				if(board[i][j] == value+3){
					Checker ch = new Checker(i,j,value);
					queenMove(board,ch);
				}
			}
		}
	}
	
	
	private void moveBlack(int [][] board,int value){ // shavebis chveulebrivi svla
		for(int i = 1; i<8; i++){
			for(int j = 0;j<8;j++){
				if(board[i][j] == value){
					if(j==0 && board[i+1][j+1]==0){ // if checker is on the left boarder
						Checker first = new Checker(i,j,value);
						Checker last = new Checker(i+1,j+1,value);
						Turn turn = new Turn(first,last,null);
						simpleTurns.add(turn);
					}
					if(j==7 && board[i+1][j-1]==0){ // if checker is on the right boarder
						Checker first = new Checker(i,j,value);
						Checker last = new Checker(i+1,j-1,value);
						Turn turn = new Turn(first,last,null);
						simpleTurns.add(turn);
					}
					if(j>0 && j<7) { // if checker is in the middle
						if (board[i+1][j-1]==0) { // moves left
							Checker first = new Checker(i,j,value);
							Checker last = new Checker(i+1,j-1,value);
							Turn turn = new Turn(first,last,null);
							simpleTurns.add(turn);
							
						}
						if (board[i+1][j+1]==0){// moves right
							Checker first = new Checker(i,j,value);
							Checker last = new Checker(i+1,j+1,value);
							Turn turn = new Turn(first,last,null);
							simpleTurns.add(turn);
						}
					}
				}
				if(board[i][j] == value+3){
					Checker ch = new Checker(i,j,value+3);
					queenMove(board,ch);
				}
			}
		}
	}
	private void queenMove(int [][] board,Checker ch){  //damkis modzraoba 
		int i = ch.row;
		int j = ch.col;
		while(i-1>=0 && j+1<8 && board[i-1][j+1]==0){  //zevit marjvniv 
			Turn turn = new Turn(ch, new Checker(i-1,j+1,ch.value),null);
			queenMoves.add(turn);
			i=i-1;
			j=j+1;
//			System.out.println("zevit marjvniv");
		}
		i = ch.row;
		j = ch.col;
		while(i-1>=0 && j-1>=0 && board[i-1][j-1]==0){  // zevit marcxniv 
			Turn turn = new Turn(ch, new Checker(i-1,j-1,ch.value),null);
			queenMoves.add(turn);
			i=i-1;
			j=j-1;
//			System.out.println("zevit marcxniv");
		}
		i = ch.row;
		j = ch.col;
		while(i+1<8 && j-1>=0 && board[i+1][j-1]==0){ // qvevit marcxniv 
			Turn turn = new Turn(ch, new Checker(i+1,j-1,ch.value),null);
			queenMoves.add(turn);
			i=i+1;
			j=j-1;
//			System.out.println("qvevit marcxniv");
		}
		i = ch.row;
		j = ch.col;
		while(i+1<8 && j+1<8 && board[i+1][j+1]==0){  //qvevit marjvniv 
			Turn turn = new Turn(ch, new Checker(i+1,j+1,ch.value),null);
			queenMoves.add(turn);
			i=i+1;
			j=j+1;
//			System.out.println("qvevit marjvniv");
		}
		i = ch.row;
		j = ch.col;
		
	}

	private void killReqursion(int board [][],Checker ch, Checker st){
		if (CanKill(board, ch)) {
			if (st == null) {
				st = new Checker(ch.row, ch.col, ch.value);
			}
		}	
		else{ 
			Checker l = new Checker(ch.row,ch.col,ch.value);
			Turn turn = new Turn(st,l, (ArrayList<Checker>)killedCheckers.clone());
			simpleKills.add(turn);
			killedCheckers.clear();
		}
		if(checkUpLeft(board, ch)){
			board [ch.row][ch.col]= 0;
			int color =board [ch.row -1][ch.col -1]=0;
			board [ch.row -1][ch.col -1]=0;
			board [ch.row -2][ch.col -2]=ch.value;
			killedCheckers.add(new Checker(ch.row -1,ch.col -1,board[ch.row -1][ch.col -1]));
			killReqursion(board, new Checker(ch.row -2,ch.col -2,ch.value), st);
			board[ch.row -2][ch.col -2] = 0;
			board[ch.row][ch.col] = ch.value;
			board[ch.row -1][ch.col -1] = color;
		}
		if(checkUpRigth(board, ch)){
			board [ch.row][ch.col]= 0;
			int color =board [ch.row -1][ch.col +1]=0;
			board [ch.row -1][ch.col +1]=0;
			board [ch.row -2][ch.col +2]=ch.value;
			killedCheckers.add(new Checker(ch.row -1,ch.col +1,board[ch.row -1][ch.col +1]));
			killReqursion(board, new Checker(ch.row -2,ch.col +2,ch.value), st);
			board[ch.row -2][ch.col +2] = 0;
			board[ch.row][ch.col] = ch.value;
			board[ch.row -1][ch.col +1] = color;
		}
		if(checkDownLeft(board, ch)){
			board [ch.row][ch.col] = 0;
			int color =board [ch.row +1][ch.col -1]=0;
			board [ch.row +1][ch.col -1] = 0;
			board [ch.row +2][ch.col -2] = ch.value;
			killedCheckers.add(new Checker(ch.row +1,ch.col -1,board[ch.row +1][ch.col -1]));
			killReqursion(board, new Checker(ch.row +2,ch.col -2,ch.value), st);
			board[ch.row +2][ch.col -2] = 0;
			board[ch.row][ch.col] = ch.value;
			board[ch.row +1][ch.col -1] = color;
		}
		if(checkDownRigth(board, ch)){
			board [ch.row][ch.col]= 0;
			int color =board [ch.row +1][ch.col +1]=0;
			board [ch.row +1][ch.col +1]=0;
			board [ch.row +2][ch.col +2]=ch.value;
			killedCheckers.add(new Checker(ch.row +1,ch.col +1,board[ch.row +1][ch.col +1]));
			killReqursion(board, new Checker(ch.row +2,ch.col +2,ch.value),st);
			board[ch.row +2][ch.col +2] = 0;
			board[ch.row][ch.col] = ch.value;
			board[ch.row +1][ch.col +1] = color;
		}
	}
	
	
	private boolean CanKill(int [][]board, Checker ch){
			if(checkDownLeft(board, ch) || checkDownRigth(board, ch) || checkUpLeft(board, ch) || checkUpRigth(board, ch)){
				return true;
			}				
		return false;
	}
	
	
	
	private boolean checkUpRigth(int [][] board,Checker  ch){  //amocmebs kvlas zemot marjvniv
		if(ch.row -2>=0 && ch.col +2<board.length){
			if(board[ch.row -1][ch.col +1]!=0 &&  board[ch.row -1][ch.col +1]!=ch.value && board[ch.row -1][ch.col +1]!=ch.value +3){
				if(board[ch.row -2][ch.col +2]==0){
					return true;
				}
			}
		}
		return false;
	}
	private boolean checkUpLeft(int [][] board,Checker  ch){ // amomcmebs mokvlas zemot marcxniv
		if(ch.row -2>=0 && ch.col -2>=0){
			if(board[ch.row -1][ch.col -1]!=0 &&  board[ch.row -1][ch.col -1]!=ch.value && board[ch.row -1][ch.col -1]!=ch.value +3){
				if(board[ch.row -2][ch.col -2]==0){
					return true;
				}
			}
		}
		return false;
		
	}
	private boolean checkDownLeft(int [][] board,Checker  ch){ // amocmebs svlas qvemot marcxniv
		if(ch.row +2<board.length && ch.col -2>=0){
			if(board[ch.row +1][ch.col -1]!=0 &&  board[ch.row +1][ch.col -1]!=ch.value && board[ch.row +1][ch.col -1]!=ch.value +3){
				if(board[ch.row +2][ch.col -2]==0){
					return true;
				}
			}
		}
		return false;
		
	}
	private boolean checkDownRigth(int [][] board,Checker  ch){ // amomcebs svlas qvemot marjvniv
		if(ch.row +2<board.length && ch.col +2<board.length){
			if(board[ch.row +1][ch.col +1]!=0 &&  board[ch.row +1][ch.col +1]!=ch.value && board[ch.row +1][ch.col +1]!=ch.value +3){
				if(board[ch.row +2][ch.col +2]==0){
					return true;
				}
			}
		}
		return false;	
	}

}
