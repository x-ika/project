package ika.games.base.controller.pool;

import com.simplejcode.commons.misc.util.ThreadUtils;

import java.util.*;

public class ObjectPool<T> {

    protected final IPoolHandler<T> handler;

    protected int current;
    protected List<T> data;

    public ObjectPool(IPoolHandler<T> handler) {
        this.handler = handler;
        data = new ArrayList<>();
    }


    public synchronized int getSize() {
        return data.size();
    }

    public synchronized T get() {
        if (current == data.size()) {
            current = 0;
        }
        return data.get(current++);
    }

    public synchronized boolean createAll(int buffer, long timeout, long createDelay) {
        List<T> list = new ArrayList<>();
        long startRepairTime = System.currentTimeMillis();
        for (int i = 0; i < buffer; i++) {
            T object;
            while ((object = safeCreate()) == null) {
                ThreadUtils.sleep(createDelay);
                if (System.currentTimeMillis() - startRepairTime > timeout) {
                    return false;
                }
            }
            list.add(object);
        }
        destroyAll();
        data.addAll(list);
        current = 0;
        return true;
    }

    public synchronized void destroyAll() {
        for (T t : data) {
            safeDestroy(t);
        }
        data.clear();
    }

    private T safeCreate() {
        try {
            return handler.create();
        } catch (Exception e) {
            return null;
        }
    }

    private void safeDestroy(T t) {
        try {
            handler.destroy(t);
        } catch (Exception e) {
            // nothing to do
        }
    }

}
