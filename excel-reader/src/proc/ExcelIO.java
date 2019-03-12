package proc;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ExcelIO {

    public void export(List<DayRecord> list, File file) throws Exception {

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("summary");

        HSSFCellStyle headerStyle = createHeaderStyle(workbook);
        HSSFCellStyle contentStyle = createContentStyle(workbook);
        HSSFCellStyle warnStyle = createWarnStyle(workbook);

        for (int i = 0; i <= list.size(); i++) {
            sheet.createRow(i);
            sheet.getRow(i).setHeight((short) (i == 0 ? 350 : 300));
        }

        // start header
        String[] headerContent = {
                "თანამშრომელი",
                "თარიღი",
                "მოსვლის დრო",
                "წასვლის დრო",
                "დაგვიანება",
                "ადრე გასვლა",
                "ნამუშევარი საათები",
                "კომენტარი"
        };
        populate(sheet, headerStyle, 0, 0, headerContent);
        // end header

        int rowInd = 1;

        for (DayRecord t : list) {

            // start row
            String[] rowContent = {
                    t.name,
                    t.dayStr,
                    t.inTimeStr,
                    t.outTimeStr,
                    t.preDelay,
                    t.postDelay,
                    t.worked,
                    null,
            };
//            boolean warning = t.preDelay != null || t.postDelay != null;
            boolean warning = t.outTime - t.inTime < TimeUnit.HOURS.toMillis(9);
            HSSFCellStyle style = warning ? warnStyle : contentStyle;
            populate(sheet, style, rowInd, 0, rowContent);
            // end row

            rowInd++;

        }

        for (int i = 0; i < headerContent.length; i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);
        out.close();

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

    private static void populate(HSSFSheet sheet, HSSFCellStyle style, int rowInd, int colInd, String[] values) {
        HSSFRow row = sheet.getRow(rowInd);
        for (Object value : values) {
            HSSFCell col = row.createCell(colInd++);
            col.setCellValue(value == null ? "" : value.toString());
            System.out.print((value == null ? "" : value.toString()) + " \t ");
            col.setCellStyle(style);
        }
        System.out.println();
    }

}
