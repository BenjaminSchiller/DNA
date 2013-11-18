package dna.visualization.components.visualizer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.JButton;

/**
 * A legenditem in the legendlist representing a value.
 * 
 * @author Rwilmes
 * 
 */
public class LegendItemValue extends LegendItem {


	
	public LegendItemValue(LegendList parent, String name, Color color) {
		super(parent, name, color);
		
		this.valueLabel.setText("V=0.0");
	}

	/** sets the value of an item **/
	public void setValue(double value) {
		this.valueLabel.setText("V=" + value);
		this.valueLabel.setToolTipText("V=" + value);
	}
}
