package ika.games.base.controller;

import ika.games.base.*;
import ika.games.base.controller.action.UserActionParam;

public interface Room {

    int getId();

    int getRoundId(int userId);

    void updateLobby();

    Player createPlayer(User user);

    Result handleAction(GameAction action, Player actor, UserActionParam param) throws Exception;


    void startWorking();

    void stopWorking(boolean wait);

    boolean isWorking();

}
