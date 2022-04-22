package accounting;

import com.simplejcode.commons.gui.Console;
import com.simplejcode.commons.misc.util.ExcelCell;
import com.simplejcode.commons.misc.util.ExcelRow;
import com.simplejcode.commons.misc.util.ExcelUtils;

import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

class AccountingHelper {

    void process(File file, Console console) throws Exception {
        FileInputStream stream = new FileInputStream(file);
        List<ExcelRow> list = ExcelUtils.parseFile(stream).values().iterator().next();
        stream.close();

        Map<String, Integer> store = new TreeMap<>();
        Map<String, BigDecimal> accounting = new TreeMap<>();

        for (int i = 1; i < list.size(); i++) {
            ExcelRow excelRow = list.get(i);
            List<ExcelCell> cells = excelRow.getCells();

            String amountStr = (String) cells.get(1).getValue();
            String type = (String) cells.get(2).getValue();
            String details = (String) cells.get(3).getValue();

            if (amountStr == null || type == null) {
                break;
            }

            BigDecimal amount = new BigDecimal(amountStr);
            accounting.put(type, accounting.getOrDefault(type, BigDecimal.ZERO).add(amount));
            if (type.matches("შესყიდვა|გაყიდვა")) {
                int add = type.equals("შესყიდვა") ? 1 : -1;
                int value = store.getOrDefault(details, 0) + add;
                if (value != 0) {
                    store.put(details, value);
                } else {
                    store.remove(details);
                }
            }

        }

        printStore(console, "Store", store);
        printAccounting(console, "Accounting", accounting);

    }

    private void printStore(Console console, String header, Map<String, Integer> map) {
        console.setFont(Font.MONOSPACED, Font.BOLD, 18);
        space(console);
        console.writeLine(header);
        console.setFont(Font.MONOSPACED, Font.PLAIN, 12);
        for (String key : map.keySet()) {
            printRow(console, key, map.get(key).toString());
        }
    }

    private void printAccounting(Console console, String header, Map<String, BigDecimal> map) {
        console.setFont(Font.MONOSPACED, Font.BOLD, 18);
        space(console);
        console.writeLine(header);
        console.setFont(Font.MONOSPACED, Font.PLAIN, 12);
        BigDecimal sumPos = BigDecimal.ZERO;
        BigDecimal sumNeg = BigDecimal.ZERO;
        for (String key : map.keySet()) {
            BigDecimal value = map.get(key);
            if (value.signum() > 0) {
                printRow(console, key, value.toPlainString());
                sumPos = sumPos.add(value);
            }
        }
        for (String key : map.keySet()) {
            BigDecimal value = map.get(key);
            if (value.signum() < 0) {
                printRow(console, key, value.toPlainString());
                sumNeg = sumNeg.add(value);
            }
        }
        console.setFont(Font.MONOSPACED, Font.BOLD, 18);
        space(console);
        console.setFont(Font.MONOSPACED, Font.PLAIN, 12);
        printRow(console, "Total +", sumPos.toPlainString());
        printRow(console, "Total -", sumNeg.toPlainString());
    }

    private void space(Console console) {
        console.writeLine(String.format("%-40s %s", "--------------------", "-----"));
    }

    private void printRow(Console console, String key, String s) {
        console.writeLine(String.format("%-60s %s", key, s));
    }

}
