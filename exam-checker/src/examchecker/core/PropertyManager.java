package examchecker.core;

import com.simplejcode.commons.misc.FileSystemUtils;
import org.apache.commons.configuration2.PropertiesConfiguration;

import java.io.*;
import java.net.*;

public class PropertyManager {

    private static PropertyManager instance = new PropertyManager();

    public static PropertyManager getInstance() {
        return instance;
    }

    //-----------------------------------------------------------------------------------

    private long lastUpdateTimestamp;

    private PropertiesConfiguration properties;

    private synchronized void reloadIfNeeded() {

        try {

            URL url = FileSystemUtils.getFileURL(Constants.PROPERTIES_FILE);
            if (url == null) {
                throw new FileNotFoundException("Configuration was not found");
            }
            URLConnection conn = url.openConnection();

            if (conn.getLastModified() > lastUpdateTimestamp) {

                lastUpdateTimestamp = conn.getLastModified();

                PropertiesConfiguration config = new PropertiesConfiguration();
                try (InputStream inputStream = conn.getInputStream()) {
                    config.read(new InputStreamReader(inputStream));
                }
                properties = config;

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private void saveProperties() {
        try {
            properties.write(new FileWriter(Constants.PROPERTIES_FILE));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setProperty(String key, String value) {
        reloadIfNeeded();
        properties.setProperty(key, value);
        saveProperties();
    }

    public String getProperty(String key, String def) {
        reloadIfNeeded();
        String value = properties.getProperty(key).toString();
        return value == null ? def : value;
    }

    public String getProperty(String key) {
        return getProperty(key, null);
    }

    public int getPropertyInt(String key) {
        return Integer.parseInt(getProperty(key));
    }

    public double getPropertyDouble(String key) {
        return Double.parseDouble(getProperty(key));
    }

}
