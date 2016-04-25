import java.awt.Graphics2D;

public class Player implements Commons{
	
	public int networkPlayerNumber = -1;
	public int networkPacketNumber = 0;
	
	public Paddle paddle;
	private int lives;
	public boolean hasBigPaddle = false;
	public int bigPaddleTimeCounter = 0;
	public boolean hasShield = false;
	public int shieldTimeCounter = 0;
	public int playerNumber;  // Player 1 at 6
							  // Player 2 at 9
							  // Player 3 at 12
							  // Player 4 at 3

	public Player(int num){
		
		playerNumber = num;
		lives = INIT_LIVES;
		switch(num){
		case 1: paddle = new Paddle(0, WIDTH/2, BOTTOM_EDGE - 8, 1);
				break;
		case 2: paddle = new Paddle(1, BORDER, HEIGHT/2, 1);
				break;
		case 3: paddle = new Paddle(0, WIDTH/2, BORDER, 1);
				break;
		case 4: paddle = new Paddle(1, WIDTH - 8 - BORDER, HEIGHT/2, 1);
				break;
		}
	}
	
	public boolean isAlive(){
		return lives>0;
	}
	
	public void reduceLife(){
		if(hasShield){
			hasShield = false;
			System.out.println("Wall has shield!");
		}
		else
			lives = lives - 1;
	}
	
	public void kill(){
		lives = 0;
	}
	
	public int lives(){
		return lives;
	}
	
	public void setLives (int l){
		lives = l;
	}
	
	public void extraLife(){
		lives = lives + 1;
	}
	
	public void setBigPaddle()
	{
		hasBigPaddle = true;
		bigPaddleTimeCounter = 0;
		switch(playerNumber){
		case 1: paddle = new Paddle(0, paddle.getX(), BOTTOM_EDGE - 8, 2);
				break;
		case 2: paddle = new Paddle(1, BORDER, paddle.getY(), 2);
				break;
		case 3: paddle = new Paddle(0, paddle.getX(), BORDER, 2);
				break;
		case 4: paddle = new Paddle(1, WIDTH - 8 - BORDER, paddle.getY(), 2);
				break;
		}
	}
	
	public void setSmallPaddle()
	{
		hasBigPaddle = false;
		switch(playerNumber){
		case 1: paddle = new Paddle(0, paddle.getX(), BOTTOM_EDGE - 8, 1);
				break;
		case 2: paddle = new Paddle(1, BORDER, paddle.getY(), 1);
				break;
		case 3: paddle = new Paddle(0, paddle.getX(), BORDER, 1);
				break;
		case 4: paddle = new Paddle(1, WIDTH - 8 - BORDER, paddle.getY(), 1);
				break;
		}
	}
	
	public void setPaddlePosition(float position){
		if (playerNumber == 2)
			paddle.setY(position);
		
		if (playerNumber == 3)
			paddle.setX(WIDTH - position - paddle.i_width);
			
		if (playerNumber == 4)
			paddle.setY(HEIGHT - position - paddle.i_heigth);
	}	

	
	public void setBallPosition(Ball ball, float x, float y, float dx, float dy){
		if (playerNumber == 2){
			ball.setY(x);
			ball.setX(WIDTH-y-ball.i_width);
			ball.setYDir(dx);
			ball.setXDir(-dy);
		}			
		
		if (playerNumber == 3){
			ball.setX(WIDTH-x-ball.i_width);
			ball.setY(HEIGHT-y-ball.i_heigth);
			ball.setXDir(-dx);
			ball.setYDir(-dy);
		}
			
		if (playerNumber == 4){
			ball.setX(y);
			ball.setY(HEIGHT-x-ball.i_heigth);
			ball.setXDir(dy);
			ball.setYDir(-dx);
		}
	}	
	
	public PowerUp addPowerUpToGame(int PowerUpType, int x, int y, int ShowTime){
		
		PowerUp powerUp = new PowerUp(PowerUpType, x, y, ShowTime);

		if (playerNumber == 2){
			powerUp.setY(x);
			powerUp.setX(WIDTH-y-powerUp.i_width);
		}			
		
		if (playerNumber == 3){
			powerUp.setX(WIDTH-x-powerUp.i_width);
			powerUp.setY(HEIGHT-y-powerUp.i_heigth);
		}
			
		if (playerNumber == 4){
			powerUp.setX(y);
			powerUp.setY(HEIGHT-x-powerUp.i_heigth);
		}

		return powerUp;
	}
}
