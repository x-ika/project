package examchecker.processor.div;

import examchecker.processor.DivType;
import examchecker.processor.ITestDiv;

public class TestDivImpl implements ITestDiv {

    private final String id;

    private DivType type;

    private int length;

    private Integer rangeStart;

    private Integer testScore;

    private String correctAnswer;

    private Integer sequenceNumber;

    private String excelHeader;


    TestDivImpl(String id) {
        this.id = id;
    }


    public String getId() {
        return id;
    }

    public DivType getType() {
        return type;
    }

    public void setType(DivType type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Integer getRangeStart() {
        return rangeStart;
    }

    public void setRangeStart(Integer rangeStart) {
        this.rangeStart = rangeStart;
    }

    public Integer getTestScore() {
        return testScore;
    }

    public void setTestScore(Integer testScore) {
        this.testScore = testScore;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getExcelHeader() {
        return excelHeader;
    }

    public void setExcelHeader(String excelHeader) {
        this.excelHeader = excelHeader;
    }

}
