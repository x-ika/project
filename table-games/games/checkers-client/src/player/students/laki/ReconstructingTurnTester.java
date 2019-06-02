package player.students.laki;
import main.*;

import java.io.*;

public class ReconstructingTurnTester {
	public static void main(String[] args) throws IOException {
		BufferedReader rd = new BufferedReader(new FileReader("reconstruct.txt"));
		int[][] a = new int[8][8], b = new int[8][8];
		readArray(a, rd);
		rd.readLine();
		readArray(b, rd);
		Board init = new Board(a, 1);
		Board after = new Board(b, 2);
		Turn t = Board.reconstructTurn(init, after);
//		System.out.println(t);
	}
	
	static void readArray(int[][] a, BufferedReader rd) throws IOException{
		for(int i=0; i<8; i++){
			String line = rd.readLine();
			for(int j=0; j<8; j++){
				int num = Integer.parseInt(""+line.charAt(j));
				a[i][j] = num;
			}
		}
	}
	
}
