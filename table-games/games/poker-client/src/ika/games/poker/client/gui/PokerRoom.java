package ika.games.poker.client.gui;

import com.simplejcode.commons.net.csbase.MapMessage;
import ika.games.poker.client.PokerClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class PokerRoom extends JPanel implements ActionListener, ComponentListener {

    private static final int CARD_WIDTH = 79;
    private static final int CARD_HEIGHT = 123;
    private static final BufferedImage ALL_CARDS = PokerClient.getImage("resources/cards.png");

    public static BufferedImage getImage(int card) {
        int cx = card % 13;
        int cy = card / 13;
        return ALL_CARDS.getSubimage(cx * CARD_WIDTH, cy * CARD_HEIGHT, CARD_WIDTH, CARD_HEIGHT);
    }

    //-----------------------------------------------------------------------------------

    private MapMessage tableRecord;
    private int place;

    public void setPlace(int place) {
        this.place = place;
    }

    private int nPlayers, diller, current, inGame;
    private boolean open;
    private int[] money;
    private int[] bank;
    private int[][] cards;
    private String[] userNames;
    private int[] tableCards;
    private int ind;

    private PTable table;
    private PlayerArea[] playerAreas;
    private ControlPanel controlPanel;

    public PokerRoom() {
        setLayout(null);
        setPreferredSize(new Dimension(900, 600));
    }

    public synchronized void updateState(MapMessage r) {

        tableRecord = r.getRecord("table");

        money = r.getInts("money");
        nPlayers = money.length;
        bank = r.getInts("bank");
        diller = r.getInt("diller");
        current = r.getInt("current");
        cards = (int[][]) r.get("cards");
        tableCards = r.getInts("table_cards");
        ind = r.getInt("ind");
        inGame = r.getInt("in_game");
        open = r.getInt("open") > 0;
        userNames = r.getStrings("users");

        if (playerAreas == null) {
            init();
        }
        for (int i = 0; i < nPlayers; i++) {
            PlayerArea t = playerAreas[i];
            t.setBank(bank[i]);
            t.setCards(cards[i]);
            t.setCurrent(current == i);
            t.setDiller(diller == i);
            t.setInGame((inGame & 1 << i) != 0);
            t.setMoney(money[i]);
            t.setMe(i == place);
            t.setEmpty(userNames[i] == null);
            t.setOpen(open);
            t.setUserName(userNames[i]);
        }

        repaint();

    }

    private void init() {
        addComponentListener(this);
        playerAreas = new PlayerArea[nPlayers];
        for (int i = 0; i < nPlayers; i++) {
            add(playerAreas[i] = new PlayerArea());
        }
        add(table = new PTable());
        add(controlPanel = new ControlPanel(this));
        controlPanel.setEnabled(false);
        align();
    }

    public void play() {
        controlPanel.setEnabled(true);
    }

    private void align() {
        int width = getWidth();
        int height = getHeight();

        int ox = width / 2;
        int oy = height / 2;

        int a = width - 150 >> 1;
        int b = height - 150 >> 1;

        for (int i = 0; i < nPlayers; i++) {
            double t = 2 * Math.PI * i / nPlayers;
            int x = (int) (ox + a * Math.cos(t)) - 50;
            int y = (int) (oy + b * Math.sin(t)) - 50;
            playerAreas[i].setSize(100, 130);
            playerAreas[i].setLocation(x, y);
        }

        table.setSize(430, 150);
        table.setLocation(ox - table.getWidth() / 2, oy - table.getHeight() / 2);

        controlPanel.setLocation(getWidth() - controlPanel.getWidth(), getHeight() - controlPanel.getHeight());

    }

    //-----------------------------------------------------------------------------------

    public void componentResized(ComponentEvent e) {
        align();
    }

    public void componentMoved(ComponentEvent e) {

    }

    public void componentShown(ComponentEvent e) {

    }

    public void componentHidden(ComponentEvent e) {

    }

    public void actionPerformed(ActionEvent e) {
//        if (current != place) {
//            return;
//        }
//        int bet = 0;
//        if (e.getActionCommand().equals("fold")) {
//            bet = -1;
//        }
//        if (e.getActionCommand().equals("check")) {
//            bet = 0;
//        }
//        if (e.getActionCommand().equals("call")) {
//            bet = Math.min(money[place], getMaxBet() - bank[place]);
//        }
//        if (e.getActionCommand().equals("raise")) {
//            try {
//                bet = controlPanel.getAmount();
//            } catch (NumberFormatException ex) {
//                return;
//            }
//        }
//        if (bet != -1 && bet > money[place]) {
//            return;
//        }
//        if (bet != -1 && bank[place] + bet < getMaxBet() && bet < money[place]) {
//            return;
//        }
//        MapMessage rec = new MapMessage(this);
//        rec.put("type", "bet");
//        rec.put("table", tableRecord.getString("name"));
//        rec.put("bet", bet);
//        PokerClient.client.sendMessage(rec);
//        controlPanel.setEnabled(false);
    }

    private int getMaxBet() {
        int maxBet = 0;
        for (int i = 0; i < bank.length; i++) {
            maxBet = Math.max(maxBet, bank[i]);
        }
        return maxBet;
    }

    //-----------------------------------------------------------------------------------

    public class PTable extends JComponent {
        public void paintComponent(Graphics g) {

            Graphics2D gr = (Graphics2D) g;
            for (int i = 0; i < ind; i++) {
                g.drawImage(PokerRoom.getImage(tableCards[i]), i * (CARD_WIDTH + 5), 0, this);
            }

            if (nPlayers == 0) {
                return;
            }
            int tot = 0;
            for (int x : bank) {
                tot += x;
            }

            g.setColor(Color.blue);
            g.setFont(new Font("Serif", Font.BOLD, 22));
            g.drawString("" + tot, getWidth() / 2, 145);

        }
    }

}
