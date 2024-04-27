import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Nonogram implements ActionListener{
    int rows;
    int columns;
    Square[][] squares;
    JFrame frame;

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
        frame = new JFrame();
        JPanel panel = new JPanel(new BorderLayout());
        JPanel gridPanel = new JPanel(new GridLayout(rows, columns));

        //Labels for displaying numbers to the side of each column and row
        JPanel rowNumbers = new JPanel(new GridLayout(rows, 1));
        JPanel columnNumbers = new JPanel(new GridLayout(1, columns));
        
        //display values for numbers that correspond to each row
        for(int i = 0; i < rows; i++){
            JLabel label = new JLabel(getRowNumbers(i));
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            rowNumbers.add(label);
        }
        //display values for numbers that correspond to each column
        for(int i = 0; i < columns; i++){
            JLabel label = new JLabel(getColumnNumbers(i));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.BOTTOM);
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
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton checkButton = new JButton("Submit");
        checkButton.addActionListener(e -> checkPuzzle());
        buttonPanel.add(checkButton);

        //adding components to main panel
        panel.add(rowNumbers, BorderLayout.WEST);
        panel.add(columnNumbers, BorderLayout.NORTH);
        panel.add(gridPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        frame.add(panel);
        frame.setVisible(true);
        frame.setTitle("Nonogram");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    //calculate and return string to represent numbers to the side of each row
    private String getRowNumbers(int rowIndex){
        StringBuilder sb = new StringBuilder();
        int count = 0;
        boolean needsComma = false;
        //loop through every element in the row
        for(int i = 0; i < columns; i++){
            //increase count if each pixel has consecutive colours
            if(puzzleImage[rowIndex][i] == 1){
                count++;
            }
            //add current count to list and reset count if colour is different to previous square
            else if(count > 0){
                if(needsComma)
                    sb.append(",");
                sb.append(count);
                count = 0;
                needsComma = true;
            }
        }
        //check for count after for loop ends
        if(count > 0){
            if(needsComma)
                sb.append(",");
            sb.append(count);
        }
        return sb.toString();
    }

    //calculate and return string to represent numbers above each column
    private String getColumnNumbers(int columnIndex){
        StringBuilder sb = new StringBuilder("<html>");
        int count = 0;
        boolean needsBreak = false;
        //loop through every element in the column
        for(int i = 0; i < rows; i++){
            //increase count if each pixel has consecutive colours
            if(puzzleImage[i][columnIndex] == 1){
                count++;
            }
            //add current count to list and reset count if colour is different to previous square
            else if(count > 0){
                if(needsBreak)
                    sb.append("<br>");
                sb.append(count);
                count = 0;
                needsBreak = true;
            }
        }
        //check for count after for loop ends
        if(count > 0){
            if(needsBreak)
                sb.append("<br>");
            sb.append(count);
        }
        return sb.toString();
    }

    //check if colour of each square matches image
    private boolean checkPuzzle(){
        //compare number of rows and columns
        if(squares.length != puzzleImage.length || squares[0].length != puzzleImage[0].length)
            return false;

        //compare each square and return false if any square does not match
        for(int x = 0; x < rows; x++){
            for(int y = 0; y < columns; y++){
                if(puzzleImage[x][y] != squares[x][y].getCurrentColor()){
                    JOptionPane.showMessageDialog(frame, "Sorry, the solution is incorrect. Try again.");
                    return false;
                }
            }
        }

        //return true otherwise to indicate game is won
        JOptionPane.showMessageDialog(frame, "You won!");
        return true;
    }

    //called when a button is pressed
    public void actionPerformed(ActionEvent e){
        Square square = (Square)e.getSource();
        square.cycleColor();
    }
}