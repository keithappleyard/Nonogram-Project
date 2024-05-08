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
    JPanel panel;
    JPanel gridPanel;

    //Hard coded image to solve
    private int[][] puzzleImage = {
        {1, 1, 1, 1, 1},
        {1, 0, 1, 0, 1},
        {1, 1, 1, 1, 1},
        {1, 0, 1, 0, 1},
        {1, 0, 0, 0, 1},
    };

    //constructor for loading default hard-coded image
    public Nonogram(){
        //initialise panels and frames
        this.rows = puzzleImage.length;
        this.columns = puzzleImage[0].length;
        frame = new JFrame();
        panel = new JPanel(new BorderLayout());
        gridPanel = new JPanel(new GridLayout(rows, columns));

        resetGUI(rows, columns);

        frame.setVisible(true);
        frame.setTitle("Nonogram");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void resetGUI(int rows, int columns){
        this.rows = rows;
        this.columns = columns;
        //remove all current components
        frame.remove(panel);
        panel = new JPanel(new BorderLayout());
        gridPanel = new JPanel(new GridLayout(rows, columns));

        //Labels for displaying numbers to the side of each column and row
        JPanel rowNumbers = new JPanel(new GridLayout(rows, 1));
        JPanel columnNumbers = new JPanel(new BorderLayout());

        //blank label for the corner to align column labels to grid
        JLabel columnCornerLabel = new JLabel();
        columnCornerLabel.setPreferredSize(new Dimension(rows * 4, columnCornerLabel.getWidth())); //may need changing
        columnNumbers.add(columnCornerLabel, BorderLayout.WEST);

        //display values for numbers that correspond to each row
        for(int i = 0; i < rows; i++){
            JLabel label = new JLabel(getRowNumbers(i));
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            rowNumbers.add(label);
        }

        JPanel columnLabelsPanel = new JPanel(new GridLayout(1, columns));
        //display values for numbers that correspond to each column
        columnNumbers.add(new JLabel());
        for(int i = 0; i < columns; i++){
            JLabel label = new JLabel(getColumnNumbers(i));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.BOTTOM);
            columnLabelsPanel.add(label);
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
        JButton solveButton = new JButton("Solve");
        checkButton.addActionListener(e -> checkPuzzle());
        solveButton.addActionListener(e -> showPuzzle());
        buttonPanel.add(checkButton);
        buttonPanel.add(solveButton);

        //adding components to main panel
        columnNumbers.add(columnLabelsPanel, BorderLayout.CENTER);
        panel.add(rowNumbers, BorderLayout.WEST);
        panel.add(columnNumbers, BorderLayout.NORTH);
        panel.add(gridPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(panel);
        frame.revalidate();
    }

    //calculate and return string to represent numbers to the side of each row
    private String getRowNumbers(int rowIndex){
        StringBuilder sb = new StringBuilder();
        int count = 0;
        boolean needsComma = false;
        //loop through every element in the row
        for(int i = 0; i < columns; i++){
            //increase count if each pixel has consecutive colours
            if(puzzleImage[rowIndex][i] == 0){
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
            if(puzzleImage[i][columnIndex] == 0){
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

    public void showPuzzle(){
        for(int y = 0; y < columns; y++){
            for(int x = 0; x < rows; x++){
                squares[x][y].setColor(puzzleImage[x][y]);
            }
        }
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
        int paddedBytesPerRow = (int)Math.ceil((float)bytesPerRow/4) * 4; //account for extra zeros after the row has been represented
        int pixelIndex = dataLocation;
        int[][] newImage = new int[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                //find the byte and bit index corresponding to this pixel
                int byteIndex = x / 8;
                int bitIndex = 7 - (x % 8);

                //extract the pixel value (0 or 1) to represent the colour
                int pixelByte = imageData[pixelIndex + byteIndex] & 0xFF; //mask negative part
                int pixelValue = (pixelByte >> bitIndex) & 0x01; //bit shift and mask every number to either get a value of 1 or 0

                newImage[height - 1 - y][x] = pixelValue;
            }
            //move to the next row in the pixel data
            pixelIndex += paddedBytesPerRow; //change to paddedBytesPerRow once it is working
        }
        puzzleImage = newImage;
        resetGUI(width, height);
    }
}