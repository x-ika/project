package accounting;

import com.simplejcode.commons.gui.AboutDialogPanel;
import com.simplejcode.commons.gui.Console;
import com.simplejcode.commons.gui.CustomFrame;
import com.simplejcode.commons.gui.GraphicUtils;
import com.simplejcode.commons.misc.PropertyManager;
import com.simplejcode.commons.misc.util.ExceptionUtils;
import com.simplejcode.commons.misc.util.IOUtils;
import com.simplejcode.commons.misc.util.ReflectionUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class AccountingController {

    public static void main(String[] args) throws Exception {
        new AccountingController().init();
    }

    //-----------------------------------------------------------------------------------
    /*
    UI Initialization
     */

    /**
     * Main frame of the application
     */
    private CustomFrame frame;

    /**
     * Used to handle all menu item clicks except the File -> Exit, Help -> About, etc.
     * Contains logic behind the UI update.
     */
    private AccountingHelper helper;

    private Console console;


    private void init() throws Exception {
        PropertyManager.setPropertiesFileName(Constants.PROPERTIES_FILE);

        frame = new CustomFrame(Constants.MAIN_FRAME_TITLE);
        frame.setJMenuBar(
                new String[][]{
                        {
                                "File",
                                "Process Excel",
                                null,
                                "Exit",
                        },
                        {"Help", "About"},
                },
                this,
                ReflectionUtils.getMethod("handle", Throwable.class),

                ReflectionUtils.getMethod("processExcel"),
                ReflectionUtils.getMethod("actionOnExit"),

                ReflectionUtils.getMethod("actionOnAbout")
        );

        helper = new AccountingHelper();
        console = new Console(frame);

        frame.setMinimumSize(new Dimension(800, 600));
        frame.setContentPane(console);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                actionOnExit();
            }
        });

    }

    public void actionOnExit() {
        System.exit(0);
    }

    public void actionOnAbout() throws Exception {
        AboutDialogPanel panel = new AboutDialogPanel(Constants.ABOUT_DIALOG_TEXT,
                ImageIO.read(IOUtils.getResource(Constants.LOGO_FILE)));
        panel.init();
        panel.showDialog(frame);
    }

    public void handle(Throwable e) {
        e = ExceptionUtils.retrieveCause(e);
        if (e instanceof CustomCheckFailureException) {
            JOptionPane.showMessageDialog(frame, e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error occurred when processing your command!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static final class CustomCheckFailureException extends RuntimeException {
        CustomCheckFailureException(String message) {
            super(message);
        }
    }

    //-----------------------------------------------------------------------------------
    /*
    Main Menu Controllers
     */

    public void processExcel() throws Exception {

        File file = openFileByProperty(Constants.KEY_FILE_PATH, "Accounting Excel", "xls", "xlsx");
        if (file == null) {
            return;
        }

        helper.process(file, console);

    }

    //-----------------------------------------------------------------------------------

    private File openFileByProperty(String key, String desc, String... ext) {
        return openFileByProperty(key, desc, false, ext);
    }

    private File openFileByProperty(String key, String desc, boolean ensureExtension, String... ext) {

        String path = PropertyManager.getInstance().getProperty(key, ".");
        File file = GraphicUtils.chooseFile(frame, path, JFileChooser.FILES_ONLY, ensureExtension, desc, ext);

        if (file != null) {
            PropertyManager.getInstance().setProperty(key, file.getParent());
        }

        return file;

    }

}
