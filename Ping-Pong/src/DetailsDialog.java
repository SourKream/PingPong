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
class DetailsDialog {

	 
    public static void display(int i) {

    	 
        String[] args = {"","","",""};
        
        String[] items = {"1", "2", "3", "4"};
        String[] items_2 = {"0", "1", "2", "3"};
        String[] items_3 = {"Low", "Medium", "High"};
        
        //final String[] items_2 = new String[4];
        JComboBox<String> combo_1 = new JComboBox<String>(items);
        JComboBox<String> combo_2 = new JComboBox<String>(items_2);
        JComboBox<String> combo_3 = new JComboBox<String>(items_3);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextField host_ip = new JTextField(" ");
        
        if(i == 0){
        	panel.add(new JLabel("Number of Human Player:"));
            panel.add(combo_1);       
            
            panel.add(new JLabel("Number of AI Player:"));
            panel.add(combo_2);
            
            panel.add(new JLabel("AI Level:"));
            panel.add(combo_3);
        }
        else
        {
            panel.add(new JLabel("Host IP Address:"));
            panel.add(host_ip);	
        }
        
        
        int result = JOptionPane.showConfirmDialog(null, panel, "PING PONG",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
        	
        	if(NewGameDialog.isHost())
        	{
	        	int Players = Integer.valueOf((String) combo_1.getSelectedItem()) + Integer.valueOf((String) combo_2.getSelectedItem());
	        	if(Players <= 4 && Players !=1)
	        	{ 
	        		int humanPlayer = Integer.valueOf((String) combo_1.getSelectedItem());
	        		
		        	if(NewGameDialog.isHost())
		        	{
		        		args[0] = "0";
		        		args[1] = Integer.toString(humanPlayer-1);
		        		args[2] = (String) combo_2.getSelectedItem();
		        		args[3] = (String) combo_3.getSelectedItem();
	
		        		System.out.println("Input:"+args[0]+args[1]+args[2]+args[3]);
		        	}
		        	else
		        	{
		        		args[0] = "1";
		        		args[1] =  host_ip.getText();
		        		args[1] = args[1].trim();
		        		
		        		System.out.println("Input:"+args[0]+args[1]);
		        	}     	
		        	Game game = new Game(Integer.parseInt(args[0]), args);
		            game.setVisible(true); 
	        	}
	        	else 
	        	{
	        		System.out.println("Error!");
	        		if(NewGameDialog.isHost())
	        		{
			        		if(Players > 4)
			        		{
			        			ToastMessage toastMessage = new DetailsDialog().new ToastMessage("Error: MAX 4 Player Allowed!",5000);
			        			toastMessage.setVisible(true);
			        			display(i);
			        		}
			        		if(Players == 1)
			        		{
			        			ToastMessage toastMessage = new DetailsDialog().new ToastMessage("Error: ATLEAST 2 Player!",5000);
			        			toastMessage.setVisible(true);
			        			display(i);
			        		}
	        		}
		        }
        	}
        	else
        	{
        		args[0] = "1";
        		args[1] =  host_ip.getText();
        		args[1] = args[1].trim();
        		
        		Game game = new Game(Integer.parseInt(args[0]), args);
	            game.setVisible(true);
        	}
        } else {
            System.out.println("Cancelled");
        }
    }
    
    public class ToastMessage extends JDialog {
        int miliseconds;
        public ToastMessage(String toastString, int time) {
            this.miliseconds = time;
            setUndecorated(true);
            getContentPane().setLayout(new BorderLayout(0, 0));

            JPanel panel = new JPanel();
            panel.setBackground(Color.GRAY);
            panel.setBorder(new LineBorder(Color.LIGHT_GRAY, 2));
            getContentPane().add(panel, BorderLayout.CENTER);

            JLabel toastLabel = new JLabel("");
            toastLabel.setText(toastString);
            toastLabel.setFont(new Font("Dialog", Font.BOLD, 12));
            toastLabel.setForeground(Color.WHITE);

            setBounds(100, 100, toastLabel.getPreferredSize().width+20, 31);


            setAlwaysOnTop(true);
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            int y = dim.height/2-getSize().height/2;
            int half = y/2;
            setLocation(dim.width/2-getSize().width/2, y+half);
            panel.add(toastLabel);
            setVisible(false);

            new Thread(){
                public void run() {
                    try {
                        Thread.sleep(miliseconds);
                        dispose();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

}