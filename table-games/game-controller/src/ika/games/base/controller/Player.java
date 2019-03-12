package ika.games.base.controller;

import com.simplejcode.commons.net.sockets.AsynchronousConnection;

public class Player {

    public final boolean local;

    public final User user;
    public final Room owner;
    public AsynchronousConnection connection;

    public Player(boolean local, Room owner, User user) {
        this.local = local;
        this.user = user;
        this.owner = owner;
    }

    public Player(Room owner, User user) {
        this(false, owner, user);
    }

}
