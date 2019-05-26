package examchecker.processor;

import java.util.List;

public interface IPaperDefinition {

    String getTemplateFileName();

    List<IPaperDiv> getPaperDivs();

}
