package ika.games.base.controller;

import java.util.*;

public class DynamicStruct {

    private final Map<String, Object> map = new HashMap<>();


    public DynamicStruct() {
    }

    public DynamicStruct(Map<String, Object> map) {
        load(map);
    }

    public DynamicStruct(DynamicStruct struct) {
        load(struct);
    }

    //-----------------------------------------------------------------------------------

    public Object put(String key, Object value) {
        return map.put(key, value);
    }

    public Object tryput(String key, Object value) {
        String s = value.toString();
        try {
            return put(key, Integer.parseInt(s));
        } catch (NumberFormatException e) {
            // no problem
        }
        try {
            return put(key, Double.parseDouble(s));
        } catch (NumberFormatException e) {
            // no problem
        }
        return map.put(key, s);
    }

    public <T> Object put(String key, Class<T> type, String value) {
        if (type == int.class) {
            return put(key, Integer.parseInt(value));
        }
        if (type == long.class) {
            return put(key, Long.parseLong(value));
        }
        if (type == double.class) {
            return put(key, Double.parseDouble(value));
        }
        return put(key, value);
    }

    public void remove(String key) {
        map.remove(key);
    }

    public Object get(String key) {
        return map.get(key);
    }

    public Object get(String... keys) {
        Object t = this;
        for (String key : keys) {
            t = ((DynamicStruct) t).get(key);
            if (t == null) {
                return null;
            }
        }
        return t;
    }

    //-----------------------------------------------------------------------------------

    public int getInt(String key) {
        return (Integer) get(key);
    }

    public long getLong(String key) {
        return (Long) get(key);
    }

    public double getDouble(String key) {
        return (Double) get(key);
    }

    public String getString(String key) {
        return (String) get(key);
    }

    public DynamicStruct getStruct(String key) {
        return (DynamicStruct) get(key);
    }


    public int[] getInts(String key) {
        return (int[]) get(key);
    }

    public long[] getLongs(String key) {
        return (long[]) get(key);
    }

    public double[] getDoubles(String key) {
        return (double[]) get(key);
    }

    public String[] getStrings(String key) {
        return (String[]) get(key);
    }


    public int getInt(String... keys) {
        return (Integer) get(keys);
    }

    public long getLong(String... keys) {
        return (Long) get(keys);
    }

    public double getDouble(String... keys) {
        return (Double) get(keys);
    }

    public String getString(String... keys) {
        return (String) get(keys);
    }

    public DynamicStruct getStruct(String... keys) {
        return (DynamicStruct) get(keys);
    }


    public int[] getInts(String... keys) {
        return (int[]) get(keys);
    }

    public long[] getLongs(String... keys) {
        return (long[]) get(keys);
    }

    public double[] getDoubles(String... keys) {
        return (double[]) get(keys);
    }

    public String[] getStrings(String... keys) {
        return (String[]) get(keys);
    }

    //-----------------------------------------------------------------------------------

    public DynamicStruct copy() {
        return new DynamicStruct(map);
    }

    public void load(DynamicStruct struct) {
        load(struct.getMap());
    }

    public void load(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public Map<String, Object> getMap() {
        return map;
    }

}
