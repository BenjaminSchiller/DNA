package dna.plot;

import dna.labels.Label;

/**
 * Wrapper-class for the labels one can set in gnuplot via set label.<br>
 * <br>
 * 
 * The getLine()-method will return the line to be added to the gnuplot script.
 * 
 * @author Rwilmes
 * 
 */
public class PlotLabel {

	public enum Orientation {
		left, center, middle
	}

	private int tag;
	private String text;

	private String posX;
	private String posY;
	private double offset;

	private Orientation orientation;

	private boolean rotate;
	private double rotationDegree;

	private String fontName;
	private double fontSize;

	private boolean noenhanced;
	private boolean foreground;

	private String colorSpec;
	private String pointStyle;

	public PlotLabel(String text, String posX) {
		this(text, posX, null);
	}

	public PlotLabel(String text, String posX, String posY,
			Orientation orientation, String pointStyle) {
		this(text, posX, posY, orientation, false, 0, 0, pointStyle);
	}

	public PlotLabel(String text, String posX, String posY) {
		this(text, posX, posY, null, false, 0, 0, null);
	}

	public PlotLabel(String text, String posX, String posY,
			Orientation orientation, boolean rotate, double rotation,
			double offset, String pointStyle) {
		this(-1, text, posX, posY, orientation, rotate, rotation, null, 0,
				false, false, null, pointStyle, 0);
	}

	public PlotLabel(Integer tag, String text, String posX, String posY,
			Orientation orientation, boolean rotate, double rotationDegree,
			String fontName, double fontSize, boolean noenhanced,
			boolean foreground, String textColor, String pointStyle,
			double offset) {
		this.tag = tag;
		this.text = text;
		this.posX = posX;
		this.posY = posY;
		this.orientation = orientation;
		this.rotationDegree = rotationDegree;
		this.offset = offset;

		this.fontName = fontName;
		this.fontSize = fontSize;

		this.noenhanced = noenhanced;
		this.foreground = foreground;

		this.colorSpec = textColor;
		this.pointStyle = pointStyle;
	}

	/** Returns the gnuplot-script line adding this PlotLabel. **/
	public String getLine() {
		String buff = "set label";
		if (tag >= 0)
			buff += " " + tag;

		buff += " " + '"' + text + '"';

		if (posX != null) {
			buff += " " + "at" + " " + posX;
			if (posY != null)
				buff += "," + posY;
		} else {
			if (posY != null)
				buff += " " + "0," + posY;
		}

		if (orientation != null)
			buff += " " + orientation.toString();

		if (rotate)
			buff += " " + "rotate by" + " " + rotationDegree;

		if (fontName != null) {
			buff += " " + fontName;
			if (fontSize > 0)
				buff += "," + fontSize;
		}

		if (noenhanced)
			buff += " " + "noenhanced";

		if (foreground)
			buff += " " + "front";

		if (colorSpec != null)
			buff += " " + "textcolor" + " " + colorSpec;

		if (pointStyle != null)
			buff += " " + "point" + " " + pointStyle;

		if (offset > 0)
			buff += " " + "offset" + " " + offset;
		return buff;
	}

	/** Crafts a PlotLabel based on the given Label. **/
	public static PlotLabel generatePlotLabel(double timestamp, Label label) {
		return new PlotLabel(timestamp + ":" + label.getType() + "="
				+ label.getValue(), "" + timestamp, "graph 0.95",
				Orientation.left, "pt 1");
	}
}
