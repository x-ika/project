package player.students.laki;
import main.*;
import java.util.*;

public class Board{
	
	public Board(int[][] d, int value){
		board = d;
		white = value==1;
		whichColor = white? WHITE: BLACK;
		dir = white? -1: 1;
		val = value;
	}
	
	public void setValue(int newVal){
		val = newVal;
		white = val==1;
		whichColor = white? WHITE: BLACK;
		dir = white? -1: 1;
	}
	
	public static final int VERY_BAD = -100000000, VERY_GOOD = 100000000;
	
	public int score(int initialValue){
		if(getAllBoardsAfterSingleMove().size()==0) return VERY_BAD;
		int score = 0;
		for(int i=0; i<BOARD_SIZE; i++){
			for(int j=0; j<BOARD_SIZE; j++){
				int value = getAt(i, j);
				if(value==0) continue;
				if(value%3==initialValue) score += (value<3? 1: 5);
				else score -= (value<3? 1: 5);
			}
		}
		return score;
	}
	
	// Returns the Turn making which caused the board "initial" to become the board "afterMove".
	public	static Turn reconstructTurn(Board initial, Board afterMove){
//		System.out.println("the board after move is ");
//		System.out.println(afterMove);
		ArrayList<Checker> allKilling = initial.getKillingCheckers();
		return allKilling.size()==0? reconstructFromOrdinary(initial, afterMove):
			reconstructFromKilling(initial, afterMove, allKilling);
	}
	
	public static Turn reconstructFromKilling(Board initial, Board afterMove, ArrayList<Checker> killingPieces){
		for(int i=0; i<killingPieces.size(); i++){
			int x = killingPieces.get(i).row, y = killingPieces.get(i).col;
			boolean ord = initial.isOrdinaryPiece(x, y);
			if(!ord){
				Checker fir = new Checker(x, y, initial.getAt(x, y));
				Turn t = searchForTurnInRecursiveQueenKills(initial, afterMove, x, y, new ArrayList<>(), fir, fir);
				if(t!=null) return t;
			}
			else{
				Checker first = new Checker(x, y, initial.getAt(x, y));
				Checker last = new Checker(x, y, initial.getAt(x, y));
				Turn t = searchForTurnInRecursiveOrdinaryKills(initial, afterMove, x, y, new ArrayList<>(), first, last);
				if(t!=null) return t;
			}
		}
		return null;
	}
	
	private void recursiveOrdinaryKill(int x, int y, ArrayList<Board> all, int value){
		if(!canKill(x, y)){
			Board b = copy();
			b.setValue(3-value);
			all.add(b);
			return;
		}
		killWithOrdinaryPieceOnDir(x, y, -1, -1, all, value);
		killWithOrdinaryPieceOnDir(x, y, -1, 1, all, value);
		killWithOrdinaryPieceOnDir(x, y, 1, -1, all, value);
		killWithOrdinaryPieceOnDir(x, y, 1, 1, all, value);
	}
	
	private void killWithOrdinaryPieceOnDir(int x, int y, int dx, int dy, ArrayList<Board> list, int value){
		if(!canKillOnDirection(x, y, dx, dy)) return;
		Board afterMove = killWithOrdinaryPiece(x, y, dx, dy);
		if(isQueenLine(x+2*dx, y+2*dy)) afterMove.recursiveQueenKillFrom(x+2*dx, y+2*dy, list, value);
		else afterMove.recursiveOrdinaryKill(x+2*dx, y+2*dy, list, value); 
	}
	
	private static Turn searchForTurnInRecOrdKillsOnDir(Board init, Board after, int x, int y, int dx, int dy,
			ArrayList<Checker> list, Checker first, Checker last){
		if(areSameBoards(init, after)) return list.size()==0? null: new Turn(first, last, list);
		if(!init.canKillOnDirection(x, y, dx, dy)) return null;
		Board afterKilling = init.killWithOrdinaryPiece(x, y, dx, dy);
		Checker last1 = new Checker(x+2*dx, y+2*dy, afterKilling.getAt(x+2*dx, y+2*dy));
		list.add(new Checker(x+dx, y+dy, init.getAt(x+dx, y+dy)));
		return !init.isQueenLine(x+2*dx, y+2*dy)? searchForTurnInRecursiveOrdinaryKills(afterKilling, after, x+2*dx, y+2*dy, 
				list, first, last1): searchForTurnInRecursiveQueenKills(afterKilling, after, x+2*dx, y+2*dy, list, first, last1);
	}
	
	private static Turn searchForTurnInRecursiveOrdinaryKills(Board init, Board after, int x, int y, ArrayList<Checker> list,
			Checker first, Checker last){
		if(areSameBoards(init, after)) return new Turn(first, last, list);
		if(!init.canKill(x, y)) return null;
		ArrayList<Checker> copy = copyArrayList(list);
		Turn t = searchForTurnInRecOrdKillsOnDir(init, after, x, y, -1, -1, copy, first, last);
		if(t!=null) return t;
		copy = copyArrayList(list);
		t = searchForTurnInRecOrdKillsOnDir(init, after, x, y, -1, 1, copy, first, last);
		if(t!=null) return t;
		copy = copyArrayList(list);
		t = searchForTurnInRecOrdKillsOnDir(init, after, x, y, 1, -1, copy, first, last);
		if(t!=null) return t;
		copy = copyArrayList(list);
		t = searchForTurnInRecOrdKillsOnDir(init, after, x, y, 1, 1, copy, first, last);
		return t;
	}
	
	private void recursiveQueenKillFrom(int x, int y, ArrayList<Board> all, int value){
		if(!canKill(x, y)){
			Board b = copy();
			b.setValue(3-value);
			all.add(b);
			return;
		}
		killWithQueenOnDir(x, y, -1, -1, all, value);
		killWithQueenOnDir(x, y, -1, 1, all, value);
		killWithQueenOnDir(x, y, 1, -1, all, value);
		killWithQueenOnDir(x, y, 1, 1, all, value);
	}

	private boolean isQueenLine(int x, int y){
		return (white && x==0) || (!white && x==7);
	}
	
	private void killWithQueenOnDir(int x, int y, int dx, int dy, ArrayList<Board> list, int value){
		if(!canKillOnDirection(x, y, dx, dy)) return;
		int num = 1;
		for(num=1; num<=BOARD_SIZE; num++){
			if(canKill(x, y, dx, dy, num, false)){
				break;
			}
		}
		ArrayList<Checker> allowed = listOfPiecesThatCanKillAfterKilling(x, y, dx, dy, num);
		if(allowed.size()==0){ // all is allowed.
			for(int i=num+1; i<=BOARD_SIZE; i++){
				int x1 = x+dx*i, y1 = y+dy*i;
				if(!isInBounds(x1, y1) || getAt(x1, y1)!=0) return;
				Board copy = copy();
				int cur = copy.getAt(x, y);
				copy.setAt(x, y, 0);
				copy.setAt(x+num*dx, y+num*dy, 0);
				copy.setAt(x1, y1, cur);
				copy.setValue(3-value);
			//	if(copy.getValue()==getValue()) System.out.println("ty");
				list.add(copy);
			}
		}
		else{
			for(int i=0; i<allowed.size(); i++){
				int curX = allowed.get(i).row, curY = allowed.get(i).col;
				Board copy = copy();
				int cur = copy.getAt(x, y);
				copy.setAt(x, y, 0);
				copy.setAt(x+num*dx, y+num*dy, 0);
				copy.setAt(curX, curY, cur);
				copy.recursiveQueenKillFrom(curX, curY, list, value);
			}
		}
	}
	
	private static Turn searchForTurnInRecQueenKillsOnDir(Board init, Board after, int x, int y, int dx, int dy, 
			ArrayList<Checker> list, Checker first, Checker last){
		if(areSameBoards(init, after)) return list.size()==0? null: new Turn(first, last, list);
		if(!init.canKillOnDirection(x, y, dx, dy)) return null;
		int num = 1;
		for(num=1; num<=BOARD_SIZE; num++) if(init.canKill(x, y, dx, dy, num, false)) break;
		ArrayList<Checker> allowed = init.listOfPiecesThatCanKillAfterKilling(x, y, dx, dy, num);
		if(allowed.size()==0){
			for(int i=num+1; i<=BOARD_SIZE; i++){
				int x1 = x+i*dx, y1 = y+i*dy;
				if(!isInBounds(x1, y1) || init.getAt(x1, y1)!=0) return null;
				Board copy = init.copy();
				int cur = copy.getAt(x, y);
				copy.setAt(x, y, 0);
				int val = init.getAt(x+num*dx, y+num*dy);
				copy.setAt(x+num*dx, y+num*dy, 0);
				copy.setAt(x1, y1, cur);
				if(areSameBoards(copy, after)){
					Checker last1 = new Checker(x1, y1, cur);
					list.add(new Checker(x+num*dx, y+num*dy, val));
					return new Turn(first, last1, list);
				}
			}
		}
		else{
			for(int i=0; i<allowed.size(); i++){
				int curX = allowed.get(i).row, curY = allowed.get(i).col;
				Board copy = init.copy();
				int cur = init.getAt(x, y);
				int val = init.getAt(x+num*dx, y+num*dy);
				copy.setAt(x, y, 0);
				copy.setAt(x+num*dx, y+num*dy, 0);
				copy.setAt(curX, curY, cur);
				ArrayList<Checker> copyList = copyArrayList(list);
				copyList.add(new Checker(x+num*dx, y+num*dy, val));
				Turn t = searchForTurnInRecursiveQueenKills(copy, after, curX, curY, copyList, first, new Checker(curX, curY, cur));
				if(t!=null) return t;
			}
		}
		return null;
	}
	
	private ArrayList<Checker> listOfPiecesThatCanKillAfterKilling(int x, int y, int dx, int dy, int num){
		ArrayList<Checker> list = new ArrayList<>();
		for(int i=num+1; i<=BOARD_SIZE; i++){
			int x1 = x+i*dx, y1 = y+i*dy;
			if(!isInBounds(x1, y1)) return list;
			if(getAt(x1, y1)!=0) return list;
			Board copy = copy();
			int cur = copy.getAt(x, y);
			copy.setAt(x, y, 0);
			copy.setAt(x+num*dx, y+num*dy, 0);
			copy.setAt(x1, y1, cur);
			if(copy.canKill(x1, y1)) list.add(new Checker(x1, y1, getAt(x1, y1)));
		}
		return list;
	}
	
	private static Turn searchForTurnInRecursiveQueenKills(Board init, Board after, int x, int y, ArrayList<Checker> list, 
			Checker first, Checker last){
		if(areSameBoards(init, after)) return list.size()==0? null: new Turn(first, last, list);
		if(!init.canKill(x, y)) return null;
		ArrayList<Checker> copy = copyArrayList(list);
		Turn t = searchForTurnInRecQueenKillsOnDir(init, after, x, y, -1, -1, copy, first, last);
		if(t!=null) return t;
		copy = copyArrayList(list);
		t = searchForTurnInRecQueenKillsOnDir(init, after, x, y, -1, 1, copy, first, last);
		if(t!=null) return t;
		copy = copyArrayList(list);
		t = searchForTurnInRecQueenKillsOnDir(init, after, x, y, 1, -1, copy, first, last);
		if(t!=null) return t;
		copy = copyArrayList(list);
		t = searchForTurnInRecQueenKillsOnDir(init, after, x, y, 1, 1, copy, first, last);
		return t;
	}
	
	private static Turn reconstructFromOrdinary(Board initial, Board afterMove){
		for(int i=0; i<BOARD_SIZE; i++){
			for(int j=0; j<BOARD_SIZE; j++){
				if(initial.isMyPiece(i, j)){
					if(initial.isOrdinaryPiece(i, j)){
						if(checkOrdinaryMove(initial, afterMove, i, j, i-1, j-1))
							return getOrdinaryPieceOrdinaryTurn(initial, afterMove, i, j, i-1, j-1);
						if(checkOrdinaryMove(initial, afterMove, i, j, i-1, j+1))
							return getOrdinaryPieceOrdinaryTurn(initial, afterMove, i, j, i-1, j+1);
						if(checkOrdinaryMove(initial, afterMove, i, j, i+1, j-1))
							return getOrdinaryPieceOrdinaryTurn(initial, afterMove, i, j, i+1, j-1);
						if(checkOrdinaryMove(initial, afterMove, i, j, i+1, j+1))
							return getOrdinaryPieceOrdinaryTurn(initial, afterMove, i, j, i+1, j+1);
					}
					else{
						Checker last = new Checker(0, 0, 0);
						if(checkQueensOrdinaryMove(initial, afterMove, i, j, -1, -1, last))
							return getQueensOrdinaryMoveTurn(initial, afterMove, i, j, -1, -1, last);
						if(checkQueensOrdinaryMove(initial, afterMove, i, j, -1, 1, last))
							return getQueensOrdinaryMoveTurn(initial, afterMove, i, j, -1, 1, last);
						if(checkQueensOrdinaryMove(initial, afterMove, i, j, 1, -1, last))
							return getQueensOrdinaryMoveTurn(initial, afterMove, i, j, 1, -1, last);
						if(checkQueensOrdinaryMove(initial, afterMove, i, j, 1, 1, last))
							return getQueensOrdinaryMoveTurn(initial, afterMove, i, j, 1, 1, last);
					}
				}
			}
		}
		return null;
	}
	
	private static Turn getQueensOrdinaryMoveTurn(Board init, Board after, int x, int y, int dx, int dy, Checker last){
		Checker first = new Checker(x, y, init.getAt(x, y));
		return new Turn(first, last, new ArrayList<>());
	}
	
	private static boolean checkQueensOrdinaryMove(Board init, Board after, int x, int y, int dx, int dy, Checker last){
		if(!init.isMyPiece(x, y) || init.isOrdinaryPiece(x, y)) return false;
		for(int num=1; num<=BOARD_SIZE; num++){
			int x1 = x+dx*num, y1 = y+dy*num;
			if(!isInBounds(x1, y1)) return false;
			if(init.getAt(x1, y1)!=0) return false;
			Board copy = init.copy();
			copy.setAt(x1, y1, init.getAt(x, y));
			copy.setAt(x, y, 0);
			if(areSameBoards(copy, after)){
				last.row = x1;
				last.col = y1;
				last.value = after.getAt(x1, y1);
				return true;
			}
		}
		return false;
	}
	
	private static Turn getOrdinaryPieceOrdinaryTurn(Board init, Board after, int x, int y, int x1, int y1){
		Checker first = new Checker(x, y, init.getAt(x, y));
		Checker last = new Checker(x1, y1, after.getAt(x1, y1));
		ArrayList<Checker> killed = new ArrayList<>();
		return new Turn(first, last, killed);
	}
	
	private static boolean checkOrdinaryMove(Board init, Board after, int x, int y, int x1, int y1){
		if(!init.isMyPiece(x, y) || !init.isOrdinaryPiece(x, y)) return false;
		if(!init.isLegalOrdinaryMove(x, y, x1, y1)) return false;
		Board copy = init.copy();
		if(!init.isQueenLine(x1, y1)) copy.setAt(x1, y1, copy.getAt(x, y));
		else copy.setAt(x1, y1, copy.getAt(x, y)+3);
		copy.setAt(x, y, 0);
		return areSameBoards(copy, after);
	}
	
	public static boolean areSameBoards(Board a, Board b){
		for(int i=0; i<BOARD_SIZE; i++)
			for(int j=0; j<BOARD_SIZE; j++)
				if(a.getAt(i, j)!=b.getAt(i, j)) return false;
		return true;
	}
	
	private static ArrayList<Checker> copyArrayList(ArrayList<Checker> v){
		ArrayList<Checker> copy = new ArrayList<>();
		for(int i=0; i<v.size(); i++) copy.add(v.get(i));
		return copy;
	}
	
	public int getValue(){
		return val;
	}
	
	public void setAt(int x, int y, int piece){
		board[x][y] = piece;
	}
	
	public int getAt(int x, int y){
		return board[x][y];
	}
	
	public ArrayList<Board> getAllBoardsAfterSingleMove(){
		ArrayList<Checker> allKilling = getKillingCheckers();
		ArrayList<Board> all = new ArrayList<>();
		if(allKilling.size()==0){
			makeAllOrdinaryMoves(all);
		}
		else{
			makeAllKillingMoves(all, allKilling);
		}
		return all;
	}
	
	private void makeAllKillingMoves(ArrayList<Board> all, ArrayList<Checker> killingPieces){
		for(int i=0; i<killingPieces.size(); i++){
			int x = killingPieces.get(i).row, y = killingPieces.get(i).col;
			boolean ord = isOrdinaryPiece(x, y);
			if(!ord) recursiveQueenKillFrom(x, y, all, val);
			else recursiveOrdinaryKill(x, y, all, val);
		}
	}
	
	private Board killWithOrdinaryPiece(int x, int y, int dx, int dy){
		Board copy = copy();
		int cur = copy.getAt(x, y);
		if(isQueenLine(x+2*dx, y+2*dy)) copy.setAt(x+2*dx, y+2*dy, cur+3);
		else copy.setAt(x+2*dx, y+2*dy, cur);
		copy.setAt(x, y, 0);
		copy.setAt(x+dx, y+dy, 0);
		return copy;
	}
	
	private void makeAllOrdinaryMoves(ArrayList<Board> all){
		for(int i=0; i<BOARD_SIZE; i++){
			for(int j=0; j<BOARD_SIZE; j++){
				if(isMyPiece(i, j)){
					if(isOrdinaryPiece(i, j)){
						tryToMakeOrdinaryMove(i, j, i-1, j-1, all, val);
						tryToMakeOrdinaryMove(i, j, i-1, j+1, all, val);
						tryToMakeOrdinaryMove(i, j, i+1, j-1, all, val);
						tryToMakeOrdinaryMove(i, j, i+1, j+1, all, val);
					}
					else{
						moveQueenOrdinarilyToDirection(i, j, -1, -1, all, val);
						moveQueenOrdinarilyToDirection(i, j, -1, 1, all, val);
						moveQueenOrdinarilyToDirection(i, j, 1, -1, all, val);
						moveQueenOrdinarilyToDirection(i, j, 1, 1, all, val);
					}
				}
			}
		}
	}
	
	private void moveQueenOrdinarilyToDirection(int x, int y, int dx, int dy, ArrayList<Board> all, int value){
		for(int i=1; i<=BOARD_SIZE; i++){
			int x1 = x+dx*i, y1 = y+dy*i;
			if(!isInBounds(x1, y1) || getAt(x1, y1)!=0) return;
			Board b = makeQueenMove(x, y, x1, y1);
			b.setValue(3-value);
			all.add(b);
		}
	}
	
	private Board makeQueenMove(int x, int y, int x1, int y1){
		int cur = getAt(x, y);
		Board copy = copy();
		copy.setAt(x, y, 0);
		copy.setAt(x1, y1, cur);
	//	if(copy.getValue()==getValue()) System.out.println("ty");
		return copy;
	}
	
	private void tryToMakeOrdinaryMove(int x, int y, int x1, int y1, ArrayList<Board> all, int value){
		if(isLegalOrdinaryMove(x, y, x1, y1)){
			Board b = makeOrdinaryMove(x, y, x1, y1);
			b.setValue(3-value);
			all.add(b);
		}
	}
	
	private boolean isLegalOrdinaryMove(int x, int y, int x1, int y1){
		return isInBounds(x1, y1) && getAt(x1, y1)==0 && (x1-x==dir);
	}
	
	private Board copy(){
		int[][] arr = new int[BOARD_SIZE][BOARD_SIZE];
		for(int i=0; i<BOARD_SIZE; i++)
			for(int j=0; j<BOARD_SIZE; j++)
				arr[i][j] = getAt(i, j);
		return new Board(arr, getValue());
	}
	
	private Board makeOrdinaryMove(int x, int y, int x1, int y1){
		Board copy = copy();
		int cur = copy.getAt(x, y);
		if(!isQueenLine(x1, y1)) copy.setAt(x1, y1, cur);
		else copy.setAt(x1, y1, cur+3);
		copy.setAt(x, y, 0);
		//if(copy.getValue()==getValue()) System.out.println("ty");
		copy.setValue(3-val);
		return copy;
	}
	
	private ArrayList<Checker> getKillingCheckers(){
		ArrayList<Checker> allPossible = new ArrayList<>();
		for(int x=0; x<BOARD_SIZE; x++){
			for(int y=0; y<BOARD_SIZE; y++){
				if(isMyPiece(x, y)){
					if(canKill(x, y)){
						allPossible.add(new Checker(x, y, getAt(x, y)));
					}
				}
			}
		}
		return allPossible;
	}
	
	private static boolean isInBounds(int x, int y){
		return x>=0 && y>=0 && x<BOARD_SIZE && y<BOARD_SIZE;
	}
	
	private boolean isMyPiece(int x, int y){
		int rem = getAt(x, y)%3;
		return rem==whichColor;
	}
	
	private boolean canKill(int x, int y){
		return canKillOnDirection(x, y, -1, -1) || canKillOnDirection(x, y, -1, 1) || canKillOnDirection(x, y, 1, -1) ||
				canKillOnDirection(x, y, 1, 1);
	}
	
	private boolean canKillOnDirection(int x, int y, int dx, int dy){
		boolean isOrdinary = isOrdinaryPiece(x, y);
		for(int i=1; i<=BOARD_SIZE; i++){
			if(!isInBounds(x+dx*i, y+dy*i)) return false;
			if(canKill(x, y, dx, dy, i, isOrdinary)) return true;
		}
		return false;
	}
	
	private boolean canKill(int x, int y, int dx, int dy, int num, boolean ordinary){
		int x1 = x+dx*num, y1 = y+dy*num;
		if(getAt(x1, y1)%3!=3-whichColor) return false; // if the piece is not enemy's.
		int nextX = x+dx*(num+1), nextY = y+dy*(num+1);
		if(!isInBounds(nextX, nextY)) return false; // can't kill if the next piece is out of bound.
		if(getAt(nextX, nextY)!=0) return false; // can't kill if the next checker is occupied.
		if(ordinary) return num==1;
		return allEmptyOnDiagonalBetween(x, y, dx, dy, num-1);
	}
	
	private boolean allEmptyOnDiagonalBetween(int x, int y, int dx, int dy, int num){
		for(int i=1; i<=num; i++){
			int cur = getAt(x+i*dx, y+i*dy);
			if(cur!=0) return false;
		}
		return true;
	}
	
	private boolean isOrdinaryPiece(int x, int y){
		return getAt(x,y) <= 2;
	}
	
	private boolean white;
	
	static final int BOARD_SIZE = 8;
	
	static final int WHITE = 1, BLACK = 2;
	
	private int whichColor;
	
	private int dir;
	
	private int val;
	
	public int[][] board;
	
	public String toString(){
		String res = "";
		for(int i=0; i<BOARD_SIZE; i++){
			for(int j=0; j<BOARD_SIZE; j++){
				res += getAt(i, j);
			}
			res += '\n';
		}
		return res;
	}
	 
}