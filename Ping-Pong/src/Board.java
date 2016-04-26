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
    private String message = "Game Over";
    private Ball ball;
    public Player players[];
    private int numPlayers = 1;
    private Corners corner[];
    public boolean ingame = true;
    private PowerUp powerUps[];
    private int lastPlayerToHitTheBall;
    private int hostPlayer;
	
	private NetworkHandler nwh;
    
    public Board (int players) {

        initBoard();
        this.numPlayers = players;
    }
	
	public void setNWH(NetworkHandler nwh){
		this.nwh = nwh;
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
    	if (numPlayers == 1){
			players[0].networkPlayerNumber = nwh.myPlayerNo;    		
        	for (int i=1; i<4; i++)
    			players[i].kill();    		
    	}
    	if (numPlayers == 2){
    		players[0].networkPlayerNumber = nwh.myPlayerNo;
    		players[2].networkPlayerNumber = (nwh.myPlayerNo + 1)%2;
    		players[1].kill();
    		players[3].kill();
    	}
    	if (numPlayers == 3){
    		if (nwh.myPlayerNo==0){
	    		players[0].networkPlayerNumber = 0;
	    		players[1].networkPlayerNumber = 1;
	    		players[2].networkPlayerNumber = 2;
	    		players[3].kill();
    		} else if (nwh.myPlayerNo==1){
	    		players[3].networkPlayerNumber = 0;
	    		players[0].networkPlayerNumber = 1;
	    		players[1].networkPlayerNumber = 2;
	    		players[2].kill();
    		} else if (nwh.myPlayerNo==2){
	    		players[2].networkPlayerNumber = 0;
	    		players[3].networkPlayerNumber = 1;
	    		players[0].networkPlayerNumber = 2;
	    		players[1].kill();
    		}
    	}
       	if (numPlayers == 4){
        	for (int i=0; i<4; i++)
    			players[i].networkPlayerNumber = (nwh.myPlayerNo + i)%4;
    	}
    	
		for (int i=0; i<4; i++){
			if (players[i].networkPlayerNumber==0)
				hostPlayer = i;
			
		}

		// Sending Initial Ball Position
		if (hostPlayer==0){
			nwh.sendStateInfo(updateStateOnNetwork(2));
			nwh.sendStateInfo(updateStateOnNetwork(2));
			nwh.sendStateInfo(updateStateOnNetwork(2));
			nwh.sendStateInfo(updateStateOnNetwork(2));
			nwh.sendStateInfo(updateStateOnNetwork(2));

		// Send Power Up Info
	        for (int i=0; i<Commons.NUM_POWER_UPS; i++){
				nwh.sendStateInfo(updateStateOnNetwork(4,i));	        	
				nwh.sendStateInfo(updateStateOnNetwork(4,i));	        	
				nwh.sendStateInfo(updateStateOnNetwork(4,i));	        	
	        }
		}		
		
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
            showLives(g2d);
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
    
    private void showLives(Graphics2D g2d){
    	
        Font font = new Font("Verdana", Font.BOLD, 18);
        FontMetrics metr = this.getFontMetrics(font);

        g2d.setColor(Color.BLACK);
        g2d.setFont(font);
        g2d.drawString("Lives: " + Integer.toString(players[0].lives()),
                Commons.WIDTH - metr.stringWidth(message),
                30);    	
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
            timeCount = (timeCount + 1)%Commons.MAX_COUNT;
            checkShowTimeForPowerUps();
			
			nwh.sendStateInfo(updateStateOnNetwork(1));
			if (ballInMyArea(ball)){
	        		nwh.sendStateInfo(updateStateOnNetwork(2));
			}
			repaint();
        }
    }
    
    private boolean ballInMyArea(Ball ball){
    	
    	float x = ball.getX();
    	float y = ball.getY();
    	
    	if (y < Commons.HEIGHT/2)
    		return false;
    	
    	if (x < Commons.WIDTH/2){
    		if (players[1].isAlive()){
    			if (y < Commons.HEIGHT - x)
    				return false;
    		}    		
    	} else {
    		if (players[3].isAlive()){
    			if (y < x)
    				return false;    			
    		}
    	}
    	
    	return true;
    }
    
    private void checkShowTimeForPowerUps(){
    	for (int i=0; i<powerUps.length; i++)
    		powerUps[i].checkShowTime(timeCount);
    }
    
    private void checkPaddle(){

    	for(int i=0; i<4; i++){
    		if(players[i].hasBigPaddle){
        		players[i].bigPaddleTimeCounter += 1;
        		if(players[i].bigPaddleTimeCounter == Commons.POWER_UP_TIME)
        			players[i].setSmallPaddle();
        	}	
    		if(players[i].hasShield){
        		players[i].shieldTimeCounter += 1;
        		if(players[i].shieldTimeCounter == Commons.POWER_UP_TIME)
        			players[i].hasShield = false;
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
	        	//System.out.println("Hit the wall of player " + Integer.toString(i+1));
	        	if (i==0 && players[0].isAlive()){
	        		players[0].reduceLife();
	        		nwh.sendStateInfo(updateStateOnNetwork(3));
	        	}
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
        	if (Physics.ballHitsCorner(i+1, ball)) {
        		//System.out.println("Ball has hit the corner "+i);
        		Physics.reflectBallFromCorner(ball, i+1);
        	}	       
    }
    
    // TODO: Application of PowerUp to the correct player
    // TODO: PowerUp Applied data packet
    private void ApplyPowerUpToPlayer (PowerUp powerUp, Player player){
    	System.out.println("Power Up: "+ powerUp.description + " to Player: "+ Integer.toString(player.playerNumber));
    	
    	switch(powerUp.powerUpType){
    	case 0: player.setBigPaddle();
    			break;
    	case 1: player.extraLife();
    			break;
    	case 3: player.hasShield = true;
				player.shieldTimeCounter = 0;
    			break;
    	case 4: ball.increaseSpeed();
    			break;
    	default :
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
    	} else if (opCode.equals("b")) {
    		int playerNumber = Integer.parseInt(data[1]);    	
    		for (int i=0; i<4; i++)
				if (players[i].networkPlayerNumber==playerNumber){					
					int packetNumber = Integer.parseInt(data[2]);
					if (packetNumber > players[i].networkPacketNumber){
						players[i].networkPacketNumber = packetNumber;
						float x = Float.parseFloat(data[3]);
						float y = Float.parseFloat(data[4]);
						float dx = Float.parseFloat(data[5]);
						float dy = Float.parseFloat(data[6]);
						players[i].setBallPosition(ball, x, y, dx, dy);
						repaint();
					}
				}
    	} else if (opCode.equals("c")) {
    		int playerNumber = Integer.parseInt(data[1]);    		
    		for (int i=0; i<4; i++)
				if (players[i].networkPlayerNumber==playerNumber){					
					int packetNumber = Integer.parseInt(data[2]);
					players[i].networkPacketNumber = packetNumber;
					int lives = Integer.parseInt(data[3]);
					players[i].setLives(lives);
				}
    	} else if (opCode.equals("d")) {
    		int playerNumber = Integer.parseInt(data[1]);    		
			if (playerNumber==0){					
				int index = Integer.parseInt(data[2]);
				int powerUpType = Integer.parseInt(data[3]);
				int x = Integer.parseInt(data[4]);
				int y = Integer.parseInt(data[5]);
				int showTime = Integer.parseInt(data[6]);
								
				powerUps[index] = players[hostPlayer].addPowerUpToGame(powerUpType, x, y, showTime);
			}
    	}
    }
    
    public String updateStateOnNetwork (int packetType){
    	
    	// packetType 1 -> Paddle Position
    	// packetType 2 -> Ball Position
    	// packetType 3 -> Life Lost
        // packetType 4 -> PowerUp Data
    	
    	String data = "";
    	
    	if (packetType == 1){
    	
	    	data += "a,";
	    	data += Integer.toString(players[0].networkPlayerNumber).concat(",");
	    	data += Integer.toString(players[0].networkPacketNumber).concat(",");
	    	players[0].networkPacketNumber += 1;
	    	data += Float.toString(players[0].paddle.x).concat(",");
    	} else if (packetType == 2) {
    		
	    	data += "b,";
	    	data += Integer.toString(players[0].networkPlayerNumber).concat(",");
	    	data += Integer.toString(players[0].networkPacketNumber).concat(",");
	    	players[0].networkPacketNumber += 1;
	    	data += Float.toString(ball.getX()).concat(",");
	    	data += Float.toString(ball.getY()).concat(",");
	    	data += Float.toString(ball.getXDir()).concat(",");
	    	data += Float.toString(ball.getYDir()).concat(",");
    	} else if (packetType == 3) {
    		
	    	data += "c,";
	    	data += Integer.toString(players[0].networkPlayerNumber).concat(",");
	    	data += Integer.toString(players[0].networkPacketNumber).concat(",");
	    	players[0].networkPacketNumber += 1;
	    	data += Integer.toString(players[0].lives()).concat(",");
    	} 

    	return data;
    }
    
    public String updateStateOnNetwork (int packetType, int powerUpNum){
    	
        // packetType 4 -> New PowerUp Data
    	
    	String data = "";
    	if (packetType == 4) {
            
            data += "d,";
            data += Integer.toString(players[0].networkPlayerNumber).concat(",");
            data += Integer.toString(powerUpNum).concat(",");
            data += Integer.toString(powerUps[powerUpNum].powerUpType).concat(",");
            data += Integer.toString((int)powerUps[powerUpNum].getX()).concat(",");
            data += Integer.toString((int)powerUps[powerUpNum].getY()).concat(",");
            data += Integer.toString(powerUps[powerUpNum].showTime).concat(",");
        }
    	return data;
    }
}