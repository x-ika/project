package util;

import com.simplejcode.commons.misc.*;

import java.util.*;

import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

@Deprecated
public class MMUtils {
    private static final String DATA = "http://www.topcoder.com/tc?module=BasicData&";
    private static final String CODERS = DATA + "c=dd_coder_list";
    private static final String MAR_LIST = DATA + "c=dd_active_marathon_list";

    private static final String HANDLE = "handle";
    private static final String RATING = "mar_rating";
    private static final String VOL = "mar_vol";
    private static final String NUM = "mar_num_ratings";

    public static List<Member> getList() throws Exception {
        Document d = XMLUtils.readXml("file:resources/tc.xml");
        NodeList list = d.getDocumentElement().getElementsByTagName("row");

        Member[] members = new Member[list.getLength()];
        for (int i = 0; i < list.getLength(); i++) {
            Node n = list.item(i);
            members[i] = new Member(
                    get(n, HANDLE), getInt(n, RATING), getInt(n, VOL), getInt(n, NUM));
        }
        Arrays.sort(members);

        String[] s = FileSystemUtils.read("list.txt").split("\n");
        List<Member> top = new ArrayList<>();
        for (String value : s) {
            String name = value.split(" ")[0];
            int j = Arrays.binarySearch(members, new Member(name, 0, 0, 0));
            if (j >= 0) {
                top.add(members[j]);
            } else {
                top.add(new Member(name, 0, 0, 0));
            }
        }
        return top;
    }

    private static int getInt(Node node, String s) {
        try {
            return Integer.parseInt(get(node, s));
        } catch (Exception e) {return 0;}
    }

    private static String get(Node node, String s) {
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeName().equals(s)) {
                return list.item(i).getChildNodes().item(0).getNodeValue();
            }
        }
        return null;
    }

    private static void updateList() {
//        ReadWriter.write("mar.xml", HttpDownloader.readSite(MAR_LIST, "ISO-8859-1"));
//        ReadWriter.write("tc.xml", HttpDownloader.readSite(CODERS, "ISO-8859-1"));
    }
}
