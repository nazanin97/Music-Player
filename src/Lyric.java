public class Lyric {
    private static Lyric ourInstance = new Lyric();

    public static Lyric getInstance() {
        return ourInstance;
    }

    private Lyric() {

    }
}
