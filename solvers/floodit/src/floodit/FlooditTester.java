package floodit;

import com.simplejcode.commons.gui.GraphicUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.File;
import java.util.*;
import java.util.List;

import floodit.solver.*;
import floodit.solver.students.vmask.MySolver;
import tester.Checker;
import tester.AbstractTester;

@SuppressWarnings("unchecked")
public class FlooditTester extends AbstractTester<int[][], int[]> {

    private static class FlooditChecker implements Checker<int[][], int[]> {

        public boolean isCorrect(int[][] test, int[] result) {
            if (result.length != new MySolver().solve(clone(test)).length) {
                return false;
            }
            int[][] p = clone(test);
            for (int c : result) {
                apply(p, c);
            }
            for (int[] a : p) {
                for (int c : a) {
                    if (c != p[0][0]) {
                        return false;
                    }
                }
            }
            return true;
        }

        public int[][] clone(int[][] board) {
            int[][] p = new int[board.length][];
            for (int i = 0; i < p.length; i++) {
                p[i] = board[i].clone();
            }
            return p;
        }

    }

    public FlooditTester() {
        super(new FlooditChecker());
    }

    public synchronized void visualize(int[][] board) {
        try {
            final int n = board.length;
            final int m = board[0].length;
            int max = 0;
            for (int[] a : board) {
                for (int x : a) {
                    max = Math.max(max, x);
                }
            }

            final int c = max;
            final int S = 20;

            JFrame frame = new JFrame();
            frame.setContentPane(new JComponent() {
                protected void paintComponent(Graphics g) {
                    for (int i = 0; i < n; i++) {
                        for (int j = 0; j < m; j++) {
                            g.setColor(new Color(GraphicUtils.color(1d * board[i][j] / c)));
                            g.fillRect(5 + S * j, 5 + S * i, S, S);
                        }
                    }
                }
            });
            frame.getContentPane().addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    synchronized (FlooditTester.this) {
                        FlooditTester.this.notify();
                    }
                }
            });

            frame.setSize(S * m + 20, S * n + 45);
            GraphicUtils.centerOnScreen(frame);
            frame.setVisible(true);

            for (int color : ret) {
                wait();
                apply(board, color);
                frame.repaint();
            }
            wait();
            frame.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void apply(int[][] p, int c) {
        int n = p.length;
        int m = p[0].length;
        boolean[][] b = new boolean[n][m];
        b[0][0] = true;
        int[] dx = {0, 1, 0, -1};
        int[] dy = {1, 0, -1, 0};
        for (int iter = 0; iter < m * n; iter++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (p[i][j] == p[0][0]) {
                        for (int d = 0; d < 4; d++) {
                            int x = i + dx[d];
                            int y = j + dy[d];
                            b[i][j] |= 0 <= x && 0 <= y && x < n && y < m && b[x][y];
                        }
                    }
                }
            }
        }
        for (int iter = 0; iter < m * n; iter++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    if (p[i][j] == c) {
                        for (int d = 0; d < 4; d++) {
                            int x = i + dx[d];
                            int y = j + dy[d];
                            b[i][j] |= 0 <= x && 0 <= y && x < n && y < m && b[x][y];
                        }
                    }
                }
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (b[i][j]) {
                    p[i][j] = c;
                }
            }
        }
    }

    public static void writeTest(int[][] test, String path) throws Exception {
        PrintWriter out = new PrintWriter(path);
        int n = test.length;
        int m = test[0].length;
        out.println(n + " " + m);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                out.print(test[i][j] + " ");
            }
            out.println();
        }
        out.close();
    }

    public static void main(String[] args) throws Exception {

        Class<? extends Solver>[] classes = new Class[] {
//                floodit.solver.students.adoli.MySolver.class,
//                floodit.solver.students.blomi.MySolver.class,
//                floodit.solver.students.eakhv.FloodIt.class,
//                floodit.solver.students.ggigl.QuickSolver.class,
//                floodit.solver.students.gkoch.MySolver.class,
//                floodit.solver.students.gmarg.Solv.class,
//                floodit.solver.students.gpata.MySolver.class,
//                floodit.solver.students.kurid.MySolver.class,
//                floodit.solver.students.lgure.MySolver.class,
//                floodit.solver.students.mkapa11.MySolver.class,
//                floodit.solver.students.ndarj.MySolver.class,
//                floodit.solver.students.sggol.MySolver.class,
//                floodit.solver.students.stsir.MySolver.class,
//                floodit.solver.students.tkesh.solver.class,
                floodit.solver.students.vmask.MySolver.class,
//                floodit.solver.students.zgven.MySolver.class,
        };

        List<int[][]> tests = new ArrayList<>();
        File[] fs = new File("resources/").listFiles();
        for (File f : fs) {
            Scanner in = new Scanner(f);
            int n = in.nextInt();
            int m = in.nextInt();
            int[][] d = new int[n][m];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < m; j++) {
                    d[i][j] = in.nextInt();
                }
            }
            tests.add(d);
            break;
        }
        new FlooditTester().test(classes[0].getConstructor().newInstance(), tests.get(0), 4000, true);
    }

}
