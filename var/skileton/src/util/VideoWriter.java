package util;

import javax.imageio.ImageIO;
import javax.media.*;
import javax.media.control.TrackControl;
import javax.media.datasink.*;
import javax.media.format.VideoFormat;
import javax.media.protocol.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public final class VideoWriter
        implements ControllerListener, DataSinkListener, PullBufferStream
{

    private static final String PATH = "out";

    private int index = 0;
    private boolean stateTransitionOK = true;
    private VideoFormat format;
    private DataSink dsink;
    private Processor p;

    public synchronized void writeMovie(int rate) throws Exception {
        BufferedImage f = ImageIO.read(new File(PATH).listFiles()[0]);
        format = new VideoFormat(
                VideoFormat.JPEG, new Dimension(f.getWidth(), f.getHeight()),
                Format.NOT_SPECIFIED, Format.byteArray, rate);
        p = Manager.createProcessor(new ImageDataSource());
        p.addControllerListener(this);
        p.configure();
        while (p.getState() != Processor.Configured && stateTransitionOK) wait();
        p.setContentDescriptor(new ContentDescriptor(FileTypeDescriptor.QUICKTIME));
        TrackControl tc = p.getTrackControls()[0];
        tc.setFormat(tc.getSupportedFormats()[0]);
        p.realize();
        while (p.getState() != Processor.Realized && stateTransitionOK) wait();
        dsink = Manager.createDataSink(p.getDataOutput(), new MediaLocator("file:xxx.avi"));
        dsink.open();
        dsink.addDataSinkListener(this);
        p.start();
        dsink.start();
    }

    public synchronized void controllerUpdate(ControllerEvent evt) {
        if (evt instanceof ConfigureCompleteEvent ||
                evt instanceof RealizeCompleteEvent ||
                evt instanceof PrefetchCompleteEvent)
        {
            stateTransitionOK = true;
            notifyAll();
        } else if (evt instanceof ResourceUnavailableEvent) {
            stateTransitionOK = false;
            notifyAll();
        } else if (evt instanceof EndOfMediaEvent) {
            evt.getSourceController().stop();
            evt.getSourceController().close();
        }
    }

    public void dataSinkUpdate(DataSinkEvent evt) {
        if (evt instanceof EndOfStreamEvent) {
            dsink.close();
            p.removeControllerListener(this);
        }
    }

    public boolean willReadBlock() {
        return false;
    }

    public void read(Buffer buf) throws IOException {
        if (index >= new File(PATH).list().length) {
            buf.setEOM(true);
            buf.setOffset(0);
            buf.setLength(0);
            return;
        }
        File file = new File(PATH).listFiles()[index++];
        byte[] data = new byte[(int) file.length()];
        buf.setData(data);
        buf.setOffset(0);
        buf.setLength(new FileInputStream(file).read(data));
        buf.setFormat(format);
        buf.setFlags(buf.getFlags() | Buffer.FLAG_KEY_FRAME);
    }

    public Format getFormat() {
        return format;
    }

    public ContentDescriptor getContentDescriptor() {
        return new ContentDescriptor(ContentDescriptor.RAW);
    }

    public long getContentLength() {
        return 0;
    }

    public boolean endOfStream() {
        return index >= new File(PATH).list().length;
    }

    public Object[] getControls() {
        return new Object[0];
    }

    public Object getControl(String type) {
        return null;
    }

    private class ImageDataSource extends PullBufferDataSource {
        public void setLocator(MediaLocator source) {
        }

        public MediaLocator getLocator() {
            return null;
        }

        public String getContentType() {
            return ContentDescriptor.RAW;
        }

        public void connect() {
        }

        public void disconnect() {
        }

        public void start() {
        }

        public void stop() {
        }

        public PullBufferStream[] getStreams() {
            return new PullBufferStream[]{VideoWriter.this};
        }

        public Time getDuration() {
            return DURATION_UNKNOWN;
        }

        public Object[] getControls() {
            return new Object[0];
        }

        public Object getControl(String type) {
            return null;
        }
    }
}
