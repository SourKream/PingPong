import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;

public class Heart extends Sprite implements Commons {

    private int colour;

    public Heart (float initX, float initY, int colour) {
       
    	setColor(colour);
    	x = initX;
        y = initY;
    }
    
    public void setColor(int colour){
    	this.colour = colour;
    	makeHearts();
    }
    
    public void makeHearts(){

    	String HeartImagePath = "res/Hearts/";
    		
    		HeartImagePath += ("Life" + Integer.toString(colour) + ".png");

    	ImageIcon ii = new ImageIcon(getClass().getResource(HeartImagePath));
        image = ii.getImage();

        i_width = image.getWidth(null);
        i_heigth = image.getHeight(null);    	
    }

}