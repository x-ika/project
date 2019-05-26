package logic;

public class VertexInfo implements java.io.Serializable {
    private static int COUNTER = 0;

    private int UID;

    private int radius;

    private String name;

    VertexInfo(String name, int UID, int radius) {
        this.name = name;
        this.UID = UID;
        this.radius = radius;
    }

    VertexInfo() {
        this("A" + ++COUNTER, COUNTER, 3);
    }

    int getRadius() {
        return radius;
    }

    void setRadius(int radius) {
        this.radius = radius;
    }

    int getUID() {
        return UID;
    }

    void setUID(int UID) {
        this.UID = UID;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }
}
