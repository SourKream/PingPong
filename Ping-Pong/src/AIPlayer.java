import java.awt.event.KeyEvent;

public class AIPlayer extends Player implements Runnable {
	
	private Board board;
	private int SleepTime = 200;

	public AIPlayer(int num, Board b){
		super(num);					
		board = b;
	}
	
    public void run() {
        while (true){
        	movePaddle();
        	try {
    			Thread.sleep(SleepTime);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}        	
        }
    }
	
	public void startPlaying(){
		
		System.out.println("Starting AI");
		board.PlayersInMyControl.add(playerNumber-1);
		Thread t = new Thread(this);
		t.start();		
	}
		
	public void movePaddle (){		
		if (playerNumber == 2 || playerNumber == 4){
			if (board.ball.getRect().getMaxY() < paddle.getY()+paddle.i_heigth/4)
				paddle.keyPressed(KeyEvent.VK_LEFT);
			else if (board.ball.getRect().getMinY() > paddle.getRect().getMaxY()-paddle.i_heigth/4)
				paddle.keyPressed(KeyEvent.VK_RIGHT);
			else
				paddle.keyReleased(KeyEvent.VK_RIGHT);
		} else if (playerNumber == 3){
			if (board.ball.getRect().getMaxX() < paddle.getX()+paddle.i_width/4)
				paddle.keyPressed(KeyEvent.VK_LEFT);
			else if (board.ball.getRect().getMinX() > paddle.getRect().getMaxX()-paddle.i_width/4)
				paddle.keyPressed(KeyEvent.VK_RIGHT);
			else
				paddle.keyReleased(KeyEvent.VK_RIGHT);
		} 
	}
}
