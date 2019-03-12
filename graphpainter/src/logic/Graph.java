package logic;

import java.util.Vector;

public class Graph implements java.io.Serializable {
    private static int COUNTER = 0;

    private int nOfVerge;

    private String name;

    private Vector<Vertex> vertexs;

    public Graph() {
        nOfVerge = 0;
        name = "Graph" + ++COUNTER;
        vertexs = new Vector<>();
    }

    public int getNOfVertex() {
        return vertexs.size();
    }

    public int getNOfVerge() {
        return nOfVerge;
    }

    public int indexOf(Vertex v) {
        return vertexs.indexOf(v);
    }

    public Vertex getVertex(int index) {
        return vertexs.get(index);
    }

    public Vector<Vertex> getVertexes() {
        return vertexs;
    }

    public void addVertex(Vertex v) {
        vertexs.add(v);
    }

    public void addVertex(int x, int y) {
        addVertex(new Vertex(x, y));
    }

    public boolean addVerge(int n1, int n2) {
        if(vertexs.size() < n1 || vertexs.size() < n2) {
            return false;
        }
        return addVerge(vertexs.get(n1), vertexs.get(n2));
    }

    public boolean addVerge(Vertex v1, Vertex v2) {
        if(v1.isConnected(v2) || v1 == v2) {
            return false;
        }
        v1.add(v2);
        v2.add(v1);
        nOfVerge++;
        return vertexs.contains(v1) && vertexs.contains(v2);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
