package ika.games.base;

public interface Result extends Identifiable {
    static Result choose(int id, Result[]... resultSets) {
        return (Result) Identifiable.choose(id, null, resultSets);
    }
}
