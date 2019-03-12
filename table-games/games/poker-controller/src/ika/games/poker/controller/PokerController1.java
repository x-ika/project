package ika.games.poker.controller;

import com.simplejcode.commons.net.csbase.Message;
import com.simplejcode.commons.net.csbase.MapMessage;
import com.simplejcode.commons.net.sockets.SocketConnection;

import java.util.*;
import java.io.*;

public class PokerController1 {


    //-----------------------------------------------------------------------------------

    private void checkLogin(SocketConnection<Message> socketConnection, MapMessage record) {
        if (socketConnection.getHost() == null) {
            socketConnection.setHost(socketConnection.getRemoteAddress());
        }
        String user = record.getString("user");
        String pass = record.getString("pass");
        Properties p = new Properties();
        try {
            p.load(new FileReader("resources/users.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!p.get(user).equals(pass)) {
            return;
        }
//        for (SocketConnection<Message> c : connections) {
//            if (c.getHost().equals(user)) {
//                return;
//            }
//        }
        socketConnection.setHost(user);
//        sendTo(socketConnection, record);
//        Table first = tables.get("Texas Holdem 0");
//        first.takeAny(first.new Player(socketConnection));
    }

    //-----------------------------------------------------------------------------------

    private void handle(MapMessage record, SocketConnection<Message> socketConnection) {
//        if (record.get("type").equals("txt")) {
//            sendToAll(record);
//        }
//        if (record.get("type").equals("login")) {
//            checkLogin(socketConnection, record);
//        }
//        if (record.get("type").equals("bet")) {
//            Table table = tables.get(record.getString("table"));
//            int bet = record.getInt("bet");
//            if (bet < 0) {
//                table.playerFold((String) socketConnection.getHost());
//            } else {
//                table.playerBet((String) socketConnection.getHost(), bet);
//            }
//        }
    }

}
