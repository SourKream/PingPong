import java.awt.Polygon;
import java.awt.geom.Rectangle2D;

public class Corners implements Commons {
	
	public Polygon poly;

    public Corners(int [] x, int [] y) {
    	 poly = new Polygon(x, y, 3);
    }
    
    Polygon getCorner()
    {
    	return poly;
    }
    
    Rectangle2D getRect()
    {
		return poly.getBounds2D();
    }

}