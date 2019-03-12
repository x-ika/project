package player.students.deme;

import main.*;

import java.util.*;


public class DemesPlayer implements Player {
    public void gameOver() {}

	private static final int HEIGHT = 8; // row count on board
	private static final int WIDTH = 8; // column count on board
	
	private static final int EMPTY = 0; // empty place on board
	private static final int WHITE = 1; // white checker on board
	private static final int BLACK = 2; // black checker on board
	private static final int WHITED = 4; // white damka on board
	private static final int BLACKD = 5; // black damka on board
	
	private static final int DEPTH = 6; // main depth of recursive search algorithm
	
	private static int ME; // my playing color
	private static int myDamka; // my damka :)
	private static int AD; // advesery's color
	@SuppressWarnings("unused")
	private static int adDamka; // advesery's damka :)
	
	private static int[][] board; // board duplicate.
	private static Checker[][] checkerBoard;
	
	private static ArrayList<Checker> myCheckers; // my checker list
	private static ArrayList<Checker> adCheckers; // adveserys checker list
	
	private static Turn myBestTurn;
	
	private static int myMax = Integer.MIN_VALUE;
	public DemesPlayer(){
		checkerBoard = new Checker[HEIGHT][WIDTH];
		ME = WHITE;
		myDamka = WHITED;
		AD = BLACK;
		adDamka = BLACKD;
	}
	
	public Turn makeTurne(int[][] d, int value) {
		myCheckers = new ArrayList<>();
		adCheckers = new ArrayList<>();
		myBestTurn = null;
		board = copyArr(d);
		ME = value;
		setColors();
		makeCheckerLists();
		myTurn(board);
        swap(myBestTurn.first);
        swap(myBestTurn.last);
        for (Checker checker : myBestTurn.killed) {
            swap(checker);
        }
        return myBestTurn;
	}

    private static void swap(Checker checker) {
        int t = checker.row;
        checker.row = checker.col;
        checker.col = t;
        checker.col = 7 - checker.col;
        checker.row = 7 - checker.row;
        checker.value = 1;
    }

    private static void printBoard(int[][] b){
//		System.out.println("\n = = = = = = = = = = ");
//		System.out.print("   ");
//		for (int i = 0; i < b[0].length; i ++){
//			System.out.print(" " + i);
//		}
//		System.out.println("");
//		for (int i = 0 ; i < b.length; i ++){
//			System.out.print(" " + i + " ");
//			for (int  j = 0; j < b[0].length; j ++){
//				System.out.print(" " + b[i][j]);
//			}
//			System.out.println("");
//		}
//		System.out.println("\n");
	}
	
	// sets my damka color
	private static void setColors(){
		if (ME == BLACK){
			myDamka = BLACKD;
			AD = WHITE;
			adDamka = WHITED;
		}
	}
	
	private static void makeCheckerLists(){
		for (int i = 0 ; i < board.length; i ++){
			for (int j = 0 ; j < board[0].length; j ++){
				checkerBoard[i][j] = new Checker(j,i,board[i][j]);
				if (board[i][j] == ME || board[i][j] == myDamka){
					myCheckers.add(new Checker(j, i, board[i][j]));
				}
				else if (board[i][j] != EMPTY){
					adCheckers.add(new Checker(j, i, board[i][j]));
				}
			}
		}
	}
	

	
	private void myTurn(int[][] b){
//		System.out.println("My Turn");
		Checker next;
		for (int i = 0; i < myCheckers.size(); i ++){
			next = myCheckers.get(i);
			if (ME == WHITE){
				makeMoveWhite(next.row, next.col, next.value, new Stack<>(), new Stack<>(), board, next.row, next.col, DEPTH);
			}
			else{
				makeMoveBlack(next.row, next.col, next.value, new Stack<>(), new Stack<>(), board, next.row, next.col, DEPTH);
			}
		}
		printBoard(board);	
	}
	
	private int adveserysTurn(int[][] b, int dep){
		Checker next;
		int ret = Integer.MIN_VALUE;
		for (int i = 0; i < adCheckers.size(); i ++){
			next = adCheckers.get(i);
			if (ME == WHITE){
				ret = makeMoveBlack(next.row, next.col, next.value, new Stack<>(), new Stack<>(), board, next.row, next.col, dep);
			}
			else{
				ret = makeMoveWhite(next.row, next.col, next.value, new Stack<>(), new Stack<>(), board, next.row, next.col, dep);
			}
		}
		return ret;
	}
	

	
	private Turn getTurn(Stack<Checker> killed, Stack<Position> path, int[][] b){
		List<Checker> nList = new ArrayList<>();
		Iterator<Checker> it = killed.iterator();
		while(it.hasNext()){
			nList.add(it.next());
		}
		
		int yF = path.peek().x;
		int xF = path.peek().y;
		
		Position lastPos = path.pop();
		int yL = lastPos.y;
		int xL = lastPos.x;
		
		Checker f = b[yF][xF] == WHITE || b[yF][xF] == WHITED || b[yF][xF] == BLACK || b[yF][xF] == BLACKD ? new Checker(xF,yF,b[yF][xF]) : null;
		Checker l = b[yL][xL] == WHITE || b[yL][xL] == WHITED || b[yL][xL] == BLACK || b[yL][xL] == BLACKD ? new Checker(xL,yL,b[yL][xL]) : null;
		
		return new Turn(f, l, nList);
	}
	
	
	private int makeMoveWhite(int x, int y, int val, Stack<Checker> killed, Stack<Position> path, int[][] b, int x0, int y0, int dep){
//		System.out.println("Making move with White X0: " + x0 + ", Y0: " + y0 + " ||  X: " + x + ", Y: " + y);
		int[][] curBoard = copyBoard(b);
		int max = -1;
		//printBoard(curBoard);
		
		if (dep > 0){
			// make move or kill for top left side
			if (x-2 >= 0 && y-2 >= 0){
				if (board[y-2][x-2] == EMPTY){
					if (board[y-1][x-1] == BLACK || board[y-1][x-1] == BLACKD){
						killed.push(checkerBoard[y-1][x-1]);
						path.push(new Position(x-2,y-2));
						curBoard[y-1][x-1] = EMPTY;
						curBoard[y-2][x-2] = curBoard[y][x];
						curBoard[y][x] = EMPTY;
						if (ME == WHITE){
							max = makeMoveWhite(x-2, y-2, val, killed, path, curBoard, x0, y0, dep-1) - adveserysTurn(curBoard, dep-1);
							if (max > myMax){
								myMax = max;
								Turn curTurn = getTurn(killed, path, curBoard);
								myBestTurn = curTurn;
							}
							else{
								path.pop();
							}
						}
						
						killed.pop();
					}
					else if (board[y-1][x-1] == EMPTY){
						path.push(new Position(x-1,y-1));
						
						curBoard[y-2][x-2] = curBoard[y][x];
						curBoard[y-1][x-1] = EMPTY;
						
						if (ME == WHITE){
							max = makeMoveWhite(x-1, y-1, val, killed, path, curBoard, x0, y0, dep-1) - adveserysTurn(curBoard, dep-1);
							if (max > myMax){
								myMax = max;
								Turn curTurn = getTurn(killed, path, curBoard);
								myBestTurn = curTurn;
							}
							else{
								path.pop();
							}
						}


					}
				}
				else if (board[y-1][x-1] == EMPTY){
					path.push(new Position(x-1,y-1));
					
					curBoard[y-1][x-1] = curBoard[y][x];
					curBoard[y][x] = EMPTY;
					
					if (ME == WHITE){
						max = makeMoveWhite(x-1, y-1, val, killed, path, curBoard, x0, y0, dep-1) - adveserysTurn(curBoard, dep-1);
						if (max > myMax){
							myMax = max;
							Turn curTurn = getTurn(killed, path, curBoard);
							myBestTurn = curTurn;
						}
						else{
							path.pop();
						}
						
					}


				}
				
			}
			
			// make move or kill for top right side
			if (x+2 < board[0].length  && y-2 >= 0){
				if (board[y-2][x+2] == EMPTY){
					if (board[y-1][x+1] == BLACK || board[y-1][x+1] == BLACKD){
						killed.push(checkerBoard[y-1][x+1]);
						path.push(new Position(x+1,y-1));
						
						curBoard[y-1][x+1] = EMPTY;
						curBoard[y-2][x+2] = curBoard[y][x];
						curBoard[y][x] = EMPTY;
						
						if (ME == WHITE){
							max =  makeMoveWhite(x+2, y-2, val, killed, path, curBoard, x0, y0, dep-1) - adveserysTurn(curBoard, dep-1);
							if (max > myMax){
								myMax = max;
								Turn curTurn = getTurn(killed, path, curBoard);
								myBestTurn = curTurn;
							}
							else{
								path.pop();
							}
						}


						killed.pop();
					}
					else if (board[y-1][x+1] == EMPTY){
						path.push(new Position(x+1,y-1));
						
						curBoard[y-1][x+1] = curBoard[y][x];
						curBoard[y][x] = EMPTY;
						
						if (ME == WHITE){
							max = makeMoveWhite(x+1, y-1, val, killed, path, curBoard, x0, y0, dep-1) - adveserysTurn(curBoard, dep-1);
							if (max > myMax){
								myMax = max;
								Turn curTurn = getTurn(killed, path, curBoard);
								myBestTurn = curTurn;
							}
							else{
								path.pop();
							}
						}


					}
				}
				else if (board[y-1][x+1] == EMPTY){
					path.push(new Position(x+1,y-1));
					
					curBoard[y-1][x+1] = curBoard[y][x];
					curBoard[y][x] = EMPTY;
					
					if (ME == WHITE){
						max = makeMoveWhite(x+1, y-1, val, killed, path, curBoard, x0, y0, dep-1) - adveserysTurn(curBoard, dep-1);
						if (max > myMax){
							myMax = max;
							Turn curTurn = getTurn(killed, path, curBoard);
							myBestTurn = curTurn;
						}
						else{
							path.pop();
						}
					}


				}
			}
			
			// make kill for bottom left side
			if (x-2 >= 0  && y + 2 < board.length){
				if (board[y+2][x-2]==EMPTY){
					if (!killed.contains(checkerBoard[y+1][x-1]) && board[y+1][x-1] == BLACK || board[y+1][x-1] == BLACKD){
						killed.push(checkerBoard[y+1][x-1]);
						path.push(new Position(x-1,y+1));
						
						curBoard[y+1][x-1] = EMPTY;
						curBoard[y+2][x-2] = curBoard[y][x];
						curBoard[y][x] = EMPTY;
						
						if (ME == WHITE){
							max = makeMoveWhite(x-2, y+2, val, killed, path, curBoard, x0, y0, dep-1) - adveserysTurn(curBoard, dep-1);
							if (max > myMax){
								myMax = max;
								Turn curTurn = getTurn(killed, path, curBoard);
								myBestTurn = curTurn;
							}
							else{
								path.pop();
							}
						}


						killed.pop();
					}
				}
			}
			
			// make kill for bottom right side
			if (x+2 < board.length  && y + 2 < board.length){
				if (board[y+2][x+2]==EMPTY){
					if (!killed.contains(checkerBoard[y+1][x+1]) && board[y+1][x+1] == BLACK || board[y+1][x+1] == BLACKD){
						killed.push(checkerBoard[y+1][x+1]);
						path.push(new Position(x+1,y+1));
						
						curBoard[y+1][x+1] = EMPTY;
						curBoard[y+2][x+2] = curBoard[y][x];
						curBoard[y][x] = EMPTY;
						
						if (ME == WHITE){
							max = makeMoveWhite(x+2, y+2, val, killed, path, curBoard, x0, y0, dep-1) - adveserysTurn(curBoard, dep-1);
							if (max > myMax){
								myMax = max;
								Turn curTurn = getTurn(killed, path, curBoard);
								myBestTurn = curTurn;
							}
							else{
								path.pop();
							}
						}


						killed.pop();
					}
				}
			}
			
		}
		else{
			return ME == WHITE ? evaluate(curBoard, ME) : evaluate (curBoard, AD);
		}
		return 0;
	}
	
	private int makeMoveBlack(int x, int y, int val, Stack<Checker> killed, Stack<Position> path, int[][] b,  int x0, int y0, int dep){
//		System.out.println("Making move with Black X0: " + x0 + ", Y0: " + y0 + " ||  X: " + x + ", Y: " + y);
		
		int[][] curBoard = copyBoard(b);
		int max = -1;
		printBoard(curBoard);
		
		if (dep > 0){
			// make kill for top left side
			if (x-2 >= 0 && y-2 >= 0){
				if (board[y-2][x-2]==EMPTY){
					if (!killed.contains(checkerBoard[y-1][x-1]) && board[y-1][x-1] == WHITE || board[y-1][x-1] == WHITED){
						
						killed.push(checkerBoard[y-1][x-1]);
						path.push(new Position(x-1,y-1));
						
						curBoard[y-1][x-1] = EMPTY;
						curBoard[y-2][x-2] = curBoard[y][x];
						curBoard[y][x] = EMPTY;
						
						if (ME == BLACK){
							max = makeMoveBlack(x-2, y-2, val, killed, path, curBoard, x0, y0, dep-1) - adveserysTurn(curBoard, dep-1);
							if (max > myMax){
								myMax = max;
								Turn curTurn = getTurn(killed, path, curBoard);
								myBestTurn = curTurn;
							}
							else{
								path.pop();
							}
						}


						
						killed.pop();
					}
				}
			}
			
			// make kill for top right side
			if (x+2 < board[0].length  && y-2 >= 0){
				if (board[y-2][x+2]==EMPTY){
					if (!killed.contains(checkerBoard[y-1][x+1]) && board[y-1][x+1] == WHITE || board[y-1][x+1] == WHITED){
						
						killed.push(checkerBoard[y-1][x+1]);
						path.push(new Position(x+1,y-1));
						
						curBoard[y-1][x+1] = EMPTY;
						curBoard[y-2][x+2] = curBoard[y][x];
						curBoard[y][x] = EMPTY;
						
						if (ME == BLACK){
							max = makeMoveBlack(x+2, y-2, val, killed,path, curBoard, x0, y0, dep-1) - adveserysTurn(curBoard, dep-1);
							if (max > myMax){
								myMax = max;
								Turn curTurn = getTurn(killed, path, curBoard);
								myBestTurn = curTurn;
							}
							else{
								path.pop();
							}
						}


						killed.pop();
					}
				}
			}
			
			// make move or kill for bottom left side
			if (x-2 >= 0  && y + 2 < board.length){
				if (board[y+2][x-2]==EMPTY){
					if (board[y+1][x-1] == WHITE || board[y+1][x-1] == WHITED){
						
						killed.push(checkerBoard[y+1][x-1]);
						path.push(new Position(x-1,y+1));
						
						curBoard[y+1][x-1] = EMPTY;
						curBoard[y+2][x-2] = curBoard[y][x];
						curBoard[y][x] = EMPTY;
						
						if (ME == BLACK){
							max = makeMoveBlack(x-2, y+2, val, killed,path, curBoard, x0, y0, dep-1) - adveserysTurn(curBoard, dep-1);
							if (max > myMax){
								myMax = max;
								Turn curTurn = getTurn(killed, path, curBoard);
								myBestTurn = curTurn;
							}
							else{
								path.pop();
							}
						}


						killed.pop();
					}
					else if (board[y+1][x-1] == EMPTY){
						path.push(new Position(x-1,y+1));
						
						curBoard[y+1][x-1] = curBoard[y][x];
						curBoard[y][x] = EMPTY;
						
						if (ME == BLACK){
							max = makeMoveBlack(x-1, y+1, val, killed,path, curBoard, x0, y0, dep-1) - adveserysTurn(curBoard, dep-1);
							if (max > myMax){
								myMax = max;
								Turn curTurn = getTurn(killed, path, curBoard);
								myBestTurn = curTurn;
							}
							else{
								path.pop();
							}
						}

						
					}
				}
				else if (board[y+1][x-1] == EMPTY){
					path.push(new Position(x-1,y+1));
					
					curBoard[y+1][x-1] = curBoard[y][x];
					curBoard[y][x] = EMPTY;
					
					if (ME == BLACK){
						max =  makeMoveBlack(x-1, y+1, val, killed,path, curBoard, x0, y0, dep-1) - adveserysTurn(curBoard, dep-1);
						if (max > myMax){
							myMax = max;
							Turn curTurn = getTurn(killed, path, curBoard);
							myBestTurn = curTurn;
						}
						else{
							path.pop();
						}
					}

					
				}
			}
			
			// make move or kill for bottom right side
			if (x + 2 < board.length && y + 2 < board.length){
				if (board[y+2][x+2]==EMPTY){
					if (board[y+1][x+1] == WHITE || board[y+1][x+1] == WHITED){
						
						killed.push(checkerBoard[y+1][x+1]);
						path.push(new Position(x+1,y+1));
						
						curBoard[y+1][x+1] = EMPTY;
						curBoard[y+2][x+2] = curBoard[y][x];
						curBoard[y][x] = EMPTY;
						
						if (ME == BLACK){
							max = makeMoveBlack(x+2, y+2, val, killed,path, curBoard, x0, y0, dep-1) - adveserysTurn(curBoard, dep-1);
							if (max > myMax){
								myMax = max;
								Turn curTurn = getTurn(killed, path, curBoard);
								myBestTurn = curTurn;
							}
							else{
								path.pop();
							}
						}

						
						killed.pop();
					}
					else if (board[y+1][x+1] == EMPTY){
						path.push(new Position(x+1,y+1));
						
						curBoard[y+1][x+1] = curBoard[y][x];
						curBoard[y][x] = EMPTY;
						
						if (ME == BLACK){
							max = makeMoveBlack(x+1, y+1, val, killed,path, curBoard, x0, y0, dep-1) - adveserysTurn(curBoard, dep-1);
							if (max > myMax){
								myMax = max;
								Turn curTurn = getTurn(killed, path, curBoard);
								myBestTurn = curTurn;
							}
							else{
								path.pop();
							}
						}

					}
				}
				else if (board[y+1][x+1] == EMPTY){
					path.push(new Position(x+1,y+1));
					
					curBoard[y+1][x+1] = curBoard[y][x];
					curBoard[y][x] = EMPTY;
					
					if (ME == BLACK){
						max = makeMoveBlack(x+1, y+1, val, killed,path, curBoard, x0, y0, dep-1) - adveserysTurn(curBoard, dep-1);
						if (max > myMax){
							myMax = max;
							Turn curTurn = getTurn(killed, path, curBoard);
							myBestTurn = curTurn;
						}
						else{
							path.pop();
						}
					}
					else{
						max = adveserysTurn(curBoard, dep-1) - makeMoveBlack(x+1, y+1, val, killed,path, curBoard, x0, y0, dep-1);
						
					}

				}
			}
		}
		else{
			return ME == BLACK ? evaluate(curBoard, ME) : evaluate (curBoard, AD);
		}
		return -1;
	}
	
	private int[][] copyBoard(int[][] b){
		int[][] ret = new int[b.length][b[0].length];
		for (int i = 0; i < b.length; i++){
			for (int j = 0 ; j < b[0].length; j++){
				ret[i][j] = b[i][j];
			}
		}
		return ret;
	}
	
	private int evaluate(int[][] b, int player){
		int ret = 0;
		for (int i = 0; i < b.length; i ++){
			for (int j = 0 ; j < b[0].length; j ++){
				if (player == BLACK){
					if (b[i][j] == WHITE){
						ret--;
					}
					else if(b[i][j] == WHITED){
						ret -=5;
					}
					else if (b[i][j] == BLACK){
						ret ++;
					}
					else if(b[i][j] == BLACKD){
						ret += 5;
					}
				}
				else if (player == WHITE){
					if (b[i][j] == WHITE){
						ret++;
					}
					else if(b[i][j] == WHITED){
						ret +=5;
					}
					else if (b[i][j] == BLACK){
						ret --;
					}
					else if(b[i][j] == BLACKD){
						ret -= 5;
					}
				}
			}
		}	
		return ret;
	}
	
	private class Position{
		public int x;
		public int y;
		public Position(int x, int y){
			this.x = x;
			this.y = y;
		}
	}
	
	
	
	
	private static int[][] copyArr(int[][] toCopy){
		int[][] newArr = new int[toCopy.length][toCopy[0].length];
		for (int i = 0; i < toCopy.length; i ++){
			for (int j = 0; j < toCopy[0].length; j ++){
				newArr[i][j] = toCopy[i][j];
			}
		}
		return newArr;
	}

}
