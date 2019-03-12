package examchecker.processor.paper;

import examchecker.processor.IRecResult;

import java.io.File;
import java.util.Map;

class IRecResultImpl implements IRecResult {

    private File file;

    private Map<String, Integer> indexById;

    private boolean[] recognized;

    private int[] offsets;

    private boolean[] filled;


    IRecResultImpl(File file, Map<String, Integer> indexById, boolean[] recognized, int[] offsets, boolean[] filled) {
        this.file = file;
        this.indexById = indexById;
        this.recognized = recognized;
        this.offsets = offsets;
        this.filled = filled;
    }

    boolean[] getFilled() {
        return filled;
    }


    public File getFile() {
        return file;
    }

    public boolean recognized(String id) {
        return !indexById.containsKey(id) || recognized[indexById.get(id)];
    }

    public boolean getIsFilled(String id, int i) {
        return filled[offsets[indexById.get(id)] + i];
    }

}
