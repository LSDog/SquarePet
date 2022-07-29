package fun.LSDog.SquarePet.utils;

import java.awt.*;

public class ScreenUtil {

    private static final Toolkit toolkit = Toolkit.getDefaultToolkit();
    private static final Dimension SCREEN_SIZE = toolkit.getScreenSize();

    public static Dimension getScreenSize() {
        return SCREEN_SIZE;
    }

    public static int getXby(double percent) {
        return Math.toIntExact(Math.round(SCREEN_SIZE.width * percent));
    }

    public static int getYby(double percent) {
        return Math.toIntExact(Math.round(SCREEN_SIZE.height * percent));
    }

    public static void setLocationCenter(Component component, double xPercent, double yPercent) {
        component.setLocation(getXby(0.5)-component.getWidth()/2, getYby(0.5)-component.getHeight()/2);
    }

}
