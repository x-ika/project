package pool.utils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.util.*;
import java.io.File;

public class SoundManager {

    private static int zoomClip;

    private static int ballsClip;

    private static int borderClip;

    private static int pushcueClip;

    private static int pocketClip;

    private static int gameClip;

    public static void createAudioClips() {
        zoomClip = SoundManager.getInstance().loadResource("zoom.wav");
        ballsClip = SoundManager.getInstance().loadResource("balls.wav");
        borderClip = SoundManager.getInstance().loadResource("border.wav");
        pushcueClip = SoundManager.getInstance().loadResource("blow.wav");
        pocketClip = SoundManager.getInstance().loadResource("pocket.wav");
        gameClip = SoundManager.getInstance().loadResource("pool.mid", true);
    }

    public static void playZoom() {
        SoundManager.getInstance().play(zoomClip);
    }

    public static void playBalls() {
        SoundManager.getInstance().play(ballsClip);
    }

    public static void playBorder() {
        SoundManager.getInstance().play(borderClip);
    }

    public static void playPushcue() {
        SoundManager.getInstance().play(pushcueClip);
    }

    public static void playPocket() {
        SoundManager.getInstance().play(pocketClip);
    }

    public static void playMain() {
        SoundManager.getInstance().play(gameClip);
    }

    //-----------------------------------------------------------------------------------

    private static class MyClip {
        private byte[] audio;
        private AudioFormat format;
        private Clip audioClip;
        boolean autoPlayMode;

        MyClip(String fileName, boolean autoPlayMode) {
            this.autoPlayMode = autoPlayMode;
            try {
                AudioInputStream ais = AudioSystem.getAudioInputStream(new File(Consts.SOUND_PATH + fileName));
                format = ais.getFormat();
                audio = new byte[(int) 1e5];
                audio = Arrays.copyOf(audio, ais.read(audio));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        void play(final boolean loop) {
            new Thread() {
                public void run() {
                    try {
                        audioClip = AudioSystem.getClip();
                        audioClip.open(format, audio, 0, audio.length);
                        audioClip.loop(loop ? Integer.MAX_VALUE : 0);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        void stop() {
            if (audioClip != null) {
                audioClip.stop();
            }
        }
    }

    private static SoundManager instance = new SoundManager();

    public static SoundManager getInstance() {
        return instance;
    }

    private List<MyClip> audioClips = new ArrayList<>();

    private boolean soundEnabled;

    private SoundManager() {
    }

    public synchronized int loadResource(String fileName, boolean autoPlayMode) {
        int descriptor = audioClips.size();
        audioClips.add(new MyClip(fileName, autoPlayMode));
        return descriptor;
    }

    public int loadResource(String fileName) {
        return loadResource(fileName, false);
    }

    public void setMusicFlag(boolean musicFlag) {
        soundEnabled = musicFlag;
        for (MyClip clip : audioClips) {
            if (clip.autoPlayMode == soundEnabled) {
                if (soundEnabled) {
                    clip.play(true);
                } else {
                    clip.stop();
                }
            }
        }
    }

    public void play(int descriptor) {
        play(descriptor, false);
    }

    public void play(int descriptor, boolean loop) {
        if (!soundEnabled) {
            return;
        }
        audioClips.get(descriptor).play(loop);
    }

    public void stop(int descriptor) {
        audioClips.get(descriptor).stop();
    }

    public void dispose() {
        for (MyClip clip : audioClips) {
            clip.stop();
        }
    }
}
