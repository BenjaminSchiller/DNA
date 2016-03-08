package dna.plot.labels;

import dna.util.Config;

/**
 * Wrapper-class for the arrows one can set in gnuplot via set arrow.<br>
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
public class PlotArrow {

	public enum PlotArrowHead {
		nohead, head, backhead, heads
	}

	public enum PlotArrowFill {
		filled, empty, nofilled
	}

	protected int tag;
	protected String posFrom;
	protected String posTo;
	protected boolean relativeTo;
	protected int arrowStyle;
	protected PlotArrowHead head;
	protected double length;
	protected double angle;
	protected double backAngle;
	protected PlotArrowFill fill;
	protected boolean foreground;
	protected String lineStyle;
	protected String lineType;
	protected String lineWidth;

	public PlotArrow(int id, String posFrom, String posTo, boolean relativeTo,
			int arrowStyle, PlotArrowHead head, double length, double angle,
			double backAngle, PlotArrowFill fill, boolean foreground,
			String lineStyle, String lineType, String lineWidth) {
		this.tag = id;
		this.posFrom = posFrom;
		this.posTo = posTo;
		this.relativeTo = relativeTo;
		this.arrowStyle = arrowStyle;
		this.head = head;
		this.length = length;
		this.angle = angle;
		this.backAngle = backAngle;
		this.fill = fill;
		this.foreground = foreground;
		this.lineStyle = lineStyle;
		this.lineType = lineType;
		this.lineWidth = lineWidth;
	}

	/** Returns the gnuplot-script line adding this PlotLabel. **/
	public String getLine() {
		String buff = "set arrow";
		if (tag >= 0)
			buff += " " + tag;

		if (posFrom != null)
			buff += " from " + posFrom;

		if (posTo != null) {
			if (relativeTo)
				buff += " rto ";
			else
				buff += " to ";

			buff += posTo;
		}

		if (head == null) {
			if (arrowStyle > 0)
				buff += " as " + arrowStyle;
		} else {
			buff += " " + head.toString();
		}

		if (length != 0) {
			buff += " " + length + "," + angle;

			if (backAngle != 0) {
				buff += "," + backAngle;
			}
		}

		if (fill != null)
			buff += " " + fill.toString();

		if (foreground)
			buff += " front";

		if (lineStyle != null)
			buff += " ls " + lineStyle;

		if (lineType != null)
			buff += " lt " + lineType;

		if (lineWidth != null)
			buff += " lw " + lineWidth;

		return buff;
	}

	/** Returns the arrow command with the given parameters. **/
	public static PlotArrow getPlotArrowInterval(int id, int arrowStyleId,
			double from, double to) {
		// calculates the y-position of the arrow
		double position = PlotLabel.calculatePosition(id);

		// craft position strings
		String posFrom;
		String posTo;

		if (Config.getBoolean("GNUPLOT_LABEL_BIG_TIMESTAMPS")) {
			posFrom = '"' + "" + from + '"' + ",graph " + position;
			posTo = '"' + "" + to + '"' + ",graph " + position;
		} else {
			posFrom = from + ",graph " + position;
			posTo = to + ",graph " + position;
		}

		// return
		return new PlotArrow(-1, posFrom, posTo, false, arrowStyleId, null,
				0.0, 0.0, 0.0, null,
				Config.getBoolean("GNUPLOT_LABEL_RENDER_FOREGROUND"), ""
						+ (id + PlotLabel.lineTypeOffset), null, null);
	}

	/** Returns the interval-arrow-style command with the given id. **/
	public static String getIntervalArrowStyle(int arrowStyleId) {
		return "set style arrow " + arrowStyleId
				+ " heads size screen 0.008,90";
	}
}
