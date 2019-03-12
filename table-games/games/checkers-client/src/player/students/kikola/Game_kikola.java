package player.students.kikola;
import main.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Game_kikola implements Player {
    public void gameOver() {}
	static int s = 8; // Size
	static int empty = 0, white = 1, black = 2, whiteK = 4, blackK = 5, pWhite = 1, pBlack = 2;
	static int[][] d;
	static int maxScore = Integer.MIN_VALUE;
	static int pl;
	static int maxAlpha;
	static int depth = 10;
	static int dp;
	static boolean breds = false;

	public Turn makeTurne(int[][] d, int value) {
		pl = value;
		breds = false;
		maxAlpha = Integer.MIN_VALUE;
		int[][] res = new int[s][s];
		alphabeta(d, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, value, res);
//		printBoard(res, 5550, 5550);
		checkDamka(res);
		return fck(d,res, value);
	}
	
	private void checkDamka(int[][] res) {
		for (int i = 0; i < res.length; i++) {
			if(isEnemy(res[0][i], black))res[0][i]=whiteK;
			if(isEnemy(res[s-1][i], white))res[s-1][i]=blackK;
		}
		
	}

	private Turn fck(int[][] d1, int[][] d2, int player) {
		Checker init = null;
		Checker fin = null;
		List<Checker> list = new ArrayList<>();
		for (int i = 0; i < d2.length; i++) {
			for (int j = 0; j < d2.length; j++) {
				if(sameCol(d1[i][j], player) && isEmpty(d2, i, j)){
					init = new Checker(i, j, d1[i][j]);
				}else if(sameCol(d2[i][j], player) && isEmpty(d1, i, j)){
					fin = new Checker(i, j, d2[i][j]);
				}else if(isEnemy(player, d1[i][j]) && isEmpty(d2, i, j)){
					list.add(new Checker(i, j, d1[i][j]));
				}
			}
		}
		d = d2;
		return new Turn(init, fin, list);
	}

	private int alphabeta(int[][] d2, int depth, int alpha, int beta,int player, int[][] res) {
//		printBoard(d2,player,depth);
		dp = depth-1;
		if(depth==0){
			int a = heur(d2);
//			System.out.println(alpha+"\t\t"+beta+"\t\t"+(this.depth-1));
			return a;
		}
		if(depth==this.depth-1){
			save(d2,alpha,res);
		}
		ArrayList<int[][]> arr = getChildren(d2,player);
		if(sameCol(player, pl)){
			for (int i = 0; i < arr.size(); i++) {
				alpha = Math.max(alpha, alphabeta(arr.get(i), depth-1, alpha, beta, not(player), res));
				if (beta <= alpha) {
					break;
				}
			}
			return alpha;
		}else {
			for (int i = 0; i < arr.size(); i++) {
				beta = Math.min(beta, alphabeta(arr.get(i), depth-1, alpha, beta, not(player), res));
				if (beta <= alpha) {
					break;
				}
			}
			return beta;
		}
	}

	private void save(int[][] d2, int alpha, int[][] res) {
		if(alpha>=maxAlpha){
			maxAlpha = alpha;
			for (int k2 = 0; k2 < s; k2++) {
				System.arraycopy(d2[k2], 0, res[k2], 0, s);
			}
		}
	}

	private ArrayList<int[][]> getChildren(int[][] d2, int value) {
		ArrayList<int[][]> arr = new ArrayList<>();
		for (int i = 0; i < s; i++) {
			for (int j = 0; j < s; j++) {
				if(sameCol(d2[i][j],value)){
//					System.out.println("PARENT "+i+" "+j);
//					for (int k = 0; k < s; k++) {
//						System.out.println(Arrays.toString(d2[k]));
//					}System.out.println();
					
					getMoves2(d2,i,j,arr);
				}
			}
		}
		return arr;
	}

	private int heur(int[][] d2) {
		int counter = 0;
		for (int i = 0; i < d2.length; i++) {
			for (int j = 0; j < d2.length; j++) {
				if (sameCol(d2[i][j], pl)) counter++;
				else if (isEnemy(d2[i][j], pl)) counter--;
			}
		}
		return counter;
	}

	private void getMoves2(int[][] d, int i, int j, ArrayList<int[][]> arr) {
//		int direction1 = ((d[i][j]==white) ? -1:1);
		int player = d[i][j];
//		int[][] d1 = null;
		if(player==whiteK || player==blackK){
			damka(d,i,j,player,arr);
		}else {
			qva(d,i,j,player,arr);
//			for (int direction=-1; direction < 2; direction+=2) {
//				for (int k = -1; k < 2; k+=2) {
//					if (inbound(d, i+direction, j+k, player)) {
//						if(d[i+direction][j+k]==empty && direction!=direction1)continue;
//						if(d[i+direction][j+k]==empty || inbound(d, i+direction*2, j+k*2)){
//							d1 = new int[s][s];
//							for (int k2 = 0; k2 < s; k2++) {
//								System.arraycopy(d[k2], 0, d1[k2], 0, s);
//							}
//							d1[i][j]=0;
//						}else continue;
//						
//						if (!isEmpty(d1, i+direction, j+k)) {
//							if(isEmpty(d, i+direction*2, j+k*2)){
//								d1[i+direction][j+k] = 0;
//								d1[i+direction*2][j+k*2] = player;
//								bred(d1,i+direction*2,j+k*2,player);
//								arr.add(d1);
//								return;
//							}
//						}else {
//							d1[i+direction][j+k] = player;
//							arr.add(d1);
//						}
////						printBoard(d1, 111, 111);
//					}
//				}
//			}
		}
	}

	private void qva(int[][] d2, int i, int j, int player, ArrayList<int[][]> arr) {
		if(mosaklavi2(d2,i,j,player)){
			if(dp == depth-1 && player == pl){
				if(!breds)arr.clear();
				breds = true;
			}
//			System.out.println(i+" asd "+j);
			kill(d2,i,j,player,arr);
		}else {
//			System.out.println(i+" qeqwee "+j);
			if(!breds)gadasvla(d2,i,j,player,arr);
		}
		
	}

	private void gadasvla(int[][] d2, int i, int j, int player,	ArrayList<int[][]> arr) {
		int l = (sameCol(player, white) ? -1:1);
		for (int k = -1; k < 2; k+=2) {
			if(inBound(d2, i+l, j+k) && isEmpty(d2, i+l,j+k)){
					int[][] d1 = new int[s][s];
					for (int k2 = 0; k2 < s; k2++) {
						System.arraycopy(d2[k2], 0, d1[k2], 0, s);
					}
					
					d1[i][j] = 0;
					d1[i+l][j+k] = player;
					arr.add(d1);
			}
		}
	}

	private void kill(int[][] d2, int i, int j, int player, ArrayList<int[][]> arr) {
		int[][] d1 = new int[s][s];
		for (int k2 = 0; k2 < s; k2++) {
			System.arraycopy(d2[k2], 0, d1[k2], 0, s);
		}
		for(int l = -1;l<2;l+=2){
			for (int k = -1; k < 2; k+=2) {
				if(inBound(d1, i+l, j+k) && isEnemy(d1[i+l][j+k], player)){
					if(inBound(d1, i+l*2, j+k*2) && isEmpty(d1, i+2*l, j+k*2)){
						d1[i][j] = 0;
						d1[i+l][j+k] = 0;
						d1[i+2*l][j+2*k] = player;
						bred(d1,i+l*2,j+k*2,player);
						arr.add(d1);
						return;
					}
				}
			}
		}
	}

	private boolean mosaklavi2(int[][] d2, int i, int j, int player) {
		for(int l = -1;l<2;l+=2){
			for (int k = -1; k < 2; k+=2) {
				if(inBound(d2, i+l, j+k) && isEnemy(d2[i+l][j+k], player)){
					if(inBound(d2, i+l*2, j+k*2) && isEmpty(d2, i+2*l, j+k*2)){
						return true;
					}
				}
			}
		}
		return false;
	}

	private void damka(int[][] d2, int i, int j, int player, ArrayList<int[][]> arr) {
		if(mosaklavi(d2,i,j,player)){
			if(dp == depth-1 && player == pl){
				if(!breds)arr.clear();
				breds = true;
			}
			int[][] d1 = new int[s][s];
			for (int k2 = 0; k2 < s; k2++) {
				System.arraycopy(d[k2], 0, d1[k2], 0, s);
			}
			maxScore = Integer.MIN_VALUE;
			int[][] damkaCopy = new int[s][s];
			madafaka(d1,i,j,player,0, damkaCopy);
			if(damkaCopy!=null){
//				printBoard(damkaCopy, 222, 222);
				arr.add(damkaCopy);
			}
		}else {
			if(!breds){
				movee1(d2,i,j,player,arr);
				movee2(d2,i,j,player,arr);
				movee3(d2,i,j,player,arr);
				movee4(d2,i,j,player,arr);
			}
		}
	}
	
	

	private void madafaka(int[][] d1, int i, int j, int player, int score, int[][] damkaCopy) {
		boolean a = true;
		for(int l = -1;l<2;l+=2){
			for (int k = -1; k < 2; k+=2) {
				for (int dir = l, counter = 1; dsa(l,dir); dir = asd(l,dir), counter++) {
					if(inBound(d1, i+dir, j+k*counter) && isEnemy(d1[i+dir][j+k*counter], player)){
						int i1 = i+asd(l, dir);
						int j1 = j+k*(counter+1);
						if(inBound(d1, i1, j1) && isEmpty(d1, i1, j1)){
							a = false;
							int prev = d1[i][j];
							int prev2 = d1[i+dir][j+k*counter];
							int prev3 = d1[i1][j1];
							d1[i][j] = 0;
							d1[i+dir][j+k*counter] = 0;
							d1[i1][j1] = player;
							madafaka(d1, i1, j1, player,score+1,damkaCopy);
							d1[i][j] = prev;
							d1[i+dir][j+k*counter] = prev2;
							d1[i1][j1] = prev3;
						}
					}else break;
				}
			}
		}
		if(a){
			if(score>maxScore){
				maxScore = score;
				for (int k2 = 0; k2 < s; k2++) {
					System.arraycopy(d1[k2], 0, damkaCopy[k2], 0, s);
				}
			}
		}
	}

	private boolean mosaklavi(int[][] d2, int i, int j, int player) {
		for(int l = -1;l<2;l+=2){
			for (int k = -1; k < 2; k+=2) {
				for (int dir = l, counter = 1; dsa(l,dir); dir = asd(l,dir), counter++) {
					if(inBound(d2, i+dir, j+k*counter) && isEnemy(d2[i+dir][j+k*counter], player)){
						if(inBound(d2, i+asd(l, dir), j+k*(counter+1)) && isEmpty(d2, i+asd(l, dir), j+k*(counter+1))){
							return true;
						}
					}else break;
				}
			}
		}
		return false;
	}
	
	private void movee1(int[][] d2, int i, int j, int player, ArrayList<int[][]> arr){
		int[][] d1 = new int[s][s];
		for (int k2 = 0; k2 < s; k2++) {
			System.arraycopy(d2[k2], 0, d1[k2], 0, s);
		}
		for (int k = 1; k < 8; k++) {
			if(inBound(d1, i-1, j-1) && isEmpty(d1,i-1,j-1)){
				d1[i][j]=0;
				d1[i-1][j-1]=player;
				arr.add(d1);
			}else break;
			i = i-1;
			j = j-1;
		}
	}
	
	private void movee2(int[][] d2, int i, int j, int player, ArrayList<int[][]> arr){
		int[][] d1 = new int[s][s];
		for (int k2 = 0; k2 < s; k2++) {
			System.arraycopy(d2[k2], 0, d1[k2], 0, s);
		}
		for (int k = 1; k < 8; k++) {
			if(inBound(d1, i-1, j+1) && isEmpty(d1,i-1,j+1)){
				d1[i][j]=0;
				d1[i-1][j+1]=player;
				arr.add(d1);
			}else break;
			i = i-1;
			j = j+1;
		}
	}
	
	private void movee3(int[][] d2, int i, int j, int player, ArrayList<int[][]> arr){
		int[][] d1 = new int[s][s];
		for (int k2 = 0; k2 < s; k2++) {
			System.arraycopy(d2[k2], 0, d1[k2], 0, s);
		}
		for (int k = 1; k < 8; k++) {
			if(inBound(d1, i+1, j+1) && isEmpty(d1,i+1,j+1)){
				d1[i][j]=0;
				d1[i+1][j+1]=player;
				arr.add(d1);
			}else break;
			i = i+1;
			j = j+1;
		}
	}
	
	private void movee4(int[][] d2, int i, int j, int player, ArrayList<int[][]> arr){
		int[][] d1 = new int[s][s];
		for (int k2 = 0; k2 < s; k2++) {
			System.arraycopy(d2[k2], 0, d1[k2], 0, s);
		}
		for (int k = 1; k < 8; k++) {
			if(inBound(d1, i+1, j-1) && isEmpty(d1,i+1,j-1)){
				d1[i][j]=0;
				d1[i+1][j-1]=player;
				arr.add(d1);
			}else break;
			i = i+1;
			j = j-1;
		}
	}

	private boolean dsa(int l, int dir) {
		return (l==-1) ? dir>-8 : dir<8;
	}

	private int asd(int l, int dir) {
		return (l == -1) ? --dir:++dir;
	}

	private int bred(int[][] d1, int i, int j, int player) {
//		printBoard(d1,player,9999);
		for (int direction=-1; direction < 2; direction+=2) {
			for (int j2 = -1; j2 < 2; j2+=2) {
				if(inbound(d1, i+direction, j+j2, player)){ // tu yavs mteri mezoblad
					if(d1[i+direction][j+j2]!=empty){
						if(inbound(d1, i+direction*2, j+j2*2)){ // tu mezoblis shemdegi carielia
							d1[i][j] = 0;
							d1[i+direction][j+j2] = 0;
							d1[i+direction*2][j+j2*2] = player;
							return bred(d1,i+direction*2,j+j2*2,player);
						}
					}
				}
			}
		}
		return 0;
	}
	
	private int not(int player) {
		return (sameCol(player, white)) ? black : white;
	}
	
	public int[][] init() {
		d = new int[s][s];
		int col;
		for (int i = 0; i < s; i++) {
			for (int j=0; j < s; j++) {
				col = empty;
				if(((i==0 || i==2) && !even(j)) || (i==1 && even(j))){
					col=black;
				}else if(((i==5 || i==7) && even(j)) || (i==6 && !even(j))){
					col = white;
				}
				d[i][j] = col;
			}
		}
		return d;
	}
	
	public void play(int[][] d, PlayerValue val){
		int player;
		switch (val) {
		case WHITE:
			player = 1;
			break;
		case BLACK:
			player = 2;
			break;
		default:
			player = 0;
		}
		makeTurne(this.d, player);
	}
	
	public void printBoard(){
		String str = "";
		for (int i = 0; i < s; i++) {
			str += Arrays.toString(d[i])+"\n";
		}
//		System.out.println(str);
	}
	
	public void printBoard(int[][] d,int pl,int dep){
		String str = "";
		for (int i = 0; i < s; i++) {
			str += Arrays.toString(d[i])+"\n";
		}
//		System.out.println(pl+" "+dep+"\n"+str);
	}
	
	private boolean even(int a){
		return (a&1<<0)==0;
	}
	
	private boolean inbound(int[][] d, int i, int j){
		if(i<0 || i>=s || j<0 || j>=s)return false;
		if(d[i][j]!=empty)return false;
		return true;
	}
	
	private boolean inbound(int[][] d, int i, int j, int player){
		if(i<0 || i>=s || j<0 || j>=s) return false;
		if(sameCol(d[i][j],player)) return false;
		return true;
	}
	
	private boolean inBound(int[][] d, int i, int j){
		if(i<0 || i>=s || j<0 || j>=s) return false;
		return true;
	}
	
	private boolean sameCol(int player1, int player2){
		if((player1 == white || player1 == whiteK) && (player2 == white || player2 == whiteK))return true;
		else if((player1 == black || player1 == blackK) && (player2 == black || player2 == blackK))return true;
		else return false;
	}
	
	private boolean isEnemy(int player1, int player2){
		if((player1 == white || player1 == whiteK) && (player2 == black || player2 == blackK))return true;
		else if((player1 == black || player1 == blackK) && (player2 == white || player2 == whiteK))return true;
		else return false;
	}
	
	private boolean isEmpty(int[][] d, int i, int j){
		return d[i][j] == empty;
	}
}
