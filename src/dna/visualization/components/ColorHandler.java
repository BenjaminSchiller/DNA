package dna.visualization.components;

import java.awt.Color;

/**
 * Used to get lots of different colors for the chart and legend. A new color
 * can be obtained by calling ColorHandler.getNextColor(). Make sure to call
 * ColorHandler.removeColor(..) whenever you get a remove an object with a
 * certain color.
 * 
 * @author Rwilmes
 */
public class ColorHandler {

	private Color[] colors = new Color[] { new Color(255, 0, 0),
			new Color(0, 255, 0), new Color(0, 0, 255), new Color(100, 100, 0),
			new Color(100, 0, 100), new Color(0, 100, 100), new Color(0, 0, 0),
			new Color(200, 200, 0), new Color(200, 0, 200),
			new Color(0, 200, 200), new Color(150, 150, 0),
			new Color(150, 0, 150), new Color(0, 150, 150) };

	private int[] usedIndexes = new int[this.colors.length];

	// constructor
	public ColorHandler() {
		super();
	}

	/** returns the next less used color **/
	public Color getNextColor() {
		int min = this.usedIndexes[0];
		int minIndex = 0;
		for (int i = 0; i < this.usedIndexes.length; i++) {
			if (this.usedIndexes[i] < min) {
				minIndex = i;
				min = this.usedIndexes[i];
			}
		}

		this.usedIndexes[minIndex]++;
		return this.colors[minIndex];
	}

	/** decrements the usedIndex of the color c **/
	public void removeColor(Color c) {
		for (int i = 0; i < this.colors.length; i++) {
			if (this.colors[i].equals(c))
				this.usedIndexes[i]--;
		}
	}
}
