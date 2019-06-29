import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class GUI {

    private GetLyric thread2 = null;

    static ArrayList<Music>songs;
    static ArrayList<Music>favorites;
    private ArrayList<Music>recentlyPlayed;
    private ArrayList<Music>mostPlayed;
    public static ArrayList<Playlist>playlists;

    public static Music nowPlaying;
    public static Play p;

    private static JFrame frame;
    private static JPanel mainPanel;
    private static JPanel bottomPanel;
    private static JPanel westPanel;
    private static JPanel barPanel;

    private static JPanel screen;
    private static JPanel screen1;         //now playing screen
    public static JPanel screen2;         //library screen
    private static JPanel screen3;         //settings screen
    private JPanel titles;

    private JScrollPane jScrollPane;

    static JButton[] controlButtons;
    JButton[] westLabels;

    private JLabel addSong;
    private JToolBar toolBar;
    private MouseHandler mouseHandler;
    private String mode1;       //for shuffle or repeat
    private static String mode2;       //for play or pause
    private boolean mute;       //for mute or not
    Border border;
    JComboBox[] comboBoxes;

    public static String getMode2(){
        return mode2;
    }

    public static void makePlay(){
        mode2 = "play";
        ImageIcon icon = new ImageIcon("pics/7-pause.png");
        icon.setImage(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        controlButtons[3].setIcon(icon);
    }

    public GUI() throws InvalidDataException, IOException, UnsupportedTagException {

        PlayButtonUpdator thread = new PlayButtonUpdator();
        thread.start();

        comboBoxes = new JComboBox[4];
        for (int i = 0; i < 4; i++) {
            comboBoxes[i] = new JComboBox();
        }
        border = BorderFactory.createLineBorder(Color.GRAY, 1);
        songs = new ArrayList<>();
        favorites = new ArrayList<>();
        recentlyPlayed = new ArrayList<>();
        mostPlayed = new ArrayList<>();
        playlists = new ArrayList<>();

        //load last playLists and songs
        if (new File("songs.info").exists()){
            loadFromFile();
        }

        frame = createFrame("Music Player", 950, 700);
        frame.setMinimumSize(new Dimension(950, 700));
        frame.setMaximumSize(new Dimension(950, 700));
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
        screen.setBackground(Color.BLACK);

        mouseHandler = new MouseHandler();
        controlButtons = new JButton[5];

        mode2 = "pause";
        mute = false;

        //createTrayIcon();
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

        Color color = new Color(16, 12, 137);
        topPanel.setBackground(color);
        toolBar.setBackground(color);
        topPanel.add(toolBar,  BorderLayout.WEST);

        createBottomPanel();
        for (int i = 0; i < playlists.size(); i++) {
            playlists.get(i).addMouseHandler(screen2);
        }
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
//        nowPlaying.revalidate();
//        nowPlaying.repaint();
        barPanel.revalidate();
        barPanel.repaint();
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
        part1();
        screen2.add(titles);
        jScrollPane = new JScrollPane(screen2);
    }
    private void part1(){
        titles = new JPanel();
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

    private void createSongsLibrary(){

        for (int i = 0; i < songs.size(); i++) {
            screen2.add(songs.get(i));
            screen2.revalidate();
        }
        repaint();
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
        Lyrics.setColor((Color)comboBoxes[2].getSelectedItem());

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
    private void saveToFiles(){
        Music.saveMusics(songs, playlists);
    }
    private void loadFromFile(){
        ArrayList<MusicInfo> infos;
        try {
            File file = new File("./playLists");
            int q = 1;

            for (File f:file.listFiles()) {
                if (f.getName().endsWith(".info")){
                    FileInputStream fi1 = new FileInputStream(f);
                    ObjectInputStream in1 = new ObjectInputStream(fi1);
                    infos = (ArrayList<MusicInfo>) in1.readObject();
                    Playlist tmp = new Playlist("playlist"+q);
                    for (MusicInfo m:infos) {
                        Music music = new Music(m.path);
                        music.setRating(m.rating);
                        music.setStarsButtons();
                        music.setRecentlyPlayed(m.recent);
                        music.setNumberOfPlays(m.numPlays);
                        tmp.musics.add(music);

                        if (m.recent)
                            recentlyPlayed.add(music);
                    }
                    playlists.add(tmp);
                    in1.close();
                    fi1.close();
                    q++;
                }

            }
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
        }

        //TODO fill mostPlayed arrayList with songs with maximum numberOfPlays//
    }

    private void createPlayScreen(){
        screen1 = new JPanel();

        screen1.setBackground(Color.lightGray);
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
        Integer[] sizes = {16, 18, 20, 22, 24, 26, 28, 30, 32, 34};
        Color[] colors = {Color.red, Color.black, Color.pink, Color.gray, Color.yellow, Color.blue, Color.cyan};

        comboBoxes[0] = new JComboBox(fonts);
        comboBoxes[1] = new JComboBox(sizes);
        comboBoxes[2] = new JComboBox(colors);

        comboBoxes[0].addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Lyrics.setFontName("" + comboBoxes[0].getSelectedItem());
                System.out.println("" + comboBoxes[0].getSelectedItem());
            }
        });
        comboBoxes[1].addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Lyrics.setFontSize((Integer) comboBoxes[1].getSelectedItem());
            }
        });
        comboBoxes[2].addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Lyrics.setColor((Color) comboBoxes[2].getSelectedItem());
            }
        });

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
        westPanel.setBorder(border);
        westPanel.setPreferredSize(new Dimension(200, bottomPanel.getHeight()));
        westPanel.setBackground(Color.lightGray);
        westLabels = new JButton[5];
        for (int i = 0; i < 5; i++) {
            westLabels[i] = new JButton();
            westLabels[i].setPreferredSize(new Dimension(200, 30));
            westLabels[i].addMouseListener(mouseHandler);
            westLabels[i].setFont(new Font("Arial", Font.PLAIN, 20));
        }
        westLabels[0].setText("Top Charts");
        westLabels[1].setText("Recently Played");
        westLabels[2].setText("Most Played");
        westLabels[3].setText("Songs");
        westLabels[4].setText("Favorite");

        for (int i = 0; i < 5; i++) {
            westPanel.add(westLabels[i]);
            westLabels[i].setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        westPanel.add(Box.createVerticalStrut(35));

        JPanel tmp = new JPanel(new BorderLayout());
        tmp.setPreferredSize(new Dimension(180, 30));
        tmp.setBackground(Color.lightGray);

        JLabel p = new JLabel("Playlists");
        p.setFont(new Font("Arial", Font.PLAIN, 20));

        JButton pButton = createIcon("add.png");
        pButton.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JLabel label = new JLabel("playlist" + (playlists.size()+1));
                label.setPreferredSize(new Dimension(200, 30));
                label.setHorizontalAlignment(SwingConstants.CENTER);

                Playlist newPlaylist = new Playlist(label.getText(), label, screen2);
                playlists.add(newPlaylist);

                westPanel.add(newPlaylist.getLabel());
                repaint();
                westPanel.add(Box.createVerticalStrut(20));
            }
        });
        tmp.add(p, BorderLayout.WEST);
        tmp.add(pButton, BorderLayout.EAST);
        westPanel.add(tmp);

        for (int i = 0; i < playlists.size(); i++) {
            westPanel.add(playlists.get(i).getLabel());
            westPanel.add(Box.createVerticalStrut(20));
        }
    }

    private JButton createIcon(String fileName) {
        JButton button = new JButton();
        ImageIcon icon = new ImageIcon(fileName);
        icon.setImage(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
        button.setIcon(icon);
        return button;
    }
    private JPanel createVolumeBar(){
        JPanel volumeHolder = new JPanel();
        volumeHolder.setPreferredSize(new Dimension(100, 40));
        JSlider temp = new JSlider(0, 100);
        temp.setPreferredSize(new Dimension(100, 40));
        String[] getVolume = {"osascript", "-e","output volume of (get volume settings)"};

        try {
            ProcessBuilder me = new ProcessBuilder(getVolume);
            Process process =  me.start();
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));
            String s;
            while ((s = stdInput.readLine()) != null)
            {
                temp.setValue(Integer.decode(s));
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        temp.addChangeListener(e -> {

            String[] commands = {"osascript", "-e","set volume " + (temp.getValue()/14)};
            try {
                new ProcessBuilder(commands).start();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        volumeHolder.add(temp);
        return volumeHolder;
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

        controlButtons[0] = createIcon("pics/1-shuffle.png");
        controlButtons[1] = createIcon("pics/2-repeat.png");
        controlButtons[2] = createIcon("pics/3-backward.png");
        controlButtons[3] = createIcon("pics/4-play.png");
        controlButtons[4] = createIcon("pics/5-forward.png");
        //controlButtons[5] = createIcon("pics/6-speaker.png");

        for (int i = 0; i < controlButtons.length; i++) {
            controlButtons[i].addMouseListener(mouseHandler);
            controlPanel.add(controlButtons[i]);
        }

        //creating volume
        JPanel volumeHolder = createVolumeBar();

        JLabel name = new JLabel("song's name");
        if (nowPlaying != null){
            name.setText(nowPlaying.getTitle());
        }
        name.setForeground(Color.WHITE);
        JLabel lyrics = new JLabel("Lyrics");
        lyrics.setForeground(Color.WHITE);
        lyrics.setHorizontalAlignment(SwingConstants.CENTER);
        lyrics.setBorder(border);
        lyrics.setPreferredSize(new Dimension(50, 30));
        lyrics.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame jFrame = createFrame("Lyrics", 600, 400);
                jFrame.setLayout(new BorderLayout());

                JPanel holding = new JPanel(new FlowLayout(FlowLayout.LEFT));
                holding.setPreferredSize(new Dimension(100, 30));

                JButton findLyrics = new JButton();
                findLyrics.setText("Find Lyrics?");
                findLyrics.setFont(new Font("Arial", Font.PLAIN, 15));

                JButton openLyrics = new JButton();
                openLyrics.setText("Open Lyrics");
                openLyrics.setFont(new Font("Arial", Font.PLAIN, 15));


                JPanel lyricsHolder = new JPanel();
                JScrollPane jScrollPane1 = new JScrollPane();
                jScrollPane1.add(lyricsHolder);
                lyricsHolder.setPreferredSize(new Dimension(550, 300));

                JTextArea content = new JTextArea();
                content.setEditable(false);
                content.setPreferredSize(new Dimension(550, 300));
                JScrollPane scrollableTextArea = new JScrollPane(content);
                scrollableTextArea.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
                scrollableTextArea.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


                findLyrics.addMouseListener(new MouseInputAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (thread2 != null)
                            thread2.terminate();

                        LyricDownloader l;
                        l = new LyricDownloader(nowPlaying.getArtist(), nowPlaying.getTitle());
                        l.start();
                        while (l.getLyrics().equals("null")){
                            try {
                                Thread.currentThread().sleep(100);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                        }
                        String lrc = l.getLyrics();
                        content.setText(lrc);
                    }
                });

                openLyrics.addMouseListener(new MouseInputAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JFileChooser chooser = new JFileChooser();
                        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                                "txt, lrc", "txt", "lrc");
                        chooser.setFileFilter(filter);
                        int returnVal = chooser.showOpenDialog(null);
                        if(returnVal == JFileChooser.APPROVE_OPTION) {

                            String lyricPath = chooser.getSelectedFile().getPath();

                            if (lyricPath.endsWith("txt")){
                                if (thread2 != null)
                                    thread2.terminate();
                                Path pathP = Paths.get(lyricPath);

//                                try {
//
//                                    //String text = Files.readString(pathP, StandardCharsets.US_ASCII);
//                                    //content.setText(text);
//                                } catch (IOException ex) {
//                                    ex.printStackTrace();
//                                }
                            }
                            else if(lyricPath.endsWith("lrc")){
                                if (thread2 != null)
                                    thread2.terminate();
                                thread2 = new GetLyric(lyricPath, content);
                                thread2.start();

                            } else {
                                if (thread2 != null)
                                    thread2.terminate();
                                content.setText("Please Choose a txt/lrc file.");



                            }
                        }

                    }

                });
                holding.add(findLyrics);
                holding.add(openLyrics);
                lyricsHolder.add(scrollableTextArea);
                jFrame.add(holding, BorderLayout.NORTH);
                jFrame.add(lyricsHolder, BorderLayout.CENTER);
                jFrame.setVisible(true);
            }
        });
        bar.add(name);
        bar.add(controlPanel);
        bar.add(volumeHolder);
        bar.add(lyrics);
        sLayout.putConstraint(SpringLayout.NORTH, name, 25, SpringLayout.NORTH, bar);
        sLayout.putConstraint(SpringLayout.WEST, name, 35, SpringLayout.WEST, bar);
        sLayout.putConstraint(SpringLayout.NORTH, controlPanel, 15, SpringLayout.NORTH, bar);
        sLayout.putConstraint(SpringLayout.WEST, controlPanel, 150, SpringLayout.EAST, name);
        sLayout.putConstraint(SpringLayout.NORTH, volumeHolder, 15, SpringLayout.NORTH, bar);
        sLayout.putConstraint(SpringLayout.WEST, volumeHolder, 15, SpringLayout.EAST, controlPanel);
        sLayout.putConstraint(SpringLayout.NORTH, lyrics, 20, SpringLayout.NORTH, bar);
        sLayout.putConstraint(SpringLayout.WEST, lyrics, 220, SpringLayout.EAST, volumeHolder);


        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(50);
        barPanel.add(progressBar, BorderLayout.NORTH);
        barPanel.add(bar, BorderLayout.CENTER);
    }
    private void createToolBar(){
        toolBar = new JToolBar();

        JLabel nowPlaying = new JLabel("Now Playing   ");
        nowPlaying.setFont(new Font("Arial", Font.PLAIN, 35));
        nowPlaying.setForeground(Color.WHITE);
        nowPlaying.addMouseListener(mouseHandler);

        JLabel library = new JLabel("   Library   ");
        library.setFont(new Font("Arial", Font.PLAIN, 35));
        library.setForeground(Color.WHITE);
        library.addMouseListener(mouseHandler);

        JLabel settings = new JLabel("   Settings   ");
        settings.setFont(new Font("Arial", Font.PLAIN, 35));
        settings.setForeground(Color.WHITE);
        settings.addMouseListener(mouseHandler);


        //todo add search field

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
            songs.get(i).setRating();
            if (songs.get(i).getRating() == 5){
                favorites.add(songs.get(i));
                screen2.add(songs.get(i));
            }
        }
        repaint();
    }
    private class MouseHandler extends MouseInputAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            //if user clicked on now playing tab
            if (e.getSource() == toolBar.getComponent(0)) {

                if (nowPlaying != null){
                    JPanel musicArt = new JPanel();
//                    musicArt.setSize(screen1.getWidth(), screen1.getHeight());

                    JLabel label = null;
                    try {
                        label = nowPlaying.getImage();
                    } catch (InvalidDataException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (UnsupportedTagException ex) {
                        ex.printStackTrace();
                    }
                    if (label != null) {
                        musicArt.add(label);
                        screen1 = musicArt;
                        screen1.revalidate();
                    }
                }
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
            //todo mode = shuffle
            if(e.getSource() == controlButtons[0]){
                mode1 = "shuffle";
            }
            //todo mode = repeat
            else if(e.getSource() == controlButtons[1]){
                mode1 = "repeat";
            }
            //play previous song
            else if(e.getSource() == controlButtons[2]){
                // TODO
            }
            //play or pause
            else if(e.getSource() == controlButtons[3]){
                if (mode2.equals("play")){

                    mode2 = "pause";
                    ImageIcon icon = new ImageIcon("pics/4-play.png");
                    icon.setImage(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                    controlButtons[3].setIcon(icon);
                    nowPlaying.offset = p.pause();
                }
                else{
                    if (nowPlaying == null)
                        return;
                    mode2 = "play";
                    ImageIcon icon = new ImageIcon("pics/7-pause.png");
                    icon.setImage(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                    controlButtons[3].setIcon(icon);
                    nowPlaying.setNumberOfPlays(nowPlaying.getNumberOfPlays() + 1);
                    p = new Play(nowPlaying.offset, nowPlaying);
                    p.start();
                }
            }
            //play next song
            else if(e.getSource() == controlButtons[4]){

            }
            //mute or not
//            else if(e.getSource() == controlButtons[5]){
//
//                if (mute){
//                    mute = false;
//                    ImageIcon icon = new ImageIcon("pics/6-speaker.png");
//                    icon.setImage(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
//                    controlButtons[5].setIcon(icon);
//                }
//                else{
//                    mute = true;
//                    ImageIcon icon = new ImageIcon("pics/8-mute.png");
//                    icon.setImage(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
//                    controlButtons[5].setIcon(icon);
//                }
//            }
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
                    } catch (IOException | UnsupportedTagException | InvalidDataException e1) {
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
                for (Music music:songs) {
                    if (music.isRecentlyPlayed())
                        recentlyPlayed.add(music);
                }
                for (Music music:recentlyPlayed) {
                    screen2.add(music);
                }
                repaint();
            }
            else if (e.getSource() == westLabels[2]){
                //todo show mostly played
                screen2.removeAll();
                screen2.add(titles);

            }
            else if (e.getSource() == westLabels[3]){
                screen2.removeAll();
                screen2.add(titles);
                createSongsLibrary();

            }
            else if (e.getSource() == westLabels[4]){
                screen2.removeAll();
                screen2.add(titles);
                showFavorites();
            }
            repaint();
        }
    }
    private class PlayButtonUpdator extends Thread
    {
        @Override
        public void run()
        {
            while(true) {
                if (p != null) {
                    try {
                        if (p.getComplete() && mode2.equals("play")) {
                            mode2 = "pause";
                            ImageIcon icon = new ImageIcon("pics/4-play.png");
                            icon.setImage(icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));
                            controlButtons[3].setIcon(icon);
                        }
                    }catch (NullPointerException e){
                        //  System.out.println("Null pointer");
                    }

                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class GetLyric extends Thread
    {
        private String lrcPath;
        private Lyrics lrc = new Lyrics();
        private String lyric = "";
        JTextArea content;
        private boolean flag = true;

        public void terminate(){
            System.out.println("terminating ...");
            this.flag = false;
        }

        public GetLyric(String lrcPath, JTextArea content){
            this.lrcPath = lrcPath;
            this.content = content;
            Font font = new Font(Lyrics.getFontName(), Font.PLAIN, Lyrics.getFontSize());
            this.content.setFont(font);
            this.content.setForeground(Lyrics.getColor());
        }

        @Override
        public void run() {
            try {
                while (!p.getComplete() && this.flag) {

                    if (p != null) {
                        long currentTime = p.getPosition() + nowPlaying.offset;
                        this.lyric = lrc.lyricAdjuster(lrcPath, currentTime);
                        //this.content.setFont(new Font(Lyrics.getFontName(), Font.PLAIN, Lyrics.getFontSize()));
                        this.content.setText(this.lyric);
                    }

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                System.out.println("TERMINATED");
            } catch (NullPointerException e) {
            }
        }
    }
}