package ika.games.poker.client.gui;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;

public class ControlPanel extends JPanel {

    private JButton fold, check, call, raise;
    private JTextField amount;

    public ControlPanel(ActionListener listener) {
        fold = new JButton("fold");
        check = new JButton("check");
        call = new JButton("call");
        raise = new JButton("raise");
        fold.addActionListener(listener);
        check.addActionListener(listener);
        call.addActionListener(listener);
        raise.addActionListener(listener);
        amount = new JTextField(3);

        setSize(180, 80);
        setBorder(new EmptyBorder(5, 5, 5, 5));

        add(fold);
        add(check);
        add(call);
        add(raise);
        add(amount);
    }

    public int getAmount() {
        return Integer.parseInt(amount.getText());
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        fold.setEnabled(enabled);
        check.setEnabled(enabled);
        call.setEnabled(enabled);
        raise.setEnabled(enabled);
        amount.setEnabled(enabled);
    }
}
