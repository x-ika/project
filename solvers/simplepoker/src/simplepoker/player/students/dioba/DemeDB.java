package simplepoker.player.students.dioba;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Database class for simple poker 
 * */
public class DemeDB {
	
	DecimalFormat format;
	 
	/**
	 * This ArrayList stores my Cards throughout the game
	 * */
	private ArrayList<Double> myCards;
	
	/**
	 * This ArrayList stores players mapped to their cards and bets they made with that cards
	 * Map<Player, he's bets for specific cards>
	 * */
	private Map<Integer, ArrayList<CardToBetMap> > cardMap;
	
	/**
	 * Constructor for initializing Arraylists and decimal format
	 * */
	public DemeDB(){
		format = new DecimalFormat("#.###");
		myCards = new ArrayList<>();
		cardMap = new HashMap<>();
	}
	
	/**
	 * add info for my cards
	 * */
	public void addMyCard(double card){
		myCards.add(Double.parseDouble(format.format(card)));
	}
	
	/**
	 * Get myCards info
	 * */
	public ArrayList<Double> getMyCards(){
		return myCards;
	}
	
	/**
	 * Adding new player to card to bet mapping
	 * */
	public void setCardToBetMap(int player, double card, int bet){
		if (cardMap.containsKey(player)){
			ArrayList<CardToBetMap> temp = cardMap.get(player);
			temp.add(new CardToBetMap(player, Double.parseDouble(format.format(card)), bet));
			cardMap.put(player, temp);
		}
		else{
			ArrayList<CardToBetMap> temp = new ArrayList<>();
			temp.add(new CardToBetMap(player, Double.parseDouble(format.format(card)), bet));
			cardMap.put(player, temp);
		}
	}
	
	/**
	 * get bet-for-card information for specific player
	 * */
	public ArrayList<CardToBetMap> getCardToBetMap(int player){
		return cardMap.get(player);
	}
	
	/**
	 * get average card for bet amount for player
	 * */
	public double getOnePlayerCard(int player, int bet){
		ArrayList<CardToBetMap> ctbm = getCardToBetMap(player);
		CardToBetMap tempMap;
		double ret = 0;
		int count = 1;
		if (ctbm == null) return -1;
		for (int i = 0; i < ctbm.size(); i ++){
			tempMap = ctbm.get(i);
			if (tempMap.getBet() == bet){
				ret += tempMap.getCard();
				count++;
			}
		}
		return Double.parseDouble(format.format(ret/count));
	}
	
	/**
	 * get average card for all players
	 * */
	public double[] getAllPlayerCards(int[] bets){
		double[] ret = new double[bets.length];
		for (int i = 0; i < bets.length; i ++){
			ret[i] = getOnePlayerCard(i, bets[i]);
		}
		return ret;
	}
	
}
