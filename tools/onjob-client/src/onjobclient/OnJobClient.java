package onjobclient;

import com.simplejcode.commons.misc.util.FileSystemUtils;

import java.io.*;
import java.net.*;

public class OnJobClient {

    private static String encode(String param) throws Exception {
        StringBuilder t = new StringBuilder();
        for (byte b : param.getBytes("UTF-8")) {
            t.append("%").append(String.format("%02X", b));
        }
        return t.toString();
    }

    public static int add(String first, String last, String mobile, String cookie) throws Exception {

        StringBuilder params = new StringBuilder();

        params.append("FirstName=").append(encode(first)).append('&');
        params.append("LastName=").append(encode(last)).append('&');
        params.append("MobilePhone=").append(mobile).append('&');
        params.append("MobilePhone2=").append("").append('&');
        params.append("Comment=").append("").append('&');
        params.append("IsMotor=").append("true").append('&');
        params.append("IsMotor=").append("false").append('&');
        params.append("IsProperty=").append("true").append('&');
        params.append("IsProperty=").append("false").append('&');
        params.append("IsAgro=").append("false").append('&');
        params.append("CityId=").append("").append('&');
        params.append("save=").append("Save").append('&');

        return postRequest("http://onjob.ge/Home/CreateClient", params.toString(), cookie);

    }

    private static int postRequest(String url, String data, String cookie) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        conn.setRequestProperty("Content-Length", "" + data.getBytes().length);
        conn.setRequestProperty("Cookie", cookie);
        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
        wr.write(data);
        wr.flush();
        wr.close();

        FileSystemUtils.read(conn.getInputStream());
        return conn.getResponseCode();
    }

}
