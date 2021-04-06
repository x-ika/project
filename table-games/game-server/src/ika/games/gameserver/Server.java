package ika.games.gameserver;

import com.simplejcode.commons.misc.util.ThreadUtils;
import com.simplejcode.commons.net.sockets.*;
import com.simplejcode.commons.net.util.*;
import ika.games.base.BasicGameAction;
import ika.games.base.controller.Controller;

import java.io.*;
import java.net.*;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.*;

/**
 * Application startup class.<p>
 * The class starts working by loading app settings:<p>
 * - server port<p>
 * - path to folder where game .jar files ar located<p>
 * - list of games which should be loaded<p>
 * <p>
 */
@SuppressWarnings("unchecked")
public class Server extends AsynchronousConnectionManager {

    private static final int SOCKET_BUFFER_CAPACITY = 1 << 12;

    private static final class DynamicLoader extends URLClassLoader {
        public DynamicLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        public void addURL(URL url) {
            if (!Arrays.asList(getURLs()).contains(url)) {
                super.addURL(url);
            }
        }
    }

    private final Properties settings;
    private final Map<String, Controller> controllers;
    private final Controller[] mapped;
    private final Map<String, JarURLConnection> openedJars;

    private AsynchronousConnection adminConnection;

    public Server(Properties settings) throws Exception {
        super(Integer.parseInt(settings.getProperty("port")), 10, 60);
        this.settings = settings;
        controllers = new Hashtable<>();
        mapped = new Controller[99];
        openedJars = new Hashtable<>();
        for (String gameName : settings.getProperty("startup").split(",")) {
            loadController(gameName);
        }
    }


    private Class loadClass(String className, String... fileNames) throws Exception {
        DynamicLoader classLoader = new DynamicLoader(new URL[0], ClassLoader.getSystemClassLoader());

        for (String fileName : fileNames) {
            URL jarUrl = new URL("jar:file:" + settings.getProperty("path") + fileName + "!/");
            JarURLConnection connection = openedJars.get(fileName);
            if (connection != null) {
                connection.getJarFile().close();
            }
            connection = (JarURLConnection) jarUrl.openConnection();
            openedJars.put(fileName, connection);
            classLoader.addURL(jarUrl);
        }

        return classLoader.loadClass(className);
    }

    private synchronized void closeController(String gameName) {
        Controller controller = controllers.get(gameName);
        if (controller == null) {
            notifyAdmin("Controller for game = " + gameName + " does not exists");
            return;
        }
        controller.close();
        controllers.remove(gameName);
        mapped[controller.getGameId()] = null;
        System.gc();
        notifyAdmin("Controller " + controller + " closed");
    }

    private synchronized void loadController(String gameName) throws Exception {
        try {
            Properties game = new Properties();
            game.load(new FileInputStream(settings.getProperty("path") + "resources/" + gameName + ".properties"));
            Class clazz = loadClass(game.getProperty("game_controller"), game.getProperty("game_classpath").split(";"));
            Controller controller = (Controller) clazz.getMethod("createInstance").invoke(null);
            controllers.put(gameName, controller);
            mapped[controller.getGameId()] = controller;
            notifyAdmin("Controller " + controller + " started");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Controller retrieve(int gameId) throws Exception {
        if (gameId < 0 || mapped.length < gameId || mapped[gameId] == null) {
            throw new RuntimeException("Game by gameId = " + gameId + " is not registered");
        }
        return mapped[gameId];
    }


    private void process(String cmd) throws Exception {
        if (cmd.equals("usage")) {
            notifyAdmin(String.format("%32s - %s", "t", "number of active threads"));
            notifyAdmin(String.format("%32s - %s", "trace", "dump stack traces"));
            notifyAdmin(String.format("%32s - %s", "mem", "memory status"));
            notifyAdmin(String.format("%32s - %s", "stop $game_id", "stop game"));
            notifyAdmin(String.format("%32s - %s", "start $game_id", "start game"));
            notifyAdmin(String.format("%32s - %s", "restart $game_id", "restart game"));
            return;
        }
        if (cmd.equals("t")) {
            notifyAdmin("Number of active threads=" + Thread.activeCount());
            return;
        }
        if (cmd.equals("trace")) {
            for (String s : ThreadUtils.dumpAllThreadsStack().split("\n")) {
                notifyAdmin(s);
            }
            return;
        }
        if (cmd.equals("mem")) {
            notifyAdmin(getMemoryUsage());
            return;
        }
        if (cmd.startsWith("stop")) {
            String gameName = cmd.split(" ")[1];
            closeController(gameName);
            return;
        }
        if (cmd.startsWith("start")) {
            String gameName = cmd.split(" ")[1];
            loadController(gameName);
            return;
        }
        if (cmd.startsWith("restart")) {
            String gameName = cmd.split(" ")[1];
            closeController(gameName);
            loadController(gameName);
            return;
        }
        notifyAdmin("Unknown command " + cmd);
    }

    private void notifyAdmin(String message) {
        if (adminConnection != null) {
            adminConnection.write(message.replace('@', '#').getBytes());
        }
    }

    private String getMemoryUsage() {
        long total = Runtime.getRuntime().totalMemory() >> 20;
        long free = Runtime.getRuntime().freeMemory() >> 20;
        return free + "MB free from " + total;
    }


    protected AsynchronousConnection create(AsynchronousSocketChannel socket) throws IOException {
        return new AsynchronousConnection(socket, SOCKET_BUFFER_CAPACITY, SOCKET_BUFFER_CAPACITY, '2', '1', 0, '@');
    }

    public void messageReceived(AsynchronousConnection source, byte[] message) {
        try {
            final MessageParser parser = new ByteMessageParser(',');
            parser.setData(message);
            int action = parser.nextInt();
            if (action == 1) {
                if (parser.nextString().equals("ika") && parser.nextString().equals("paracalo")) {
                    adminConnection = source;
                    notifyAdmin("LOGIN_OK");
                }
                return;
            }
            if (action == 2) {
                if (adminConnection == source) {
                    ThreadUtils.executeInNewThread(() -> {
                        try {
                            process(parser.nextString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
                return;
            }
            if (action == BasicGameAction.GUEST_REQUEST.getId()) {
                retrieve(parser.nextInt()).handleGuestRequest(source, message);
            }
            if (action == BasicGameAction.LOG_IN.getId()) {
                Controller controller = retrieve(parser.nextInt());
                removeConnection(source);
                controller.addConnection(source);
                controller.messageReceived(source, message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------------

    public static void main(String[] args) throws Exception {
        Properties settings = new Properties();
        settings.load(new FileInputStream("resources/server.properties"));
        new Server(settings).start();
    }

}
