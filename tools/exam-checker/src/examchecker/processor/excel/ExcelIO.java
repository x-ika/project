package examchecker.processor.excel;

import com.simplejcode.commons.misc.util.*;
import examchecker.core.Constants;
import examchecker.processor.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.*;

public class ExcelIO {

    public void export(String groupById,
                       String primaryId,
                       ITestDefinition def,
                       List<ICheckResult> checkResults,
                       File file)
    {

        // ordering
        List<ITestDiv> divs = def.getTestDivs();
        divs = new ArrayList<>(divs);
        divs.sort(Comparator.comparingInt(ITestDiv::getSequenceNumber));

        Workbook workbook = ExcelUtils.createWorkbook(true);
        Sheet sheet = workbook.createSheet("summary");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle contentStyle = createContentStyle(workbook);
        CellStyle warnStyle = createWarnStyle(workbook);

        // create
        Set<String> distinct = new HashSet<>();
        for (ICheckResult result : checkResults) {
            distinct.add(result.getValue(groupById));
        }
        for (int i = 0; i <= checkResults.size() + distinct.size(); i++) {
            sheet.createRow(i);
            sheet.getRow(i).setHeight((short) (i == 0 ? 350 : 300));
        }

        // start header
        List<String> headerContent = new ArrayList<>();
        for (ITestDiv div : divs) {
            if (div.getExcelHeader() != null) {
                headerContent.add(div.getExcelHeader());
            }
        }
        ExcelUtils.populate(sheet, headerStyle, 0, 0, headerContent);
        // end header

        int rowInd = 1;

        Map<String, List<ICheckResult>> map = new TreeMap<>(StreamUtils.list2list(checkResults, t -> t.getValue(groupById)));

        for (List<ICheckResult> testResults : map.values()) {

            testResults.sort(Comparator.comparing(t -> getScore(t, primaryId)));

            for (ICheckResult result : testResults) {
                rowInd++;

                // start row
                List<String> rowContent = new ArrayList<>();
                for (ITestDiv div : divs) {
                    rowContent.add(result.getValue(div.getId()));
                }
                CellStyle style = rowContent.stream().anyMatch(t -> t.contains(Constants.UNDEFINED)) ?
                        warnStyle : contentStyle;
                ExcelUtils.populate(sheet, style, rowInd, 0, rowContent);
                // end row

            }

            rowInd++;

        }

        for (int i = 0; i < divs.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        ExcelUtils.writeToFile(workbook, file);

    }

    private int getScore(ICheckResult t, String id) {
        String s = t.getValue(id);
        return s.contains(Constants.UNDEFINED) ? 1 : 0;
    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = ExcelUtils.createCellStyle(workbook);
        style.setFont(ExcelUtils.createFont(workbook, (short) 300));
        return style;
    }

    private static CellStyle createContentStyle(Workbook workbook) {
        CellStyle style = ExcelUtils.createCellStyle(workbook);
        style.setFont(ExcelUtils.createFont(workbook, (short) 200));
        return style;
    }

    private static CellStyle createWarnStyle(Workbook workbook) {
        CellStyle style = ExcelUtils.createCellStyle(workbook, HSSFColor.HSSFColorPredefined.LIGHT_YELLOW.getIndex());
        style.setFont(ExcelUtils.createFont(workbook, (short) 200));
        return style;
    }

}
