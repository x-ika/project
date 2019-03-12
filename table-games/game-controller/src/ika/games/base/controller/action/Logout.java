package ika.games.base.controller.action;

public class Logout extends UserActionParam {
    public final String address;

    public Logout(String address) {
        this.address = address;
    }

    public String[] params() {
        return new String[]{address};
    }
}
