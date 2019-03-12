package logic;

import java.util.Properties;
import java.io.*;

public class Problem implements Serializable {

    private static final String PROBLEM_NAME = "name";
    private static final String FILE_NAME = "file";
    private static final String DATA_PATH = "datapath";
    private static final String TESTER_EXEC = "tester";
    private static final String STATEMENT = "statement";
    private static final String TIME_LIMIT = "timelimit";
    private static final String TESTER_TIME_LIMIT = "testertimelimit";

    private String directory;
    private String name;
    private String fileName;
    private String dataPath;
    private String testerExec;
    private String statement;
    private int timeLimit;
    private int testerTimeLimit;

    public Problem(String propsFileName) {
        try {

            File propsFile = new File(propsFileName);
            directory = propsFile.getParent() + File.separator;
            Properties properties = new Properties();
            properties.load(new FileReader(propsFile));

            name = properties.getProperty(PROBLEM_NAME);
            fileName = properties.getProperty(FILE_NAME);
            dataPath = properties.getProperty(DATA_PATH);
            testerExec = properties.getProperty(TESTER_EXEC);
            statement = properties.getProperty(STATEMENT);
            timeLimit = Integer.parseInt(properties.getProperty(TIME_LIMIT));
            testerTimeLimit = Integer.parseInt(properties.getProperty(TESTER_TIME_LIMIT));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public String getDataPath() {
        return directory + dataPath;
    }

    public String getTesterExec() {
        return directory + testerExec;
    }

    public String getStatement() {
        return directory + statement;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public int getTesterTimeLimit() {
        return testerTimeLimit;
    }
}
