import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
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
import java.util.concurrent.ThreadLocalRandom;

public class Board extends JPanel implements Commons {

    private Timer timer;
    private int timeCount;
    private int paddleTimeCount;
    private String message = "Game Over";
    private Ball ball;
    private Player players[];
    private Corners corner[];
    private boolean ingame = true;
    private PowerUp powerUps[];
    private int lastPlayerToHitTheBall = 0;
    
    public Board() {

        initBoard();
        startGame();
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
    }
    
    public void startGame(){
    	
    	// Assigning Network Player Number to all players
//		players[0].networkPlayerNumber = NetworkHandler.myPlayerNo;
//		players[1].networkPlayerNumber = (NetworkHandler.myPlayerNo + 1)%4;
//		players[2].networkPlayerNumber = (NetworkHandler.myPlayerNo + 2)%4;
//		players[3].networkPlayerNumber = (NetworkHandler.myPlayerNo + 3)%4;
    	
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
        
        powerUps = new PowerUp[Commons.NUM_POWER_UPS];
        for (int i=0; i<Commons.NUM_POWER_UPS; i++)
        	powerUps[i] = new PowerUp(ThreadLocalRandom.current().nextInt(0, 5), 
        			ThreadLocalRandom.current().nextInt(50, Commons.WIDTH - 60),
        			ThreadLocalRandom.current().nextInt(50, Commons.HEIGHT - 60),
        			ThreadLocalRandom.current().nextInt((Commons.MAX_COUNT*i)/Commons.NUM_POWER_UPS,(Commons.MAX_COUNT*(i+1))/Commons.NUM_POWER_UPS));        
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
        
        g2d.drawImage(ball.getImage(), Math.round(ball.getX()), Math.round(ball.getY()),
                ball.getWidth(), ball.getHeight(), this);
        
        for (int i=0; i<players.length; i++)
        	if (players[i].isAlive())
	            g2d.drawImage(players[i].paddle.getImage(), 
	            		Math.round(players[i].paddle.getX()), Math.round(players[i].paddle.getY()),
	            		players[i].paddle.getWidth(), players[i].paddle.getHeight(), this);
        
        for (int i=0; i<powerUps.length; i++)
        	if (powerUps[i].isActive())
	            g2d.drawImage(powerUps[i].getImage(), 
	            		(int)powerUps[i].getX(),(int)powerUps[i].getY(),
	            		powerUps[i].getWidth(), powerUps[i].getHeight(), this);
        
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
            checkPaddle();
            repaint();
            timeCount = (timeCount + 1)%Commons.MAX_COUNT;
            checkShowTimeForPowerUps();
        }
    }
    
    private void checkShowTimeForPowerUps(){
    	for (int i=0; i<powerUps.length; i++)
    		powerUps[i].checkShowTime(timeCount);
    }
    
    private void checkPaddle()
    {
    	for(int i=0; i<4; i++)
    	{
    		if(players[i].isBigPaddle)
        	{
        		paddleTimeCount += 1;
        		if(paddleTimeCount == Commons.PADDLE_COUNT)
        		{
        			players[i].isBigPaddle = false;
        			players[i].smallPaddle(i+1);
        			paddleTimeCount = 0;
        		}
        	}	
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
        
        // Collision of Ball with Paddle
        for (int i=0; i<players.length; i++)
        	if (players[i].isAlive())
		        if ((ball.getRect()).intersects(players[i].paddle.getRect())){
		        	lastPlayerToHitTheBall = i;
		        	Physics.reflectBallFromPaddle(ball, players[i].paddle);
		        }

        // Collision of Ball with a Player's Wall
        for (int i=0; i<players.length; i++)
	        if (Physics.ballHitPlayersWall(ball, players[i])){
	        	System.out.println("Hit the wall of player " + Integer.toString(i+1));
	        	if (players[i].isAlive())
	        		players[i].reduceLife();
	        	Physics.reflectBallFromWall(ball, players[i]);
	        }
        
        // Collision of Ball with a PowerUp
        for (int i=0; i<powerUps.length; i++)
        	if (powerUps[i].isActive())
        		if (powerUps[i].getRect().intersects(ball.getRect())){
        			powerUps[i].Disable();
        			ApplyPowerUpToPlayer(powerUps[i], players[lastPlayerToHitTheBall]);
        		}
  
        // Collision of Ball with a corner
        for (int i = 0; i<4; i++)
        	if (Physics.ballHitsCorner(i+1, ball))
        	{
        		System.out.println("corner "+(i+1)+" hit!");
        		Physics.reflectBallFromCorner(ball, i+1);
        	}	       
    }
    
    private void ApplyPowerUpToPlayer (PowerUp powerUp, Player player){
    	System.out.println("Power Up: "+ powerUp.description + " to Player: "+ Integer.toString(player.playerNumber));
    	if (powerUp.powerUpType==0)
    	{
    		player.bigPaddle(player.playerNumber);
    		player.isBigPaddle = true;
    		paddleTimeCount = 0;
    	}
    	if (powerUp.powerUpType==1){
    		player.extraLife();
    		return;
    	}
    	if (powerUp.powerUpType==4){
    		ball.increaseSpeed();
    		return;
    	}
    }
    
    public void updateStateFromNetwork (String inputString){

    	String data[] = inputString.split(",");
    	String opCode = data[0];
    	if (opCode.equals("a")){
    		int playerNumber = Integer.parseInt(data[1]);    		
    		for (int i=0; i<4; i++)
				if (players[i].networkPlayerNumber==playerNumber){					
					int packetNumber = Integer.parseInt(data[2]);
					if (packetNumber > players[i].networkPacketNumber){
						players[i].networkPacketNumber = packetNumber;
						float position = Float.parseFloat(data[3]);
						players[i].setPaddlePosition(position);
					}					
				}
    	}
    }
    
    public void updateStateOnNetwork (){
    	
    	String data = "";
    	
    	data.concat("a,");
    	data.concat(Integer.toString(players[0].networkPlayerNumber).concat(","));
    	data.concat(Integer.toString(players[0].networkPacketNumber).concat(","));
    	players[0].networkPacketNumber += 1;
    	data.concat(Float.toString(players[0].paddle.x).concat(","));
    	
    }
}