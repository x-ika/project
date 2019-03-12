package minesweeper.strategy;

public class IntBitSet {
    private static final int X = 5;

    private int[] w;

    public IntBitSet(int nBits) {
        w = new int[(nBits - 1 >> X) + 1];
    }

    int size() {
        return w.length << X;
    }

    boolean isEmpty() {
        for (int x : w) {
            if (x != 0) {
                return false;
            }
        }
        return true;
    }

    int bitCount() {
        int r = 0;
        for (int x : w) {
            r += Integer.bitCount(x);
        }
        return r;
    }

    void set(int i) {
        w[i >> X] |= 1 << i;
    }

    boolean cont(int i) {
        return (w[i >> X] & 1 << i) != 0;
    }

    boolean cont(IntBitSet s) {
        for (int i = 0; i < w.length; i++) {
            if ((w[i] | s.w[i]) != w[i]) {
                return false;
            }
        }
        return true;
    }

    boolean secs(IntBitSet s) {
        for (int i = 0; i < w.length; i++) {
            if ((w[i] & s.w[i]) != 0) {
                return true;
            }
        }
        return false;
    }

    IntBitSet copy(IntBitSet s) {
        for (int i = 0; i < w.length; i++) {
            w[i] = s.w[i];
        }
        return this;
    }

    IntBitSet or(IntBitSet s) {
        for (int i = 0; i < w.length; i++) {
            w[i] |= s.w[i];
        }
        return this;
    }

    IntBitSet and(IntBitSet s) {
        for (int i = 0; i < w.length; i++) {
            w[i] &= s.w[i];
        }
        return this;
    }

    IntBitSet andNot(IntBitSet s) {
        for (int i = 0; i < w.length; i++) {
            w[i] &= ~s.w[i];
        }
        return this;
    }

    boolean nextSubset(IntBitSet set) {
        int i = 0;
        while (i < w.length && w[i] == 0) {
            i++;
        }
        if (i < w.length) {
            w[i]--;
        }
        while (i-- > 0) {
            w[i] = -1;
        }
        and(set);
        for (i = 0; i < w.length; i++) {
            if (w[i] != set.w[i]) {
                return true;
            }
        }
        return false;
    }

}
