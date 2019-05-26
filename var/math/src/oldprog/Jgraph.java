package oldprog;

import java.util.Stack;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;

public class Jgraph {
    public static int numberOfVertex;
    public static int dfn;
    public static int numberOfPoints;
    public static int[][] graph;
    public static int[] numberOfNeighbours;
    public static int[] parent;
    public static int[] number;
    public static int[] points;
    public static int[] low;
    public static Stack stack;
    public static Stack bridges;

    public static void reading() throws IOException {
        BufferedReader tr = new BufferedReader(new InputStreamReader
                (new FileInputStream("graph")));
        numberOfVertex = Integer.parseInt(tr.readLine());
        graph = new int [numberOfVertex][numberOfVertex];
        numberOfNeighbours = new int[numberOfVertex];
        for (int i=0; i<numberOfVertex; i++) {
            StringTokenizer str;
            str = new StringTokenizer(tr.readLine());
            numberOfNeighbours[i]= Integer.parseInt(str.nextToken());
            for (int j= 0; j<numberOfNeighbours[i]; j++) {
                graph[i][j]= Integer.parseInt(str.nextToken());
            }
        }
    }

    public static void bicomponents() {
        stack = new Stack();
        bridges = new Stack();
        dfn=0;
        parent=new int[numberOfVertex];
        number=new int[numberOfVertex];
        low=new int[numberOfVertex];
        for (int i=0; i < numberOfVertex; i++) {
            parent[i]=0;
            number[i]=0;
            low[i]=0;
        }
        bidfs(0);
    }

    public static void bidfs(int currentVertice) {
        int neighbour;
        dfn++;
        number[currentVertice]=dfn;
        low[currentVertice]=dfn;
        for (int i=0; i<numberOfNeighbours[currentVertice];i++ ) {
            neighbour =graph[currentVertice][i];
            bridge rebro;
            rebro = new bridge(currentVertice,neighbour);
            if (number[neighbour]==0) {
                bridge rebroOfBicomponent;
                stack.push(rebro);
                parent[neighbour]=currentVertice;
                bidfs(neighbour);
                if (low[neighbour]>=number[currentVertice]) {
                    rebroOfBicomponent=(bridge)stack.pop();
                    if (rebroOfBicomponent!=rebro) {
                        System.out.println("There is a new bicomponent");
                        System.out.print(rebroOfBicomponent.firstVertice +"-"+rebroOfBicomponent.secondVertice +" ");
                        do {
                            rebroOfBicomponent = (bridge) stack.pop();
                            System.out.print(rebroOfBicomponent.firstVertice +"-"+rebroOfBicomponent.secondVertice +" ");
                        } while (rebroOfBicomponent!=rebro);
                        System.out.println();
                    } else {
                        bridges.push(rebroOfBicomponent);
                    }
                } else {
                    low[currentVertice]= Math.min(low[currentVertice], low[neighbour]);
                    }
            } else if ((number[neighbour]<number[currentVertice])&&(neighbour !=parent[currentVertice])) {
                stack.push(rebro);
                low[currentVertice]= Math.min(number[neighbour],low[currentVertice]);
            }
        }
    }

    public static void articulationPoints() {
        points=new int[numberOfVertex];
        int index;
        numberOfPoints=0;
        parent[0]=numberOfVertex;
        for (int i=0; i<numberOfVertex; i++) {
            index=0;
            for (int j=0; j<numberOfVertex; j++) {
                if ((parent[j]==i)&&(low[j]>= number[i])) {
                    index++;
                }
            }
            if (((i==0)&&(index>1))||((i>0)&&(index>0))) {
                points[numberOfPoints]=i;
                numberOfPoints++;
            }
        }
        System.out.println("Articulation points:");
        for (int i=0; i<numberOfPoints; i++) {
            System.out.print(points[i]+" ");
        }
        System.out.println();
    }

    public static void bridges() {
        System.out.println("Bridges:");
        bridge bridge;
        while(!bridges.empty()) {
            bridge=(bridge)bridges.pop();
            System.out.println(bridge.firstVertice +"-"+bridge.secondVertice);
        }
    }

    public static void main (String args[]) throws IOException {
        reading();
        bicomponents();
        System.out.println();
        articulationPoints();
        System.out.println();
        bridges();
    }
}
class bridge {
    int firstVertice, secondVertice;
    bridge next, previous;
    bridge (int value1, int value2){
        firstVertice =value1;
        secondVertice =value2;
    }
}
