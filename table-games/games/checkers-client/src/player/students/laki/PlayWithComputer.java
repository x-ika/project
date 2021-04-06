package player.students.laki;

import main.*;

import java.io.*;
import java.util.*;

public class PlayWithComputer {
    public static void main(String[] args) throws IOException {
        BufferedReader rd = new BufferedReader(new FileReader("input.txt"));
        int[][] input = new int[8][8];
        for (int i = 0; i < 8; i++) {
            String line = rd.readLine();
            for (int j = 0; j < 8; j++) {
                int cur = Integer.parseInt(line.charAt(j) + "");
                input[i][j] = cur;
            }
        }
        currentBoard = new Board(input, 1);
        PlayerByLashaLakirbaia comp = new PlayerByLashaLakirbaia();
        while (true) {
//			System.out.println(currentBoard);
//			System.out.println("score: "+currentBoard.score(currentBoard.getValue()));
//			System.out.println("Computer's turn:");
            Turn compsTurn = comp.makeTurne(currentBoard.board, 1);
            makeMove(compsTurn);
//			System.out.println(currentBoard);
//			System.out.println("score: "+currentBoard.score(currentBoard.getValue()));
//			System.out.println("Player's turn: ");
            Turn t = enterTurn();
            makeMove(t);
        }
    }

    static Turn enterTurn() {
        while (true) {
            int x = sc.nextInt(), y = sc.nextInt(), x1 = sc.nextInt(), y1 = sc.nextInt();
            if (x < 0 || y < 0 || x1 < 0 || y1 < 0 || x1 >= 8 || y1 >= 8 || x >= 8 || y >= 8) {
//				System.out.println("Error. Re-type.");
                continue;
            }
            int numKilled = sc.nextInt();
            ArrayList<Checker> list = new ArrayList<>();
            for (int i = 0; i < numKilled; i++) {
                int xi = sc.nextInt(), yi = sc.nextInt();
                list.add(new Checker(xi, yi, 0));
            }
            String st = sc.next();
//			System.out.println(currentBoard.getAt(x, y));
            if (st.equals("ok"))
                return new Turn(new Checker(x, y, 0), new Checker(x1, y1, currentBoard.getAt(x, y)), list);
//			System.out.println("Error. Try again.");
        }
    }

    static void makeMove(Turn turn) {
        Checker fir = turn.first, last = turn.last;
//		System.out.println(fir.row +" "+fir.col +" "+last.row +" "+last.col +" "+last.value);
        List<? extends Checker> list = turn.killed;
        for (int i = 0; i < list.size(); i++) {
            Checker cur = list.get(i);
            currentBoard.setAt(cur.row, cur.col, 0);
        }
        currentBoard.setAt(fir.row, fir.col, 0);
        currentBoard.setAt(last.row, last.col, last.value);
    }

    static Board currentBoard;

    static Scanner sc = new Scanner(System.in);

}
