package simplepoker.player.students.dioba;

/**
 * This Class maps a player to it's card and bet he made with this card;
 * */
public class CardToBetMap{
	private double card;
	private int bet;
	private int player;
	
	public CardToBetMap(int player, double card, int bet){
		this.player = player;
		this.card = card;
		this.bet = bet;
	}
	
	public void setPlayer(int player){
		this.player = player;
	}
	public int getPlayer(){
		return this.player;
	}
	
	public void setCard(double card){
		this.card = card;
	}
	public double getCard(){
		return this.card;
	}
	
	public void setBet(int bet){
		this.bet = bet;
	}
	public int getBet(){
		return this.bet;
	}
}