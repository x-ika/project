package player.students.machika;

import main.Turn;
import main.Checker;
import main.Player;

import java.lang.reflect.Array;
import java.util.*;

import javax.swing.text.html.HTMLDocument.HTMLReader.IsindexAction;
public class game implements Player {
    public void gameOver() {}
	public static void main(String[] args) {
		int [][] mas=new int [][]{
			{0,2,0,2,0,2,0,2},

			{2,0,2,0,2,0,2,0},

			{0,2,0,2,0,2,0,2},

			{0,0,0,0,0,0,0,0},

			{0,0,0,0,0,0,0,0},

			{1,0,1,0,1,0,1,0},

			{0,1,0,1,0,1,0,1},

			{1,0,1,0,1,0,1,0}
		};

		game g=new game();
		ArrayList<int [][]> list = new ArrayList<>();
	}




	static int maxDepth=10;

	int [][] dir={{-1,-1,1,1},{-1,1,-1,1}};







	public Turn makeTurne(int[][] mas, int value) {
		alfabeta(-1000000, 100000, value, maxDepth, mas);
		ArrayList<Checker> kill= new ArrayList<>();
		Checker first=new Checker(0, 0, 0);
		Checker last=new Checker(0, 0, 0);
		for (int i = 0; i < mas.length; i++) {
			for (int j = 0; j < mas.length; j++) {
				if(((mas[i][j]==value || mas[i][j]==value+3)) && ans[i][j]==0)
					first=new Checker(i,j, mas[i][j]);
				if(mas[i][j]==0 && (ans[i][j]==value|| ans[i][j]==value+3))
					last=new Checker(i, j, ans[i][j]);
				if((mas[i][j]!=3-value ||mas[i][j]==6-value) && ans[i][j]==0)
					kill.add(new Checker(i,j,mas[i][j]));
			}
		}
		return new Turn(first, last, kill);
	}








	public int heur(int [][] mas ){
		int c1=0,c2=0;
		for (int i = 0; i < mas.length; i++) {
			for (int j = 0; j < mas.length; j++) {
				if(mas[i][j]%3==1){
					c1++;
					if(mas[i][j]>3) c1+=3;

				}
				if(mas[i][j]%3==2){
					c2++;
					if(mas[i][j]>3) c2+=3;
				}
			}
		}

		if(c2==0) return 1000000;
		if(c1==0) return -1000000;
		return c1-c2;
	}


	static int [][] ans=new int [8][8];

	public boolean gameOver(int [][] mas){
		int c1=0,c2=0;
		for (int i = 0; i < mas.length; i++) {
			for (int j = 0; j < mas.length; j++) {
				if(mas[i][j]%3==1){
					c1++;
					if(mas[i][j]>3) c1+=3;

				}
				if(mas[i][j]%3==2){
					c2++;
					if(mas[i][j]>3) c2+=3;
				}
			}
		}
		if(c1==0 || c2==0) return true;
		return false;
	}


	public int alfabeta(int alfa, int beta, int n, int depth, int [][] mas){
		ArrayList<int [][]> list = allNeigbours(n, mas);
		if(depth ==0 || gameOver(mas) ) return heur(mas);
		if(n== 1){
			for (int i = 0; i < list.size(); i++) {
				int x =  alfabeta( alfa, beta, 3-n, depth-1,list.get(i));
				if(x>alfa){
					alfa = x;
					if(depth==maxDepth){
					for (int q = 0; q < mas.length; q++) {
						for (int p = 0; p< mas.length; p++) {
							ans[q][p]=list.get(i)[q][p];
						}
					}
					}
				}
				if(alfa>=beta) break;
			}
			return alfa;

		}else{
			for (int i = 0; i < list.size(); i++) {
				int x =  alfabeta(alfa,beta, 3-n, depth-1,list.get(i));
				if( x < beta){
					beta = x;
					if(depth==maxDepth){
					for (int q = 0; q < mas.length; q++) {
						for (int p = 0; p< mas.length; p++) {
							ans[q][p]=list.get(i)[q][p];
						}
					}
					}
				}
				if(alfa >= beta){
					break;
				}
			}
			return beta;
		}
	}


	public boolean inBorders(int x, int y){
		if(x < 0 || x > 7 || y < 0 || y > 7) return false;
		return true;
	}

	public boolean QvisSvlaArsebobs(int x, int y, int n, int [][] mas){
		if(n==1){
			if(inBorders(x-1, y+1) && mas[x-1][y+1]==0) return true;
			if(inBorders(x-1, y-1) && mas[x-1][y-1]==0)  return true;
		}else{
			if(inBorders(x+1, y-1) && mas[x+1][y-1]==0) return true;
			if(inBorders(x-1, y-1) && mas[x-1][y-1]==0)  return true;
		}
		return false;
	}

	public boolean DamkisSvlaArsebobs(int x, int y , int n , int [][] mas){
		for(int i=0;i<4;i++){
			if(inBorders(x+dir[0][i], y+dir[1][i]) && mas[x+dir[0][i]][ y+dir[1][i]]==0) return true;
		}
		return false;
	}

	public boolean svlaArsebobs(int n, int [][] mas){
		for (int i = 0; i < mas.length; i++) {
			for (int j = 0; j < mas.length; j++) {
				if(mas[i][j]<3 && QvisSvlaArsebobs(i, j, n, mas)) return true;
				if(mas[i][j]>3 && DamkisSvlaArsebobs(i, j, n, mas)) return true;
			}
		}
		return false;

	}

	public boolean mkvleliQva(int x, int y, int n, int[][]mas){
			if(n==1){
				for(int i=0;i<4;i++){
					if(inBorders(x+dir[0][i], y+dir[1][i])&& mas[x+dir[0][i]][y+dir[1][i]]%3==2 && inBorders(x+2*dir[0][i], y+2*dir[1][i]) && mas[x+2*dir[0][i]][y+2*dir[1][i]]==0)
						return true;
				}
			}else {
				for(int i=0;i<4;i++){
					if(inBorders(x+dir[0][i], y+dir[1][i])&& mas[x+dir[0][i]][y+dir[1][i]]%3==1 && inBorders(x+2*dir[0][i], y+2*dir[1][i]) && mas[x+2*dir[0][i]][y+2*dir[1][i]]==0)
						return true;
				}

			}
			return false;
	}




	public boolean mkvleliDamka(int x, int y, int n, int [][] mas){
		if(n==4){
			for(int i=0;i<4;i++){
				for(int j=1;j<8;j++){
					if(!inBorders(x+j*dir[0][i], y+j*dir[1][i])|| mas[x+j*dir[0][i]][y+j*dir[1][i]]%3==1) break;
					if(inBorders(x+j*dir[0][i], y+j*dir[1][i])&& mas[x+j*dir[0][i]][y+j*dir[1][i]]%3==2 && inBorders(x+(j+1)*dir[0][i], y+(j+1)*dir[1][i]) && mas[x+(j+1)*dir[0][i]][y+(j+1)*dir[1][i]]!=0)break;
					else if(inBorders(x+j*dir[0][i], y+j*dir[1][i])&& mas[x+j*dir[0][i]][y+j*dir[1][i]]%3==2 && inBorders(x+(j+1)*dir[0][i], y+(j+1)*dir[1][i]) && mas[x+(j+1)*dir[0][i]][y+(j+1)*dir[1][i]]==0)
						return true;
				}
			}
		}else {
			for(int i=0;i<4;i++){
				for(int j=1;j<8;j++){
					if(!inBorders(x+j*dir[0][i], y+j*dir[1][i])|| mas[x+j*dir[0][i]][y+j*dir[1][i]]%3==2) break;
					if(inBorders(x+j*dir[0][i], y+j*dir[1][i])&& mas[x+j*dir[0][i]][y+j*dir[1][i]]%3==1 && inBorders(x+(j+1)*dir[0][i], y+(j+1)*dir[1][i]) && mas[x+(j+1)*dir[0][i]][y+(j+1)*dir[1][i]]!=0) break;
					if(inBorders(x+j*dir[0][i], y+j*dir[1][i])&& mas[x+j*dir[0][i]][y+j*dir[1][i]]%3==1 && inBorders(x+(j+1)*dir[0][i], y+(j+1)*dir[1][i]) && mas[x+(j+1)*dir[0][i]][y+(j+1)*dir[1][i]]==0)
						return true;
				}
			}
		}
		return false;
	}




	public boolean mkvleliTipiexists(int n, int [] [] mas){
		if(n==1){
			for(int i=0;i<8;i++){
				for(int j=0;j<8;j++){
					if(mas[i][j]==1 && mkvleliQva(i,j,n,mas)) return true;
					if(mas[i][j]==4 && mkvleliDamka(i, j, n, mas)) return true;
				}
			}
		}else{
			for(int i=0;i<8;i++){
				for(int j=0;j<8;j++){
					if(mas[i][j]==2 && mkvleliQva(i,j,n,mas)) return true;
					if(mas[i][j]==5 && mkvleliDamka(i, j, n, mas)) return true;
				}
			}

		}
		return false;
	}



	public void qvitMokvla(int x, int y , int n, int [][] a, ArrayList<int [][]> list){
		int [][]mas=new int [8][8];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < mas.length; j++) {
				mas[i][j]=a[i][j];
			}
		}
		if(n==1){
			for(int i=0;i<4;i++){
				if(inBorders(x+dir[0][i], y+dir[1][i]) && a[x+dir[0][i]][y+dir[1][i]]%3==2&& inBorders(x+2*dir[0][i], y+2*dir[1][i]) && a[x+2*dir[0][i]][y+2*dir[1][i]]==0 ){
					mas=new int [8][8];
					for (int w = 0; w < a.length; w++) {
						for (int l = 0; l < mas.length; l++) {
							mas[w][l]=a[w][l];
						}
					}


					mas[x][y]=0;
					mas[x+2*dir[0][i]][y+2*dir[1][i]]=1;
					mas[x+dir[0][i]][y+dir[1][i]]=0;
					if(x+2*dir[0][i]==0){
						mas[x+2*dir[0][i]][y+2*dir[1][i]]=4;
						if(mkvleliDamka(x+2*dir[0][i],y+2*dir[1][i], 4, mas))
							damkitMokvla(x+2*dir[0][i],y+2*dir[1][i], 4, mas, list);
					}else{
						if(mkvleliQva(x+2*dir[0][i],y+2*dir[1][i], 1, mas)) qvitMokvla(x+2*dir[0][i],y+2*dir[1][i], 1,  mas, list);
						else list.add(mas);
					}
				}
			}
		}else{
			for(int i=0;i<4;i++){
				if(inBorders(x+dir[0][i], y+dir[1][i]) && a[x+dir[0][i]][y+dir[1][i]]%3==1 && inBorders(x+2*dir[0][i], y+2*dir[1][i]) && a[x+2*dir[0][i]][y+2*dir[1][i]]==0 ){
					mas[x][y]=0;
					mas[x+2*dir[0][i]][y+2*dir[1][i]]=2;
					mas[x+dir[0][i]][y+dir[1][i]]=0;
					if(x+2*dir[0][i]==7){
						mas[x+2*dir[0][i]][y+2*dir[1][i]]=5;
						if(mkvleliDamka(x+2*dir[0][i],y+2*dir[1][i], 5, mas))
							damkitMokvla(x+2*dir[0][i],y+2*dir[1][i], 5, mas, list);
					}else{
						if(mkvleliQva(x+2*dir[0][i],y+2*dir[1][i], 2, mas))
							qvitMokvla(x+2*dir[0][i],y+2*dir[1][i], 2,  mas, list);
						else list.add(mas);
					}
				}
			}
		}
		}




	public void damkitMokvla(int x, int y , int n, int [][] a, ArrayList<int [][]> list){

		if(n==4){

			for(int i=0;i<4;i++){
				for(int j=1;j<8;j++){

					if(!inBorders(x+j*dir[0][i], y+j*dir[1][i]) || a[x+j*dir[0][i]][y+j*dir[1][i]]%3==1) break;


					if(inBorders(x+j*dir[0][i], y+j*dir[1][i])&& a[x+j*dir[0][i]][y+j*dir[1][i]]%3==2 && inBorders(x+(j+1)*dir[0][i], y+(j+1)*dir[1][i])
						&& a[x+(j+1)*dir[0][i]][y+(j+1)*dir[1][i]]!=0)break;

					if(inBorders(x+j*dir[0][i], y+j*dir[1][i])&& a[x+j*dir[0][i]][y+j*dir[1][i]]%3==2 && inBorders(x+(j+1)*dir[0][i], y+(j+1)*dir[1][i])
							&& a[x+(j+1)*dir[0][i]][y+(j+1)*dir[1][i]]==0){

						for(int k=0;k<8;k++){
							if(inBorders(x+(j+1+k)*dir[0][i], y+(j+1+k)*dir[1][i]) &&  a[x+(j+1+k)*dir[0][i]][y+(j+1+k)*dir[1][i]]==0){
								int [][] ragaca=new int [8][8];
								for (int l= 0; l < a.length; l++) {
									for (int w = 0; w < a.length; w++) {
										ragaca[l][w]=a[l][w];
									}
								}
								ragaca[x][y]=0;
								ragaca[x+j*dir[0][i]][ y+j*dir[1][i]]=0;
								ragaca[x+(j+1+k)*dir[0][i]][ y+(j+1+k)*dir[1][i]]=n;
								if(mkvleliDamka(x+(j+1+k)*dir[0][i], y+(j+1)*dir[1][i], n, ragaca)){
									damkitMokvla(x+(j+1+k)*dir[0][i], y+(j+1)*dir[1][i], n, ragaca,list);
								}
								else list.add(ragaca);
							}else break;
						}
						break;
					}
				}

			}
		}else{	//n==5

			for(int i=0;i<4;i++){
				for(int j=1;j<8;j++){

					if(!inBorders(x+j*dir[0][i], y+j*dir[1][i]) || a[x+j*dir[0][i]][y+j*dir[1][i]]%3==2) break;


					if(inBorders(x+j*dir[0][i], y+j*dir[1][i])&& a[x+j*dir[0][i]][y+j*dir[1][i]]%3==1 && inBorders(x+(j+1)*dir[0][i], y+(j+1)*dir[1][i])
						&& a[x+(j+1)*dir[0][i]][y+(j+1)*dir[1][i]]!=0) break;

					if(inBorders(x+j*dir[0][i], y+j*dir[1][i])&& a[x+j*dir[0][i]][y+j*dir[1][i]]%3==1 && inBorders(x+(j+1)*dir[0][i], y+(j+1)*dir[1][i])
							&& a[x+(j+1)*dir[0][i]][y+(j+1)*dir[1][i]]==0)
					{

						for(int k=0;k<8;k++){

							if(inBorders(x+(j+1+k)*dir[0][i], y+(j+1+k)*dir[1][i]) &&  a[x+(j+1+k)*dir[0][i]][y+(j+1+k)*dir[1][i]]==0){

								int [][] ragaca=new int [8][8];
								for (int l= 0; l < a.length; l++) {
									for (int w = 0; w < a.length; w++) {
										ragaca[l][w]=a[l][w];
									}
								}
								ragaca[x][y]=0;
								ragaca[x+j*dir[0][i]][ y+j*dir[1][i]]=0;
								ragaca[x+(j+1+k)*dir[0][i]][ y+(j+1+k)*dir[1][i]]=n;
								if(mkvleliDamka(x+(j+1+k)*dir[0][i], y+(j+1+k)*dir[1][i], n, ragaca)){
									damkitMokvla(x+(j+1+k)*dir[0][i], y+(j+1+k)*dir[1][i], n, ragaca,list);
								}
								else list.add(ragaca);
							}else break;
						}
						break;
					}
				}

			}
		}
	}



	public void QvisSvla(int x, int y , int n, int [][]mas, ArrayList<int [][]> list){
		if(n==1){
			if(inBorders(x-1, y+1) && mas[x-1][y+1]==0){
				int [][] ragaca=new int [8][8];
				for (int l= 0; l < mas.length; l++) {
					for (int w = 0; w < mas.length; w++) {
						ragaca[l][w]=mas[l][w];
					}
				}
				ragaca[x][y]=0;
				ragaca[x-1][y+1]=n;
				if(x==1) ragaca[x-1][y+1]+=3;
				list.add(ragaca);
			}
			if(inBorders(x-1, y-1) && mas[x-1][y-1]==0){
				int [][] ragaca=new int [8][8];
				for (int l= 0; l < mas.length; l++) {
					for (int w = 0; w < mas.length; w++) {
						ragaca[l][w]=mas[l][w];
					}
				}
				ragaca[x][y]=0;
				ragaca[x-1][y-1]=n;
				if(x==1) ragaca[x-1][y-1]+=3;
				list.add(ragaca);
			}
		}else{
			if(inBorders(x+1, y+1) && mas[x+1][y+1]==0){
				int [][] ragaca=new int [8][8];
				for (int l= 0; l < mas.length; l++) {
					for (int w = 0; w < mas.length; w++) {
						ragaca[l][w]=mas[l][w];
					}
				}
				ragaca[x][y]=0;
				ragaca[x+1][y+1]=n;
				if(x==6) ragaca[x+1][y+1]+=3;
				list.add(ragaca);
			}
			if(inBorders(x+1, y-1) && mas[x+1][y-1]==0){
				int [][] ragaca=new int [8][8];
				for (int l= 0; l < mas.length; l++) {
					for (int w = 0; w < mas.length; w++) {
						ragaca[l][w]=mas[l][w];
					}
				}
				ragaca[x][y]=0;
				ragaca[x+1][y-1]=n;
				if(x==6) ragaca[x+1][y-1]=n+3;
				list.add(ragaca);
			}
		}

	}
	public void damkisSvla(int x, int y , int n, int [][]mas, ArrayList<int [][]> list){
			for(int i=0;i<4;i++){
				for(int j=1;j<8;j++){
					if(!inBorders(x+j*dir[0][i], y+j*dir[1][i]) || mas[x+j*dir[0][i]][y+j*dir[1][i]]!=0) break;
					int [][] ragaca=new int [8][8];
					for (int l= 0; l < mas.length; l++) {
						for (int w = 0; w < mas.length; w++) {
							ragaca[l][w]=mas[l][w];
						}
					}
					ragaca[x+j*dir[0][i]][y+j*dir[1][i]]=n;
					ragaca[x][y]=0;
					list.add(ragaca);
				}
			}
	}


	public ArrayList<int [][]> allNeigbours(int n, int [] [] mas){
		ArrayList<int [][]> list= new ArrayList<>();
		if(mkvleliTipiexists(n, mas)){
			for(int i=0;i<8;i++){
				for(int j=0;j<8;j++){
					if(mas[i][j]%3==n){
						if(mas[i][j]>3 && mkvleliDamka(i, j, n, mas)) damkitMokvla(i, j, n, mas, list);
						if(mas[i][j]<3 && mkvleliQva(i, j, n, mas)) qvitMokvla(i, j, n, mas, list);
					}
				}
			}
		}else{
			if(svlaArsebobs(n, mas)){
				for(int i=0;i<8;i++){
					for(int j=0;j<8;j++){
						if(mas[i][j]%3==n){
							if(mas[i][j]>3 && DamkisSvlaArsebobs(i, j, n, mas)) damkisSvla(i, j, n, mas, list);
							if(mas[i][j]<3 && QvisSvlaArsebobs(i, j, n, mas)) QvisSvla(i, j, n, mas, list);
						}
					}
				}
			}
		}
		return list;
	}
}
