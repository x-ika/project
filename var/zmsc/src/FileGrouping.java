import java.io.File;
import java.util.*;

public class FileGrouping {

    public static void main(String[] args) {

        String dir = "d:/t/";

        groupFilesByDate(dir);

    }

    private static void groupFilesByDate(String dir) {
        for (File file : getFiles(dir)) {
            Date date = new Date(file.lastModified());
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int y = c.get(Calendar.YEAR);
            int m = c.get(Calendar.MONTH);
            int d = c.get(Calendar.DATE);

            String sm = String.format("%02d", m + 1);
            String sd = String.format("%02d", d);

            String dst = dir + y + "." + sm + "." + sd + "/";

            new File(dst).mkdirs();
            if (!file.renameTo(new File(dst + file.getName()))) {
                System.out.println("FAIL " + file.getName());
            }

        }
    }

    public static void groupBySuffix(String dir, String suffix) {

        for (File file : getFiles(dir)) {
            if (file.getName().endsWith(suffix) && !file.renameTo(new File("D:/r/" + suffix + "/" + file.getName()))) {
                System.out.println("FAIL " + file.getName());
            }

        }

    }

    private static File[] getFiles(String dir) {
        return new File(dir).listFiles();
    }

}
