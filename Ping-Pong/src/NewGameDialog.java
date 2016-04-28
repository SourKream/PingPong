import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.LineBorder;

/** @see http://stackoverflow.com/a/3002830/230513 */
class NewGameDialog {

	public static boolean isHost; 
	
    public static void display() {

    	JDialog dialog = new JDialog();
    	
        JPanel panel = new JPanel(new GridLayout(2,1,10,40));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 10, 40, 10));
        
        JButton joinButton = new JButton("Join Game");        
        JButton hostButton = new JButton("Host Game");
        panel.add(hostButton);
        panel.add(joinButton);
        
        joinButton.addActionListener(new ActionListener() {
        	 
            public void actionPerformed(ActionEvent e)
            {
                //Execute when button is pressed
                System.out.println("You clicked the join button");

                setHost(false);
                DetailsDialog.display(1);
            }
        }); 
        
        hostButton.addActionListener(new ActionListener() {
       	 
            public void actionPerformed(ActionEvent e)
            {
                //Execute when button is pressed
                System.out.println("You clicked the host button");

                setHost(true);
                DetailsDialog.display(0);
            }
        }); 
        
        dialog.add(panel);
        dialog.setTitle("PING PONG");
        dialog.setSize(200, 200);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
    
    public static void setHost(boolean i){
    	isHost = i;
    }
    public static boolean isHost(){
    	return isHost;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                display();
            }
        });
    }
}