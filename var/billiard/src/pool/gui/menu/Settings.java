package pool.gui.menu;

import pool.utils.SoundManager;
import pool.gui.primitive.JButtons;
import pool.gui.MainFrame;

import java.awt.event.*;

public class Settings extends BasicDialog implements ActionListener {
    private final String SOUND_ON = "Sound ON";

    private final String SOUND_OFF = "Sound OFF";

    public Settings(MainFrame owner) {
        super(owner, "Settings");
        setVisible(true);
    }

    protected void setContent() {
        getContentPane().add(new JButtons(this, SOUND_ON, SOUND_OFF));
    }

    public void actionPerformed(ActionEvent ev) {
        String comand = ev.getActionCommand();
        if (comand.equals(SOUND_ON)) {
            SoundManager.getInstance().setMusicFlag(true);
        } else if (comand.equals(SOUND_OFF)) {
            SoundManager.getInstance().setMusicFlag(false);
        }
    }
}
