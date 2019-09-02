package examchecker.core;

import examchecker.model.*;
import examchecker.processor.*;
import examchecker.processor.div.DivProcessor;
import examchecker.processor.excel.ExcelIO;
import examchecker.processor.paper.*;
import examchecker.processor.test.TestProcessor;

import java.io.File;
import java.nio.file.*;
import java.util.*;

class ExamCheckerHelper {

    private PaperModel paperModel;
    private TestModel testModel;

    private List<IRecResult> recResults;

    private List<ICheckResult> checkResults;


    PaperModel getPaperModel() {
        return paperModel;
    }

    TestModel getTestModel() {
        return testModel;
    }

    List<ICheckResult> getCheckResults() {
        return checkResults;
    }


    void readPaperModel(File file) throws Exception {
        paperModel = ModelIO.read(file.getAbsolutePath(), PaperModel.class);
        paperModel.setTemplateFileName(file.getParent() + "/" + paperModel.getTemplateFileName());
    }

    void exportPaperModel(File file) throws Exception {
        ModelIO.write(paperModel, file.getAbsolutePath());
    }

    void readTestModel(File file) throws Exception {
        testModel = ModelIO.read(file.getAbsolutePath(), TestModel.class);
    }

    void exportTestModel(File file) throws Exception {
        ModelIO.write(testModel, file.getAbsolutePath());
    }


    void checkPapers(File[] files) throws Exception {

        recResults = new ArrayList<>();
        checkResults = new ArrayList<>();

        IPaperDefinition paperDefinition = getModelForPaperRecognition();
        ITestDefinition testDefinition = getModelForTestProcessing();

        loadTemplate(paperDefinition);

        for (File file : files) {

            if (!file.isFile() || file.isHidden()) {
                return;
            }

            IRecResult recResult = PaperProcessor.getInstance().analyze(
                    paperDefinition,
                    file,
                    DetectionMode.NORMAL);

            ICheckResult checkResult = new TestProcessor().calculateDivValues(
                    testDefinition,
                    recResult);

            recResults.add(recResult);
            checkResults.add(checkResult);
        }

    }

    boolean renamePapers() throws Exception {

        boolean ret = true;

        Map<String, Integer> mobiles = new HashMap<>();

        for (int i = 0; i < recResults.size(); i++) {

            IRecResult rec = recResults.get(i);
            ICheckResult result = checkResults.get(i);

            File file = rec.getFile();
            String name = file.getName();

            String id = result.getValue(paperModel.getPrimaryDivId());
            int ord = mobiles.getOrDefault(id, 0);
            mobiles.put(id, ord + 1);
            if (ord > 0) {
                id += "_" + ord;
            }
            String newName = id + name.substring(name.lastIndexOf('.'));

            File dest = new File(file.getParent() + "/checked/" + (newName.contains(Constants.UNDEFINED) ? "###/" : "/") + newName);
            if (!dest.getParentFile().exists()) {
                ret &= dest.getParentFile().mkdirs();
            }
            Files.copy(
                    Paths.get(file.getAbsolutePath()),
                    Paths.get(dest.getAbsolutePath()),
                    StandardCopyOption.REPLACE_EXISTING);
            ret &= dest.exists();

        }

        return ret;

    }

    void exportToExcel(File file) throws Exception {

        new ExcelIO().export(
                paperModel.getGroupByDivId(),
                paperModel.getPrimaryDivId(),
                getModelForTestProcessing(),
                checkResults,
                file);

    }

    void showBlackWhiteImage(File file) throws Exception {
        testSingleFile(file, DetectionMode.BLACK_WHITE);
    }

    void debugPaper(File file) throws Exception {
        testSingleFile(file, DetectionMode.DEBUG_INFO);
    }

    //-----------------------------------------------------------------------------------
    /*
    Private
     */

    private void testSingleFile(File file, DetectionMode detectionMode) throws Exception {
        IPaperDefinition paperDefinition = getModelForPaperRecognition();
        loadTemplate(paperDefinition);
        PaperProcessor.getInstance().analyze(paperDefinition, file, detectionMode);
    }

    private void loadTemplate(IPaperDefinition paperDefinition) throws Exception {
        if (paperDefinition.getTemplateFileName() != null) {
            PaperProcessor.getInstance().loadTemplate(
                    paperDefinition, new File(paperDefinition.getTemplateFileName()));
        }
    }


    private IPaperDefinition getModelForPaperRecognition() {
        return new DivProcessor().createPaperDef(paperModel);
    }

    private ITestDefinition getModelForTestProcessing() {
        return new DivProcessor().createTestDef(paperModel, testModel);
    }

}
