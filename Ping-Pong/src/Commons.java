// Defining some global parameters

public interface Commons {

    public static final int BORDER = 10;   //Width of the board boundary
    public static final int WIDTH = 400;
    public static final int HEIGHT = 400;
    public static final int BOTTOM_EDGE = HEIGHT - BORDER;
    public static final int INIT_BALL_X = WIDTH/2 + 30;
    public static final int INIT_BALL_Y = BOTTOM_EDGE - 30;    
    public static final int DELAY = 1000;
    public static final int PERIOD = 5;
    public static final int SIZE = 26;
    public static final int INIT_LIVES = 2;
    public static final int MAX_COUNT = 25000;
    public static final int POWER_UP_TIME = 2500;
    public static final int NUM_POWER_UPS = 10;
    public static final int FAST_BALL_TIME = 700;
    public static final int[] CORNER_1_X = {BORDER, BORDER+SIZE, BORDER};
    public static final int[] CORNER_2_X = {WIDTH-BORDER-SIZE, WIDTH-BORDER, WIDTH-BORDER};
    public static final int[] CORNER_3_X = {WIDTH-BORDER, WIDTH-BORDER, WIDTH-BORDER-SIZE};
    public static final int[] CORNER_4_X = {BORDER, BORDER+SIZE, BORDER};
    public static final int[] CORNER_1_Y = {BORDER, BORDER, BORDER+SIZE};
    public static final int[] CORNER_2_Y = {BORDER, BORDER, BORDER+SIZE};
    public static final int[] CORNER_3_Y = {HEIGHT-BORDER-SIZE, HEIGHT-BORDER, HEIGHT-BORDER};
    public static final int[] CORNER_4_Y = {HEIGHT-BORDER-SIZE, HEIGHT-BORDER, HEIGHT-BORDER};
}