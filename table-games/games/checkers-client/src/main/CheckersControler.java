package main;

import player.*;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.lang.reflect.*;

import gui.CheckersTableView;

public class CheckersControler {

    public static int N = 8;
    private int[][] desk, previousDesk;
    private int winner;
    private boolean waitingForEnd;
    private Thread turns;
    private Player first, second;

    private CheckersTableView checkersTableView;

    public static class Song extends Thread {
        public static AudioClip ac;

        Song(String s) throws IOException {
            ac = Applet.newAudioClip(new URL("file:" + s));
        }

        public static void stopPlay() {
            ac.stop();
        }

        public static void play() {
            ac.loop();
        }

        public void run() {
        }
    }

    private CheckersControler() throws IOException {
        new Song("spacemusic.au").start();
    }

    public void dispose() {
        checkersTableView.dispose();
    }

    public int playNewGame() {
        return playNewGame(N, first, second);
    }

    public int playNewGame(int nC) {
        return playNewGame(nC, first, second);
    }

    public int playNewGame(Player a, Player b) {
        return playNewGame(N, a, b);
    }

    public int playNewGame(int nC, Player a, Player b) {
        if (turns != null) {
            first.gameOver();
            second.gameOver();
            Thread tmp = turns;
            turns = null;
            try {
                tmp.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        first = a;
        second = b;
        if (checkersTableView == null) {
            checkersTableView = new CheckersTableView(this);
        }
        N = nC;
        desk = new int[N][N];
        previousDesk = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                desk[i][j] = 0;
                if ((i + j) % 2 != 0 && Math.abs(N - 2 * i - 1) > 1) {
                    desk[i][j] = 2 * i > N ? 1 : 2;
                }
            }
        }
        checkersTableView.updateView(desk, N);
        return play();
    }

    private synchronized int play() {
        checkersTableView.clearListeners();
        final Player[] players = {first, second};
        for (Player player : players) {
            if (player instanceof TableListener) {
                checkersTableView.addListener((TableListener) player);
            }
        }
        turns = new Thread() {
            public void run() {
                turnsThread(players);
            }
        };
        turns.start();
        waitingForEnd = true;
        while (waitingForEnd) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return winner;
    }

    private synchronized void turnsThread(Player[] players) {
        winner = -1;
        for (int c = 0, ind = 0; c < 200 && turns != null; c++, ind = 1 - ind) {
            BasicPlayer arbiter = new BasicPlayer() {
                public Turn makeTurne(int[][] d, int value) {
                    return null;
                }
            };
            arbiter.copyDesk(desk);
            List<Turn> all = arbiter.countTurnes(ind + 1);
            if (all.isEmpty()) {
                winner = 1 - ind;
                break;
            }
            Turn t = players[ind].makeTurne(cloneIt(desk), ind + 1);
            if (t == null) {
                System.out.println(players[ind].getClass());
                winner = 1 - ind;
                break;
            }
            boolean good = false;
            for (Turn turn : all) {
                if (BasicPlayer.equals(t, turn)) {
                    t = turn;
                    good = true;
                    break;
                }
            }
            if (!good) {
                System.out.println("WTF");
                //System.out.println(arbiter.countTurnes(ind + 1));
                winner = 1 - ind;
                break;
            }
            checkersTableView.updateDesk(t);
            checkersTableView.repaint();
            pause(1000);
        }
        checkersTableView.gameOver(winner == 0 ? "FIRST PLAYER WIN" : "SECOND PLAYER WIN");
        waitingForEnd = false;
        notify();
    }

    private static int[][] cloneIt(int[][] d) {
        int[][] clone = new int[d.length][];
        for (int i = 0; i < d.length; i++) {
            clone[i] = d[i].clone();
        }
        return clone;
    }

    public void back() {
        throw new RuntimeException("Not Implemented");
//        for (int i = 0; i < N; i++) {
//            System.arraycopy(previousDesk[i], 0, desk[i], 0, N);
//        }
//        mcheckers.repaint();
    }

    public void pause(long millis) {
        try {
            wait(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------------

    public static void main(String[] args) throws Exception {
        play(Computer.class, User.class);

//        play(player.students.deme.DemesPlayer.class, player.students.guga.GuggerPlayer.class);

//        List<Class<? extends Player>> players = getPlayers();
//        championship(players);
    }

    private static void championship(List<Class<? extends Player>> players) throws Exception {

        String parch =
                "player.students.alpenidze.main.BestMove\n" +
                        "player.students.chakvetadze.MyPlayer\n" +
                        "player.students.kervala.possibleTurns\n" +
                        "player.students.lmagla.MyPlayer" +
                        "player.students.natroshvili.implementation" +
                        "player.students.ujmajuridze.ComputerPlayer" +
                        "player.students.xidasheli.allWays" +
                        "player.students.xucishvili.test";

        Map<String, Integer> scores = new TreeMap<>();
        for (int i = 0; i < players.size(); i++) {
            if (parch.contains(players.get(i).getName())) {
                players.remove(i--);
            }
        }
        for (Class<? extends Player> player : players) {
            scores.put(player.getName(), 0);
        }
        for (int i = 0; i < players.size(); i++) {
            for (int j = i + 1; j < players.size(); j++) {
                Class<? extends Player> a = players.get(j);
                Class<? extends Player> b = players.get(i);

                if (a.getName().contains("laki")) {
                    play(User.class, a);
                } else {
                    continue;
                }

                if (b.getName().contains("barb") && a.getName().contains("kik")) {
                    System.out.println(a);
                    System.out.println(b);
                } else {
                    continue;
                }

                int winner = play(a, b);
                switch (winner) {
                    case 0:
                        scores.put(a.getName(), scores.get(a.getName()) + 1);
                        break;
                    case 1:
                        scores.put(b.getName(), scores.get(b.getName()) + 1);
                        break;
                }
            }
        }

        for (String s : scores.keySet()) {
            System.out.printf("%-50s %d\n", s, scores.get(s));
        }
    }

    private static int play(Class<? extends Player> a, Class<? extends Player> b) throws Exception {
        CheckersControler instance = new CheckersControler();
        int winner = instance.playNewGame(8, a.getConstructor().newInstance(), b.getConstructor().newInstance());
        System.out.printf("%-50s %-50s %d\n", a.getName(), b.getName(), winner);
//        instance.dispose();
        return winner;
    }

    private static List<Class<? extends Player>> getPlayers() {
        List<Class> list = new ArrayList<>();
        List<Class<? extends Player>> res = new ArrayList<>();
        go(new File("D:\\ika\\projects\\Games\\checkers\\src\\player"), "player", list);
        for (Class clazz : list) {
            if (!Modifier.isAbstract(clazz.getModifiers()) && clazz.getInterfaces().length > 0 && clazz.getInterfaces()[0] == Player.class) {
                res.add(clazz);
            }
        }
        return res;
    }

    private static void go(File file, String s, List<Class> list) {
        s += ".";
        for (File f : file.listFiles()) {
            String name = f.getName();
            if (f.isDirectory()) {
                go(f, s + name, list);
            } else {
                try {
                    list.add(Class.forName(s + name.substring(0, name.length() - 5)));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
