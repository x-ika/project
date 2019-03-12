package ika.games.base.controller.action;

public class Setting extends UserActionParam {
    public final String key;
    public final String value;

    public Setting(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
