package dna.visualization.components.legend;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

/**
 * The list showing elements added to the legend.
 * 
 * @author Rwilmes
 * 
 */
public class LegendList extends JPanel {

	public LegendList() {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		System.out.println("LegendList created");

	}

	/** add a new item **/
	public void addItem(String name, Color color) {
		boolean alreadyAdded = false;
		for (Component c : this.getComponents()) {
			if (c instanceof LegendItem) {
				if (c.getName().equals(name)) {
					alreadyAdded = true;
				}
			}
		}

		if (!alreadyAdded)
			this.add(new LegendItem(name, color));
	}

	/** set value of an item **/
	public void setItemValue(String name, double value) {
		for (Component c : this.getComponents()) {
			if (c instanceof LegendItem) {
				if (c.getName().equals(name)) {
					((LegendItem) c).setValue(value);
				}
			}
		}
	}

	/** remove an item from the list **/
	public void removeItem(String name) {
		for (Component c : this.getComponents()) {
			if (c instanceof LegendItem) {
				if (c.getName().equals(name)) {
					this.remove(c);
				}
			}
		}
	}
}
