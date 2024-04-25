import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Nonogram implements ActionListener{
    int rows;
    int columns;
    public Square[][] squares;

    public Nonogram(int rows, int columns){
        //initialise panels and frames
        this.rows = rows;
        this.columns = columns;
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();

        GridLayout layout = new GridLayout(rows, columns);
        panel.setLayout(layout);

        //initialise each individual square
        squares = new Square[rows][columns];
        for(int x = 0; x < rows; x++){
            for(int y = 0; y < columns; y++){
                squares[x][y] = new Square(x, y);
                squares[x][y].addActionListener(this);
                panel.add(squares[x][y]);
            }
        }

        frame.add(panel);
        frame.setVisible(true);
        frame.setTitle("Nonogram");
        frame.setSize(475, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    //called when a button is pressed
    public void actionPerformed(ActionEvent e){
        Square square = (Square)e.getSource();
        square.cycleColor();
    }
}