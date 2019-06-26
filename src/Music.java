import com.mpatric.mp3agic.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
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
    private ArrayList<Star>starsButtons;
    private File f;
    public int offset = 0;


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

    public Music(String dir) throws InvalidDataException, IOException, UnsupportedTagException {

        this.path = dir;
        extractMetaData(dir);
        makeMusicPanel();
    }
    private void extractMetaData(String dir) throws InvalidDataException, UnsupportedTagException, IOException{
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
            if (this.genre == "" || this.genre.toLowerCase() == "unknown")
                this.genre = id3v2Tag.getGenreDescription();

            albumImageData = id3v2Tag.getAlbumImage();
        }

    }
    private void makeMusicPanel(){
        this.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (GUI.nowPlaying != null) {
                    //if (GUI.nowPlaying.offset != 0)
                    GUI.p.pause();
                    GUI.nowPlaying.offset = 0;

                    GUI.nowPlaying = Music.this;
                    GUI.p = new Play(0, Music.this);
                    GUI.p.start();
                }

                else
                    GUI.nowPlaying = Music.this;

                recentlyPlayed = true;
                Border border = BorderFactory.createLineBorder(Color.BLUE, 1);
                for (Music m:GUI.songs) {
                    m.setBorder(null);
                }
                Music.this.setBorder(border);
            }
        });
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

        starsButtons = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Star button = new Star();
            starsButtons.add(button);
            stars.add(button);
        }
        this.add(hold);
        this.add(stars);
    }
    public void setStarsButtons(){
        for (int i = 0; i < rating; i++) {
            starsButtons.get(i).setMode(2);
        }
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public void setRating(){
        int count = 0;
        for (int i = 0; i < 5; i++) {
            if(starsButtons.get(i).getMode() == 2){
                count++;
            }
        }
        setRating(count);
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

    public static void saveMusics(ArrayList<Music> musics, ArrayList<Playlist>playlists){
        ArrayList<MusicInfo>musicInfos1 = new ArrayList<>();

        ArrayList<ArrayList<MusicInfo>> playlistInfos = new ArrayList<>();

        for (Music d:musics) {
            d.setRating();
            MusicInfo musicInfo = new MusicInfo(d);
            musicInfos1.add(musicInfo);
        }
        for (Playlist p:playlists) {
            ArrayList<MusicInfo>musicInfos2 = new ArrayList<>();
            for (Music m:p.getMusics()) {
                m.setRating();
                MusicInfo musicInfo = new MusicInfo(m);
                musicInfos2.add(musicInfo);
            }
            playlistInfos.add(musicInfos2);
        }

        try{
            FileOutputStream fileOut = new FileOutputStream("songs.info");
            ObjectOutputStream oos = new ObjectOutputStream (fileOut);
            oos.writeObject(musicInfos1);
            oos.close();
            fileOut.close();

        }catch(Exception e){
            System.err.println(e.getMessage());
        }

        int k = 1;
        for (ArrayList a:playlistInfos) {
            try{
                FileOutputStream fileOut = new FileOutputStream("./playLists/playlist" + k + ".info");
                ObjectOutputStream oos = new ObjectOutputStream (fileOut);
                oos.writeObject(a);
                oos.close();
                fileOut.close();

            }catch(Exception e){
                System.err.println(e.getMessage());
            }
            k++;
        }
    }
}
