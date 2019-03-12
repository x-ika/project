package ika.games.poker.client;

import ika.games.base.client.GameClient;

public class PokerClient extends GameClient {

    public PokerClient() {
        super(2, "POKER");
    }

    protected void handle(byte[] record) {
//        if (record.get("type").equals("txt")) {
//            Color color = userName.equals(record.get("sender")) ? Color.blue : Color.black;
//            chatPanel.writeText(record.getString("content"), color);
//        }
//        if (record.get("type").equals("login") && record.get("user").equals(userName)) {
//            loginSuccessful();
//        }
//        if (record.get("type").equals("action_sit") && record.get("result").equals("success")) {
//
//            String tableName = record.getString("table", "name");
//
//            PokerRoom ptable = new PokerRoom();
//            loadFrame(tableName, ptable);
//            openedTables.put(tableName, ptable);
//
//            ptable.setPlace(record.getInt("place"));
//
//        }
//        if (record.get("type").equals("table_state")) {
//            String tableName = record.getString("table", "name");
//            openedTables.get(tableName).updateState(record);
//        }
//        if (record.get("type").equals("action_play")) {
//            String tableName = record.getString("table", "name");
//            openedTables.get(tableName).play();
//        }
    }

    public static void main(String[] args) {
        client = new PokerClient();
    }

}
