import java.awt.event.KeyEvent;

public class AIPlayer extends Player implements Runnable {
	
	private Board board;
	private int SleepTime = 20;

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
		if (playerNumber == 2){
			int min = 0;
			for (int i=0; i<board.balls.size(); i++){
				if (board.balls.get(i).x < board.balls.get(min).x)
					min = i;
			}
			if (board.balls.get(min).getRect().getMaxY() < paddle.getY()+paddle.i_heigth/4)
				paddle.keyPressed(KeyEvent.VK_LEFT);
			else if (board.balls.get(min).getRect().getMinY() > paddle.getRect().getMaxY()-paddle.i_heigth/4)
				paddle.keyPressed(KeyEvent.VK_RIGHT);
			else
				paddle.keyReleased(KeyEvent.VK_RIGHT);
		}else if (playerNumber == 4){
			int min = 0;
			for (int i=0; i<board.balls.size(); i++){
				if (board.balls.get(i).x > board.balls.get(min).x)
					min = i;
			}
			if (board.balls.get(min).getRect().getMaxY() < paddle.getY()+paddle.i_heigth/4)
				paddle.keyPressed(KeyEvent.VK_LEFT);
			else if (board.balls.get(min).getRect().getMinY() > paddle.getRect().getMaxY()-paddle.i_heigth/4)
				paddle.keyPressed(KeyEvent.VK_RIGHT);
			else
				paddle.keyReleased(KeyEvent.VK_RIGHT);
		} else if (playerNumber == 3){
			int min = 0;
			for (int i=0; i<board.balls.size(); i++){
				if (board.balls.get(i).y < board.balls.get(min).y)
					min = i;
			}
			if (board.balls.get(min).getRect().getMaxX() < paddle.getX()+paddle.i_width/4)
				paddle.keyPressed(KeyEvent.VK_LEFT);
			else if (board.balls.get(min).getRect().getMinX() > paddle.getRect().getMaxX()-paddle.i_width/4)
				paddle.keyPressed(KeyEvent.VK_RIGHT);
			else
				paddle.keyReleased(KeyEvent.VK_RIGHT);
		} 
	}
}
