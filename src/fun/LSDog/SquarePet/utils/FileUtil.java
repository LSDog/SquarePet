package fun.LSDog.SquarePet.utils;

import fun.LSDog.SquarePet.Main;

import java.io.File;
import java.net.URL;

public class FileUtil {

    public static URL getResource(String file) {
        if (file == null) return null;
        try {
            File iconFile = new File(file);
            if (iconFile.exists()) {
                return iconFile.toURI().toURL();
            }
            if (!file.startsWith("/")) file = "/" + file;
            URL url = FileUtil.class.getResource(file);
            if (url != null) {
                return url;
            }
        } catch (Exception ignore) {
        }
        System.err.println("Unable to find file in jar or root folder: '"+file+"'");
        return null;
    }

}
