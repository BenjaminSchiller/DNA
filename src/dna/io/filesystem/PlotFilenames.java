package dna.io.filesystem;

import dna.series.Values;
import dna.series.data.Distribution;
import dna.series.data.MetricData;
import dna.series.data.NodeValueList;
import dna.util.Config;

public class PlotFilenames {

	public static String getDistributionDataFile(MetricData m, Distribution d) {
		return Config.get("PREFIX_DIST_DATA_FILE") + m.getName()
				+ Config.get("PLOT_DELIMITER") + d.getName()
				+ Config.get("SUFFIX_DATA");
	}

	public static String getDistributionGnuplotScript(MetricData m,
			Distribution d) {
		return PlotFilenames.getDistributionGnuplotScript(m.getName(),
				d.getName());
	}

	public static String getDistributionGnuplotScript(String metric,
			String distribution) {
		return Config.get("PREFIX_GNUPLOT_SCRIPT") + metric
				+ Config.get("PLOT_DELIMITER") + distribution
				+ Config.get("SUFFIX_GNUPLOT");
	}

	public static String getDistributionGnuplotScript(String distribution) {
		return Config.get("PREFIX_GNUPLOT_SCRIPT") + distribution
				+ Config.get("SUFFIX_GNUPLOT");
	}

	public static String getDistributionCdfGnuplotScript(String metric,
			String distribution) {
		return Config.get("PREFIX_GNUPLOT_SCRIPT") + metric
				+ Config.get("PLOT_DELIMITER") + distribution
				+ Config.get("PLOT_DELIMITER")
				+ Config.get("PLOT_DISTRIBUTION_CDF")
				+ Config.get("SUFFIX_GNUPLOT");
	}

	public static String getDistributionCdfGnuplotScript(String distribution) {
		return Config.get("PREFIX_GNUPLOT_SCRIPT") + distribution
				+ Config.get("PLOT_DELIMITER")
				+ Config.get("PLOT_DISTRIBUTION_CDF")
				+ Config.get("SUFFIX_GNUPLOT");
	}

	public static String getDistributionPlot(MetricData m, Distribution d) {
		return PlotFilenames.getDistributionPlot(m.getName(), d.getName());
	}

	public static String getDistributionPlot(String metric, String distribution) {
		return metric + Config.get("PLOT_DELIMITER") + distribution;
	}

	public static String getDistributionPlot(String distribution) {
		return distribution;
	}

	public static String getDistributionCdfPlot(String metric,
			String distribution) {
		return metric + Config.get("PLOT_DELIMITER") + distribution
				+ Config.get("PLOT_DELIMITER")
				+ Config.get("PLOT_DISTRIBUTION_CDF");
	}

	public static String getDistributionCdfPlot(String distribution) {
		return distribution + Config.get("PLOT_DELIMITER")
				+ Config.get("PLOT_DISTRIBUTION_CDF");
	}

	public static String getValuesDataFile(MetricData m, Values v, int index) {
		return PlotFilenames.getValuesDataFile(m.getName(), v.getName(), index);
	}

	public static String getValuesDataFile(String metric, String values,
			int index) {
		return Config.get("PREFIX_VALUE_DATA_FILE") + metric
				+ Config.get("PLOT_DELIMITER") + values
				+ Config.get("PLOT_DELIMITER") + index
				+ Config.get("SUFFIX_DATA");
	}

	public static String getValuesDataFile(Values v) {
		return Config.get("PREFIX_VALUE_DATA_FILE") + v.getName()
				+ Config.get("SUFFIX_DATA");
	}

	public static String getValuesGnuplotScript(MetricData m, Values v) {
		return PlotFilenames.getValuesGnuplotScript(m.getName(), v.getName());
	}

	public static String getValuesGnuplotScript(String metric, String value) {
		return Config.get("PREFIX_GNUPLOT_SCRIPT") + metric
				+ Config.get("PLOT_DELIMITER") + value
				+ Config.get("SUFFIX_GNUPLOT");
	}

	public static String getValuesPlotCDF(String value) {
		return value + Config.get("PLOT_DELIMITER")
				+ Config.get("PLOT_DISTRIBUTION_CDF");
	}

	public static String getValuesGnuplotScriptCDF(String value) {
		return Config.get("PREFIX_GNUPLOT_SCRIPT") + value
				+ Config.get("PLOT_DELIMITER")
				+ Config.get("PLOT_DISTRIBUTION_CDF")
				+ Config.get("SUFFIX_GNUPLOT");
	}

	public static String getValuesGnuplotScript(String value) {
		return Config.get("PREFIX_GNUPLOT_SCRIPT") + value
				+ Config.get("SUFFIX_GNUPLOT");
	}

	public static String getValuesGnuplotScript(Values v) {
		return Config.get("PREFIX_GNUPLOT_SCRIPT") + v.getName()
				+ Config.get("SUFFIX_GNUPLOT");
	}

	public static String getValuesPlot(MetricData m, Values v) {
		return PlotFilenames.getValuesPlot(m.getName(), v.getName());
	}

	public static String getValuesPlot(String metric, String value) {
		return metric + Config.get("PLOT_DELIMITER") + value;
	}

	public static String getValuesPlot(String value) {
		return value;
	}

	public static String getValuesPlot(Values v) {
		return v.getName();
	}

	public static String getRuntimesDataFile(String name) {
		return Config.get("PREFIX_RUNTIMES_DATA_FILE") + name
				+ Config.get("SUFFIX_DATA");
	}

	public static String getRuntimesMultiSeriesGnuplotFile(String name) {
		return Config.get("PREFIX_RUNTIME_GNUPLOT_FILE") + name;
	}

	public static String getRuntimesMultiSeriesGnuplotScript(String name) {
		return Config.get("PREFIX_GNUPLOT_SCRIPT")
				+ PlotFilenames.getRuntimesMultiSeriesGnuplotFile(name);
	}

	public static String getRuntimesPlotFile(String name) {
		return name;
	}

	public static String getRuntimesPlotFileCDF(String name) {
		return name + Config.get("PLOT_DELIMITER")
				+ Config.get("PLOT_DISTRIBUTION_CDF");
	}

	public static String getRuntimesGnuplotScript(Values v) {
		return Config.get("PREFIX_GNUPLOT_SCRIPT") + v.getName()
				+ Config.get("SUFFIX_GNUPLOT");
	}

	public static String getRuntimesGnuplotScript(String name) {
		return Config.get("PREFIX_GNUPLOT_SCRIPT") + name
				+ Config.get("SUFFIX_GNUPLOT");
	}

	public static String getRuntimesGnuplotScriptCDF(String name) {
		return Config.get("PREFIX_GNUPLOT_SCRIPT") + name
				+ Config.get("PLOT_DELIMITER")
				+ Config.get("PLOT_DISTRIBUTION_CDF")
				+ Config.get("SUFFIX_GNUPLOT");
	}

	public static String getRuntimesMetricPlot(String name) {
		return name;
	}

	public static String getRuntimesMetricPlotCDF(String name) {
		return name + Config.get("PLOT_DELIMITER")
				+ Config.get("PLOT_DISTRIBUTION_CDF");
	}

	public static String getRuntimesStatisticPlot(String name) {
		return name;
	}

	public static String getRuntimesStatisticPlotCDF(String name) {
		return name + Config.get("PLOT_DELIMITER")
				+ Config.get("PLOT_DISTRIBUTION_CDF");
	}

	public static String getNodeValueListDataFile(MetricData m, NodeValueList n) {
		return Config.get("PREFIX_NVL_DATA_FILE") + m.getName()
				+ Config.get("PLOT_DELIMITER") + n.getName()
				+ Config.get("SUFFIX_DATA");
	}

	public static String getNodeValueListDataFile(String metric,
			String nodevaluelist, int index) {
		return Config.get("PREFIX_NVL_DATA_FILE") + metric
				+ Config.get("PLOT_DELIMITER") + nodevaluelist
				+ Config.get("PLOT_DELIMITER") + index
				+ Config.get("SUFFIX_DATA");
	}

	public static String getNodeValueListGnuplotScript(MetricData m,
			NodeValueList n) {
		return PlotFilenames.getNodeValueListGnuplotScript(m.getName(),
				n.getName());
	}

	public static String getNodeValueListGnuplotScript(String metric,
			String nodevaluelist) {
		return Config.get("PREFIX_GNUPLOT_SCRIPT") + metric
				+ Config.get("PLOT_DELIMITER") + nodevaluelist
				+ Config.get("SUFFIX_GNUPLOT");
	}

	public static String getNodeValueListGnuplotScript(String nodevaluelist) {
		return Config.get("PREFIX_GNUPLOT_SCRIPT") + nodevaluelist
				+ Config.get("SUFFIX_GNUPLOT");
	}

	public static String getNodeValueListPlot(MetricData m, NodeValueList n) {
		return PlotFilenames.getNodeValueListPlot(m.getName(), n.getName());
	}

	public static String getNodeValueListPlot(String metric,
			String nodevaluelist) {
		return metric + Config.get("PLOT_DELIMITER") + nodevaluelist;
	}

	public static String getNodeValueListPlot(String nodevaluelist) {
		return nodevaluelist;
	}

	public static String getCombinationPlot(String value) {
		return Config.get("PREFIX_COMBINATION_METRIC_PLOT") + value;
	}

	public static String getCombinationGnuplotScript(String value) {
		return Config.get("PREFIX_COMBINATION_METRIC_GNUPLOT_SCRIPT") + value
				+ Config.get("SUFFIX_GNUPLOT");
	}
}