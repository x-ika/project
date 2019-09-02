import com.simplejcode.commons.gui.ImagePanel;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import static java.awt.GridBagConstraints.*;

public class CommandPanel extends JPanel {

    private JLabel titleLabel;

    private ImagePanel taskLogo;

    private JTextField inputTextField;
    private JLabel commentLabel;

    private JToggleButton startButton;
    private JToggleButton stopButton;

    private JTextField statusTextField;

    public CommandPanel() {

        titleLabel = new JLabel();
        titleLabel.setOpaque(true);
        titleLabel.setBorder(new EmptyBorder(5, 5, 5, 5));

        taskLogo = new ImagePanel();

        inputTextField = new JTextField();
        commentLabel = new JLabel();

        startButton = new JToggleButton("START");
        stopButton = new JToggleButton("STOP");
        ButtonGroup group = new ButtonGroup();
        group.add(startButton);
        group.add(stopButton);

        statusTextField = new JTextField();
        statusTextField.setEditable(false);
        statusTextField.setBackground(Color.white);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        titlePanel.add(titleLabel);
        add(titlePanel);
        add(createMainPanel());

        BasicStroke stroke = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{5}, 0);
        setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new StrokeBorder(stroke, Color.gray)));

    }

    private JPanel createMainPanel() {

        GridBagLayout layout = new GridBagLayout();
        JPanel panel = new JPanel(layout);

        Box input = new Box(BoxLayout.Y_AXIS);
        input.add(inputTextField);
        input.add(commentLabel);

        int gap = 15;
        panel.add(taskLogo, new GridBagConstraints(0, 0, 1, 2, 0, 0, NORTH, HORIZONTAL, new Insets(gap, gap, gap, gap), 0, 0));

        panel.add(input, new GridBagConstraints(1, 0, 1, 1, 1, 1, NORTH, HORIZONTAL, new Insets(gap, gap, gap, gap), 0, 0));
        panel.add(startButton, new GridBagConstraints(2, 0, 1, 1, 0, 0, NORTH, HORIZONTAL, new Insets(gap, gap, gap, gap), 0, 0));
        panel.add(stopButton, new GridBagConstraints(3, 0, 1, 1, 0, 0, NORTH, HORIZONTAL, new Insets(gap, gap, gap, gap), 0, 0));

        panel.add(statusTextField, new GridBagConstraints(1, 1, 3, 1, 1, 1, CENTER, HORIZONTAL, new Insets(gap, gap, gap, gap), 0, 0));

        setPreferredSize(new Dimension(600, 200));

        return panel;
    }

    public void setInputFont(Font font) {
        inputTextField.setFont(font);
    }

    public void setStatusFont(Font font) {
        statusTextField.setFont(font);
    }

    public void setTitle(Font font, String title, Color background) {
        titleLabel.setFont(font);
        titleLabel.setBackground(background);
        titleLabel.setText(title);
    }

    public void setComment(Font font, String comment) {
        commentLabel.setFont(font);
        commentLabel.setText(comment);
    }

    public void setTaskLogo(BufferedImage logo) {
        taskLogo.setImage(logo);
        taskLogo.setPreferredSize(new Dimension(200, logo.getHeight()));
    }

    public void setActionOnStart(Consumer<Void> action) {
        startButton.addActionListener(e -> {
            if (startButton.isSelected()) {
                action.accept(null);
            }
        });
    }

    public void setActionOnEnd(Consumer<Void> action) {
        stopButton.addActionListener(e -> {
            if (stopButton.isSelected()) {
                action.accept(null);
            }
        });
    }

    public String getParameters() {
        return inputTextField.getText();
    }

    public void setStatus(String status) {
        SwingUtilities.invokeLater(() -> statusTextField.setText(status));
    }

    public void reset() {
        SwingUtilities.invokeLater(() -> stopButton.setSelected(true));
    }

}
