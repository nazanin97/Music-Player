import java.awt.*;
import java.io.File;

public class Lyrics {
    private static int fontSize;
    private static String color;
    private static String fontName;
    private File text;
    public Lyrics(){

    }

    public static void setFontSize(int fontSize) {
        Lyrics.fontSize = fontSize;
    }

    public static void setFontName(String fontName) {
        Lyrics.fontName = fontName;
    }

    public static void setColor(String color) {

        Lyrics.color = color;
    }

    public static int getFontSize() {
        return fontSize;
    }

    public static String getColor() {
        return color;
    }

    public static String getFontName() {
        return fontName;
    }

    public File getText() {
        return text;
    }
}
