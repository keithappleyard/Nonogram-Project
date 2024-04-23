import javax.swing.*;

//class to represent each square in the nonogram
public class Square extends JButton{
    private int xPos;
    private int yPos;

    public Square(int xPos, int yPos){
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public int getXPos(){
        return xPos;
    }

    public int getYPos(){
        return yPos;
    }
}
