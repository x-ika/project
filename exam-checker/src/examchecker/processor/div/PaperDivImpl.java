package examchecker.processor.div;

import examchecker.processor.IPaperDiv;

public class PaperDivImpl implements IPaperDiv {

    private String id;

    private int rows;

    private int columns;


    PaperDivImpl(String id, int rows, int columns) {
        this.id = id;
        this.rows = rows;
        this.columns = columns;
    }


    public String getId() {
        return id;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getLength() {
        return rows * columns;
    }

}
