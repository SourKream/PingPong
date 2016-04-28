import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.ImageIcon;

public class Ball extends Sprite implements Commons {

	// Ball movement directions
    private float xdir;
    private float ydir;
    
    // SpeedUp factor of ball
    public float speedup = (float)BALL_NORMAL_SPEED;	
    private int speedupCounter;

    public int lastPlayerToHit;

    public Ball() {

    	// Initial movement direction
        xdir = Math.round((1 * (float)Math.cos(Math.PI/4)) * 100) / 100f;
        ydir = Math.round((-1 * (float)Math.cos(Math.PI/4)) * 100) / 100f;

        ImageIcon ii = new ImageIcon(getClass().getResource("/res/ball.png"));
        image = ii.getImage();
        i_width = image.getWidth(null);
        i_heigth = image.getHeight(null);
        
        resetState();
    }

    // Called at every Timestep
    public void move() {
        
        x = Math.round((x + speedup * xdir) * 100) / 100f;
        y = Math.round((y + speedup * ydir) * 100) / 100f;

        if (speedup==BALL_FAST_SPEED){
        	speedupCounter += 1;
        	if (speedupCounter == FAST_BALL_TIME)
        		speedup = BALL_NORMAL_SPEED;
        }
    }
    
    public void increaseSpeed(){
    	speedup = BALL_FAST_SPEED;
    	speedupCounter = 0;
    }
    
    private void resetState() {
        
        x = INIT_BALL_X + ThreadLocalRandom.current().nextInt(0, 100);
        y = INIT_BALL_Y;
    }

    public void setXDir(float x) {
    	xdir = Math.round(x * 100) / 100f;
    }

    public void setYDir(float y) {
    	ydir = Math.round(y * 100) / 100f;
    }

    public float getYDir() {
        return ydir;
    }
    
    public float getXDir() {
        return xdir;
    }
}