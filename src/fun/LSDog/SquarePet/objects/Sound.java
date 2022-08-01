package fun.LSDog.SquarePet.objects;

import javax.sound.sampled.*;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class Sound implements Closeable {

    private final String name;

    private final Clip clip;

    private final FloatControl gainControl;

    private boolean stopped = true;
    private boolean closed = false;

    private int startFrame = 0;

    public Sound(String name, InputStream inputStream) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        this.name = name;
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(inputStream);
        this.clip = AudioSystem.getClip();
        clip.open(audioIn);
        this.gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

        clip.addLineListener(event -> {
            //System.out.println("event - " + event.getType() + ": " + event.getFramePosition());
            LineEvent.Type type = event.getType();
            if (type == LineEvent.Type.STOP) {
                stopped = true;
            } else if (type == LineEvent.Type.CLOSE) {
                closed = true;
            }
        });
    }

    public void play() {
        clip.setFramePosition(startFrame);
        clip.start();
    }

    public void stop() {
        clip.stop();
        startFrame = getFramePosition();
    }

    public void reset() {
        if (clip.isRunning()) clip.stop();
        startFrame = 0;
    }

    public int getFramePosition() {
        return clip.getFramePosition();
    }

    public void setFramePosition(int frames) {
        clip.setFramePosition(frames);
    }

    public float getGain() {
        return gainControl.getValue();
    }

    public void setGain(float volume) {
        gainControl.setValue(volume);
    }

    public void resetGain() {
        gainControl.setValue(0);
    }

    public boolean isPlaying() {
        return clip.isRunning();
    }

    public boolean isClosed() {
        return closed;
    }

    public String getName() {
        return name;
    }

    public Clip getClip() {
        return clip;
    }

    @Override
    public void close() {
        clip.close();
    }
}
