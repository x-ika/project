package ika.games.gomoku.controller;

import com.simplejcode.commons.net.sockets.AsynchronousConnection;
import com.simplejcode.commons.net.util.MessageParser;
import ika.games.base.*;
import ika.games.base.controller.*;
import ika.games.base.controller.action.UserActionParam;
import ika.games.gomoku.controller.action.GomokuMove;

public class GomokuController extends Controller {

    public static GomokuController createInstance() throws Exception {
        return new GomokuController();
    }

    private GomokuController() throws Exception {
        super("resources/gomoku.properties");
        allowGuests = true;
    }

    protected Room createRoom(PersistentObject struct) throws Exception {
        return new GomokuLogic(struct, this);
    }

    protected UserActionParam readParam(AsynchronousConnection source, MessageParser parser, Player player, GameAction action) {
        UserActionParam param = super.readParam(source, parser, player, action);
        if (param != null) {
            return param;
        }
        if (action instanceof BasicGameAction) {
            switch ((BasicGameAction) action) {
                case MAKE_MOVE:
                    return new GomokuMove(parser.nextInt(), parser.nextInt());
                case START_ROUND:
                    return new UserActionParam();
            }
        }
        return null;
    }

}
