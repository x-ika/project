package simplepoker.player.students.llaki;

import java.util.*;

import simplepoker.controller.Player;

public class LlakiPlayer implements Player {
	
	protected double myCardValue;
	
	private int myIndex;
	
	private static final int NUMBER_OF_INTERVALS = 21;
	
	private static final double NOT_COUNTED_YET = -1;
	
	private int[] cardCounts = new int[NUMBER_OF_INTERVALS], sumCardBets = new int[NUMBER_OF_INTERVALS];
	
	private double[] averageCards = new double[NUMBER_OF_INTERVALS];
	
	private int[] currentBank;
	
	private int learntIntervals = 0;
	
	public String ragaca(){
		return Arrays.toString(averageCards);
	}
	
	private int getIntervalForCard(double card){
		int interval = (int) (100*card);
		if(Math.abs(interval-100*card) < Math.abs(interval+1-100*card)) return interval/5;
		return (interval+1)/5;
	}
	

	public void startHand(double card) {
		myCardValue = card;
	}

	private int numberTurns = 0;
	
	private boolean moreThanTwoPlayers(int[] bank, int[] money){
		int num = 0;
		for(int i=0; i<bank.length; i++) if(bank[i]>0) num++;
		return num>2;
	}
	

	public int makeBet(int me, int diller, int round, int[] money, int[] bank,
			Collection<Integer> inGame) {
		if(moreThanTwoPlayers(bank, money)){
			int max = 0;
			for(int i=0; i<money.length; i++) max = Math.max(max, bank[i]);
			if(myCardValue>0.99){
				if(round<3) return money[me];
				else return max-bank[me];
			}
			return 0;
		}
		numberTurns++;
		currentBank = bank.clone();
		myIndex = me;
		if(numberTurns<50){
			if(myCardValue<0.97) return 0;
			int max = 0;
			for(int i=0; i<bank.length; i++) max = Math.max(max, bank[i]);
			if(round<3) return money[me];
			return max-bank[me]<=money[me]? max-bank[me]: money[me];
		}
		int opponentIndex = 0;
		for(int i=0; i<money.length; i++){
			if(i!=me && (money[i]>0 || bank[i]>0))
				opponentIndex = i;
		}
		int enemyBetAlready = bank[opponentIndex];
		double expCard = getExpectedCard(enemyBetAlready);
		if(expCard>myCardValue) return 0;
		int maxBet = 0;
		for(int i=0; i<bank.length; i++) maxBet = Math.max(maxBet, bank[i]);
		
		int minimalToBet = maxBet-bank[me], maxToBet = money[me];
		
		if(minimalToBet > maxToBet) return 0;
		if(round==0){
			return minimalToBet;
		}
		
		double diff = (myCardValue - expCard);
		double mid = (minimalToBet+maxToBet)*0.5;
		
		double toBet = (minimalToBet + diff*(mid-minimalToBet)*COEF[round]);
		toBet = Math.min(toBet, maxToBet);
		if(round<3)
			return (int) toBet;
		return minimalToBet;
	}
	
	private static final double[] COEF = new double[]{0.02, 0.08, 1, 1};
	
	private double getExpectedCard(int enemyBet){
		ArrayList<Double> possible = new ArrayList<>();
		for(int i=0; i<NUMBER_OF_INTERVALS; i++){
			if(averageCards[i]==NOT_COUNTED_YET) continue;
			double historyAverage = (.0+sumCardBets[i])/cardCounts[i];
			if(historyAverage >= enemyBet)
				return cardInInterval(i);
		}
		if(possible.size()==0) return 0.5;
		double sum = 0;
		for(int i=0; i<possible.size(); i++) sum += possible.get(i);
		return sum / possible.size();
	}
	
	private double cardInInterval(int interval){
		double num = 5*interval+2.5;
		return 0.01 * num;
	}


	public void finishHand(double[] allOpenedCards) {
		double opponentsCard = 0.0;
		int indexOfPlayer = 0;
		for(int i=0; i<allOpenedCards.length; i++){
			if(i==myIndex || allOpenedCards[i]==0) continue;
			opponentsCard = allOpenedCards[i];
			indexOfPlayer = i;
		}
		if(opponentsCard<0.0001) return;
		int index = getIntervalForCard(opponentsCard);
		cardCounts[index]++;
		sumCardBets[index] += currentBank[indexOfPlayer];
		averageCards[index] = (.0+sumCardBets[index]) / cardCounts[index];
		if(cardCounts[index]==1) learntIntervals++;
	}
	
	public LlakiPlayer(){
		Arrays.fill(averageCards, NOT_COUNTED_YET);
	}
	
}
