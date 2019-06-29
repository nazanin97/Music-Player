import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

public class Playlist {
    public ArrayList<Music>musics;
    private String name;
    private JLabel addSong;
    private JLabel label;

    public Playlist(String n){
        musics = new ArrayList<>();
        name = n;
        label = new JLabel(n);
        label.setPreferredSize(new Dimension(200, 30));
        label.setHorizontalAlignment(SwingConstants.CENTER);
    }
    public Playlist(String name, JLabel l, JPanel screen){

        musics = new ArrayList<>();
        this.name = name;

        addSong = new JLabel();
        this.label = l;
        addMouseHandler(screen);


    }
    public void addMouseHandler(JPanel screen){
        Border border = BorderFactory.createLineBorder(Color.GRAY, 1);
        label.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (int i = 0; i < GUI.playlists.size(); i++) {
                    GUI.playlists.get(i).getLabel().setBorder(null);
                }
                label.setBorder(border);
                screen.removeAll();
                createTitles(screen);
                //System.out.println("clk");
                for (int i = 0; i < musics.size(); i++) {
                    screen.add(musics.get(i));
                    screen.revalidate();
                }

                GUI.repaint();
            }
        });
    }
    private void createTitles(JPanel screen){
        JPanel titles = new JPanel();
        Border border = BorderFactory.createLineBorder(Color.GRAY, 1);
        //titles.setBorder(border);
        titles.setPreferredSize(new Dimension(700, 40));
        titles.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JLabel[] topTitles = new JLabel[5];
        for (int i = 0; i < 5; i++) {
            topTitles[i] = new JLabel();
            topTitles[i].setPreferredSize(new Dimension(100, 40));
            topTitles[i].setBorder(border);
            topTitles[i].setHorizontalAlignment(SwingConstants.CENTER);
        }

        topTitles[0].setText("name");
        topTitles[1].setText("time");
        topTitles[2].setText("artist");
        topTitles[3].setText("genre");
        topTitles[4].setText("rating");
        topTitles[4].setPreferredSize(new Dimension(250, 40));

        addSong = new JLabel("+", SwingConstants.CENTER);
        addSong.setFont(new Font("Arial", Font.PLAIN, 30));
        addSong.setPreferredSize(new Dimension(50, 40));
        addSong.setBorder(border);
        addSong.setToolTipText("Add a song");
        addSong.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    addMusic();
                    GUI.repaint();
                } catch (IOException | UnsupportedTagException | InvalidDataException |
                        CannotReadException | TagException | ReadOnlyFileException |
                        LineUnavailableException | UnsupportedAudioFileException | InvalidAudioFrameException e1) {
                    e1.printStackTrace();
                }
            }
        });

        for (JLabel topTitle:topTitles) {
            titles.add(topTitle);
        }
        titles.add(addSong);
        screen.add(titles);
    }
    private void addMusic() throws IOException, UnsupportedTagException, CannotReadException,
            InvalidDataException, ReadOnlyFileException, TagException, UnsupportedAudioFileException,
            LineUnavailableException, InvalidAudioFrameException {

        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "mp3, ogg, wav", "mp3", "ogg", "wav");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            Music newMusic;
            newMusic = new Music(chooser.getSelectedFile().getPath());
            musics.add(newMusic);
            GUI.songs.add(newMusic);
        }
    }

    public void removeMusic(){

    }

    public JLabel getLabel() {
        return label;
    }

    public ArrayList<Music> getMusics() {
        return musics;
    }

    public void setMusics(ArrayList<Music> musics) {
        this.musics = musics;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
