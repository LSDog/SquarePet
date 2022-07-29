package fun.LSDog.SquarePet.animations;

import java.awt.*;
import java.util.function.Consumer;

public class FadeOpacity {

    public static void fade(Component component, final double start, double end, final int steps, long stepTime, Consumer<Component> after) {
        if (!(component instanceof Window)) return;
        Window window = (Window) component;
        try {
            window.setOpacity((float) start);
        } catch (Exception e) {
            return;
        }
        if (steps < 0) return;

        new Thread(() -> {
            double d = end - start;
            double s = d / steps;
            double now = start;
            if (d > 0) s = Math.abs(s); else s = - Math.abs(s);
            try {
                while ((d < 0 && now > end) || (d > 0 && now < end)) {
                    Thread.sleep(stepTime);
                    now = (float) (now + s);
                    if (d > 0) now = Math.min(now, end); else now = Math.max(now, end);
                    ((Window) component).setOpacity((float) now);
                }
                if (after != null) after.accept(component);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
