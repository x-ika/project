package player;

import main.*;

import java.util.*;

public class User extends BasicPlayer implements TableListener {

    private int value, depth;
    private boolean ready, gameOver, waiting;
    private Turn turn;
    private Collection<Turn> all;

    public synchronized void gameOver() {
        gameOver = true;
        notify();
    }

    public synchronized Turn makeTurne(int[][] d, int value) {
        this.value = value;
        desk = d;
        copyDesk(d);
        all = countTurnes(value);
        ready = true;
        while (++depth > 0) {
            try {
                waiting = true;
                while (waiting && !gameOver) {
                    wait(1000);
                }
                if (gameOver) {
                    return null;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ready = false;
        Turn ret = turn;
        turn = null;
        return ret;
    }

    public synchronized void mouseDragged(int row1, int col1, int row2, int col2) {
        if (!ready) {
            return;
        }
        if ((row1 - row2 + 2) % 2 == 1) {
            turn = new Turn(new Checker(row1, col1, value), new Checker(row2, col2, value), null);
        } else {
            if (turn == null) {
                turn = new Turn(new Checker(row1, col1, value), null, new ArrayList<>());
            }
            for (int k = 1; k < Math.abs(row1 - row2); k++) {
                int r = row1 + Integer.signum(row2 - row1) * k;
                int c = col1 + Integer.signum(col2 - col1) * k;
                if (desk[r][c] != 0) {
                    ((List<Checker>) turn.killed).add(new Checker(r, c, 3 - value));
                    break;
                }
            }
            turn.last = new Checker(row2, col2, row2 == 0 && value < 3 ? value + 3 : value);
        }
        for (Turn t : all) {
            if (equals(t, turn)) {
                depth = -1;
            }
        }
        waiting = false;
        notify();

    }

}
