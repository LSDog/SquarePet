package fun.LSDog.SquarePet.enums;

import com.sun.media.sound.JavaSoundAudioClip;
import fun.LSDog.SquarePet.objects.Sound;
import fun.LSDog.SquarePet.utils.FileUtil;
import sun.applet.AppletAudioClip;
import sun.audio.AudioPlayer;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.applet.AudioClip;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public enum Sounds {

    HIT("res/hit.wav");

    private final String path;
    private Sound sound = null;

    Sounds(String path) {

        this.path = path;
        URL url = FileUtil.getResource(path);

        try {
            sound = new Sound(path, url.openStream());
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        if (sound != null) {
            sound.reset();
            sound.play();
        }
    }

    public Sound getSound() {
        return sound;
    }

    public static void init() {
        for (Sounds s : Sounds.values()) {
            System.out.println("Sounds >> load sound: " + s.path);
        }
    }

}
