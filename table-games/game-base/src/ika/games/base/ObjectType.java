package ika.games.base;

public interface ObjectType {

    int getType();

    String getName();

    static <T extends ObjectType> T getObjectByType(T[] a, int type) {
        for (T t : a) {
            if (t.getType() == type) {
                return t;
            }
        }
        return null;
    }

}
