package oldprog;

import java.util.*;
import java.io.*;

public class Graph {
    static int nVertex, dfn = 0,
            graph[][], nNeighbours[],
            parent[], low[], num[];

    static Stack<Bridge> stack, bridges;

    static Set<Integer> points;

    static class Bridge {
        int vertex1, vertex2;

        Bridge(int v1, int v2) {
            vertex1 = v1;
            vertex2 = v2;
        }
    }

    public static void readGraph() throws IOException {
        stack = new Stack<>();
        bridges = new Stack<>();
        points = new HashSet<>();

        BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream("graph.txt")));
        nVertex = Integer.parseInt(br.readLine());
        graph = new int [nVertex][];
        nNeighbours = new int[nVertex];
        parent = new int[nVertex];
        num = new int[nVertex];
        low = new int[nVertex];
        for(int i = 0; i < nVertex; i++) {
            StringTokenizer str = new StringTokenizer(br.readLine());
            nNeighbours[i] = Integer.parseInt(str.nextToken());
            graph[i] = new int[nNeighbours[i]];
            for (int j = 0; j < nNeighbours[i]; j++) {
                graph[i][j] = Integer.parseInt(str.nextToken());
            }
        }
    }

    static void bidfs(int v) {
        int neighbour;
        dfn++;
        num[v] = low[v] = dfn;

        for (int i = 0; i < nNeighbours[v]; i++) {
            neighbour = graph[v][i];
            Bridge rib = new Bridge(v, neighbour);
            if (num[neighbour] == 0) {
                stack.push(rib);
                parent[neighbour] = v;
                bidfs(neighbour);
                if (low[neighbour] >= num[v]) {
                    if (i > 0 || v > 0) {
                        points.add(v);
                    }
                    processAPOrRoot(v, rib);
                } else {
                    low[v]= Math.min(low[v], low[neighbour]);
                    }
            } else
            if (num[neighbour] < num[v] && neighbour != parent[v]) {
                stack.push(rib);
                low[v] = Math.min(num[neighbour], low[v]);
            }
        }
    }

    private static void processAPOrRoot(int v, Bridge rib) {
        Bridge ribOfBicomponent;
        ribOfBicomponent = stack.pop();
        if (ribOfBicomponent != rib) {
            System.out.print("new bicomponent : {");
            System.out.print(ribOfBicomponent.vertex1 +"-"+ribOfBicomponent.vertex2);
            do {
                ribOfBicomponent = stack.pop();
                System.out.print(", " + ribOfBicomponent.vertex1 +
                        "-" + ribOfBicomponent.vertex2);
            } while (ribOfBicomponent != rib);
            System.out.println("};");
        } else {
            bridges.push(ribOfBicomponent);
        }
    }

    public static void points() {
        System.out.print("points : ");
        for (Object point : points) {
            System.out.print(point + "; ");
        }
        System.out.println();
    }

    public static void bridges() {
        System.out.print("Bridges : ");
        Bridge bridge;
        while(!bridges.empty()) {
            bridge = bridges.pop();
            System.out.print(bridge.vertex1 +"-"+bridge.vertex2+"; ");
        }
    }

    public static void main (String args[]) throws IOException {
        readGraph();
        bidfs(0);
        points();
        bridges();
        System.out.println();
    }
}