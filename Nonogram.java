import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;

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

    //function to read from windows bmp file
    private byte[] readFile(String path){
        byte[] allBytes = null;
        try{
            allBytes = Files.readAllBytes(Paths.get(path));
        } catch (IOException ex){
            ex.printStackTrace();
        }
        return allBytes;
    }

    //function to load image from file
    public void loadImage(String path){
        byte[] imageData = readFile(path);
        int width = imageData[18];  //add more bytes later to increase maximum image width
        int height = imageData[22]; //add more bytes later to increase maximum image height
        int dataLocation = imageData[10]; //add more bytes later
        int bitsPerPixel = imageData[28]; //add more bytes later and relevant later

        int bytesPerRow = (int)Math.ceil(width / 8);
        int paddedBytesPerRow = (int)Math.ceil(bytesPerRow/4) * 4; //account for extra zeros after the row has been represented
        int pixelIndex = dataLocation;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //find the byte and bit index corresponding to this pixel
                int byteIndex = x / 8;
                int bitIndex = 7 - (x % 8);

                //extract the pixel value (0 or 1) to represent the colour
                int pixelByte = imageData[pixelIndex + byteIndex] & 0xFF; //mask negative part
                int pixelValue = (pixelByte >> bitIndex) & 0x01; //bit shift and mask every number to either get a value of 1 or 0

                //print where each colour pixel is depending on its pixel value
                if (pixelValue == 0) {
                    System.out.println("Black pixel at (" + x + ", " + y + ")");
                } else {
                    System.out.println("White pixel at (" + x + ", " + y + ")");
                }
            }
            //move to the next row in the pixel data
            pixelIndex += paddedBytesPerRow;
        }
    }
}