import javax.swing.ImageIcon;

public class Ball extends Sprite implements Commons {

    private float xdir;
    private float ydir;
    public float speedup = (float)BALL_NORMAL_SPEED;	
    private int speedupCounter;

    public Ball() {

        xdir = 1 * (float)Math.cos(Math.PI/4);
        ydir = -1 * (float)Math.sin(Math.PI/4);

        ImageIcon ii = new ImageIcon("../res/ball.png");
        image = ii.getImage();

        i_width = image.getWidth(null);
        i_heigth = image.getHeight(null);

        resetState();
    }

    public void move() {
        
        x += speedup * xdir;
        y += speedup * ydir;
        
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
        
        x = INIT_BALL_X;
        y = INIT_BALL_Y;
    }

    public void setXDir(float x) {
        xdir = x;
    }

    public void setYDir(float y) {
        ydir = y;
    }

    public float getYDir() {
        return ydir;
    }
    
    public float getXDir() {
        return xdir;
    }
}