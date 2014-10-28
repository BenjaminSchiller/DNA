package dna.visualization.components.statsdisplay;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

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
	private StatsDisplay statsDisplay;
	private JScrollPane scrollPane;
	private JPanel panel;

	// sizes
	private static final Dimension scrollPaneMinSize = new Dimension(0, 0);
	private static final Dimension scrollPanePrefSize = new Dimension(285, 100);
	private static final Dimension panelMinSize = new Dimension(50, 50);
	private static final int sizingFirstStepSize = 19;
	private static final int sizingStepSize = 17;
	private static final int sizingMaxThreshold = 100;
	private boolean firstStep = false;

	// constructor
	public StatsGroup(StatsDisplay statsDisplay, String title) {
		// set name
		this.setName(title);
		this.statsDisplay = statsDisplay;

		// init hashmap
		this.valueLabels = new HashMap<String, JLabel>();

		// set border
		TitledBorder border = BorderFactory.createTitledBorder(title);
		border.setTitleFont(new Font(this.statsDisplay.getDefaultFont()
				.getName(), Font.BOLD, this.statsDisplay.getDefaultFont()
				.getSize()));
		border.setTitleColor(this.statsDisplay.getDefaultFontColor());
		this.setBorder(border);

		// set layout
		this.setLayout(new GridBagLayout());
		this.statsGroupConstraints = new GridBagConstraints();
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;

		this.statsGroupConstraints.fill = GridBagConstraints.HORIZONTAL;
		this.statsGroupConstraints.anchor = GridBagConstraints.WEST;
		this.statsGroupConstraints.weightx = 0.5;
		this.statsGroupConstraints.gridx = 0;
		this.statsGroupConstraints.gridy = 0;
		this.panel = new JPanel(new GridBagLayout());
		this.panel.setMinimumSize(StatsGroup.panelMinSize);
		this.scrollPane = new JScrollPane(this.panel);
		this.scrollPane.setMinimumSize(StatsGroup.scrollPaneMinSize);
		this.scrollPane.setPreferredSize(StatsGroup.scrollPanePrefSize);
		this.scrollPane.setAlignmentX(LEFT_ALIGNMENT);
		this.add(this.scrollPane, c);
	}

	/** add values to the panel **/
	public void addValue(String name, double value) {
		JLabel tempName = new JLabel(name + ": ");
		tempName.setFont(this.statsDisplay.getDefaultFont());
		tempName.setForeground(this.statsDisplay.getDefaultFontColor());

		JLabel tempValue = new JLabel("" + value);
		tempValue.setHorizontalAlignment(JLabel.RIGHT);
		tempValue.setName(name);
		tempValue.setFont(this.statsDisplay.getDefaultFont());
		tempValue.setForeground(this.statsDisplay.getDefaultFontColor());

		// add labels
		this.statsGroupConstraints.gridx = 0;
		this.statsGroupConstraints.anchor = GridBagConstraints.WEST;
		this.panel.add(tempName, this.statsGroupConstraints);

		this.statsGroupConstraints.gridx = 1;
		this.statsGroupConstraints.anchor = GridBagConstraints.EAST;
		this.panel.add(tempValue, this.statsGroupConstraints);
		this.valueLabels.put(tempValue.getName(), tempValue);

		// increment y position
		this.statsGroupConstraints.gridy++;

		// increase scrollpane size
		int height = this.scrollPane.getMinimumSize().height;
		if (height < StatsGroup.sizingMaxThreshold) {
			if (!this.firstStep) {
				this.firstStep = true;
				height += StatsGroup.sizingFirstStepSize;
			} else {
				height += StatsGroup.sizingStepSize;
			}
		}
		Dimension dim = new Dimension(this.scrollPane.getMinimumSize().width,
				height);
		this.scrollPane.setMinimumSize(dim);

		// validate
		this.scrollPane.validate();
		this.revalidate();
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
		this.scrollPane.setMinimumSize(StatsGroup.scrollPaneMinSize);
		this.firstStep = false;
		for (String s : this.valueLabels.keySet()) {
			this.valueLabels.get(s).setText("" + 0.0);
		}
		this.validate();
	}

	/** clears the whole list **/
	public void clear() {
		this.statsGroupConstraints.gridy = 0;
		for (Component c : this.panel.getComponents()) {
			this.panel.remove(c);
		}
		this.validate();
	}

}
