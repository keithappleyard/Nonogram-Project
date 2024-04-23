import javax.swing.*;

public class Nonogram{
    public Nonogram(){
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();

        frame.setContentPane(panel);
        frame.setVisible(true);
        frame.setTitle("Nonogram");
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}