import java.io.FileInputStream;
import java.io.IOException;

public class Game {
    public static void main(String[] args){
        Nonogram a = new Nonogram(5,5);
        a.loadImage("bmp-files/summer-project/elephant.bmp");
    }
}
