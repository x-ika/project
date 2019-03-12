package ika.games.base;

public interface RoomAction extends Identifiable {
    static RoomAction choose(int id, RoomAction[]... actionSets) {
        return (RoomAction) Identifiable.choose(id, null, actionSets);
    }
}
