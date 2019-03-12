/*tamar jafaridze*/

package simplepoker.player.students.TjapharSimple;

import java.util.Collection;

import simplepoker.controller.Player;

public class TjapharSimple implements Player {

	protected double card;

	public void startHand(double card) {
		this.card = card;
	}

	/* aketebs bet-s */
	public int makeBet(int me, int diller, int round, int[] money, int[] bank,
			Collection<Integer> inGame) {

		int max = 0;
		int n = -1;
		for (int x : bank) {
			max = Max(max, x);
			if (x > 1) {
                n++;
            }
		}
				
		int total = money[me];
		double coef = koef(n,card);
//		System.out.println(total);
		
		if (coef < 0.7)	return 0;
		if (round < 1 )	return Min(money[me], max - bank[me] + 3);
		
		else {
			
			if (coef > 0.7 && coef <2) {
				if ((round == 1 || round == 2))return Min(money[me], max - bank[me]+ ((card * 10 / card) *coef));
				else return Math.min(money[me], max - bank[me]);
			}
			
			else if (coef>= 2) {	
				if (round ==1 || round == 2)return Min(money[me], max - bank[me]  + ((card * 10 / card) *coef)+4);					
				else return Min(money[me], max - bank[me]);
			}			
		}
		return 0;

	}
	/*itvlis koeficients*/
	public double koef(int n, double card2){
		double c = 10*card2/3 -7/3;
		c = Math.pow(c, n);
		return c;
	}
	/*minimaluris povna*/
	public int Min(int money, double other){
		return (int)Math.min(money, other);
	}
	/*maximumis povna*/
	public int Max(int f, int s){
		return Math.max(f, s);		
	}
	
	public void finishHand(double[] allOpenedCards) {

	}
}
