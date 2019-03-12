package player.students.guga;

import main.*;
import java.util.List;
import java.util.Vector;

public class GuggerTurnsGetter {
	private int me, you, meK, youK;
	private int[][] d;
	private Vector<Turn> turns = new Vector<>();
	
	public GuggerTurnsGetter(int[][] d, int value) {
		this.d = d;
		me = value;
		you = me==1 ? 2:1;
		meK = me + 3;
		youK = you + 3;
	}
	
	public Vector<Turn> getAllPossibleTurns() {
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				if(itsMe(d[i][j])){
					getAllTurnsFor(i,j);
				}
			}
		}
		if(fuka())
			filter();
		
		return turns;
	}

	private void filter() {
		for (int i = 0; i < turns.size();){
			if(turns.get(i).killed.size() == 0)
				turns.remove(i);
			else{
				i++;
			}
		}
	}

	private boolean fuka() {
		for (int i = 0; i < turns.size(); i++)
			if(turns.get(i).killed.size() != 0)
				return true;
		
		return false;
	}

	private void getAllTurnsFor(int i, int j) {
		Vector<Turn> oneStepTurns = getOneStepTurnsFrom(i,j);
		turns.addAll(oneStepTurns);
		
		Turn t;
		for (int k = 0; k < oneStepTurns.size(); k++){
			t = oneStepTurns.get(k);
			if(t.killed.size() != 0){
				perform(t);
				addAllKillerTurnsRecursively(t.last.col, t.last.row, t);
				undo(t);
			}
		}
    }

	private Vector<Turn> getOneStepTurnsFrom(int y, int x) {
		Vector<Turn> ans = new Vector<>();
		int i,j;

		// zeviT marjvniv (damka, kvla, tetri)
		i=-1; j=1;
		getOneStepTurnsFromYXInDirectionIJ(y, x, i, j, ans);
		
		// zeviT marcxniv (damka, kvla, tetri)
		i=-1; j=-1;
		getOneStepTurnsFromYXInDirectionIJ(y, x, i, j, ans);

		// qvevit marjvniv (damka, kvla, shavi)
		i=1; j=1;
		getOneStepTurnsFromYXInDirectionIJ(y, x, i, j, ans);
		
		// qvevit marcxniv (damka, kvla, shavi)
		i=1; j=-1;
		getOneStepTurnsFromYXInDirectionIJ(y, x, i, j, ans);
		
		return ans;
	}

	private void getOneStepTurnsFromYXInDirectionIJ(int y, int x, int i, int j, Vector<Turn> ans) {
		Checker first = new Checker(x, y, d[y][x]), last, killedChecker;
		Vector<Checker> killed = new Vector<>();
		Turn t;
		
		if(d[y][x] == me){
			if(isOnBoard(y+i, x+j) && d[y+i][x+j]==0 && itsFront(i)){
				//just move
				last = new Checker(x+j, y+i, d[y][x]);
				if(gotKing(last.col))
					last.value = meK;
				t = new Turn(first, last, killed);
				ans.add(t);
			}

			if(isOnBoard(y+2*i, x+2*j) && itsYou(d[y+i][x+j]) && isEmpty(y+2*i, x+2*j)){
				//kill
				last = new Checker(x+2*j, y+2*i, d[y][x]);
				if(gotKing(last.col))
					last.value = meK;
				killedChecker = new Checker(x+j, y+i, d[y+i][x+j]);
				killed.add(killedChecker);
				t = new Turn(first, last, killed);
				ans.add(t);
			}
		}else if(d[y][x] == meK){
			
			for (int k = 1; k<8 && isOnBoard(y+k*i,x+k*j) && !itsMe(d[y+k*i][x+k*j]); k++) {
				if(itsYou(d[y+k*i][x+k*j])){
					killedChecker = new Checker(x+k*j, y+k*i, d[y+k*i][x+k*j]);
					killed.add(killedChecker);
				}else{
					last = new Checker(x+k*j, y+k*i, d[y][x]);
					Vector<Checker> killedToAdd = new Vector<>();
					killedToAdd.addAll(killed);
					t = new Turn(first, last, killedToAdd);
					ans.add(t);
				}
			}
		}
	}

	private boolean gotKing(int y) {
		if(me==1 && y==0)
			return true;
		if(me==2 && y==7)
			return true;
		return false;
	}

	private boolean itsFront(int i) {
		if(me==1 && i==-1)
			return true;
		if(me==2 && i==1)
			return true;
		return false;
	}

	private boolean isEmpty(int i, int j) {
		return d[i][j] == 0;
	}

	private boolean isOnBoard(int i, int j) {
		return i>=0 && i<8 && j>=0 && j<8;
	}

	private void addAllKillerTurnsRecursively(int i, int j, Turn tPrev) {
		Vector<Turn> oneStepTurns = getOneStepTurnsFrom(i,j);
		Turn t;
		for (int k = 0; k < oneStepTurns.size(); k++){
			t = oneStepTurns.get(k);
			if(t.killed.size() != 0){
                turns.remove(tPrev);
                Turn sumT = sumOfTurns(tPrev, t);
				turns.add(sumT);
				perform(t);
				addAllKillerTurnsRecursively(t.last.col, t.last.row, sumT);
				undo(t);
			}
		}
	}

	private Turn sumOfTurns(Turn t0, Turn t1) {
		List<Checker> killedBoth = new Vector<>();
		killedBoth.addAll(t0.killed);
		killedBoth.addAll(t1.killed);
		return new Turn(t0.first, t1.last, killedBoth);
	}

	private void perform(Turn t) {
		d[t.last.col][t.last.row] = t.last.value;
		d[t.first.col][t.first.row] = 0;
		for (int i = 0; i < t.killed.size(); i++) {
			d[t.killed.get(i).col][t.killed.get(i).row] = 0;
		}
	}

	private void undo(Turn t) {
		d[t.first.col][t.first.row] = t.first.value;
		d[t.last.col][t.last.row] = 0;
		for (int i = 0; i < t.killed.size(); i++) {
			d[t.killed.get(i).col][t.killed.get(i).row] = t.killed.get(i).value;
		}
	}

	private boolean itsMe(int v) {
		return v == me || v == meK;
	}

	private boolean itsYou(int v) {
		return v == you || v == youK;
	}

}
