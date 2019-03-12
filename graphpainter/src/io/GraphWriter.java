package io;

import java.io.*;

public class GraphWriter {
    private ObjectOutputStream oos;

    public GraphWriter(File file) {
        try {
            oos = new ObjectOutputStream(new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean write(Object o) {
        try {
            oos.writeObject(o);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
