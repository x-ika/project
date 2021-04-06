package ika.games.joker.server;

import com.simplejcode.commons.net.sockets.AsynchronousConnection;
import com.simplejcode.commons.net.util.MessageParser;
import ika.games.base.*;
import ika.games.base.controller.*;
import ika.games.base.controller.action.*;
import ika.games.joker.server.logic.JokerLogic;

public class JokerController extends Controller {

    public static JokerController createInstance() throws Exception {
        return new JokerController();
    }

    private JokerController() throws Exception {
        super("resources/joker.properties");
    }

    protected Room createRoom(PersistentObject struct) throws Exception {
        return new JokerLogic(struct, this);
    }

    protected Login readLogin(AsynchronousConnection source, MessageParser parser) {
        parser.nextInt(); // gameId
        int roomId = parser.nextInt();
        int userId = parser.nextInt();
        String ses = parser.nextString();
        return new JokerNamespace.JokerLogin(userId, roomId, ses, source.getRemoteAddress(), parser.nextInt());
    }

    protected GameAction readAction(MessageParser parser) {
        return GameAction.choose(parser.nextInt(), BasicGameAction.values(), JokerNamespace.JokerAction.values());
    }

    protected UserActionParam readParam(AsynchronousConnection source, MessageParser parser, Player player, GameAction action) {
        if (action instanceof BasicGameAction) {
            switch ((BasicGameAction) action) {
                case LOG_IN:
                    return readLogin(source, parser);
                case LOG_OUT:
                    return new Logout(source.getRemoteAddress());
            }
        }
        if (action instanceof JokerNamespace.JokerAction) {
            switch ((JokerNamespace.JokerAction) action) {
                case USER_CLAIM:
                    return new JokerNamespace.PlayAction(JokerNamespace.TYPE_CLAIM, null, parser.nextInt(), -1);
                case USER_PLAY_CARD:
                    return new JokerNamespace.PlayAction(JokerNamespace.TYPE_PLAY, parser.nextString(), parser.nextInt(), parser.nextInt());
                case USER_SUIT:
                    return new JokerNamespace.PlayAction(JokerNamespace.TYPE_SUIT, null, -1, parser.nextInt());
                case USER_KICK_BOT:
                    return new UserActionParam();
                case USER_GET_LIST:
                    return new JokerNamespace.GetList(parser.nextInt());
                case USER_TAKE_QUEUE:
                    return new JokerNamespace.PlayerGamePreferences(parser.nextString(), parser.nextString(), parser.nextString(), parser.nextString(), parser.nextString(), parser.nextString());
                case USER_LEAVE_QUEUE:
                    return new UserActionParam();
            }
        }
        return null;
    }

}
