import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Nonogram implements ActionListener{
    int width;
    int height;
    Square[][] squares;
    JFrame frame;
    JPanel panel;
    JPanel gridPanel;
    Color[] colors = new Color[2];

    //Hard coded image to solve
    private int[][] puzzleImage = {
        {Color.WHITE.getRGB(), Color.WHITE.getRGB(), Color.WHITE.getRGB(), Color.WHITE.getRGB(), Color.WHITE.getRGB()},
        {Color.WHITE.getRGB(), Color.BLACK.getRGB(), Color.WHITE.getRGB(), Color.BLACK.getRGB(), Color.BLACK.getRGB()},
        {Color.WHITE.getRGB(), Color.WHITE.getRGB(), Color.WHITE.getRGB(), Color.WHITE.getRGB(), Color.BLACK.getRGB()},
        {Color.WHITE.getRGB(), Color.BLACK.getRGB(), Color.WHITE.getRGB(), Color.BLACK.getRGB(), Color.BLACK.getRGB()},
        {Color.WHITE.getRGB(), Color.WHITE.getRGB(), Color.WHITE.getRGB(), Color.WHITE.getRGB(), Color.WHITE.getRGB()},
    };

    //constructor for loading default hard-coded image
    public Nonogram(){
        //initialise panels and frames
        this.height = puzzleImage.length;
        this.width = puzzleImage[0].length;
        frame = new JFrame();
        panel = new JPanel(new BorderLayout());
        gridPanel = new JPanel(new GridLayout(height, width));

        colors[0] = Color.WHITE;
        colors[1] = Color.BLACK;
        resetGUI(puzzleImage);

        frame.setVisible(true);
        frame.setTitle("Nonogram");
        frame.setSize(800, 800);
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
        
        //display values for numbers that correspond to each row
        for(int i = 0; i < height; i++){
            JLabel label = new JLabel(getRowNumbers(i));
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            rowNumbers.add(label);
        }

        //blank label for the corner to align column labels to grid
        JLabel columnCornerLabel = new JLabel();
        columnCornerLabel.setPreferredSize(new Dimension(rowNumbers.getPreferredSize().width, columnCornerLabel.getHeight()));
        columnNumbers.add(columnCornerLabel, BorderLayout.WEST);

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

        //add buttons to screen
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton chooseButton = new JButton("Load");     //button to load new puzzle
        JButton checkButton = new JButton("Submit");    //button to check puzzle
        JButton solveButton = new JButton("Solve");     //button to solve the puzzle
        JButton resetButton = new JButton("Reset");      //button to reset puzzle
        chooseButton.addActionListener(e -> pickFile());
        checkButton.addActionListener(e -> checkPuzzle());
        solveButton.addActionListener(e -> showPuzzle());
        resetButton.addActionListener(e -> resetGUI(puzzleImage));
        buttonPanel.add(chooseButton);
        buttonPanel.add(checkButton);
        buttonPanel.add(solveButton);
        buttonPanel.add(resetButton);

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
        StringBuilder sb = new StringBuilder("<html>");
        int count = 1;
        boolean needsComma = false;
        Color col = new Color(puzzleImage[0][rowIndex]);

        //loop through every element in the row
        for(int i = 1; i < width; i++){
            //increase count if each pixel has consecutive colours
            if(puzzleImage[i][rowIndex] == col.getRGB()){
                count++;
            }
            //otherwise add current count to list and reset count if colour is different to previous square
            else{
                //ignore if square is white
                if(col.getRGB() != Color.WHITE.getRGB()){
                    if(needsComma)
                        sb.append(",");
                    sb.append("<span style='color: rgb(")
                    .append(col.getRed()).append(", ")
                    .append(col.getGreen()).append(", ")
                    .append(col.getBlue()).append(");'>")
                    .append(count)
                    .append("</span>");
                    needsComma = true;
                }
                count = 1;
                col = new Color(puzzleImage[i][rowIndex]);
            }
        }
        //check for count after for loop ends and ignore if squares are white
        if(count > 0 & col.getRGB() != Color.WHITE.getRGB()){
            if(needsComma)
                sb.append(",");
            sb.append("<span style='color: rgb(")
            .append(col.getRed()).append(", ")
            .append(col.getGreen()).append(", ")
            .append(col.getBlue()).append(");'>")
            .append(count)
            .append("</span>");
        }
        return sb.toString();
    }

    //calculate and return string to represent numbers above each column
    private String getColumnNumbers(int columnIndex){
        StringBuilder sb = new StringBuilder("<html>");
        int count = 1;
        boolean needsBreak = false;
        Color col = new Color(puzzleImage[columnIndex][0]);

        //loop through every element in the column
        for(int i = 1; i < height; i++){
            //increase count if each pixel has consecutive colours
            if(puzzleImage[columnIndex][i] == col.getRGB()){
                count++;
            }
            //otherwise add current count to list and reset count if colour is different to previous square
            else{
                if(col.getRGB() != Color.WHITE.getRGB()){
                    if(needsBreak)
                        sb.append("<br>");
                    sb.append("<span style='color: rgb(")
                    .append(col.getRed()).append(", ")
                    .append(col.getGreen()).append(", ")
                    .append(col.getBlue()).append(");'>")
                    .append(count)
                    .append("</span>");
                    needsBreak = true;
                }
                count = 1;
                col = new Color(puzzleImage[columnIndex][i]);
            }
        }
        //check for count after for loop ends and ignore if the squares are white
        if(count > 0 & col.getRGB() != Color.WHITE.getRGB()){
            if(needsBreak)
                sb.append("<br>");
            sb.append("<span style='color: rgb(")
            .append(col.getRed()).append(", ")
            .append(col.getGreen()).append(", ")
            .append(col.getBlue()).append(");'>")
            .append(count)
            .append("</span>");
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

    //function to pick new file
    private void pickFile(){
        JFileChooser chooser = new JFileChooser();
        File current = new File(System.getProperty("user.dir"));
        chooser.setCurrentDirectory(current);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Windows BMP File","bmp");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION)
            loadImage(chooser.getSelectedFile().getAbsolutePath());
    }

    //function to load image from file
    private void loadImage(String path){
        BMPLoader loader = new BMPLoader();
        int[][] newImage = loader.loadImage(path);
        colors = loader.getColors();
        resetGUI(newImage);
    }
}