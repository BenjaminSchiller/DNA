package dna.plot;

import java.util.ArrayList;

import dna.plot.data.PlotData.DistributionPlotType;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
import dna.plot.data.PlotData.PlotStyle;
import dna.plot.data.PlotData.PlotType;
import dna.util.Config;

/**
 * The PlottingConfig is a config object that controls the plotting behaviour.
 * 
 * @author RWilmes
 * @date 11.05.2014
 */
public class PlottingConfig {
	public static enum PlotFlag {
		plotAll, plotStatistics, plotRuntimes, plotMetricValues, plotDistributions, plotNodeValueLists, plotCustomValues
	};

	// config
	private PlotType plotType;
	private PlotStyle plotStyle;
	private DistributionPlotType distPlotType;
	private NodeValueListOrder nvlOrder;
	private NodeValueListOrderBy nvlOrderBy;

	// combined metric runtimes plot
	private ArrayList<String> generalRuntimes;

	// custom plots
	private ArrayList<PlotConfig> customValuePlots;
	private ArrayList<PlotConfig> customStatisticPlots;
	private ArrayList<PlotConfig> customRuntimePlots;
	private ArrayList<PlotConfig> customMetricValuePlots;
	private ArrayList<PlotConfig> customDistributionPlots;
	private ArrayList<PlotConfig> customNodeValueListPlots;

	// plot flags
	private boolean plotStatistics;
	private boolean plotRuntimes;
	private boolean plotMetricValues;
	private boolean plotDistributions;
	private boolean plotNodeValueLists;
	private boolean plotCustomValues;

	// interval selection
	private long timestampFrom;
	private long timestampTo;
	private long stepsize;

	// constructors
	public PlottingConfig(PlotType plotType, PlotStyle plotStyle,
			long timestampFrom, long timestampTo, long stepsize,
			DistributionPlotType distPlotType, NodeValueListOrder nvlOrder,
			NodeValueListOrderBy nvlOrderBy, PlotFlag... flags) {
		this.plotType = plotType;
		this.plotStyle = plotStyle;
		this.timestampFrom = timestampFrom;
		this.timestampTo = timestampTo;
		this.stepsize = stepsize;
		this.distPlotType = distPlotType;
		this.nvlOrder = nvlOrder;
		this.nvlOrderBy = nvlOrderBy;

		this.generalRuntimes = new ArrayList<String>();
		this.generalRuntimes.add("total");
		this.generalRuntimes.add("overhead");
		this.generalRuntimes.add("metrics");
		this.generalRuntimes.add("sum");
		this.generalRuntimes.add("batchGeneration");
		this.generalRuntimes.add("graphUpdate");

		this.plotStatistics = false;
		this.plotRuntimes = false;
		this.plotMetricValues = false;
		this.plotDistributions = false;
		this.plotNodeValueLists = false;
		this.plotCustomValues = false;

		// check plot flags
		for (PlotFlag flag : flags) {
			switch (flag) {
			case plotAll:
				this.plotStatistics = true;
				this.plotRuntimes = true;
				this.plotMetricValues = true;
				this.plotDistributions = true;
				this.plotNodeValueLists = true;
				this.plotCustomValues = true;
				break;
			case plotStatistics:
				this.plotStatistics = true;
				break;
			case plotRuntimes:
				this.plotRuntimes = true;
				break;
			case plotMetricValues:
				this.plotMetricValues = true;
				break;
			case plotDistributions:
				this.plotDistributions = true;
				break;
			case plotNodeValueLists:
				this.plotNodeValueLists = true;
				break;
			case plotCustomValues:
				this.plotCustomValues = true;
				break;
			}
		}
		// if custom plots are enabled by default, read them
		if (Config.getBoolean("CUSTOM_PLOTS_ENABLED"))
			this.createCustomPlotsFromConfig();
	}

	public PlottingConfig(long timestampFrom, long timestampTo, long stepsize) {
		this(
				Config.getPlotType("GNUPLOT_DEFAULT_PLOTTYPE"),
				Config.getPlotStyle("GNUPLOT_DEFAULT_PLOTSTYLE"),
				timestampFrom,
				timestampTo,
				stepsize,
				Config.getDistributionPlotType("GNUPLOT_DEFAULT_DIST_PLOTTYPE"),
				Config.getNodeValueListOrder("GNUPLOT_DEFAULT_NVL_ORDER"),
				Config.getNodeValueListOrderBy("GNUPLOT_DEFAULT_NVL_ORDERBY"),
				PlotFlag.plotAll);
	}

	public PlottingConfig(PlotFlag... flags) {
		this(
				Config.getPlotType("GNUPLOT_DEFAULT_PLOTTYPE"),
				Config.getPlotStyle("GNUPLOT_DEFAULT_PLOTSTYLE"),
				0,
				Long.MAX_VALUE,
				1,
				Config.getDistributionPlotType("GNUPLOT_DEFAULT_DIST_PLOTTYPE"),
				Config.getNodeValueListOrder("GNUPLOT_DEFAULT_NVL_ORDER"),
				Config.getNodeValueListOrderBy("GNUPLOT_DEFAULT_NVL_ORDERBY"),
				flags);
	}

	// class methods
	/**
	 * Creates custom plots from the config. This method will be called by
	 * default on initialization if "CUSTOM_PLOTS_ENABLED" is set as true in the
	 * plotting.properties.
	 * 
	 * Note: Configs will be created depending on the PlotFlag's that the
	 * PlottingConfig object holds.
	 **/
	public void createCustomPlotsFromConfig() {
		if (this.plotStatistics)
			this.customStatisticPlots = PlotConfig.getCustomStatisticPlots();
		if (this.plotRuntimes)
			this.customRuntimePlots = PlotConfig.getCustomRuntimePlots();
		if (this.plotMetricValues)
			this.customMetricValuePlots = PlotConfig
					.getCustomMetricValuePlots();
		if (this.plotDistributions)
			this.customDistributionPlots = PlotConfig
					.getCustomMetricDistributionPlots();
		if (this.plotNodeValueLists)
			this.customNodeValueListPlots = PlotConfig
					.getCustomMetricNodeValueListPlots();
		if (this.plotCustomValues)
			this.customValuePlots = PlotConfig.getCustomValuePlots();
	}

	// getters and setters
	public PlotType getPlotType() {
		return plotType;
	}

	public void setPlotType(PlotType plotType) {
		this.plotType = plotType;
	}

	public PlotStyle getPlotStyle() {
		return plotStyle;
	}

	public void setPlotStyle(PlotStyle plotStyle) {
		this.plotStyle = plotStyle;
	}

	public DistributionPlotType getDistPlotType() {
		return distPlotType;
	}

	public void setDistPlotType(DistributionPlotType distPlotType) {
		this.distPlotType = distPlotType;
	}

	public NodeValueListOrder getNvlOrder() {
		return nvlOrder;
	}

	public void setNvlOrder(NodeValueListOrder nvlOrder) {
		this.nvlOrder = nvlOrder;
	}

	public NodeValueListOrderBy getNvlOrderBy() {
		return nvlOrderBy;
	}

	public void setNvlOrderBy(NodeValueListOrderBy nvlOrderBy) {
		this.nvlOrderBy = nvlOrderBy;
	}

	public boolean isPlotStatistics() {
		return plotStatistics;
	}

	public void setPlotStatistics(boolean plotStatistics) {
		this.plotStatistics = plotStatistics;
	}

	public boolean isPlotRuntimes() {
		return plotRuntimes;
	}

	public void setPlotRuntimes(boolean plotRuntimes) {
		this.plotRuntimes = plotRuntimes;
	}

	public boolean isPlotMetricValues() {
		return plotMetricValues;
	}

	public void setPlotMetricValues(boolean plotMetricValues) {
		this.plotMetricValues = plotMetricValues;
	}

	public boolean isPlotDistributions() {
		return plotDistributions;
	}

	public void setPlotDistributions(boolean plotDistributions) {
		this.plotDistributions = plotDistributions;
	}

	public boolean isPlotNodeValueLists() {
		return plotNodeValueLists;
	}

	public void setPlotNodeValueLists(boolean plotNodeValueLists) {
		this.plotNodeValueLists = plotNodeValueLists;
	}

	public void setPlotInterval(long timestampFrom, long timestampTo,
			long stepsize) {
		this.timestampFrom = timestampFrom;
		this.timestampTo = timestampTo;
		this.stepsize = stepsize;
	}

	public long getTimestampFrom() {
		return timestampFrom;
	}

	public long getTimestampTo() {
		return timestampTo;
	}

	public long getStepsize() {
		return stepsize;
	}

	public ArrayList<String> getGeneralRuntimes() {
		return this.generalRuntimes;
	}

	public ArrayList<PlotConfig> getCustomStatisticPlots() {
		return customStatisticPlots;
	}

	public ArrayList<PlotConfig> getCustomRuntimePlots() {
		return customRuntimePlots;
	}

	public ArrayList<PlotConfig> getCustomMetricValuePlots() {
		return customMetricValuePlots;
	}

	public ArrayList<PlotConfig> getCustomDistributionPlots() {
		return customDistributionPlots;
	}

	public ArrayList<PlotConfig> getCustomNodeValueListPlots() {
		return customNodeValueListPlots;
	}

	public ArrayList<PlotConfig> getCustomValuePlots() {
		return customValuePlots;
	}

	public void setCustomValuePlots(ArrayList<PlotConfig> customValuePlots) {
		this.customValuePlots = customValuePlots;
	}

	public boolean isPlotCustomValues() {
		return plotCustomValues;
	}

	public void setPlotCustomValues(boolean plotCustomValues) {
		this.plotCustomValues = plotCustomValues;
	}
}