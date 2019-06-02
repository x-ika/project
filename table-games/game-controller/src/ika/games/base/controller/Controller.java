package ika.games.base.controller;

import static ika.games.base.controller.Constants.*;

import com.simplejcode.commons.misc._pattern.pool.*;
import com.simplejcode.commons.misc.DynamicStruct;
import com.simplejcode.commons.net.sockets.*;
import com.simplejcode.commons.net.util.*;
import ika.games.base.*;
import ika.games.base.controller.action.*;
import ika.games.base.controller.dao.*;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Basic class for various game controllers<p>
 * Controller holds:<p>
 * - all client connections<p>
 * - list of rooms called lobby<p>
 * - two pools of db connections<p>
 * - instance of the logger class<p>
 * <br><br>
 * Each socket connection created by Server is passed to controller and is held
 * until client will be disconnected.<p>
 * Main metohd of the class is {@link #messageReceived messageReceived} where all the client requests are processed.<p>
 * It first {@link #readAction determines the requested client action} then {@link #readParam reads the corresponding parameter}<p>
 * and next passes the action and parameter to the room associated requester connection.<p>
 * Please note that {@link Controller} holds one connection per user login so each connection uniquely<p>
 * determines the (user, room) pair<p>
 */
public abstract class Controller extends AsynchronousConnectionManager {

    protected abstract class PoolingHelper implements IPoolHandler<GameDAO> {
        public void destroy(GameDAO dao) throws Exception {
            dao.close();
        }
    }

    public CustomLogger std;

    protected final String propertiesFile;
    protected final DynamicStruct game;
    protected final ObjectPool<GameDAO> pooler;

    protected Thread appUpdateThread;
    protected long nextCleanupTime;

    protected boolean allowGuests;
    protected int guestId;
    protected Map<Integer, Room> lobby;

    public Controller(String propertiesFile) throws Exception {
        super(0, 5, 60);
        this.propertiesFile = propertiesFile;
        game = new DynamicStruct();
        game.put(REPAIR_TIMEOUT, 1000);
        loadProperties(propertiesFile);
        lobby = new HashMap<>();
        pooler = new ObjectPool<>(getForPooler());

        updateApp();
        appUpdateThread = new Thread() {
            public void run() {
                while (appUpdateThread != null) {
                    try {
                        sleep(TimeUnit.SECONDS.toMillis(game.getInt(APP_UPDATE_INTERVAL)));
                        updateApp();
                    } catch (Exception e) {
                        handleException(e);
                    }
                }
            }
        };
        appUpdateThread.start();
        start();
    }

    protected Properties loadProperties(String propertiesFile) throws IOException {
        try (InputStream is = new FileInputStream(propertiesFile)) {
            Properties properties = new Properties();
            properties.load(is);
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                game.tryput((String) entry.getKey(), entry.getValue());
            }
            return properties;
        }
    }

    protected AsynchronousConnection create(AsynchronousSocketChannel socket) throws IOException {
        throw new UnsupportedOperationException("not implemented");
    }

    protected GameDAO createGameDAO() throws Exception {
        return new GameDAOImpl(getGameConfigURL());
    }

    protected abstract Room createRoom(PersistentObject struct) throws Exception;

    protected Room tryCreateRoom(PersistentObject struct) {
        try {
            return createRoom(struct);
        } catch (Exception e) {
            if (std != null) {
                std.log(e);
            }
            return null;
        }
    }

    /**
     * Updates application settings and lobby.
     * @throws Exception if any problem raises during the update process
     */
    protected void updateApp() throws Exception {

        // general settings
        recreate(false);
        game.load(get().getGame(getGameId()));

        // logger
        if (std == null) {
            std = new CustomLogger(0, true);
        }
        std.setOutputBase(game.getString(LOG_FILE));
        std.setLevelValue(game.getInt(LOG_LEVEL));

        // rooms
        updateLobby();

        // cleanup
        long time = System.currentTimeMillis();
        if (nextCleanupTime < time) {
            System.gc();
            System.runFinalization();
            nextCleanupTime = time + 60 * 1000;
        }

    }

    protected void updateLobby() {
        Collection<DynamicStruct> rooms = get().getRooms(getGameId(), RoomType.USUAL.type);
        Map<Integer, Room> newLobby = new HashMap<>();
        for (DynamicStruct struct : rooms) {
            int id = struct.getInt(ID);
            Room room = lobby.get(id);
            if (room == null) {
                PersistentObject newRoom = new PersistentObject(struct);
                loadRoomSettings(newRoom);
                room = tryCreateRoom(newRoom);
                if (room == null) {
                    continue;
                }
                room.startWorking();
            }
            newLobby.put(id, room);
        }
        for (Room room : lobby.values()) {
            if (!newLobby.containsKey(room.getId()) && room.isWorking()) {
                room.stopWorking(false);
                newLobby.put(room.getId(), room);
            }
        }
        lobby = newLobby;
        for (Room room : lobby.values()) {
            room.updateLobby();
        }
    }

    //-----------------------------------------------------------------------------------

    public DynamicStruct getGame() {
        return game;
    }

    public int getGameId() {
        return game.getInt("game_id");
    }

    public int getPoolSize() {
        return game.getInt("pool_size");
    }

    public String getGameConfigURL() {
        return game.getString("game_config");
    }

    public void close() {
        Thread thread = appUpdateThread;
        appUpdateThread = null;
        try {
            thread.join();
        } catch (InterruptedException e) {
            std.log(e);
        }
        Map<Integer, Room> map = new HashMap<>(lobby);
        lobby.clear();
        for (Room room : map.values()) {
            room.stopWorking(false);
        }
        while (true) {
            boolean allClosed = true;
            for (Room room : map.values()) {
                if (room.isWorking()) {
                    allClosed = false;
                    break;
                }
            }
            if (allClosed) {
                break;
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                std.log(e);
            }
        }
        stop();
        for (AsynchronousConnection connection : connections) {
            connection.closeConnection();
        }
        pooler.destroyAll();

        std.log("-------------------------- " + getClass().getName() + " closed.");
    }

    public GameDAO get() {
        return pooler.get();
    }

    public IPoolHandler<GameDAO> getForPooler() {
        return new PoolingHelper() {
            public GameDAO create() throws Exception {
                try {
                    return createGameDAO();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }
        };
    }

    /**
     * Destroys old database connections and creates them again.
     * It is necessary when connection to the db server are lost.
     * @param force recreate connections even if they are healthy
     */
    public void recreate(boolean force) {
        int poolSize = getPoolSize();
        if (force || pooler.getSize() != poolSize) {
            pooler.createAll(poolSize, TimeUnit.SECONDS.toMillis(game.getInt(REPAIR_TIMEOUT)), 1000);
        }
    }

    public long logAction(GameAction action, int actor, int room, int round, Result result, String... logParams) {
        return get().logAction(getGameId(), room, actor, round, action, result, logParams);
    }

    public User createUser(int userId) {
        DynamicStruct struct;
        if (userId == 0) {
            struct = new DynamicStruct();
            synchronized (this) {
                struct.put(ID, --guestId);
                struct.put(NAME, "guest_" + -guestId);
                guestId %= (int) 1e9;
            }
        } else {
            throw new UnsupportedOperationException("cant create user");
        }
        if (struct == null) {
            return null;
        }
        User user = new User(struct);
        loadUserSettings(user);
        return user;
    }

    public void loadRoomSettings(PersistentObject persistentObject) {
        persistentObject.load(get().getRoom(getGameId(), persistentObject.id));
    }

    public void loadUserSettings(User user) {
        user.load(get().getUser(getGameId(), user.id));
    }

    //-----------------------------------------------------------------------------------

    protected void sendStatusMessage(AsynchronousConnection connection, GameAction action, Result result) {
        MessageBuilder builder = new ByteMessageBuilder(16, ',');
        builder.writeInt(MessageTypes.MSG_TYPE_STATUS);
        builder.writeInt(action.getId());
        builder.writeInt(result.getId());
        sendTo(connection, builder.getMessage());
    }

    protected void sendLobby(AsynchronousConnection connection) {
        MessageBuilder builder = new ByteMessageBuilder(1024, ',');
        builder.writeInt(MessageTypes.MSG_TYPE_LOBBY);
        Collection<DynamicStruct> rooms = get().getRooms(getGameId(), RoomType.USUAL.type);
        for (DynamicStruct struct : rooms) {
            PersistentObject room = new PersistentObject(struct);
            loadRoomSettings(room);
        }
        connection.write(builder.getMessage());
    }

    //-----------------------------------------------------------------------------------

    public void handleGuestRequest(AsynchronousConnection source, byte[] message) {
        ByteMessageParser parser = new ByteMessageParser(message, ',');
        parser.nextInt(); // actionId
        parser.nextInt(); // gameId
        sendLobby(source);
    }

    protected Login readLogin(AsynchronousConnection source, MessageParser parser) {
        parser.nextInt(); // gameId
        int roomId = parser.nextInt();
        int userId = parser.nextInt();
        String ses = parser.nextString();
        return new Login(userId, roomId, ses, source.getRemoteAddress());
    }

    protected Result login(AsynchronousConnection source, int roomId, int userId, String ses) throws Exception {
        if (!lobby.containsKey(roomId)) {
            return BasicResult.INVALID_ROOM;
        }
        boolean guest = allowGuests && userId == 0;
        if (!guest && !ses.equals("x")) {
            return BasicResult.INVALID_SESSION;
        }
        User user = createUser(userId);
        if (user == null) {
            return BasicResult.INVALID_USER;
        }
        Player player = lobby.get(roomId).createPlayer(user);
        if (player.connection != null) {
            removeConnection(player.connection);
        }
        player.connection = source;
        source.setHost(player);
        return BasicResult.OK;
    }

    protected void logout(Player player, AsynchronousConnection source) throws Exception {
        source.setHost(null);
        removeConnection(source);
        source.closeConnection();
        for (Map.Entry<String, Object> entry : player.user.getMap().entrySet()) {
            get().updateUser(getGameId(), player.user.id, entry.getKey(), entry.getValue().toString());
        }
    }

    //-----------------------------------------------------------------------------------

    private void logMessage(AsynchronousConnection socketConnection, byte[] message, boolean incoming) {
        Player player = (Player) socketConnection.getHost();
        String direction = incoming ? "<<<<" : ">>>>";
        std.log((player != null ? player.user.name : socketConnection.getRemoteAddress()) + direction + new String(message));
    }

    public void sendTo(AsynchronousConnection socketConnection, byte[] message) {
        logMessage(socketConnection, message, false);
        super.sendTo(socketConnection, message);
    }

    public void messageReceived(AsynchronousConnection source, byte[] message) {
        logMessage(source, message, true);
        Result result = BasicResult.OK;
        GameAction action = BasicGameAction.UNKNOWN;
        Player player = (Player) source.getHost();
        UserActionParam param = new UserActionParam();
        try {
            MessageParser parser = new ByteMessageParser(message, ',');
            action = readAction(parser);
            if (action == BasicGameAction.UNKNOWN) {
                result = BasicResult.UNKNOWN_ACTION;
            } else  if (action != BasicGameAction.LOG_IN && player == null) {
                result = BasicResult.UNAUTHORIZED;
            } else {
                param = readParam(source, parser, player, action);
                if (param == null) {
                    param = new UserActionParam();
                    result = BasicResult.UNHANDLED_ACTION;
                } else {
                    parser.verifyAndClose();
                }
                if (result == BasicResult.OK && action == BasicGameAction.LOG_IN) {
                    Login login = (Login) param;
                    result = login(source, login.roomId, login.userId, login.session);
                    player = (Player) source.getHost();
                }
                param.player = player;
                if (result == BasicResult.OK) {
                    result = player.owner.handleAction(action, player, param);
                }
                if (result == BasicResult.OK && action == BasicGameAction.LOG_OUT) {
                    logout(player, source);
                }
            }
        } catch (Exception e) {
            result = handleException(e);
        }
        finalizeHandlingProcess(action, result, source, param);
    }

    protected GameAction readAction(MessageParser parser) {
        return GameAction.choose(parser.nextInt(), BasicGameAction.values());
    }

    protected UserActionParam readParam(AsynchronousConnection source, MessageParser parser, Player player, GameAction action) {
        if (action instanceof BasicGameAction) {
            switch ((BasicGameAction) action) {
                case LOG_IN:
                    return readLogin(source, parser);
                case LOG_OUT:
                    return new Logout(source.getRemoteAddress());
                case SIT_DOWN:
                    return new SitDown(parser.nextInt());
                case STAND_UP:
                    return new StandUp();
            }
        }
        return null;
    }

    public void disconnected(AsynchronousConnection source) {
        Result result;
        UserActionParam param = new Logout(source.getRemoteAddress());
        try {
            Player player = param.player = (Player) source.getHost();
            if (player == null) {
                result = BasicResult.UNAUTHORIZED;
            } else {
                result = player.owner.handleAction(BasicGameAction.LOG_OUT, player, param);
                logout(player, source);
            }
        } catch (Exception e) {
            result = handleException(e);
        }
        finalizeHandlingProcess(BasicGameAction.LOG_OUT, result, source, param);
        super.disconnected(source);
    }

    private Result handleException(Exception e) {
        std.log(e, CustomLogger.Level.SEVERE);
        if (e instanceof GameDAOException) {
            recreate(true);
        }
        return e instanceof ParsingException ? BasicResult.PARSER_ERROR : e instanceof GameDAOException ? BasicResult.DB_ERROR : BasicResult.SYSTEM_ERROR;
    }

    private void finalizeHandlingProcess(GameAction action, Result result, AsynchronousConnection source, UserActionParam param) {
        std.logAction(action.getName(), result.getName(), Arrays.toString(param.params()));
        try {
            logAction(action, param.user(), param.room(), param.round(), result, param.params());
        } catch (Exception e) {
            // doesn't matter
        }
        sendStatusMessage(source, action, result);
    }

}
