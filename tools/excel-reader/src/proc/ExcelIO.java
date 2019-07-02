package proc;

import com.simplejcode.commons.misc.util.ExcelUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ExcelIO {

    public void export(List<DayRecord> list, File file) throws Exception {

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("summary");

        CellStyle headerStyle = ExcelUtils.createHeaderStyle(workbook);
        CellStyle contentStyle = ExcelUtils.createContentStyle(workbook);
        CellStyle warnStyle = ExcelUtils.createWarnStyle(workbook);

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

        FileOutputStream out = new FileOutputStream(file);
        workbook.write(out);
        out.close();

    }

}
