package simplepoker.player.students.alpenidze;

import java.util.HashMap;

public class Helper {
	public double estimateCard = 0.5;
	private int gameNumber = 0;
	private HashMap<Integer, Integer>betList= new HashMap<>();
	private HashMap<Integer, Double>cardList = new HashMap<>();
	public Helper(){
		
	}
	
	public void bet(int round, int money){
	
		betList.put((3*gameNumber+round), money);
		if(round==2)gameNumber++;
	}
	
	public void update(double realCard){
		cardList.put(gameNumber,realCard);		
	}
	
	public void showSize(){
		System.out.println(cardList.size());
	}
	
	public double getEstimateCard(int round, int bet){
		double average=0;
		for(int i=0;i<=gameNumber;i++){
			
			if(betList.get(3*i+round)!=null&&bet==betList.get(3*i+round)){
				average+=betList.get(3*i+round);
				
			}
		}
		average/=(double)gameNumber;
		return average;
	}

}
