public interface ProcessListener {

    void lineRead(ProcessExecutor exec, String s);

    void stopped(ProcessExecutor exec);

}
