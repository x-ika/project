package gui;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.*;
import java.io.File;

public class FileChooser extends JFrame implements ActionListener {
    private final String PATH = "C:\\Documents and Settings\\George Merabishvili\\" +
            "IdeaProjects\\My Projects\\University\\Graph Painter\\Samples";

    private JFileChooser fileChooser;

    private final String APPROVE_SELECTION = "ApproveSelection";

    private final String CANCEL_SELECTION = "CancelSelection";

    private MainFrame frame;

    FileChooser(MainFrame mainFrame, String title) {
        super(title);
        frame = mainFrame;
        fileChooser = new JFileChooser(PATH);
        fileChooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return isRight(f);
            }

            public String getDescription() {
                return "GRP Files";
            }
        });
        fileChooser.setApproveButtonText("Ok");
        fileChooser.addActionListener(this);
        getContentPane().add(fileChooser);
        setLocation(100, 100);
        pack();
        setVisible(true);
    }

    public void actionPerformed(ActionEvent av) {
        String command = av.getActionCommand();
        if (command.equals(APPROVE_SELECTION)) {
            File file = fileChooser.getSelectedFile();
            if (!isRight(file)) {
                file = new File(file.getAbsolutePath() + ".GRP");
            }
            frame.choose(file, getTitle());
            this.dispose();
        } else if (command.equals(CANCEL_SELECTION)) {
            this.dispose();
        }
    }

    private static boolean isRight(File file) {
        String name = file.getName();
        if (name.length() < 4) {
            return false;
        }
        name = name.substring(name.length() - 4);
        return name.equalsIgnoreCase(".grp");
    }
}
