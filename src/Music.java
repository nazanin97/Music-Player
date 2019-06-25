import com.mpatric.mp3agic.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import javax.sound.sampled.*;
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

import static javax.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
import static javax.sound.sampled.AudioSystem.getAudioInputStream;

public class Music extends JPanel{
//    private String status="";
//    private File f;
//    private Long currentFrame;
//    private Clip clip;
//    private AudioInputStream audioInputStream;

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

    public Music(String dir) throws InvalidDataException, IOException, UnsupportedTagException, UnsupportedAudioFileException, LineUnavailableException, CannotReadException, ReadOnlyFileException, InvalidAudioFrameException, TagException {

        this.path = dir;
        extractMetaData(dir);
        makeMusicPanel();
    }
    //    private void extractMetaData(String dir) throws IOException, CannotReadException, TagException, InvalidAudioFrameException
//            , ReadOnlyFileException,UnsupportedAudioFileException,
//            IOException, LineUnavailableException
//
//    {
//        this.path = dir;
//        this.f = new File(dir);
//        AudioFile audioFile = AudioFileIO.read(new File(dir));
//        this.artist = audioFile.getTag().getFirst(FieldKey.ARTIST);
//        this.title = audioFile.getTag().getFirst(FieldKey.TITLE);
//        this.album = audioFile.getTag().getFirst(FieldKey.ALBUM);
//        this.genre = audioFile.getTag().getFirst(FieldKey.GENRE);
//        Tag tag = audioFile.getTag();
//        this.time = audioFile.getAudioHeader().getTrackLength() ;
//        // create AudioInputStream object
//        this.audioInputStream =
//                AudioSystem.getAudioInputStream(new File(dir).getAbsoluteFile());
//
//        // create clip reference
//        this.clip = AudioSystem.getClip();
//
//        // open audioInputStream to the clip
//        clip.open(audioInputStream);
//
//        clip.loop(Clip.LOOP_CONTINUOUSLY);
//
//
//
//
//        System.out.println("Title = " + title);
//        System.out.println("Album = " + album);
//        System.out.println("Time = " + time);
//        System.out.println("Artist = " + artist);
//        System.out.println("Genre = " + genre);
//
//    }
    private void extractMetaData(String dir) throws InvalidDataException, IOException, UnsupportedTagException, CannotReadException , TagException ,InvalidAudioFrameException
            ,ReadOnlyFileException ,UnsupportedAudioFileException,
            IOException, LineUnavailableException{
        this.path = dir;
        this.f = new File(dir);
        AudioFile audioFile = AudioFileIO.read(new File(dir));
        this.artist = audioFile.getTag().getFirst(FieldKey.ARTIST);
        this.title = audioFile.getTag().getFirst(FieldKey.TITLE);
        this.album = audioFile.getTag().getFirst(FieldKey.ALBUM);
        this.genre = audioFile.getTag().getFirst(FieldKey.GENRE);
        Tag tag = audioFile.getTag();
        this.time = audioFile.getAudioHeader().getTrackLength() ;
        System.out.println("Title = " + title);
        System.out.println("Album = " + album);
        System.out.println("Time = " + time);
        System.out.println("Artist = " + artist);
        System.out.println("Genre = " + genre);
    }
    private void makeMusicPanel(){
        this.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                GUI.nowPlaying = Music.this;
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
        for (int i = 0; i < 4; i++) {
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

    public static void saveMusics(ArrayList<Music> musics){
        ArrayList<MusicInfo>musicInfos = new ArrayList<>();

        for (Music d:musics) {
            d.setRating();
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
    public void playMusic() {

        numberOfPlays++;
//        try (final AudioInputStream in = getAudioInputStream(f)) {
//
//            final AudioFormat outFormat = getOutFormat(in.getFormat());
//            final DataLine.Info info = new DataLine.Info(SourceDataLine.class, outFormat);
//
//            try (final SourceDataLine line =
//                         (SourceDataLine) AudioSystem.getLine(info)) {
//
//                if (line != null) {
//                    line.open(outFormat);
//                    line.start();
//                    stream(getAudioInputStream(outFormat, in), line);
//                    line.drain();
//                    line.stop();
//                }
//            }
//
//        } catch (UnsupportedAudioFileException
//                | LineUnavailableException
//                | IOException e) {
//            throw new IllegalStateException(e);
//        }
    }




    private AudioFormat getOutFormat(AudioFormat inFormat) {
        final int ch = inFormat.getChannels();
        final float rate = inFormat.getSampleRate();
        return new AudioFormat(PCM_SIGNED, rate, 16, ch, ch * 2, rate, false);
    }

    private void stream(AudioInputStream in, SourceDataLine line)
            throws IOException {
        final byte[] buffer = new byte[65536];
        for (int n = 0; n != -1; n = in.read(buffer, 0, buffer.length)) {
            line.write(buffer, 0, n);
        }
    }
    public void pauseMusic(){

    }
}
