package ika.games.base;

public interface GameAction extends Identifiable {
    static GameAction choose(int id, GameAction[]... actionSets) {
        return (GameAction) Identifiable.choose(id, BasicGameAction.UNKNOWN, actionSets);
    }
}
