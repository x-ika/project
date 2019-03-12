package ika.games.base;

public enum BasicGameAction implements GameAction {

    // user requests

    GUEST_REQUEST(10),
    LOG_IN(11),
    LOG_OUT(12),
    GET_STATE(13),
    SET_SETTING(14),
    DEFINE_SEED(15),
    SIT_DOWN(16),
    STAND_UP(17),
    START_ROUND(18),
    MAKE_MOVE(19),

    CHAT_GET_HISTORY(30),
    CHAT_GET_USERS(31),
    CHAT_MESSAGE(32),
    CHAT_ADD_FRIEND(33),
    CHAT_REM_FRIEND(34),

    // room events

    ROOM_STATE_CHANGED(50),
    ROOM_ROUND_STARTED(51),
    ROOM_ROUND_FINISHED(52),
    ROOM_WAITING_FOR_MOVE(53),

    UNKNOWN(0);

    private final int type;
    private final String name;

    BasicGameAction(int code) {
        this.type = code;
        this.name = name();
    }

    public int getId() {
        return type;
    }

    public String getName() {
        return name;
    }

}
