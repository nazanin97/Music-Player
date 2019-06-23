import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.io.File;


public class GUI {

    private JFrame frame;
    private JPanel mainPanel;
    private JPanel westPanel;
    private JPanel bottomPanel;
    private JPanel barPanel;
    private JPanel screen;
    JButton controlButtons[];
    private JToolBar toolBar;
    private MouseHandler mouseHandler;
    private ActHandler act;
    private String mode1;       //for shuffle or repeat
    private String mode2;       //for play or pause
    private boolean mute;       //for mute or not

    public GUI(){
        frame = createFrame("Music Player", 900, 700);
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
        createMainPanel();
        createWestPanel();
        createBarPanel();
    }
    public void show(){
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    private void createLibraryScreen(){
        screen.removeAll();
        JScrollPane jScrollPane = new JScrollPane(screen);
    }
    private void createPlayScreen(){
        screen.removeAll();
    }
    private void createSettings(){
        screen.removeAll();

        SpringLayout sLayout = new SpringLayout();
        JPanel panels[] = new JPanel[3];

        for (int i = 0; i < 3; i++) {
            panels[i] = new JPanel();
            panels[i].setLayout(sLayout);
            panels[i].setPreferredSize(new Dimension(mainPanel.getWidth() - 10, 200));

        }
        BoxLayout bLayout = new BoxLayout(screen, BoxLayout.Y_AXIS);
        screen.setLayout(bLayout);
        //screen.setBorder(new EmptyBorder(new Insets(50, 80, 50, 80)));

        JLabel l = new JLabel("Lyrics:");
        l.setFont(new Font("Arial", Font.PLAIN, 40));
        JLabel font = new JLabel("Font:");
        font.setFont(new Font("Arial", Font.PLAIN, 20));
        JLabel fontSize = new JLabel("Font-Size:");
        fontSize.setFont(new Font("Arial", Font.PLAIN, 20));
        JLabel color = new JLabel("Color:");
        color.setFont(new Font("Arial", Font.PLAIN, 20));
        String fonts[]={"Serif","SansSerif","Monospaced"};
        String sizes[]={"10","12","14", "16", "18", "20", "22", "24"};
        String colors[]={"red","black","pink"};
        JComboBox cb = new JComboBox(fonts);
        JComboBox cb2 = new JComboBox(sizes);
        JComboBox cb3 = new JComboBox(colors);

        panels[0].add(l);
        panels[0].add(font);
        panels[0].add(cb);
        panels[0].add(fontSize);
        panels[0].add(cb2);
        panels[0].add(color);
        panels[0].add(cb3);

        sLayout.putConstraint(SpringLayout.NORTH, l, 20, SpringLayout.NORTH, screen);
        sLayout.putConstraint(SpringLayout.NORTH, font, 100, SpringLayout.NORTH, screen);
        sLayout.putConstraint(SpringLayout.NORTH, fontSize, 100, SpringLayout.NORTH, screen);
        sLayout.putConstraint(SpringLayout.NORTH, color, 100, SpringLayout.NORTH, screen);
        sLayout.putConstraint(SpringLayout.NORTH, cb, 100, SpringLayout.NORTH, screen);
        sLayout.putConstraint(SpringLayout.NORTH, cb2, 100, SpringLayout.NORTH, screen);
        sLayout.putConstraint(SpringLayout.NORTH, cb3, 100, SpringLayout.NORTH, screen);
        sLayout.putConstraint(SpringLayout.WEST, l, 50, SpringLayout.WEST, screen);
        sLayout.putConstraint(SpringLayout.WEST, font, 50, SpringLayout.WEST, screen);
        sLayout.putConstraint(SpringLayout.WEST, cb, 2, SpringLayout.EAST, font);
        sLayout.putConstraint(SpringLayout.WEST, fontSize, 50, SpringLayout.EAST, cb);
        sLayout.putConstraint(SpringLayout.WEST, cb2, 2, SpringLayout.EAST, fontSize);
        sLayout.putConstraint(SpringLayout.WEST, color, 50, SpringLayout.EAST, cb2);
        sLayout.putConstraint(SpringLayout.WEST, cb3, 2, SpringLayout.EAST, color);

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
       // Border blackLine = BorderFactory.createLineBorder(Color.black);
        //panels[1].setBorder(blackLine);
        q.setSize(100, 50);
        panels[1].add(q);
        sLayout.putConstraint(SpringLayout.WEST, q, 50, SpringLayout.WEST, screen);
        sLayout.putConstraint(SpringLayout.NORTH, q, 50, SpringLayout.NORTH, screen);

        JLabel s = new JLabel("Sleep After(min):");
        s.setFont(new Font("Arial", Font.PLAIN, 20));
        String times[]={"5","10","15", "30", "45", "60"};
        JComboBox cb4 = new JComboBox(times);
        panels[2].add(s);
        panels[2].add(cb4);

        sLayout.putConstraint(SpringLayout.WEST, s, 50, SpringLayout.WEST, screen);
        sLayout.putConstraint(SpringLayout.WEST, cb4, 2, SpringLayout.EAST, s);

        for (int i = 0; i < 3; i++) {
            screen.add(panels[i]);
        }

    }
    private void createWestPanel(){
        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
        westPanel.setPreferredSize(new Dimension(200, mainPanel.getHeight()));
        westPanel.setBackground(Color.lightGray);
        JLabel labels[] = new JLabel[5];
        for (int i = 0; i < 5; i++) {
            labels[i] = new JLabel();
            labels[i].addMouseListener(mouseHandler);
            labels[i].setFont(new Font("Arial", Font.PLAIN, 20));

        }
        labels[0].setText("  Top Charts");
        labels[1].setText("  Recently Played");
        labels[2].setText("  Most Played");
        labels[3].setText("  Songs");
        labels[4].setText("  Favorite");

        for (int i = 0; i < 5; i++) {
            westPanel.add(Box.createVerticalStrut(15));
            westPanel.add(labels[i]);
        }
        westPanel.add(Box.createVerticalStrut(35));

        JLabel tmp = new JLabel();
        tmp.setLayout(new BorderLayout());
        JLabel p = new JLabel("Playlists");
        p.setLayout(new BorderLayout());
        JButton pButton = new JButton();
        ImageIcon icon = new ImageIcon("add.png");
        icon.setImage(icon.getImage().getScaledInstance(10, 10, Image.SCALE_SMOOTH));
        pButton.setIcon(icon);
        pButton.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTextField label = new JTextField("untitled");
                label.addMouseListener(new MouseInputAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON3){

                        }
                    }
                });
                westPanel.add(label);
                westPanel.add(Box.createVerticalStrut(10));
            }
        });
        tmp.add(p, BorderLayout.WEST);
        tmp.add(pButton, BorderLayout.EAST);
        //westPanel.add(p);
        westPanel.add(tmp);

    }
    private void createMainPanel(){
        JPanel topPanel = new JPanel(new BorderLayout());
        createToolBar();
        createBottomPanel();
        //bottomPanel.add(westPanel, BorderLayout.WEST);
        topPanel.add(toolBar,  BorderLayout.WEST);
        Color color = new Color(66, 75, 244);
        topPanel.setBackground(color);
        toolBar.setBackground(color);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.CENTER);
        frame.add(mainPanel);
    }
    private void createBottomPanel(){
        bottomPanel.add(westPanel, BorderLayout.WEST);
        bottomPanel.add(screen, BorderLayout.CENTER);
        bottomPanel.add(barPanel, BorderLayout.SOUTH);
    }
    private JButton createIcon(String fileName) {
        JButton button = new JButton();
        ImageIcon icon = new ImageIcon(fileName);
        icon.setImage(icon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
        button.setIcon(icon);
        return button;
    }
    private void createBarPanel(){
        File pics = new File("pics");
        File[] directoryListing = pics.listFiles();
        assert directoryListing != null;

        JPanel bar = new JPanel();
        bar.setPreferredSize(new Dimension(mainPanel.getWidth(), 50));
        bar.setBackground(Color.YELLOW);
        SpringLayout sLayout = new SpringLayout();
        bar.setLayout(sLayout);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        for (int i = 0; i < 7; i++) {

            if (directoryListing[i].getName().equals(".DS_Store"))
                continue;

            controlButtons[i-1] = createIcon("pics/" + directoryListing[i].getName());
            controlButtons[i-1].addMouseListener(mouseHandler);
            controlPanel.add(controlButtons[i-1]);

        }

        JLabel name = new JLabel("name");
        JLabel lyrics = new JLabel("Lyrics");
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
        sLayout.putConstraint(SpringLayout.NORTH, name, 15, SpringLayout.NORTH, bar);
        sLayout.putConstraint(SpringLayout.WEST, name, 35, SpringLayout.WEST, bar);
        sLayout.putConstraint(SpringLayout.NORTH, lyrics, 15, SpringLayout.NORTH, bar);
        sLayout.putConstraint(SpringLayout.WEST, lyrics, 50, SpringLayout.EAST, controlPanel);
        sLayout.putConstraint(SpringLayout.NORTH, controlPanel, 5, SpringLayout.NORTH, bar);
        sLayout.putConstraint(SpringLayout.WEST, controlPanel, 250, SpringLayout.EAST, name);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(50);
        barPanel.add(progressBar, BorderLayout.NORTH);
        barPanel.add(bar, BorderLayout.CENTER);
    }
    private void createToolBar(){
        Color color = new Color(66, 75, 244);
        toolBar = new JToolBar();
        JLabel nowPlaying = new JLabel("Now Playing   ");
        nowPlaying.setFont(new Font("Arial", Font.PLAIN, 50));
        nowPlaying.setBackground(color);
        nowPlaying.setForeground(Color.WHITE);
        nowPlaying.addMouseListener(mouseHandler);
        JLabel library = new JLabel("   Library   ");
        library.setFont(new Font("Arial", Font.PLAIN, 50));
        library.setBackground(color);
        library.setForeground(Color.WHITE);
        library.addMouseListener(mouseHandler);
        JLabel settings = new JLabel("   Settings   ");
        settings.setFont(new Font("Arial", Font.PLAIN, 50));
        settings.setBackground(color);
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
    private class MouseHandler extends MouseInputAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() == toolBar.getComponent(0)){
                westPanel.hide();
                barPanel.show();
                createPlayScreen();
            }
            else if (e.getSource() == toolBar.getComponent(1)){
                barPanel.hide();
                westPanel.show();
                createLibraryScreen();
            }
            else if (e.getSource() == toolBar.getComponent(2)) {
                westPanel.hide();
                barPanel.hide();
                createSettings();
            }
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

            }
            //play or pause
            else if(e.getSource() == controlButtons[3]){
                if (mode2.equals("play")){
                    mode2 = "pause";
                    ImageIcon icon = new ImageIcon("pics/4-play.png");
                    icon.setImage(icon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
                    controlButtons[3].setIcon(icon);
                }
                else{
                    mode2 = "play";
                    ImageIcon icon = new ImageIcon("pics/7-pause.png");
                    icon.setImage(icon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
                    controlButtons[3].setIcon(icon);

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
                    icon.setImage(icon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
                    controlButtons[5].setIcon(icon);
                }
                else{
                    mute = true;
                    ImageIcon icon = new ImageIcon("pics/8-mute.png");
                    icon.setImage(icon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
                    controlButtons[5].setIcon(icon);
                }
            }

            frame.revalidate();
        }
    }
    private class ActHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            frame.revalidate();
        }
    }
}
