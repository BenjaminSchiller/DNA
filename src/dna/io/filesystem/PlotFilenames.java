package dna.io.filesystem;

import dna.series.Values;
import dna.series.data.Distribution;
import dna.series.data.MetricData;
import dna.series.data.NodeValueList;

public class PlotFilenames {
	public static final String delimiter = ".";

	public static final String generalRuntimes = "general";

	public static final String metricRuntimes = "metrics";

	public static final String metricRuntimesFraction = "metrics.fraction";

	public static String getDistributionDataFile(MetricData m, Distribution d) {
		return Prefix.distributionsDataFile + m.getName() + delimiter
				+ d.getName() + Suffix.data;
	}

	public static String getDistributionGnuplotScript(MetricData m,
			Distribution d) {
		return PlotFilenames.getDistributionGnuplotScript(m.getName(),
				d.getName());
	}

	public static String getDistributionGnuplotScript(String metric,
			String distribution) {
		return Prefix.distributionsGnuplotScript + metric + delimiter
				+ distribution + Suffix.gnuplot;
	}

	public static String getDistributionPlot(MetricData m, Distribution d) {
		return PlotFilenames.getDistributionPlot(m.getName(), d.getName());
	}

	public static String getDistributionPlot(String metric, String distribution) {
		return metric + delimiter + distribution;
	}

	public static String getValuesDataFile(MetricData m, Values v, int index) {
		return PlotFilenames.getValuesDataFile(m.getName(), v.getName(), index);
	}

	public static String getValuesDataFile(String metric, String values,
			int index) {
		return Prefix.valuesDataFile + metric + delimiter + values + delimiter
				+ index + Suffix.data;
	}

	public static String getValuesDataFile(Values v) {
		return Prefix.valuesDataFile + v.getName() + Suffix.data;
	}

	public static String getValuesGnuplotScript(MetricData m, Values v) {
		return PlotFilenames.getValuesGnuplotScript(m.getName(), v.getName());
	}

	public static String getValuesGnuplotScript(String metric, String value) {
		return Prefix.valuesGnuplotScript + metric + delimiter + value
				+ Suffix.gnuplot;
	}

	public static String getValuesGnuplotScript(Values v) {
		return Prefix.valuesGnuplotScript + v.getName() + Suffix.gnuplot;
	}

	public static String getValuesPlot(MetricData m, Values v) {
		return PlotFilenames.getValuesPlot(m.getName(), v.getName());
	}

	public static String getValuesPlot(String metric, String value) {
		return metric + delimiter + value;
	}

	public static String getValuesPlot(Values v) {
		return v.getName();
	}

	public static String getRuntimesDataFile(String name) {
		return Prefix.runtimesDataFile + name + Suffix.data;
	}

	public static String getRuntimesGnuplotScript(Values v) {
		return Prefix.runtimesGnuplotScript + v.getName() + Suffix.gnuplot;
	}

	public static String getRuntimesGnuplotScript(String name) {
		return Prefix.runtimesGnuplotScript + name;
	}

	public static String getRuntimesMetricPlot(String name) {
		return Prefix.runtimesMetricPlot + name;
	}

	public static String getRuntimesStatisticPlot(String name) {
		return Prefix.runtimesStatisticPlot + name;
	}

	public static String getNodeValueListDataFile(MetricData m, NodeValueList n) {
		return Prefix.nodeValueListsDataFile + m.getName() + delimiter
				+ n.getName() + Suffix.data;
	}

	public static String getNodeValueListGnuplotScript(MetricData m,
			NodeValueList n) {
		return PlotFilenames.getNodeValueListGnuplotScript(m.getName(),
				n.getName());
	}

	public static String getNodeValueListGnuplotScript(String metric,
			String nodevaluelist) {
		return Prefix.nodeValueListsGnuplotScript + metric + delimiter
				+ nodevaluelist + Suffix.gnuplot;
	}

	public static String getNodeValueListPlot(MetricData m, NodeValueList n) {
		return PlotFilenames.getNodeValueListPlot(m.getName(), n.getName());
	}

	public static String getNodeValueListPlot(String metric,
			String nodevaluelist) {
		return metric + delimiter + nodevaluelist;
	}
}