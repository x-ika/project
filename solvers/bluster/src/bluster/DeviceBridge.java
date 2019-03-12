package bluster;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public final class DeviceBridge {

    private static final String P = "C:\\Program Files\\Android\\android-sdk\\tools\\monkeyrunner.bat";

    //-----------------------------------------------------------------------------------

    public static BufferedImage getScreen() throws Exception {
        String jpgName = "resources/android/x.jpg";
        Runtime.getRuntime().exec("java -jar ass.jar -d " + jpgName).waitFor();
        Thread.sleep(100);
        return ImageIO.read(new File(jpgName));
    }

    //-----------------------------------------------------------------------------------

    public static PrintWriter createHeader(String fname) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(new File(fname));
        out.println("from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice");
        out.println("device = MonkeyRunner.waitForConnection()");
        return out;
    }

    public static void appendWait(PrintWriter out, double secs) {
        out.println(String.format("MonkeyRunner.sleep(%.3f)", secs));
    }

    public static void appendTouch(PrintWriter out, int x, int y) throws Exception {
        out.println(String.format("device.touch(%d, %d, MonkeyDevice.DOWN)", x, y));
        appendWait(out, 0.1);
        out.println(String.format("device.touch(%d, %d, MonkeyDevice.UP)", x, y));
    }

    public static void executeScript(String fname) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec(new String[]{P, fname});
        p.waitFor();
    }

    //-----------------------------------------------------------------------------------

    public static void sendTouch(String fname, int x, int y) throws Exception {
        PrintWriter out = createHeader(fname);
        appendTouch(out, x, y);
        out.close();
        executeScript(fname);
    }

}
