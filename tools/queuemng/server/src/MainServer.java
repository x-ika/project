import com.simplejcode.commons.gui.*;
import com.simplejcode.commons.misc.ReflectionUtils;
import com.simplejcode.commons.net.csbase.*;
import com.simplejcode.commons.net.sockets.*;

import javax.print.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.net.Socket;
import java.util.*;
import java.io.*;
import java.awt.image.BufferedImage;
import java.awt.*;

public class MainServer extends ConnectionsManager<Message> {

    public static final int port = 4411;

    public static MainServer mainServer;

    private int front, end;
    private Map<SocketConnection, OperatorData> data;

    public MainServer() {
        super(port);
        data = new HashMap<>();
    }

    @Override
    protected SocketConnection<Message> create(Socket socket) throws IOException {
        return new NetMessanger(socket, 0, 5000);
    }

    //-----------------------------------------------------------------------------------

    private void checkLogin(SocketConnection socketConnection, MapMessage record) {
        String user = record.getString("user");
        String pass = record.getString("pass");
        Properties p = new Properties();
        try {
            p.load(new FileReader("resources/users.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!p.get(user).equals(pass)) {
            return;
        }
        for (SocketConnection<Message> c : connections) {
            if (c.getHost().equals(user)) {
                return;
            }
        }
        socketConnection.setHost(user);
        data.put(socketConnection, new OperatorData());
        sendTo(socketConnection, record);
        assignNextClient();
        operatorsPanel.updateTable(data);
    }

    private synchronized void assignNextClient() {

        if (front == end) {
            return;
        }
        SocketConnection free = null;
        for (SocketConnection c : connections) {
            OperatorData d = data.get(c);
            if (!d.isFree()) {
                continue;
            }
            if (free == null || d.getLastFinishTime() < data.get(free).getLastFinishTime()) {
                free = c;
            }
        }
        if (free == null) {
            return;
        }

        if (data.get(free).startService(++front)) {
            MapMessage record = new MapMessage(this);
            record.put("type", "assign");
            record.put("ticket", front);
            sendTo(free, record);
        }
        operatorsPanel.updateTable(data);

    }

    private synchronized void finishService(SocketConnection connection) {
        data.get(connection).endService();
        operatorsPanel.updateTable(data);
    }

    public synchronized void getNextTicket() {
        end++;
        try {
            print("" + end);
        } catch (Exception e) {
            e.printStackTrace();
        }
        assignNextClient();
    }

    private void print(String num) throws Exception {
        BufferedImage img = ImageIO.read(new File("resources/ticket.jpg"));
        Graphics g = img.getGraphics();
        g.setFont(new Font("Serif", Font.BOLD, 100));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.black);
        g.drawString(num, img.getWidth() - fm.stringWidth(num) >> 1, img.getHeight() - fm.getHeight() + 2 * fm.getAscent() >> 1);

        File temp = new File("resources/tmp.jpg");
        ImageIO.write(img, "jpeg", temp);

        PrintService service = PrintServiceLookup.lookupDefaultPrintService();
        DocPrintJob printJob = service.createPrintJob();
        printJob.print(new SimpleDoc(new FileInputStream(temp), DocFlavor.INPUT_STREAM.JPEG, null), null);

    }

    //-----------------------------------------------------------------------------------

    public void disconnected(SocketConnection socketConnection) {
        super.disconnected(socketConnection);
        data.remove(socketConnection);
    }

    public void messageReceived(final SocketConnection socketConnection, final Message message) {
        new Thread() {
            public void run() {
                handle((MapMessage) message, socketConnection);
            }
        }.start();
    }

    private void handle(MapMessage record, SocketConnection socketConnection) {
        if (record.get("type").equals("login")) {
            checkLogin(socketConnection, record);
        }
        if (record.get("type").equals("finish")) {
            finishService(socketConnection);
            assignNextClient();
        }
    }

    //-----------------------------------------------------------------------------------

    private AboutDialogPanel aboutDialogPanel;
    private CustomFrame frame;
    private OperatorsPanel operatorsPanel;

    public void actionOnPrint() {
        getNextTicket();
    }

    public void actionOnExit() {
        for (SocketConnection connection : connections) {
            connection.closeConnection();
        }
        System.exit(0);
    }

    public void actionOnAbout() {
        aboutDialogPanel.showDialog(frame);
    }

    public void handle(Throwable e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(frame, "Error occurred when processing your command!", "Error", JOptionPane.ERROR_MESSAGE);
    }

    public synchronized void createMainFrame() {
        aboutDialogPanel = new AboutDialogPanel("QMNG", getImage("resources/logo.jpeg"));
        aboutDialogPanel.init();
        frame = new CustomFrame("Queue Manager");

        frame.setSize(900, 600);

        frame.setJMenuBar(GraphicUtils.createMenuBar(
                new String[][]{
                        {"File", null, "Exit"},
                        {"Help", "About"}},
                this,
                ReflectionUtils.getMethod("handle", Throwable.class),
                ReflectionUtils.getMethod("actionOnExit"),
                ReflectionUtils.getMethod("actionOnAbout")));

        frame.setContentPane(operatorsPanel = new OperatorsPanel(GraphicUtils.createAction("Print Ticket", this, ReflectionUtils.getMethod("actionOnPrint"))));

        frame.setSize(900, 600);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    }

    //-----------------------------------------------------------------------------------

    public static BufferedImage getImage(String path) {
        try {
            return ImageIO.read(getResource(path));
        } catch (IOException e) {
            return null;
        }
    }

    public static InputStream getResource(String path) {
        try {
            InputStream stream = MainServer.class.getClassLoader().getResourceAsStream(path);
            return stream != null ? stream : new FileInputStream(path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //-----------------------------------------------------------------------------------

    public static void main(String[] args) throws InterruptedException {
        mainServer = new MainServer();
        mainServer.createMainFrame();
    }

}
