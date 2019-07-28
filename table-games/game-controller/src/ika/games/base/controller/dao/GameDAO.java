package ika.games.base.controller.dao;

import java.io.Closeable;
import java.util.*;

import com.simplejcode.commons.misc.DynamicStruct;
import ika.games.base.*;

import ika.games.base.controller.ProvablyFair;

public interface GameDAO extends Closeable {

    DynamicStruct getGame(int gameId);

    DynamicStruct getRoom(int gameId, int roomId);

    DynamicStruct getUser(int gameId, int userId);

    //-----------------------------------------------------------------------------------

    long updateGame(int gameId, String key, String value);

    long updateRoom(int gameId, int roomId, String key, String value);

    long updateUser(int gameId, int userId, String key, String value);


    Collection<DynamicStruct> getRooms(int gameId, int type);

    int getLastRoundId(int gameId, int roomId);

    //-----------------------------------------------------------------------------------

    long logAction(int gameId, int roomId, int userId, int roundId, GameAction action, Result result, Object[] params);

    long updateAction(long actionId, Result result);

    long logProvablyFair(ProvablyFair pf);

}
