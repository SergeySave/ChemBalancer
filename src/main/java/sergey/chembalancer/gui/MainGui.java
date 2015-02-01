package sergey.chembalancer.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import sergey.chembalancer.chemistry.EquationBalancer;

public class MainGui
{
	JFrame frame;
	JPanel panel;
	JTextField field;
	JButton button;
	JLabel label;
	
	public MainGui()
	{
		frame = new JFrame("Sergey's Chemical Equation Balancer");
		
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		panel = new JPanel(gbl);
		
		field = new JTextField(40);
		button = new JButton("Calculate");
		label = new JLabel("");
		
		button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				label.setText(new EquationBalancer(field.getText()).getResult(true));
				frame.pack();
			}
		});
		
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(new JLabel("<html><br/><br/><br/></html>"), c);
		c.gridy = 1;
		panel.add(field, c);
		c.gridx = 1;
		panel.add(button, c);
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		panel.add(label, c);
		c.gridx = 0;
		c.gridy = 3;
		panel.add(new JLabel("<html><br/><br/><br/></html>"), c);
		
		frame.add(panel);
		
		
		frame.pack();
		
		frame.setVisible(true);
	}
}
