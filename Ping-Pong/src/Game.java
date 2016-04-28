import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class Game extends JFrame {

	private Board board;
	
	public Game(int i, String[] argsIn){
		initUI(i, argsIn);
	}
    
    private void initUI(int i, String[] argsIn) {
		
		if (i==0){	// if host
	    board = new Board (Integer.parseInt(argsIn[1]) + Integer.parseInt(argsIn[2]) + 1);
		}
		else{	// if client
			System.out.println("Client!!!!");
		board = new Board (0);	// don't know now, will set later
		}
		
		String OS = OsUtils.getOsName();
		
        Border thickBorder = new LineBorder(Commons.BorderColor, Commons.BORDER);
        board.setBorder(thickBorder);
        add(board);
        
    //    add(new MenuPane(), BorderLayout.AFTER_LINE_ENDS);
        
        setTitle("Ping Pong");
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        if(OsUtils.isLinux())
        {
        	setSize(Commons.WIDTH + 100, Commons.HEIGHT);
        }
        if(OsUtils.isMac())
        	setSize(Commons.WIDTH, Commons.HEIGHT + 22);
        
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
		if (i==0){	// host
			NetworkHandler nwh = new NetworkHandler(board,Integer.parseInt(argsIn[1]),Integer.parseInt(argsIn[2]));
		}		
		else {	// client
			System.out.println("Add:"+argsIn[1]);
			NetworkHandler nwh = new NetworkHandler(board, argsIn[1], 1231);
		}
		
    }
    
    public static final class OsUtils
    {
       private static String OS = null;
       public static String getOsName()
       {
          if(OS == null) { OS = System.getProperty("os.name"); }
          return OS;
       }
       public static boolean isLinux() 
       {
    	  return getOsName().startsWith("Linux");
       }
       public static boolean isMac() 
       {
    	  return getOsName().startsWith("Mac OS X");
       }
    }

    public static void main(final String[] args) {
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() { 	
            	NewGameDialog.display();  
            	
            	//Game game = new Game(Integer.parseInt(args[0]), args);
	            //game.setVisible(true); 
            }
        });
    }
}