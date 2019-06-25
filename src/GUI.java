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
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class GUI {

    private ArrayList<Music>songs;
    private ArrayList<Music>recentlyPlayed;
    private ArrayList<Music>mostPlayed;
    public static Music nowPlaying;
    private static JFrame frame;
    private static JPanel mainPanel;
    private static JPanel bottomPanel;
    private static JPanel westPanel;
    private static JPanel barPanel;

    private static JPanel screen;
    private static JPanel screen1;         //now playing screen
    private static JPanel screen2;         //library screen
    private static JPanel screen3;         //settings screen
    private JPanel titles;

    private JScrollPane jScrollPane;

    JButton[] controlButtons;
    JButton[] westLabels;

    private JLabel addSong;
    private JToolBar toolBar;
    private MouseHandler mouseHandler;
    private ActHandler act;
    private String mode1;       //for shuffle or repeat
    private String mode2;       //for play or pause
    private boolean mute;       //for mute or not
    Border border;
    JComboBox[] comboBoxes;
    public GUI(){
        comboBoxes = new JComboBox[4];
        for (int i = 0; i < 4; i++) {
            comboBoxes[i] = new JComboBox();
        }
        border = BorderFactory.createLineBorder(Color.GRAY, 1);
        songs = new ArrayList<>();
        recentlyPlayed = new ArrayList<>();
        mostPlayed = new ArrayList<>();
        if (new File("songs.info").exists()){
            loadFromFile();
        }

        frame = createFrame("Music Player", 900, 700);
        frame.setMinimumSize(new Dimension(900, 700));
        frame.setMaximumSize(new Dimension(900, 700));
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveToFiles();
                saveSettings();
            }
        });
        mainPanel = new JPanel(new BorderLayout());
        westPanel = new JPanel();
        bottomPanel = new JPanel(new BorderLayout());
        barPanel = new JPanel(new BorderLayout());
        screen = new JPanel();
        mouseHandler = new MouseHandler();
        act = new ActHandler();
        controlButtons = new JButton[6];
        mode2 = "pause";
        mute = false;
        createTrayIcon();
        createSettings();
        createLibraryScreen();
        createPlayScreen();
        createWestPanel();
        createBarPanel();
        createMainPanel();
        if (new File("settings.txt").exists()){
            loadSettings();
        }
    }
    private void createMainPanel(){
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(mainPanel.getWidth(), 60));
        createToolBar();
        createBottomPanel();
        topPanel.add(toolBar,  BorderLayout.WEST);
        Color color = new Color(16, 12, 137);
        topPanel.setBackground(color);
        toolBar.setBackground(color);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.CENTER);
        frame.add(mainPanel);
    }
    private void createBottomPanel(){
        bottomPanel.add(westPanel, BorderLayout.WEST);
        bottomPanel.add(barPanel, BorderLayout.SOUTH);
        bottomPanel.add(screen, BorderLayout.CENTER);
    }
    static void repaint(){
        screen2.revalidate();
        screen2.repaint();
        bottomPanel.revalidate();
        bottomPanel.revalidate();
        mainPanel.revalidate();
        mainPanel.repaint();
        frame.revalidate();
    }
    void show(){
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private void createLibraryScreen(){
        screen2 = new JPanel();
        //screen2.setBackground(Color.GREEN);
        //screen2.setLayout(new BoxLayout(screen2, BoxLayout.Y_AXIS));
        part1();
        screen2.add(titles);
        jScrollPane = new JScrollPane(screen2);
    }
    private void part1(){
        titles = new JPanel();
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
        addSong.addMouseListener(mouseHandler);

        for (JLabel topTitle:topTitles) {
            titles.add(topTitle);
        }
        titles.add(addSong);
    }

    private void saveToFiles(){
        Music.saveMusics(songs);
    }
    private void loadSettings(){
        ArrayList<String>listLines = new ArrayList<>();
        try {
            BufferedReader lineReader = new BufferedReader(new FileReader("settings.txt"));
            String lineText;
            while ((lineText = lineReader.readLine()) != null) {
                listLines.add(lineText);
            }
            lineReader.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        for (int i = 0; i < 4; i++) {
            String value = listLines.get(i).split(":")[1];
            comboBoxes[i].setSelectedItem(value);
        }
        Lyrics.setFontName( "" + comboBoxes[0].getSelectedItem());
        Lyrics.setFontSize(Integer.valueOf("" + comboBoxes[1].getSelectedItem()));
        Lyrics.setColor("" + comboBoxes[2].getSelectedItem());
    }
    private void saveSettings(){

        try {
            FileWriter fw = new FileWriter("settings.txt");
            fw.write("font:" + comboBoxes[0].getSelectedItem());
            fw.write("\nfontSize:" + comboBoxes[1].getSelectedItem());
            fw.write("\ncolor:" + comboBoxes[2].getSelectedItem());
            fw.write("\nsleep:" + comboBoxes[3].getSelectedItem());
            fw.close();
        } catch(Exception e){
            System.out.println(e);
        }
        System.out.println("Success...");

    }
    private void loadFromFile(){
        ArrayList<MusicInfo> infos;
        try {

            FileInputStream fi = new FileInputStream(new File("songs.info"));
            ObjectInputStream in = new ObjectInputStream(fi);
            infos = (ArrayList<MusicInfo>) in.readObject();

            for (MusicInfo m:infos) {
                Music music = new Music(m.path);
                music.setRating(m.rating);
                music.setStarsButtons();
                music.setRecentlyPlayed(m.recent);
                music.setNumberOfPlays(m.numPlays);
                songs.add(music);
                if (m.recent)
                    recentlyPlayed.add(music);
            }
            in.close();
            fi.close();
        }catch (IOException | ClassNotFoundException | InvalidDataException | UnsupportedTagException ex){
            ex.printStackTrace();
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        }

        //TODO fill mostPlayed arrayList with songs with maximum numberOfPlays//
    }
    private void createLibraryScreen2(){

        for (int i = 0; i < songs.size(); i++) {
            screen2.add(songs.get(i));
            screen2.revalidate();
        }
        repaint();
    }
    private void createPlayScreen(){
        screen1 = new JPanel();
        //screen1.setBackground(Color.lightGray);
        //TODO: GHAZAL: add visualization here//
    }
    private void createSettings(){
        screen3 = new JPanel();
        screen3.setBackground(new Color(139, 177, 237));
        SpringLayout sLayout = new SpringLayout();
        JPanel[] panels = new JPanel[3];

        for (int i = 0; i < 3; i++) {
            panels[i] = new JPanel();
            panels[i].setLayout(sLayout);
            panels[i].setPreferredSize(new Dimension(mainPanel.getWidth(), 200));
        }
        BoxLayout bLayout = new BoxLayout(screen3, BoxLayout.Y_AXIS);
        screen3.setLayout(bLayout);

        JLabel l = new JLabel("Lyrics:");
        l.setFont(new Font("Arial", Font.PLAIN, 40));
        JLabel font = new JLabel("Font:");
        font.setFont(new Font("Arial", Font.PLAIN, 20));
        JLabel fontSize = new JLabel("Font-Size:");
        fontSize.setFont(new Font("Arial", Font.PLAIN, 20));
        JLabel color = new JLabel("Color:");
        color.setFont(new Font("Arial", Font.PLAIN, 20));
        String[] fonts = {"Serif", "SansSerif", "Monospaced"};
        String[] sizes = {"10", "12", "14", "16", "18", "20", "22", "24"};
        String[] colors = {"red", "black", "pink", "white", "yellow", "blue", "cyan"};

        comboBoxes[0] = new JComboBox(fonts);
        comboBoxes[1] = new JComboBox(sizes);
        comboBoxes[2] = new JComboBox(colors);

        panels[0].add(l);
        panels[0].add(font);
        panels[0].add(comboBoxes[0]);
        panels[0].add(fontSize);
        panels[0].add(comboBoxes[1]);
        panels[0].add(color);
        panels[0].add(comboBoxes[2]);

        sLayout.putConstraint(SpringLayout.NORTH, l, 20, SpringLayout.NORTH, screen3);
        sLayout.putConstraint(SpringLayout.NORTH, font, 100, SpringLayout.NORTH, screen3);
        sLayout.putConstraint(SpringLayout.NORTH, fontSize, 100, SpringLayout.NORTH, screen3);
        sLayout.putConstraint(SpringLayout.NORTH, color, 100, SpringLayout.NORTH, screen3);
        sLayout.putConstraint(SpringLayout.NORTH, comboBoxes[0], 100, SpringLayout.NORTH, screen3);
        sLayout.putConstraint(SpringLayout.NORTH, comboBoxes[1], 100, SpringLayout.NORTH, screen3);
        sLayout.putConstraint(SpringLayout.NORTH, comboBoxes[2], 100, SpringLayout.NORTH, screen3);
        sLayout.putConstraint(SpringLayout.WEST, l, 50, SpringLayout.WEST, screen3);
        sLayout.putConstraint(SpringLayout.WEST, font, 50, SpringLayout.WEST, screen3);
        sLayout.putConstraint(SpringLayout.WEST, comboBoxes[0], 2, SpringLayout.EAST, font);
        sLayout.putConstraint(SpringLayout.WEST, fontSize, 50, SpringLayout.EAST, comboBoxes[0]);
        sLayout.putConstraint(SpringLayout.WEST, comboBoxes[1], 2, SpringLayout.EAST, fontSize);
        sLayout.putConstraint(SpringLayout.WEST, color, 50, SpringLayout.EAST, comboBoxes[1]);
        sLayout.putConstraint(SpringLayout.WEST, comboBoxes[2], 2, SpringLayout.EAST, color);

        JButton q = new JButton("Create a Queue");
        q.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame q = createFrame("Create your queue", 400, 300);
                JPanel main = new JPanel(new BorderLayout());
                JPanel top = new JPanel();
                top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
                top.setBackground(Color.blue);
                JPanel bottom = new JPanel(new FlowLayout());
                JButton ok = new JButton("OK");
                JButton cancel = new JButton("Cancel");
                cancel.addActionListener(e15 -> q.setVisible(false));
                bottom.add(ok);
                bottom.add(cancel);
                main.add(top, BorderLayout.CENTER);
                main.add(bottom, BorderLayout.SOUTH);
                q.add(main);
                q.setVisible(true);
            }
        });
        q.setSize(100, 50);
        panels[1].add(q);
        //panels[1].setBackground(Color.GRAY);
        sLayout.putConstraint(SpringLayout.WEST, q, 50, SpringLayout.WEST, screen3);
        sLayout.putConstraint(SpringLayout.NORTH, q, 50, SpringLayout.NORTH, screen3);

        JLabel s = new JLabel("Sleep After(min):");
        s.setFont(new Font("Arial", Font.PLAIN, 20));
        String[] times = {"5", "10", "15", "30", "45", "60"};
        comboBoxes[3] = new JComboBox(times);
        panels[2].add(s);
        panels[2].add(comboBoxes[3]);

        sLayout.putConstraint(SpringLayout.WEST, s, 50, SpringLayout.WEST, screen3);
        sLayout.putConstraint(SpringLayout.WEST, comboBoxes[3], 2, SpringLayout.EAST, s);

        for (int i = 0; i < 3; i++) {
            screen3.add(panels[i]);
        }
    }
    private void createWestPanel(){
        //BoxLayout boxLayout = new BoxLayout(westPanel, BoxLayout.Y_AXIS);
        //westPanel.setLayout(boxLayout);
        westPanel.setBorder(border);
        westPanel.setPreferredSize(new Dimension(200, bottomPanel.getHeight()));
        westPanel.setBackground(Color.lightGray);
        westLabels = new JButton[5];
        for (int i = 0; i < 5; i++) {
            westLabels[i] = new JButton();
            westLabels[i].setPreferredSize(new Dimension(200, 30));
            westLabels[i].addMouseListener(mouseHandler);
            westLabels[i].setFont(new Font("Arial", Font.PLAIN, 20));
            //westLabels[i].setBorder(border);
        }
        westLabels[0].setText("Top Charts");
        westLabels[1].setText("Recently Played");
        westLabels[2].setText("Most Played");
        westLabels[3].setText("Songs");
        westLabels[4].setText("Favorite");

        for (int i = 0; i < 5; i++) {
            //westPanel.add(Box.createVerticalStrut(15));
            westPanel.add(westLabels[i]);
            westLabels[i].setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        westPanel.add(Box.createVerticalStrut(35));

        JPanel tmp = new JPanel(new BorderLayout());
        tmp.setPreferredSize(new Dimension(180, 30));
        tmp.setBackground(Color.lightGray);
        //tmp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//        tmp.setLayout(new FlowLayout());
        JLabel p = new JLabel("Playlists");
        p.setFont(new Font("Arial", Font.PLAIN, 20));
        //p.setLayout(new BorderLayout());
        JButton pButton = createIcon("add.png");
        pButton.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JLabel label = new JLabel("untitled");
                label.setPreferredSize(new Dimension(190, 30));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.addMouseListener(new MouseInputAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON3){
                            System.out.println("rename");
                        }
                    }
                });
                westPanel.add(label);
                westPanel.repaint();
                frame.revalidate();
                westPanel.add(Box.createVerticalStrut(20));
            }
        });
        tmp.add(p, BorderLayout.WEST);
        tmp.add(pButton, BorderLayout.EAST);
        westPanel.add(tmp);

    }

    private JButton createIcon(String fileName) {
        JButton button = new JButton();
        ImageIcon icon = new ImageIcon(fileName);
        icon.setImage(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        button.setIcon(icon);
        return button;
    }
    private void createBarPanel(){
        File pics = new File("pics");
        File[] directoryListing = pics.listFiles();
        assert directoryListing != null;

        JPanel bar = new JPanel();
        bar.setPreferredSize(new Dimension(mainPanel.getWidth(), 70));
        bar.setBackground(new Color(16, 12, 137));
        SpringLayout sLayout = new SpringLayout();
        bar.setLayout(sLayout);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        int k = 0;
        for (int i = 0; i < 7; i++) {

            if (directoryListing[i].getName().endsWith(".png")){
                controlButtons[k] = createIcon("pics/" + directoryListing[i].getName());
                controlButtons[k].addMouseListener(mouseHandler);
                controlPanel.add(controlButtons[k]);
                k++;
            }
        }

        JLabel name = new JLabel("name");
        name.setForeground(Color.WHITE);
        JLabel lyrics = new JLabel("Lyrics");
        lyrics.setForeground(Color.WHITE);
        lyrics.setHorizontalAlignment(SwingConstants.CENTER);
        lyrics.setBorder(border);
        lyrics.setPreferredSize(new Dimension(50, 30));
        lyrics.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame lyrics = createFrame("Lyrics", 500, 200);
                lyrics.add(new TextField("........"));
                lyrics.setVisible(true);
            }
        });
        bar.add(name);
        bar.add(controlPanel);
        bar.add(lyrics);
        sLayout.putConstraint(SpringLayout.NORTH, name, 25, SpringLayout.NORTH, bar);
        sLayout.putConstraint(SpringLayout.WEST, name, 35, SpringLayout.WEST, bar);
        sLayout.putConstraint(SpringLayout.NORTH, lyrics, 20, SpringLayout.NORTH, bar);
        sLayout.putConstraint(SpringLayout.WEST, lyrics, 40, SpringLayout.EAST, controlPanel);
        sLayout.putConstraint(SpringLayout.NORTH, controlPanel, 15, SpringLayout.NORTH, bar);
        sLayout.putConstraint(SpringLayout.WEST, controlPanel, 250, SpringLayout.EAST, name);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(50);
        barPanel.add(progressBar, BorderLayout.NORTH);
        barPanel.add(bar, BorderLayout.CENTER);
    }
    private void createToolBar(){
        //Color color = new Color(0, 0, 0);
        toolBar = new JToolBar();
        JLabel nowPlaying = new JLabel("Now Playing   ");
        nowPlaying.setFont(new Font("Arial", Font.PLAIN, 45));
        //nowPlaying.setBackground(color);
        nowPlaying.setForeground(Color.WHITE);
        nowPlaying.addMouseListener(mouseHandler);
        JLabel library = new JLabel("   Library   ");
        library.setFont(new Font("Arial", Font.PLAIN, 45));

        //library.setBackground(color);
        library.setForeground(Color.WHITE);
        library.addMouseListener(mouseHandler);
        JLabel settings = new JLabel("   Settings   ");
        settings.setFont(new Font("Arial", Font.PLAIN, 45));
        //settings.setBackground(color);
        settings.setForeground(Color.WHITE);
        settings.addMouseListener(mouseHandler);
        toolBar.add(nowPlaying);
        toolBar.add(library);
        toolBar.add(settings);

    }
    private JFrame createFrame(String name, int width, int height) {
        JFrame newFrame = new JFrame(name);
        newFrame.setSize(width, height);
        newFrame.setLocationRelativeTo(null);
        return newFrame;
    }
    private void createTrayIcon() {
        Image image = Toolkit.getDefaultToolkit().getImage("music-player.png");
        TrayIcon trayIcon = new TrayIcon(image);
        SystemTray tray = SystemTray.getSystemTray();

        try {
            tray.add(trayIcon);
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
        trayIcon.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.setVisible(true);
            }
        });
    }
    private void showFavorites(){
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getRating() == 5){
                screen2.add(songs.get(i));
                screen2.revalidate();
            }
        }
    }
    private class MouseHandler extends MouseInputAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            //if user clicked on now playing tab
            if (e.getSource() == toolBar.getComponent(0)) {
                bottomPanel.remove(2);
                bottomPanel.add(screen1, BorderLayout.CENTER);
                westPanel.hide();
                barPanel.show();
            }
            //if user clicked on library tab
            else if (e.getSource() == toolBar.getComponent(1)){
                bottomPanel.remove(2);
                bottomPanel.add(screen2, BorderLayout.CENTER);
                barPanel.hide();
                westPanel.show();
                bottomPanel.revalidate();
            }
            //if user clicked on settings tab
            else if (e.getSource() == toolBar.getComponent(2)) {
                bottomPanel.remove(2);
                bottomPanel.add(screen3, BorderLayout.CENTER);
                westPanel.hide();
                barPanel.hide();
            }
            //barPanel buttons
            //mode = shuffle
            if(e.getSource() == controlButtons[0]){
                mode1 = "shuffle";
            }
            //mode = repeat
            else if(e.getSource() == controlButtons[1]){
                mode1 = "repeat";
            }
            //play previous song
            else if(e.getSource() == controlButtons[2]){
                // TODO //
            }
            //play or pause
            else if(e.getSource() == controlButtons[3]){
                if (mode2.equals("play")){

                    mode2 = "pause";
                    ImageIcon icon = new ImageIcon("pics/4-play.png");
                    icon.setImage(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                    controlButtons[3].setIcon(icon);
                    /*
                    TODO pause the music
                    nowPlaying.pauseMusic();
                    */
                }
                else{
                    mode2 = "play";
                    ImageIcon icon = new ImageIcon("pics/7-pause.png");
                    icon.setImage(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                    controlButtons[3].setIcon(icon);

                    nowPlaying.playMusic();

                }
            }
            //play next song
            else if(e.getSource() == controlButtons[4]){

            }
            //mute or not
            else if(e.getSource() == controlButtons[5]){

                if (mute){
                    mute = false;
                    ImageIcon icon = new ImageIcon("pics/6-speaker.png");
                    icon.setImage(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                    controlButtons[5].setIcon(icon);
                }
                else{
                    mute = true;
                    ImageIcon icon = new ImageIcon("pics/8-mute.png");
                    icon.setImage(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                    controlButtons[5].setIcon(icon);
                }
            }
            //when user clicked on add song in library
            if (e.getSource() == addSong){
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "mp3, ogg, wav", "mp3", "ogg", "wav");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    Music newMusic;
                    try {
                        newMusic = new Music(chooser.getSelectedFile().getPath());
                        songs.add(newMusic);
                    } catch (IOException | UnsupportedTagException | InvalidDataException |
                            InvalidAudioFrameException | LineUnavailableException | UnsupportedAudioFileException |
                            TagException | ReadOnlyFileException | CannotReadException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            if (e.getSource() == westLabels[0]){
                screen2.removeAll();
                //TODO: get best songs
            }
            else if (e.getSource() == westLabels[1]){
                //show recently played
                screen2.removeAll();
                screen2.add(titles);
            }
            else if (e.getSource() == westLabels[2]){
                //show mostly played
                screen2.removeAll();
                screen2.add(titles);
            }
            else if (e.getSource() == westLabels[3]){
                screen2.removeAll();
                screen2.add(titles);
                createLibraryScreen2();

            }
            else if (e.getSource() == westLabels[4]){
                screen2.removeAll();
                screen2.add(titles);
                showFavorites();
            }
            repaint();
        }
    }
    private class ActHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

        }
    }
}
