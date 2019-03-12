import com.simplejcode.commons.gui.GraphicUtils;

import javax.swing.*;
import java.awt.*;

public class NewSimulationForm extends JFrame {

    private JTextField nf;
    private JTextField kf;

    public NewSimulationForm(Action okAction, Action cancelAction) {
        super("Simulation Parameters");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addPanel("Maximum Possible Value", nf = new JTextField("100", 5));
        addPanel("Number Of Rare Numbers", kf = new JTextField("5", 5));

        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        JPanel buttons = new JPanel();
        buttons.add(new JButton(okAction));
        buttons.add(new JButton(cancelAction));
        getContentPane().add(buttons);

        pack();
        GraphicUtils.centerOnScreen(this);
        setVisible(true);
    }

    private void addPanel(String labelText, JTextField field) {
        JLabel label = new JLabel(labelText);
        label.setDisplayedMnemonic(labelText.charAt(0));
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.add(label);
        panel.add(field);
        getContentPane().add(panel);
    }

    public String getN() {
        return nf.getText();
    }

    public String getK() {
        return kf.getText();
    }
}
