package fun.LSDog.SquarePet;

import fun.LSDog.SquarePet.enums.Sounds;
import fun.LSDog.SquarePet.objects.Sound;
import fun.LSDog.SquarePet.objects.Square;
import fun.LSDog.SquarePet.utils.FileUtil;

import javax.sound.sampled.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main {


    public static void main(String[] args) {

        System.out.println("SquarePet by LSDog (sh*t code hah)");

        Square frame = new Square(60, 0.5, 0);

        URL iconUrl = FileUtil.getResource("res/icon.png");
        if (iconUrl != null) {
            frame.setIconImage(frame.getToolkit().getImage(iconUrl));
        }

        Sounds.init();
        Sounds.HIT.getSound().setDefaultPan(-0.15F);
        Sounds.HIT.getSound().resetPan();

        frame.setVisible(true);

        frame.tip.showHtmlText(
        "<html>" +
                "&lt;LSDog>" +
                "<br>- 试着拖动我.. " +
                "<br>- 划起来也好... 但是.. 请不要戳我太快////" +
                "<br>- 按Esc退出." +
                "<br>- 按→←改变摩擦." +
                "<br>- 按↑↓改变重力." +
                "</html>", 7_000);

        //Timer gcTimer = new Timer();
        //gcTimer.schedule(new TimerTask() {
        //    @Override
        //    public void run() {
        //        System.gc();
        //        System.out.println("gc");
        //    }
        //}, 0, 60000);

    }

}
