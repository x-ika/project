package gui;

import logic.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import java.awt.event.*;
import java.awt.*;

class GraphTree extends JTree
    implements ActionListener, MouseListener, TreeSelectionListener {

    private static final String ADD_VERTEX = "Add Vertex";

    private static final String DELETE_G = "Delete";

    private static final String RENAME_G = "Rename";

    private static final String COPY_G = "Copy";

    private static final String CUT_G = "Cut";

    private static final String RENAME_V = "Rename";

    private static final String DELETE_V = "Delete";

    private static final String COPY_V = "Copy";

    private static final String CUT_V = "Cut";

    private JPopupMenu graphMenu;

    private JPopupMenu vertexMenu;

    private Object selected;

    GraphTree(DefaultMutableTreeNode root) {
        super(root);
        addMouseListener(this);
        addTreeSelectionListener(this);
        setCellEditor(new DefaultTreeCellEditor(this, new DefaultTreeCellRenderer()));
        setCellRenderer(new DefaultTreeCellRenderer());
        graphMenu = createGraphMenu();
        vertexMenu = createVertexMenu();
    }

    private JPopupMenu createGraphMenu() {
        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(createMenuItem(ADD_VERTEX));
        popupMenu.add(createMenuItem(RENAME_G));
        popupMenu.add(createMenuItem(DELETE_G));
        popupMenu.add(createMenuItem(COPY_G));
        popupMenu.add(createMenuItem(CUT_G));
        return popupMenu;
    }

    private JPopupMenu createVertexMenu() {
        final JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.add(createMenuItem(RENAME_V));
        popupMenu.add(createMenuItem(DELETE_V));
        popupMenu.add(createMenuItem(COPY_V));
        popupMenu.add(createMenuItem(CUT_V));
        return popupMenu;
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

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if(selected instanceof Graph) {
            Graph g = (Graph)selected;
            if(command.equals(ADD_VERTEX)) {
                g.addVertex(100, 100);
            } else
            if(command.equals(DELETE_G)) {
            } else
            if(command.equals(RENAME_G)) {
                g.setName("Super Graph");
            } else
            if(command.equals(COPY_G)) {
            }
            if(command.equals(CUT_G)) {
            }
        } else
        if(selected instanceof Vertex) {
            Vertex v = (Vertex)selected;
            if(command.equals(RENAME_V)) {
                v.setName("Super Vertex");
            } else
            if(command.equals(DELETE_V)) {
            } else
            if(command.equals(COPY_V)) {
            } else
            if(command.equals(CUT_V)) {
            }
        }
    }

    public void mousePressed(MouseEvent e) {
        if(e.getButton() == 3) {
            if(selected instanceof Graph) {
                graphMenu.show(this, e.getX(), e.getY());
            } else
            if(selected instanceof Vertex) {
                vertexMenu.show(this, e.getX(), e.getY());
            }
        }
    }

    public void valueChanged(TreeSelectionEvent e) {
        selected = ((DefaultMutableTreeNode)e.getPath().getLastPathComponent()).getUserObject();
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
