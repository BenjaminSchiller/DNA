package dna.plot.data;

/**
 * @author benni
 * 
 */
public class ConfidenceData1 extends PlotData {

	public ConfidenceData1(String data, String domain, PlotStyle style,
			String title) {
		super(data, domain, style, title);
	}

	@Override
	public boolean isStyleValid() {
		return this.style.equals(PlotStyle.candlesticks);
	}

	@Override
	public String getEntry(int lt, int lw, double offsetX, double offsetY) {
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
		String x = "($1 + " + offsetX + ")";
		String box_min = "($9 + " + offsetY + ")";
		String whisker_min = "($3 + " + offsetY + ")";
		String whisker_high = "($4 + " + offsetY + ")";
		String box_high = "($10 + " + offsetY + ")";
		String median = "($5 + " + offsetY + ")";

		buff.append("'-' using " + x + ":" + box_min + ":" + whisker_min + ":"
				+ whisker_high + ":" + box_high + " with candlesticks");
		buff.append(" lt " + lt + " lw " + lw);
		buff.append(title == null ? " notitle" : " title \"" + this.title
				+ "\"");
		buff.append(" whiskerbars, \\\n");
		buff.append("'' using " + x + ":" + median + ":" + median + ":"
				+ median + ":" + median + " with candlesticks");
		buff.append(" lt " + lt + " lw " + lw + " notitle");
		return buff.toString();
	}

	public String getEntry(int lt, int lw, double offsetX, double offsetY,
			DistributionPlotType distPlotType) {
		return this.getEntry(lt, lw, offsetX, offsetY);
	}
}
