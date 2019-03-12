package player.students.natroshvili;

import java.util.ArrayList;
import java.util.HashMap;


/*
 * mokled didi wvalebis shemdeg, mivxdvi rom bolomde damweri ar var am davalebis, ager es aris davalebis naxevarze karga metia shesrulebuli,
 * yvela metodi romelic miweria gamartulad mushaobs, tavis rekursianad da tavis CANKILLebit da MOVEbit.
 * ideac dzalian kargad mesmis alfa beta kvetis da min-max-is teoriazec, tumca ver mivedi maqamde, razec didad gixdit bodishs
 * imedia nashroms damipasebt da naxavt chem unikalur kods. :)
 * 						
 * 						ptiviscemit da gulistkivilebit zurab natroshvili
 */
import main.*;


public class implementation implements Player {
    public void gameOver() {}
	
	public static void main(String[] args) {
		int [][] d = new int[8][8];
//		d[0][7] = WHITE;
//		d[1][6] = BLACK;
//		d[1][4] = BLACK;
//		d[1][2] = BLACK;
//		d[3][2] = BLACK;
//		d[5][2] = BLACK;
//		d[3][4] = BLACK;
//		d[5][4] = BLACK;
//		d[3][6] = BLACK;
//		d[5][6] = BLACK;
//		d[5][4] = BLACK;

		
		
//		ArrayList<int[][]> arr = new ArrayList<int[][]>();
//		KillCheker(d, WHITE, 0, 7, arr);
//		System.out.println(arr.size());
//		oneMove(d, BLACK, 6, 1);
//		canKillForKings1(d, WHITE, 0, 1, 3, 4);
//		canKillForKings2(d, WHITE, 0, 1, 3, 4);
//		canKillForKings3(d, WHITE, 0, 1, 3, 4);
//		canKillForKings4(d, WHITE, 0, 1, 3, 4);
//		System.out.println(canKillForKings1(d, WHITE, 0, 1, 3, 4));
	}
	
	private static final int EMPTY_SPACE = 0, WHITE = 1, BLACK = 2, WHITE_DAMKA = 4, BLACK_DAMKA = 5;

	
	
	private static boolean IsInBorder(int[][] d, int row, int col){
		if(row >= 0 && col >=0 && row <= d.length-1 && col <=d.length-1){
			return true;
		}
		return false;
	}
	
	private static boolean canKill1(int[][]d, int value, int row, int col){
		if(IsInBorder(d, row-2, col-2)){
			if(d[row-1][col-1] !=value && d[row-1][col-1] !=value+3 && d[row-1][col-1]!=0){
				if(d[row-2][col-2]==0){
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean canKill2(int[][]d, int value, int row, int col){
		if(IsInBorder(d, row+2, col-2)){
			if(d[row+1][col-1] !=value && d[row+1][col-1] !=value+3 && d[row+1][col-1]!=0){
				if(d[row+2][col-2]==0){
					return true;
				}
			}	
		}
		return false;		
	}	
	private static boolean canKill3(int[][]d, int value, int row, int col){	
		if(IsInBorder(d, row-2, col+2)){
			if(d[row-1][col+1] !=value && d[row-1][col+1] !=value+3 && d[row-1][col+1]!=0){
				if(d[row-2][col+2]==0){
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean canKill4(int[][]d, int value, int row, int col){	
		if(IsInBorder(d, row+2, col+2)){
			if(d[row+1][col+1] !=value && d[row+1][col+1] !=value+3 && d[row+1][col+1]!=0){
				if(d[row+2][col+2]==0){
					return true;
				}
			}
		}				
		return false;
	}
	
///////////////////////////////////////////////////////////
	private static void KillCheker(int [][]d, int value, int row, int col, ArrayList<int[][]> arr){
		int [][] d1 = new int [8][8];
		if(IsInBorder(d, row, col)){
			if(canKill4(d, value, row, col)){
				for(int i=0; i<d1.length; i++){
					for(int j=0; j<d1.length; j++){
						d1[i][j] = d[i][j];						
					}
				}
				d1[row+2][col+2] = d1[row][col];
				d1[row][col]=0;
				d1[row+1][col+1]=0;
				if(!canKill1(d1, value, row+2, col+2)){
					if(d1[row+2][col+2]==2 && row+2==7){
						d1[row+2][col+2]+=3;
					}
					arr.add(d1);
				}
				 else {
						KillCheker(d1, value, row+2, col+2, arr);
				}
			}
		}
		
		if(IsInBorder(d, row, col)){
			if(canKill3(d, value, row, col)){
				for(int i=0; i<d1.length; i++){
					for(int j=0; j<d1.length; j++){
						d1[i][j] = d[i][j];						
					}
				}
				d1[row-2][col+2] = d1[row][col];
				d1[row][col]=0;
				d1[row-1][col+1]=0;
				if(!canKill1(d1, value, row-2, col+2)){
					if(d1[row-2][col+2]==1 && row-2==0){
						d1[row-2][col+2]+=3;
					}
					arr.add(d1);
				}
				 else {
						KillCheker(d1, value, row-2, col+2, arr);
				}
			}
		}
		
		if(IsInBorder(d, row, col)){
			if(canKill2(d, value, row, col)){
				for(int i=0; i<d1.length; i++){
					for(int j=0; j<d1.length; j++){
						d1[i][j] = d[i][j];						
					}
				}
				d1[row+2][col-2] = d1[row][col];
				d1[row][col]=0;
				d1[row+1][col-1]=0;
				if(!canKill1(d1, value, row+2, col-2)){
					if(d1[row+2][col-2]==2 && row+2==7){
						d1[row+2][col-2]+=3;
					}
					arr.add(d1);
				}
				 else {
						KillCheker(d1, value, row+2, col-2, arr);
				}
			}
		}
		
		if(IsInBorder(d, row, col)){
			if(canKill1(d, value, row, col)){
				for(int i=0; i<d1.length; i++){
					for(int j=0; j<d1.length; j++){
						d1[i][j] = d[i][j];						
					}
				}
				d1[row-2][col-2] = d1[row][col];
				d1[row][col]=0;
				d1[row-1][col-1]=0;
				if(!canKill1(d1, value, row+2, col-2)){
					if(d1[row-2][col-2]==1 && row-2==0){
						d1[row-2][col-2]+=3;
					}
					arr.add(d1);
				}
				 else {
						KillCheker(d1, value, row-2, col-2, arr);
				}
			}
		}			
	}
///////////////////////////////////////////////	
	
	
	private static boolean makeKing(int [][]d, int value, int row, int col){
		if(value==WHITE && row==0){
			return true;
		}
		if(value == BLACK && row==7){
			return true;
		}
		return false;
	}
	
///////////////////////////////////////////////	
	private static void oneMove(int [][]d, int value, int row, int col){
		ArrayList<Turn> arr = new ArrayList<>();
		if(IsInBorder(d, row, col) && value ==WHITE && row>0 && col>0 && col<7){
			if(d[row-1][col+1]==0){
				Checker c =new Checker(row, col, value);
				if(makeKing(d, value, row-1, col+1)){
					value += 3;
				}
				Checker c1 =new Checker(row-1, col+1, value);				
				Turn t1 = new Turn(c, c1, null);
				arr.add(t1);
			}
			if(d[row-1][col-1]==0){
				Checker c =new Checker(row, col, value);
				if(makeKing(d, value, row-1, col-1)){
					value += 3;
				}
				Checker c1 =new Checker(row-1, col-1, value);				
				Turn t1 = new Turn(c, c1, null);
				arr.add(t1);
			}
		}
				
		if(IsInBorder(d, row, col) && value ==BLACK && row<7 && col>0 && col<7 ){
			if(d[row+1][col-1]==0){
				Checker c =new Checker(row, col, value);
				if(makeKing(d, value, row+1, col-1)){
					value += 3;
				}
				Checker c1 =new Checker(row+1, col-1, value);				
				Turn t1 = new Turn(c, c1, null);
				arr.add(t1);
			}
			if(d[row+1][col-1]==0){
				Checker c =new Checker(row, col, value);
				if(makeKing(d, value, row+1, col-1)){
					value += 3;
				}
				Checker c1 =new Checker(row+1, col-1, value);				
				Turn t1 = new Turn(c, c1, null);
				arr.add(t1);
			}
		}
				
		if(IsInBorder(d, row, col) && value == WHITE && col==0){
			if(d[row-1][col+1]==0){
				Checker c =new Checker(row, col, value);
				if(makeKing(d, value, row-1, col+1)){
					value += 3;
				}
				Checker c1 =new Checker(row-1, col+1, value);				
				Turn t1 = new Turn(c, c1, null);
				arr.add(t1);
			}
		}
		
		if(IsInBorder(d, row, col) && value == WHITE && col==7){
			if(d[row-1][col-1]==0){
				Checker c =new Checker(row, col, value);
				if(makeKing(d, value, row-1, col-1)){
					value += 3;
				}
				Checker c1 =new Checker(row-1, col-1, value);				
				Turn t1 = new Turn(c, c1, null);
				arr.add(t1);
			}
		}
		
		if(IsInBorder(d, row, col) && value == BLACK && col==0){
			if(d[row+1][col+1]==0){
				Checker c =new Checker(row, col, value);
				if(makeKing(d, value, row+1, col+1)){
					value += 3;
				}
				Checker c1 =new Checker(row+1, col+1, value);				
				Turn t1 = new Turn(c, c1, null);
				arr.add(t1);
			}
		}
		
		if(IsInBorder(d, row, col) && value == BLACK && col==7){
			if(d[row+1][col-1]==0){
				Checker c =new Checker(row, col, value);
				if(makeKing(d, value, row+1, col-1)){
					value += 3;
				}
				Checker c1 =new Checker(row+1, col-1, value);				
				Turn t1 = new Turn(c, c1, null);
				arr.add(t1);
			}
		}
	}
////////////////////////////////////////////////////
	

	
	private static boolean canKillForKings1(int [][] d, int value, int x, int y, int x1, int y1) {
		if(IsInBorder(d, x+x1+1, y+y1+1)){
			if(d[x1][y1]!=value && d[x1+1][y1+1]==0 && d[x1][y1]!=value+3 && d[x1][y1]!=0){
				return true;
			}
		}
		return false;
	}

	private static boolean canKillForKings2(int [][] d, int value, int x, int y, int x1, int y1) {
		if(IsInBorder(d, x-x1-1, y+y1+1)){
			if(d[x1][y1]!=value && d[x1-1][y1+1]==0 && d[x1][y1]!=value+3 && d[x1][y1]!=0){
				return true;
			}
		}
		return false;
	}

	private static boolean canKillForKings3(int [][] d, int value, int x, int y, int x1, int y1) {
		if(IsInBorder(d, x+x1+1, y-y1-1)){
			if(d[x1][y1]!=value && d[x1+1][y1-1]==0 && d[x1][y1]!=value+3 && d[x1][y1]!=0){
				return true;
			}
		}
		return false;
	}

	private static boolean canKillForKings4(int [][] d, int value, int x, int y, int x1, int y1) {
		if(IsInBorder(d, x-x1-1, y-y1-1)){
			if(d[x1][y1]!=value && d[x1-1][y1-1]==0 && d[x1][y1]!=value+3 && d[x1][y1]!=0){
				return true;
			}
		}
		return false;
	}

//////////////////////////////////////////////////////////

	private static void moveKing(int [][]d, int value, int x, int y){
		while(IsInBorder(d, x, y)){
			
		}
	}
	
	
	
	
	
	
	
	
//	private static void kingKill(int [][]d, int value, int x, int y, int x1, int y1 ){
//		if(IsInBorder(d, x, y)){
//			if(canKillForKings1(d, value, x, y, x1, y1)){
//				
//			}
//		}
//	}
	
	
	
	
	public Turn makeTurne(int[][] d, int value) {

		return null;
	}
}
