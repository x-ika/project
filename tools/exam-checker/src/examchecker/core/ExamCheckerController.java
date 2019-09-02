package examchecker.core;

import com.simplejcode.commons.gui.*;
import com.simplejcode.commons.misc.PropertyManager;
import com.simplejcode.commons.misc.util.*;
import examchecker.view.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

import static java.awt.GridBagConstraints.*;

public class ExamCheckerController {

    private static boolean debugMode;

    private static ExamCheckerController instance;

    public static ExamCheckerController getInstance() {
        return instance;
    }

    public static void main(String[] args) throws Exception {
        debugMode = args.length > 0 && args[0].equals("debug");
        (instance = new ExamCheckerController(new ExamCheckerHelper())).init();
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
    private ExamCheckerHelper helper;

    private PaperModelView paperModelView;

    private TestModelView testModelView;


    private ExamCheckerController(ExamCheckerHelper helper) {
        this.helper = helper;
    }

    private void init() throws Exception {
        PropertyManager.setPropertiesFileName(Constants.PROPERTIES_FILE);

        frame = new CustomFrame(Constants.MAIN_FRAME_TITLE);
        frame.setJMenuBar(
                new String[][]{
                        {
                                "File",
                                "Load Paper Model",
                                "Save Paper Model",
                                null,
                                "Load Test Model",
                                "Save Test Model",
                                null,
                                "Exit",
                        },
                        {
                                "Test",
                                "Check Papers",
                                "Rename Papers",
                                "Export To Excel",
                        },
                        {
                                "Tools",
                                "Show Black/White Image",
                                "Debug Paper",
                                "Show Log",
                        },
                        {"Help", "About"},
                },
                this,
                ReflectionUtils.getMethod("handle", Throwable.class),

                ReflectionUtils.getMethod("loadPaperModel"),
                ReflectionUtils.getMethod("exportPaperModel"),
                ReflectionUtils.getMethod("loadTestModel"),
                ReflectionUtils.getMethod("exportTestModel"),
                ReflectionUtils.getMethod("actionOnExit"),

                ReflectionUtils.getMethod("checkPapers"),
                ReflectionUtils.getMethod("renamePapers"),
                ReflectionUtils.getMethod("exportToExcel"),

                ReflectionUtils.getMethod("showBlackWhiteImage"),
                ReflectionUtils.getMethod("debugPaper"),
                ReflectionUtils.getMethod("showLog"),

                ReflectionUtils.getMethod("actionOnAbout")
        );

        frame.getContentPane().setBackground(new JPanel().getBackground());
        frame.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints c1 = new GridBagConstraints(0, 0, 1, 1, 1, 1, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0);
        frame.getContentPane().add(paperModelView = new PaperModelView(), c1);
        GridBagConstraints c2 = new GridBagConstraints(0, 1, 1, 1, 1, 3, CENTER, BOTH, new Insets(0, 0, 0, 0), 0, 0);
        frame.getContentPane().add(testModelView = new TestModelView(), c2);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.pack();

        if (debugMode) {
            helper.readPaperModel(new File("D:\\ika\\projects\\project\\exam-checker\\data\\models\\P15.paper"));
            helper.readTestModel(new File("D:\\ika\\projects\\project\\exam-checker\\data\\models\\T15.test"));
            repaint(paperModelView, helper.getPaperModel());
            repaint(testModelView, helper.getTestModel());
        }

    }

    public void actionOnExit() {
        System.exit(0);
    }

    public void actionOnAbout() throws Exception {
        AboutDialogPanel panel = new AboutDialogPanel(Constants.ABOUT_DIALOG_TEXT,
                ImageIO.read(FileSystemUtils.getResource(Constants.LOGO_FILE)));
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

    public void loadPaperModel() throws Exception {

        File file = openFileByProperty(Constants.KEY_MODEL_PATH, "Paper Models", "paper");
        if (file == null) {
            return;
        }

        helper.readPaperModel(file);

        repaint(paperModelView, helper.getPaperModel());

    }

    public void exportPaperModel() throws Exception {

        checkPaperModel();

        File file = openFileByProperty(Constants.KEY_MODEL_PATH, "Paper Models", true, "paper");
        if (file == null) {
            return;
        }

        helper.exportPaperModel(file);

        JOptionPane.showMessageDialog(frame, "Command complete", "Info", JOptionPane.INFORMATION_MESSAGE);

    }

    public void loadTestModel() throws Exception {

        File file = openFileByProperty(Constants.KEY_MODEL_PATH, "Test Models", "test");
        if (file == null) {
            return;
        }

        helper.readTestModel(file);

        repaint(testModelView, helper.getTestModel());

    }

    public void exportTestModel() throws Exception {

        checkTestModel();

        File file = openFileByProperty(Constants.KEY_MODEL_PATH, "Test Models", true, "test");
        if (file == null) {
            return;
        }

        helper.exportTestModel(file);

        JOptionPane.showMessageDialog(frame, "Command complete", "Info", JOptionPane.INFORMATION_MESSAGE);

    }

    private <T> void repaint(TableView<T> view, T model) {
        SwingUtilities.invokeLater(() -> {
            view.setModel(model);
            view.createContent();
            view.updateTable();
            frame.validate();
        });
    }


    public void checkPapers() throws Exception {

        checkModel();

        File dir = openDirectoryByProperty(Constants.KEY_DATA_PATH);
        if (dir == null || dir.listFiles() == null) {
            return;
        }

        helper.checkPapers(dir.listFiles());

        JOptionPane.showMessageDialog(frame, "Command complete", "Info", JOptionPane.INFORMATION_MESSAGE);

    }

    public void renamePapers() throws Exception {

        checkModel();
        checkResults();

        if (!helper.renamePapers()) {
            throw new CustomCheckFailureException("Some files may not renamed");
        }

        JOptionPane.showMessageDialog(frame, "Command complete", "Info", JOptionPane.INFORMATION_MESSAGE);

    }

    public void exportToExcel() throws Exception {

        checkModel();
        checkResults();

        File file = openFileByProperty(Constants.KEY_RESULTS_PATH, "Excel Files", true, "xls");
        if (file == null) {
            return;
        }

        helper.exportToExcel(file);

        JOptionPane.showMessageDialog(frame, "Command complete", "Info", JOptionPane.INFORMATION_MESSAGE);

    }


    public void showBlackWhiteImage() throws Exception {

        checkModel();

        File file = openFileByProperty(Constants.KEY_DATA_PATH, "Scanned Test");
        if (file == null) {
            return;
        }

        helper.showBlackWhiteImage(file);

    }

    public void debugPaper() throws Exception {

        checkModel();

        File file = openFileByProperty(Constants.KEY_DATA_PATH, "Scanned Test");
        if (file == null) {
            return;
        }

        helper.debugPaper(file);

    }

    public void showLog() throws Exception {

    }

    //-----------------------------------------------------------------------------------

    private void checkModel() {
        checkPaperModel();
        checkTestModel();
    }

    private void checkPaperModel() {
        if (helper.getPaperModel() == null) {
            throw new CustomCheckFailureException("Please load the paper model");
        }
    }

    private void checkTestModel() {
        if (helper.getTestModel() == null) {
            throw new CustomCheckFailureException("Please load the test model");
        }
    }

    private void checkResults() {
        if (helper.getCheckResults() == null) {
            throw new CustomCheckFailureException("Please check the papers");
        }
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

    private File openDirectoryByProperty(String key) {

        String path = PropertyManager.getInstance().getProperty(key, ".");
        File file = GraphicUtils.chooseFile(frame, path, JFileChooser.DIRECTORIES_ONLY);

        if (file != null) {
            PropertyManager.getInstance().setProperty(key, file.getAbsolutePath());
        }

        return file;

    }

}
