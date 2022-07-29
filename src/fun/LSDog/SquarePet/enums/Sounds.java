package fun.LSDog.SquarePet.enums;

import fun.LSDog.SquarePet.utils.FileUtil;
import sun.audio.AudioPlayer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public enum Sounds {

    HIT("/res/hit.wav");

    private final String path;
    private final URL url;
    private byte[] byteSound;

    Sounds(String path) {

        this.path = path;
        this.url = FileUtil.getResource(path);

        try {

            InputStream audioIn = url.openStream();

            int i;
            byte[] buff = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while ((i = audioIn.read(buff)) != -1) {
                out.write(buff, 0, i);
            }
            this.byteSound = out.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private InputStream getAudioIn() throws IOException {
        return new ByteArrayInputStream(byteSound);
    }

    public void play() {
        if (url == null || byteSound == null) return;
        AudioPlayer.player.start(new ByteArrayInputStream(byteSound));
    }

    public static void init() {
        for (Sounds s : Sounds.values()) {
            System.out.println("Sounds >> load sound: " + s.path);
            s.play();
        }
    }

}
