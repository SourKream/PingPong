import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class Game extends JFrame {

	private Board board;
	
	public Game(int i, String[] hostAddress_or_connPlayers_and_aiPlayers){
		initUI(i, hostAddress_or_connPlayers_and_aiPlayers);
	}
    
    private void initUI(int i, String[] hostAddress_or_connPlayers_and_aiPlayers) {
		
		if (i==0){	// if host
        board = new Board (Integer.parseInt(hostAddress_or_connPlayers_and_aiPlayers[1]) + Integer.parseInt(hostAddress_or_connPlayers_and_aiPlayers[2]) + 1);
		}
		else{	// if client
		board = new Board (0);	// don't know now, will set later
		}
		
	
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
		
		if (i==0){	// host
			NetworkHandler nwh = new NetworkHandler(board,Integer.parseInt(hostAddress_or_connPlayers_and_aiPlayers[1]),Integer.parseInt(hostAddress_or_connPlayers_and_aiPlayers[2]));
		}		
		else {	// client
			NetworkHandler nwh = new NetworkHandler(board, hostAddress_or_connPlayers_and_aiPlayers[1], 1231);
		}
		
    }

    public static void main(final String[] args) {
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() { 				
                Game game = new Game(Integer.parseInt(args[0]),args);
                game.setVisible(true); 
            }
        });
    }
}