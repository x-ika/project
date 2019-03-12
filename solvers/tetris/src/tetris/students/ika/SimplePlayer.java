package tetris.students.ika;

import static tetris.logic.TetrisUtils.*;
import tetris.logic.*;

import java.util.*;

/**
 * Class uses bfs to find all valid locations for the new pile,
 * then tries to put it to all these locations and uses score method
 * to evaluate board position and chooses the location which gives best
 * possible score.
 * Hense the player is static
 * More detailed specification of the algorithm used:
 *         // 1. try 4 rotations an find best one
 *         // 2. rotate the pile
 *         // 3. find the best location for the rotated pile
 *         // 3.1. find the reachable locations
 *         // 3.2. try all locations for each one
 *         // 3.2.1 check if location is valid (pile is on the ground)
 *         // 3.2.2 if so, try tu put it and calculate board penalty
 *         // 3.3 choose the location which gives lowest penalty
 *         // 4. format result

 */
@SuppressWarnings({"StatementWithEmptyBody"})
public class SimplePlayer implements Player {

    private BitRectangle board;
    private int dcorner;
    private int[] prev;
    private Command[] cmds;

    public Turn play(BitRectangle board, Pile pile) {
        if (pile == null || pile.filledArea() < 4) {
            return new Turn();
        }
        this.board = board;
        prev = new int[board.area()];
        cmds = new Command[board.area()];
        int rots = 0, score = Integer.MAX_VALUE;

        for (int i = 0; i < 4; i++) {
            Pile cur = pile;
            for (int j = 0; j < i; j++) {
                cur = cur.rotate();
            }
            int x = tryit(cur);
            if (score > x) {
                score = x;
                rots = i;
            }
        }

        Turn turn = new Turn();
        if (rots != 0) {
            for (int i = 0; i < rots; i++) {
                pile = pile.rotate();
            }
        }

        tryit(pile);

        while (prev[dcorner] != -2) {
            turn.commands.add(cmds[dcorner]);
            dcorner = prev[dcorner]; 
        }
        Collections.reverse(turn.commands);
        for (int i = 0; i < rots; i++) {
            turn.commands.add(0, Command.ROTATION);
        }

        return turn;
    }

    private int tryit(Pile pile) {
        int[] bf = board.getBitsetPrepresentation();
        int[] pf = pile.getBitsetPrepresentation();

        bfs(pile, pf, bf);

        int res = Integer.MAX_VALUE;
        for (int r = 0; r <= pile.row; r++) {
            for (int c = 0; c < board.getWidth(); c++) {
                
                if (prev[r * board.getWidth() + c] != -1) {
                    if (!TetrisUtils.checkContact(bf, pf, r, c)) {
                        continue;
                    }
                    xor(bf, pf, r, c);
                    int cur = score(board.getWidth(), bf.clone()) + 100 * r + c;
                    if (res > cur) {
                        res = cur;
                        dcorner = r * board.getWidth() + c;
                    }
                    xor(bf, pf, r, c);
                }
            }
        }

        return res;
    }

    private int score(int w, int[] t) {
        int s = 0;
        for (int i = 0; i < t.length; i++) {
            if (t[i] == (1 << w) - 1) {
                System.arraycopy(t, i + 1, t, i, t.length - i - 1);
                i--;
            }
        }
        int top = top(t);
        s += 100 * top;
        for (int i = 0; i < top; i++) {
            int max = 0;
            for (int j = i + 1; j <= top; j++) {
                int level = t[j];
                max = Math.max(max, 4 * Integer.bitCount(level & ~t[i]));
                if (j - i > 2) {
                    level |= t[j] << 1;
                    level |= t[j] >> 1;
                    level &= (1 << w) - 1;
                }
                max = Math.max(max, 3 * Integer.bitCount(level & ~t[i]));
            }
            s += 100 * max;
        }
        return s;
    }

    private void bfs(Pile pile, int[] pf, int[] bf) {

        int w = board.getWidth();
        int h = board.getHeight();

        Arrays.fill(prev, -1);
        int[] q = new int[w * h];
        prev[q[0] = pile.row * w + pile.col] = -2;

        for (int rd = 0, wr = 1; rd < wr;) {
            int f = q[rd++];
            wr = put(f, 0, 1, Command.RIGHT, w, q, wr, pile, pf, bf);
            wr = put(f, 0, -1, Command.LEFT, w, q, wr, pile, pf, bf);
            wr = put(f, -1, 0, Command.DOWN, w, q, wr, pile, pf, bf);
        }

    }

    private int put(int f, int dr, int dc, Command cmd, int w, int[] q, int wr, Pile pile, int[] pf, int[] bf) {
        int r = f / w + dr, c = f % w + dc;
        if (r < 0 || c < 0 || w - pile.getWidth() < c) {
            return wr;
        }
        int g = r * w + c;
        if (prev[g] != -1) {
            return wr;
        }
        for (int i = 0; i < pf.length; i++) {
            if (r + i < bf.length && (pf[i] << c & bf[r + i]) != 0) {
                return wr;
            }
        }
        prev[q[wr++] = g] = f;
        cmds[g] = cmd;
        return wr;
    }

    public static void main(String[] args) {
        BitRectangle board = new BitRectangle(5, new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        Pile pile = new Pile(7, new int[]{2, 2, 3});

        Turn turn = new SimplePlayer().play(board, pile);
        System.out.println(turn.commands);
    }

}
