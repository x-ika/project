package tetris.logic;

import java.util.*;

public class Turn {

    public final Vector<Command> commands;

    public Turn() {
        commands = new Vector<>();
    }

    public void add(Command command) {
        commands.add(command);
    }

}
