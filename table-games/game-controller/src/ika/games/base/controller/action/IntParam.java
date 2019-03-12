package ika.games.base.controller.action;

public class IntParam extends UserActionParam {
    public final int value;

    public IntParam(int value) {
        this.value = value;
    }

    public int getInt() {
        return value;
    }
}
