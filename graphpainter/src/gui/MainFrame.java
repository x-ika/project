package gui;

import io.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

import gui.FileChooser;
import gui.GraphPainter;
import gui.LeftPanel;

public class MainFrame extends JFrame implements ActionListener  {
    private static final String NEW = "New";

    private static final String OPEN = "Open";

    private static final String SAVE = "Save";

    private static final String SAVE_AS = "Save As";

    private static final String EXIT = "Exit";

    private static final String A = "Add o";

    private static final String B = "Add -";

    private static final String C = "del o";

    private static final String D = "del -";

    private static final Dimension STANDART = new Dimension(750, 650);

    private File currentFile;

    private JSplitPane sp;

    private GraphPainter graphPainter;

    private LeftPanel leftPanel;

    private MainFrame() {
        super("Graph Painter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setExtendedState(Frame.MAXIMIZED_BOTH);
        setJMenuBar(createMenuBar());
        setContentPane(createContentPane());
        pack();
        setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        fileMenu.add(createMenuItem(NEW, 'N'));
        fileMenu.add(createMenuItem(OPEN, 'O'));
        fileMenu.add(createMenuItem(SAVE, 'S'));
        fileMenu.add(createMenuItem(SAVE_AS, 'A'));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem(EXIT, 'X'));

        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');

        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');

        JMenu toolsMenu = new JMenu("Toos");
        toolsMenu.setMnemonic('T');
        toolsMenu.add(createMenuItem(A));
        toolsMenu.add(createMenuItem(B));
        toolsMenu.add(createMenuItem(C));
        toolsMenu.add(createMenuItem(D));

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    private JMenuItem createMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(this);
        return menuItem;
    }

    private JMenuItem createMenuItem(String text, char ch) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(ch, KeyEvent.CTRL_DOWN_MASK));
        menuItem.addActionListener(this);
        return menuItem;
    }

    private JPanel createContentPane() {
        leftPanel = new LeftPanel(200, 650);
        graphPainter = new GraphPainter(leftPanel, STANDART);
        sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        sp.setLeftComponent(new JScrollPane(leftPanel));
        sp.setRightComponent(new JScrollPane(graphPainter));
        sp.setOneTouchExpandable(true);
        sp.setDividerLocation(210);
        sp.setPreferredSize(new Dimension(1010, 670));
        JPanel contentPanel = new JPanel();
        contentPanel.add(sp);
        return contentPanel;
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if(command.equals(NEW)) {
            currentFile = null;
            graphPainter = new GraphPainter(leftPanel, STANDART);
            sp.setRightComponent(new JScrollPane(graphPainter));
        } else
        if(command.equals(OPEN)) {
            new FileChooser(this, command);
        } else
        if(command.equals(SAVE)) {
            if(currentFile != null) {
                new GraphWriter(currentFile).write(graphPainter);
            } else {
                new FileChooser(this, SAVE_AS);
            }
        } else
        if(command.equals(SAVE_AS)) {
            new FileChooser(this, command);
        }
        if(command.equals(EXIT)) {
            dispose();
        } else
        if(command.equals(A)) {
            //graphPainter.add
        } else
        if(command.equals(B)) {
            //graphPainter.addVerge();
        } else
        if(command.equals(C)) {
            //saveAS();
        } else
        if(command.equals(D)) {
            //exit();
        }
    }

    void choose(File file, String title) {
        if(title.equals(OPEN)) {
            graphPainter = new GraphReader(file).readGraphPainter();
            sp.setRightComponent(new JScrollPane(graphPainter));
            currentFile = file;
        } else
        if(title.equals(SAVE_AS)) {
            new GraphWriter(file).write(graphPainter);
            currentFile = file;
        }
    }

    public static void main(String[] args) {
        new MainFrame();
    }
}
