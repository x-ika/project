package logic;

import logic.db.*;

import java.io.*;
import java.util.*;

@SuppressWarnings("unchecked")
public class DBManager {

    private static final String DB_FILE = "resources\\db";
    private static final String TEAMS = "teams";

    private Map<String, Object> map;

    public DBManager() {

        map = new HashMap<>();
        try {
            Object o = new ObjectInputStream(new FileInputStream(DB_FILE)).readObject();
            if (o instanceof Map) {
                map.putAll((Map) o);
            }
        } catch (Exception ignore) {
        }

        if (!map.containsKey(TEAMS)) {
            map.put(TEAMS, new HashMap<String, String>());
        }

    }

    public void setTeamPassword(String name, String password) {
        Map<String, Team> teams = getTeams();
        put(teams, new Team(name, password));
        commit();
    }

    public void removeTeam(String name) {
        getTeams().remove(name);
        commit();
    }

    public boolean existsTeam(String name, String password) {
        Team team = getTeams().get(name);
        return team != null && team.getPassword().equals(password);
    }

    public Collection<Team> getRegisteredTeams() {
        List<Team> list = new ArrayList<>(getTeams().values());
        Collections.sort(list, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        return list;
    }

    public void save(String key, Object o) {
        map.put(key, o);
        commit();
    }

    public Object get(String key) {
        return map.get(key);
    }

    public void commit() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DB_FILE));
            oos.writeObject(map);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private <T extends Record> void put(Map<String, T> map, T rec) {
        map.put(rec.getKey(), rec);
    }

    private Map<String, Team> getTeams() {
        return (Map<String, Team>) map.get(TEAMS);
    }
}
