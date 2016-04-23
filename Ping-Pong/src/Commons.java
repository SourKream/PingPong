// Defining some global parameters

public interface Commons {

    public static final int BORDER = 10;   //Width of the board boundary
    public static final int WIDTH = 400;
    public static final int HEIGHT = 400;
    public static final int BOTTOM_EDGE = HEIGHT - BORDER;
    public static final int INIT_PADDLE_X = WIDTH/2;
    public static final int INIT_PADDLE_Y = BOTTOM_EDGE - 10;
    public static final int INIT_BALL_X = WIDTH/2;
    public static final int INIT_BALL_Y = BOTTOM_EDGE - 15;    
    public static final int DELAY = 1000;
    public static final int PERIOD = 10;
    public static final int SIZE = 20;
    public static final int[] CORNER_1_X = {BORDER, BORDER+SIZE, BORDER};
    public static final int[] CORNER_2_X = {WIDTH-BORDER-SIZE, WIDTH-BORDER, WIDTH-BORDER};
    public static final int[] CORNER_3_X = {WIDTH-BORDER, WIDTH-BORDER, WIDTH-BORDER-SIZE};
    public static final int[] CORNER_4_X = {BORDER, BORDER+SIZE, BORDER};
    public static final int[] CORNER_1_Y = {BORDER, BORDER, BORDER+SIZE};
    public static final int[] CORNER_2_Y = {BORDER, BORDER, BORDER+SIZE};
    public static final int[] CORNER_3_Y = {HEIGHT-BORDER-SIZE, HEIGHT-BORDER, HEIGHT-BORDER};
    public static final int[] CORNER_4_Y = {HEIGHT-BORDER-SIZE, HEIGHT-BORDER, HEIGHT-BORDER};
}