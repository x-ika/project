package examchecker.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
public class PaperModel {

    private String primaryDivId;

    private String groupByDivId;

    private String templateFileName;

    private List<PaperDivModel> divs;

}
