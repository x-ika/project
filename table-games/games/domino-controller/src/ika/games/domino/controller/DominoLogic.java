package ika.games.domino.controller;

import ika.games.base.*;
import ika.games.base.controller.*;
import ika.games.base.controller.action.*;
import ika.games.domino.base.*;
import ika.games.domino.controller.action.PlayAction;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DominoLogic extends BasicRoom {

    private static final int EV_USERS_UPDATED = 1;
    private static final int EV_PLAY = 2;

    private int nPlayers, here, whoPlays, score[];
    private boolean first, open;
    private int[][] stones;
    private DominoMove played;
    private Player[] players;
    private DominoTree dominoTree;

    private List<Integer> allStones;

    public DominoLogic(PersistentObject settings, Controller controller) throws NoSuchAlgorithmException {
        super(settings, controller);
        players = new Player[nPlayers = 4];
        score = new int[2];
        dominoTree = new DominoTree();
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
                    result = userPlay(actor, (PlayAction) param);
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

    private Result userPlay(Player player, PlayAction done) {
        if (whoPlays == -1 || players[whoPlays] != player || played != null) {
            return BasicResult.UNEXPECTED_MOVE;
        }
        try {
            for (DominoMove move : getPossibleTurns(whoPlays)) {
                if (equals(move, done)) {
                    played = move;
                    notify(EV_PLAY);
                    return BasicResult.OK;
                }
            }
        } catch (Exception e) {
            return BasicResult.INVALID_MOVE;
        }
        return BasicResult.INVALID_MOVE;
    }

    private int getPlayerSit(Player player) {
        for (int i = 0; i < nPlayers; i++) {
            if (players[i] == player) {
                return i;
            }
        }
        return -1;
    }

    private boolean equals(DominoMove move, PlayAction done) {
        boolean equals = true;
        equals &= done.node == (move.basic == null ? -1 : move.basic.getStone());
        equals &= done.newNode == move.newNode.getStone();
        equals &= done.side1 == move.side1;
        equals &= done.side2 == move.side2;
        return equals;
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
            if (stones == null) {
                builder.writeInt(0);
                continue;
            }
            builder.writeInt(stones[i].length);
            for (int x : stones[i]) {
                builder.writeInt(open || i == sit ? x : -1);
            }
        }
        builder.writeInt(score[0]);
        builder.writeInt(score[1]);
        builder.writeInt(first ? 1 : 0);

        Map<Node, Integer> map = new HashMap<>();
        int id = 0;
        for (Node node : dominoTree.getNodes()) {
            map.put(node, id++);
        }
        builder.writeInt(dominoTree.getNodes().size());
        for (Node node : dominoTree.getNodes()) {
            builder.writeInt(node.getStone());
            builder.writeInt(node.getSides());
        }
        for (Node node : dominoTree.getNodes()) {
            for (Node next : node.getNext()) {
                builder.writeInt(next == null ? -1 : map.get(next));
            }
        }
        builder.writeInt(dominoTree.getRoot() == null ? -1 : map.get(dominoTree.getRoot()));

        appendMessage();
        return buffer.toArray(new byte[0][]);
    }

    private synchronized void run() {

        first = true;
        whoPlays = -1;
        score[0] = score[1] = 0;

        while (here < nPlayers) {
            waitFor(EV_USERS_UPDATED);
            sleep(100);
            sendStates(BasicGameAction.SIT_DOWN, null, false);
        }

        sendStates(BasicGameAction.ROOM_ROUND_STARTED, null, true);

        pause(3000);

        whoPlays = 0;
        boolean riba = false;

        WHOLE:
        while (Math.max(score[0], score[1]) < 355 || riba) {

            startHand();

            HAND:
            while (true) {

                for (int counter = 0; getPossibleTurns(whoPlays).isEmpty(); ) {
                    whoPlays = next(whoPlays);
                    if (++counter == 4) {
                        endHand(riba = true);
                        break HAND;
                    }
                }

                waitForPlay();

                makeTurn(played);

                if (dominoTree.getNodes().size() == 1 &&
                        getPossibleTurns(next(whoPlays)).isEmpty() &&
                        getPossibleTurns(prev(whoPlays)).isEmpty())
                {
                    pause(3000);
                    continue WHOLE;
                }

                first = false;

                int sum = dominoTree.getScore();
                if (sum % 5 == 0) {
                    score[whoPlays % 2] += sum;
                }

                sendStates(BasicGameAction.MAKE_MOVE, null, true);

                if (stones[whoPlays].length == 0) {
                    endHand(riba = false);
                    break;
                }

                whoPlays = next(whoPlays);

            }

        }

    }

    private void startHand() {
        dominoTree.clear();
        open = false;
        M:
        while (true) {

            allStones = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j <= i; j++) {
                    allStones.add(i << 3 | j);
                }
            }
            Collections.shuffle(allStones, random);

            int perPlayer = 7;
            stones = new int[nPlayers][perPlayer];
            for (int i = 0; i < nPlayers; i++) {
                for (int j = 0; j < perPlayer; j++) {
                    stones[i][j] = allStones.remove(0);
                }
            }

            for (int[] stone : stones) {
                int[] c = new int[7];
                int d = 0;
                for (int x : stone) {
                    c[DominoTopology.getTop(x)]++;
                    if (!DominoTopology.isDouble(x)) {
                        c[DominoTopology.getBottom(x)]++;
                    }
                    if (DominoTopology.isDouble(x)) {
                        d++;
                    }
                }
                for (int v : c) {
                    if (v > 5) {
                        continue M;
                    }
                }
                if (d > 4) {
                    continue M;
                }
            }

            break;

        }

        sendStates(BasicGameAction.ROOM_STATE_CHANGED, null, true);
    }

    private void endHand(boolean riba) {
        open = true;
        if (riba) {
            whoPlays = prev(whoPlays);
            int closerSide = whoPlays % 2;
            if (getSum(closerSide) <= getSum(1 - closerSide)) {
                score[closerSide] += (getSum(1 - closerSide) + 4) / 5 * 5;
            } else {
                score[1 - closerSide] += (getSum(closerSide) + 4) / 5 * 5;
            }
        } else {
            int winnerSide = whoPlays % 2;
            int looserSide = 1 - winnerSide;
            score[winnerSide] += (getSum(looserSide) + 4) / 5 * 5;
        }
        sendStates(BasicGameAction.ROOM_ROUND_FINISHED, null, true);
        pause(5000);
    }

    private int getSum(int side) {
        int sum = 0;
        for (int stone : stones[side]) {
            sum += DominoTopology.getSum(stone);
        }
        for (int stone : stones[side + 2]) {
            sum += DominoTopology.getSum(stone);
        }
        return sum;
    }

    protected void waitForPlay() {
        played = null;
        setWaitFlags(EV_PLAY, TimeUnit.SECONDS.toMillis(getMoveTime()));
        logAndSendToAll(BasicGameAction.ROOM_WAITING_FOR_MOVE, null, false, null);
        waitFor(EV_PLAY);
    }

    private void makeTurn(DominoMove t) {

        if (t == null) {
            int[] a = stones[whoPlays];
            a = Arrays.copyOf(a, a.length + 1);
            a[a.length - 1] = buyDomino();
            stones[whoPlays] = a;
        } else {
            dominoTree.makeTurn(t);
            int[] a = stones[whoPlays];
            for (int i = 0; i < a.length; i++) {
                if (t.newNode.getStone() == a[i]) {
                    System.arraycopy(a, i + 1, a, i, a.length - i - 1);
                    stones[whoPlays] = Arrays.copyOf(a, a.length - 1);
                }
            }
        }

    }

    private int buyDomino() {
        return allStones.remove(0);
    }

    private int next(int i) {
        return (i + 1) % nPlayers;
    }

    private int prev(int i) {
        return (i + nPlayers - 1) % nPlayers;
    }

    private List<DominoMove> getPossibleTurns(int whoPlays) {
        return dominoTree.getPossibleMoves(stones[whoPlays], allStones.size(), first);
    }

}
