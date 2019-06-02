package ika.games.base.controller;

import com.simplejcode.commons.misc.FastLogger;

public class CustomLogger extends FastLogger {

    public CustomLogger(int level, boolean logDate) {
        super(level, logDate);
    }

    public void logAction(Level level, String action, Object... params) {
        String prefix = "";
        StringBuilder sb = new StringBuilder();
        sb.append(prefix).append(action).append(" --> [");
        for (Object param : params) {
            sb.append(param).append(',');
        }
        sb.setCharAt(sb.length() - 1, ']');
        log(sb.toString(), level);
    }

    public void logAction(String action, Object... params) {
        logAction(Level.INFO, action, params);
    }

}
