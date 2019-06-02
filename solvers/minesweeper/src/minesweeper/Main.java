package minesweeper;

import minesweeper.simulator.*;

import java.util.Arrays;
import java.util.Calendar;

import minesweeper.strategy.SimplePlayer;
import minesweeper.strategy.GameSolver;

public class Main {

    private static int[] stats = new int[500];

    public static void main(String[] args) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(2011, 2, 20);
//        if (System.currentTimeMillis() > calendar.getTime().getTime()) {
//            JOptionPane.showMessageDialog(null, "Trial period has expired!", "Warning", JOptionPane.WARNING_MESSAGE);
//            return;
//        }

        if (args.length == 0) {
            test(0, 0, 0, 1);
        } else {
            test(parse(args[0]), parse(args[1]), parse(args[2]), parse(args[3]));
        }

    }

    private static void test(int n, int m, int k, int t) {
        long startTime = System.nanoTime();
        int won = 0;
        for (int i = 0; i < t; i++) {
//            if (simulateGame(new TestSimulator(n, m, k))) {
//                won++;
//            }
            if (simulateGame(new LiveSimulator())) {
                won++;
            }
        }
        System.out.println(Arrays.toString(stats));
        System.out.printf("%d from %d\n", won, t);
        System.out.println("Time: " + (System.nanoTime() - startTime) / 1e9);
    }

    private static boolean simulateGame(Simulator simulator) {
        SimplePlayer player = new SimplePlayer(new GameSolver());
        simulator.init();

//        MainFrame frame = new MainFrame("Minesweeper");
//        Table table = new Table(40);
//        frame.setContentPane(table.createContentPane());
//        table.init(visiblePart);
//        table.addListener(this);
//        frame.setVisible(true);

        while (simulator.getOpenedCells() < simulator.getEmptyCells()) {

            Cell turn = player.makeTurn(simulator.getDesk(), simulator.getBombs());
//            updateAndWate(table);
            simulator.openCell(turn.i, turn.j);
//            updateAndWate(table);
            if (simulator.check(turn.i, turn.j)) {
                break;
            }

        }

//        frame.processExit();
        simulator.finish();
        stats[simulator.getOpenedCells()]++;
        return simulator.getOpenedCells() == simulator.getEmptyCells();
    }

    private static int parse(String s) {
        return Integer.parseInt(s);
    }

}
