package util;

import javax.media.*;
import javax.media.format.VideoFormat;

public class CameraVEC245 {
    public static void main(String[] args) {
        for (Object o : CaptureDeviceManager.getDeviceList(null)) {
            CaptureDeviceInfo info = (CaptureDeviceInfo) o;
            System.out.println(info.getName());
            for (Format format : info.getFormats()) {
                if (format instanceof VideoFormat) {
                    System.out.println(format.toString());
                }
            }
            System.out.println("");
        }
    }
}
