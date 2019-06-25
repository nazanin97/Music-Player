import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Star extends JButton {

    private int mode;
    ImageIcon icon;
    ImageIcon icon2;
    private MouseHandler mouseHandler;
    private static ArrayList<Star>stars;
    public Star(){
        stars = new ArrayList<>();
        mode = 1;
        icon = new ImageIcon("star.png");
        icon2 = new ImageIcon("star-2.png");
        icon.setImage(icon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
        icon2.setImage(icon2.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
        this.setIcon(icon);
        this.addMouseListener(mouseHandler);
    }
    private class MouseHandler extends MouseInputAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (stars.contains(e.getSource())){

                if (mode == 1){
                    setIcon(icon2);
                    mode = 2;
                }
                else {
                    setIcon(icon);
                    mode = 1;
                }
                GUI.repaint();
            }
        }

    }
}
