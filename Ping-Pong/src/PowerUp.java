import javax.swing.ImageIcon;

public class PowerUp extends Sprite implements Commons {

/*
	Power Up Type: 
	0 -> Big Paddle
	1 -> Extra Life
	2 -> Multi Ball
	3 -> Shield
	4 -> Fast Ball
*/
	
	private boolean isActive = false;
	public int showTime;
	public int powerUpType;
	public String description;
	
	public PowerUp(int PowerUpType, int init_x, int init_y, int showTime){
		
		this.showTime = showTime;
		this.powerUpType = PowerUpType;
		
		ImageIcon ii;
		switch(PowerUpType){
		case 0: ii = new ImageIcon(getClass().getResource("/res/BigPaddle.png"));
				description = "Big Paddle";
				break;
		case 1: ii = new ImageIcon(getClass().getResource("/res/ExtraLife.png"));
				description = "Extra Life";
				break;
		case 2: ii = new ImageIcon(getClass().getResource("/res/MultiBall.png"));
				description = "Multi Ball";
				break;
		case 3: ii = new ImageIcon(getClass().getResource("/res/Shield.png"));
				description = "Shield";
				break;
		case 4: ii = new ImageIcon(getClass().getResource("/res/FastBall.png"));
				description = "Fast Ball";
				break;
		default: ii = new ImageIcon(getClass().getResource("r./es/BigPaddle.png"));
		}		

        image = ii.getImage();
        i_width = image.getWidth(null);
        i_heigth = image.getHeight(null);
        
        x = init_x;
        y = init_y;
		
	}
	
	public boolean isActive(){
		return isActive;
	}
	
	public void Activate(){
		isActive = true;
	}
	
	public void Disable(){
		isActive = false;
	}
	
	public void checkShowTime(int timeCount){
		if (isActive==false)
			if (timeCount == showTime)
				isActive = true;
		if (isActive==true)
			if (timeCount == showTime + 1000)
				isActive = false;
	}
}
