package pool.gui.menu;

import pool.gui.MainFrame;

import javax.swing.*;

public abstract class BasicDialog extends JDialog {
    protected MainFrame mainFrame;

    protected BasicDialog(MainFrame mainFrame, String title) {
        super(mainFrame, title, true);
        this.mainFrame = mainFrame;
        setContent();
        pack();
        setLocation(mainFrame.getX() + (mainFrame.getWidth() - getWidth()) / 2,
                mainFrame.getY() + (mainFrame.getHeight() - getHeight()) / 2);
        setResizable(false);
    }

    protected abstract void setContent();

    protected void done(Object... args) {
        mainFrame.dialogDone(args);
    }
}
