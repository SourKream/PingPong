import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

public class Sprite {

    protected float x;
    protected float y;
    protected int i_width;
    protected int i_heigth;
    protected Image image;

    public void setX(float x) {
        this.x = x;
    }

    public float getX() {
        return x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getY() {
        return y;
    }

    public int getWidth() {
        return i_width;
    }

    public int getHeight() {
        return i_heigth;
    }

    Image getImage() {
        return image;
    }

    Rectangle getRect() {
        return new Rectangle(Math.round(x), Math.round(y),
                image.getWidth(null), image.getHeight(null));        
    }
    
    Ellipse2D getCircle() {
    	return new Ellipse2D.Float(x, y, image.getWidth(null), image.getHeight(null));
    }
    
}