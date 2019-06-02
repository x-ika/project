package player.students.laki;

//import acm.graphics.*;
//import acm.program.*;
//import acmx.export.java.io.FileReader;


public class GUIPlayer extends Object {
//	public void run(){
//		try{
////			System.out.println("read");
//			BufferedReader rd = new BufferedReader(new FileReader("input.txt"));
//			int[][] input = new int[8][8];
//			for(int i=0; i<8; i++){
//				String line = rd.readLine();
//				for(int j=0; j<8; j++){
//					int cur = Integer.parseInt(line.charAt(j)+"");
//					input[i][j] = cur;
//				}
//			}
//			rd.close();
//			Board init = new Board(input, 1);
//			play2(init);
//		}catch(Exception ex){
//			ex.printStackTrace();
//		}
//	}
//
//	public void play(Board b){
//		Scanner sc = new Scanner(System.in);
//		Board currentBoard = b;
//		PlayerByLashaLakirbaia comp = new PlayerByLashaLakirbaia();
//		while(true){
//			removeAll();
//		//	System.out.println(currentBoard);
//			drawBoard(currentBoard);
//		//	System.out.println("score: "+currentBoard.score());
////			System.out.println("Computer's turn:");
//			Turn compsTurn = comp.makeTurne(currentBoard.board, 1);
//			makeMove(compsTurn, b);
//		//	System.out.println(currentBoard);
//			drawBoard(currentBoard);
//	//		System.out.println("score: "+currentBoard.score());
////			System.out.println("Player's turn: ");
//			Turn t = enterTurn(b, sc);
//			makeMove(t, b);
//		}
//	}
//
//	public void play2(Board b){
//		Scanner sc = new Scanner(System.in);
//		Board currentBoard = b;
//		PlayerByLashaLakirbaia comp = new PlayerByLashaLakirbaia();
//		while(true){
//			removeAll();
//		//	System.out.println(currentBoard);
//			drawBoard(currentBoard);
//	//		System.out.println("score: "+currentBoard.score());
////			System.out.println("Player's turn: ");
//			Turn t = enterTurn(b, sc);
//			makeMove(t, b);
////			System.out.println(currentBoard);
//			drawBoard(currentBoard);
//		//	System.out.println("score: "+currentBoard.score());
////			System.out.println("Computer's turn:");
//			Turn compsTurn = comp.makeTurne(currentBoard.board, 2);
//			makeMove(compsTurn, b);
//		}
//	}
//
//	static Turn enterTurn(Board b, Scanner sc){
//		while(true){
//			int x = sc.nextInt(), y = sc.nextInt(), x1 = sc.nextInt(), y1 = sc.nextInt();
//			if(x<0 || y<0 || x1<0 || y1<0 || x1>=8 || y1>=8 || x>=8 || y>=8){
////				System.out.println("Error. Re-type.");
//				continue;
//			}
//			int numKilled = sc.nextInt();
//			ArrayList<Checker> list = new ArrayList<Checker>();
//			for(int i=0; i<numKilled; i++){
//				int xi = sc.nextInt(), yi = sc.nextInt();
//				list.add(new Checker(xi, yi, 0));
//			}
//			String st = sc.next();
////			System.out.println(b.getAt(x, y));
//			if(st.equals("ok")) return new Turn(new Checker(x,y,0), new Checker(x1,y1,b.getAt(x, y)), list);
//			else return new Turn(new Checker(x,y,0), new Checker(x1,y1,b.getAt(x, y)<3?
//					b.getAt(x, y)+3: b.getAt(x, y)), list);
//		}
//	}
//
//	static void makeMove(Turn turn, Board b){
//		Checker fir = turn.first, last = turn.last;
////		System.out.println(fir.row +" "+fir.col +" "+last.row +" "+last.col +" "+last.value);
//		List<? extends Checker> list = turn.killed;
//		boolean appearedInQueenLine = false;
//
//		for(int i=0; i<list.size(); i++){
//			Checker cur = list.get(i);
//			b.setAt(cur.row, cur.col, 0);
//		}
//		b.setAt(fir.row, fir.col, 0);
//		b.setAt(last.row, last.col, last.value);
//	}
//
//	public void drawBoard(Board b){
//		for(int i=0; i<8; i++){
//			for(int j=0; j<8; j++){
//				GRect rect = new GRect(j*UNIT_SIZE, i*UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
//				if((i+j)%2==0) rect.setFillColor(Color.BLACK);
//				else rect.setFillColor(Color.RED);
//				rect.setFilled(true);
//				add(rect);
//			}
//		}
//		for(int i=0; i<8; i++){
//			for(int j=0; j<8; j++){
//				GOval piece = getPiece(b.getAt(i, j), j*UNIT_SIZE, i*UNIT_SIZE);
//				if(piece==null) continue;
//				add(piece);
//			}
//		}
//	}
//
//	public GOval getPiece(int val, int x, int y){
//		if(val==0) return null;
//		Color col = val%3==1? Color.white: Color.yellow;
//		GOval ov;
//		if(val<3) ov = new GOval(x, y, UNIT_SIZE, UNIT_SIZE);
//		else ov = new GOval(x+0.1*UNIT_SIZE, y+0.1*UNIT_SIZE, 0.8*UNIT_SIZE, 0.8*UNIT_SIZE);
//		ov.setFillColor(col);
//		ov.setFilled(true);
//		ov.setVisible(true);
//		return ov;
//	}
//
//	public static final int UNIT_SIZE = 60;
	
	
	
}
