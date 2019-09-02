package examchecker.processor.div;

import examchecker.model.*;
import examchecker.processor.*;

import java.util.*;

public class DivProcessor {

    public IPaperDefinition createPaperDef(PaperModel paperModel) {

        List<IPaperDiv> ret = new ArrayList<>();
        for (PaperDivModel paperDivModel : paperModel.getDivs()) {
            if (paperDivModel.getType() == PaperDivType.TEST) {
                for (int i = 0; i < paperDivModel.getCount(); i++) {
                    String id = paperDivModel.getId() + getTestInd(i, paperDivModel.getHeight(), paperDivModel.getCount());
                    ret.add(new PaperDivImpl(id, 1, paperDivModel.getLength()));
                }
            } else if (paperDivModel.getType() == PaperDivType.NUMBER) {
                ret.add(new PaperDivImpl(paperDivModel.getId(), 10, paperDivModel.getLength() / 10));
            } else {
                ret.add(new PaperDivImpl(paperDivModel.getId(), 1, paperDivModel.getLength()));
            }
        }

        return new IPaperDefinition() {
            public String getTemplateFileName() {
                return paperModel.getTemplateFileName();
            }

            public List<IPaperDiv> getPaperDivs() {
                return ret;
            }
        };
    }

    public ITestDefinition createTestDef(PaperModel paperModel, TestModel testModel) {

        List<ITestDiv> ret = new ArrayList<>();
        for (PaperDivModel paperDivModel : paperModel.getDivs()) {

            if (paperDivModel.getType() == PaperDivType.SKIP) {
                continue;
            }

            if (paperDivModel.getType() == PaperDivType.TEST) {

                for (int i = 0; i < paperDivModel.getCount(); i++) {

                    int testInd = getTestInd(i, paperDivModel.getHeight(), paperDivModel.getCount());

                    TestDivImpl div = new TestDivImpl(paperDivModel.getId() + testInd);

                    div.setType(DivType.valueOf(paperDivModel.getType().name()));
                    div.setLength(paperDivModel.getLength());
                    div.setRangeStart(paperDivModel.getRangeStart());
                    div.setSequenceNumber(paperDivModel.getSequenceNumber() + testInd);
                    div.setExcelHeader(paperDivModel.getExcelHeader() + (testInd + 1));

                    TestDivModel testDivModel = testModel.getDivs().get(testInd);
                    div.setTestScore(testDivModel.getScore());
                    div.setCorrectAnswer(testDivModel.getCorrectAnswer());

                    ret.add(div);

                }

                TestDivImpl div = new TestDivImpl("TT");

                div.setType(DivType.TEST_SUM);
                div.setLength(0);
                div.setSequenceNumber(paperDivModel.getSequenceNumber() + paperDivModel.getCount());
                div.setExcelHeader("Total");

                ret.add(div);

                continue;
            }

            TestDivImpl div = new TestDivImpl(paperDivModel.getId());

            div.setType(DivType.valueOf(paperDivModel.getType().name()));
            div.setLength(paperDivModel.getLength());
            div.setRangeStart(paperDivModel.getRangeStart());
            div.setSequenceNumber(paperDivModel.getSequenceNumber());
            div.setExcelHeader(paperDivModel.getExcelHeader());

            ret.add(div);

        }

        TestDivImpl div = new TestDivImpl("file");

        div.setType(DivType.FILE_NAME);
        div.setLength(0);
        div.setSequenceNumber(99);
        div.setExcelHeader("File Name");

        ret.add(div);

        return () -> ret;
    }

    private static int getTestInd(int i, int c, int n) {
        int full = n - c;
        return i < 2 * full ? i % 2 == 0 ? i / 2 : i / 2 + c : i - full;
    }

}
