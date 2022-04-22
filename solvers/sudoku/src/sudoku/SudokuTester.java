package sudoku;

import com.simplejcode.commons.misc.util.IOUtils;
import tester.*;

import java.util.*;

import com.simplejcode.commons.gui.GraphicUtils;
import sudoku.solver.IkasSolver;
import sudoku.solver.students.EleneAkhvlediani.ElenesSolver;
import sudoku.solver.students.adoli11.SandroSolver;
import sudoku.solver.students.Evgeniormotsadze.Sudoku;
import sudoku.solver.students.GiorgiMatiashvili.MySolver;
import sudoku.solver.students.gkoch.GkochSolver;
import sudoku.solver.students.Gmarg10Solver.Gmarg10Solver;
import sudoku.solver.students.KeshelavaTamari.tamunasSolver;
import sudoku.solver.students.lashagureshidzesudoku.LashasSolver;
import sudoku.solver.students.qetiuridiaassign2.kuridSolver;
import sudoku.solver.students.stsir11solver.stsir11solver;
import sudoku.solver.students.vmask.VatoSolver;
import sudoku.solver.students.gpata11.Gpata11solver;
import sudoku.solver.students.ggigl.GiglemaSolver;

@SuppressWarnings("unchecked")
public class SudokuTester extends AbstractTester<int[][], int[][]> {

    private static class SudokuChecker implements Checker<int[][], int[][]> {

        public boolean isCorrect(int[][] test, int[][] result) {
            if (result.length != 9 || result[0].length != 9) {
                return false;
            }
            int[] a = new int[9];
            int[] b = new int[9];
            int[] c = new int[9];
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (test[i][j] != 0 && test[i][j] != result[i][j]) {
                        return false;
                    }
                    a[i] |= 1 << result[i][j];
                    b[j] |= 1 << result[i][j];
                    c[3 * (i / 3) + j / 3] |= 1 << result[i][j];
                }
            }
            for (int i = 0; i < 9; i++) {
                if (a[i] != 1022 || b[i] != 1022 || c[i] != 1022) {
                    return false;
                }
            }
            return true;
        }

        public int[][] clone(int[][] a) {
            int[][] t = new int[9][];
            for (int i = 0; i < 9; i++) {
                t[i] = a[i].clone();
            }
            return t;
        }
    }

    public SudokuTester() {
        super(new SudokuChecker());
    }

    public void visualize(int[][] board) {
        GraphicUtils.showMatrix(board);
    }

    public static void main(String[] args) throws Exception {

        Class[] classes = {
                IkasSolver.class,
                SandroSolver.class,
                ElenesSolver.class,
                Sudoku.class,
                GiglemaSolver.class,
                MySolver.class,
                GkochSolver.class,
                Gmarg10Solver.class,
                Gpata11solver.class,
                sudoku.solver.students.GventsadzeZura.MySolver.class,
                tamunasSolver.class,
                LashasSolver.class,
                sudoku.solver.students.mkapa.MySolver.class,
                kuridSolver.class,
                sudoku.solver.students.sggol.MySolver.class,
                stsir11solver.class,
                VatoSolver.class,
        };

        List<int[][]> tests = new ArrayList<>();
        for (String s : IOUtils.read("resources/1.txt").split("\n")) {
            int[][] a = new int[9][9];
            for (int i = 0; i < 81; i++) {
                a[i / 9][i % 9] = s.charAt(i) == '.' ? 0 : s.charAt(i) - '0';
            }
            tests.add(a);
        }

        long startTime = System.nanoTime();
        new SudokuTester().test(classes, tests, 10000);
        System.out.println("Time: " + (System.nanoTime() - startTime) / 1e9);

    }

}
