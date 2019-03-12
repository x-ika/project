package tetris.logic;

public class PortEvent {

    public static final int TYPE_MAKE_TURN = 1;
    public static final int TYPE_GAME_OVER = 2;

    public final int type;
    public final BitRectangle board;
    public final Pile pile;

    public PortEvent(int type, BitRectangle board, Pile pile) {
        this.type = type;
        this.board = board;
        this.pile = pile;
    }
}
