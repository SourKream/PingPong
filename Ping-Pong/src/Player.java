import java.awt.Graphics2D;

public class Player implements Commons{
	
	public Paddle paddle;
	private int lives;
	public int playerNumber;  // Player 1 at 6
							  // Player 2 at 9
							  // Player 3 at 12
							  // Player 4 at 3

	public Player(int num){
		
		playerNumber = num;
		lives = INIT_LIVES;
		
		switch(num){
		case 1: paddle = new Paddle(0, WIDTH/2, BOTTOM_EDGE - 10);
				break;
		case 2: paddle = new Paddle(1, BORDER, HEIGHT/2);
				break;
		case 3: paddle = new Paddle(0, WIDTH/2, BORDER);
				break;
		case 4: paddle = new Paddle(1, WIDTH - 10 - BORDER, HEIGHT/2);
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
}
