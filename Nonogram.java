import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;

public class Nonogram implements ActionListener{
    int width;
    int height;
    Square[][] squares;
    JFrame frame;
    JPanel panel;
    JPanel gridPanel;
    ArrayList<Color> colors = new ArrayList<Color>();

    //Hard coded image to solve
    private int[][] puzzleImage = {
        {1, 1, 1, 1, 1},
        {1, 0, 1, 0, 0},
        {1, 1, 1, 1, 0},
        {1, 0, 1, 0, 0},
        {1, 1, 1, 1, 1},
    };

    //constructor for loading default hard-coded image
    public Nonogram(){
        //initialise panels and frames
        this.height = puzzleImage.length;
        this.width = puzzleImage[0].length;
        frame = new JFrame();
        panel = new JPanel(new BorderLayout());
        gridPanel = new JPanel(new GridLayout(height, width));

        colors.add(Color.WHITE);
        colors.add(Color.BLACK);
        resetGUI(puzzleImage);

        frame.setVisible(true);
        frame.setTitle("Nonogram");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void resetGUI(int[][] image){
        puzzleImage = image;
        this.height = image[0].length;
        this.width = image.length;
        //remove all current components
        frame.remove(panel);
        panel = new JPanel(new BorderLayout());
        gridPanel = new JPanel(new GridLayout(height, width));

        //Labels for displaying numbers to the side of each column and row
        JPanel rowNumbers = new JPanel(new GridLayout(height, 1));
        JPanel columnNumbers = new JPanel(new BorderLayout());

        //blank label for the corner to align column labels to grid
        JLabel columnCornerLabel = new JLabel();
        columnCornerLabel.setPreferredSize(new Dimension((int)(height * 2.5), columnCornerLabel.getWidth())); //may need changing
        columnNumbers.add(columnCornerLabel, BorderLayout.WEST);

        //display values for numbers that correspond to each row
        for(int i = 0; i < height; i++){
            JLabel label = new JLabel(getRowNumbers(i));
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            rowNumbers.add(label);
        }

        JPanel columnLabelsPanel = new JPanel(new GridLayout(1, width));
        //display values for numbers that correspond to each column
        columnNumbers.add(new JLabel());
        for(int i = 0; i < width; i++){
            JLabel label = new JLabel(getColumnNumbers(i));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setVerticalAlignment(SwingConstants.BOTTOM);
            columnLabelsPanel.add(label);
        }

        //initialise each individual square
        squares = new Square[width][height];
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                squares[x][y] = new Square(colors);
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
        for(int i = 0; i < width; i++){
            //increase count if each pixel has consecutive colours
            if(puzzleImage[i][rowIndex] == Color.BLACK.getRGB()){
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
        for(int i = 0; i < height; i++){
            //increase count if each pixel has consecutive colours
            if(puzzleImage[columnIndex][i] == Color.BLACK.getRGB()){
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
        //compare number of height and width
        if(squares.length != puzzleImage.length || squares[0].length != puzzleImage[0].length)
            return false;

        //compare each square and return false if any square does not match
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                if(squares[x][y].getCurrentColor().getRGB() != puzzleImage[x][y]){
                    showIncorrectColors();
                    JOptionPane.showMessageDialog(frame, "Sorry, the solution is incorrect. Try again.");
                    return false;
                }
            }
        }

        //return true otherwise to indicate game is won
        JOptionPane.showMessageDialog(frame, "You won!");
        return true;
    }

    //function to highlight incorrect squares
    private void showIncorrectColors(){
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                if(squares[x][y].getCurrentColor().getRGB() != new Color(puzzleImage[x][y]).getRGB())
                    squares[x][y].highlight();
            }
        }
    }

    //function to reveal each square
    public void showPuzzle(){
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                Color col = new Color(puzzleImage[x][y]);
                squares[x][y].setColor(col);
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
        int newWidth = imageData[18];  //add more bytes later to increase maximum image width
        int newHeight = imageData[22]; //add more bytes later to increase maximum image height
        int dataLocation = imageData[10]; //add more bytes later
        int bitsPerPixel = imageData[28]; //add more bytes later and relevant later
;
        int bytesPerRow = (int)Math.ceil(newWidth * bitsPerPixel / 8);
        int paddedBytesPerRow = (int)Math.ceil((float)bytesPerRow/4) * 4; //account for extra zeros after the row has been represented
        int pixelIndex = dataLocation;
        int[][] newImage = new int[newWidth][newHeight];
        colors = new ArrayList<Color>();
        for (int y = newHeight - 1; y >= 0; y--) {
            for (int x = 0; x < newWidth; x++) {
                //extract rgb values for each pixel, also need to add support for different bit images
                int blue = imageData[pixelIndex + x * 3] & 0xFF;
                int green = imageData[pixelIndex + x * 3 + 1] & 0xFF;
                int red = imageData[pixelIndex + x * 3 + 2] & 0xFF;

                //add colour to colour palette if it hasn't already been added
                Color color = new Color(red, green, blue);
                if (!colors.contains(color)) {
                    colors.add(color);
                }
                newImage[x][y] = color.getRGB();
            }
            //move to the next row in the pixel data
            pixelIndex += paddedBytesPerRow;
        }
        //puzzleImage = newImage;
        resetGUI(newImage);
    }
}