import com.simplejcode.commons.gui.Console;
import com.simplejcode.commons.gui.*;
import com.simplejcode.commons.misc.util.*;
import proc.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class ExcelReader {

    private CustomFrame frame;

    private Properties settings;

    private void init() throws Exception {
        frame = new CustomFrame("Work Time Calendar");
        frame.setJMenuBar(
                new String[][]{
                        {
                                "File",
                                "Load File",
                                "Export",
                                null,
                                "Exit"
                        },
                        {"Help", "About"},
                },
                this,
                ReflectionUtils.getMethod("handle", Exception.class),

                ReflectionUtils.getMethod("loadFile"),
                ReflectionUtils.getMethod("export"),
                ReflectionUtils.getMethod("actionOnExit"),

                ReflectionUtils.getMethod("actionOnAbout")
        );

        console = new Console(frame);
        frame.setContentPane(console);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }

    public void actionOnExit() {
        System.exit(0);
    }

    public void actionOnAbout() throws Exception {
        AboutDialogPanel aboutDialogPanel = new AboutDialogPanel(
                "Stats", ImageIO.read(IOUtils.getResource("resources/logo.jpg")));
        aboutDialogPanel.init();
        aboutDialogPanel.showDialog(frame);
    }

    public void handle(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(frame, "Error occurred when processing your command!", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void reloadSettings() throws IOException {
        settings = new Properties();
        settings.load(new FileReader("resources/init.properties"));
    }

    private void saveSettings() throws IOException {
        settings.store(new FileOutputStream("resources/init.properties"), "");
    }

    //-----------------------------------------------------------------------------------

    private Console console;

    private List<InOutRecord> data;

    public void loadFile() throws Exception {

        reloadSettings();
        String path = settings.getProperty("path", ".");
        File file = GraphicUtils.chooseFile(frame, path, JFileChooser.FILES_ONLY, "Excel Files", "xls", "xlsx");

        if (file == null) {
            return;
        }

        settings.put("path", file.getParent());
        saveSettings();

        data = DataParser.readData(file);

        JOptionPane.showMessageDialog(frame, "Command complete", "Info", JOptionPane.INFORMATION_MESSAGE);

    }

    public void export() throws Exception {

        reloadSettings();
        String path = settings.getProperty("path", ".");
        File file = GraphicUtils.chooseFile(frame, path, JFileChooser.FILES_ONLY, true, "Excel Files", "xls", "xlsx");

        if (file == null) {
            return;
        }

        settings.put("path", file.getParent());
        saveSettings();

        ArrayList<DayRecord> list = DataParser.processData(data);
        new ExcelIO().export(list, file);

        JOptionPane.showMessageDialog(frame, "Command complete", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    //-----------------------------------------------------------------------------------

    public static void main(String[] args) throws Exception {
        new ExcelReader().init();

    }

}
