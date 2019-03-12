package ika.games.base.controller.action;

import ika.games.base.controller.Player;

public class UserActionParam {

    private static final String[] DEF = new String[0];

    public Player player;

    public int user() {
        return player == null ? 0 : player.user.id;
    }

    public int room() {
        return player == null ? 0 : player.owner.getId();
    }

    public int round() {
        return player == null ? 0 : player.owner.getRoundId(user());
    }

    public String[] params() {
        return DEF;
    }

}
