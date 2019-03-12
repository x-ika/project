package floodit.solver.students.tkesh;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class solver {
    static int [][] arr ;
    static int n, m;
    static String res="";
    static String g;

	public static void main(String[] args) {
    	read("000_12-12-4.txt");
        if(Check(arr)){System.out.println("ar aris sachiro"); return;}
        HashMap<String , String> parent= new HashMap<>();
        HashMap<String,Integer> parent1= new HashMap<>();
        Queue<int[][]> Q= new LinkedList<>();
        Q.add(arr);
        parent1.put(getStr(arr), 0);
        parent.put(getStr(arr), getStr(arr));
        g = getStr(arr);
        while (!Q.isEmpty()){
            int [][] tmp=Q.poll();
            for(int i=0;i<m;i++){
                if(i!=tmp[0][0]){
                    int [][] neighb=new int [n][n];
                    change(tmp, neighb);
                    dfs(0,0,tmp[0][0],i, neighb);
                     if(Check(neighb)) { 
                        res += i - '0';
                        goBack(getStr(tmp), parent, parent1);
                        for(int j=0;j<res.length();j++)
                            System.out.print(res.charAt(res.length()-j-1));
                        return;
                    }
                    String s = getStr(neighb);   
                    if(!parent1.containsKey(s)){
                        Q.add(neighb);    
                        parent1.put(s,i);
                        parent.put(s, getStr(tmp));
                    }
                }
            }            
        } 
    }
    
    static void goBack(String goal, HashMap<String, String > parent, HashMap<String, Integer> parent1){
        String s=goal;
        while(!s.equals(g)){
        	int a = parent1.get(s);
            res+=a - '0';
            s=parent.get(s);
        }
    }

    static void dfs(int a, int b, int color, int newColor,int [][] arr){
        arr[a][b]=newColor;
        if(a-1>=0 &&  arr[a-1][b]==color) dfs(a-1,b, color, newColor, arr);
        if(a+1<n &&  arr[a+1][b]==color) dfs(a+1,b, color, newColor, arr);
        if(b-1>=0 &&  arr[a][b-1]==color) dfs(a,b-1, color, newColor, arr);
        if(b+1<n &&  arr[a][b+1]==color) dfs(a,b+1, color, newColor, arr);  
    }
    
    static void change(int [][] arr , int [][] arr1){
        for(int i=0;i<n;i++){
            for (int j = 0; j < m; j++) {
                arr1[i][j]=arr[i][j];
            }
        }
    }
        
    static boolean Check(int [][] arr){
        int a=arr[0][0];
        int n=arr.length;
        for(int i=0;i<n;i++)
            for(int j=0;j<m;j++)	
                if(arr[i][j]!=a) return false; 
        return true;
    }
        
    static String getStr(int [][] arr){
        String res="";
        for(int i=0;i<n;i++)
            for(int j=0;j<n;j++){
                res+=arr[i][j];
                if(j==n-1) res+=" ";
            }        
        return res;
    }
    
    private static void read(String filename){	
		try {
			BufferedReader reader = new BufferedReader (new FileReader(filename));
			String line = reader.readLine();
			StringTokenizer token = new StringTokenizer(line);
			n = Integer.parseInt(token.nextToken());
			m = Integer.parseInt(token.nextToken());
	        arr=new int[n][m];
			for (int i = 0; i < n; i++) {
				line = reader.readLine();
				String str = "";
				for(int k = 0; k < line.length(); k++) {
					if(line.charAt(k) != ' ') {
						str = str + line.charAt(k);
					}
				}
				for (int j = 0; j < m; j++) {
					int a = str.charAt(j) - '0';
					arr[i][j] = a;
				}
			}	
			reader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
	}
}
