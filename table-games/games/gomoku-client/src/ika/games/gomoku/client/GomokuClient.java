package ika.games.gomoku.client;

import ika.games.base.*;
import ika.games.base.client.GameClient;
import com.simplejcode.commons.net.util.ByteMessageParser;
import com.simplejcode.commons.net.util.MessageParser;

public class GomokuClient extends GameClient {

    public GomokuClient() {
        super(3, "GOMOKU");
    }

    protected void handle(byte[] message) {

        MessageParser parser = new ByteMessageParser(message, ',');
        int event = parser.nextInt();

        if (event == MessageTypes.MSG_TYPE_CHAT_MSG) {
            parser.nextInt();
            parser.nextInt();
            int sender = parser.nextInt();
            String name = parser.nextHexString();
            String date = parser.nextHexString();
            String msg = parser.nextHexString();
        }

        if (event == MessageTypes.MSG_TYPE_STATUS) {
            GameAction action = GameAction.choose(parser.nextInt(), BasicGameAction.values());
            Result result = Result.choose(parser.nextInt(), BasicResult.values());

            if (action == BasicGameAction.LOG_IN && result == BasicResult.OK) {
                loginSuccessful();
            }

            if (action == BasicGameAction.SIT_DOWN && result == BasicResult.OK) {
                startRound();
            }

        }

        if (event == MessageTypes.MSG_TYPE_STATE_HEADER) {

            parser.nextInt(); // room id
            String roomName = parser.nextHexString();

            if (!openedTables.containsKey(roomName)) {
                GomokuRoom room = new GomokuRoom(25);
                loadTable(roomName, room);
                openedTables.put(roomName, room);
            }

            parser.nextInt(); // receiver id
            parser.nextString(); // name
            parser.nextInt(); // round
            int nPlayers = parser.nextInt();

            int sit = parser.nextInt();
            int plays = parser.nextInt();
            int restTime = parser.nextInt();
            int totalTime = parser.nextInt();

            String[] users = new String[nPlayers];
            for (int i = 0; i < nPlayers; i++) {
                users[i] = parser.nextHexString();
            }
            int row = parser.nextInt();
            int col = parser.nextInt();
            int[][] desk = new int[row][col];
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    desk[i][j] = parser.nextInt();
                }
            }
            Move[] win = null;
            int winLength = parser.nextInt();
            if (winLength > 0) {
                win = new Move[winLength];
                for (int i = 0; i < winLength; i++) {
                    int r = parser.nextInt();
                    int c = parser.nextInt();
                    win[i] = new Move(r, c);
                }
            }

            ((GomokuRoom) openedTables.get(roomName)).update(desk, null, win);

        }

    }

    public static void main(String[] args) {
        client = new GomokuClient();
    }

}
