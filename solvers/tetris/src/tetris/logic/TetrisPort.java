package tetris.logic;

public interface TetrisPort {

    void setListener(PortListener listener);

    void start();

    void apply(Turn turn);

}
