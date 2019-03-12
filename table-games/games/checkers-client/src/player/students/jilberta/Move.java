package player.students.jilberta;

import main.*;
import java.util.ArrayList;
import java.util.List;

public class Move {
	public JChecker from;
	public JChecker to;
	public boolean jump;
	public List<JChecker> killedChecks = new ArrayList<>();
	
	public Move(JChecker from, JChecker to, boolean jump){
		this.from = from;
		this.to = to;
		this.jump = jump;
	}
	
	public void saveKilledChecks(JChecker ch){
		this.killedChecks.add(ch);
	}
	
	public void clearKilledChecks(){
		this.killedChecks.clear();
	}
	
	public JChecker getFrom(){
		return this.from;
	}
	
	public JChecker getTo(){
		return this.to;
	}
	
	public List<JChecker> getKilledChecks (){
		return this.killedChecks;
	}
	
	public boolean isJump(){
		return jump;
	}
	
	public Move copy (){
		JChecker f = this.from.copy();
		JChecker t = this.to.copy();
		boolean j = this.jump;
		Move newMove = new Move(f, t, j);
		return newMove;
	}
	
	public String toString(){
		String st = "";
		st = "From: " + this.from.row + " "+this.from.col +" "+this.from.getColor()
				+" To: "+this.to.row +" "+this.to.col +" "+this.to.getColor()+
				" Jump: "+this.jump;
		return st;
	}
	
}
