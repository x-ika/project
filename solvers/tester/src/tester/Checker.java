package tester;

public interface Checker<T, R> {
    boolean isCorrect(T test, R result);

    T clone(T t);
}
