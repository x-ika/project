package message;

import com.simplejcode.commons.net.csbase.Message;
import java.io.*;

public class FileMessage extends Message {

    protected String fileName;
    protected byte[] buff;

    public FileMessage(Object sender) {
        this(sender, null);
    }

    public FileMessage(Object sender, File file) {
        super(sender);
        if (file != null) {
            fileName = file.getName();
            try {
                buff = new byte[(int) file.length()];
                //noinspection ResultOfMethodCallIgnored
                new FileInputStream(file).read(buff);
            } catch (IOException e) {
                fileName = null;
                buff = null;
                e.printStackTrace();
            }
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getBuff() {
        return buff;
    }

    public void setBuff(byte[] buff) {
        this.buff = buff;
    }

    public String toString() {
        return fileName;
    }
}
