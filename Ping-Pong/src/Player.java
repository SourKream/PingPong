import java.awt.Graphics2D;

public class Player implements Commons{
	
	public Paddle paddle;
	private int lives;
	public boolean isBigPaddle;
	public int playerNumber;  // Player 1 at 6
							  // Player 2 at 9
							  // Player 3 at 12
							  // Player 4 at 3

	public Player(int num){
		
		playerNumber = num;
		lives = INIT_LIVES;
		isBigPaddle = false;
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
		lives = lives - 1;
	}
	
	public void extraLife(){
		lives = lives + 1;
	}
	
	public void bigPaddle(int num)
	{
		switch(num){
		case 1: paddle = new Paddle(0, (int)paddle.getX(), BOTTOM_EDGE - 8, 2);
				break;
		case 2: paddle = new Paddle(1, BORDER, (int)paddle.getY(), 2);
				break;
		case 3: paddle = new Paddle(0, (int)paddle.getX(), BORDER, 2);
				break;
		case 4: paddle = new Paddle(1, WIDTH - 8 - BORDER, (int)paddle.getY(), 2);
				break;
		}
	}
	
	public void smallPaddle(int num)
	{
		switch(num){
		case 1: paddle = new Paddle(0, (int)paddle.getX(), BOTTOM_EDGE - 8, 1);
				break;
		case 2: paddle = new Paddle(1, BORDER, (int)paddle.getY(), 1);
				break;
		case 3: paddle = new Paddle(0, (int)paddle.getX(), BORDER, 1);
				break;
		case 4: paddle = new Paddle(1, WIDTH - 8 - BORDER, (int)paddle.getY(), 1);
				break;
		}
	}
}
