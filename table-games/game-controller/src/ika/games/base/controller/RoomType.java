package ika.games.base.controller;

import ika.games.base.ObjectType;

public enum RoomType implements ObjectType {

    INACTIVE(0, "Inactive"),
    USUAL(1, "Usual"),
    TURNAMENT(2, "Turnament");

    public final int type;
    public final String name;

    RoomType(int type, String name) {
        this.name = name;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
