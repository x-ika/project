package ika.games.poker.controller;

import ika.games.base.*;
import ika.games.base.controller.Controller;
import ika.games.base.controller.PersistentObject;
import ika.games.base.controller.Player;
import ika.games.base.controller.Room;
import ika.games.base.controller.action.Logout;
import ika.games.base.controller.action.SitDown;
import ika.games.base.controller.action.StandUp;
import ika.games.base.controller.action.UserActionParam;
import ika.games.base.controller.dao.GameDAO;
import ika.games.base.controller.dao.GameDAOImpl;
//import ika.games.domino.base.DominoAction;
//import ika.games.domino.controller.action.PlayAction;
import com.simplejcode.commons.net.sockets.AsynchronousConnection;
import com.simplejcode.commons.net.util.MessageParser;

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
