package gui;

import logic.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

public class GraphPainter extends JComponent implements
        java.io.Serializable, ActionListener, MouseListener, MouseMotionListener
{
    private LeftPanel leftPanel;

    private Vector<Graph> graphs;

    private Graph currentGraph;

    private Vertex selected;

    public GraphPainter(LeftPanel list, Dimension d) {
        leftPanel = list;
        addMouseListener(this);
        addMouseMotionListener(this);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.white);
        setPreferredSize(d);
        graphs = new Vector<>();
        graphs.add(currentGraph = new Graph());
    }

    public Graph getGraph() {
        return currentGraph;
    }

    public void addGraph(Graph graph) {
        graphs.add(graph);
    }

    public void addVertex(int x, int y) {
        currentGraph.addVertex(x, y);
        leftPanel.setTree(graphs);
        repaint();
    }

    public void addVerge(Vertex v1, Vertex v2) {
        currentGraph.addVerge(v1, v2);
        repaint();
    }

    public GraphPainter(LeftPanel list, int width, int height) {
        this(list, new Dimension(width, height));
    }

    public void paintComponent(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, getSize().width, getSize().height);
        for (Graph graph : graphs) {
            paintGraph(g, graph);
        }
    }

    private void paintGraph(Graphics g, Graph graph) {
        for (Vertex v : graph.getVertexes()) {
            g.setColor(Color.black);
            drawVertex(g, v);
            for (Vertex fv : v.getConnectedVertexs()) {
                g.drawLine(v.x, v.y, fv.x, fv.y);
            }
        }
    }

    private void drawVertex(Graphics g, Vertex v) {
        int r = v.radius();
        g.drawOval(v.x - r, v.y - r, 2 * r, 2 * r);
    }

    public void actionPerformed(ActionEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        int x = e.getX(),
                y = e.getY();
        Vertex v = getVertex(x, y);
        if (selected != null && v != null) {
            addVerge(v, selected);
        }
        selected = v;
        if (e.getClickCount() == 2) {
            addVertex(x, y);
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (selected != null) {
            selected.move(e.getX(), e.getY());
            repaint();
        }
    }

    private Vertex getVertex(int x, int y) {
        for (Vertex v : currentGraph.getVertexes()) {
            if (v.contains(x, y)) {
                return v;
            }
        }
        return null;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
