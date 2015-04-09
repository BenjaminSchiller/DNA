package dna.latex;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import dna.latex.TexTable.TableFlag;
import dna.latex.TexTable.TableMode;
import dna.plot.PlottingConfig.PlotFlag;
import dna.util.Config;

/** A TexConfig object is used to configure the tex-output process. **/
public class TexConfig {

	// directories
	private String dstDir;
	private String srcDir;
	private String plotDir;

	// output interval
	private String scaling;
	private long from;
	private long to;
	private long stepsize;
	private boolean intervalByIndex;

	// scaling
	private HashMap<Long, Long> map;

	// date format
	private SimpleDateFormat dateFormat;

	// flags
	private PlotFlag[] plotFlags;
	private TableFlag[] tableFlags;

	// table settings
	private boolean multipleSeriesTables;
	private TableMode tableMode;

	// constructor
	public TexConfig(String dstDir, String srcDir, String plotDir, long from,
			long to, long stepsize, PlotFlag[] plotFlags,
			TableFlag... tableFlags) {
		this.dstDir = dstDir;
		this.srcDir = srcDir;
		this.plotDir = plotDir;
		this.plotFlags = plotFlags;
		this.tableFlags = tableFlags;

		this.multipleSeriesTables = false;
		this.tableMode = TableMode.alternatingValues;

		this.from = from;
		this.to = to;
		this.stepsize = stepsize;
		this.intervalByIndex = false;

		// if default datetime is set in config, set it here
		if (Config.get("LATEX_DEFAULT_DATETIME") != null) {
			String tempDateTime = Config.get("LATEX_DEFAULT_DATETIME");
			if (!tempDateTime.equals("null"))
				this.dateFormat = new SimpleDateFormat(tempDateTime);
		}
	}

	public TexConfig(String dstDir, String srcDir, String plotDir,
			PlotFlag[] plotFlags, TableFlag... tableFlags) {
		this(dstDir, srcDir, plotDir, 0, Long.MAX_VALUE, 1, plotFlags,
				tableFlags);
	}

	// getters & setters
	public void setPlotFlags(PlotFlag... flags) {
		this.plotFlags = flags;
	}

	public void setTableFlags(TableFlag... flags) {
		this.tableFlags = flags;
	}

	public void setOutputInterval(long from, long to, long stepsize) {
		this.from = from;
		this.to = to;
		this.stepsize = stepsize;
		this.intervalByIndex = false;
	}

	public void setOutputIntervalByIndex(long from, long to, long stepsize) {
		this.from = from;
		this.to = to;
		this.stepsize = stepsize;
		this.intervalByIndex = true;
	}

	public String getDstDir() {
		return dstDir;
	}

	public String getSrcDir() {
		return srcDir;
	}

	public String getPlotDir() {
		return plotDir;
	}

	public PlotFlag[] getPlotFlags() {
		return plotFlags;
	}

	public TableFlag[] getTableFlags() {
		return tableFlags;
	}

	public boolean isIncludeRuntimes() {
		for (PlotFlag p : this.plotFlags) {
			switch (p) {
			case plotAll:
				return true;
			case plotRuntimes:
				return true;
			}
		}
		return false;
	}

	public boolean isIncludeStatistics() {
		for (PlotFlag p : this.plotFlags) {
			switch (p) {
			case plotAll:
				return true;
			case plotSingleScalarValues:
				return true;
			case plotStatistics:
				return true;
			}
		}
		return false;
	}

	public boolean isIncludeDistributions() {
		for (PlotFlag p : this.plotFlags) {
			switch (p) {
			case plotAll:
				return true;
			case plotMultiScalarValues:
				return true;
			case plotMetricEntirely:
				return true;
			case plotDistributions:
				return true;
			}
		}
		return false;
	}

	public boolean isIncludeNodeValueLists() {
		for (PlotFlag p : this.plotFlags) {
			switch (p) {
			case plotAll:
				return true;
			case plotMultiScalarValues:
				return true;
			case plotMetricEntirely:
				return true;
			case plotNodeValueLists:
				return true;
			}
		}
		return false;
	}

	public boolean isIncludeMetricValues() {
		for (PlotFlag p : this.plotFlags) {
			switch (p) {
			case plotAll:
				return true;
			case plotSingleScalarValues:
				return true;
			case plotMetricEntirely:
				return true;
			case plotMetricValues:
				return true;
			}
		}
		return false;
	}

	public boolean isIncludeMetrics() {
		for (PlotFlag p : this.plotFlags) {
			switch (p) {
			case plotAll:
				return true;
			case plotSingleScalarValues:
				return true;
			case plotMultiScalarValues:
				return true;
			case plotMetricEntirely:
				return true;
			case plotMetricValues:
				return true;
			case plotDistributions:
				return true;
			case plotNodeValueLists:
				return true;
			}
		}
		return false;
	}

	public void setDateFormat(String pattern) {
		this.dateFormat = new SimpleDateFormat(pattern);
	}

	public void setDateFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public SimpleDateFormat getDateFormat() {
		return this.dateFormat;
	}

	public void setScaling(String scaling) {
		this.scaling = scaling;
	}

	public String getScaling() {
		return this.scaling;
	}

	public void setMapping(HashMap<Long, Long> map) {
		this.map = map;
	}

	public HashMap<Long, Long> getMapping() {
		return this.map;
	}

	public long getFrom() {
		return this.from;
	}

	public long getTo() {
		return this.to;
	}

	public long getStepsize() {
		return this.stepsize;
	}

	public boolean isIntervalByIndex() {
		return this.intervalByIndex;
	}

	/**
	 * When set, tables will incorporate multiple values of multiple series.
	 * 
	 * Example: |min_s1|min_s2|max_s1|max_s2|avg_s1|avg_s2|
	 * **/
	public boolean isMultipleSeriesTables() {
		return this.multipleSeriesTables;
	}

	/**
	 * When set, tables will incorporate multiple values of multiple series.
	 * 
	 * Example: |min_s1|min_s2|max_s1|max_s2|avg_s1|avg_s2|
	 * **/
	public void setMultipleSeriesTables(boolean multipleSeriesTables) {
		this.multipleSeriesTables = multipleSeriesTables;
	}

	public TableMode getTableMode() {
		return this.tableMode;
	}

	/**
	 * Sets the TableMode. Doesnt have any effect if multipleSeriesTables-flag
	 * is not set.
	 **/
	public void setMultipleSeriesTableMode(TableMode tableMode) {
		this.tableMode = tableMode;
	}

}
