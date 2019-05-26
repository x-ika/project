package examchecker.processor;

import java.io.File;

public interface IRecResult {

    File getFile();

    boolean recognized(String id);

    boolean getIsFilled(String id, int i);

}
