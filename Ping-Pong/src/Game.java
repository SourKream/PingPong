import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class Game extends JFrame {

	private Board board;
	
    public Game(int i, int numPlayers, String hostAddress) {
        
        initUI(i, numPlayers, hostAddress);
    }
    
    private void initUI(int i, int numPlayers, String hostAddress) {
        board = new Board (numPlayers);
        
        Border thickBorder = new LineBorder(Commons.BorderColor, Commons.BORDER);
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
			NetworkHandler nwh = new NetworkHandler(board,numPlayers-1);
		}		
		else {
			NetworkHandler nwh = new NetworkHandler(board,numPlayers-1, hostAddress, 1231);
		}
		
    }

    public static void main(final String[] args) {
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() { 				
                Game game = new Game(Integer.parseInt(args[0]),Integer.parseInt(args[1]),args[2]);
                game.setVisible(true); 
            }
        });
    }
}