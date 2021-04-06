package ika.games.poker.controller;

import java.util.Arrays;

public final class CardComplect {

    private static final int[] PS = new int[6];

    private static final int N_CARDS = 52;
    private static final int[] RANK = new int[N_CARDS];
    private static final int[] SUIT = new int[N_CARDS];

    static {
        for (int i = 0; i < N_CARDS; i++) {
            RANK[i] = i % 13 == 0 ? 12 : i % 13 - 1;
            SUIT[i] = i / 13;
        }
        PS[0] = 1;
        for (int i = 0; i < 5; i++) {
            PS[i + 1] = 13 * PS[i];
        }
    }

    private static int[] h = new int[13];
    private static int[] m = new int[4];

    //-----------------------------------------------------------------------------------

    public static int getPokerHandStrength(int[] a, int[] tableCards) {

        int[] c = new int[7];
        System.arraycopy(tableCards, 0, c, 0, 5);
        System.arraycopy(a, 0, c, 5, 2);

        Arrays.fill(h, 0);
        Arrays.fill(m, 0);
        for (int x : c) {
            h[RANK[x]]++;
            m[SUIT[x]]++;
        }

        int score = 0;
        score = Math.max(score, getStraightFlush(c));
        score = Math.max(score, getKare(c));
        score = Math.max(score, getFull(c));
        score = Math.max(score, getFlush(c));
        score = Math.max(score, getStraight(c));
        score = Math.max(score, getSet(c));
        score = Math.max(score, getTwoPairs(c));
        score = Math.max(score, getPair(c));
        score = Math.max(score, getHighestCardsScore(5, c, 0));
        return score;
    }

    //-----------------------------------------------------------------------------------

    private static int getStraightFlush(int[] c) {
        return 0;
    }

    private static int getKare(int[] c) {
        for (int x : c) {
            if (h[RANK[x]] == 4) {
                int set = getBy(RANK, RANK[x], 4, c, 0);
                return 7 * PS[5] + pokerScore(c, set);
            }
        }
        return 0;
    }

    private static int getFull(int[] c) {
        for (int i = 13; i-- > 0; ) {
            if (h[i] == 3) {
                int set = getBy(RANK, i, 3, c, 0);
                for (int j = 13; j-- > 0; ) {
                    if (h[j] > 1 && j != RANK[c[f(set)]]) {
                        int spair = getBy(RANK, j, 2, c, 0);
                        return 6 * PS[5] + pokerScore(c, set, spair);
                    }
                }
            }
        }
        return 0;
    }

    private static int getFlush(int[] c) {
        for (int i = 0; i < 4; i++) {
            if (m[i] > 4) {
                int set = getBy(SUIT, i, 5, c, 0);
                return 5 * PS[5] + getHighestCardsScore(5, c, ~set);
            }
        }
        return 0;
    }

    private static int getStraight(int[] c) {
        M:
        for (int i = 13; i-- > 3; ) {
            int j = i;
            for (int t = 0; t < 5; t++) {
                if (h[j] == 0) {
                    continue M;
                }
                j = (j + 12) % 13;
            }
            return 4 * PS[5] + i;
        }
        return 0;
    }

    private static int getSet(int[] c) {
        for (int i = 13; i-- > 0; ) {
            if (h[i] == 3) {
                int set = getBy(RANK, i, 3, c, 0);
                return 3 * PS[5] + pokerScore(c, set);
            }
        }
        return 0;
    }

    private static int getTwoPairs(int[] c) {
        for (int i = 13; i-- > 0; ) {
            if (h[i] == 2) {
                int set = getBy(RANK, i, 2, c, 0);
                for (int j = RANK[c[f(set)]]; j-- > 0; ) {
                    if (h[j] == 2) {
                        int spair = getBy(RANK, j, 2, c, 0);
                        return 2 * PS[5] + pokerScore(c, set, spair);
                    }
                }
            }
        }
        return 0;
    }

    private static int getPair(int[] c) {
        for (int i = 13; i-- > 0; ) {
            if (h[i] == 2) {
                int set = getBy(RANK, i, 2, c, 0);
                return PS[5] + pokerScore(c, set);
            }
        }
        return 0;
    }

    //-----------------------------------------------------------------------------------

    private static int pokerScore(int[] c, int... s) {
        int t = 5, all = 0;
        for (int x : s) {
            t -= Integer.bitCount(x);
            all |= x;
        }
        int score = getHighestCardsScore(t, c, all);
        for (int i = s.length; i-- > 0; ) {
            score += PS[t++] * RANK[c[f(s[i])]];
        }
        return score;
    }

    private static int f(int set) {
        return Integer.numberOfTrailingZeros(set);
    }

    private static int getHighestCardsScore(int count, int[] c, int used) {
        int set = 0;
        for (int i = 0; i < c.length; i++) {
            if ((used & 1 << i) == 0) {
                set |= 1 << RANK[c[i]];
            }
        }
        int score = 0;
        for (int i = 13; i-- > 0 && count > 0; ) {
            if ((set & 1 << i) != 0) {
                score += PS[--count] * i;
            }
        }
        return score;
    }

    private static int getBy(int[] f, int need, int count, int[] c, int used) {
        int ret = 0;
        for (int i = 0; i < c.length; i++) {
            if ((used & 1 << i) == 0 && f[c[i]] == need) {
                ret |= 1 << i;
            }
        }
        while (Integer.bitCount(ret) > count) {
            int worst = -1;
            for (int i = 0; i < c.length; i++) {
                if ((ret & 1 << i) != 0 && (worst == -1 || RANK[c[worst]] > RANK[c[i]])) {
                    worst = i;
                }
            }
            ret ^= 1 << worst;
        }
        return ret;
    }

}
