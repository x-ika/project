package logic.db;

import java.io.Serializable;

public class Record implements Serializable {
    private final String key;

    public String getKey() {
        return key;
    }

    public Record(String key) {
        this.key = key;
    }
}
