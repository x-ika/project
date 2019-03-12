package tetris.students.lasha;

import tetris.logic.BitRectangle;
import tetris.logic.Command;
import tetris.logic.Pile;
import tetris.logic.Player;
import tetris.logic.TetrisUtils;
import tetris.logic.Turn;

public class LashasPlayer implements Player{
//	private BitRectangle board;
//	private Pile pile;
	public Turn play(BitRectangle board, Pile pile) {
//		this.board = board;
//		this.pile = pile;
		int[] myb = board.getBitsetPrepresentation();
		int[] myp = pile.getBitsetPrepresentation(),fi=null;
		int mini = Integer.MIN_VALUE;
		int r=0,c=0,myh=pile.row,h=0;
		for(int i=0;i<4;i++){
			pile = pile.rotate();
			int curh = myh;
			myp = pile.getBitsetPrepresentation();
			for(int k=0;k+TetrisUtils.rightmost(myp)<board.getWidth();k++){
				for(int j=myh;j>=0;j--){
					myb = board.getBitsetPrepresentation();
					if(TetrisUtils.intersecs(myb, myp, j, k)){
						break;
					}else{
						curh = j;
					}
				}
				myb = board.getBitsetPrepresentation();
				if(TetrisUtils.intersecs(myb, myp,curh, k)){
					continue;
				}
				TetrisUtils.xor(myb, myp, curh, k);
				int cur = rateBoard(myb, board.getWidth());
				if(cur>mini){
					mini = cur;
					r=i;
					c=k;
					h=curh;
					fi = myb.clone();
				}
			}
		}
		Turn t = new Turn();
		while((r-- + 1)%4 > 0){
			t.commands.add(Command.ROTATION);
		}
		while(c-- > 0){
			t.commands.add(Command.RIGHT);
		}
		while(pile.row - h++ > 0){
			t.commands.add(Command.DOWN);
		}
		//System.out.println(Arrays.toString(fi));
		
		return t;
	}
	static int rateBoard(int myb[],int width){
		int ans = 0;
		int top = TetrisUtils.top(myb);
		for(int i=0;i<=top;i++){
			if(myb[i]==(1<<width)-1){//vamowmeb shevsebulia tu ara piveli w biti
				System.arraycopy(myb, i+1, myb, i, myb.length-i-1);//chamovitanot yvela xazi
				i--;
				ans+=1000;
			}
		}
		top = TetrisUtils.top(myb);
		for(int i=0;i<top;i++){
			for(int j=i+1;j<=top;j++){
				int cur = myb[i]^myb[j];//vitvli ramdeni gansxvavebuli bitia
				ans-=150*Integer.bitCount(cur&myb[j]);//vitvli zeda xazi sad iyo shevsebuli
			}
		}
		top = TetrisUtils.top(myb);
		for(int i=0;i<top;i++){
			for(int j=0;j<width;j++){
				if((myb[i]&(1<<j))==0){
					ans-=(top+1-i);//vitvli ramdni bitia shvesebuli tito xazze
				} //rac ufro meti sicarielea qvevit mit uaresi
			}
		}
		return ans-top*50;
	}
	 public static void main(String[] args) {
	        BitRectangle board = new BitRectangle(5, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
	        Pile pile = new Pile(7, new int[]{1, 1, 1, 3});
	        Turn turn = new LashasPlayer().play(board, pile);
//	        System.out.println(turn.commands);
	    }
}
