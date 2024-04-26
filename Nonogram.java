import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Nonogram implements ActionListener{
    int rows;
    int columns;
    Square[][] squares;

    //Hard coded image to solve
    private int[][] puzzleImage = {
        {0, 0, 0, 0, 0},
        {0, 1, 0, 1, 0},
        {0, 0, 0, 0, 0},
        {0, 1, 0, 1, 0},
        {0, 1, 1, 1, 0},
    };

    public Nonogram(int rows, int columns){
        //initialise panels and frames
        this.rows = rows;
        this.columns = columns;
        JFrame frame = new JFrame();
        JPanel panel = new JPanel(new BorderLayout());
        JPanel gridPanel = new JPanel(new GridLayout(rows, columns));

        //Labels for displaying numbers to the side of each column and row
        JPanel rowNumbers = new JPanel(new GridLayout(rows, 1));
        JPanel columnNumbers = new JPanel(new GridLayout(1, columns));
        
        //temporary values for numbers that correspond to each row
        for(int i = 0; i < rows; i++){
            JLabel label = new JLabel("1");
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            rowNumbers.add(label);
        }
        //temporary values for numbers that correspond to each column
        for(int i = 0; i < columns; i++){
            JLabel label = new JLabel("1");
            label.setHorizontalAlignment(SwingConstants.CENTER);
            columnNumbers.add(label);
        }

        //initialise each individual square
        squares = new Square[rows][columns];
        for(int x = 0; x < rows; x++){
            for(int y = 0; y < columns; y++){
                squares[x][y] = new Square(x, y);
                squares[x][y].addActionListener(this);
                gridPanel.add(squares[x][y]);
            }
        }

        //button to check puzzle
        JButton checkButton = new JButton("Submit");
        checkButton.addActionListener(e -> checkPuzzle());

        panel.add(rowNumbers, BorderLayout.WEST);
        panel.add(columnNumbers, BorderLayout.NORTH);
        panel.add(gridPanel, BorderLayout.CENTER);
        panel.add(checkButton, BorderLayout.SOUTH);
        
        frame.add(panel);
        frame.setVisible(true);
        frame.setTitle("Nonogram");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    //check if colour of each square matches image
    public boolean checkPuzzle(){
        //compare number of rows and columns
        if(squares.length != puzzleImage.length || squares[0].length != puzzleImage[0].length)
            return false;

        //compare each square and return false if any square does not match
        for(int x = 0; x < rows; x++){
            for(int y = 0; y < columns; y++){
                if(puzzleImage[x][y] != squares[x][y].getCurrentColor())
                    return false;
            }
        }

        System.out.println("You won!");
        //return true otherwise
        return true;
    }

    //called when a button is pressed
    public void actionPerformed(ActionEvent e){
        Square square = (Square)e.getSource();
        square.cycleColor();
    }
}