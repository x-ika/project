import java.awt.*;

public class MouseTest {
    public static void main(String[] args) throws Exception {

        Thread.sleep(5000);

        Robot robot = new Robot();
        while (true) {

            robot.mouseWheel(-1);
            Thread.sleep(100);

        }

    }
}
