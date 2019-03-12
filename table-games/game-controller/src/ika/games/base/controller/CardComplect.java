package ika.games.base.controller;

import java.util.*;

/**
 * H A 2 3 4 5 6 7 8 9 10 J Q K
 * O A 2 3 4 5 6 7 8 9 10 J Q K
 * X A 2 3 4 5 6 7 8 9 10 J Q K
 * S A 2 3 4 5 6 7 8 9 10 J Q K
 */
public final class CardComplect {

    private CardComplect() {}

    private static final String SUIT_STRING = "hoxs";
    private static final String RANK_STRING = "e23456789abcd";
//    private static final String RANK_STRING = "A23456789TJQK";

    private static final int N_SUITS = 4;
    private static final int N_RANKS = 13;
    private static final int N_CARDS = N_SUITS * N_RANKS;

    private static final int SEKA_JOCKER = 26 + 8;
    private static final int ACE_RANK = 14;
    private static final int JOKER1 = 26 + 5;
    private static final int JOKER2 = 39 + 5;

    private static final int[] PT = new int[5];
    private static final int[] PS = new int[6];

    private static final int[] SUIT = new int[N_CARDS];
    private static final int[] RANK = new int[N_CARDS];
    private static final int[][] CARD = new int[N_SUITS][N_RANKS];

    private static final int[] JRANK = new int[N_CARDS];
    private static final int[] PRANK = new int[N_CARDS];

    private static final int[] SEKA_SCORE;
    private static final String[] COMB;

    static {
        for (int i = 0; i < N_CARDS; i++) {
            CARD[SUIT[i] = i / N_RANKS][RANK[i] = i % N_RANKS] = i;

            JRANK[i] = RANK[i] == 0 ? ACE_RANK : RANK[i] + 1;
            PRANK[i] = JRANK[i] - 2;
        }
        PT[0] = 1;
        for (int i = 1; i < PT.length; i++) {
            PT[i] = N_CARDS * PT[i - 1];
        }
        PS[0] = 1;
        for (int i = 1; i < PS.length; i++) {
            PS[i] = 13 * PS[i - 1];
        }

        SEKA_SCORE = new int[PT[3]];
        COMB = new String[PT[3]];
        Arrays.fill(SEKA_SCORE, -1);
    }

    public static int suit(int c) {
        return SUIT[c];
    }

    public static char suitChar(int c) {
        return SUIT_STRING.charAt(SUIT[c]);
    }

    public static int suitOf(char c) {
        return SUIT_STRING.indexOf(c);
    }

    public static char rankChar(int c) {
        return RANK_STRING.charAt(RANK[c]);
    }

    public static int rankOf(char c) {
        return RANK_STRING.indexOf(c);
    }

    public static String toString(int c) {
        return suitChar(c) + "" + rankChar(c);
    }

    public static int toCard(String s) {
        return CARD[suitOf(s.charAt(0))][rankOf(s.charAt(1))];
    }

    public static int[] getFullComplect() {
        int[] complect = new int[N_CARDS];
        for (int i = 0; i < N_CARDS; i++) {
            complect[i] = i;
        }
        return complect;
    }

    //-----------------------------------------------------------------------------------
    // SEKA SCORING
    //-----------------------------------------------------------------------------------

    public static int[] getSeka21Complect() {
        int[] complect = new int[N_CARDS];
        int z = 0;
        for (int i = 0; i < N_CARDS; i++) {
            if (isSeka21Card(i)) {
                complect[z++] = i;
            }
        }
        return Arrays.copyOf(complect, z);
    }

    public static boolean isSeka21Card(int c) {
        return c == SEKA_JOCKER || JRANK[c] > 9;
    }


    public static int sekaScore(int[] c) {
        return getSekaScore(cardsToSet(c));
    }

    public static String sekaComb(int[] c) {
        return getComb(cardsToSet(c));
    }


    private static int getSekaScore(int set) {
        if (SEKA_SCORE[set] != -1) {
            return SEKA_SCORE[set];
        }
        return SEKA_SCORE[set] = getScore(setToCards(set, 3));
    }

    private static String getComb(int set) {
        if (COMB[set] != null) {
            return COMB[set];
        }
        return COMB[set] = getComb(setToCards(set, 3));
    }

    private static String getComb(int[] c) {
        if (getThreeKind(c) != 0) {
            return "X3";
        }
        if (getThreeMast(c) != 0) {
            return "Flash";
        }
        if (getTwoAces(c) != 0) {
            return "22";
        }
        if (getTwoSuit(c) != 0) {
            return "" + getTwoSuit(c);
        }
        return JRANK[c[0]] == ACE_RANK ? "11" : JRANK[c[2]] == 10 ? "10" : "";
    }

    private static int getScore(int[] c) {
        int s;
        if ((s = getThreeKind(c)) != 0) {
            return PT[4] * s;
        }
        if ((s = getThreeMast(c)) != 0) {
            return PT[3] * s;
        }
        if ((s = getTwoAces(c)) != 0) {
            return PT[2] * s;
        }
        if ((s = getTwoSuit(c)) != 0) {
            return PT[2] * s;
        }
        return JRANK[c[0]] == ACE_RANK ? 11 : JRANK[c[2]] == 10 ? 10 : 0;
    }

    private static int getThreeKind(int[] c) {
        for (int i = 0; i < 2; i++) {
            if (c[i] != SEKA_JOCKER && c[i + 1] != SEKA_JOCKER && JRANK[c[i]] != JRANK[c[i + 1]]) {
                return 0;
            }
        }
        return JRANK[c[2]];
    }

    private static int getThreeMast(int[] c) {
        for (int i = 0; i < 2; i++) {
            if (c[i] != SEKA_JOCKER && SUIT[c[i]] != SUIT[c[i + 1]]) {
                return 0;
            }
        }
        return combr(c[0]) + combr(c[1]) + combr(c[2]);
    }

    private static int getTwoAces(int[] c) {
        return JRANK[c[1]] == ACE_RANK ? 22 : 0;
    }

    private static int getTwoSuit(int[] c) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < i; j++) {
                if (c[j] == SEKA_JOCKER || SUIT[c[i]] == SUIT[c[j]]) {
                    return combr(c[i]) + combr(c[j]);
                }
            }
        }
        return 0;
    }


    private static int combr(int c) {
        return c == SEKA_JOCKER || JRANK[c] == ACE_RANK ? 11 : 10;
    }

    public static int[] setToCards(int set, int k) {
        int[] c = new int[k];
        for (int i = 0; i < k; i++) {
            c[i] = set % N_CARDS;
            set /= N_CARDS;
            for (int j = 0; j < i; j++) {
                if (c[i] == SEKA_JOCKER || c[j] != SEKA_JOCKER && JRANK[c[i]] > JRANK[c[j]]) {
                    int t = c[i];
                    c[i] = c[j];
                    c[j] = t;
                }
            }
        }
        return c;
    }

    public static int cardsToSet(int[] c) {
        int set = 0;
        for (int x : c) {
            set *= N_CARDS;
            set += x;
        }
        return set;
    }

    //-----------------------------------------------------------------------------------
    // POKER SCORING
    //-----------------------------------------------------------------------------------

    private static int[] h = new int[13];
    private static int[] m = new int[4];

    public static int getPokerHandStrength(int[] a, int[] tableCards) {

        int[] c = new int[7];
        System.arraycopy(tableCards, 0, c, 0, 5);
        System.arraycopy(a, 0, c, 5, 2);

        Arrays.fill(h, 0);
        Arrays.fill(m, 0);
        for (int x : c) {
            h[PRANK[x]]++;
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


    private static int getStraightFlush(int[] c) {
        return 0;
    }

    private static int getKare(int[] c) {
        for (int x : c) {
            if (h[PRANK[x]] == 4) {
                int set = getBy(PRANK, PRANK[x], 4, c, 0);
                return 7 * PS[5] + pokerScore(c, set);
            }
        }
        return 0;
    }

    private static int getFull(int[] c) {
        for (int i = 13; i-- > 0;) {
            if (h[i] == 3) {
                int set = getBy(PRANK, i, 3, c, 0);
                for (int j = 13; j-- > 0;) {
                    if (h[j] > 1 && j != PRANK[c[f(set)]]) {
                        int spair = getBy(PRANK, j, 2, c, 0);
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
        for (int i = 13; i-- > 3;) {
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
        for (int i = 13; i-- > 0;) {
            if (h[i] == 3) {
                int set = getBy(PRANK, i, 3, c, 0);
                return 3 * PS[5] + pokerScore(c, set);
            }
        }
        return 0;
    }

    private static int getTwoPairs(int[] c) {
        for (int i = 13; i-- > 0;) {
            if (h[i] == 2) {
                int set = getBy(PRANK, i, 2, c, 0);
                for (int j = PRANK[c[f(set)]]; j-- > 0;) {
                    if (h[j] == 2) {
                        int spair = getBy(PRANK, j, 2, c, 0);
                        return 2 * PS[5] + pokerScore(c, set, spair);
                    }
                }
            }
        }
        return 0;
    }

    private static int getPair(int[] c) {
        for (int i = 13; i-- > 0;) {
            if (h[i] == 2) {
                int set = getBy(PRANK, i, 2, c, 0);
                return PS[5] + pokerScore(c, set);
            }
        }
        return 0;
    }


    private static int pokerScore(int[] c, int... s) {
        int t = 5, all = 0;
        for (int x : s) {
            t -= Integer.bitCount(x);
            all |= x;
        }
        int score = getHighestCardsScore(t, c, all);
        for (int i = s.length; i-- > 0;) {
            score += PS[t++] * PRANK[c[f(s[i])]];
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
                set |= 1 << PRANK[c[i]];
            }
        }
        int score = 0;
        for (int i = 13; i-- > 0 && count > 0;) {
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
                if ((ret & 1 << i) != 0 && (worst == -1 || PRANK[c[worst]] > PRANK[c[i]])) {
                    worst = i;
                }
            }
            ret ^= 1 << worst;
        }
        return ret;
    }

    //-----------------------------------------------------------------------------------
    // JOKER SCORING
    //-----------------------------------------------------------------------------------

    public static int jrank(int c) {
        return JRANK[c];
    }

    public static boolean isJJoker(int c) {
        return jrank(c) == 6 && suit(c) > 1;
    }

    public static boolean isAce(int c) {
        return jrank(c) == ACE_RANK;
    }

    public static int[] getJokerComplect() {
        int[] complect = new int[36];
        int z = 0;
        for (int i = 0; i < N_CARDS; i++) {
            if (JRANK[i] > 5) {
                complect[z++] = i;
            }
        }
        return complect;
    }

    public static int getFirstJoker() {
        return JOKER1;
    }

    public static int getSecondJoker() {
        return JOKER2;
    }

}
