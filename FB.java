import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FB extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 800;
    int boardHeight = 600;

    //Image
    Image BackgroundImage ;
    Image BirdImage ;
    Image TopPipeImage ;
    Image BottomPipeImage;

    //Bird Class
    int birdX = boardWidth/8;
    int birdY = boardWidth/2;
    int birdWidth = 32;
    int birdHeight = 26;

    class Bird {
        int x = birdX;
        int y = birdY;
        int height = birdHeight ;
        int width = birdWidth ;
        Image image ;

        Bird(Image image) {
            this.image = image ;
        }
    }

    //Pipe Class
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512; 

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image image;
        boolean gotThru = false;

        Pipe(Image image) {
            this.image = image;
        }
    }

    //Game Logic
    Bird bird;
    int gravity = 1;
    int velocityX = -4; // appears as though the bird is moving right by moving pipes to the left at a constant speed
    int velocityY = 0; // up/down speed of bird 

    ArrayList<Pipe> pipes;
    Random random = new Random();
    
    Timer gameLoop, placePipeTimer;
    boolean gameover = false;
    double score = 0;
    boolean gameStarted = false; // Track if the game has started

    FB() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        //Load Images
        BackgroundImage = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        BirdImage = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        TopPipeImage = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        BottomPipeImage = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        //Bird
        bird = new Bird(BirdImage);
        pipes = new ArrayList<Pipe>();

        //Place Pipe Timer
        placePipeTimer = new Timer(1350, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipeTimer.start();

        //Game Timer
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();
    }

    void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*(pipeHeight/2));
        int openingSpace = boardHeight/4;
    
        Pipe topPipe = new Pipe(TopPipeImage);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);
    
        Pipe bottomPipe = new Pipe(BottomPipeImage);
        bottomPipe.y = topPipe.y  + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }
    
    
    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Scale the graphics context to fit the current size
		double scaleX = (double) getWidth() / boardWidth;
		double scaleY = (double) getHeight() / boardHeight;
		Graphics2D g2d = (Graphics2D) g;
		g2d.scale(scaleX, scaleY);
		draw(g2d);
	}

	public void draw(Graphics g) {
        //background
        g.drawImage(BackgroundImage, 0, 0, this.boardWidth, this.boardHeight, null);

        // Display instructions if the game hasn't started
        if (!gameStarted) {
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.PLAIN, 38));
            g.drawString("Press SPACEBAR to jump", boardWidth / 4, boardHeight / 2);
        }

        //bird
        g.drawImage(BirdImage, bird.x, bird.y, bird.width, bird.height, null);

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.image, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        //score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        g.drawString("Score: " + String.valueOf((int) score), 10, 35);

        if (gameover) {
            // Center the "Game Over" text
            String gameOverText = "Game Over: " + String.valueOf((int) score);
            FontMetrics metrics = g.getFontMetrics(g.getFont());
            int x = (boardWidth - metrics.stringWidth(gameOverText)) / 2;
            int y = boardHeight / 2; // Center vertically
            g.drawString(gameOverText, x, y);
        }
	}

    public void move() {
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0); 

        //pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.gotThru && bird.x > pipe.x + pipe.width) {
                score += 0.5; 
                pipe.gotThru = true;
            }

            if (collision(bird, pipe)) {
                gameover = true;
            }
        }

        if (bird.y > boardHeight) {
            gameover = true;
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&   //a's top left corner doesn't reach b's top right corner
               a.x + a.width > b.x &&   //a's top right corner passes b's top left corner
               a.y < b.y + b.height &&  //a's top left corner doesn't reach b's bottom left corner
               a.y + a.height > b.y;    //a's bottom left corner passes b's top left corner
    }

    @Override
    public void actionPerformed(ActionEvent e) { 
        move();
        repaint();
        if (gameover) {
            placePipeTimer.stop();
            gameLoop.stop();
        }
    }  

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            gameStarted = true; // Set game as started when space is pressed

            if (gameover) {
                //restart game by resetting conditions
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                gameover = false;
                score = 0;
                gameLoop.start();
                placePipeTimer.start();
            }
        }
    }

    //not needed
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}




