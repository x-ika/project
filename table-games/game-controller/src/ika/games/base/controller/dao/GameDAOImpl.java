package ika.games.base.controller.dao;

import com.simplejcode.commons.misc.DynamicStruct;
import ika.games.base.controller.Constants;
import ika.games.base.controller.ProvablyFair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ika.games.base.*;

public class GameDAOImpl implements GameDAO {

    private String fileName;

    private DynamicStruct game;
    private Map<Integer, DynamicStruct> rooms;

    public GameDAOImpl(String fileName) throws IOException {
        this.fileName = fileName;

        game = new DynamicStruct();
        rooms = new HashMap<>();

        BufferedReader in = new BufferedReader(new FileReader(fileName));

        for (String s; (s = in.readLine()) != null; ) {
            if (s.trim().isEmpty()) {
                continue;
            }
            String[] p = s.split("=");
            if (s.startsWith("ROOM")) {
                DynamicStruct struct = new DynamicStruct();
                for (String ins : p[1].trim().split(",")) {
                    String[] inp = ins.split(":");
                    struct.tryput(inp[0], inp[1]);
                }
                rooms.put(struct.getInt(Constants.ID), struct);
            } else {
                game.tryput(p[0].trim(), p.length == 1 ? "" : p[1].trim());
            }
        }
        in.close();
    }

    public DynamicStruct getGame(int gameId) {
        return game.copy();
    }

    public DynamicStruct getRoom(int gameId, int roomId) {
        return rooms.get(roomId);
    }

    public DynamicStruct getUser(int gameId, int userId) {
        return new DynamicStruct();
    }

    //-----------------------------------------------------------------------------------

    public long updateGame(int gameId, String key, String value) {
        return 0;
    }

    public long updateRoom(int gameId, int roomId, String key, String value) {
        return 0;
    }

    public long updateUser(int gameId, int userId, String key, String value) {
        return 0;
    }

    public Collection<DynamicStruct> getRooms(int gameId, int type) {
        return rooms.values();
    }

    public int getLastRoundId(int gameId, int roomId) {
        return 0;
    }

    //-----------------------------------------------------------------------------------

    public long logAction(int gameId, int roomId, int userId, int roundId, GameAction action, Result result, Object[] params) {
        return 0;
    }

    public long updateAction(long actionId, Result result) {
        return 0;
    }

    public long logProvablyFair(ProvablyFair pf) {
        return 0;
    }

    public void close() throws IOException {
    }

}
