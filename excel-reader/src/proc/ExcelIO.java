package proc;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExcelIO {

    public void export(List<DayRecord> list, File file) throws Exception {

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("summary");

        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle contentStyle = createContentStyle(workbook);
        CellStyle warnStyle = createWarnStyle(workbook);

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
            CellStyle style = warning ? warnStyle : contentStyle;
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

    private static void populate(Sheet sheet, CellStyle style, int rowInd, int colInd, String[] values) {
        Row row = sheet.getRow(rowInd);
        for (Object value : values) {
            Cell col = row.createCell(colInd++);
            col.setCellValue(value == null ? "" : value.toString());
            System.out.print((value == null ? "" : value.toString()) + " \t ");
            col.setCellStyle(style);
        }
        System.out.println();
    }

}
