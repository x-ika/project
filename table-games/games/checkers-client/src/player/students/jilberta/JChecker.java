package player.students.jilberta;

import main.Checker;

public class JChecker extends Checker{
    public JChecker(int row, int col, int value) {
        super(row, col, value);
    }

    public boolean king = false;

    public boolean isKing(){
    	return king;
    }

    public void makeKing(){
    	if(this.value == 1){
    		this.value = 4;
    	}else if(this.value == 2){
    		this.value = 5;
    	}
    	king = true;
    }

    public boolean isKingRow(){
    	if(this.value == 1 && this.col == 0)return true;
    	if(this.value == 2 && this.col == 7)return true;
    	return false;
    }

    public int getColor(){
    	return value;
    }

    public JChecker copy(){
    	int x1 = this.row;
    	int y1 = this.col;
    	int col = this.value;
    	JChecker newCh = new JChecker(x1, y1, col);
    	return newCh;
    }

    public boolean equals(JChecker to){
    	if(this.row == to.row){
    		if(this.col == to.col){
    			return true;
    		}
    	}
    	return false;
    }

    public JChecker downLeftMove(){
    	int x = this.row - 1;
    	int y = this.col + 1;
    	JChecker next = new JChecker(x, y, this.value);
    	return next;
    }

    public JChecker downRightMove(){
    	int x = this.row + 1;
    	int y = this.col + 1;
    	JChecker next = new JChecker(x, y, this.value);
    	return next;
    }

    public JChecker upLeftMove(){
    	int x = this.row - 1;
    	int y = this.col - 1;
    	JChecker next = new JChecker(x, y, this.value);
    	return next;
    }

    public JChecker upRightMove(){
    	int x = this.row + 1;
    	int y = this.col - 1;
    	JChecker next = new JChecker(x, y, this.value);
    	return next;
    }

    public JChecker upLeftJump(){
    	int x = this.row - 2;
    	int y = this.col - 2;
    	JChecker next = new JChecker (x, y, this.value);
    	return next;
    }

    public JChecker upRightJump(){
    	int x = this.row + 2;
    	int y = this.col - 2;
    	JChecker next = new JChecker (x, y, this.value);
    	return next;
    }

    public JChecker downLeftJump(){
    	int x = this.row - 2;
    	int y = this.col + 2;
    	JChecker next = new JChecker (x, y, this.value);
    	return next;
    }

    public JChecker downRightJump(){
    	int x = this.row + 2;
    	int y = this.col + 2;
    	JChecker next = new JChecker (x, y, this.value);
    	return next;
    }

    public String toString(){
    	String st = "";
    	st += this.row +" "+this.col +" "+this.value;
    	return st;
    }
}
