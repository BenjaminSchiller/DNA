package dna.plot;

import dna.plot.Gnuplot.PlotStyle;
import dna.plot.data.PlotData.DistributionPlotType;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
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
		plotAll, plotStatistics, plotRuntimes, plotMetricValues, plotDistributions, plotNodeValueLists
	};

	// config
	private PlotType plotType;
	private PlotStyle plotStyle;
	private DistributionPlotType distPlotType;
	private NodeValueListOrder nvlOrder;
	private NodeValueListOrderBy nvlOrderBy;

	// plot flags
	private boolean plotStatistics;
	private boolean plotRuntimes;
	private boolean plotMetricValues;
	private boolean plotDistributions;
	private boolean plotNodeValueLists;

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

		this.plotStatistics = false;
		this.plotRuntimes = false;
		this.plotMetricValues = false;
		this.plotDistributions = false;
		this.plotNodeValueLists = false;

		// check plot flags
		for (PlotFlag flag : flags) {
			switch (flag) {
			case plotAll:
				this.plotStatistics = true;
				this.plotRuntimes = true;
				this.plotMetricValues = true;
				this.plotDistributions = true;
				this.plotNodeValueLists = true;
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
			}
		}
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

}
