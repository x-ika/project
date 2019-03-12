package ika.games.base;

public enum BasicResult implements Result {

    OK(0),
    PARSER_ERROR(1),
    DB_ERROR(2),
    SYSTEM_ERROR(3),
    UNKNOWN_ACTION(4),
    UNAUTHORIZED(5),
    UNHANDLED_ACTION(6),

    INVALID_SESSION(11),
    INVALID_ROOM(12),
    INVALID_USER(13),
    USER_NOT_LOGGED(14),
    INVALID_PLACE(15),
    ALREADY_SITTING(16),
    PLACE_IS_OCCUPIED(17),
    INVALID_MOVE(18),
    UNEXPECTED_MOVE(19),

    GENERAL(99);

    private final int code;
    private final String description;

    BasicResult(int code) {
        this.code = code;
        this.description = name();
    }

    public int getId() {
        return code;
    }

    public String getName() {
        return description;
    }

}
