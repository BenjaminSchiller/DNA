package dna.plot.labels;

import dna.labels.Label;
import dna.util.Config;

/**
 * Wrapper-class for the labels one can set in gnuplot via set label.<br>
 * <br>
 * 
 * Check the gnuplot documentation for more details.<br>
 * <br>
 * 
 * The getLine()-method will return the line to be added to the gnuplot script.
 * 
 * @author Rwilmes
 * 
 */
public class PlotLabel {

	/** Statics **/
	public static final String LABEL_NAME_PLACEHOLDER = "$label_name$";
	public static final String LABEL_TYPE_PLACEHOLDER = "$label_type$";
	public static final String LABEL_VALUE_PLACEHOLDER = "$label_value$";
	public static final String SERIES_NAME_PLACEHOLDER = "$series_name$";

	public static final String gnuplotDefaultKeyPlotLabelText = "GNUPLOT_DEFAULT_PLOT_LABEL_TEXT";
	public static final String gnuplotFirstPlotLabelText = "GNUPLOT_LABEL_FIRST_TEXT";
	public static final String gnuplotSuccessivePlotLabelText = "GNUPLOT_LABEL_SUCCESSIVE_TEXT";

	public static final double idOffset = Config
			.getDouble("GNUPLOT_LABEL_Y_OFFSET");
	public static final double startPosition = Config
			.getDouble("GNUPLOT_LABEL_Y_OFFSET_START");
	public static final double startPositionBeneath = Config
			.getDouble("GNUPLOT_LABEL_Y_OFFSET_START_BENEATH");
	public static final int lineTypeOffset = Config
			.getInt("GNUPLOT_LABEL_COLOR_OFFSET");

	public enum Orientation {
		left, center, right
	}

	/** Class stuff **/
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
				false, Config.getBoolean("GNUPLOT_LABEL_RENDER_FOREGROUND"),
				null, pointStyle, 0);
	}

	public PlotLabel(int tag, String text, String posX, String posY,
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
	public static PlotLabel generatePlotLabel(double timestamp,
			String seriesName, Label label, int id, boolean beneathGraph) {
		return generatePlotLabel(timestamp, seriesName, label, id, "2",
				beneathGraph);
	}

	/** Crafts a PlotLabel based on the given Label. **/
	public static PlotLabel generatePlotLabel(double timestamp,
			String seriesName, Label label, int id, String pointType,
			boolean beneathGraph) {
		String timestampString;
		if (Config.getBoolean("GNUPLOT_LABEL_BIG_TIMESTAMPS")) {
			timestampString = '"' + "" + timestamp + '"';
		} else {
			timestampString = "" + timestamp;
		}

		String plotLabelText = Config
				.getBoolean("GNUPLOT_LABEL_SHOW_TEXT_ONLY_ONCE") ? ""
				: getSuccessivePlotLabelText(label, seriesName);

		return new PlotLabel(plotLabelText, timestampString, "graph "
				+ calculatePosition(id, beneathGraph), Orientation.right, "lt "
				+ (id + lineTypeOffset) + " pt " + pointType);
	}

	/** Crafts the first PlotLabel based on the given Label. **/
	public static PlotLabel generateFirstPlotLabel(double timestamp,
			String seriesName, Label label, int id, boolean inGraph) {
		return generateFirstPlotLabel(timestamp, seriesName, label, id, "" + 2,
				inGraph);
	}

	/** Crafts the first PlotLabel based on the given Label. **/
	public static PlotLabel generateFirstPlotLabel(double timestamp,
			String seriesName, Label label, int id, String pointType,
			boolean beneathGraph) {
		String timestampString;
		if (Config.getBoolean("GNUPLOT_LABEL_BIG_TIMESTAMPS")) {
			timestampString = '"' + "" + timestamp + '"';
		} else {
			timestampString = "" + timestamp;
		}

		String plotLabelText = Config
				.getBoolean("GNUPLOT_LABEL_SHOW_TEXT_ONLY_ONCE") ? getDefaultPlotLabelText(
				label, seriesName) : getFirstPlotLabelText(label, seriesName);

		return new PlotLabel(plotLabelText, timestampString, "graph "
				+ calculatePosition(id, beneathGraph), Orientation.right, "lt "
				+ (id + lineTypeOffset) + " pt " + pointType);
	}

	/** Calculates the relative position of the label. **/
	public static double calculatePosition(int id, boolean beneathGraph) {
		if (beneathGraph)
			return startPositionBeneath - (id * idOffset);
		else
			return startPosition - (id * idOffset);
	}

	/** Generates the plot-label text. **/
	public static String getDefaultPlotLabelText(Label l, String seriesName) {
		return getPlotLabelText(Config.get(gnuplotDefaultKeyPlotLabelText), l,
				seriesName);
	}

	/** Generates the first plot-label text for multiple texts **/
	public static String getFirstPlotLabelText(Label l, String seriesName) {
		return getPlotLabelText(Config.get(gnuplotFirstPlotLabelText), l,
				seriesName);
	}

	/** Generates the successive plot-label texts. **/
	public static String getSuccessivePlotLabelText(Label l, String seriesName) {
		return getPlotLabelText(Config.get(gnuplotSuccessivePlotLabelText), l,
				seriesName);
	}

	/**
	 * Adds series-name suffix and then replaces the placeholders in the
	 * replaceString with the proper label values.
	 **/
	public static String getPlotLabelText(String replaceString, Label l,
			String seriesName) {
		String temp = (seriesName.equals("")) ? "" : Config
				.get("GNUPLOT_LABEL_SERIES_PREFIX");
		temp += replaceString;
		return temp.replace(LABEL_NAME_PLACEHOLDER, l.getName())
				.replace(LABEL_TYPE_PLACEHOLDER, l.getType())
				.replace(LABEL_VALUE_PLACEHOLDER, l.getValue())
				.replace(SERIES_NAME_PLACEHOLDER, seriesName);
	}
}
