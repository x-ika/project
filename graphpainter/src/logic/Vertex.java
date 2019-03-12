package logic;

import java.awt.*;
import java.util.HashSet;

public class Vertex extends Point implements java.io.Serializable {
    private VertexInfo info;

    private HashSet<Vertex> friends;

    public Vertex(int x, int y) {
        super(x, y);
        friends = new HashSet<>();
        info = new VertexInfo();
    }

    public Vertex(Vertex v) {
        this(v.x, v.y);
    }

    public Vertex() {
        this(0, 0);
    }

    public boolean contains(int X, int Y) {
        return (X -= x) * X + (Y -= y) * Y <= radius() * radius();
    }

    public boolean contains(Point p) {
        return contains(p.x, p.y);
    }

    public void add(Vertex v) {
        friends.add(v);
    }

    public int radius() {
        return info.getRadius();
    }

    public int getN() {
        return friends.size();
    }

    public boolean isConnected(Vertex v) {
        return friends.contains(v);
    }

    public Vertex getConnectedVertex(int index) {
        return (Vertex)friends.toArray()[index];
    }

    public HashSet<Vertex> getConnectedVertexs() {
        return friends;
    }

    public void setName(String name) {
        info.setName(name);
    }

    public String toString() {
        return info.getName();
    }
}
