package fun.LSDog.SquarePet.enums;

import fun.LSDog.SquarePet.objects.Sound;
import fun.LSDog.SquarePet.utils.FileUtil;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.net.URL;

public enum Sounds {

    HIT("res/hit.wav");

    private Sound sound = null;

    Sounds(String path) {

        URL url = FileUtil.getResource(path);

        try {
            sound = new Sound(path, url.openStream());
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        new Thread(() -> {
            if (sound != null) {
                sound.reset();
                sound.play();
            }
        }).start();
    }

    public Sound getSound() {
        return sound;
    }

    public static void init() {
        for (Sounds s : Sounds.values()) {
            System.out.println("Sounds >> load sound: " + s.getSound().getName());
        }
    }

}
