package ika.games.base.controller.action;

public class Msg extends UserActionParam {
    public long id;
    public final int type;
    public final int senderId;
    public final String senderName;
    public final int receiverId;
    public final String date;
    public final String text;

    public Msg(int type, int senderId, String senderName, int receiverId, String date, String text) {
        this.type = type;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.date = date;
        this.text = text;
    }
}
