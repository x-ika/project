package ika.games.base.controller;

import com.simplejcode.commons.misc.MultiStateObject;
import com.simplejcode.commons.misc.struct.DynamicStruct;
import com.simplejcode.commons.misc.util.*;
import com.simplejcode.commons.net.util.*;
import ika.games.base.*;
import ika.games.base.controller.action.*;
import ika.games.base.controller.dao.GameDAOException;

import java.security.*;
import java.util.*;

import static ika.games.base.controller.Constants.*;

/**
 * Basic class containing game logic.<p>
 * Each sublass should implement four methods<p>
 * main method which process user requests:<p>
 * {@link #updateLobby() updateLobby}<p>
 * used to update state of the current room into the database:<p>
 * {@link #handleAction(GameAction, Player, UserActionParam)}<p>
 * used to send to the user current rooom's state:<p>
 * {@link #writeState(Player, GameAction, Player, boolean)}<p>
 * <br><br>
 * <h4>high level API methods are making it easy to implement various game logics</h4><p>
 * {@link #isRoomThread()} determine whether the current thread is external or worker thread<p>
 * {@link #startWorker(Runnable, boolean)} start's worker thread<p>
 * {@link #loadRoomSettings()} loads the room setting from the database<p>
 * <br><br>
 * <h4>low level API methods are used to log and send to the users various messages</h4><p>
 * {@link #sendGeneratorHash(Player, String)} provable fair<p>
 * {@link #sendGeneratorState(Player, String, int[])} provable fair<p>
 * {@link #sendMessage(Player, Msg)} chat<p>
 * {@link #sendChats()} chat<p>
 * {@link #sendState(Player, GameAction, Player, boolean)} room state<p>
 * {@link #sendUserList(Player, java.util.Collection)} user list<p>
 */
public abstract class BasicRoom extends MultiStateObject implements Room {

    protected final PersistentObject settings;
    protected final Controller controller;
    protected final Map<Integer, Player> observers;
    protected final Map<Thread, Player> runningThreads;
    protected final Deque<Msg> chatHistory;
    protected final GameRandom random;
    protected int roundId;
    protected boolean active;

    protected BasicRoom(PersistentObject settings, Controller controller) throws NoSuchAlgorithmException {
        this.settings = settings;
        this.controller = controller;
        observers = new HashMap<>();
        runningThreads = new HashMap<>();
        chatHistory = new ArrayDeque<>();
        roundId = controller.get().getLastRoundId(controller.getGameId(), getId()) + 1;
        random = new GameRandom(SecureRandom.getInstance("SHA1PRNG"));
    }

    //-----------------------------------------------------------------------------------
    /*
    high level API
     */

    protected void loadRoomSettings() {
        controller.loadRoomSettings(settings);
    }

    protected int getAppSetting(String key) {
        return controller.getGame().getInt(key);
    }

    protected boolean isRoomThread() {
        return runningThreads.containsKey(Thread.currentThread());
    }

    protected void startWorker(Runnable runnable, boolean loop, Thread.UncaughtExceptionHandler eh) {
        Thread thread = new Thread() {
            public void run() {
                synchronized (BasicRoom.this) {
                    runningThreads.put(this, null);
                }
                try {
                    do {
                        runnable.run();
                    } while (loop && active);
                } finally {
                    synchronized (BasicRoom.this) {
                        runningThreads.remove(this);
                    }
                }
            }
        };
        thread.setUncaughtExceptionHandler(eh);
        thread.start();
    }

    protected void startWorker(Runnable runnable, boolean loop) {
        startWorker(runnable, loop, null);
    }

    protected Player createLocalPlayer(String name) {
        DynamicStruct struct = new DynamicStruct();
        struct.put(Constants.ID, 0);
        struct.put(Constants.NAME, name);
        return new Player(true, this, new User(struct));
    }

    protected Msg createSystemMessage(String id) {
        return new Msg(0, 0, "SYSTEM", 0, DateUtils.currentTime(), id);
    }

    protected void addMessage(Msg msg) {
        chatHistory.add(msg);
        while (chatHistory.size() > Math.max(getAppSetting(CHAT_HISTORY_BUFFER), 0)) {
            chatHistory.removeFirst();
        }
    }

    protected void chatMsg(Msg msg) {
        addMessage(msg);
        sendMessages(msg);
    }

    protected ProvablyFair createPF(String secret, int serverSeed) throws NoSuchAlgorithmException {
        ProvablyFair pf = new ProvablyFair();
        pf.secret = secret;
        pf.serverSeed = serverSeed;
        pf.initialState = serverSeed + ";" + secret;
        pf.hash = CryptoUtils.sha256(pf.initialState);
        return pf;
    }

    protected void applySeeds(ProvablyFair pf, int[] seeds, int n) throws NoSuchAlgorithmException {
        StringBuilder sb = new StringBuilder(11 * seeds.length);
        for (int seed : seeds) {
            sb.append(',').append(seed);
        }
        pf.seeds = sb.substring(1);
        random.setClientSeeds(seeds);
        pf.output = random.nextInt(n);
    }

    protected void kickAllUsers() {
        Collection<Player> players = new ArrayList<>(observers.values());
        for (Player player : players) {
            try {
                handleAction(BasicGameAction.LOG_OUT, player, new UserActionParam());
            } catch (Exception e) {
                // empty
            }
        }
    }

    protected void waitAfterWorkerFailure(Exception reason) {
        ThreadUtils.sleep(60000);
    }

    protected void pause(int millis) {
        synchronized (this) {
            try {
                wait(millis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //-----------------------------------------------------------------------------------
    /*
    low level API
     */

    protected void debug(String method, Object... params) {
        Object[] p = new Object[params.length + 1];
        System.arraycopy(params, 0, p, 1, params.length);
        p[0] = getId();
        controller.std.logAction(CustomLogger.Level.FINE, method, p);
    }

    protected long logAction(GameAction action, Player actor, int roundId, Long amount, String... logParams) {
        controller.std.logAction(action.getName(), settings.name, actor == null ? "ROOM" : actor.user.name, amount, Arrays.toString(logParams));
        for (int tryCount = 1; ; tryCount++) {
            try {
                return controller.logAction(action, actor == null ? 0 : actor.user.id, getId(), roundId, BasicResult.OK, logParams);
            } catch (GameDAOException e) {
                handleDBException(e, tryCount);
            }
        }
    }

    protected long logAction(GameAction action, Player actor, Long amount, String... logParams) {
        return logAction(action, actor, roundId, amount, logParams);
    }

    protected void updateSetting(String key, String value) {
        settings.put(key, value);
        for (int tryCount = 1; ; tryCount++) {
            try {
                controller.get().updateRoom(controller.getGameId(), getId(), key, value);
                return;
            } catch (GameDAOException e) {
                handleDBException(e, tryCount);
            }
        }
    }

    protected void updateUserSetting(Player player, String key, String value) {
        player.user.put(key, value);
        for (int tryCount = 1; ; tryCount++) {
            try {
                controller.get().updateUser(controller.getGameId(), player.user.id, key, value);
                return;
            } catch (GameDAOException e) {
                handleDBException(e, tryCount);
            }
        }
    }

    private void handleDBException(GameDAOException e, int tryCount) {
        handleDBException(e, tryCount, isRoomThread() ? getAppSetting(REPAIR_TRY_COUNT) : 1);
    }

    private void handleDBException(GameDAOException e, int tryCount, int limit) {
        if (tryCount == limit) {
            throw e;
        }
        controller.recreate(true);
    }

    //-----------------------------------------------------------------------------------
    /*
    API for sending various messages to the client
     */

    private boolean checkConnection(Player player) {
        if (player.connection == null) {
            if (!player.local) {
                controller.std.log("Can not send update to the player " + player.user.name);
            }
            return false;
        }
        return true;
    }

    protected void sendMessageTo(Player player, byte[] pkg) {
        if (checkConnection(player)) {
            controller.sendTo(player.connection, pkg);
        }
    }

    protected void sendMessageTo(Player player, byte[][] pkgs) {
        if (checkConnection(player)) {
            for (byte[] pack : pkgs) {
                controller.sendTo(player.connection, pack);
            }
        }
    }

    protected void sendToAll(byte[] pkg) {
        for (Player player : observers.values()) {
            sendMessageTo(player, pkg);
        }
    }

    protected void sendToAll(byte[][] pkg) {
        for (Player player : observers.values()) {
            sendMessageTo(player, pkg);
        }
    }


    protected interface StructWriter {

        boolean writeHeader();

        boolean writeNextStruct();

    }

    protected abstract class BasicStructWriter<T> implements StructWriter {

        private Collection<T> collection;
        private Iterator<T> iterator;

        protected BasicStructWriter(Collection<T> collection) {
            this.collection = collection;
            iterator = collection.iterator();
        }

        public boolean writeHeader() {
            builder.writeInt(collection.size());
            return iterator.hasNext();
        }

        public boolean writeNextStruct() {
            write(iterator.next());
            return iterator.hasNext();
        }

        public abstract void write(T struct);

    }

    protected List<byte[]> buffer = new ArrayList<>();
    protected MessageBuilder builder = new ByteMessageBuilder(1024, ',');

    protected void resetBuilder() {
        builder.clear();
        builder.setDelimiter(',');
    }

    protected void appendMessage() {
        if (builder.size() > 0) {
            buffer.add(builder.getMessage());
        }
        resetBuilder();
    }

    protected byte[][] writePaging(int headerCode, int pagingCode, int pageLimit, StructWriter writer) {
        buffer.clear();
        resetBuilder();
        builder.writeInt(headerCode);
        boolean hasNext = writer.writeHeader();
        appendMessage();
        while (hasNext) {
            if (builder.size() > pageLimit) {
                appendMessage();
            }
            if (builder.size() == 0) {
                builder.writeInt(pagingCode);
                builder.writeInt(buffer.size() - 1);
            }
            builder.setDelimiter(';');
            hasNext = writer.writeNextStruct();
            builder.setLast(',');
        }
        appendMessage();
        return buffer.toArray(new byte[0][]);
    }


    private void writeMsg(Msg msg) {
        builder.writeInt(msg.id);
        builder.writeInt(msg.type);
        builder.writeInt(msg.senderId);
        builder.writeHexString(msg.senderName);
        builder.writeHexString(msg.date);
        builder.writeHexString(msg.text);
    }

    protected void writeInts(int[] a, int n) {
        if (n == 0) {
            builder.writeString("");
        } else {
            builder.setDelimiter(';');
            for (int i = 0; i < n; i++) {
                builder.writeInt(a[i]);
            }
            builder.setLast(',');
            builder.setDelimiter(',');
        }
    }

    protected void writeStateHeader() {
        buffer.clear();
        resetBuilder();
        builder.writeInt(MessageTypes.MSG_TYPE_STATE_HEADER);
        builder.writeInt(settings.id);
        builder.writeBytes(settings.getHexName());
    }

    protected abstract byte[][] writeState(Player receiver, GameAction action, Player actor, boolean wholeState);

    protected byte[] writeMessage(Msg msg) {
        resetBuilder();
        builder.writeInt(MessageTypes.MSG_TYPE_CHAT_MSG);
        writeMsg(msg);
        return builder.getMessage();
    }

    protected byte[][] writeChat() {
        return writePaging(MessageTypes.MSG_TYPE_CHAT_HEADER, MessageTypes.MSG_TYPE_CHAT_PAGING, 900, new BasicStructWriter<>(chatHistory) {
            public void write(Msg msg) {
                writeMsg(msg);
            }
        });
    }

    protected byte[][] writeUserList(Collection<Player> userList) {
        return writePaging(MessageTypes.MSG_TYPE_USERS_HEADER, MessageTypes.MSG_TYPE_USERS_PAGING, 900, new BasicStructWriter<>(userList) {
            public void write(Player player) {
                builder.writeInt(player.user.id);
                builder.writeBytes(player.user.getHexName());
            }
        });
    }

    protected byte[] writeGeneratorHash(String hash) {
        resetBuilder();
        builder.writeInt(MessageTypes.MSG_TYPE_GEN_HASH);
        builder.writeString(hash);
        return builder.getMessage();
    }

    protected byte[][] writeGeneratorState(String initialState, int[] clientSeeds) {
        return writePaging(MessageTypes.MSG_TYPE_GEN_HEADER, MessageTypes.MSG_TYPE_GEN_PAGING, 900, new StructWriter() {
            int ind = 0;

            public boolean writeHeader() {
                builder.writeString(initialState);
                builder.writeInt(clientSeeds.length);
                return clientSeeds.length > 0;
            }

            public boolean writeNextStruct() {
                builder.writeInt(clientSeeds[ind++]);
                return ind < clientSeeds.length;
            }
        });
    }


    protected void sendState(Player receiver, GameAction action, Player actor, boolean wholeState) {
        sendMessageTo(receiver, writeState(receiver, action, actor, wholeState));
    }

    protected void sendMessage(Player receiver, Msg msg) {
        sendMessageTo(receiver, writeMessage(msg));
    }

    protected void sendChat(Player receiver) {
        sendMessageTo(receiver, writeChat());
    }

    protected void sendUserList(Player receiver, Collection<Player> userList) {
        sendMessageTo(receiver, writeUserList(userList));
    }

    protected void sendGeneratorHash(Player receiver, String hash) {
        sendMessageTo(receiver, writeGeneratorHash(hash));
    }

    protected void sendGeneratorState(Player receiver, String initialState, int[] seeds) {
        sendMessageTo(receiver, writeGeneratorState(initialState, seeds));
    }


    protected void sendStates(GameAction action, Player actor, boolean wholeState) {
        for (Player player : observers.values()) {
            sendState(player, action, actor, wholeState);
        }
    }

    protected void logAndSendToOne(GameAction action, Player actor, int roundId, boolean wholeState, Long amount, String... logParams) {
        logAction(action, actor, roundId, amount, logParams);
        sendState(actor, action, actor, wholeState);
    }

    protected void logAndSendToAll(GameAction action, Player actor, boolean wholeState, Long amount, String... logParams) {
        logAction(action, actor, roundId, amount, logParams);
        sendStates(action, actor, wholeState);
    }

    protected void sendMessages(Msg msg) {
        sendToAll(writeMessage(msg));
    }

    protected void sendChats() {
        sendToAll(writeChat());
    }

    protected void sendUserLists(Collection<Player> userList) {
        sendToAll(writeUserList(userList));
    }

    protected void sendGeneratorHashs(String state) {
        sendToAll(writeGeneratorHash(state));
    }

    protected void sendGeneratorStates(String initialState, int[] seeds) {
        sendToAll(writeGeneratorState(initialState, seeds));
    }

    //-----------------------------------------------------------------------------------
    /*
    Room implementation
     */

    public int getId() {
        return settings.id;
    }

    public synchronized int getRoundId(int userId) {
        return roundId;
    }

    public synchronized void updateLobby() {
    }

    public synchronized Player createPlayer(User user) {
        Player player = observers.get(user.id);
        return player != null ? player : new Player(this, user);
    }

    public synchronized boolean isWorking() {
        return !runningThreads.isEmpty();
    }

    public synchronized void startWorking() {
        active = true;
    }

    public synchronized void stopWorking(boolean wait) {
        if (isRoomThread()) {
            throw new RuntimeException("Fatal bug: It is not possible to stop room inside the working thread.");
        }
        if (!active) {
            return;
        }
        debug("_stopWorking", "Room is shutting down");
        active = false;
        while (wait && isWorking()) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                // impossible
            }
        }
    }

}
