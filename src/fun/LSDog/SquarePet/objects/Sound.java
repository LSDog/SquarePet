package fun.LSDog.SquarePet.objects;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class Sound implements Closeable {

    private final String name;

    private final Clip clip;

    private final FloatControl gainControl;
    private float defaultGain = 0;
    private final FloatControl panControl;
    private float defaultPan = 0;

    private int tagFrame = 0;

    public Sound(String name, InputStream inputStream) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        this.name = name;
        AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream));
        this.clip = AudioSystem.getClip();
        clip.open(audioIn);
        this.gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        this.panControl = (FloatControl) clip.getControl(FloatControl.Type.PAN);
    }

    public synchronized void play() {
        clip.setFramePosition(tagFrame);
        clip.start();
    }

    public void stop() {
        clip.stop();
        tagFrame = getFramePosition();
    }

    public synchronized void reset() {
        //if (clip.isRunning()) clip.stop();
        clip.stop();
        tagFrame = 0;
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

    public void setDefaultGain(float defaultGain) {
        this.defaultGain = defaultGain;
    }

    public void resetGain() {
        gainControl.setValue(defaultGain);
    }

    public float getPan() {
        return panControl.getValue();
    }

    public void setPan(float balance) {
        panControl.setValue(balance);
    }

    public void setDefaultPan(float defaultPan) {
        this.defaultPan = defaultPan;
    }

    public void resetPan() {
        panControl.setValue(defaultPan);
    }

    public boolean isPlaying() {
        return clip.isRunning();
    }

    public boolean isClosed() {
        return !clip.isOpen();
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
