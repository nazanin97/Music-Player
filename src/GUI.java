import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static javax.swing.SwingConstants.CENTER;

public class GUI {

    private JFrame frame;
    private JPanel mainPanel;
    private JPanel westPanel;
    private JToolBar toolBar;
    private MouseHandler mouseHandler;
    private ActHandler act;

    public GUI(){
        frame = createFrame("Music Player", 900, 700);
        mainPanel = new JPanel(new BorderLayout());
        westPanel = new JPanel();
        mouseHandler = new MouseHandler();
        act = new ActHandler();
        createTrayIcon();
        createMainPanel();
        createWestPanel();
    }
    public void show(){
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    private void createWestPanel(){
        westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.Y_AXIS));
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
        JPanel bottomPanel = new JPanel(new BorderLayout());

        createToolBar();
        topPanel.add(toolBar,  BorderLayout.WEST);
        Color color = new Color(66, 75, 244);
        topPanel.setBackground(color);
        toolBar.setBackground(color);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bottomPanel, BorderLayout.CENTER);
        frame.add(mainPanel);
    }
    private void createToolBar(){
        Color color = new Color(66, 75, 244);
        toolBar = new JToolBar();
        JLabel nowPlaying = new JLabel("Now Playing   ");
        nowPlaying.setFont(new Font("Arial", Font.PLAIN, 30));
        nowPlaying.setBackground(color);
        nowPlaying.setForeground(Color.WHITE);
        nowPlaying.addMouseListener(mouseHandler);
        JLabel library = new JLabel("   Library   ");
        library.setFont(new Font("Arial", Font.PLAIN, 30));
        library.setBackground(color);
        library.setForeground(Color.WHITE);
        library.addMouseListener(mouseHandler);
        JLabel settings = new JLabel("   Settings   ");
        settings.setFont(new Font("Arial", Font.PLAIN, 30));
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
                mainPanel.remove(westPanel);

            }
            else if (e.getSource() == toolBar.getComponent(1)){
                westPanel.setPreferredSize(new Dimension(200, mainPanel.getHeight()));
                westPanel.setBackground(Color.lightGray);
                mainPanel.add(westPanel, BorderLayout.WEST);
            }

            else if (e.getSource() == toolBar.getComponent(2)) {
                mainPanel.remove(westPanel);
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
