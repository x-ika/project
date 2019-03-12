package ika.games.domino.controller;

import com.simplejcode.commons.net.sockets.AsynchronousConnection;
import com.simplejcode.commons.net.util.MessageParser;
import ika.games.base.*;
import ika.games.base.controller.*;
import ika.games.base.controller.action.*;

import ika.games.domino.controller.action.PlayAction;

public class DominoController extends Controller {

    public static DominoController createInstance() throws Exception {
        return new DominoController();
    }

    private DominoController() throws Exception {
        super("resources/domino.properties");
        allowGuests = true;
    }

    protected Room createRoom(PersistentObject struct) throws Exception {
        return new DominoLogic(struct, this);
    }

    protected UserActionParam readParam(AsynchronousConnection source, MessageParser parser, Player player, GameAction action) {
        UserActionParam param = super.readParam(source, parser, player, action);
        if (param != null) {
            return param;
        }
        if (action instanceof BasicGameAction) {
            switch ((BasicGameAction) action) {
                case MAKE_MOVE:
                    return new PlayAction(parser.nextInt(), parser.nextInt(), parser.nextInt(), parser.nextInt());
            }
        }
        return null;
    }

}
