package logic;

import com.simplejcode.commons.misc.structures.FastLogger;
import model.TestResult;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.net.Socket;

import com.simplejcode.commons.gui.Console;
import com.simplejcode.commons.misc.StringUtils;
import com.simplejcode.commons.net.csbase.*;
import com.simplejcode.commons.net.sockets.*;
import logic.db.Team;
import message.*;

public class SystemManager extends ConnectionsManager<Message> {

    private static final String LOG_FILE_NAME = "log.txt";
    private static final String SERVER_PROPERTIES = "resources\\server.properties";
    private static final String DEFAULT_CONTEST = "resources\\contest.properties";
    private static final String TESTER_WORKING_DIR = "resources\\work\\";

    private static SystemManager instance;

    public static SystemManager getInstance() {
        return instance;
    }

    private FastLogger logger;

    private Console console;

    private DBManager dbManager;

    private SubmissionProcessThread tester;

    private Contest contest;

    public SystemManager() throws Exception {
        super(4444);
        logger = new FastLogger(FastLogger.Level.FINEST.value, true);
        logger.setOutputBase(LOG_FILE_NAME);
        console = Console.createInstance();
//        connectionsManager = new ConnectionsManager();
        dbManager = new DBManager();
        tester = new SubmissionProcessThread(TESTER_WORKING_DIR);
        contest = new Contest();
    }

    protected SocketConnection<Message> create(Socket socket) throws IOException {
        return new NetMessanger(socket, 999, 50000);
    }

    private synchronized void startContest(String contestProperties) {
        if (contest.started()) {
            return;
        }
        contest.load(contestProperties);
        sendToAll(new ContestStatus(this, true));
    }

    public synchronized void handleMessageReceived(SocketConnection<Message> source, Message message) {
        if (message instanceof TextMessage) {
            sendToAll(source, message);
        }
        if (message instanceof Login) {
            Login login = (Login) message;
            handleLogin(source, login);
        }
        if (message instanceof Submission) {
            Submission submission = (Submission) message;
            if (submission.getBuff() == null) {
                return;
            }
            test(submission);
        }
        if (message instanceof Request) {
            handleRequest(source, (Request) message);
        }
    }

    //-----------------------------------------------------------------------------------
    // API for client

    public void handleLogin(SocketConnection<Message> source, Login login) {
        if (!dbManager.existsTeam(login.getLogin(), login.getPassword())) {
            source.closeConnection();
            return;
        }

        sendTo(source, login);

        String teamName = login.getLogin();
        Map<String, TeamHistory> histories = contest.getHistories();
        if (!histories.containsKey(teamName)) {
            histories.put(teamName, new TeamHistory(this, teamName));
            sendToAll(histories.get(teamName));
        }
        dbManager.commit();
    }

    public void test(Submission submission) {
        if (!contest.started()) {
            throw new RuntimeException("Hacked client!!!");
        }
        submission.setSendTime(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        if (submission.getSendTime() <= contest.getEndTime()) {
            tester.test(submission);
        }
    }

    public void handleRequest(SocketConnection<Message> source, Request request) {
        switch (request.getRequestType()) {
            case CONTEST_STATUS:
                sendTo(source, new ContestStatus(this, contest.started()));
                break;
        }
        if (!contest.started()) {
            return;
        }
        switch (request.getRequestType()) {
            case CONTEST_INFO:
                sendTo(source, contest.getInfo());
                break;
            case HISTORIES:
                for (TeamHistory history : contest.getHistories().values()) {
                    sendTo(source, history);
                }
                break;
            case PROBLEM_STATEMENTS:
                for (int i = 0; i < contest.getInfo().getNProblems(); i++) {
                    Problem problem = contest.getProblem(i);
                    File file = new File(problem.getStatement());
                    sendTo(source, new ProblemInfo(this, file, i, problem.getTimeLimit()));
                }
                break;
        }
    }

    //-----------------------------------------------------------------------------------
    // API for Tester

    public Problem getProblem(int problem) {
        return contest.getProblem(problem);
    }

    public String getProperty(String key) {
        try {
            Properties properties = new Properties();
            properties.load(new FileReader(SERVER_PROPERTIES));
            return properties.getProperty(key);
        } catch (IOException e) {
            return null;
        }
    }

    public void testingFinished(Submission submission, TestResult result, int test) {
        TeamHistory history = contest.addAttemp(submission, result, test);

        logger.log(String.format("%-30s %-20s %-5s %-10s",
                submission.getSenderTeam(),
                getProblem(submission.getProblem()).getName(),
                result,
                test));

        sendToAll(history);
        dbManager.commit();
    }

    //-----------------------------------------------------------------------------------
    // Admin CP

    public void process(String command) {
        try {
            String[] args = StringUtils.parse(command, ' ', '"');
            if (args[0].equals("help")) {
                console.writeLine("start [id]");
                console.writeLine("register [team] [password]");
                console.writeLine("unregister [team]");
                console.writeLine("changepass [team] [password]");
                console.writeLine("listteams");
                console.writeLine("backup");
                console.writeLine("restore");
            }
            if (args[0].equals("start")) {
                instance.startContest(args.length == 1 ? DEFAULT_CONTEST : args[1]);
            }
            if (args[0].equals("register")) {
                instance.dbManager.setTeamPassword(args[1], args[2]);
            }
            if (args[0].equals("unregister")) {
                instance.dbManager.removeTeam(args[1]);
            }
            if (args[0].equals("changepass")) {
                instance.dbManager.setTeamPassword(args[1], args[2]);
            }
            if (args[0].equals("listteams")) {
                for (Team t : instance.dbManager.getRegisteredTeams()) {
                    console.writeLine(String.format("    %-50s%s", t.getName(), t.getPassword()));
                }
            }
            if (args[0].equals("backup")) {
                dbManager.save("contest", contest);
            }
            if (args[0].equals("restore")) {
                contest = (Contest) dbManager.get("contest");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        instance = new SystemManager();
        instance.start();

        String line;
        for (String arg : args) {
            instance.console.writeLine(arg);
            instance.process(arg);
        }
        while (!(line = instance.console.readLine()).equalsIgnoreCase("exit")) {
            instance.process(line);
        }
        System.exit(0);
    }

}
