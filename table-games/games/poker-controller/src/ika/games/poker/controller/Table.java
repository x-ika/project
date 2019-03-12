package ika.games.poker.controller;

import com.simplejcode.commons.net.csbase.MapMessage;
import com.simplejcode.commons.net.csbase.Message;
import com.simplejcode.commons.net.sockets.SocketConnection;

import java.util.*;

@SuppressWarnings({"StatementWithEmptyBody"})
public class Table {

    public class Player {

        SocketConnection<Message> s;

        public Player(SocketConnection<Message> s) {
            this.s = s;
        }

        public void updateState(boolean open) {
            MapMessage record = new MapMessage(this);

            record.put("type", "table_state");
            record.put("table", tableRecord);
            record.put("money", money);
            record.put("bank", bank);
            record.put("diller", diller);
            record.put("current", current);
            record.put("cards", cards);
            record.put("table_cards", tableCards);
            record.put("ind", ind);
            record.put("in_game", inGame);
            record.put("open", open ? 1 : 0);
            String[] users = new String[n];
            for (int i = 0; i < n; i++) {
                if (players[i] != null) {
                    users[i] = (String) players[i].s.getHost();
                }
            }
            record.put("users", users);

            s.sendMessage(record);
        }

        public void sit(int sit) {
            MapMessage record = new MapMessage(this);

            record.put("type", "action_sit");
            record.put("table", tableRecord);
            record.put("result", "success");
            record.put("place", sit);

            s.sendMessage(record);
        }

        public void play() {
            MapMessage record = new MapMessage(this);
            record.put("type", "action_play");
            record.put("table", tableRecord);
            s.sendMessage(record);
        }

    }

    public static final int START_MONEY = 100;
    public static final int N_ROUNDS = 3;

    private final MapMessage tableRecord;
    private final int n, small;
    private final Random rnd;

    // table state
    private int total;
    private int diller;
    private int current;
    private int inGame;
    private int[] money;
    private int[] bank;
    private int[] timesBet;
    private int[][] cards;
    private Player[] players;

    private int[] tableCards;
    private int ind;

    private List<Integer> allCards;

    public Table(MapMessage record) {
        this.tableRecord = record;
        n = record.getInt("max_players");
        small = record.getInt("small_blind");
        players = new Player[n];
        money = new int[n];
        Arrays.fill(money, -1);
        rnd = new Random();

        cards = new int[n][2];
        bank = new int[n];
        timesBet = new int[n];

        tableCards = new int[5];

    }

    public String getName() {
        return tableRecord.getString("name");
    }

    //-----------------------------------------------------------------------------------

    public synchronized int placeOf(String playerName) {
        for (int i = 0; i < n; i++) {
            if (players[i] != null && players[i].s.getHost().equals(playerName)) {
                return i;
            }
        }
        return -1;
    }

    public synchronized void takeAny(Player player) {
        for (int i = 0; i < n; i++) {
            if (players[i] == null) {
                take(i, player);
                break;
            }
        }
    }

    public synchronized void take(int sit, Player player) {
        if (players[sit] != null) {
            return;
        }
        players[sit] = player;
        player.sit(sit);
        if (++total == 1) {
            diller = sit;
        }
        if (total == 2) {
            new Thread() {
                public void run() {
                    startHand();
                }
            }.start();
        }
    }

    public synchronized void free(int sit) {
        money[sit] = 1;
        players[sit] = null;
        inGame &= ~(1 << sit);
    }

    public synchronized void playerBet(String name, int bet) {
        int sit = placeOf(name);
        if (sit != current) {
            return;
        }
        if (!checkBet(sit, bet)) {
            playerFold(name);
            return;
        }
        timesBet[sit]++;
        makeBet(sit, bet);
        goToNext();
    }

    public synchronized void playerFold(String name) {
        int sit = placeOf(name);
        if (sit != current) {
            return;
        }
        inGame &= ~(1 << sit);
        goToNext();
    }

    //-----------------------------------------------------------------------------------

    private synchronized void startHand() {

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // add/remove players
        for (int i = 0; i < n; i++) {
            if (players[i] != null && money[i] == -1) {
                money[i] = START_MONEY;
            }
            if (players[i] == null && money[i] == 1) {
                money[i] = -1;
                total--;
            }
        }
        if (total < 2) {
            return;
        }
        // who is in game
        inGame = 0;
        for (int i = 0; i < n; i++) {
            if (money[i] > 0) {
                inGame |= 1 << i;
            }
        }
        diller = nextCan(diller);

        // blinds
        makeBet(nextIn(diller), small);
        makeBet(nextIn(nextIn(diller)), 2 * small);

        // init cards
        allCards = new ArrayList<>();
        for (int i = 0; i < 52; i++) {
            allCards.add(i);
        }
        for (int i = 0; i < 1000; i++) {
            int a = rnd.nextInt(52);
            int b = rnd.nextInt(52);
            int t = allCards.get(a);
            allCards.set(a, allCards.get(b));
            allCards.set(b, t);
        }
        for (int i = 0; i < 2; i++) {
            int d = diller;
            for (int j = 0; j < total; j++) {
                d = nextIn(d);
                cards[d][i] = allCards.remove(0);
            }
        }

        ind = 0;
        current = nextIn(nextIn(diller));
        Arrays.fill(timesBet, 0);
        updateAll(false);
        goToNext();
    }

    private synchronized void goToNext() {
        if (Integer.bitCount(inGame) == 1) {
            openAll();
            return;
        }
        if (everyonePlayed()) {
            if (ind == 5 || everyoneALLIN()) {
                openAll();
                return;
            }
            openOneCard();
            if (ind == 1) {
                openOneCard();
                openOneCard();
            }
            // next rec
            Arrays.fill(timesBet, 0);
            current = nextCan(diller);
        } else {
            current = nextCan(current);
        }
        updateAll(false);
        // say to play
        players[current].play();
    }

    private synchronized void openAll() {

        if (Integer.bitCount(inGame) == 1) {
            endHand();
            return;
        }

        updateAll(true);
        pause(2000);

        while (ind < 5) {
            openOneCard();
            updateAll(true);
            pause(2000);
        }

        endHand();

    }

    private synchronized void endHand() {

        while (inGame != 0) {

            // the players with the best hands
            List<Integer> best = new ArrayList<>();
            int max = -1;
            for (int i : inGame()) {
                int cur = CardComplect.getPokerHandStrength(cards[i], tableCards);
                if (max < cur) {
                    max = cur;
                    best.clear();
                }
                if (max == cur) {
                    best.add(i);
                }
            }
            // peek required amount of money from the other players
            Collections.sort(best, (o1, o2) -> bank[o1] - bank[o2]);
            int min = bank[best.get(0)];
            int total = 0;
            for (int i = 0; i < n; i++) {
                int peek = Math.min(min, bank[i]);
                bank[i] -= peek;
                total += peek;
            }
            // distribute total amount over best hands
            int div = total / best.size();
            int mod = total % best.size();
            for (int i = 0; i < best.size(); i++) {
                int t = i < mod ? div + 1 : div;
                money[best.get(i)] += t;
            }
            // remove outsiders
            for (int i = 0; i < n; i++) {
                if (bank[i] == 0) {
                    inGame &= ~(1 << i);
                }
            }

        }

        updateAll(Integer.bitCount(inGame) > 1);
        startHand();
    }

    //-----------------------------------------------------------------------------------

    private synchronized void updateAll(boolean open) {
        for (Player p : players) {
            if (p != null) {
                p.updateState(open);
            }
        }
    }

    private synchronized void openOneCard() {
        allCards.remove(0);
        tableCards[ind++] = allCards.remove(0);
    }

    private synchronized void makeBet(int i, int bet) {
        bet = Math.min(bet, money[i]);
        money[i] -= bet;
        bank[i] += bet;
    }

    //-----------------------------------------------------------------------------------

    private synchronized boolean everyonePlayed() {
        int maxBet = getMaxBet();
        for (int i : inGame()) {
            if (money[i] > 0 && (timesBet[i] == 0 || bank[i] < maxBet)) {
                return false;
            }
        }
        return true;
    }

    private synchronized boolean everyoneALLIN() {
        int c = 0;
        for (int i : inGame()) {
            if (money[i] > 0) {
                c++;
            }
        }
        return c < 2;
    }

    private synchronized int[] inGame() {
        int[] t = new int[Integer.bitCount(inGame)];
        for (int i = 0, z = 0; i < n; i++) {
            if ((inGame & 1 << i) != 0) {
                t[z++] = i;
            }
        }
        return t;
    }

    private synchronized boolean checkBet(int i, int bet) {
        int maxBet = getMaxBet();

        // bet more then have
        if (bet > money[i]) {
            return false;
        }
        // not enough
        if (bank[i] + bet < maxBet && bet < money[i]) {
            return false;
        }

        // seems that everything is ok
        return true;
    }

    private synchronized int getMaxBet() {
        int maxBet = 0;
        for (int i = 0; i < n; i++) {
            maxBet = Math.max(maxBet, bank[i]);
        }
        return maxBet;
    }

    private synchronized int nextCan(int i) {
        while (money[i = nextIn(i)] <= 0) ;
        return i;
    }

    private synchronized int nextIn(int i) {
        while ((inGame & 1 << (i = next(i))) == 0) ;
        return i;
    }

    private synchronized int next(int i) {
        return (i + 1) % n;
    }

    private static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
