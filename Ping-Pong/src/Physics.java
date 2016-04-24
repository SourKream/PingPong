
public class Physics implements Commons {
	
	public static void reflectBallFromPaddle (Ball ball, Paddle paddle){
		
		
		if (paddle.movingAxis==0)
			ball.setYDir(-1 * ball.getYDir());
		else
			ball.setXDir(-1 * ball.getXDir());
/*
		int paddleLPos = (int) paddle.getRect().getMinX();
        int ballLPos = (int) ball.getRect().getMinX();

        int first = paddleLPos + 8;
        int second = paddleLPos + 16;
        int third = paddleLPos + 24;
        int fourth = paddleLPos + 32;
        

        if (ballLPos < first) {
            ball.setXDir(-1);
            ball.setYDir(-1);
        }

        if (ballLPos >= first && ballLPos < second) {
            ball.setXDir(-1);
            ball.setYDir(-1 * ball.getYDir());
        }

        if (ballLPos >= second && ballLPos < third) {
            ball.setXDir(0);
            ball.setYDir(-1);
        }

        if (ballLPos >= third && ballLPos < fourth) {
            ball.setXDir(1);
            ball.setYDir(-1 * ball.getYDir());
        }

        if (ballLPos > fourth) {
            ball.setXDir(1);
            ball.setYDir(-1);
        }
*/
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
		case 1:	ball.setYDir(-1);
				break;
		case 2:	ball.setXDir(1);
				break;
		case 3:	ball.setYDir(1);
				break;
		case 4: ball.setXDir(-1);
		}
	}
	
    public static boolean ballHitsCorner(int corner_no, Ball ball)
    {
    	switch (corner_no){
		case 0:	if(ball.getY() <= CORNER_1_Y[1] + 
				(-1)*(ball.getX()-CORNER_1_X[1]))
				return true;
				break;
		case 1:	if(ball.getY() <= CORNER_2_Y[0] + 
				(1)*(ball.getX()-CORNER_2_X[0]))
				return true;
				break;
		case 2:	if(ball.getY() >= CORNER_3_Y[0] + 
				(-1)*(ball.getX()-CORNER_3_X[0]))
				return true;
				break;
		case 3: if(ball.getY() >= CORNER_4_Y[0] + 
				(1)*(ball.getX()-CORNER_4_X[0]))
				return true;
				break;
		}
    	return false;
    }

	
	
}
