package dna.visualization.components.statsdisplay;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import dna.visualization.GuiOptions;

/**
 * A statstic group is a list that shows items and their corresponding values.
 * Several update- and add-methods make for an easy use.
 * 
 * @author Rwilmes
 * 
 */
@SuppressWarnings("serial")
public class StatsGroup extends JPanel {
	// panels
	public JPanel NamePanel;
	public JPanel ValuePanel;

	// constructor
	public StatsGroup(String title, Dimension size) {
		// set name
		this.setName(title);
		
		// size
		//this.setPreferredSize(size);

		// set border
		TitledBorder border = BorderFactory.createTitledBorder(title);
		border.setTitleFont(new Font(GuiOptions.defaultFont.getName(),
				Font.BOLD, GuiOptions.defaultFont.getSize()));
		this.setBorder(border);

		// add name and value panels
		this.NamePanel = new JPanel();
		this.NamePanel.setName("Labels");
		//this.NamePanel.setPreferredSize(new Dimension((int) Math.floor((size
				//.getWidth() - 5) / 2), size.height - 1));
		this.ValuePanel = new JPanel();
		this.ValuePanel.setName("Values");
		//this.ValuePanel.setPreferredSize(new Dimension((int) Math.floor((size
			//	.getWidth() - 5) / 2), size.height - 1));

		this.NamePanel
				.setLayout(new BoxLayout(this.NamePanel, BoxLayout.Y_AXIS));
		this.ValuePanel.setLayout(new BoxLayout(this.ValuePanel,
				BoxLayout.Y_AXIS));

		// set layout
		// this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.WEST;
		c.gridx = 0;
		c.gridy = 0;
		this.add(this.NamePanel, c);
		c.gridx = 1;
		this.add(this.ValuePanel, c);
	}

	/** add values to the panel **/
	public void addValue(String name, double value) {
		JLabel tempName = new JLabel(name + ": ");
		tempName.setName(name);
		tempName.setFont(GuiOptions.defaultFont);
		this.NamePanel.add(tempName);
		this.NamePanel.validate();

		JLabel tempValue = new JLabel("" + value);
		tempValue.setName(name + "value");
		tempValue.setFont(GuiOptions.defaultFont);
		this.ValuePanel.add(tempValue);
		this.ValuePanel.validate();

		this.revalidate();
	}

	/** update value already on the panel **/
	public void updateValue(String name, double value) {
		Component[] values = this.NamePanel.getComponents();
		for (int i = 0; i < values.length; i++) {
			if (values[i].getName().equals(name)) {
				Component c = this.ValuePanel.getComponents()[i];
				if (c instanceof JLabel) {
					((JLabel) c).setText("" + value);
				}
			}
		}
		this.validate();
	}

	/** increment values **/
	public void incrementValue(String name) {
		Component[] values = this.NamePanel.getComponents();
		for (int i = 0; i < values.length; i++) {
			if (values[i].getName().equals(name)) {
				Component c = this.ValuePanel.getComponents()[i];
				if (c instanceof JLabel) {
					double tempValue = Double.parseDouble(((JLabel) c)
							.getText());
					tempValue++;
					((JLabel) c).setText("" + tempValue);
				}
			}
		}
		this.validate();
	}

	/** decrement values **/
	public void decrementValue(String name) {
		Component[] values = this.NamePanel.getComponents();
		for (int i = 0; i < values.length; i++) {
			if (values[i].getName().equals(name)) {
				Component c = this.ValuePanel.getComponents()[i];
				if (c instanceof JLabel) {
					double tempValue = Double.parseDouble(((JLabel) c)
							.getText());
					tempValue--;
					((JLabel) c).setText("" + tempValue);
				}
			}
		}
		this.validate();
	}

	/** resets all set values to zero **/
	public void reset() {
		for (Component c : this.ValuePanel.getComponents()) {
			if (c instanceof JLabel)
				((JLabel) c).setText("" + 0);
		}
		this.validate();
	}

	/** clears the whole list **/
	public void clear() {
		for (Component c : this.NamePanel.getComponents()) {
			this.NamePanel.remove(c);
		}
		for (Component c : this.ValuePanel.getComponents()) {
			this.ValuePanel.remove(c);
		}
		this.validate();
	}

}
