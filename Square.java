import javax.swing.*;
import java.awt.*;

//class to represent each square in the nonogram
public class Square extends JButton{
    private Color incorrect = Color.YELLOW;
    private int currentColor = 0;

    public Square(){
        setBackground(Color.WHITE);
    }

    public void highlight(){
        setBackground(incorrect);
    }

    //function to cycle between each color in the array of colors when a button is clicked
    public void cycleColor(Color[] colors){
        if(currentColor < colors.length - 1){
            currentColor++;
        }
        else{
            currentColor = 0;
        }
        setBackground(colors[currentColor]);
    }

    //function to return value corresponding to current colour of image
    public Color getCurrentColor(){
        return this.getBackground();
    }

    //function to set colour value
    public void setColor(Color col){
        setBackground(col);
    }
}
