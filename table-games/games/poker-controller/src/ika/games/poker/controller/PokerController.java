package ika.games.poker.controller;

import ika.games.base.controller.Controller;
import ika.games.base.controller.PersistentObject;
import ika.games.base.controller.Room;
//import ika.games.domino.base.DominoAction;
//import ika.games.domino.controller.action.PlayAction;


public class PokerController extends Controller {

    public static PokerController createInstance() throws Exception {
        return new PokerController();
    }

    private PokerController() throws Exception {
        super("resources/poker.properties");
        allowGuests = true;
    }

    protected Room createRoom(PersistentObject struct) throws Exception {
//        return new Table(struct, this);
        return null;
    }

}
