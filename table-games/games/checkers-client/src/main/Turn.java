package main;

import java.util.List;

public class Turn {
    public Checker first, last;
    public List<? extends Checker> killed;

    public Turn(Checker first, Checker last, List<? extends Checker> killed) {
        this.first = first;
        this.last = last;
        this.killed = killed;
    }
}
