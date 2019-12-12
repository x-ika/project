import java.io.File;
import java.util.*;

public class FileGrouping {

    public static void main(String[] args) {

        String dir = "d:/ika/z/1";

        for (File file : new File(dir).listFiles()) {
            Date date = new Date(file.lastModified());
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int y = c.get(Calendar.YEAR);
            int m = c.get(Calendar.MONTH);
            int d = c.get(Calendar.DATE);

            String sm = String.format("%02d", m + 1);
            String sd = String.format("%02d", d);

            String dst = "D:/t/" + y + "." + sm + "." + sd + "/";

            new File(dst).mkdirs();
            if (!file.renameTo(new File(dst + file.getName()))) {
                System.out.println("P " + file.getName());
            }

        }

    }

}
