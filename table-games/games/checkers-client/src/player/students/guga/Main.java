package player.students.guga;

import main.*;

import java.util.Vector;

public class Main {
    public static void main(String[] args) {
        int[][] b = new int[8][8];
        fill(b);

//		Player White = new GuggerPlayer(1);
//		Player Black = new GuggerPlayer(6);
//		playerGame(b, White, Black);
//		randomGame(b);
    }

    private static void playerGame(int[][] b, Player pl1, Player pl2) {
        print(b);
        Turn t = pl1.makeTurne(b, 1);

        for (int i = 0; t != null; i++) {
            perform(t, b);
            print(b);
//			System.out.println(i);
            if ((i + 1) % 2 + 1 == 1)
                t = pl1.makeTurne(b, (i + 1) % 2 + 1);
            if ((i + 1) % 2 + 1 == 2)
                t = pl2.makeTurne(b, (i + 1) % 2 + 1);
        }
    }

    private static void randomGame(int[][] b) {
        print(b);
        GuggerTurnsGetter tg = new GuggerTurnsGetter(b, 1);
        Vector<Turn> turns = tg.getAllPossibleTurns();

        for (int i = 0; turns.size() > 0 && i < 1000; i++) {
            int r = (int) (Math.random() * turns.size());
            Turn t = turns.get(r);
            perform(t, b);
            print(b);
            tg = new GuggerTurnsGetter(b, (i + 1) % 2 + 1);
            turns = tg.getAllPossibleTurns();
        }
    }

    private static void fill(int[][] b) {
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (i < 3 && (i + j) % 2 == 1)
                    b[i][j] = 2;
                else if (i > 4 && (i + j) % 2 == 1)
                    b[i][j] = 1;
                else
                    b[i][j] = 0;
            }
        }
    }

    private static void print(int[][] b) {
//		System.out.println(" --------------- ");
//		for (int i = 0; i < b.length; i++) {
//			for (int j = 0; j < b.length; j++) {
//				System.out.print(" ");
//				if(b[i][j] == 0)
//					System.out.print("·");
//				if(b[i][j] == 1)
//					System.out.print("O");
//				if(b[i][j] == 2)
//					System.out.print("@");
//				if(b[i][j] == 4)
//					System.out.print("H");
//				if(b[i][j] == 5)
//					System.out.print("#");
//
//			}
//			System.out.println("");
//		}
    }

    private static void perform(Turn t, int[][] d) {
        d[t.last.col][t.last.row] = t.last.value;
        d[t.first.col][t.first.row] = 0;
        for (int i = 0; i < t.killed.size(); i++) {
            d[t.killed.get(i).col][t.killed.get(i).row] = 0;
        }
    }

}
