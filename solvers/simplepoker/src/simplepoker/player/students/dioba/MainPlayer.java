package simplepoker.player.students.dioba;

import java.text.DecimalFormat;
import java.util.Collection;

import simplepoker.controller.Player;

/**
 * MainPlayer Version 1.1
 * By: Demetre Iobashvili
 * Simple poker player. Using information from previous games,
 * it's building database consisting of a map linking Card to Bet. 
 * Player asumes that the bet coresponds to the card of opponents.
 * */

public class MainPlayer implements Player{
	
	private double myCard;
	private DemeDB database;
	private DecimalFormat format;
	
	private int me;
	private int[] curMoney;
	private int[] curBank;
	private double[] probCards;
	
	int maxBet;
	int avgBet;
	int myCurMoney;
	int myCurBank;
	int maxBetter;	
	
	public MainPlayer(){
		database = new DemeDB();
		format = new DecimalFormat("#.###");
	}
	

	public void startHand(double card) {
		myCard = card;
		database.addMyCard(myCard);
	}


	public int makeBet(int me, int diller, int round, int[] money, int[] bank,
			Collection<Integer> inGame) {
		this.me = me;
		curMoney = money;
		curBank = bank;
		maxBet = getMaxBet(bank);
		avgBet = getAverageBet(bank);
		maxBetter = getMaxBetter(maxBet, bank);	
		myCurMoney = money[me];
		myCurBank = bank[me];
		probCards = database.getAllPlayerCards(bank);
		
		if (probCards.length > 0 && probCards.length >= maxBetter){
			if (probCards[maxBetter] != -1){
				if (myCard >= probCards[maxBetter]){
					if (myCurMoney > maxBet - myCurBank){
						if (round < 3){
							return (maxBet - myCurBank) + 2;
						}
						else{
							return (maxBet - myCurBank);
						}
					}
					else if(myCurMoney == maxBet - myCurBank){
						return (maxBet - myCurBank);
					}
				}
			}
		}
		return 0;
	}
	
	private void addInfoInDB(int me, int[] money, int[] bank, double[] allOpenedCards){
		for (int i = 0; i < money.length; i ++){
			if (i != me && bank[i] > 1){
				database.setCardToBetMap(i, Double.parseDouble(format.format(allOpenedCards[i])), bank[i]);
			}
		}
		
	}
	
	private int getMaxBetter(int maxBet, int[] bank){
		for (int i = 0; i < bank.length; i ++){
			if (bank[i] == maxBet){
				return i;
			}
		}
		return -1;
	}
	
	private int getMaxBet(int[] bank){
		int max = 1;
		for (int i = 0; i < bank.length; i ++){
			if (bank[i] > max){
				max = bank[i];
			}
		}
		return max;
	}
	
	private int getAverageBet(int[] bank){
		int sum = 0;
		for (int i = 0; i < bank.length; i++){
			sum += bank[i];
		}
		return sum/bank.length;
	}


	public void finishHand(double[] allOpenedCards) {		
		addInfoInDB(me, curMoney, curBank, allOpenedCards);
		//database.printPlayerBetCards(me, allOpenedCards.length);
	}
	

	public String playerName(){
		return "Demetre";
	}
	
}
