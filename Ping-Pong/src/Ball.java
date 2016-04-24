import javax.swing.ImageIcon;

public class Ball extends Sprite implements Commons {

    private float xdir;
    private float ydir;
    private float speedup = 1;

    public Ball() {

        xdir = 1 * (float)Math.cos(Math.PI/4);
        ydir = -1 * (float)Math.sin(Math.PI/4);

        ImageIcon ii = new ImageIcon("res/ball.png");
        image = ii.getImage();

        i_width = image.getWidth(null);
        i_heigth = image.getHeight(null);

        resetState();
    }

    public void move() {
        
        x += speedup * xdir;
        y += speedup * ydir;

    }
    
    public void increaseSpeed(){
    	speedup = 2;
    }
    
    public void slowDown(){
    	speedup = 1;
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
    
    public void setPositionAndHeading (float x, float y, float xdir, float ydir){
    	setX(x);
    	setY(y);
    	this.xdir = xdir;
    	this.ydir = ydir;
    }
}