package ika.games.joker.server;

import ika.games.base.*;
import ika.games.base.ObjectType;
import ika.games.base.controller.action.*;

public class JokerNamespace {

    private JokerNamespace() {}

    public static final String JOKER_TYPE_CHOICE = "joker_types";
    public static final String PENALTY_TYPE_CHOICE = "penalty_types";
    public static final String PENALTY_CHOICE8 = "penalty_amounts8";
    public static final String PENALTY_CHOICE9 = "penalty_amounts9";
    public static final String DELAY_TURN_CHOICE = "delay_player_turns";

    public static final int MSG_TYPE_JOKER_SCOREBOARD = 210;
    public static final int MSG_TYPE_JOKER_CARDS = 220;

    public static final int TYPE_CLAIM = 1;
    public static final int TYPE_PLAY = 2;
    public static final int TYPE_SUIT = 3;

    //-----------------------------------------------------------------------------------

    public enum JokerType implements ObjectType {

        USUAL(       1, "USUAL"),
        NINES_9999(  2, "NINES_9999"),
        NINES_999(   3, "NINES_999"),
        NINES_99(    4, "NINES_99"),
        NINES_9(     5, "NINES_9");

        public final int type;
        public final String name;

        JokerType(int type, String name) {
            this.name = name;
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public static JokerType byType(int type) {
            return ObjectType.getObjectByType(values(), type);
        }

    }

    public enum JokerPenaltyType implements ObjectType {

        USUAL( 1, "USUAL"),
        SPEC(  2, "SPEC");

        public final int type;
        public final String name;

        JokerPenaltyType(int type, String name) {
            this.name = name;
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public static JokerPenaltyType byType(int type) {
            return ObjectType.getObjectByType(values(), type);
        }

    }

    public static final class JokerSettings {

        public final JokerType jokerType;
        public final JokerPenaltyType penaltyType;
        public final int penalty8, penalty9, playerTurnDelay;
        public final long stakeFromEachPlayer;

        public JokerSettings(long stakeFromEachPlayer, int playerTurnDelay, JokerType jokerType, JokerPenaltyType penaltyType, int penalty8, int penalty9) {
            this.stakeFromEachPlayer = stakeFromEachPlayer;
            this.playerTurnDelay = 1000 * playerTurnDelay;
            this.jokerType = jokerType;
            this.penaltyType = penaltyType;
            this.penalty8 = penalty8;
            this.penalty9 = penalty9;
        }

        public int hashCode() {
            int a = jokerType == null ? 0 : jokerType.getType();
            int b = penaltyType == null ? 0 : penaltyType.getType();
            return a ^ b << 9 ^ penalty8 ^ penalty9 << 9 ^ playerTurnDelay ^ (int) stakeFromEachPlayer;
        }

        public boolean equals(Object o) {
            if (!(o instanceof JokerSettings)) {
                return false;
            }
            JokerSettings s = (JokerSettings) o;
            return stakeFromEachPlayer == s.stakeFromEachPlayer && playerTurnDelay == s.playerTurnDelay &&
                    jokerType == s.jokerType && penaltyType == s.penaltyType &&
                    penalty8 == s.penalty8 && penalty9 == s.penalty9;
        }

    }

    public enum JokerAction implements GameAction {

        USER_CLAIM(         21, "USER_CLAIM"),
        USER_PLAY_CARD(     22, "USER_PLAY_CARD"),
        USER_SUIT(          23, "USER_SUIT"),
        USER_KICK_BOT(      24, "USER_SUIT"),
        USER_GET_LIST(      25, "USER_GET_LIST"),
        USER_TAKE_QUEUE(    26, "USER_TAKE_QUEUE"),
        USER_LEAVE_QUEUE(   27, "USER_LEAVE_QUEUE"),

        ROOM_START_ROUND(   51, "ROOM_START_ROUND"),
        ROOM_END_ROUND(     52, "ROOM_END_ROUND"),
        ROOM_NEW_GAME(      53, "ROOM_NEW_GAME"),
        ROOM_ASSIGN_GAME(   54, "ROOM_ASSIGN_GAME"),
        ROOM_GET_BETS(      55, "ROOM_GET_BETS"),
        ROOM_SUIT(          56, "ROOM_SUIT"),
        ROOM_CLAIM(         57, "ROOM_CLAIM"),
        ROOM_PLAY(          58, "ROOM_PLAY"),
        ROOM_SET_TRUMP(     59, "ROOM_SET_TRUMP"),
        ROOM_DISTRIBUTE(    60, "ROOM_DISTRIBUTE"),
        ROOM_TAKE(          61, "ROOM_TAKE");

        public final int id;
        public final String name;

        JokerAction(int code, String name) {
            this.id = code;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

    }

    public enum JokerResult implements Result {

        INVALID_TURN(     21),
        UNEXPECTED_TURN(  22),
        INVALID_SETTINGS( 23);

        public final int id;
        public final String name;

        JokerResult(int id) {
            this.id = id;
            this.name = name();
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

    }

    public static class JokerLogin extends Login {
        public final int roundId;

        public JokerLogin(int userId, int roomId, String session, String address, int roundId) {
            super(userId, roomId, session, address);
            this.roundId = roundId;
        }
    }

    public static final class PlayAction extends UserActionParam {

        public final int playType, call, suit;
        public final String card;

        public PlayAction(int playType, String card, int call, int suit) {
            this.playType = playType;
            this.card = card;
            this.call = call;
            this.suit = suit;
        }
    }

    public static final class GetList extends UserActionParam {

        public final int whole;

        public GetList(int whole) {
            this.whole = whole;
        }

    }

    public static final class PlayerGamePreferences extends UserActionParam {

        public final String stake, delay, jokerType, penaltyType, penalty8, penalty9;

        public PlayerGamePreferences(String stake, String delay, String jokerTypes, String penaltyTypes, String penalty8, String penalty9) {
            this.stake = stake;
            this.delay = delay;
            this.jokerType = jokerTypes;
            this.penaltyType = penaltyTypes;
            this.penalty8 = penalty8;
            this.penalty9 = penalty9;
        }

    }

    public static final class PlayerScore{
        public byte[] hexName;
        public int score;
        public PlayerScore(byte[] hexName,int score){
            this.hexName = hexName;
            this.score = score;
        }
    }

}
