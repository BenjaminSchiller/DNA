package dna.plot.data;

/**
 * @author benni
 * 
 */
public abstract class PlotData {
	public static enum PlotStyle {
		lines, dots, points, linespoint, impulses, steps, boxes, candlesticks, yerrorbars, fillsteps, filledcurves
	}

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

	public static enum PlotDataLocation {
		dataFile, scriptFile
	}

	protected String data;

	protected String domain;

	protected PlotStyle style;

	protected String title;

	protected String source;

	protected boolean plotAsCdf;

	protected PlottingSortMode sortMode;

	protected NodeValueListOrder nodeValueListOrder;

	protected PlotDataLocation dataLocation;

	protected String dataPath;

	public PlotData(String data, String domain, PlotStyle style, String title,
			String source) {
		this.data = data;
		this.domain = domain;
		this.style = style;
		this.title = title;
		this.source = source;

		// default type = data inside scriptfile
		this.dataLocation = PlotDataLocation.scriptFile;
	}

	public abstract boolean isStyleValid();

	public String getName() {
		return this.data;
	}

	public String getDomain() {
		return this.domain;
	}

	public String getTitle() {
		return this.title;
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

	public PlotDataLocation getDataLocation() {
		return this.dataLocation;
	}

	public void setDataLocation(PlotDataLocation dataLocation) {
		this.dataLocation = dataLocation;
	}

	public void setDataLocation(PlotDataLocation dataLocation, String dataPath) {
		this.dataLocation = dataLocation;
		this.dataPath = dataPath;
	}

	public String getDataPath() {
		return this.dataPath;
	}

	public void setDataPath(String dataPath) {
		this.dataPath = dataPath;
	}

	public String getSource() {
		return this.source;
	}

	public abstract String getEntry(int lt, int lw, double offsetX,
			double offsetY, String scalingX, String scalingY);

	public abstract String getEntry(int lt, int lw, double offsetX,
			double offsetY, String scalingX, String scalingY, PlotStyle style);

	public abstract String getEntry(int lt, int lw, double offsetX,
			double offsetY, String scalingX, String scalingY,
			DistributionPlotType distPotType);

	public abstract String getEntry(int lt, int lw, double offsetX,
			double offsetY, String scalingX, String scalingY,
			DistributionPlotType distPotType, PlotStyle style);

	public static PlotData get(String data, String domain, PlotStyle style,
			String title, PlotType type, String source) {
		switch (type) {
		case average:
			return new AverageData(data, domain, style, title, source);
		case median:
			return new MedianData(data, domain, style, title, source);
		case minimum:
			return new MinimumData(data, domain, style, title, source);
		case maximum:
			return new MaximumData(data, domain, style, title, source);
		case variance:
			return new VarianceData(data, domain, style, title, source);
		case confidence1:
			return new ConfidenceData1(data, domain, style, title, source);
		case confidence2:
			return new ConfidenceData2(data, domain, style, title, source);
		case function:
			return new FunctionData(data, domain, style, title, source);
		default:
			return null;
		}
	}

}
