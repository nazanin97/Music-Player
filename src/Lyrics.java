import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Lyrics {
    private static int fontSize;
    private static Color color;
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

    public static void setColor(Color color) {

        Lyrics.color = color;
    }

    public static int getFontSize() {
        return fontSize;
    }

    public static Color getColor() {
        return color;
    }

    public static String getFontName() {
        return fontName;
    }

    public File getText() {
        return text;
    }
    public static String readFileAsString(String fileName)throws Exception
    {
        String data = "";
        data = new String(Files.readAllBytes(Paths.get(fileName)));
        return data;
    }



    public static String lyricCleaner(String path){
        String lyric = "";

        try {
            FileInputStream fstream = new FileInputStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String strLine;

            while ((strLine = br.readLine()) != null)   {
                if (strLine.length() > 0) {
                    if (strLine.substring(0, 1).equals("[")) ;
                    {
                        int endIndex = strLine.indexOf("]");
                        strLine = strLine.substring(endIndex + 1, strLine.length());
                        lyric += (strLine + '\n');
                    }
                }

            }

            fstream.close();

        }
        catch (Exception e) {
            System.out.println("execption = " + e);
        }
        return lyric.trim();
    }

    public String lyricDecoder(String path){

        String lyrics = "";

        try {
            FileInputStream fstream = new FileInputStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String strLine;

            while ((strLine = br.readLine()) != null)   {
                if (strLine.length() > 0) {
                    if (strLine.startsWith("[")){
                        if (strLine.substring(1,2).equals("0")){
                            lyrics += strLine + '\n';
                        }
                    }
                }

            }

            fstream.close();

        }
        catch (Exception e) {
            System.out.println("execption = " + e);
        }

        return lyrics;
    }


    public String lyricAdjuster(String path, long currentTime){
        String decoded = lyricDecoder(path);

        String[] lines = decoded.split("\n");
        int num = 5;

        String res = "";
        int j = -1;
        for (int i = 0; i < lines.length; i++) {
            int min = Integer.parseInt(lines[i].substring(1, 3));
            int sec = Integer.parseInt(lines[i].substring(4, 6));
            int msec = Integer.parseInt(lines[i].substring(7, 9));
            long time = (min * 60 + sec) * 1000 + msec;

            if (time > currentTime) {
                j = i;
                break;
            }
        }
        if (j == -1 || j > lines.length - 3)
            j = lines.length - 3;

        if (j < 2)
            j = 2;


        for (int i = 0; i < num; i++) {

            try {
                String line = lines[j-num+i+3];
                res += line.substring(10) + '\n';
            } catch (StringIndexOutOfBoundsException e){
                res += '\n';
            }


        }
        return res;
    }
}
