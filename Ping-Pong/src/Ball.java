import javax.swing.ImageIcon;

public class Ball extends Sprite implements Commons {

    private int xdir;
    private int ydir;

    public Ball() {

        xdir = 1;
        ydir = -1;

        ImageIcon ii = new ImageIcon("res/ball.png");
        image = ii.getImage();

        i_width = image.getWidth(null);
        i_heigth = image.getHeight(null);

        resetState();
    }

    public void move() {
        
        x += xdir;
        y += ydir;

        if (x == BORDER) {
            setXDir(1);
        }

        if (x == WIDTH - i_width - BORDER) {
            setXDir(-1);
        }

        if (y == BORDER) {
            setYDir(1);
        }
    }

    private void resetState() {
        
        x = INIT_BALL_X;
        y = INIT_BALL_Y;
    }

    public void setXDir(int x) {
        xdir = x;
    }

    public void setYDir(int y) {
        ydir = y;
    }

    public int getYDir() {
        return ydir;
    }
    
    public int getXDir() {
        return xdir;
    }
    
    public void setPositionAndHeading (int x, int y, int xdir, int ydir){
    	setX(x);
    	setY(y);
    	this.xdir = xdir;
    	this.ydir = ydir;
    }
}