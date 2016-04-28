import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.util.concurrent.ThreadLocalRandom;

public class Board extends JPanel implements Commons {

    private Timer timer;
    private int timeCount;
    private String message = "Game Over";
    public List<Ball> balls = new ArrayList<>();
	public Player players[];
    public List<Integer> PlayersInMyControl = new ArrayList<>();
    private int numPlayers = 1;
    private Corners corner[];
    public boolean ingame = true;
    private PowerUp powerUps[];
    private int hostPlayer;
    private Image shields[];
	private NetworkHandler nwh;
	private Corners rightBorder;
    
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
        
        rightBorder = new Corners(Commons.BORDER_1_X, Commons.BORDER_1_Y);
        
        shields = new Image[2];
        ImageIcon ii;
        ii = new ImageIcon("../res/ShieldH.png");
        shields[0] = ii.getImage();
        ii = new ImageIcon("../res/ShieldV.png");
        shields[1] = ii.getImage();
                
        setDoubleBuffered(true);
        timer = new Timer();
    }
    
	public void setNumPlayers(int players){
		this.numPlayers = players;
	}
	
	public void startAIPlayer (int networkID){
		int id = getPlayerByNetworkID(networkID);
		players[id] = new AIPlayer(id+1, this, players[id].lives());
		players[id].setNetworkPlayerNumber(networkID);
		((AIPlayer) players[id]).startPlaying();
	}
	
    public int getPlayerByNetworkID (int id){
    	for (int i=0; i<players.length; i++)
    		if (players[i].getNetworkPlayerNumber()==id)
    			return i;
    	return -1;
    }
	
    public void startGame(int numAIPlayers){
    	
    	// Assigning Network Player Number to all players
    	if (numPlayers == 1){
			players[0].setNetworkPlayerNumber(nwh.myPlayerNo);    		
        	for (int i=1; i<4; i++)
        		players[i].kill();    		
    	} else if (numPlayers == 2){
    		players[0].setNetworkPlayerNumber(nwh.myPlayerNo);
    		System.out.println("ID: " + players[0].getNetworkPlayerNumber());
    		players[2].setNetworkPlayerNumber((nwh.myPlayerNo + 1)%2);
    		System.out.println("ID: " + players[2].getNetworkPlayerNumber());
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
		
		for (int i=0; i<numAIPlayers; i++){
			startAIPlayer(i+1);
		}

		// Sending Initial Ball Position
		if (hostPlayer==0){
			for (int i=0; i<balls.size(); i++){
				nwh.sendStateInfo(updateStateOnNetwork(2,0,i));
				nwh.sendStateInfo(updateStateOnNetwork(2,0,i));
				nwh.sendStateInfo(updateStateOnNetwork(2,0,i));
				nwh.sendStateInfo(updateStateOnNetwork(2,0,i));
				nwh.sendStateInfo(updateStateOnNetwork(2,1,i));
				nwh.sendStateInfo(updateStateOnNetwork(2,1,i));
				nwh.sendStateInfo(updateStateOnNetwork(2,1,i));
				nwh.sendStateInfo(updateStateOnNetwork(2,1,i));
			}

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

        balls.add(new Ball());
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

        drawShields(g2d);
                
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
        
    	for (int i=0; i<balls.size(); i++)
        g2d.drawImage(balls.get(i).getImage(), Math.round(balls.get(i).getX()), Math.round(balls.get(i).getY()),
                balls.get(i).getWidth(), balls.get(i).getHeight(), this);
        
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
        
        g2d.setColor(Commons.BorderColor);
    	g2d.fill(rightBorder.getCorner());
    }
    
    private void showLives(Graphics2D g2d){
    	
        Font font = new Font("Verdana", Font.BOLD, 18);
        FontMetrics metr = this.getFontMetrics(font);

        g2d.setColor(Color.BLACK);
        g2d.setFont(font);
        
        g2d.drawString("Balls: " + Integer.toString(balls.size()),
				        Commons.WIDTH + 115 - metr.stringWidth(message),
				        50); 
        g2d.drawString("Lives:",
                Commons.WIDTH + 115 - metr.stringWidth(message),
                60+20*(1));  
        
	    for (int i=0; i<4; i++)
	    	if (players[i].isAlive())
		        g2d.drawString("P" + Integer.toString(i+1) + ": " + Integer.toString(players[i].lives()),
		                Commons.WIDTH + 115 - metr.stringWidth(message),
		                60+30*(i+2));    	

    }
    
    private void gameFinished(Graphics2D g2d) {

        Font font = new Font("Verdana", Font.BOLD, 18);
        FontMetrics metr = this.getFontMetrics(font);

        g2d.setColor(Color.BLACK);
        g2d.setFont(font);
        
        if (players[0].isAlive())
        	message = "You Win !!!";
        
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

        	for (int i=0; i<balls.size(); i++)
        		balls.get(i).move();
            for (int i=0; i<PlayersInMyControl.size(); i++){
            	players[PlayersInMyControl.get(i)].paddle.move();
            	nwh.sendStateInfo(updateStateOnNetwork(1,PlayersInMyControl.get(i)));
            }
            checkCollision();
            checkPaddle();
            timeCount = (timeCount + 1)%Commons.MAX_COUNT;
            checkShowTimeForPowerUps();
			
            for (int i=0; i<balls.size(); i++)
				if (ballInMyArea(balls.get(i)))
		        		nwh.sendStateInfo(updateStateOnNetwork(2,0,i));

            repaint();
        }
    }
    
    private boolean ballInMyArea(Ball ball){
    	
    	for (int i=0; i<PlayersInMyControl.size(); i++)
	    	if (Physics.testIntersection(players[PlayersInMyControl.get(i)].paddle.getVibeRectangle(), ball.getCircle()))
	    		return true;
    	return false;
    }
    
    private void checkShowTimeForPowerUps(){
    	for (int i=0; i<powerUps.length; i++)
    		powerUps[i].checkShowTime(timeCount);
    }
    
    private void checkPaddle(){

    	for(int i=0; i<4; i++){
    		if(players[i].hasBigPaddle){
        		players[i].bigPaddleTimeCounter += 1;
        		if(players[i].bigPaddleTimeCounter == Commons.POWER_UP_TIME){
        			players[i].setSmallPaddle();
        			nwh.sendStateInfo(updateStateOnNetwork(3,i));
        		}
        	}	
    		if(players[i].hasShield){
        		players[i].shieldTimeCounter += 1;
        		if(players[i].shieldTimeCounter == Commons.POWER_UP_TIME){
        			players[i].hasShield = false;
        			nwh.sendStateInfo(updateStateOnNetwork(3,i));
        		}
        	}
    	}   	    			
    }

    private void stopGame() {

        ingame = false;
        timer.cancel();
    }
    
    private void checkCollision() {

        if (!players[0].isAlive())
            stopGame();
        
        if (!(players[1].isAlive() || players[2].isAlive() || players[3].isAlive()))
        	stopGame();
        
        // Collision of Ball with Paddle
        for (int j=0; j<balls.size(); j++)
        for (int i=0; i<players.length; i++)
        	if (players[i].isAlive())
		        if (Physics.testIntersection(balls.get(j).getCircle(), players[i].paddle.getRect())){
		        	balls.get(j).lastPlayerToHit = i;
		        	if (PlayersInMyControl.contains(i))
		        		nwh.sendStateInfo(updateStateOnNetwork(5,i,j));
		        	Physics.reflectBallFromPaddle(balls.get(j), players[i].paddle);
		        	return;
		        }

        // Collision of Ball with a PowerUp
        for (int j=0; j<balls.size(); j++)
        for (int i=0; i<powerUps.length; i++)
        	if (powerUps[i].isActive())
        		if (Physics.testIntersection(powerUps[i].getCircle(), balls.get(j).getCircle())){
        			powerUps[i].Disable();
        			if (powerUps[i].powerUpType==4){
        				ApplyPowerUpToPlayer(powerUps[i], j);
        				nwh.sendStateInfo(updateStateOnNetwork(6,balls.get(j).lastPlayerToHit,j));
        			} else
        				ApplyPowerUpToPlayer(powerUps[i], balls.get(j).lastPlayerToHit);
        			return;
        		}

        // Collision of Ball with a Player's Wall
        for (int j=0; j<balls.size(); j++)
        for (int i=0; i<players.length; i++)
	        if (Physics.ballHitPlayersWall(balls.get(j), players[i])){
	        	//System.out.println("Hit the wall of player " + Integer.toString(i+1));
	        	if (PlayersInMyControl.contains(i) && players[i].isAlive()){
	        		players[i].reduceLife();
	        		nwh.sendStateInfo(updateStateOnNetwork(3,i));
	        	}
	        	Physics.reflectBallFromWall(balls.get(j), players[i]);
	        	return;
	        }        
        // Collision of Ball with a corner
        for (int j=0; j<balls.size(); j++)
        for (int i = 0; i<4; i++)
        	if (Physics.testIntersection(corner[i].getCorner(), balls.get(j).getCircle())) {
        		//System.out.println("Ball has hit the corner "+i);
        		Physics.reflectBallFromCorner(balls.get(j), i+1, corner[i]);
        	}	       
    }
    
    private void ApplyPowerUpToPlayer (PowerUp powerUp, int index){
    	
    	switch(powerUp.powerUpType){
    	case 0: players[index].setBigPaddle();
    			nwh.sendStateInfo(updateStateOnNetwork(3,index));
    			break;
    	case 1: if (players[index].isAlive()){
					players[index].extraLife();
					nwh.sendStateInfo(updateStateOnNetwork(3,index));
				}
    			break;
    	case 2: if (PlayersInMyControl.contains(index)){
    				balls.add(new Ball());
    				nwh.sendStateInfo(updateStateOnNetwork(7,index));
    				nwh.sendStateInfo(updateStateOnNetwork(2,index,balls.size()-1));
    			}
    			break;
    	case 3: players[index].hasShield = true;
				players[index].shieldTimeCounter = 0;
    			nwh.sendStateInfo(updateStateOnNetwork(3,index));
    			break;
    	case 4: balls.get(index).increaseSpeed();
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
					int packetNumber = Integer.parseInt(data[2].trim());
					if (packetNumber > players[i].networkPacketNumber){
						players[i].networkPacketNumber = packetNumber;
						float position = Float.valueOf(data[3]);
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
						int ballNumber = Integer.parseInt(data[3].trim());
						float x = Float.valueOf(data[4]);
						float y = Float.valueOf(data[5]);
						float dx = Float.valueOf(data[6]);
						float dy = Float.valueOf(data[7]);
						if (ballNumber < balls.size())
							players[i].setBallPosition(balls.get(ballNumber), x, y, dx, dy);
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
					if (data[4].trim().equals("t"))
						players[i].hasShield = true;
					else
						players[i].hasShield = false;
					if (data[5].trim().equals("t"))
						players[i].setBigPaddle();
					else
						players[i].setSmallPaddle();
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
					int ballNumber = Integer.parseInt(data[3].trim());
					players[i].networkPacketNumber = packetNumber;
					if (balls.get(ballNumber).lastPlayerToHit != i){
						System.out.println("Disagreement with last player to hit the ball");
						balls.get(ballNumber).lastPlayerToHit = i;
					}
				}
    	} else if (opCode.equals("f")) {
    		System.out.println("Make Ball Fast Command From Network");
    		int playerNumber = Integer.parseInt(data[1].trim());    		
    		for (int i=0; i<4; i++)
				if (players[i].getNetworkPlayerNumber()==playerNumber){		
					int packetNumber = Integer.parseInt(data[2].trim());
					players[i].networkPacketNumber = packetNumber;
					int ballIndex = Integer.parseInt(data[3].trim());
					if (balls.get(ballIndex).speedup != Commons.BALL_FAST_SPEED)
			        	balls.get(ballIndex).increaseSpeed();
				}
    	} else if (opCode.equals("g")) {
    		int playerNumber = Integer.parseInt(data[1].trim());    		
    		for (int i=0; i<4; i++)
				if (players[i].getNetworkPlayerNumber()==playerNumber){		
					int packetNumber = Integer.parseInt(data[2].trim());
					players[i].networkPacketNumber = packetNumber;
					int numBalls = Integer.parseInt(data[3].trim());
					if (balls.size()<numBalls)
						while(balls.size()<numBalls)
							balls.add(new Ball());
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
	    	data += Integer.toString(players[playerNumber].paddle.getPosition());
    	} else if (packetType == 3) {
    		
	    	data += "c,";
	    	data += Integer.toString(players[playerNumber].getNetworkPlayerNumber()).concat(",");
	    	data += Integer.toString(players[playerNumber].networkPacketNumber).concat(",");
	    	players[playerNumber].networkPacketNumber += 1;
	    	data += Integer.toString(players[playerNumber].lives()).concat(",");
	    	if (players[playerNumber].hasShield)
	    		data += "t,";
	    	else
	    		data += "f,";
	    	if (players[playerNumber].hasBigPaddle)
	    		data += "t";
	    	else
	    		data += "f";	    	
    	} else if (packetType == 7) {
    		
	    	data += "g,";
	    	data += Integer.toString(players[playerNumber].getNetworkPlayerNumber()).concat(",");
	    	data += Integer.toString(players[playerNumber].networkPacketNumber).concat(",");
	    	players[playerNumber].networkPacketNumber += 1;
	    	data += Integer.toString(balls.size());
    	}

    	return data;
    }
    
    public String updateStateOnNetwork (int packetType, int playerNumber, int index){
    	
        // packetType 4 -> New PowerUp Data
        // packetType 6 -> Fast Ball Applied to Ball
    	
    	String data = "";
    	if (packetType == 4) {
            
            data += "d,";
            data += Integer.toString(players[0].getNetworkPlayerNumber()).concat(",");
            data += Integer.toString(index).concat(",");
            data += Integer.toString(powerUps[index].powerUpType).concat(",");
            data += Integer.toString((int)powerUps[index].getX()).concat(",");
            data += Integer.toString((int)powerUps[index].getY()).concat(",");
            data += Integer.toString(powerUps[index].showTime);
        } else if (packetType == 2) {
    		
	    	data += "b,";
	    	data += Integer.toString(players[0].getNetworkPlayerNumber()).concat(",");
	    	data += Integer.toString(players[0].networkPacketNumber).concat(",");
	    	players[0].networkPacketNumber += 1;
	    	data += Integer.toString(index).concat(",");	    	
	    	data += String.format( "%.2f", balls.get(index).getX()).concat(",");
	    	data += String.format( "%.2f", balls.get(index).getY()).concat(",");
	    	data += String.format( "%.2f", balls.get(index).getXDir()).concat(",");
	    	data += String.format( "%.2f", balls.get(index).getYDir());
    	} else if (packetType == 6) {
    		
	    	data += "f,";
	    	data += Integer.toString(players[playerNumber].getNetworkPlayerNumber()).concat(",");
	    	data += Integer.toString(players[playerNumber].networkPacketNumber).concat(",");
	    	players[playerNumber].networkPacketNumber += 1;
	    	data += Integer.toString(index);
    	} else if (packetType == 5) {
    		
	    	data += "e,";
	    	data += Integer.toString(players[playerNumber].getNetworkPlayerNumber()).concat(",");
	    	data += Integer.toString(players[playerNumber].networkPacketNumber).concat(",");
	    	players[playerNumber].networkPacketNumber += 1;
	    	data += Integer.toString(index);	  
    	} 
    	return data;
    }
    
    private void drawShields(Graphics2D g2d){
    	
       	if (players[0].hasShield)
       		g2d.drawImage(shields[0], 0,Commons.HEIGHT-30, 
       					shields[0].getWidth(null), shields[0].getHeight(null), this);
       	if (players[2].hasShield)
       		g2d.drawImage(shields[0], 0,10, 
       					shields[0].getWidth(null), shields[0].getHeight(null), this);
       	if (players[1].hasShield)
       		g2d.drawImage(shields[1], 10,0, 
       					shields[1].getWidth(null), shields[1].getHeight(null), this);
       	if (players[3].hasShield)
       		g2d.drawImage(shields[1], Commons.WIDTH-30,0, 
       					shields[1].getWidth(null), shields[1].getHeight(null), this);
    }
}