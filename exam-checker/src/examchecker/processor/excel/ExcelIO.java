package examchecker.processor.excel;

import com.simplejcode.commons.misc.StreamUtils;
import examchecker.core.Constants;
import examchecker.processor.ITestDefinition;
import examchecker.processor.ITestDiv;
import examchecker.processor.ICheckResult;
import org.apache.poi.hssf.usermodel.*;
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

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("summary");

        HSSFCellStyle headerStyle = createHeaderStyle(workbook);
        HSSFCellStyle contentStyle = createContentStyle(workbook);
        HSSFCellStyle warnStyle = createWarnStyle(workbook);

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
                HSSFCellStyle style = rowContent.stream().anyMatch(t -> t.contains(Constants.UNDEFINED)) ?
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

    private HSSFCellStyle createHeaderStyle(HSSFWorkbook workbook) {

        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight((short) 300);
        style.setFont(font);

        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);

        return style;
    }

    private HSSFCellStyle createContentStyle(HSSFWorkbook workbook) {

        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight((short) 200);
        style.setFont(font);

        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);

        return style;
    }

    private HSSFCellStyle createWarnStyle(HSSFWorkbook workbook) {

        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        HSSFFont font = workbook.createFont();
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

    private static void populate(HSSFSheet sheet, HSSFCellStyle style, int rowInd, int colInd, List<String> values) {
        HSSFRow row = sheet.getRow(rowInd);
        for (Object value : values) {
            HSSFCell col = row.createCell(colInd++);
            col.setCellValue(value == null ? "" : value.toString());
            col.setCellStyle(style);
        }
    }

}
