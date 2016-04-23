import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

    public class MenuPane extends JPanel {

        public MenuPane() {
            setBorder(new EmptyBorder(4, 4, 4, 4));
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            add(new JButton("New Game"), gbc);
            gbc.gridy++;
            add(new JButton("Add Player"), gbc);
            gbc.gridy++;
            add(new JButton("Pause"), gbc);
            gbc.gridy++;
            add(new JButton("Exit"), gbc);

        }
    }
