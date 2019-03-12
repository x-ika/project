package player.students.jilberta;
import main.*;

public class mainClass {
	private static Board board;
	private static int player;
	private static final int WHITE = 1;
	private static final int BLACK = 2;
	private static final int WHITEKING = 4;
	private static final int BLACKKING = 5;
	private static final int EMPTY = 0;
	private static final int N = 8;
	private static int whites;
	private static int blacks;
	
	
	public static void main(String [] args){
		CheckerGame gm = new CheckerGame();
		Turn tr = gm.makeTurne(d(), WHITE);
		
//		System.out.println(tr.first + "  "+tr.last+ " "+tr.killed);
		
		
//		if(validBlackJump(from, to, board)){
//			Turn tr = new Turn (from.copy(), to.copy(), new LinkedList<JChecker>());
//			tr.killed.add(board.getJChecker(from.upLeftMove()).copy());
//			userJump(from, to, board);
//			System.out.println(board);
//			System.out.println(tr.first);
//			System.out.println(tr.last);
//			System.out.println(tr.killed.get(0));
//		}
		
//		board.getJChecker(new JChecker(7, 6, WHITE)).makeKing();
//		moveJChecker(board.getJChecker(new JChecker(2,3, EMPTY)), board.getJChecker(new JChecker(6, 5, EMPTY)), board);
//		moveJChecker(board.getJChecker(new JChecker(2,3, EMPTY)), board.getJChecker(new JChecker(2, 1, EMPTY)), board);
//		moveJChecker(board.getJChecker(new JChecker(1,0, BLACK)), board.getJChecker(new JChecker(5, 4, EMPTY)), board);
//		System.out.println(board);
//		Board brd = board.copy();
//		ArrayList<Move> moves = new ArrayList<Move>();
//		findKingJumps(from, brd, moves, WHITEKING);
//		for(int i=0; i<moves.size(); i++){
//			System.out.println(moves.get(i).isJump());
//			board = move(brd, moves.get(i), WHITEKING);
//			System.out.println(board);
//		}
		
//		int turn = WHITE;
//		Board brd = null;
//		Board mda = null;
//		Move mv = null;
//		int k = 0;
//		while(k < 10){
//			if(turn == WHITE){
//				brd = board.copy();
//				brd.getCameFrom().clear();
//				mda = miniMaxAB(brd, 9, WHITE, minusInfinity(), plusInfinity());
//				System.out.println(mda.getCameFrom().get(0));
//				mv = mda.getCameFrom().get(0);
//				board = move(board, mv, turn);
//			}else{
//				brd = board.copy();
//				brd.getCameFrom().clear();
//				mda = miniMaxAB(brd, 3, BLACK, minusInfinity(), plusInfinity());
//				System.out.println(mda.getCameFrom().get(0));
//				mv = mda.getCameFrom().get(0);
//				board = move(board, mv, turn);
//			}
//			turn = user(turn);
//			System.out.println(board);
//			System.out.println();
//			k++;
//		}
		
//		brd = board.copy();
//		brd.getCameFrom().clear();
//		mda = miniMaxAB(brd, 9, WHITE, minusInfinity(), plusInfinity());
//		System.out.println(mda.getCameFrom().get(0));
//		mv = mda.getCameFrom().get(0);
//		board = move(board, mv, turn);
//		System.out.println(board);
		
	}
	
	private static int [][] d(){
		int [][] mas = new int [N][N];
		
		for(int i=0; i<3; i++){
			for(int j=0; j<N; j++){
				if(i%2 == 0 && j%2 == 1){
					mas[i][j] = BLACK;
				}else if(i%2 == 1 && j%2 == 0){
					mas[i][j] = BLACK;
				}
			}
		}
		for(int i=3; i<5; i++){
			for(int j=0; j<N; j++){
				if(i%2 == 0 && j%2 == 1){
					mas[i][j] = EMPTY;
				}else if(i%2 == 1 && j%2 == 0){
					mas[i][j] = EMPTY;
				}
			}
		}
		for(int i=5; i<8; i++){
			for(int j=0; j<N; j++){
				if((i%2 == 1 && j%2 == 0)){
					mas[i][j] = WHITE;
				}else if((i%2 == 0 && j%2 == 1)){
					mas[i][j] = WHITE;
				}
			}
		}
		
		mas[2][1] = 0;
		mas[4][1] = 2;
		
		return mas;
	}
	
	private static void init(){
		whites = 12;
		blacks = 12;
		JChecker [][] mas = new JChecker [N][N];
		for(int i=0; i<3; i++){
			for(int j=0; j<N; j++){
				if(i%2 == 0 && j%2 == 1){
					JChecker ch = new JChecker(j, i, BLACK);
					mas[i][j] = ch;
				}else if(i%2 == 1 && j%2 == 0){
					JChecker ch = new JChecker(j, i, BLACK);
					mas[i][j] = ch;
				}
			}
		}
		for(int i=3; i<5; i++){
			for(int j=0; j<N; j++){
				if(i%2 == 0 && j%2 == 1){
					JChecker ch = new JChecker(j, i, EMPTY);
					mas[i][j] = ch;
				}else if(i%2 == 1 && j%2 == 0){
					JChecker ch = new JChecker(j, i, EMPTY);
					mas[i][j] = ch;
				}
			}
		}
		for(int i=5; i<8; i++){
			for(int j=0; j<N; j++){
				if((i%2 == 1 && j%2 == 0)){
					JChecker ch = new JChecker(j, i, WHITE);
					mas[i][j] = ch;
				}else if((i%2 == 0 && j%2 == 1)){
					JChecker ch = new JChecker(j, i, WHITE);
					mas[i][j] = ch;
				}
			}
		}
		board = new Board(mas);
	}
}
