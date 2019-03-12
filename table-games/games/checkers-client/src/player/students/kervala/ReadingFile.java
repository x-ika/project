package player.students.kervala;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;


public class ReadingFile {
private static  final  int boardSize =8;	
	public static void main(String [] args) throws FileNotFoundException{
		Scanner sc = new Scanner(new FileReader("board.txt"));
		int [][] board = new int[boardSize][boardSize];
		String st = "";
		for(int i = 0;i<boardSize;i++){
			st = "";
			for(int j = 0;j<boardSize;j++){
				board[i][j] = sc.nextInt();
				st +=" " +  board[i][j];
			}
//			System.out.println(st);
		}
		possibleTurns poss = new possibleTurns();
		poss.makeTurne(board,2);
	}
}
