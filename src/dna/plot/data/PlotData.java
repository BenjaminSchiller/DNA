package dna.plot.data;

import dna.plot.Gnuplot.PlotStyle;


/**
 * @author benni
 * 
 */
public abstract class PlotData {
	public static enum PlotType {
		average, median, minimum, maximum, variance, confidence, function
	}

	protected String data;

	protected PlotStyle style;

	protected String title;

	public PlotData(String data, PlotStyle style, String title) {
		this.data = data;
		this.style = style;
		this.title = title;
	}

	public abstract boolean isStyleValid();

	public abstract String getEntry(int lt, int lw, double offsetX,
			double offsetY);

	public static PlotData get(String data, PlotStyle style, String title, PlotType type) {
		switch (type) {
		case average:
			return new AverageData(data, style, title);
		case median:
			return new MedianData(data, style, title);
		case minimum:
			return new MinimumData(data, style, title);
		case maximum:
			return new MaximumData(data, style, title);
		case variance:
			return new VarianceData(data, style, title);
		case confidence:
			return new ConfidenceData(data, style, title);
		case function:
			return new FunctionData(data, style, title);
		default:
			return null;
		}
	}
}
