package dna.visualization.components.visualizer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;

import dna.visualization.VerticalLayout;
import dna.visualization.config.VisualizerListConfig.SortModeDist;
import dna.visualization.config.VisualizerListConfig.SortModeNVL;

/**
 * The list showing elements added to the legend.
 * 
 * @author Rwilmes
 * 
 */
@SuppressWarnings("serial")
public class LegendList extends JPanel {

	protected Legend parent;

	// constructor
	public LegendList(Legend parent) {
		super();
		this.parent = parent;
		this.setLayout(new VerticalLayout(0));
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

		if (!alreadyAdded) {
			LegendItem i = new LegendItem(this, name, color);
			i.setToolTipText(name);
			this.add(i);
		}
		this.validate();
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
		this.validate();
		this.repaint();
	}

	/** remove an item from the list **/
	public void removeItem(Component c) {
		if (c instanceof LegendItem) {
			remove(c);
			this.parent.removeItem(c.getName(), ((LegendItem) c).getColor());
		}
		this.validate();
		this.repaint();
	}

	/** updates the value of an legend item contained in the list **/
	public void updateItem(String name, double value) {
		for (Component c : this.getComponents()) {
			if (c instanceof LegendItemValue) {
				if (c.getName().equals(name)) {
					((LegendItemValue) c).setValue(value);
				}
			}
		}
		this.validate();
	}

	/** updates the denominator of an legend item contained in the list **/
	public void updateItem(String name, long denominator) {
		for (Component c : this.getComponents()) {
			if (c instanceof LegendItemDistribution) {
				if (c.getName().equals(name)) {
					((LegendItemDistribution) c).setDenominator(denominator);
				}
			}
		}
		this.validate();
	}

	/** resets the legend list by removing all items **/
	public void reset() {
		for (Component c : this.getComponents()) {
			if (c instanceof LegendItem) {
				this.removeItem(c);
			}
		}
	}

	/** toggles y axis of an item **/
	public void toggleYAxis(LegendItem item) {
		this.parent.toggleYAxis(item);
	}

	/** toggles x axis of an item **/
	public void toggleXAxis(LegendItem item) {
		this.parent.toggleXAxis(item);
	}

	/** toggles visiblity of a trace **/
	public void toggleVisiblity(LegendItem item) {
		this.parent.toggleVisiblity(item);
	}

	/** toggles the display mode of a trace **/
	public void toggleDisplayMode(LegendItem item) {
		this.parent.toggleDisplayMode(item);
	}

	/** returns the legenditem identified by its name **/
	public LegendItem getLegendItem(String name) {
		for (Component c : this.getComponents()) {
			if (c instanceof LegendItem) {
				if (c.getName().equals(name))
					return (LegendItem) c;
			}
		}
		return null;
	}

	/** called from an item to get resorted while paused **/
	public void sortItem(LegendItem i, SortModeNVL s) {
		this.parent.sortItem(i, s);
	}

	/** called from an item to get resorted while paused **/
	public void sortItem(LegendItem i, SortModeDist s) {
		this.parent.sortItem(i, s);
	}
}
