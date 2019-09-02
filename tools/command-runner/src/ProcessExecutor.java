import com.simplejcode.commons.misc.util.ThreadUtils;
import com.sun.jna.*;
import com.sun.jna.platform.win32.*;

import java.io.*;
import java.lang.reflect.Field;

public class ProcessExecutor {

    private enum Status {
        IDLE,
        RUNNING,
        EXITING;
    }

    private String command;

    private ProcessListener listener;
    private Process process;

    private BufferedReader in;

    private Status status;

    public ProcessExecutor() {
        updateStatus(Status.IDLE);
    }

    public void setListener(ProcessListener listener) {
        this.listener = listener;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    //-----------------------------------------------------------------------------------

    public synchronized void start(String params) throws Exception {
        if (status != Status.IDLE) {
            stopAndWait();
        }
        startInternal(command + params);
    }

    private void startInternal(String params) throws Exception {
        process = runInCommandLine(params);
        in = new BufferedReader(new InputStreamReader(process.getInputStream()));

        updateStatus(Status.RUNNING);

        ThreadUtils.executeInNewThread(this::startReading);
    }

    private void startReading() {
        while (status == Status.RUNNING) {

            try {
                if (!in.ready()) {
                    Thread.sleep(100);
                }
                String s = in.readLine();
                if (s == null) {
                    // input end reached
                    stop();
                    break;
                }
                notifyReading(s);
            } catch (Exception e) {
                // input closed?
                stop();
                break;
            }

        }

        updateStatus(Status.IDLE);
    }

    public synchronized void stopAndWait() {
        stop();
        while (status != Status.IDLE) {
            try {
                wait(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void stop() {
        if (status == Status.RUNNING) {
            updateStatus(Status.EXITING);
            freeResources();
        }
    }

    //-----------------------------------------------------------------------------------

    private synchronized void updateStatus(Status newStatus) {
        if (status == Status.EXITING && newStatus == Status.IDLE) {
            notifyStopping();
        }
        status = newStatus;
    }

    private void notifyReading(String s) {
        try {
            listener.lineRead(this, s);
        } catch (Exception e) {
            // do nothing
        }
    }

    private void notifyStopping() {
        try {
            listener.stopped(this);
        } catch (Exception e) {
            // do nothing
        }
    }

    private void freeResources() {
        try {
            in.close();
            runInCommandLine("taskkill /PID " + getPid(process) + " /T /F");
            Thread.sleep(1000);
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Process runInCommandLine(String params) throws IOException {
        ProcessBuilder builder = new ProcessBuilder("cmd");
        builder.redirectErrorStream(true);
        Process process = builder.start();
        process.getOutputStream().write((params + "\n").getBytes());
        process.getOutputStream().close();
        return process;
    }

    private static int getPid(Process p) {
        Field f;
        try {
            if (Platform.isWindows()) {
                f = p.getClass().getDeclaredField("handle");
                f.setAccessible(true);
                WinNT.HANDLE handle = new WinNT.HANDLE(new Pointer((Long) f.get(p)));
                return Kernel32.INSTANCE.GetProcessId(handle);
            } else if (Platform.isLinux()) {
                f = p.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                return (int) (Integer) f.get(p);
            } else {
                // print no solution :)
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
