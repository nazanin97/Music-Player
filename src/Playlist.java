import java.util.ArrayList;

public class Playlist {
    ArrayList<Music>musics;
    private String name;
    public Playlist(String name){
        musics = new ArrayList<>();
        this.name = name;
    }
    public void addMusic(Music music){
        musics.add(music);
    }
    public void removeMusic(){

    }

    public ArrayList<Music> getMusics() {
        return musics;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
