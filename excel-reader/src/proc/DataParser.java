package proc;

import com.simplejcode.commons.misc.DateUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class DataParser {

    private static final String DATE_PATTERN = "dd.MM.yyyy";
    private static final String TIME_PATTERN = "HH:mm";
    private static final String PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    public static List<InOutRecord> readData(File file) throws Exception {
        Workbook workbook = new XSSFWorkbook(new FileInputStream(file));
        Sheet sheet = workbook.getSheetAt(0);

        List<InOutRecord> data = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            String name = row.getCell(1).getStringCellValue();
            String in = row.getCell(3).getStringCellValue();
            Date date;
            try {
                date = row.getCell(4).getDateCellValue();
            } catch (Exception ignore) {
                String dateStr = row.getCell(4).getStringCellValue();
                date = new SimpleDateFormat(PATTERN).parse(dateStr);
            }
            data.add(new InOutRecord(name, in, date.getTime()));
        }
        return data;
    }


    public static ArrayList<DayRecord> processData(List<InOutRecord> data) {

        Map<String, DayRecord> map = groupData(data);

        ArrayList<DayRecord> list = sortData(map);

        formatData(list);

        return list;
    }

    private static Map<String, DayRecord> groupData(List<InOutRecord> data) {
        Set<String> part1 = new HashSet<>();
        Set<Long> part2 = new HashSet<>();
        Map<String, DayRecord> map = new HashMap<>();
        for (InOutRecord t : data) {
            part1.add(t.name);
            part2.add(getUTC0(t.time));
        }
        for (String name : part1) {
            for (Long day : part2) {
                map.put(name + "_" + day, new DayRecord(name, day, Long.MAX_VALUE, 0));
            }
        }

        for (InOutRecord t : data) {
            long day = getUTC0(t.time);
            DayRecord record = map.get(t.name + "_" + day);
            record.inTime = Math.min(record.inTime, t.time);
            record.outTime = Math.max(record.outTime, t.time);
        }
        return map;
    }

    private static ArrayList<DayRecord> sortData(Map<String, DayRecord> map) {
        ArrayList<DayRecord> list = new ArrayList<>(map.values());
        list.sort((o1, o2) -> {
            int c1 = o1.name.compareTo(o2.name);
            int c2 = Long.compare(o1.day, o2.day);
            return c1 != 0 ? c1 : c2;
        });
        return list;
    }

    private static void formatData(ArrayList<DayRecord> list) {
        for (DayRecord t : list) {

            long inTime = t.inTime;
            long outTime = t.outTime;

            if (outTime == 0) {
                continue;
            }

            long requiredInTime = t.day + TimeUnit.HOURS.toMillis(6) + TimeUnit.MINUTES.toMillis(30);
            long requiredOutTime = t.day + TimeUnit.HOURS.toMillis(15);

            t.dayStr = DateUtils.formatTime(t.day, DATE_PATTERN);
            t.inTimeStr = DateUtils.formatTime(inTime, TIME_PATTERN);
            t.outTimeStr = DateUtils.formatTime(outTime, TIME_PATTERN);

            long pre = inTime - requiredInTime;
            if (pre > 0) {
                t.preDelay = formatMS(pre);
            }
            long post = outTime - requiredOutTime;
            if (post < 0) {
                t.postDelay = formatMS(-post);
            }

            t.worked = formatMS(outTime - inTime);

        }
    }


    private static String formatMS(long dif) {
        dif /= 1000;
        dif /= 60;
        return String.format("%02d:%02d", dif / 60, dif % 60);
    }

    private static long getUTC0(long time) {
        long millisInDay = TimeUnit.DAYS.toMillis(1);
        return time / millisInDay * millisInDay;
    }

}
