package util;

import javax.media.*;
import javax.media.control.TrackControl;
import javax.media.format.*;
import javax.media.protocol.*;

/**
 * Sample class to access individual video frames.
 */
public final class FrameAccess implements ControllerListener, BufferTransferHandler {

    private final ImageHandler handler;

    private int imgWidth, imgHeight;
    private boolean stateTransitionOK = true;
    private Processor p;

    public FrameAccess(ImageHandler handler) {
        this.handler = handler;
    }

    public synchronized void open(String file) throws Exception {
        p = Manager.createProcessor(new MediaLocator("file:" + file));
        p.addControllerListener(this);
        p.configure();
        while (p.getState() != Processor.Configured && stateTransitionOK) wait();
        p.setContentDescriptor(new ContentDescriptor(ContentDescriptor.RAW));
        TrackControl[] tc = p.getTrackControls();
        for (TrackControl track : tc) {
            if (track.getFormat() instanceof VideoFormat) {
                track.setFormat(new RGBFormat(null, -1, Format.intArray, -1.0F, 32, 255 << 16, 255 << 8, 255));
            } else {
                track.setEnabled(false);
            }
        }
        p.realize();
        while (p.getState() != Controller.Realized && stateTransitionOK) wait();
        DataSource ods = p.getDataOutput();
        PushBufferStream[] pushStrms = ((PushBufferDataSource) ods).getStreams();
        pushStrms[0].setTransferHandler(this);
        VideoFormat vidFormat = (VideoFormat) pushStrms[0].getFormat();
        imgWidth = vidFormat.getSize().width;
        imgHeight = vidFormat.getSize().height;
        ods.start();
    }

    public int getImageWidth() {
        return imgWidth;
    }

    public int getImageHeight() {
        return imgHeight;
    }

    public void start() {
        p.start();
    }

    public void stop() {
        p.stop();
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
            p.close();
        }
    }

    public void transferData(PushBufferStream stream) {
        try {
            Buffer readBuffer = new Buffer();
            stream.read(readBuffer);
            if (!readBuffer.isEOM()) {
                handler.processImage((int[]) readBuffer.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
