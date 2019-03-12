package ika.games.joker.server.logic;

import static ika.games.base.controller.CardComplect.*;
import static ika.games.joker.server.JokerNamespace.*;

import com.simplejcode.commons.misc.ArrayUtils;
import ika.games.base.*;
import ika.games.base.controller.*;
import ika.games.base.controller.action.*;

import java.security.*;
import java.util.*;

public class JokerLogic extends BasicRoom implements Runnable {

    private static final class Turn {

        public final int playType, card, call, suit;

        public Turn(int playType, int card, int call, int suit) {
            this.playType = playType;
            this.card = card;
            this.call = call;
            this.suit = suit;
        }

        public boolean equals(PlayAction t) {
            int c = t.card == null ? -1 : toCard(t.card);
            return t.playType == playType && c == card && t.call == call && t.suit == suit;
        }
    }

    private final class HistoryRow {
        int nCards;
        int[] call, take, scores, cstatus, cards[];

        public HistoryRow(int nCards) {
            this.nCards = nCards;
            call = new int[nPlayers];
            take = new int[nPlayers];
            scores = new int[nPlayers];
            cstatus = new int[nPlayers];
            cards = new int[nPlayers][nCards];
            Arrays.fill(call, -1);
            Arrays.fill(take, -1);
            for (int i = 0; i < nPlayers; i++) {
                Arrays.fill(cards[i], -1);
            }
        }
    }

    private static final int EV_SIT = 1;
    private static final int EV_PLAY = 2;

    private static final int CST_GOOD = 1;
    private static final int CST_BAD = 2;
    private static final int CST_PENALTY = 3;
    private static final int CST_STRIKE = 4;

    protected JokerSettings jokerSettings;

    protected Player[] players;
    protected Player[] originalPlayers;
    protected int nPlayers = 4, botSet;
    protected int[] inhand, sums[];
    protected int stage, roundInStage, bonusSet, trump, taker, whoPlayes;
    protected HistoryRow currentRow, history[][];
    protected Turn[] done;
    protected Turn played;
    protected List<Turn> possibleTurns;

    public JokerLogic(PersistentObject struct, Controller controller) throws NoSuchAlgorithmException {
        super(struct, controller);
        players = new Player[nPlayers];
        inhand = new int[nPlayers];
        done = new Turn[nPlayers];
        possibleTurns = new ArrayList<>();
    }

    public void setGameSettings(JokerSettings settings) {
        jokerSettings = settings;
        initJokerTypeDependantVariables(jokerSettings.jokerType);
    }

    public JokerSettings getSettings() {
        return jokerSettings;
    }

    //-----------------------------------------------------------------------------------

    protected byte[][] writeState(Player receiver, GameAction action, Player actor, boolean wholeState) {
        writeStateHeader();
        builder.writeInt(receiver.user.id);
        builder.writeBytes(receiver.user.getHexName());
        builder.writeInt(roundId);
        builder.writeInt(jokerSettings.stakeFromEachPlayer);
        builder.writeInt(jokerSettings.jokerType.getType());
        builder.writeInt(jokerSettings.penaltyType.getType());
        builder.writeInt(jokerSettings.penalty8);
        builder.writeInt(jokerSettings.penalty9);
        builder.writeInt(nPlayers);
        builder.writeInt(stage);
        builder.writeInt(roundInStage);
        builder.writeInt(currentRow == null ? -1 : currentRow.nCards);
        if (trump == -6) {
            builder.writeString("");
        } else if (trump < 0) {
            builder.writeInt(-trump - 1);
        } else {
            builder.writeString(CardComplect.toString(trump));
        }
        int sit = getPlayerSit(receiver);
        builder.writeInt(whoPlayes == -1 ? -1 : (whoPlayes + nPlayers - sit) % nPlayers);
        builder.writeInt(isWaitingFor(EV_PLAY) ? timeRest(EV_PLAY) / 1000 : -1);
        builder.writeInt(jokerSettings.playerTurnDelay);
        builder.setDelimiter(';');
        for (int i = 0; i < nPlayers; i++) {
            int j = (i + sit) % nPlayers;
            if (players[j] == null) {
                builder.writeString(";;;;");
                continue;
            }
            builder.writeInt(currentRow == null ? -1 : currentRow.call[j]);
            builder.writeInt(inhand[j]);
            builder.writeInt(currentRow == null ? -1 : currentRow.take[j]);
            builder.writeAscii(writeCard(done[j]));
        }
        builder.setLast(',');
        builder.setDelimiter(',');
        builder.writeInt(sit);
        builder.writeInt(botSet >> sit & 1);
        char[] c = new char[2 * inhand[sit]];
        for (int i = 0; i < inhand[sit]; i++) {
            c[2 * i] = suitChar(currentRow.cards[sit][i]);
            c[2 * i + 1] = rankChar(currentRow.cards[sit][i]);
        }
        builder.writeAscii(c);
        int yourTurn = whoPlayes == sit ? possibleTurns.get(0).playType : 0;
        builder.writeInt(yourTurn);
        if (yourTurn == 0 || yourTurn == 3) {
            builder.writeString("");
        } else if (yourTurn == 1) {
            c = new char[possibleTurns.size()];
            for (int i = 0; i < possibleTurns.size(); i++) {
                c[i] = (char) ('0' + possibleTurns.get(i).call);
            }
            builder.writeAscii(c);
        } else if (yourTurn == 2) {
            StringBuilder sb = new StringBuilder();
            for (Turn turn : possibleTurns) {
                if (!isJJoker(turn.card)) {
                    sb.append(writeCard(turn));
                }
            }
            for (int x : new int[]{getFirstJoker(), getSecondJoker()}) {
                int jc = 0;
                for (Turn turn : possibleTurns) {
                    if (turn.card == x) {
                        jc++;
                    }
                }
                if (jc > 0) {
                    sb.append(suitChar(x)).append(rankChar(x)).append(jc);
                }
            }
            builder.writeString(sb.toString());
        }
        appendMessage();
        return buffer.toArray(new byte[0][]);
    }

    private char[] writeCard(Turn turn) {
        if (turn == null) {
            return new char[0];
        }
        if (!isJJoker(turn.card)) {
            return new char[]{suitChar(turn.card), rankChar(turn.card)};
        } else {
            return new char[]{suitChar(turn.card), rankChar(turn.card), (char) ('0' + turn.call), (char) (turn.suit == -1 ? '?' : '0' + turn.suit)};
        }
    }

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
                    for (int i = 0; i < nPlayers; i++) {
                        if (players[i] == null) {
                            players[i] = actor;
                            if (stage != -1) {
                                sendMessageTo(players[i], writeScoreList(false));
                            }
                            break;
                        }
                    }
                    notify(EV_SIT);
                    break;
                case LOG_OUT:
                    for (int i = 0; i < nPlayers; i++) {
                        if (players[i] == actor) {
                            players[i] = null;
                            break;
                        }
                    }
                    observers.remove(actor.user.id);
                    break;
            }
        }
        if (action instanceof JokerAction) {
            switch ((JokerAction) action) {
                case USER_CLAIM:
                    result = userPlay(actor, (PlayAction) param);
                    break;
                case USER_PLAY_CARD:
                    PlayAction pAction = (PlayAction) param;
                    controller.std.log( "CARD IS: " + pAction.card );
                    result = userPlay(actor, (PlayAction) param);
                    break;
                case USER_SUIT:
                    result = userPlay(actor, (PlayAction) param);
                    break;
                case USER_GET_LIST:
                    sendMessageTo(actor, writeScoreList(((GetList) param).whole == 1));
                    break;
                case USER_KICK_BOT:
                    botSet &= ~(1 << getPlayerSit(actor));
                    break;
                default:
                    result = BasicResult.UNHANDLED_ACTION;
            }
        }
        if (result == BasicResult.OK) {
            if (action != JokerAction.USER_GET_LIST) {
                sendStates(action, actor, false);
            }
        }
        return result;
    }

    private Result userPlay(Player player, PlayAction done) {
        if (whoPlayes == -1 || players[whoPlayes] != player) {
            return JokerResult.UNEXPECTED_TURN;
        }
        try {
            for (Turn turn : possibleTurns) {
                if (turn.equals(done)) {
                    played = turn;
                    whoPlayes = -1;
                    notify(EV_PLAY);
                    return BasicResult.OK;
                }
            }
        } catch (Exception e) {
            return JokerResult.INVALID_TURN;
        }
        return JokerResult.INVALID_TURN;
    }

    protected void sendScoreLists() {
        sendToAll(writeScoreList(false));
    }

    protected byte[] writeScoreList(boolean whole) {
        resetBuilder();
        builder.writeInt(MSG_TYPE_JOKER_SCOREBOARD);
        if (roundInStage == -1) {
            return builder.getMessage();
        }

        if (!whole) {
            if (roundInStage > 0) {
                writeHistoryRow(stage, roundInStage - 1);
            }
            writeHistoryRow(stage, roundInStage);
            builder.setLast(';');
            for (int k = 0; k < nPlayers; k++) {
                builder.writeInt(sums[history.length][k]);
            }
        } else {
            for (int i = 0; i < history.length; i++) {
                for (int j = 0; j < history[i].length; j++) {
                    writeHistoryRow(i, j);
                }
                builder.setLast(';');
                for (int k = 0; k < nPlayers; k++) {
                    builder.writeInt(sums[i][k]);
                }
                builder.setLast(';');
            }
        }

        return builder.getMessage();
    }

    private void writeHistoryRow(int i, int j) {
        HistoryRow h = history[i][j];
        builder.writeInt(h.nCards);
        if (i == stage && j == roundInStage) {
            builder.removeLastDelimiter();
            builder.writeChar('*');
        }
        for (int k = 0; k < nPlayers; k++) {
            builder.writeInt(h.call[k]);
            builder.writeInt(h.scores[k]);
            if (h.cstatus[k] == CST_STRIKE) {
                builder.removeLastDelimiter();
                builder.writeChar('-');
            }
        }
    }

    protected int getPlayerSit(Player player) {
        for (int i = 0; i < nPlayers; i++) {
            if (players[i] == player) {
                return i;
            }
        }
        return -1;
    }

    //-----------------------------------------------------------------------------------
    // game process

    public synchronized void startWorking() {
        super.startWorking();
        startWorker(this, true);
    }

    public synchronized void run() {
        try {
            nextRound();
        } catch (Exception e) {
            processFailure(e);
        }
    }

    private void processFailure(Exception e) {
        synchronized (controller) {
            controller.std.log(e);
            controller.std.log("#######################################################");
            controller.std.log("SYSTEM FAILURE " + settings.id + " " + settings.name);
            controller.std.log("#######################################################");
        }
        waitAfterWorkerFailure(e);
    }

    private void nextRound() throws Exception {

        stage = roundInStage = whoPlayes = -1;
        Arrays.fill(inhand, 0);
        Arrays.fill(done, null);
        trump = -6;

        loadRoomSettings();
        logAndSendToAll(JokerAction.ROOM_START_ROUND, null, false, null);

        M:
        while (true) {
            waitFor(EV_SIT);
            for (int i = 0; i < nPlayers; i++) {
                if (players[i] == null) {
                    continue M;
                }
            }
            break;
        }
        originalPlayers = players.clone();
        debug("_start_joker_party");

        int lastPlayer;

        int[] complect = random.shuffleArray(getJokerComplect());
        for (int i = 0; ; i++) {
            if (isAce(complect[i])) {
                lastPlayer = i % nPlayers;
                for (int j = 0; j < nPlayers; j++) {
                    resetBuilder();
                    builder.writeInt(MSG_TYPE_JOKER_CARDS);
                    builder.writeInt(j == 0 ? 0 : nPlayers - j);
                    for (int k = 0; k <= i; k++) {
                        builder.writeString(suitChar(complect[k]) + "" + rankChar(complect[k]));
                    }
                    sendMessageTo(players[j], builder.getMessage());
                }
                sleep(2000 + 700 * (i + 1));
                break;
            }
        }

        debug("_last_player_is ", lastPlayer);

        for (stage = 0; stage < history.length; stage++) {
            bonusSet = (1 << nPlayers) - 1;
            for (roundInStage = 0; roundInStage < history[stage].length; roundInStage++) {
                currentRow = history[stage][roundInStage];
                play((roundInStage + lastPlayer) % nPlayers);
            }
            roundInStage--;
            processBonusPlayers();
        }

        //endGame();

        logAndSendToAll(JokerAction.ROOM_END_ROUND, null, false, null);

//        parent.killMe(this);

    }

    private void play(int diller) {

        Arrays.fill(currentRow.take, 0);
        Arrays.fill(inhand, 0);
        Arrays.fill(done, null);
        trump = -6;

        sendScoreLists();

        int[] complect = random.shuffleArray(getJokerComplect());

        // distribute cards
        int firstPlayer = (diller + 1) % nPlayers;
        int z = 0;
        for (int i = 0; i < currentRow.nCards; i++) {

            if (currentRow.nCards == 9 && i == 3) {
                defineSuits();
                waitForPlay(firstPlayer, TYPE_SUIT, jokerSettings.playerTurnDelay);
                trump = -played.suit - 1;
                logAndSendToAll(JokerAction.ROOM_SUIT, players[firstPlayer], false, null);
            }

            for (int j = 0; j < nPlayers; j++) {
                currentRow.cards[(firstPlayer + j) % nPlayers][i] = complect[z++];
                inhand[(firstPlayer + j) % nPlayers]++;
            }

        }
        if (currentRow.nCards < 9) {
            trump = complect[z];
        }

        for (int i = 0; i < nPlayers; i++) {
            int[] a = currentRow.cards[i];
            for (int j = 0; j < a.length; j++) {
                for (int k = 0; k < j; k++) {
                    if (cardScore(a[j]) > cardScore(a[k])) {
                        ArrayUtils.swap(a, j, k);
                    }
                }
            }
        }

        logAction(JokerAction.ROOM_SET_TRUMP, null, null);
        logAndSendToAll(JokerAction.ROOM_DISTRIBUTE, players[firstPlayer], false, null);

        // claims
        for (int j = 0; j < nPlayers; j++) {
            int sit = (firstPlayer + j) % nPlayers;
            defineClaims();
            waitForPlay(sit, TYPE_CLAIM, jokerSettings.playerTurnDelay);
            currentRow.call[sit] = played.call;
            logAndSendToAll(JokerAction.ROOM_CLAIM, players[sit], false, null);
        }
        sendScoreLists();

        // play
        int trumpSuit = trump < 0 ? -trump - 1 : isJJoker(trump) ? -1 : suit(trump);
        for (int i = 0; i < currentRow.nCards; i++) {
            taker = -1;
            Arrays.fill(done, null);
            for (int j = 0; j < nPlayers; j++) {
                int sit = (firstPlayer + j) % nPlayers;
                defineTurns(trumpSuit, done[firstPlayer], currentRow.cards[sit], inhand[sit]);
                waitForPlay(sit, TYPE_PLAY, jokerSettings.playerTurnDelay);
                done[sit] = played;
                for (int t = 0; t < inhand[sit]; t++) {
                    if (currentRow.cards[sit][t] == played.card) {
                        System.arraycopy(currentRow.cards[sit], t + 1, currentRow.cards[sit], t, --inhand[sit] - t);
                        break;
                    }
                }
                if (taker == -1 || isGreater(trumpSuit, done[taker], done[sit])) {
                    taker = sit;
                }
                logAndSendToAll(JokerAction.ROOM_PLAY, players[sit], false, null);
            }
            currentRow.take[taker]++;
            firstPlayer = taker;
            logAndSendToAll(JokerAction.ROOM_TAKE, players[taker], false, null);
            sleep(2000);
        }

        // scores
        for (int i = 0; i < nPlayers; i++) {
            currentRow.scores[i] = score(currentRow.call[i], currentRow.take[i]);
            currentRow.cstatus[i] = status(currentRow.call[i], currentRow.take[i]);
            if (currentRow.cstatus[i] != CST_GOOD) {
                bonusSet &= ~(1 << i);
            }
            addScore(stage, i, currentRow.scores[i]);
        }
        sendScoreLists();

    }

    private void endGame(){
        PlayerScore[] pScores = new PlayerScore[4];
        for (int k = 0; k < nPlayers; k++) {
            pScores[k] = new PlayerScore( originalPlayers[k].user.getHexName(), sums[history.length][k] );
        }
    }

    private void processBonusPlayers() {
        for (int i = 0; i < nPlayers; i++) {
            if ((bonusSet & 1 << i) != 0) {
                int ind = findBestRound(stage, i, true);
                HistoryRow best = history[stage][ind];
                addScore(stage, i, best.scores[i]);
                history[stage][history[stage].length - 1].scores[i] += best.scores[i];
            } else if (Integer.bitCount(bonusSet) == 1) {
                int ind = findBestRound(stage, i, false);
                HistoryRow best = history[stage][ind];
                addScore(stage, i, -best.scores[i]);
                best.cstatus[i] = CST_STRIKE;
            }
        }
        sendScoreLists();
    }

    private void addScore(int i, int j, int s) {
        sums[i][j] += s;
        sums[history.length][j] += s;
    }

    protected int score(int call, int take) {
        int nCards = currentRow.nCards;
        return call == take ? call == nCards ? 100 * nCards : 50 * (call + 1) : take == 0 ? penalty() : 10 * take;
    }

    protected int penalty() {
        return jokerSettings.penaltyType == JokerPenaltyType.SPEC ? -100 * currentRow.nCards :
                currentRow.nCards == 9 ? jokerSettings.penalty9 : jokerSettings.penalty8;
    }

    protected int status(int call, int take) {
        return call == take ? CST_GOOD : take == 0 ? CST_PENALTY : CST_BAD;
    }

    protected int cardScore(int c) {
        int t = trump < 0 ? -trump - 1 : suit(trump);
        int d = (suit(c) - t + 4) % 4;
        return isJJoker(c) ? 1000 : 100 * (d == 0 ? 3 : d == 2 ? 2 : d == 1 ? 1 : 0) + jrank(c);
    }

    protected int findBestRound(int stage, int ind, boolean includePass) {
        int best = 0;
        for (int i = 1; i < history[stage].length; i++) {
            HistoryRow bst = history[stage][best];
            HistoryRow cur = history[stage][i];
            if (bst.scores[ind] < cur.scores[ind] && (includePass || cur.cstatus[ind] != CST_GOOD || cur.scores[ind] != 50)) {
                best = i;
            }
        }
        return best;
    }

    //-----------------------------------------------------------------------------------

    protected void waitForPlay(int sit, int type, long delay) {
        played = null;
        whoPlayes = sit;
        if ((botSet & 1 << sit) == 0) {
            setWaitFlags(EV_PLAY, delay);
            logAndSendToAll(JokerAction.ROOM_GET_BETS, null, false, null);
            waitFor(EV_PLAY, delay);
        }
        if (played == null) {
            botSet |= 1 << sit;
            sleep(1000);
            if (type == TYPE_PLAY) {
                played = getRobotTurn();
            } else {
                played = random.pickRandom(possibleTurns);
            }
        }
        whoPlayes = -1;
        possibleTurns.clear();
    }

    protected void defineSuits() {
        possibleTurns.clear();
        for (int i = 0; i <= 4; i++) {
            possibleTurns.add(new Turn(TYPE_SUIT, -1, -1, i));
        }
    }

    protected void defineClaims() {
        possibleTurns.clear();
        int t = 0, s = 0;
        for (int x : currentRow.call) {
            if (x != -1) {
                t++;
                s += x;
            }
        }
        for (int i = 0; i <= currentRow.nCards; i++) {
            if (t != 3 || s + i != currentRow.nCards) {
                possibleTurns.add(new Turn(TYPE_CLAIM, -1, i, -1));
            }
        }

    }

    protected void defineTurns(int trumpSuit, Turn first, int[] c, int n) {
        possibleTurns.clear();
        int needSuit = first == null ? -1 : isJJoker(first.card) ? first.suit : suit(first.card);
        int maxNeed = -1;
        if (first != null && isJJoker(first.card) && first.call > 0) {
            for (int i = 0; i < n; i++) {
                if (!isJJoker(c[i]) && suit(c[i]) == first.suit) {
                    maxNeed = Math.max(maxNeed, jrank(c[i]));
                }
            }
        }
        for (int i = 0; i < n; i++) {
            if (!isJJoker(c[i]) && jrank(c[i]) >= maxNeed && (first == null || suit(c[i]) == needSuit)) {
                possibleTurns.add(new Turn(TYPE_PLAY, c[i], -1, -1));
            }
        }
        if (possibleTurns.isEmpty()) {
            for (int i = 0; i < n; i++) {
                if (!isJJoker(c[i]) && suit(c[i]) == trumpSuit) {
                    possibleTurns.add(new Turn(TYPE_PLAY, c[i], -1, -1));
                }
            }
        }
        if (possibleTurns.isEmpty()) {
            for (int i = 0; i < n; i++) {
                if (!isJJoker(c[i])) {
                    possibleTurns.add(new Turn(TYPE_PLAY, c[i], -1, -1));
                }
            }
        }
        for (int i = 0; i < n; i++) {
            if (isJJoker(c[i])) {
                if (first == null) {
                    for (int suit = 0; suit < 4; suit++) {
                        possibleTurns.add(new Turn(TYPE_PLAY, c[i], 0, suit));
                        possibleTurns.add(new Turn(TYPE_PLAY, c[i], 1, suit));
                    }
                } else {
                    possibleTurns.add(new Turn(TYPE_PLAY, c[i], 0, -1));
                    possibleTurns.add(new Turn(TYPE_PLAY, c[i], 1, -1));
                }
            }
        }
    }

    protected Turn getRobotTurn() {
        boolean firstCard = true;
        for (Turn turn : done) {
            firstCard &= turn == null;
        }
        if (!firstCard) {
            for (Turn turn : possibleTurns) {
                if (turn.call == 1) {
                    return turn;
                }
            }
            return random.pickRandom(possibleTurns);
        }
        List<Turn> nonJokerTurns = new ArrayList<>();
        for (Turn turn : possibleTurns) {
            if (turn.call == -1) {
                nonJokerTurns.add(turn);
            }
        }
        if (nonJokerTurns.isEmpty()) {
            int suit = random.nextInt(4);
            for (Turn turn : possibleTurns) {
                if (turn.call == 1 && turn.suit == suit) {
                    return turn;
                }
            }
        }
        return random.pickRandom(possibleTurns);
    }

    protected boolean isGreater(int trumpSuit, Turn prev, Turn cur) {
        if (isJJoker(cur.card)) {
            return cur.call > 0;
        }
        if (isJJoker(prev.card)) {
            return prev.suit != -1 &&
                    (prev.call == 0 ? suit(cur.card) == prev.suit || suit(cur.card) == trumpSuit : prev.suit != trumpSuit && suit(cur.card) == trumpSuit);
        }
        return suit(cur.card) == suit(prev.card) ? jrank(cur.card) > jrank(prev.card) : suit(cur.card) == trumpSuit;
    }

    protected void initJokerTypeDependantVariables(JokerType type) {
        int nStages = type == JokerType.USUAL || type == JokerType.NINES_9999 ? 4 :
                type == JokerType.NINES_999 ? 3 : type == JokerType.NINES_99 ? 2 : 1;
        sums = new int[nStages + 1][nPlayers];
        history = new HistoryRow[nStages][];
        for (int i = 0; i < nStages; i++) {
            boolean eightsStage = type == JokerType.USUAL && i % 2 == 0;
            int roundsInStage = eightsStage ? 8 : 4;
            history[i] = new HistoryRow[roundsInStage];
            for (int j = 0; j < roundsInStage; j++) {
                int nCards = eightsStage ? i == 0 ? j + 1 : 8 - j : 9;
                history[i][j] = new HistoryRow(nCards);
            }
        }
    }

}
