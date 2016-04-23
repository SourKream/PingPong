// Defining some global parameters

public interface Commons {

    public static final int BORDER = 10;   //Width of the board boundary
    public static final int WIDTH = 400;
    public static final int HEIGTH = 400;
    public static final int BOTTOM_EDGE = 400 - BORDER;
    public static final int INIT_PADDLE_X = 200;
    public static final int INIT_PADDLE_Y = 390 - BORDER - 4;
    public static final int INIT_BALL_X = 230;
    public static final int INIT_BALL_Y = 390 - BORDER - 9;    
    public static final int DELAY = 1000;
    public static final int PERIOD = 10;
    public static final int SIZE = 20;
    public static final int[] CORNER_1_X = {BORDER, BORDER+SIZE, BORDER};
    public static final int[] CORNER_2_X = {WIDTH-BORDER-SIZE, WIDTH-BORDER, WIDTH-BORDER};
    public static final int[] CORNER_3_X = {WIDTH-BORDER, WIDTH-BORDER, WIDTH-BORDER-SIZE};
    public static final int[] CORNER_4_X = {BORDER, BORDER+SIZE, BORDER};
    public static final int[] CORNER_1_Y = {BORDER, BORDER, BORDER+SIZE};
    public static final int[] CORNER_2_Y = {BORDER, BORDER, BORDER+SIZE};
    public static final int[] CORNER_3_Y = {HEIGTH-BORDER-SIZE, HEIGTH-BORDER, HEIGTH-BORDER};
    public static final int[] CORNER_4_Y = {HEIGTH-BORDER-SIZE, HEIGTH-BORDER, HEIGTH-BORDER};
}