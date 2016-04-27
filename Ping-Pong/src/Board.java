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
    public Ball ball;
	public Player players[];
    public List<Integer> PlayersInMyControl = new ArrayList<>();
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

        this.setBackground(Commons.BoardColor);

        corner = new Corners[4];        
        corner[0] = new Corners(Commons.CORNER_1_X, Commons.CORNER_1_Y);
        corner[1] = new Corners(Commons.CORNER_2_X, Commons.CORNER_2_Y);
        corner[2] = new Corners(Commons.CORNER_3_X, Commons.CORNER_3_Y);
        corner[3] = new Corners(Commons.CORNER_4_X, Commons.CORNER_4_Y);
        
        setDoubleBuffered(true);
        timer = new Timer();
    }
    
	public void setNumPlayers(int players){
		this.numPlayers = players;
	}
	
	public void startAIPlayer (int networkID){
		int id = getPlayerByNetworkID(networkID);
		players[id] = new AIPlayer(id+1, this);
		players[id].setNetworkPlayerNumber(networkID);
		((AIPlayer) players[id]).startPlaying();
	}
	
    public int getPlayerByNetworkID (int id){
    	for (int i=0; i<players.length; i++)
    		if (players[i].getNetworkPlayerNumber()==id)
    			return i;
    	return -1;
    }
	
    public void startGame(){
    	
    	// Assigning Network Player Number to all players
    	if (numPlayers == 1){
			players[0].setNetworkPlayerNumber(nwh.myPlayerNo);    		
			players[1].kill();    		
			players[2] = new AIPlayer(3, this);
			((AIPlayer) players[2]).startPlaying();
			players[3].kill();    		
//        	for (int i=1; i<4; i++)
//   			players[i].kill();    		
    	} else if (numPlayers == 2){
    		players[0].setNetworkPlayerNumber(nwh.myPlayerNo);
    		players[2].setNetworkPlayerNumber((nwh.myPlayerNo + 1)%2);
    		players[1].kill();
    		players[3].kill();
    	} else if (numPlayers == 3){
    		if (nwh.myPlayerNo==0){
	    		players[0].setNetworkPlayerNumber(0);
	    		players[1].setNetworkPlayerNumber(1);
	    		players[2].setNetworkPlayerNumber(2);
	    		players[3].kill();
    		} else if (nwh.myPlayerNo==1){
	    		players[3].setNetworkPlayerNumber(0);
	    		players[0].setNetworkPlayerNumber(1);
	    		players[1].setNetworkPlayerNumber(2);
	    		players[2].kill();
    		} else if (nwh.myPlayerNo==2){
	    		players[2].setNetworkPlayerNumber(0);
	    		players[3].setNetworkPlayerNumber(1);
	    		players[0].setNetworkPlayerNumber(2);
	    		players[1].kill();
    		}
    	} else if (numPlayers == 4){
        	for (int i=0; i<4; i++)
    			players[i].setNetworkPlayerNumber((nwh.myPlayerNo + i)%4);
    	}
    	
		for (int i=0; i<4; i++){
			if (players[i].getNetworkPlayerNumber()==0)
				hostPlayer = i;
			
		}

		// Sending Initial Ball Position
		if (hostPlayer==0){
			nwh.sendStateInfo(updateStateOnNetwork(2,0));
			nwh.sendStateInfo(updateStateOnNetwork(2,0));
			nwh.sendStateInfo(updateStateOnNetwork(2,0));
			nwh.sendStateInfo(updateStateOnNetwork(2,0));
			nwh.sendStateInfo(updateStateOnNetwork(2,0));

		// Send Power Up Info
	        for (int i=0; i<Commons.NUM_POWER_UPS; i++){
				nwh.sendStateInfo(updateStateOnNetwork(4,0,i));	        	
				nwh.sendStateInfo(updateStateOnNetwork(4,0,i));	        	
				nwh.sendStateInfo(updateStateOnNetwork(4,0,i));	        	
				nwh.sendStateInfo(updateStateOnNetwork(4,0,i));	        	
				nwh.sendStateInfo(updateStateOnNetwork(4,0,i));	        	
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
    	
    	PlayersInMyControl.clear();
    	PlayersInMyControl.add(0);

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
        	g2d.setColor(Commons.CornerColor);
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
    		players[0].paddle.keyReleased(e.getKeyCode());
        }

        @Override
        public void keyPressed(KeyEvent e) {
    		players[0].paddle.keyPressed(e.getKeyCode());
        }
    }

    private class ScheduleTask extends TimerTask {

        @Override
        public void run() {

        	ball.move();
            for (int i=0; i<PlayersInMyControl.size(); i++){
            	players[PlayersInMyControl.get(i)].paddle.move();
            	nwh.sendStateInfo(updateStateOnNetwork(1,i));
            }
            checkCollision();
            checkPaddle();
            timeCount = (timeCount + 1)%Commons.MAX_COUNT;
            checkShowTimeForPowerUps();
			
			if (ballInMyArea(ball)){
	        		nwh.sendStateInfo(updateStateOnNetwork(2,0));
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
		        if (Physics.testIntersection(ball.getCircle(), players[i].paddle.getRect())){
		        	lastPlayerToHitTheBall = i;
		        	if (PlayersInMyControl.contains(i))
		        		nwh.sendStateInfo(updateStateOnNetwork(5,i));
		        	Physics.reflectBallFromPaddle(ball, players[i].paddle);
		        	return;
		        }

        // Collision of Ball with a PowerUp
        for (int i=0; i<powerUps.length; i++)
        	if (powerUps[i].isActive())
        		if (Physics.testIntersection(powerUps[i].getCircle(), ball.getCircle())){
        			powerUps[i].Disable();
        			ApplyPowerUpToPlayer(powerUps[i], players[lastPlayerToHitTheBall]);
		        	if (PlayersInMyControl.contains(lastPlayerToHitTheBall))
		        		nwh.sendStateInfo(updateStateOnNetwork(6,lastPlayerToHitTheBall,i));
        			return;
        		}

        // Collision of Ball with a Player's Wall
        for (int i=0; i<players.length; i++)
	        if (Physics.ballHitPlayersWall(ball, players[i])){
	        	//System.out.println("Hit the wall of player " + Integer.toString(i+1));
	        	if (PlayersInMyControl.contains(i) && players[i].isAlive()){
	        		players[i].reduceLife();
	        		nwh.sendStateInfo(updateStateOnNetwork(3,i));
	        	}
	        	Physics.reflectBallFromWall(ball, players[i]);
	        	return;
	        }        
        // Collision of Ball with a corner
        for (int i = 0; i<4; i++)
        	if (Physics.testIntersection(corner[i].getCorner(), ball.getCircle())) {
        		//System.out.println("Ball has hit the corner "+i);
        		Physics.reflectBallFromCorner(ball, i+1, corner[i]);
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
    
    private void checkPowerUpApplicationToPlayer (int powerUpIndex, int playerIndex){
    	switch(powerUps[powerUpIndex].powerUpType){
    	case 0: if (!players[playerIndex].hasBigPaddle){
    				System.out.println("CONFLICT IN POWER UP");
    				players[playerIndex].setBigPaddle();
    			}
    			break;
    	case 3: if (!players[playerIndex].hasShield){
    				System.out.println("CONFLICT IN POWER UP");
	    			players[playerIndex].hasShield = true;
					players[playerIndex].shieldTimeCounter = 0;
    			}
    			break;
    	case 4: if (ball.speedup != Commons.BALL_FAST_SPEED){
					System.out.println("CONFLICT IN POWER UP");
					ball.increaseSpeed();
		    	}
		    	break;
    	default :
    	}    	
    }
    
    public void updateStateFromNetwork (String inputString){

    	String data[] = inputString.split(",");
    	String opCode = data[0];
    	if (opCode.equals("a")){
    		int playerNumber = Integer.parseInt(data[1].trim());    		
    		for (int i=0; i<4; i++)
				if (players[i].getNetworkPlayerNumber()==playerNumber){					
					int packetNumber = Integer.parseInt(data[2]);
					if (packetNumber > players[i].networkPacketNumber){
						players[i].networkPacketNumber = packetNumber;
						float position = Float.parseFloat(data[3]);
						players[i].setPaddlePosition(position);
					}					
				}
    	} else if (opCode.equals("b")) {
    		int playerNumber = Integer.parseInt(data[1].trim());    	
    		for (int i=0; i<4; i++)
				if (players[i].getNetworkPlayerNumber()==playerNumber){					
					int packetNumber = Integer.parseInt(data[2].trim());
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
    		int playerNumber = Integer.parseInt(data[1].trim());    		
    		for (int i=0; i<4; i++)
				if (players[i].getNetworkPlayerNumber()==playerNumber){					
					int packetNumber = Integer.parseInt(data[2].trim());
					players[i].networkPacketNumber = packetNumber;
					int lives = Integer.parseInt(data[3].trim());
					players[i].setLives(lives);
				}
    	} else if (opCode.equals("d")) {
    		int playerNumber = Integer.parseInt(data[1].trim());    		
			if (playerNumber==0){					
				int index = Integer.parseInt(data[2].trim());
				int powerUpType = Integer.parseInt(data[3].trim());
				int x = Integer.parseInt(data[4].trim());
				int y = Integer.parseInt(data[5].trim());
				int showTime = Integer.parseInt(data[6].trim());
								
				powerUps[index] = players[hostPlayer].addPowerUpToGame(powerUpType, x, y, showTime);
			}
    	} else if (opCode.equals("e")) {
    		int playerNumber = Integer.parseInt(data[1].trim());    		
    		for (int i=0; i<4; i++)
				if (players[i].getNetworkPlayerNumber()==playerNumber){					
					int packetNumber = Integer.parseInt(data[2].trim());
					players[i].networkPacketNumber = packetNumber;
					if (lastPlayerToHitTheBall != i){
						System.out.println("Disagreement with last player to hit the ball");
						lastPlayerToHitTheBall = i;
					}
				}
    	} else if (opCode.equals("f")) {
    		System.out.println("SOME POWERUP INFO RECEIVED");
    		int playerNumber = Integer.parseInt(data[1].trim());    		
    		for (int i=0; i<4; i++)
				if (players[i].getNetworkPlayerNumber()==playerNumber){		
					int packetNumber = Integer.parseInt(data[2].trim());
					players[i].networkPacketNumber = packetNumber;
					int powerUpIndex = Integer.parseInt(data[3].trim());
					checkPowerUpApplicationToPlayer(powerUpIndex, i);
				}
    	}
    }
    
    public String updateStateOnNetwork (int packetType, int playerNumber){
    	
    	// packetType 1 -> Paddle Position
    	// packetType 2 -> Ball Position
    	// packetType 3 -> Life Lost
        // packetType 4 -> PowerUp Data
        // packetType 5 -> Set Last Player to hit the ball
    	
    	String data = "";
    	
    	if (packetType == 1){
    	
	    	data += "a,";
	    	data += Integer.toString(players[playerNumber].getNetworkPlayerNumber()).concat(",");
	    	data += Integer.toString(players[playerNumber].networkPacketNumber).concat(",");
	    	players[playerNumber].networkPacketNumber += 1;
	    	data += Float.toString(players[playerNumber].paddle.x).concat(",");
    	} else if (packetType == 2) {
    		
	    	data += "b,";
	    	data += Integer.toString(players[0].getNetworkPlayerNumber()).concat(",");
	    	data += Integer.toString(players[0].networkPacketNumber).concat(",");
	    	players[0].networkPacketNumber += 1;
	    	data += Float.toString(ball.getX()).concat(",");
	    	data += Float.toString(ball.getY()).concat(",");
	    	data += Float.toString(ball.getXDir()).concat(",");
	    	data += Float.toString(ball.getYDir()).concat(",");
    	} else if (packetType == 3) {
    		
	    	data += "c,";
	    	data += Integer.toString(players[playerNumber].getNetworkPlayerNumber()).concat(",");
	    	data += Integer.toString(players[playerNumber].networkPacketNumber).concat(",");
	    	players[playerNumber].networkPacketNumber += 1;
	    	data += Integer.toString(players[playerNumber].lives()).concat(",");
    	} else if (packetType == 5) {
    		
	    	data += "e,";
	    	data += Integer.toString(players[playerNumber].getNetworkPlayerNumber()).concat(",");
	    	data += Integer.toString(players[playerNumber].networkPacketNumber).concat(",");
	    	players[playerNumber].networkPacketNumber += 1;
    	} 

    	return data;
    }
    
    public String updateStateOnNetwork (int packetType, int playerNumber, int powerUpNum){
    	
        // packetType 4 -> New PowerUp Data
        // packetType 6 -> PowerUp Applied to Player
    	
    	String data = "";
    	if (packetType == 4) {
            
            data += "d,";
            data += Integer.toString(players[0].getNetworkPlayerNumber()).concat(",");
            data += Integer.toString(powerUpNum).concat(",");
            data += Integer.toString(powerUps[powerUpNum].powerUpType).concat(",");
            data += Integer.toString((int)powerUps[powerUpNum].getX()).concat(",");
            data += Integer.toString((int)powerUps[powerUpNum].getY()).concat(",");
            data += Integer.toString(powerUps[powerUpNum].showTime).concat(",");
        } else if (packetType == 6) {
    		
	    	data += "f,";
	    	data += Integer.toString(players[playerNumber].getNetworkPlayerNumber()).concat(",");
	    	data += Integer.toString(players[playerNumber].networkPacketNumber).concat(",");
	    	players[playerNumber].networkPacketNumber += 1;
	    	data += Integer.toString(powerUpNum);
    	}
    	return data;
    }
}