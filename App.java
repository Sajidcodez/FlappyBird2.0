import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 800;
        int boardHeight = 600;

        JFrame frame = new JFrame("Flappy Bird Game");
        frame.setVisible(true);
		frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FB flappyBird = new FB();
        frame.add(flappyBird);
        frame.pack();
        flappyBird.requestFocus();
        frame.setVisible(true);
    }
}

