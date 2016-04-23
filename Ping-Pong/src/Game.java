import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class Game extends JFrame {

    public Game() {
        
        initUI();
    }
    
    private void initUI() {
        
     
        Board bd = new Board();
        Border thickBorder = new LineBorder(Color.RED, Commons.BORDER);
        bd.setBorder(thickBorder);
        add(bd);
        
    //    add(new MenuPane(), BorderLayout.AFTER_LINE_ENDS);
        
        setTitle("Ping Pong");
        
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(Commons.WIDTH, Commons.HEIGTH);
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    public static void main(String[] args) {
        
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {                
                Game game = new Game();
                game.setVisible(true);                
            }
        });
    }
}