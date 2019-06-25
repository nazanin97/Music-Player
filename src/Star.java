import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

class Star extends JButton {

    private int mode;
    private ImageIcon icon;
    private ImageIcon icon2;

    int getMode() {
        return mode;
    }

    void setMode(int mode) {
        this.mode = mode;
        if (mode == 2){
            setIcon(icon2);
        }
    }
    Star(){

        mode = 1;
        icon = new ImageIcon("star.png");
        icon2 = new ImageIcon("star-2.png");
        icon.setImage(icon.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
        icon2.setImage(icon2.getImage().getScaledInstance(15, 15, Image.SCALE_SMOOTH));
        this.setIcon(icon);
        this.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
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
        });
    }
}
