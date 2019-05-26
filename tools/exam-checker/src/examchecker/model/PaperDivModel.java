package examchecker.model;

import lombok.*;

@Getter
@Setter
public class PaperDivModel {

    /*
    Main attributes
     */

    private String id;

    private PaperDivType type;

    private int length;

    private Integer count;

    private Integer height;

    private Integer rangeStart;

    /*
    For excel export
     */

    private Integer sequenceNumber;

    private String excelHeader;

}
