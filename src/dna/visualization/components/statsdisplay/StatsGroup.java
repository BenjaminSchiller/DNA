package dna.visualization.components.statsdisplay;

import java.awt.Component;
import java.awt.Font;

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
	public StatsGroup(String title) {
		// set name
		this.setName(title);

		// set border
		TitledBorder border = BorderFactory.createTitledBorder(title);
		border.setTitleFont(new Font(GuiOptions.defaultFont.getName(),
				Font.BOLD, GuiOptions.defaultFont.getSize()));
		this.setBorder(border);

		// add name and value panels
		this.NamePanel = new JPanel();
		this.NamePanel.setName("Labels");
		this.ValuePanel = new JPanel();
		this.ValuePanel.setName("Values");

		this.NamePanel
				.setLayout(new BoxLayout(this.NamePanel, BoxLayout.Y_AXIS));
		this.ValuePanel.setLayout(new BoxLayout(this.ValuePanel,
				BoxLayout.Y_AXIS));

		this.add(this.NamePanel);
		this.add(this.ValuePanel);

		// set layout
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
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
	}

	/** resets all set values to zero **/
	public void reset() {
		for (Component c : this.ValuePanel.getComponents()) {
			if (c instanceof JLabel)
				((JLabel) c).setText("" + 0);
		}
	}

	/** clears the whole list **/
	public void clear() {
		for (Component c : this.NamePanel.getComponents()) {
			this.NamePanel.remove(c);
		}
		for (Component c : this.ValuePanel.getComponents()) {
			this.ValuePanel.remove(c);
		}
	}

}
