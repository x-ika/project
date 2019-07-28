package ika.games.gomoku.controller;

import ika.games.base.BasicGameAction;
import ika.games.base.BasicResult;
import ika.games.base.GameAction;
import ika.games.base.Result;
import ika.games.base.controller.BasicRoom;
import ika.games.base.controller.Controller;
import ika.games.base.controller.PersistentObject;
import ika.games.base.controller.Player;
import ika.games.base.controller.action.SitDown;
import ika.games.base.controller.action.UserActionParam;
import ika.games.gomoku.controller.action.GomokuMove;
import ika.games.gomoku.controller.player.GomokuAI;

import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class GomokuLogic extends BasicRoom {

    public static final int WIN_SEQUENCE_LENGTH = 5;
    public static final int N_ROWS = 20;
    public static final int N_COLUMNS = 20;

    private static final int EV_USERS_UPDATED = 1;
    private static final int EV_PLAY = 2;

    private int nPlayers, here, whoPlays;
    private GomokuMove played;
    private Player[] players;
    private int[][] desk;

    public GomokuLogic(PersistentObject settings, Controller controller) throws NoSuchAlgorithmException {
        super(settings, controller);
        players = new Player[nPlayers = 2];
        desk = new int[N_ROWS][N_COLUMNS];
    }

    private int getMoveTime() {
        return settings.getInt("MOVE_TIME");
    }

    //-----------------------------------------------------------------------------------

    public synchronized Result handleAction(GameAction action, Player actor, UserActionParam param) throws Exception {
        debug("_handleAction", action.getName());
        if (action == BasicGameAction.LOG_IN) {
            if (observers.containsKey(actor.user.id)) {
                return BasicResult.OK;
            }
            observers.put(actor.user.id, actor);
        }
        if (actor != observers.get(actor.user.id)) {
            return BasicResult.USER_NOT_LOGGED;
        }
        Result result = BasicResult.OK;
        if (action instanceof BasicGameAction) {
            switch ((BasicGameAction) action) {
                case LOG_IN:
                    notify(EV_USERS_UPDATED);
                    break;
                case LOG_OUT:
                    standUp(actor);
                    observers.remove(actor.user.id);
                    notify(EV_USERS_UPDATED);
                    break;
                case SIT_DOWN:
                    result = sitDown(actor, ((SitDown) param).sit);
                    break;
                case STAND_UP:
                    result = standUp(actor);
                    break;
                case MAKE_MOVE:
                    result = userPlay(actor, (GomokuMove) param);
                    break;
                case START_ROUND:
                    result = start(actor);
                    break;
            }
        }
        return result;
    }

    private Result sitDown(Player player, int sit) {
        if (getPlayerSit(player) != -1) {
            return BasicResult.GENERAL;
        }
        if (players[sit] != null) {
            return BasicResult.GENERAL;
        }
        players[sit] = player;
        here++;
        notify(EV_USERS_UPDATED);
        return BasicResult.OK;
    }

    private Result standUp(Player player) {
        int sit = getPlayerSit(player);
        if (sit == -1 || players[sit] == null) {
            return BasicResult.GENERAL;
        }
        players[sit] = null;
        here--;
        notify(EV_USERS_UPDATED);
        return BasicResult.OK;
    }

    private Result userPlay(Player player, GomokuMove move) {
        if (whoPlays == -1 || players[whoPlays] != player || played != null) {
            return BasicResult.UNEXPECTED_MOVE;
        }
        if (desk[move.i][move.j] != 0) {
            return BasicResult.INVALID_MOVE;
        }
        played = move;
        notify(EV_PLAY);
        return BasicResult.OK;
    }

    private int getPlayerSit(Player player) {
        for (int i = 0; i < nPlayers; i++) {
            if (players[i] == player) {
                return i;
            }
        }
        return -1;
    }

    private Result start(Player player) {
        return sitDown(createLocalPlayer("AI_13"), 1 - getPlayerSit(player));
    }

    //-----------------------------------------------------------------------------------

    public synchronized void startWorking() {
        super.startWorking();
        startWorker(this::run, true);
    }

    protected byte[][] writeState(Player receiver, GameAction action, Player actor, boolean wholeState) {
        writeStateHeader();

        builder.writeInt(receiver.user.id);
        builder.writeBytes(receiver.user.getHexName());

        builder.writeInt(roundId);
        builder.writeInt(nPlayers);
        int sit = getPlayerSit(receiver);
        builder.writeInt(sit);
        builder.writeInt(whoPlays);
        builder.writeInt(isWaitingFor(EV_PLAY) ? TimeUnit.MILLISECONDS.toSeconds(timeRest(EV_PLAY)) : -1);
        builder.writeInt(getMoveTime());
        for (int i = 0; i < nPlayers; i++) {
            builder.writeBytes(players[i] == null ? new byte[0] : players[i].user.getHexName());
        }
        builder.writeInt(N_ROWS);
        builder.writeInt(N_COLUMNS);
        for (int i = 0; i < N_ROWS; i++) {
            for (int j = 0; j < N_COLUMNS; j++) {
                builder.writeInt(desk[i][j]);
            }
        }
        GomokuMove[] win = checkGameOver();
        if (win == null) {
            builder.writeInt(0);
        } else {
            builder.writeInt(win.length);
            for (GomokuMove move : win) {
                builder.writeInt(move.i);
                builder.writeInt(move.j);
            }
        }

        appendMessage();
        return buffer.toArray(new byte[0][]);
    }

    private synchronized void run() {

        whoPlays = -1;

        while (here < nPlayers) {
            waitFor(EV_USERS_UPDATED);
            sleep(100);
            sendStates(BasicGameAction.SIT_DOWN, null, false);
        }

        whoPlays = 0;
        sendStates(BasicGameAction.ROOM_ROUND_STARTED, null, true);

        pause(3000);

        for (int i = 0; i < N_ROWS; i++) {
            for (int j = 0; j < N_COLUMNS; j++) {
                desk[i][j] = 0;
            }
        }
        sendStates(BasicGameAction.ROOM_STATE_CHANGED, null, true);

        while (checkGameOver() == null) {

            waitForPlay();

            desk[played.i][played.j] = currentValue();
            played = null;
            whoPlays = next(whoPlays);

            sendStates(BasicGameAction.ROOM_STATE_CHANGED, null, true);

        }

        whoPlays = -1;
        sendStates(BasicGameAction.ROOM_ROUND_FINISHED, null, true);

        pause(5000);

    }

    private void waitForPlay() {
        if (players[whoPlays].local) {
            int[][] copy = new int[desk.length][];
            for (int i = 0; i < desk.length; i++) {
                copy[i] = desk[i].clone();
            }
            played = new GomokuAI().getMove(copy, currentValue());
            return;
        }
        played = null;
        setWaitFlags(EV_PLAY, TimeUnit.SECONDS.toMillis(getMoveTime()));
        logAndSendToAll(BasicGameAction.ROOM_WAITING_FOR_MOVE, null, false, null);
        waitFor(EV_PLAY);
    }

    private int currentValue() {
        return whoPlays == 0 ? 1 : 2;
    }

    private int next(int i) {
        return (i + 1) % nPlayers;
    }

    private GomokuMove[] checkGameOver() {
        final int L = WIN_SEQUENCE_LENGTH;

        for (int i = 0; i < desk.length; i++) {
            for (int j = 0; j < desk[0].length; j++) {
                if (desk[i][j] == 0) {
                    continue;
                }
                for (int di = -1; di < 2; di++) {
                    M:
                    for (int dj = -1; dj < 2; dj++) {
                        if (di * di + dj * dj > 0) {
                            for (int len = 0; len < L; len++) {
                                int ii = i + len * di;
                                int jj = j + len * dj;
                                if (ii < 0 || jj < 0 || ii >= desk.length || jj >= desk[0].length) {
                                    continue M;
                                }
                                if (desk[i][j] != desk[ii][jj]) {
                                    continue M;
                                }
                            }
                            GomokuMove[] win = new GomokuMove[L];
                            for (int len = 0; len < L; len++) {
                                win[len] = new GomokuMove(i + len * di, j + len * dj);
                            }
                            return win;
                        }
                    }
                }
            }
        }
        return null;
    }

}
