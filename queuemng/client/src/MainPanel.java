import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {

    private int ticket;

    public MainPanel(Action finishAction) {
        add(new JButton(finishAction));
    }

    public int getTicket() {
        return ticket;
    }

    public void setTicket(int ticket) {
        this.ticket = ticket;
    }

    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(Color.black);
        g.setFont(new Font("Serif", Font.BOLD, 100));
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth("" + ticket);
        g.drawString("" + ticket, getWidth() - w >> 1, getHeight() - fm.getHeight() + 2 * fm.getAscent() >> 1);

    }
}
