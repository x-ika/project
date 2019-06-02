package examchecker.processor.excel;

import com.simplejcode.commons.misc.util.StreamUtils;
import examchecker.core.Constants;
import examchecker.processor.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.*;

public class ExcelIO {

    public void export(String groupById,
                       String primaryId,
                       ITestDefinition def,
                       List<ICheckResult> checkResults,
                       File file) throws Exception
    {

        // ordering
        List<ITestDiv> divs = def.getTestDivs();
        divs = new ArrayList<>(divs);
        divs.sort(Comparator.comparingInt(ITestDiv::getSequenceNumber));

        Workbook workbook = new HSSFWorkbook();
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
        populate(sheet, headerStyle, 0, 0, headerContent);
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
                populate(sheet, style, rowInd, 0, rowContent);
                // end row

            }

            rowInd++;

        }

        for (int i = 0; i < divs.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);
        out.close();

    }

    private int getScore(ICheckResult t, String id) {
        String s = t.getValue(id);
        return s.contains(Constants.UNDEFINED) ? 1 : 0;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight((short) 300);
        style.setFont(font);

        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);

        return style;
    }

    private CellStyle createContentStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight((short) 200);
        style.setFont(font);

        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);

        return style;
    }

    private CellStyle createWarnStyle(Workbook workbook) {

        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight((short) 200);
        style.setFont(font);

        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);

        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.LIGHT_YELLOW.getIndex());

        return style;
    }

    private static void populate(Sheet sheet, CellStyle style, int rowInd, int colInd, List<String> values) {
        Row row = sheet.getRow(rowInd);
        for (Object value : values) {
            Cell col = row.createCell(colInd++);
            col.setCellValue(value == null ? "" : value.toString());
            col.setCellStyle(style);
        }
    }

}
