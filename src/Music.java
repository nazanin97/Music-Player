import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Music extends JPanel{
    private int rating;
    private String title;
    private Lyrics lyrics;
    private String artist;
    private String album;
    private String path;
    private long time;
    private String genre;
    private byte[] albumImageData;
    private boolean recentlyPlayed;
    private int numberOfPlays;

    public String getTitle() {
        return title;
    }

    public byte[] getAlbumImageData() {
        return albumImageData;
    }

    public boolean isRecentlyPlayed() {
        return recentlyPlayed;
    }

    public int getNumberOfPlays() {
        return numberOfPlays;
    }

    public String getGenre() {
        return genre;
    }

    public String getTime() {
        long min = time / 60;
        long sec = time % 60;
        return min + ":" + sec;
    }

    public void setRecentlyPlayed(boolean recentlyPlayed) {
        this.recentlyPlayed = recentlyPlayed;
    }

    public void setNumberOfPlays(int numberOfPlays) {
        this.numberOfPlays = numberOfPlays;
    }

    public Music(String dir) throws IOException, UnsupportedTagException, com.mpatric.mp3agic.InvalidDataException {

        this.path = dir;
        Mp3File mp3file = new Mp3File(dir);
        time = mp3file.getLengthInSeconds();

        if (mp3file.hasId3v1Tag()) {
            ID3v1 id3v1Tag = mp3file.getId3v1Tag();
            this.artist = id3v1Tag.getArtist();
            this.title = id3v1Tag.getTitle();
            this.album = id3v1Tag.getAlbum();
            this.genre = id3v1Tag.getGenreDescription();
        }

        if (mp3file.hasId3v2Tag()) {
            ID3v2 id3v2Tag = mp3file.getId3v2Tag();

            if (this.artist == "")
                this.artist = id3v2Tag.getArtist();
            if (this.title == "")
                this.title = id3v2Tag.getTitle();
            if (this.album == "")
                this.album = id3v2Tag.getAlbum();
//            if (this.genre == "" || this.genre.toLowerCase()=="unknown")
//                this.genre = id3v2Tag.getGenreDescription();

            albumImageData = id3v2Tag.getAlbumImage();

        }
        System.out.println("Title = " + title);
        System.out.println("Album = " + album);
        System.out.println("Time = " + time);
        System.out.println("Artist = " + artist);
        System.out.println("Genre = " + genre);
        makeMusicPanel();
    }
    private void makeMusicPanel(){

        this.setLayout(new FlowLayout());

        this.setMinimumSize(new Dimension(700, 40));
        this.setMaximumSize(new Dimension(700, 40));

        JLabel[] labels = new JLabel[4];

        for (int i = 0; i < 4; i++) {
            labels[i] = new JLabel();
            labels[i].setPreferredSize(new Dimension(100, 40));
            labels[i].setHorizontalAlignment(SwingConstants.CENTER);
        }

        labels[0].setText(getTitle());
        labels[1].setText(getTime());
        labels[2].setText(getArtist());
        labels[3].setText(getGenre());
        JPanel hold = new JPanel();
        hold.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        hold.setPreferredSize(new Dimension(400, 40));
        for (int j = 0; j < 4; j++) {
            hold.add(labels[j]);
        }
        JPanel stars = new JPanel();
        stars.setPreferredSize(new Dimension(300, 40));
        stars.setLayout(new FlowLayout(FlowLayout.CENTER));
        stars.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 50));

        for (int i = 0; i < 5; i++) {
            Star button = new Star();
            stars.add(button);
        }
        this.add(hold);
        this.add(stars);
    }
    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setLyrics(Lyrics lyrics) {
        this.lyrics = lyrics;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getRating() {
        return rating;
    }

    public Lyrics getLyrics() {
        return lyrics;
    }

    public String getArtist() {
        return artist;
    }

    public String getAlbum() {
        return album;
    }

    public String getPath() {
        return path;
    }
    public static void saveMusics(ArrayList<Music> musics){
        ArrayList<MusicInfo>musicInfos = new ArrayList<>();

        for (Music d:musics) {
            MusicInfo musicInfo = new MusicInfo(d);
            musicInfos.add(musicInfo);
        }

        writeInfos(musicInfos);
    }
    private static void writeInfos(ArrayList<MusicInfo>m){
        try{
            FileOutputStream fileOut = new FileOutputStream("songs.info");
            ObjectOutputStream oos = new ObjectOutputStream (fileOut);
            oos.writeObject(m);
            oos.close();
            fileOut.close();

        }catch(Exception e){
            System.err.println(e.getMessage());
        }

    }

}
