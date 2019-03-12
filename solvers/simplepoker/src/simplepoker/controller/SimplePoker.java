package simplepoker.controller;

import simplepoker.player.*;

import java.util.*;
import java.io.File;

@SuppressWarnings({"StatementWithEmptyBody"})
public class SimplePoker {

    public static void main(String[] args) throws Exception {

        int runMode = args.length > 0 ? Integer.parseInt(args[0]) : 0;

        Class<Player>[] group = getAllPlayers();

        Class<Player>[] studentPlayers = getAllPlayers();

        Map<String, Integer> map = new HashMap<>();
        for (Class player : studentPlayers) {
            map.put(getName(player), 0);
        }
        for (Class player : group) {
            map.put(getName(player), 0);
        }

        for (int t = 0; t < studentPlayers.length; t++) {

            long startTime = System.nanoTime();

            for (Class<Player> player : group) {

                int scale = t == 2 ? 1 : 1;
                for (int iter = 0; iter < 100 / scale; iter++) {
                    SimplePokerController controller = new SimplePokerController();
                    Player[] players = {
                            player.getConstructor().newInstance(),
                            studentPlayers[t].getConstructor().newInstance(),
                    };
                    String[] playerNames = {getName(player), getName(studentPlayers[t])};
                    int winner = controller.playNewGame(runMode, players, playerNames);
                    String s = getName(players[winner].getClass());
                    map.put(s, map.getOrDefault(s, 0) + scale);
                }

            }

            System.out.println(getName(studentPlayers[t]));
            System.out.println("Time: " + (System.nanoTime() - startTime) / 1e9);
            System.out.println();

        }

        printWinners(map);

    }

    private static Class[] getAllPlayers() throws Exception {
        Class[] myPlayers = getMyPlayers();
        Class[] students = loadStudents();
        Class[] ret = new Class[myPlayers.length + students.length];
        System.arraycopy(myPlayers, 0, ret, 0, myPlayers.length);
        System.arraycopy(students, 0, ret, myPlayers.length, students.length);
        return ret;
    }

    private static Class[] getMyPlayers() throws Exception {
        return new Class[]{
                Caller.class,
                PredictablePlayer.class,
                SimplePlayer.class,
                SimpleAdapter.class,
                SmarterThanSimplePlayer.class,
        };
    }

    private static Class[] loadStudents() throws Exception {
        Class[] studentPlayers = new Class[17];
        int i = 0;

        for (File f : new File("src\\simplepoker\\player\\students").listFiles()) {
            for (File g : f.listFiles()) {
                String className = f.getName() + "." + g.getName().substring(0, g.getName().length() - 5);
                Class<?> clazz = Class.forName("simplepoker.player.students." + className);
                if (Player.class.isAssignableFrom(clazz)) {
                    studentPlayers[i++] = clazz;
                }
            }
        }
        return studentPlayers;
    }

    private static void printWinners(Map<String, Integer> map) {
        System.out.println("---------------------------------------------------");
        while (!map.isEmpty()) {
            String best = null;
            for (String s : map.keySet()) {
                if (best == null || map.get(best) < map.get(s)) {
                    best = s;
                }
            }
            System.out.println(String.format("%25s %3d", best, map.get(best)));
            map.remove(best);
        }
    }

    private static String getName(Class player) {
        String name = player.getName();
        name = name.substring(0, name.lastIndexOf('.'));
        name = name.substring(name.lastIndexOf('.') + 1);
        return name.equals("player") ? player.getSimpleName() + "*" : name;
    }

}
