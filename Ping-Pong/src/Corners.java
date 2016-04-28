import java.awt.Polygon;
import java.awt.geom.Rectangle2D;

public class Corners implements Commons {
	
	public Polygon poly;
	public float slope; 

    public Corners(int [] x, int [] y) {
    	if(x.length == 3)
    		poly = new Polygon(x, y, 3);
    	else
    		poly = new Polygon(x, y, 4);
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