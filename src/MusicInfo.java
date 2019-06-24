import java.io.Serializable;

public class MusicInfo implements Serializable {

    public String path;
    public boolean recent;
    public int rating;
    public int numPlays;

    MusicInfo(Music music){

        path = music.getPath();
        recent = music.isRecentlyPlayed();
        rating = music.getRating();
        numPlays = music.getNumberOfPlays();
    }
}
