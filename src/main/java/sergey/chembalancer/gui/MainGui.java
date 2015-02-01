package sergey.chembalancer.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import sergey.chembalancer.chemistry.EquationBalancer;

/**
 * 
 * @author sergeys
 *
 */
public class MainGui
{
	//All of the elements for the GUI
	private JFrame frame;
	private JPanel panel;
	private JTextField field;
	private JButton button;
	private JLabel label;
	
	//Constructor
	public MainGui()
	{
		//Create the frame
		frame = new JFrame("Sergey's Chemical Equation Balancer");
		
		//Set frame size and close operation
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		//Create the layout
		GridBagLayout gbl = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		//Create a panel
		panel = new JPanel(gbl);
		
		//Create the elements
		field = new JTextField(40);
		button = new JButton("Calculate");
		label = new JLabel("");
		
		//Add an action to the button (using a lambda expression)
		button.addActionListener((e) -> {
				label.setText(new EquationBalancer(field.getText()).getResult(true));
				frame.pack();
			});
		
		//Create a label on the top (for white space)
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 0;
		panel.add(new JLabel("<html><br/><br/><br/></html>"), c);
		
		//Add the text field to the gui
		c.gridy = 1;
		panel.add(field, c);
		
		//Add the button to the gui
		c.gridx = 1;
		panel.add(button, c);
		
		//Add the output label to the gui
		c.gridx = 0;
		c.gridy = 2;
		c.gridwidth = 2;
		panel.add(label, c);
		
		//Create another label for spacing
		c.gridx = 0;
		c.gridy = 3;
		panel.add(new JLabel("<html><br/><br/><br/></html>"), c);
		
		//Add the panel to the frame
		frame.add(panel);
		
		//Resize the frame to be correct
		frame.pack();
		
		//Make it visible
		frame.setVisible(true);
	}
}
