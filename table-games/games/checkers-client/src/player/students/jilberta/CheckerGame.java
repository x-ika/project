package player.students.jilberta;

import main.*;

import java.util.ArrayList;

public class CheckerGame implements Player {
    public void gameOver() {
    }

    private static Board board;
    private static final int WHITE = 1;
    private static final int BLACK = 2;
    private static final int WHITEKING = 4;
    private static final int BLACKKING = 5;
    private static final int EMPTY = 0;
    private static final int N = 8;


    public Turn makeTurne(int[][] d, int value) {
        Turn myTurn = null;
        initBoard(d);
        int color = value;
        Board brd = null;
        Board mda = null;
        Move mv = null;

        brd = board.copy();
        brd.getCameFrom().clear();
        mda = miniMaxAB(brd, 9, color, minusInfinity(), plusInfinity());
        mv = mda.getCameFrom().get(0);

        myTurn = new Turn(mv.getFrom(), mv.getTo(), mv.getKilledChecks());

        return myTurn;
    }

    private static Board move(Board brd, Move mv, int color) {
        if (mv.isJump())
            return jump(brd, mv, color);
        else {
            Board newBoard = brd.copy();
            Move newMove = mv.copy();
            moveJChecker(newMove.from, newMove.to, newBoard);
            newBoard.addMoveToCameFrom(mv);
            return newBoard;
        }
    }

    private static Board jump(Board brd, Move mv, int color) {
        Board newBoard = brd.copy();
        Move newMove = mv.copy();
        if (brd.getJChecker(newMove.from).isKing()) {
            JChecker from = newMove.from.copy();
            JChecker to = newMove.to.copy();
            if (from.col - to.col > 0) {
                if (from.row - to.row > 0) {
                    from = to.downRightJump().copy();
                } else {
                    from = to.downLeftJump().copy();
                }
            } else {
                if (from.row - to.row > 0) {
                    from = to.upRightJump().copy();
                } else {
                    from = to.upLeftJump().copy();
                }
            }
            mv.saveKilledChecks(killJChecker(from, to));
            newBoard.removeJChecker(killJChecker(from, to));
        } else {
            mv.saveKilledChecks(killJChecker(newMove.from, newMove.to));
            newBoard.removeJChecker(killJChecker(newMove.from, newMove.to));
        }
        moveJChecker(newMove.from, newMove.to, newBoard);
        newBoard.addMoveToCameFrom(mv);

        ArrayList<Move> moves = new ArrayList<>();
        findJumps(newMove.from, newBoard, moves, color);
        if (moves.size() == 0) {
            return newBoard;
        } else {
            if (moves.size() == 1) {
                return jump(newBoard, moves.get(0), color);
            } else {
                ArrayList<Board> bList = new ArrayList<>();
                for (int i = 0; i < moves.size(); i++) {
                    bList.add(jump(newBoard, moves.get(i), color));
                }
                Board best = bList.get(0);
                for (int i = 1; i < bList.size(); i++) {
                    if (color == WHITE)
                        best = maxScoreBoard(best, bList.get(i));
                    else
                        best = minScoreBoard(best, bList.get(i));
                }
                return best;
            }
        }
    }

    public static Board miniMaxAB(Board brd, int rec, int playerColor, Board alpha, Board beta) {
        if (rec > 0) {
            ArrayList<Move> moves = null;
            moves = findAllMoves(brd, playerColor);
            if (moves.size() == 0)
                return brd;
            if (playerColor == BLACK) {
                for (int i = 0; i < moves.size(); i++) {
                    Board next = miniMaxAB(move(brd, moves.get(i), playerColor), rec - 1, user(playerColor), alpha, beta);
                    beta = minScoreBoard(beta, next);
                    if (alpha.heuristic() >= beta.heuristic()) {
                        return alpha;
                    }
                }
                return beta;
            } else {
                for (int i = 0; i < moves.size(); i++) {
                    Board next = miniMaxAB(move(brd, moves.get(i), playerColor), rec - 1, user(playerColor), alpha, beta);
                    alpha = maxScoreBoard(alpha, next);
                    if (alpha.heuristic() >= beta.heuristic())
                        return beta;
                }
                return alpha;
            }
        } else
            return brd;
    }

    public static Board plusInfinity() {
        JChecker[][] mas = new JChecker[8][8];
        Board b = new Board(mas);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JChecker whiteKing = new JChecker(j, i, WHITE);
                whiteKing.makeKing();
                b.setJChecker(whiteKing, new JChecker(j, i, EMPTY));
            }
        }
        return b;
    }

    public static Board minusInfinity() {
        JChecker[][] mas = new JChecker[8][8];
        Board b = new Board(mas);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JChecker blackKing = new JChecker(j, i, BLACK);
                blackKing.makeKing();
                b.setJChecker(blackKing, new JChecker(j, i, EMPTY));
            }
        }
        return b;
    }

    public static Board minScoreBoard(Board a, Board b) {
        if (a.heuristic() <= b.heuristic())
            return a;
        else
            return b;
    }

    public static Board maxScoreBoard(Board a, Board b) {
        if (a.heuristic() >= b.heuristic())
            return a;
        else
            return b;
    }

    private static ArrayList<Move> findAllMoves(Board brd, int color) {
        boolean checkJump = false;
        ArrayList<Move> moves = new ArrayList<>();
        JChecker temp = null;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (brd.getJChecker(new JChecker(j, i, 0)) != null) {
                    temp = brd.getJChecker(new JChecker(j, i, 0)).copy();
                    if ((brd.getJChecker(temp).getColor() != EMPTY) &&
                            (brd.getJChecker(temp).getColor() == color))
                    {
                        if (checkJump)
                            findJumps(brd.getJChecker(temp), brd, moves, color);
                        else if (findMoves(brd.getJChecker(temp), brd, moves, color))
                            checkJump = true;
                    }
                }
            }
        }
        if (checkJump) {
            removeNonJumpMoves(moves);
        }
        return moves;
    }

    private static void removeNonJumpMoves(ArrayList<Move> moves) {
        ArrayList<Move> mv = new ArrayList<>();
        Move curr = null;
        for (int i = 0; i < moves.size(); i++) {
            curr = moves.get(i);
            if (!curr.isJump())
                mv.add(curr);
        }
        for (int i = 0; i < mv.size(); i++) {
            moves.remove(mv.get(i));
        }
    }

    private static boolean findMoves(JChecker ch, Board brd, ArrayList<Move> moves, int color) {
        if (!findJumps(ch, brd, moves, color)) {
            if (color == BLACK) {
                if (ch.isKing()) {
                    findKingMoves(ch, brd, moves, color);
                    return false;
                } else {
                    if (validBlackMove(ch, ch.downLeftMove(), brd))
                        moves.add(new Move(ch, ch.downLeftMove(), false));
                    if (validBlackMove(ch, ch.downRightMove(), brd))
                        moves.add(new Move(ch, ch.downRightMove(), false));
                    return false;
                }
            } else {
                if (ch.isKing()) {
                    findKingMoves(ch, brd, moves, color);
                    return false;
                } else {
                    if (validWhiteMove(ch, ch.upLeftMove(), brd))
                        moves.add(new Move(ch, ch.upLeftMove(), false));
                    if (validWhiteMove(ch, ch.upRightMove(), brd))
                        moves.add(new Move(ch, ch.upRightMove(), false));
                    return false;
                }
            }
        } else
            return true;
    }

    private static void findKingMoves(JChecker ch, Board board, ArrayList<Move> moves, int color) {
        findKingMovesDownLeft(ch, ch.copy(), board, moves, color);
        findKingMovesDownRight(ch, ch.copy(), board, moves, color);
        findKingMovesUpLeft(ch, ch.copy(), board, moves, color);
        findKingMovesUpRight(ch, ch.copy(), board, moves, color);
    }

    private static void findKingMovesDownLeft(JChecker ch, JChecker next, Board board, ArrayList<Move> moves, int color) {
        Board brd = null;
        if (validKingMove(next, next.downLeftMove(), board)) {
            moves.add(new Move(ch, next.downLeftMove(), false));
            brd = board.copy();
            moveJChecker(next, next.downLeftMove(), brd);
            findKingMovesDownLeft(ch, next, brd, moves, color);
        } else {
            return;
        }
    }

    private static void findKingMovesDownRight(JChecker ch, JChecker next, Board board, ArrayList<Move> moves, int color) {
        Board brd = null;
        if (validKingMove(next, next.downRightMove(), board)) {
            moves.add(new Move(ch, next.downRightMove(), false));
            brd = board.copy();
            moveJChecker(next, next.downRightMove(), brd);
            findKingMovesDownRight(ch, next, brd, moves, color);
        } else {
            return;
        }
    }

    private static void findKingMovesUpRight(JChecker ch, JChecker next, Board board, ArrayList<Move> moves, int color) {
        Board brd = null;
        if (validKingMove(next, next.upRightMove(), board)) {
            moves.add(new Move(ch, next.upRightMove(), false));
            brd = board.copy();
            moveJChecker(next, next.upRightMove(), brd);
            findKingMovesUpRight(ch, next, brd, moves, color);
        } else {
            return;
        }
    }

    private static void findKingMovesUpLeft(JChecker ch, JChecker next, Board board, ArrayList<Move> moves, int color) {
        Board brd = null;
        if (validKingMove(next, next.upLeftMove(), board)) {
            moves.add(new Move(ch, next.upLeftMove(), false));
            brd = board.copy();
            moveJChecker(next, next.upLeftMove(), brd);
            findKingMovesUpLeft(ch, next, brd, moves, color);
        } else {
            return;
        }
    }


    private static boolean validKingMove(JChecker from, JChecker to, Board board) {
        return (validUpMove(from, to, board) || validDownMove(from, to, board));
    }

    private static boolean findJumps(JChecker ch, Board brd, ArrayList<Move> moves, int color) {
        boolean check = false;
        if (color == BLACK) {
            if (ch.isKing()) {
                if (findKingJumps(ch, brd, moves, color)) {
                    check = true;
                }
                return check;
            } else {
                if (validBlackJump(ch, ch.downLeftJump(), brd)) {
                    moves.add(new Move(ch, ch.downLeftJump(), true));
                    check = true;
                }
                if (validBlackJump(ch, ch.downRightJump(), brd)) {
                    moves.add(new Move(ch, ch.downRightJump(), true));
                    check = true;
                }
                if (validBlackJump(ch, ch.upLeftJump(), brd)) {
                    moves.add(new Move(ch, ch.upLeftJump(), true));
                    check = true;
                }
                if (validBlackJump(ch, ch.upRightJump(), brd)) {
                    moves.add(new Move(ch, ch.upRightJump(), true));
                    check = true;
                }
                return check;
            }
        } else {
            if (ch.isKing()) {
                if (findKingJumps(ch, brd, moves, color)) {
                    check = true;
                }
                return check;
            } else {
                if (validWhiteJump(ch, ch.upLeftJump(), brd)) {
                    moves.add(new Move(ch, ch.upLeftJump(), true));
                    check = true;
                }
                if (validWhiteJump(ch, ch.upRightJump(), brd)) {
                    moves.add(new Move(ch, ch.upRightJump(), true));
                    check = true;
                }
                if (validWhiteJump(ch, ch.downLeftJump(), brd)) {
                    moves.add(new Move(ch, ch.downLeftJump(), true));
                    check = true;
                }
                if (validWhiteJump(ch, ch.downRightJump(), brd)) {
                    moves.add(new Move(ch, ch.downRightJump(), true));
                    check = true;
                }
                return check;
            }
        }
    }

    private static boolean findKingJumps(JChecker ch, Board board, ArrayList<Move> moves, int color) {
        if (findKingJumpUpLeft(ch, ch.copy(), ch.copy(), board, moves, color)
                || findKingJumpUpRight(ch, ch.copy(), ch.copy(), board, moves, color)
                || findKingJumpDownLeft(ch, ch.copy(), ch.copy(), board, moves, color)
                || findKingJumpDownRight(ch, ch.copy(), ch.copy(), board, moves, color))
        {
            return true;
        }
        return false;
    }

    private static boolean findKingJumpUpLeft(JChecker ch, JChecker next, JChecker prev, Board board, ArrayList<Move> moves, int color) {
        Board brd = null;
        if (validKingMove(next, next.upLeftMove(), board)) {
            brd = board.copy();
            prev = next.upLeftMove().copy();
            moveJChecker(next, next.upLeftMove(), brd);
            findKingJumpUpLeft(ch, next, prev, brd, moves, color);
        } else {
            if (checkBounds(next.upLeftMove()) && checkBounds(prev) && checkBounds(prev.upLeftJump())) {
                next = board.getJChecker(next.upLeftMove()).copy();
                if (checkBounds(next) && (next != null) && validKingJump(board.getJChecker(prev), board.getJChecker(prev.upLeftJump()), board)) {
                    moves.add(new Move(ch, prev.upLeftJump(), true));
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean findKingJumpUpRight(JChecker ch, JChecker next, JChecker prev, Board board, ArrayList<Move> moves, int color) {
        Board brd = null;
        if (validKingMove(next, next.upRightMove(), board)) {
            brd = board.copy();
            prev = next.upRightMove().copy();
            moveJChecker(next, next.upRightMove(), brd);
            findKingJumpUpRight(ch, next, prev, brd, moves, color);
        } else {
            if (checkBounds(next.upRightMove()) && checkBounds(prev) && checkBounds(prev.upRightJump())) {
                next = board.getJChecker(next.upRightMove()).copy();
                if (checkBounds(next) && (next != null) && validKingJump(board.getJChecker(prev), board.getJChecker(prev.upRightJump()), board)) {
                    moves.add(new Move(ch, prev.upRightJump(), true));
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean findKingJumpDownRight(JChecker ch, JChecker next, JChecker prev, Board board, ArrayList<Move> moves, int color) {
        Board brd = null;
        if (validKingMove(next, next.downRightMove(), board)) {
            brd = board.copy();
            prev = next.downRightMove().copy();
            moveJChecker(next, next.downRightMove(), brd);
            findKingJumpDownRight(ch, next, prev, brd, moves, color);
        } else {
            if (checkBounds(next.downRightMove()) && checkBounds(prev) && checkBounds(prev.downRightJump())) {
                next = board.getJChecker(next.downRightMove()).copy();
                if (checkBounds(next) && (next != null) && validKingJump(board.getJChecker(prev), board.getJChecker(prev.downRightJump()), board)) {
                    moves.add(new Move(ch, prev.downRightJump(), true));
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean findKingJumpDownLeft(JChecker ch, JChecker next, JChecker prev, Board board, ArrayList<Move> moves, int color) {
        Board brd = null;
        if (validKingMove(next, next.downLeftMove(), board)) {
            brd = board.copy();
            prev = next.downLeftMove().copy();
            moveJChecker(next, next.downLeftMove(), brd);
            findKingJumpDownLeft(ch, next, prev, brd, moves, color);
        } else {
            if (checkBounds(next.downLeftMove()) && checkBounds(prev) && checkBounds(prev.downLeftJump())) {
                next = board.getJChecker(next.downLeftMove()).copy();
                if (checkBounds(next) && (next != null) && validKingJump(board.getJChecker(prev), board.getJChecker(prev.downLeftJump()), board)) {
                    moves.add(new Move(ch, prev.downLeftJump(), true));
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean validKingJump(JChecker from, JChecker to, Board board) {
        int color = board.getJChecker(from).getColor();
        return validUpJump(from, to, color, board) ||
                validDownJump(from, to, color, board);
    }

    private static void userJump(JChecker from, JChecker to, Board board) {
        board.removeJChecker(killJChecker(from, to));
        moveJChecker(from, to, board);
    }

    private static JChecker killJChecker(JChecker from, JChecker to) {
        if ((from.col - to.col) == 2) {
            if ((from.row - to.row) == 2)
                return from.upLeftMove();
            else
                return from.upRightMove();
        } else {
            if ((from.row - to.row) == 2)
                return from.downLeftMove();
            else
                return from.downRightMove();
        }
    }

    private static void moveJChecker(JChecker from, JChecker to, Board board) {
        board.removeJChecker(from);
        board.setJChecker(from, to);
        if ((board.getJChecker(to).isKingRow()) && (!board.getJChecker(to).isKing())) {
            board.getJChecker(to).makeKing();
        }
    }

    private static boolean validBlackMove(JChecker from, JChecker to, Board board) {
        return validDownMove(from, to, board) &&
                (board.getJChecker(from).getColor() == BLACK);
    }

    private static boolean validWhiteMove(JChecker from, JChecker to, Board board) {
        return validUpMove(from, to, board) &&
                (board.getJChecker(from).getColor() == WHITE);
    }

    private static boolean validBlackJump(JChecker from, JChecker to, Board board) {
        return validDownJump(from, to, BLACK, board) || validUpJump(from, to, BLACK, board);
    }

    private static boolean validWhiteJump(JChecker from, JChecker to, Board board) {
        return validUpJump(from, to, WHITE, board) || validDownJump(from, to, WHITE, board);
    }


    private static boolean validUpJump(JChecker from, JChecker to, int color, Board board) {
        boolean check = checkBounds(to) && checkBounds(from) &&
                isFree(to, board) && !isFree(from, board) &&
                (board.getJChecker(from).getColor() == color);
        if (check) {
            if (from.upLeftJump().equals(to))
                return (!isFree(from.upLeftMove(), board) &&
                        ((board.getJChecker(from.upLeftMove()).getColor() == user(color))
                                || (board.getJChecker(from.upLeftMove()).getColor() == userKing(color))));
            else if (from.upRightJump().equals(to))
                return (!isFree(from.upRightMove(), board) &&
                        ((board.getJChecker(from.upRightMove()).getColor() == user(color))
                                || (board.getJChecker(from.upRightMove()).getColor() == userKing(color))));
            else
                return false;
        } else {
            return false;
        }
    }

    private static boolean validDownJump(JChecker from, JChecker to, int color, Board board) {
        boolean check = checkBounds(to) && checkBounds(from) && isFree(to, board)
                && !isFree(from, board)
                && (board.getJChecker(from).getColor() == color);
        if (check) {
            if (from.downLeftJump().equals(to))
                return (!isFree(from.downLeftMove(), board) &&
                        ((board.getJChecker(from.downLeftMove()).getColor() == user(color))
                                || (board.getJChecker(
                                from.downLeftMove()).getColor() == userKing(color))));
            else if (from.downRightJump().equals(to))
                return (!isFree(from.downRightMove(), board) &&
                        ((board.getJChecker(from.downRightMove()).getColor() == user(color))
                                || (board.getJChecker(from.downRightMove()).getColor() == userKing(color))));
            else
                return false;
        } else {
            return false;
        }
    }

    public static int user(int color) {
        if (color == WHITE || color == WHITEKING) {
            return BLACK;
        } else {
            return WHITE;
        }
    }

    public static int userKing(int color) {
        if (color == WHITE || color == WHITEKING) {
            return BLACKKING;
        } else {
            return WHITEKING;
        }
    }

    private static boolean validDownMove(JChecker from, JChecker to, Board board) {
        return checkBounds(to) && checkBounds(from) &&
                isFree(to, board) && !isFree(from, board) &&
                ((from.downLeftMove().equals(to)) || (from.downRightMove().equals(to)));
    }

    private static boolean validUpMove(JChecker from, JChecker to, Board board) {
        return checkBounds(to) && checkBounds(from) &&
                isFree(to, board) && !isFree(from, board) &&
                ((from.upLeftMove().equals(to)) || (from.upRightMove().equals(to)));
    }

    private static boolean isFree(JChecker ch, Board board) {
        if (board.getJChecker(ch).value == EMPTY) return true;
        return false;
    }

    private static boolean checkBounds(JChecker next) {
        if (next.row < 0 || next.row > (N - 1)) return false;
        if (next.col < 0 || next.col > (N - 1)) return false;
        return true;
    }

    private static void initBoard(int[][] d) {
        JChecker[][] mas = new JChecker[N][N];
        JChecker ch = null;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i % 2 == 0 && j % 2 == 1) {
                    if (d[i][j] == 4) {
                        ch = new JChecker(j, i, WHITE);
                        ch.makeKing();
                        mas[i][j] = ch;
                    } else if (d[i][j] == 5) {
                        ch = new JChecker(j, i, BLACK);
                        ch.makeKing();
                        mas[i][j] = ch;
                    } else {
                        ch = new JChecker(j, i, d[i][j]);
                        mas[i][j] = ch;
                    }
                } else if (i % 2 == 1 && j % 2 == 0) {
                    if (d[i][j] == 4) {
                        ch = new JChecker(j, i, WHITE);
                        ch.makeKing();
                        mas[i][j] = ch;
                    } else if (d[i][j] == 5) {
                        ch = new JChecker(j, i, BLACK);
                        ch.makeKing();
                        mas[i][j] = ch;
                    } else {
                        ch = new JChecker(j, i, d[i][j]);
                        mas[i][j] = ch;
                    }
                }
            }
        }
        board = new Board(mas);
    }

    private static int color(int color) {
        int c = color;
        return c;
    }
}
