package util;

import com.simplejcode.commons.misc.util.*;

import java.io.*;
import java.net.URLEncoder;

public class Utils {

    public static int find(String source, int index, String template) {
        index = source.indexOf(template, index);
        return index == -1 ? -1 : index + template.length();
    }

    public static int readInt(String s, int index) {
        while (s.charAt(index) <= 32) {
            index++;
        }
        int ret = 0;
        while (Character.isDigit(s.charAt(index))) {
            ret *= 10;
            ret += s.charAt(index++) - '0';
        }
        return ret;
    }

    public static String readString(String s, int index) {
        String ret = "";
        while (Character.isLetterOrDigit(s.charAt(index)) || s.charAt(index) != '<') {
            ret += s.charAt(index++);
        }
        return ret;
    }

    public static String readUrl(String url, String charset, boolean useCache) {
        if (!useCache) {
            return HttpUtils.get(url, charset).getResponse();
        }
        final String encoded;
        try {
            encoded = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("can not encode the url");
            return null;
        }
        File dir = new File("resources\\cache\\");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.equals(encoded);
            }
        });
        if (files.length > 0) {
            try {
                return IOUtils.read(new FileInputStream(files[0]), charset);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        String ret = HttpUtils.get(url, charset).getResponse();
        IOUtils.write(dir.getAbsolutePath() + "\\" + encoded, charset, ret);
        return ret;
    }

}
