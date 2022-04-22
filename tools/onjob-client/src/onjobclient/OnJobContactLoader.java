package onjobclient;

import com.simplejcode.commons.gui.Console;
import com.simplejcode.commons.gui.*;
import com.simplejcode.commons.misc.util.*;
import org.apache.poi.ss.usermodel.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class OnJobContactLoader {

    private CustomFrame frame;

    private Properties settings;

    private void init() throws Exception {
        frame = new CustomFrame("OnJob Contact Loader");
        frame.setJMenuBar(
                new String[][]{
                        {
                                "File",
                                "Load File",
                                "Add Contacts EN",
                                "Add Contacts GE",
                                null,
                                "Exit"
                        },
                        {"Help", "About"},
                },
                this,
                ReflectionUtils.getMethod("handle", Exception.class),
                ReflectionUtils.getMethod("loadFile"),
                ReflectionUtils.getMethod("addContactsEn"),
                ReflectionUtils.getMethod("addContactsGe"),
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
                "OnJob", ImageIO.read(IOUtils.getResource("resources/logo.jpg")));
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

    private static Map<String, String> translit;

    static {
        translit = new HashMap<>();
        String pattern = "a b g d e v,w z t i k' l m n o p' zh r s t' u p,f k gh q sh ch c,ts dz ts' ch' x,kh j h";
        String[] t = pattern.split(" ");
        for (int i = 0; i < t.length; i++) {
            for (String s : t[i].split(",")) {
                char geo = (char) ('ა' + i);
                translit.put(s, "" + geo);
            }
        }
    }

    private static String translit(String en) {
        StringBuilder geo = new StringBuilder();
        int n = en.length();
        for (int i = 0; i < n; i++) {
            if (i < n - 1 && translit.containsKey(en.substring(i, i + 2))) {
                geo.append(translit.get(en.substring(i, i + 2)));
                i++;
                continue;
            }
            if (translit.containsKey(en.substring(i, i + 1))) {
                geo.append(translit.get(en.substring(i, i + 1)));
            }
        }
        return geo.toString();
    }

    private List<String[]> data;

    public void loadFile() throws Exception {

        reloadSettings();
        String path = settings.getProperty("path", ".");
        File file = GraphicUtils.chooseFile(frame, path, JFileChooser.FILES_ONLY, "Excel Files", "xls", "xlsx");

        if (file == null) {
            return;
        }

        settings.put("path", file.getParent());
        saveSettings();

        // load file here
        Workbook workbook = WorkbookFactory.create(new FileInputStream(file));
        Sheet sheet = workbook.getSheetAt(0);

        data = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || row.getCell(0) == null) {
                continue;
            }
            String name = row.getCell(0).getStringCellValue();
            String mobile;
            try {
                mobile = "" + (long) row.getCell(1).getNumericCellValue();
            } catch (Exception e) {
                mobile = row.getCell(1).getStringCellValue().replaceAll(" ", "");
            }
            if (mobile.startsWith("995")) {
                mobile = mobile.substring(3);
            }
            if (mobile.startsWith("5") && mobile.length() == 9) {
                data.add(new String[]{name, mobile});
            }
        }

        JOptionPane.showMessageDialog(frame, "Command complete", "Info", JOptionPane.INFORMATION_MESSAGE);

    }

    public void addContactsEn() throws Exception {
        addContacts(true);
    }

    public void addContactsGe() throws Exception {
        addContacts(false);
    }

    private void addContacts(boolean en) throws Exception {

        List<Callable<Void>> tasks = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            String[] contact = data.get(i);
            final int rowNum = i + 1;
            tasks.add(() -> {
                addContact(contact, rowNum, en);
                return null;
            });
        }

        Executors.newFixedThreadPool(1).invokeAll(tasks);

        JOptionPane.showMessageDialog(frame, "Command complete", "Info", JOptionPane.INFORMATION_MESSAGE);

    }

    private void addContact(String[] contact, int rowNum, boolean en) {
        try {

            String name = contact[0];
            String mobile = contact[1];

            String first = name.toLowerCase().split(" ")[1];
            String last = name.toLowerCase().split(" ")[0];

            if (en) {
                first = translit(first);
                last = translit(last);
            }

            int code = OnJobClient.add(first, last, mobile, settings.getProperty("cookie"));
            console.writeLine(String.format("%05d %05d | %-10s | %s", rowNum, code, mobile, first + " " + last));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------------

    public static void main(String[] args) throws Exception {
        new OnJobContactLoader().init();

    }

}
