package ika.games.base.controller.action;

public class Login extends UserActionParam {
    public final int userId, roomId;
    public final String session, address;

    public Login(int userId, int roomId, String session, String address) {
        this.address = address;
        this.roomId = roomId;
        this.session = session;
        this.userId = userId;
    }

    public int user() {
        return userId;
    }

    public int room() {
        return roomId;
    }

    public String[] params() {
        return new String[]{address};
    }
}
