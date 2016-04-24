
public class Physics {
	
	public static void reflectBallFromPaddle (Ball ball, Paddle paddle){
				
		if (paddle.movingAxis==0){

			int paddlePos = (int) paddle.getRect().getMaxX(); 
	        int ballPos = (int) ball.getRect().getMaxX();
	        int paddleLength = paddle.i_width + ball.i_width;
	        
	        double reflectingAngle = (((double)paddlePos - ballPos)/paddleLength) * Math.PI;
	        System.out.println("Reflection Angle (degrees): "+ Double.toString(reflectingAngle*180/Math.PI));

	        ball.setXDir((float)Math.cos(reflectingAngle));
	        if (paddle.getY()>Commons.HEIGHT/2)
	        	ball.setYDir(-(float)Math.sin(reflectingAngle));
	        else
	        	ball.setYDir((float)Math.sin(reflectingAngle));
	        
		} else {
			
			int paddlePos = (int) paddle.getRect().getMaxY();
	        int ballPos = (int) ball.getRect().getMaxY();
	        int paddleLength = paddle.i_heigth + ball.i_heigth;
	       
	        double reflectingAngle = (((double)paddlePos - ballPos)/paddleLength) * Math.PI;	        
	        System.out.println("Reflection Angle (degrees): "+ Double.toString(reflectingAngle*180/Math.PI));

	        ball.setYDir((float)Math.cos(reflectingAngle));
	        if (paddle.getX()>Commons.WIDTH/2)
	        	ball.setXDir(-(float)Math.sin(reflectingAngle));
	        else
	        	ball.setXDir((float)Math.sin(reflectingAngle));
		}
	}
	
	public static boolean ballHitPlayersWall(Ball ball, Player player){
		
		switch (player.playerNumber){
		case 1:	if (ball.getRect().getMaxY()>=Commons.BOTTOM_EDGE)
					return true;
				break;
		case 2:	if (ball.getRect().getMinX()<=Commons.BORDER)
					return true;
				break;
		case 3:	if (ball.getRect().getMinY()<=Commons.BORDER)
					return true;
				break;
		case 4: if (ball.getRect().getMaxX()>=Commons.BOTTOM_EDGE)
					return true;
		}
		return false;
	}
	
	public static void reflectBallFromWall(Ball ball, Player player){

		switch (player.playerNumber){
		case 1:
		case 3:	ball.setYDir(-1 * ball.getYDir());
				break;
		case 2: 
		case 4:	ball.setXDir(- 1 * ball.getXDir());
				break;
		}
	}
}