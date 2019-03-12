package ika.games.domino.client;

import com.simplejcode.commons.net.util.*;
import ika.games.base.*;
import ika.games.base.client.GameClient;
import ika.games.domino.base.*;

import ika.games.domino.client.gui.DominoRoom;

public class DominoClient extends GameClient {

    public DominoClient() {
        super(1, "DOMINO");
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
            }

        }

        if (event == MessageTypes.MSG_TYPE_STATE_HEADER) {

            parser.nextInt(); // room id
            String roomName = parser.nextHexString();

            if (!openedTables.containsKey(roomName)) {
                DominoRoom room = new DominoRoom();
                loadTable(roomName, room);
                openedTables.put(roomName, room);
            }

            parser.nextInt(); // receiver id
            parser.nextString(); // name
            parser.nextInt(); // round
            int nPlayers = parser.nextInt();

            int sit = parser.nextInt();
            int playes = parser.nextInt();
            int restTime = parser.nextInt();
            int totalTime = parser.nextInt();

            int[][] stones = new int[nPlayers][];
            String[] users = new String[nPlayers];
            for (int i = 0; i < nPlayers; i++) {
                users[i] = parser.nextHexString();
                int n = parser.nextInt();
                stones[i] = new int[n];
                for (int j = 0; j < stones[i].length; j++) {
                    stones[i][j] = parser.nextInt();
                }
            }
            int[] score = new int[2];
            score[0] = parser.nextInt();
            score[1] = parser.nextInt();
            boolean first = parser.nextInt() == 1;

            int nodes = parser.nextInt();
            Node[] all = new Node[nodes];
            for (int i = 0; i < nodes; i++) {
                all[i] = new Node(parser.nextInt(), parser.nextInt());
            }
            for (int i = 0; i < nodes; i++) {
                for (int j = 0; j < 4; j++) {
                    int id = parser.nextInt();
                    all[i].getNext()[j] = id == -1 ? null : all[id];
                }
            }
            int rn = parser.nextInt();
            Node root = rn == -1 ? null : all[rn];

            DominoTree tree = new DominoTree();
            for (Node node : all) {
                tree.getNodes().add(node);
            }
            tree.setRoot(root);

            ((DominoRoom) openedTables.get(roomName)).update(sit, playes, stones, users, score, restTime, totalTime, first, tree);

        }

    }

    public static void main(String[] args) {
        client = new DominoClient();
    }

}
