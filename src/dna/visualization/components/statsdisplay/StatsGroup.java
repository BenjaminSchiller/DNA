package dna.visualization.components.statsdisplay;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;

import javax.swing.BorderFactory;
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
	// main constraints and valuelabels hashmap
	private GridBagConstraints statsGroupConstraints;
	private HashMap<String, JLabel> valueLabels;

	// constructor
	public StatsGroup(String title) {
		// set name
		this.setName(title);

		// init hashmap
		this.valueLabels = new HashMap<String, JLabel>();

		// set border
		TitledBorder border = BorderFactory.createTitledBorder(title);
		border.setTitleFont(new Font(GuiOptions.defaultFont.getName(),
				Font.BOLD, GuiOptions.defaultFont.getSize()));
		this.setBorder(border);

		// set layout
		this.setLayout(new GridBagLayout());
		this.statsGroupConstraints = new GridBagConstraints();
		this.statsGroupConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.statsGroupConstraints.anchor = GridBagConstraints.WEST;
		this.statsGroupConstraints.weightx = 0.5;
		this.statsGroupConstraints.gridx = 0;
		this.statsGroupConstraints.gridy = 0;
	}

	/** add values to the panel **/
	public void addValue(String name, double value) {
		JLabel tempName = new JLabel(name + ": ");
		tempName.setFont(GuiOptions.defaultFont);

		JLabel tempValue = new JLabel("" + value);
		tempValue.setHorizontalAlignment(JLabel.RIGHT);
		tempValue.setName(name);
		tempValue.setFont(GuiOptions.defaultFont);

		// add labels
		this.statsGroupConstraints.gridx = 0;
		this.statsGroupConstraints.anchor = GridBagConstraints.WEST;
		this.add(tempName, this.statsGroupConstraints);

		this.statsGroupConstraints.gridx = 1;
		this.statsGroupConstraints.anchor = GridBagConstraints.EAST;
		this.add(tempValue, this.statsGroupConstraints);
		this.valueLabels.put(tempValue.getName(), tempValue);

		// increment y position
		this.statsGroupConstraints.gridy++;

		this.validate();
	}

	/** update value already on the panel **/
	public void updateValue(String name, double value) {
		if (this.valueLabels.containsKey(name))
			this.valueLabels.get(name).setText("" + value);
		this.validate();
	}

	/** increment values **/
	public void incrementValue(String name) {
		if (this.valueLabels.containsKey(name)) {
			JLabel valueLabel = this.valueLabels.get(name);
			double tempValue = Double.parseDouble(valueLabel.getText());
			tempValue++;
			valueLabel.setText("" + tempValue);
		}
		this.validate();
	}

	/** decrement values **/
	public void decrementValue(String name) {
		if (this.valueLabels.containsKey(name)) {
			JLabel valueLabel = this.valueLabels.get(name);
			double tempValue = Double.parseDouble(valueLabel.getText());
			tempValue--;
			valueLabel.setText("" + tempValue);
		}
		this.validate();
	}

	/** resets all set values to zero **/
	public void reset() {
		for (String s : this.valueLabels.keySet()) {
			this.valueLabels.get(s).setText("" + 0.0);
		}
		this.validate();
	}

	/** clears the whole list **/
	public void clear() {
		this.statsGroupConstraints.gridy = 0;
		for (Component c : this.getComponents()) {
			this.remove(c);
		}
		this.validate();
	}

}
