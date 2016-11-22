package dna.visualization.components.visualizer;

import java.awt.Color;

/**
 * A legenditem in the legendlist representing a labeler and type pair.
 * 
 * @author Rwilmes
 * 
 */
@SuppressWarnings("serial")
public class LegendItemLabel extends LegendItem {

	// constructor
	public LegendItemLabel(LegendList parent, String name, Color color) {
		super(parent, name, color);

		this.nameLabel.setText(name);
		this.valueLabel.setText("V=0.0");
	}

	/** sets the value of an item **/
	public void setValue(double value) {
		this.valueLabel.setText("V=" + value);
		this.valueLabel.setToolTipText("V=" + value);
	}
}
