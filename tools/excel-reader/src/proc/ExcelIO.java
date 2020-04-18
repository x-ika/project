package proc;

import com.simplejcode.commons.misc.util.ExcelUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ExcelIO {

    public void export(List<DayRecord> list, File file) {

        Workbook workbook = ExcelUtils.createWorkbook(true);
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
        ExcelUtils.populate(sheet, headerStyle, 0, 0, Arrays.asList(headerContent));
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
            ExcelUtils.populate(sheet, style, rowInd, 0, Arrays.asList(rowContent));
            // end row

            rowInd++;

        }

        for (int i = 0; i < headerContent.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ExcelUtils.writeToFile(workbook, file);

    }

    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = ExcelUtils.createCellStyle(workbook);
        style.setFont(ExcelUtils.createFont(workbook, true, (short) 300));
        return style;
    }

    private static CellStyle createContentStyle(Workbook workbook) {
        CellStyle style = ExcelUtils.createCellStyle(workbook);
        style.setFont(ExcelUtils.createFont(workbook, false, (short) 200));
        return style;
    }

    private static CellStyle createWarnStyle(Workbook workbook) {
        CellStyle style = ExcelUtils.createCellStyle(workbook, HSSFColor.HSSFColorPredefined.LIGHT_YELLOW.getIndex());
        style.setFont(ExcelUtils.createFont(workbook, false, (short) 200));
        return style;
    }

}
