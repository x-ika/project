package tetris.students.eormo;

import tetris.logic.BitRectangle;
import tetris.logic.Command;
import tetris.logic.Pile;
import tetris.logic.Player;
import tetris.logic.TetrisUtils;
import tetris.logic.Turn;
import static tetris.logic.TetrisUtils.xor;

public class EvgoSolution implements Player{

	// this method find best position if necessary update this
	// takes parameters and calculate which is best for current position
	private void findOutBest(int[] tetris, Pile p, int w,int rot, int col, int r) {
		int[] h = new int[w]; 
		int max = 0; //find max height and save meanings and also fill heights
		for (int j = 0; j < w; j++) {
			boolean b = false;
			for (int i = tetris.length - 1; i >= 0; i--) {
				if ((tetris[i] & (1 << j)) != 0) {
					if (max < i) {
						max = i+1;
					}
					h[j] = i+1;
					b = true;
					break;
				}
			}
			if(!b){
				h[j] = 0;
			}			
		}
		
		int holes = 0; //this calculate how many spot is free 
		for (int i = 0; i < w; i++) {
			if(h[i] == 0)continue;
			for (int j = h[i]-1; j>=0; j--) {
				if ((tetris[j] & (1 << i)) == 0) {
					holes++;
				}
			}
		}
		int colTransitions = 0; // this calculate how many free spot are in the columns
		for (int i = 0; i < w; i++) { // if colTransitions is big number free spot are more,
			int count = 1; // and if low number accordingly free spot less
			int trans = (tetris[0]&(1<<i));
			if(h[i]==0)continue;
			for (int j = h[i]-1; j>= 0; j--) {
				if((tetris[j] & (1 << i)) == 0){
					if(trans != 0){
						trans = 0;
						count ++;
					}
				}else{
					if(trans == 0){
						trans = 1;
						count++;
					}
				}
			}
			colTransitions += count;
		}
		
		int rowTransitions = 0; // this calculate how many free spot are in the row
		for(int i = 0; i < max; i++){ // like above but in the rowe
			int count = 1;
			int trans = (tetris[i]&1);
			for(int j = 0; j < w; j++){
				if((tetris[i]&(1<<j))==0){
					if(trans != 0){
						trans = 0;
						count ++;
					}
				}else{
					if(trans == 0){
						trans = 1;
						count++;
					}
				}
			}
			rowTransitions += count;
		}
		int wells = 0;
		if(h[0] < h[1]){ // there calculate how much free space between columns
			int factor = h[1] - h[0];
			wells += ((1+factor)*factor)/2;
		}
		if(h[9] < h[8]){
			int factor = h[8] - h[9];
			wells += ((1+factor)*factor)/2;
		}
		for (int i = 0; i < w - 2; i++) {
			int delta1 = h[i] - h[i + 1];
			int delta2 = h[i+2] - h[i + 1];
			if(delta1>0 && delta2>0){
				int factor = 0;
				if(delta1 > delta2){
					factor = delta2;
				}else{
					factor = delta1;
				}
				wells += ((1+factor)*factor)/2;
			}
		}
		// this coefficients find in the Internet (ONlY THIS COOEFICIENTS ) 
		// each parameters has coefficient calculate by neural weighted vector
		double s = (-3.4181268)*cleared + (4.5001588)*(r-cleared+((double)p.getHeight())/2) + (7.8992654)*holes + (9.3486953)*colTransitions + (3.2178882)*rowTransitions + (3.3855972)*wells;
		if (s < this.s) {
			this.s = s;
			this.rot = rot;
			this.c = col;
			this.r = r;
		}
	}

	private int cleared = 0;
	//this return coordinate on which  figure sit down 
	//return only height coordinate
	private int fillBoard(int[] grid, int width, int col, Pile pile) {
		cleared = 0;
		int tmp = 0;
		for (int r = pile.row; r >= 0; r--) {
			if (TetrisUtils.checkContact(grid,pile.getBitsetPrepresentation(), r, col)) {
				xor(grid, pile.getBitsetPrepresentation(), r, col);
				tmp = r;
				break;
			}
		}
		for (int i = 0; i < grid.length; i++) {
			if (Integer.bitCount(grid[i]) == width) {
				for(int j = i; j< grid.length; j++){
					grid[i] = grid[i+1];
				}
				cleared++;
			}
		}
		return tmp;
	}
	
	//this calculate which moves must do 
	//for example how many moves must do for moving right, make rotations or  sit down on the board 
	private Turn turn(BitRectangle board) {
		Turn turn = new Turn();
		for (int i = 0; i < rot; i++)
			turn.add(Command.ROTATION);
		for (int i = 0; i < c; i++)
			turn.add(Command.RIGHT);
		for (int i = board.getHeight() - 5; i > r; i--)
			turn.add(Command.DOWN);
		return turn;
	}

	private	double s = Double.MAX_VALUE; // this must max because best must be less than other values 
	private	int rot = 0;
	private	int r = 0;
	private	int c = 0;
		
	public Turn play(BitRectangle board, Pile pile) {
		if (pile == null || pile.filledArea() < 4)return new Turn(); // as IkaSolution has if pile is null or pile area is less than 4 then start new turn
		int[] myBoard = board.getBitsetPrepresentation();
		this.s = Double.MAX_VALUE;
		this.rot = 0;
		this.r = 0;
		this.c = 0;
		for (int i = 0; i < 4; i++) { 
			for (int j = 0; j < board.getWidth() - pile.getWidth() + 1; j++) {
				int[] tetris = myBoard.clone();
				findOutBest(tetris,pile,board.getWidth(),i,j,fillBoard(tetris, board.getWidth(), j, pile)); //find best position on each iteration
			}
			pile = pile.rotate(); 
		}
		return turn(board);
	}


	
	
	/**
	 * @param args
	*/


}
