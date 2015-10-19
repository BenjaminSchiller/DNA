package dna.plot.data;

/**
 * @author benni
 * 
 */
public class ConfidenceData1 extends PlotData {

	public ConfidenceData1(String data, String domain, PlotStyle style,
			String title, String source) {
		super(data, domain, style, title, source);
	}

	@Override
	public boolean isStyleValid() {
		return this.style.equals(PlotStyle.candlesticks);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			String scalingX, String scalingY) {
		StringBuffer buff = new StringBuffer();
		// 2 avg
		// 3 min
		// 4 max
		// 5 median
		// 6 variance
		// 7 variance-low
		// 8 variance-up
		// 9 confidence-low
		// 10 confidence-up
		// http://www.gnuplot.info/demo/candlesticks.html
		// whisker plot: x box_min whisker_min whisker_high box_high

		// data point scaling
		String xpoint = "$1 + ";
		String y9 = "$9 + ";
		String y3 = "$3 + ";
		String y4 = "$4 + ";
		String y10 = "$10 + ";
		String y5 = "$5 + ";
		if (!scalingX.equals("null"))
			xpoint = scalingX.replace("x", "$1") + " + ";
		if (!scalingY.equals("null")) {
			y9 = scalingY.replace("y", "$9") + " + ";
			y3 = scalingY.replace("y", "$3") + " + ";
			y4 = scalingY.replace("y", "$4") + " + ";
			y10 = scalingY.replace("y", "$10") + " + ";
			y5 = scalingY.replace("y", "$5") + " + ";
		}

		String x = "(" + xpoint + offsetX + ")";
		String box_min = "(" + y9 + offsetY + ")";
		String whisker_min = "(" + y3 + offsetY + ")";
		String whisker_high = "(" + y4 + offsetY + ")";
		String box_high = "(" + y10 + offsetY + ")";
		String median = "(" + y5 + offsetY + ")";

		// data location
		String dataLoc = null;
		if (super.dataLocation.equals(PlotDataLocation.scriptFile))
			dataLoc = "'-'";
		if (super.dataLocation.equals(PlotDataLocation.dataFile))
			dataLoc = '"' + super.dataPath + '"';

		// build stringbuffer
		buff.append(dataLoc + " using " + x + ":" + box_min + ":" + whisker_min
				+ ":" + whisker_high + ":" + box_high + " with candlesticks");
		buff.append(" lt " + lt + " lw " + lw);
		buff.append(title == null ? " notitle" : " title \"" + this.title
				+ "\"");
		// buff.append(" whiskerbars, \\\n");
		// buff.append("'' using " + x + ":" + median + ":" + median + ":"
		// + median + ":" + median + " with candlesticks");
		// buff.append(" lt " + lt + " lw " + lw + " notitle");

		return buff.toString();
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			String scalingX, String scalingY, DistributionPlotType distPlotType) {
		return this.getEntry(lt, lw, offsetX, offsetY, scalingX, scalingY);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			String scalingX, String scalingY,
			DistributionPlotType distPlotType, PlotStyle style) {
		return this.getEntry(lt, lw, offsetX, offsetY, scalingX, scalingY,
				style);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			String scalingX, String scalingY, PlotStyle style) {
		return this.getEntry(lt, lw, offsetX, offsetY, scalingX, scalingY);
	}
}
