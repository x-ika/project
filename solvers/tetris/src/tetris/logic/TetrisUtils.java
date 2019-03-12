package tetris.logic;

import static java.lang.Integer.*;

/**
 * Provides some operations on board/pile
 */
public final class TetrisUtils {

    private TetrisUtils() {}

    /**
     * @param t board representation
     * @return  number of the filled blocks
     */
    public static int area(int[] t) {
        int s = 0;
        for (int x : t) {
            s += bitCount(x);
        }
        return s;
    }

    /**
     * @param t board representation
     * @return  0-based index of the topmost nonempty row
     */
    public static int top(int[] t) {
        int top = -1;
        while (t[top + 1] != 0) {
            top++;
        }
        return top;
    }

    /**
     * @param t board representation
     * @return 0-based index of the rightmost nonempty column or 32 if board contains empty row
     */
    public static int rightmost(int[] t) {
        int a = 0;
        for (int x : t) {
            a = Math.max(a, numberOfTrailingZeros(highestOneBit(x)));
        }
        return a;
    }

    /**
     * @param t board representation
     * @return 0-based index of the leftmost nonempty column or 32 if board is empty (area=0)
     */
    public static int leftmost(int[] t) {
        int a = 32;
        for (int x : t) {
            a = Math.min(a, numberOfTrailingZeros(lowestOneBit(x)));
        }
        return a;
    }

    /**
     * Removes starting empty columns from the board
     * @param t board representation
     * @return padded board
     */
    public static int[] normalize(int[] t) {
        int left = leftmost(t);
        for (int i = 0; i < t.length; i++) {
            t[i] >>= left;
        }
        return t;
    }

    /**
     * Rotates the board by 90deg counter clockwise
     * @param t board representation
     * @return rotated board
     */
    public static int[] rotate(int[] t) {
        int left = leftmost(t);
        int[] s = new int[rightmost(t) - left + 1];
        for (int i = 0; i < s.length; i++) {
            for (int j = 0; j < t.length; j++) {
                if ((t[j] & 1 << i + left) != 0) {
                    s[i] |= 1 << 10 - j;
                }
            }
        }
        return normalize(s);
    }

    /**
     * Puts the pile on the given board at the given position
     * Works correctly only when board and pile are not intersecting
     * @param t board representation
     * @param p pile representation
     * @param r pile row
     * @param c pile column
     */
    public static void xor(int[] t, int[] p, int r, int c) {
        for (int i = 0; i < p.length; i++) {
            t[i + r] ^= p[i] << c;
        }
    }

    /**
     * @param t board representation
     * @param p pile representation
     * @param r pile row
     * @param c pile column
     * @return true if and only if pile intersects filled region of the board
     */
    public static boolean intersecs(int[] t, int[] p, int r, int c) {
        for (int i = 0; i < p.length; i++) {
            if ((t[i + r] & p[i] << c) != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param t board representation
     * @param p pile representation
     * @param r pile row
     * @param c pile column
     * @return true if the given pile at position (r, c) would be on the ground.
     * Which means that we can put the pile at this particular position (after we
     * ensure that pile does not occupies filled region of the board)
     */
    public static boolean checkContact(int[] t, int[] p, int r, int c) {
        int contact = 0;
        for (int i = 0; i < p.length; i++) {
            contact |= r == 0 ? 1 : p[i] << c & t[r + i - 1];
        }
        return contact != 0;
    }

    /**
     * @param t board representation
     * @return String representation of the board
     */
    public static String toString(int[] t) {
        StringBuilder sb = new StringBuilder();
        for (int i = t.length; i-- > 0;) {
            for (int j = 0; j < 10; j++) {
                sb.append((t[i] >> j & 1) == 0 ? '.' : '#');
            }
            sb.append('\n');
        }
        sb.append("-----------------------------\n");
        return sb.toString();
    }

}
