package ika.games.base.controller.pool;

public interface IPoolHandler<T> {

    T create() throws Exception;

    void destroy(T object) throws Exception;

}
