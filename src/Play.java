import com.mpatric.mp3agic.Mp3File;
//import javazoom.jl.decoder.BitstreamException;
//import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;
import java.io.FileInputStream;
import java.util.Scanner;

public class Play extends Thread {


    private int offset;
    private AdvancedPlayer p;
    Music music;

    public Play(int offset, Music m){
        this.offset = offset;
        music = m;
    }

    public void run() {

        try {

            Mp3File temp1 = new Mp3File(music.getPath());
            int sampleRate = temp1.getFrameCount();
            long time = temp1.getLengthInSeconds();

            FileInputStream file = new FileInputStream(music.getPath());
            p = new AdvancedPlayer(file);
            if(offset == 0)
                p.play();
            else
                p.play((int) (offset*sampleRate/(time*1000)), 130000);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public int pause() {
        int last = p.getPosition();
        p.close();
        return last + offset;
    }
    public boolean getComplete(){
        return p.getComplete();
    }

    public int getPosition(){
        return p.getPosition();
    }
}
