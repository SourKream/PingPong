import java.awt.Color;
// Defining some global parameters

public interface Commons {

    public static final int BORDER = 10;   //Width of the board boundary
    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;
    public static final int AURA = 20;
    public static final int BOTTOM_EDGE = HEIGHT - BORDER;
    public static final int INIT_BALL_X = WIDTH/2 + 30;
    public static final int INIT_BALL_Y = BOTTOM_EDGE - 40;    
    public static final int DELAY = 1000;
    public static final int PERIOD = 15;
    public static final int SIZE = 40;		//Corner Size
    public static final int INIT_LIVES = 10;
    public static final int MAX_COUNT = 2500;
    public static final int POWER_UP_TIME = 600;
    public static final int NUM_POWER_UPS = 10;
    public static final int FAST_BALL_TIME = 800;
    public static final int BALL_FAST_SPEED = 8;
    public static final float BALL_NORMAL_SPEED = (float)4;
    public static final int PADDLE_STEP = 8;
    public static final Color BorderColor = new Color(0, 70, 70);
    public static final Color CornerColor = new Color(130,170,170);
    public static final Color BoardColor = new Color(230, 255, 255);
    public static final int[] CORNER_1_X = {BORDER, BORDER+SIZE, BORDER};
    public static final int[] CORNER_2_X = {WIDTH-BORDER-SIZE, WIDTH-BORDER, WIDTH-BORDER};
    public static final int[] CORNER_3_X = {WIDTH-BORDER, WIDTH-BORDER, WIDTH-BORDER-SIZE};
    public static final int[] CORNER_4_X = {BORDER, BORDER+SIZE, BORDER};
    public static final int[] CORNER_1_Y = {BORDER, BORDER, BORDER+SIZE};
    public static final int[] CORNER_2_Y = {BORDER, BORDER, BORDER+SIZE};
    public static final int[] CORNER_3_Y = {HEIGHT-BORDER-SIZE, HEIGHT-BORDER, HEIGHT-BORDER};
    public static final int[] CORNER_4_Y = {HEIGHT-BORDER-SIZE, HEIGHT-BORDER, HEIGHT-BORDER};
    public static final int[] BORDER_1_X = {WIDTH-BORDER, WIDTH, WIDTH, WIDTH-BORDER};
    public static final int[] BORDER_1_Y = {0, 0, HEIGHT+22, HEIGHT+22};
    
}