
public class Physics implements Commons {
	
	public static void reflectBallFromPaddle (Ball ball, Paddle paddle){
				
		if (paddle.movingAxis==0){

			int paddlePos = (int) paddle.getRect().getMaxX() + ball.i_width; 
	        int ballPos = (int) ball.getRect().getMaxX();
	        int paddleLength = paddle.i_width + 2*ball.i_width;
	        
	        double reflectingAngle = (((double)paddlePos - ballPos)/paddleLength);
	        reflectingAngle = reflectingAngle*Math.PI*5/6 + Math.PI/12;
	        //System.out.println("Reflection Angle (degrees): "+ Double.toString(reflectingAngle*180/Math.PI));

	        ball.setXDir((float)Math.cos(reflectingAngle));
	        if (paddle.getY()>Commons.HEIGHT/2)
	        	ball.setYDir(-(float)Math.sin(reflectingAngle));
	        else
	        	ball.setYDir((float)Math.sin(reflectingAngle));
	        
		} else {
			
			int paddlePos = (int) paddle.getRect().getMaxY() + ball.i_heigth;
	        int ballPos = (int) ball.getRect().getMaxY();
	        int paddleLength = paddle.i_heigth + 2*ball.i_heigth;
	       
	        double reflectingAngle = (((double)paddlePos - ballPos)/paddleLength);
	        reflectingAngle = reflectingAngle*Math.PI*5/6 + Math.PI/12;
	        //System.out.println("Reflection Angle (degrees): "+ Double.toString(reflectingAngle*180/Math.PI));

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
	
    public static boolean ballHitsCorner(int corner_no, Ball ball)
    {
    	switch (corner_no){
		case 1:	if(ball.getY() <= CORNER_1_Y[1] + 
				(-1)*(ball.getX()-CORNER_1_X[1]))
				return true;
				break;
		case 2:	if(ball.getY() <= CORNER_2_Y[0] + 
				(1)*(ball.getX()+ball.i_width-CORNER_2_X[0]))
				return true;
				break;
		case 3:	if(ball.getY()+ball.i_heigth >= CORNER_3_Y[0] + 
				(-1)*(ball.getX()+ball.i_width-CORNER_3_X[0]))
				return true;
				break;
		case 4: if(ball.getY()+ball.i_heigth >= CORNER_4_Y[0] + 
				(1)*(ball.getX()-CORNER_4_X[0]))
				return true;
				break;
		}
    	return false;
    }

    public static void reflectBallFromCorner(Ball ball, int corner_no){
    	
    	// Corner number 1 -> Top Left
    	// Corner number 2 -> Top Right ...
    	
    	float xdir = ball.getXDir();
    	float ydir = ball.getYDir();
    	
    	switch(corner_no)
    	{
    	case 1:
    	case 3:	ball.setXDir(-ydir);
    			ball.setYDir(-xdir);
    			break;
    	case 2:
    	case 4: ball.setXDir(ydir);
				ball.setYDir(xdir);
				break;
    	}
	}
	
}
