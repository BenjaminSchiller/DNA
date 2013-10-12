package dna.visualization.components.legend;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * An item in the legendlist.
 * 
 * @author Rwilmes
 * 
 */
public class LegendItem extends JPanel {

	private LegendList parent;
	private Color color;

	// setPanel containing of color point panel and option panel
	private JPanel setPanel;
	private JPanel colorPointPanel;
	// option panel containing of toggleYAxisButton and plotPoints checkbox
	private JPanel optionPanel;
	private JButton toggleYAxisButton;
	private JCheckBox plotPointsBox;

	// text panel containing the name and value
	private JPanel textPanel;
	private JLabel nameLabel;
	private JLabel valueLabel;
	// button panel containing the remove button
	private JPanel removePanel;
	private JButton removeButton;

	// constructor
	public LegendItem(String name, Color color) {
		super();
		this.setName(name);
		this.color = color;
		this.setPreferredSize(new Dimension(110, 30));
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();


		// init set-, text- and button panels
		this.setPanel = new JPanel();
		this.textPanel = new JPanel();
		this.removePanel = new JPanel();
		
		// config set-, text- and button panels
		this.textPanel.setLayout(new BoxLayout(this.textPanel, BoxLayout.Y_AXIS));
		this.nameLabel = new JLabel(name);
		this.nameLabel.setForeground(color);
		this.textPanel.add(this.nameLabel);
		this.valueLabel = new JLabel("V= " + 0.0);
		this.textPanel.add(this.valueLabel);
		
		// add set-, text- and button panels
		c.gridx = 0;
		c.gridy = 0;
		this.add(this.setPanel, c);
		
		c.gridx = 1;
		c.gridy = 0;
		this.add(this.textPanel, c);
		
		c.gridx = 2;
		c.gridy = 0;
		this.add(this.removePanel, c);
		
		
		this.textPanel.setLayout(new BoxLayout(this.textPanel, BoxLayout.Y_AXIS));

	}

	public void setNameLabel(String name) {
		this.nameLabel.setText(name);
	}

	public void setValue(double value) {
		this.valueLabel.setText("V= " + value);
	}

	public void setColor(Color color) {
		this.nameLabel.setForeground(color);
	}

	public void setParent(LegendList parent) {
		this.parent = parent;
	}
}
