import com.simplejcode.commons.gui.*;
import com.simplejcode.commons.misc.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Properties;

public class CommandRunner implements ProcessListener {

    private static CommandRunner instance;

    public static CommandRunner getInstance() {
        return instance;
    }

    private CustomFrame frame;

    private void init() throws Exception {
        frame = new CustomFrame("Command Runner");
        frame.setJMenuBar(
                new String[][]{
                        {
                                "File",
                                "Reload Settings",
                                null,
                                "Exit"
                        },
                        {"Help", "About"},
                },
                this,
                ReflectionUtils.getMethod("handle", Throwable.class),

                ReflectionUtils.getMethod("reloadSettings"),
                ReflectionUtils.getMethod("actionOnExit"),

                ReflectionUtils.getMethod("actionOnAbout")
        );

        panels = new CommandPanel[2];
        execs = new ProcessExecutor[2];
        for (int i = 0; i < panels.length; i++) {
            ProcessExecutor exec = execs[i] = new ProcessExecutor();
            CommandPanel view = panels[i] = new CommandPanel();
            view.setActionOnStart((t) -> startCommand(view, exec));
            view.setActionOnEnd((t) -> stopCommand(view, exec));
            exec.setListener(this);
        }

        reloadSettings();

        frame.setMinimumSize(new Dimension(800, 600));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                actionOnExit();
            }
        });

    }

    public void actionOnExit() {
        int option = JOptionPane.showInternalConfirmDialog(
                frame.getContentPane(), "Are you sure you want to exit", "Confirm Exit", JOptionPane.YES_NO_OPTION);

        if (option != JOptionPane.YES_OPTION) {
            return;
        }

        for (ProcessExecutor t : execs) {
            t.stop();
        }
        for (ProcessExecutor t : execs) {
            t.stopAndWait();
        }
        frame.dispose();
        System.exit(0);

    }

    public void actionOnAbout() throws Exception {
        AboutDialogPanel aboutDialogPanel = new AboutDialogPanel(
                "CMD Ext", ImageIO.read(FileSystemUtils.getResource("resources/" + getProperty("logo_name"))));
        aboutDialogPanel.init();
        aboutDialogPanel.showDialog(frame);
    }

    public void handle(Throwable e) {
        try {
            PrintWriter out = new PrintWriter("error-log.txt");
            e.printStackTrace(out);
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        String message = "Error occurred when processing your command!\n\n" +
                "See error-log.txt for more details\n ";
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    //-----------------------------------------------------------------------------------

    private Properties settings;

    private CommandPanel[] panels;
    private ProcessExecutor[] execs;

    public void reloadSettings() throws IOException {
        settings = new Properties();
        settings.load(new FileReader("resources/init.properties"));

        SwingUtilities.invokeLater(this::updateUI);
        for (int i = 0; i < execs.length; i++) {
            execs[i].setCommand(getProperty("task" + (i + 1) + "_command"));
        }
    }

    private void updateUI() {
        try {

            BufferedImage logo = ImageIO.read(FileSystemUtils.getResource("resources/" + getProperty("header_logo_name")));
            ImagePanel logoPanel = new ImagePanel(logo);

            JLabel headerLabel = new JLabel(getProperty("header_text"));
            headerLabel.setFont(createFont("header_font"));
            JPanel aligner = new JPanel(new FlowLayout(FlowLayout.LEFT));
            aligner.add(headerLabel);

            Box header = new Box(BoxLayout.X_AXIS);
            header.setBorder(new EmptyBorder(10, 10, 10, 10));
            header.add(logoPanel);
            header.add(aligner);

            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.add(header);
            for (int i = 0; i < panels.length; i++) {
                String prefix = "task" + (i + 1);
                panels[i].setTitle(createFont("task_title_font"), "Task " + (i + 1), createColor("task_title_background"));
                panels[i].setInputFont(createFont("task_input_font"));
                panels[i].setComment(createFont("task_comment_font"), getProperty(prefix + "_comment"));
                panels[i].setStatusFont(createFont("task_status_font"));
                panels[i].setTaskLogo(ImageIO.read(FileSystemUtils.getResource("resources/" + getProperty(prefix + "_logo"))));
                panel.add(panels[i]);
            }

            frame.getContentPane().removeAll();
            frame.getContentPane().add(panel);
            frame.revalidate();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return settings.getProperty(key);
    }

    public Color createColor(String key) {
        String[] s = getProperty(key).split(",");
        return new Color(Integer.parseInt(s[0]), Integer.parseInt(s[1]), Integer.parseInt(s[2]));
    }

    public Font createFont(String key) {
        String[] s = getProperty(key).split(",");
        int style = 0;
        for (String v : s[1].split("[|]")) {
            switch (v) {
                case "PLAIN":
                    style |= Font.PLAIN;
                    break;
                case "BOLD":
                    style |= Font.BOLD;
                    break;
                case "ITALIC":
                    style |= Font.ITALIC;
                    break;
            }
        }
        return new Font(s[0], style, Integer.parseInt(s[2]));
    }

    private void startCommand(CommandPanel view, ProcessExecutor exec) {
        try {
            exec.start(view.getParameters());
        } catch (Exception e) {
            view.reset();
            handle(e);
        }
    }

    private void stopCommand(CommandPanel view, ProcessExecutor exec) {
        try {
            exec.stop();
        } catch (Exception e) {
            handle(e);
        }
    }

    public void lineRead(ProcessExecutor exec, String s) {
        for (int i = 0; i < execs.length; i++) {
            if (execs[i] == exec) {
                panels[i].setStatus("Running - " + s);
            }
        }
    }

    public void stopped(ProcessExecutor exec) {
        for (int i = 0; i < execs.length; i++) {
            if (execs[i] == exec) {
                panels[i].setStatus("Inactive");
                panels[i].reset();
            }
        }
    }

    //-----------------------------------------------------------------------------------

    public static void main(String[] args) throws Exception {
        instance = new CommandRunner();
        instance.init();
    }

}
