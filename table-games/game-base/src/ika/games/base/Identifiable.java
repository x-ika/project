package ika.games.base;

public interface Identifiable {

    int getId();

    String getName();

    static Identifiable choose(int id, Identifiable def, Identifiable[]... sets) {
        for (Identifiable[] set : sets) {
            for (Identifiable identifiable : set) {
                if (identifiable.getId() == id) {
                    return identifiable;
                }
            }
        }
        return def;
    }

}
