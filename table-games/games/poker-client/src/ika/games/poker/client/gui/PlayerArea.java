package ika.games.poker.client.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.border.*;

public class PlayerArea extends JComponent {

    private Color bg;

    private String userName;
    private int money, bank;
    private boolean isDiller, current, inGame, me, isEmpty = true, open;
    private int[] cards;

    public PlayerArea() {
        bg = new Color(238, 238, 238);
    }

    public void setUserName(String userName) {
        this.userName = userName;
        Border inner = BorderFactory.createTitledBorder(userName);
        Border outer = BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.blue, Color.gray);
        setBorder(BorderFactory.createCompoundBorder(outer, inner));
        setBorder(inner);
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void setBank(int bank) {
        this.bank = bank;
    }

    public void setDiller(boolean diller) {
        isDiller = diller;
    }

    public void setCurrent(boolean current) {
        this.current = current;
    }

    public void setCards(int[] cards) {
        this.cards = cards;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public void setMe(boolean me) {
        this.me = me;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    //-----------------------------------------------------------------------------------

    public void paint(Graphics g) {
        super.paint(g);
        if (isEmpty) {
            return;
        }
        Graphics2D gr = (Graphics2D) g;

        int yoff = 20;

        BufferedImage ex = PokerRoom.getImage(0);
        int w = ex.getWidth();
        double d = 1d * (getWidth() - 10 >> 1) / w;
        gr.scale(d, d);
        if ((!open || !inGame) && !me) {
            cards = new int[]{54, 54};
        }
        g.drawImage(PokerRoom.getImage(cards[0]), (int) (5 / d), (int) (yoff / d), this);
        g.drawImage(PokerRoom.getImage(cards[1]), w + (int) (5 / d), (int) (yoff / d), this);
        gr.scale(1 / d, 1 / d);

        yoff += d * ex.getHeight() + 5;
        g.setColor(isDiller ? Color.yellow : bg);
        g.fillOval(10, yoff, 10, 10);
        g.setColor(inGame ? bg : Color.black);
        g.fillOval(30, yoff, 10, 10);
        g.setColor(current ? Color.red: bg);
        g.fillOval(50, yoff, 10, 10);

        yoff += 15;
        g.setColor(Color.black);
        g.drawString("" + money + " / " + bank, 10, yoff + 10);

    }

}
