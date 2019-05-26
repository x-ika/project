package examchecker.processor;

public interface ITestDiv {

    String getId();

    DivType getType();

    int getLength();

    Integer getRangeStart();

    Integer getTestScore();

    String getCorrectAnswer();

    Integer getSequenceNumber();

    String getExcelHeader();

}
