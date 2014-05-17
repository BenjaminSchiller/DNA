package dna.plot.data;

import dna.plot.Gnuplot.PlotStyle;
import dna.util.Config;

/**
 * @author benni
 * 
 */
public abstract class PlotData {
	public static enum PlotType {
		average, median, minimum, maximum, variance, confidence1, confidence2, function
	}

	public static enum NodeValueListOrder {
		ascending, descending
	}

	public static enum PlottingSortMode {
		unsorted, ascending, descending
	}

	public static enum NodeValueListOrderBy {
		index, average, median, minimum, maximum, variance, varianceLow, varianceUp, confidenceLow, confidenceUp
	}

	public static enum DistributionPlotType {
		distOnly, cdfOnly, distANDcdf
	}

	protected String data;

	protected String domain;

	protected PlotStyle style;

	protected String title;

	protected boolean plotAsCdf;

	protected PlottingSortMode sortMode;
	
	protected NodeValueListOrder nodeValueListOrder;

	public PlotData(String data, String domain, PlotStyle style, String title) {
		this.data = data;
		this.domain = domain;
		this.style = style;
		this.title = title;
	}

	public abstract boolean isStyleValid();

	public String getName() {
		return this.data;
	}

	public String getDomain() {
		return this.domain;
	}

	public void setSortMode(PlottingSortMode sortMode) {
		this.sortMode = sortMode;
	}

	public PlottingSortMode getSortMode() {
		return this.sortMode;
	}

	public void setPlotAsCdf(boolean plotAsCdf) {
		this.plotAsCdf = plotAsCdf;
	}

	public boolean isPlotAsCdf() {
		return this.plotAsCdf;
	}

	public abstract String getEntry(int lt, int lw, double offsetX,
			double offsetY);

	public abstract String getEntry(int lt, int lw, double offsetX,
			double offsetY, DistributionPlotType distPotType);

	public static PlotData get(String data, String domain, PlotStyle style,
			String title, PlotType type) {
		switch (type) {
		case average:
			return new AverageData(data, domain, style, title);
		case median:
			return new MedianData(data, domain, style, title);
		case minimum:
			return new MinimumData(data, domain, style, title);
		case maximum:
			return new MaximumData(data, domain, style, title);
		case variance:
			return new VarianceData(data, domain, style, title);
		case confidence1:
			return new ConfidenceData1(data, domain, style, title);
		case confidence2:
			return new ConfidenceData2(data, domain, style, title);
		case function:
			return new FunctionData(data, domain, style, title);
		default:
			return null;
		}
	}
}
