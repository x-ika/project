package io;

import gui.GraphPainter;

import java.io.*;

public class GraphReader {
    private ObjectInputStream ois;

    public GraphReader(File file) {
        try {
            ois = new ObjectInputStream(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object read() {
        try {
            return ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    public GraphPainter readGraphPainter() {
        return (GraphPainter) read();
    }
}
