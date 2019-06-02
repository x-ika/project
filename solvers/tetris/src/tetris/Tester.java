package tetris;

import com.simplejcode.commons.gui.Console;
import tetris.logic.*;

public class Tester implements PortListener {

    private static boolean vis;
    private static int tdelay = 0, mdelay = 0;
    private static Console console;

    private StaticPort port;
    private Player player;

    public Tester(StaticPort port, Player player) {
        this.port = port;
        this.player = player;
    }

    public static double test(long seed, Class<Player> o) throws Exception {
        Tester tester = new Tester(new StaticPort(), o.getConstructor().newInstance());
        tester.port.setListener(tester);
        tester.port.setParams(seed, vis, tdelay, mdelay);
        tester.port.start();
        synchronized (tester) {
            tester.wait();
        }
        return tester.port.getScore();
    }

    public synchronized void eventOcuured(PortEvent event) {

        BitRectangle board = event.board;
        if (event.type == PortEvent.TYPE_MAKE_TURN) {
            Turn turn;
            try {
                turn = player.play(board, event.pile);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            //console.writeLine(turn.commands);
            port.apply(turn);
            return;
        }
        if (event.type == PortEvent.TYPE_GAME_OVER) {
//            console.writeLine("Your score is " + port.getScore());
//            System.out.printf("%d %d %5d\n", board.getWidth(), board.getHeight(), port.getScore());
            notify();
        }

    }

    public static void main(String[] args) throws Exception {
        vis = args[0].equals("t");
        console = Console.createInstance();
        console.setLocation(1200, 100);
        Class[] classes = {
//                SimplePlayer.class,
                tetris.students.dabe.MyPlayer.class,
//                tetris.students.adoli.SandroPlayer.class,
//                tetris.students.dato.MyPlayer.class,
                tetris.students.eormo.EvgoSolution.class,
//                tetris.students.gqoch.GkochPlayer.class,
//                tetris.students.lasha.LashasPlayer.class,
//                tetris.students.vmask.VatoPlayer.class,
        };

//        MMTester.test(Tester.class.getMethod("test", long.class, Class.class), "scores", MMTester.ABSOLUTE_SCORING, args, 1, classes);
    }

}
