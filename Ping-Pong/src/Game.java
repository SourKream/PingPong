import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class Game extends JFrame {

	private Board board;
	
    public Game(int i) {
        
        initUI(i);
    }
    
    private void initUI(int i) {
        board = new Board();
        
        Border thickBorder = new LineBorder(Color.RED, Commons.BORDER);
        board.setBorder(thickBorder);
        add(board);
        
    //    add(new MenuPane(), BorderLayout.AFTER_LINE_ENDS);
        
        setTitle("Ping Pong");
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(Commons.WIDTH, Commons.HEIGHT + 22);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
		
		if (i==0){
			NetworkHandler nwh = new NetworkHandler(board,2);
		}		
		else {
			NetworkHandler nwh = new NetworkHandler(board,2, "localhost", 1231);
		}
		
    }

    public static void main(String[] args) {
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() { 
				
				if (args[0].equals("0")){               
                Game game = new Game(0);
                game.setVisible(true); 
				}
				else{
	                Game game = new Game(1);
	                game.setVisible(true); 
				}               
            }
        });
    }
}