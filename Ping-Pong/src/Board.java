import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JPanel;

public class Board extends JPanel implements Commons {

    private Timer timer;
    private String message = "Game Over";
    private Ball ball;
    private Player players[];
    private Corners corner[];
    private boolean ingame = true;

    public Board() {

        initBoard();
    }

    private void initBoard() {

        addKeyListener(new TAdapter());
        setFocusable(true);

        corner = new Corners[4];
        
        corner[0] = new Corners(Commons.CORNER_1_X, Commons.CORNER_1_Y);
        corner[1] = new Corners(Commons.CORNER_2_X, Commons.CORNER_2_Y);
        corner[2] = new Corners(Commons.CORNER_3_X, Commons.CORNER_3_Y);
        corner[3] = new Corners(Commons.CORNER_4_X, Commons.CORNER_4_Y);
        
        setDoubleBuffered(true);
        timer = new Timer();
        timer.scheduleAtFixedRate(new ScheduleTask(), DELAY, PERIOD);
    }

    @Override
    public void addNotify() {

        super.addNotify();
        gameInit();
    }

    private void gameInit() {

        ball = new Ball();
        players = new Player[4];
        for (int i=0; i<4; i++)
        	players[i] = new Player(i+1);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        
        drawCorners(g2d);

        if (ingame) {
            
            drawObjects(g2d);
        } else {

            gameFinished(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
    }
    
    private void drawObjects(Graphics2D g2d) {
        
        g2d.drawImage(ball.getImage(), ball.getX(), ball.getY(),
                ball.getWidth(), ball.getHeight(), this);
        
        for (int i=0; i<players.length; i++){
        	if (players[i].isAlive())
	            g2d.drawImage(players[i].paddle.getImage(), players[i].paddle.getX(), players[i].paddle.getY(),
	            		players[i].paddle.getWidth(), players[i].paddle.getHeight(), this);
        }
    }
    
    private void drawCorners(Graphics2D g2d) {
        
        for(int i = 0; i< 4; i++)
        {
        	g2d.setColor(Color.RED);
        	g2d.fill(corner[i].getCorner());
        }
    }
    
    private void gameFinished(Graphics2D g2d) {

        Font font = new Font("Verdana", Font.BOLD, 18);
        FontMetrics metr = this.getFontMetrics(font);

        g2d.setColor(Color.BLACK);
        g2d.setFont(font);
        g2d.drawString(message,
                (Commons.WIDTH - metr.stringWidth(message)) / 2,
                Commons.WIDTH / 2);
    }

    private class TAdapter extends KeyAdapter {

        @Override
        public void keyReleased(KeyEvent e) {
            players[0].paddle.keyReleased(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            players[0].paddle.keyPressed(e);
        }
    }

    private class ScheduleTask extends TimerTask {

        @Override
        public void run() {

            ball.move();
            players[0].paddle.move();
            checkCollision();
            repaint();
        }
    }

    private void stopGame() {

        ingame = false;
        timer.cancel();
    }
    

    private void checkCollision() {

        if (!players[0].isAlive()) {
            stopGame();
        }
        
        for (int i=0; i<players.length; i++)
        	if (players[i].isAlive())
		        if ((ball.getRect()).intersects(players[i].paddle.getRect()))
		        	Physics.reflectBallFromPaddle(ball, players[i].paddle);

        for (int i=0; i<players.length; i++)
	        if (Physics.ballHitPlayersWall(ball, players[i])){
	        	System.out.println("Hit the wall of player " + Integer.toString(i+1));
	        	if (players[i].isAlive())
	        		players[i].reduceLife();
	        	Physics.reflectBallFromWall(ball, players[i]);
	        }
        
        for (int i = 0; i<4; i++)
        	if (Physics.ballHitsCorner(i, ball))
        	{
        		System.out.println("Ball has hit the corner"+i);
        	}	       
    }
}