import javax.swing.*;
import java.awt.*;

//class to represent each square in the nonogram
public class Square extends JButton{
    private int xPos;
    private int yPos;
    private Color[] colors = {Color.WHITE, Color.BLACK};
    private int currentColor = 0;

    public Square(int xPos, int yPos){
        setBackground(colors[0]);
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public int getXPos(){
        return xPos;
    }

    public int getYPos(){
        return yPos;
    }

    //function to cycle between each color in the array of colors when a button is clicked
    public void cycleColor(){
        if(currentColor < colors.length - 1){
            currentColor++;
        }
        else{
            currentColor = 0;
        }
        setBackground(colors[currentColor]);
    }

    //function to return value corresponding to current colour of image
    public int getCurrentColor(){
        return currentColor;
    }

    //function to set colour value
    public void setColor(int col){
        currentColor = col;
        setBackground(colors[currentColor]);
    }
}
