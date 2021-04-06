package gui;

import logic.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.Vector;

class LeftPanel extends JPanel {
    private GraphTree tree;

    public LeftPanel(Dimension d) {
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.white);
        setPreferredSize(d);
    }

    public LeftPanel(int width, int height) {
        this(new Dimension(width, height));
    }

    public void paintComponent(Graphics g) {
        if (tree == null) {
            return;
        }
        super.paintComponent(g);
    }

    public void setTree(Vector<Graph> graphs) {
        if (tree != null) {
            remove(tree);
        }
        add(tree = createTree(graphs));
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        revalidate();
        repaint();
    }

    private GraphTree createTree(Vector<Graph> graphs) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Graphs");
        for (Graph graph : graphs) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(graph);
            for (Vertex v : graph.getVertexes()) {
                node.add(new DefaultMutableTreeNode(v));
            }
            root.add(node);
        }
        return new GraphTree(root);
    }
}
