package dna.plot;

import dna.series.Values;
import dna.series.data.Distribution;
import dna.series.data.MetricData;
import dna.settings.Prefix;
import dna.settings.Suffix;

public class PlotFilenames {
	public static final String delimiter = ".";

	public static String getDistributionDataFile(MetricData m, Distribution d) {
		return Prefix.distributionsDataFile + m.getName() + delimiter
				+ d.getName() + Suffix.data;
	}

	public static String getDistributionGnuplotScript(MetricData m,
			Distribution d) {
		return Prefix.distributionsGnuplotScript + m.getName() + delimiter
				+ d.getName() + Suffix.gnuplot;
	}

	public static String getDistributionPlot(MetricData m, Distribution d) {
		return m.getName() + delimiter + d.getName();
	}

	public static String getValuesDataFile(MetricData m, Values v) {
		return Prefix.valuesDataFile + m.getName() + delimiter + v.getName()
				+ Suffix.data;
	}

	public static String getValuesGnuplotScript(MetricData m, Values v) {
		return Prefix.valuesGnuplotScript + m.getName() + delimiter
				+ v.getName() + Suffix.gnuplot;
	}

	public static String getValuesPlot(MetricData m, Values v) {
		return m.getName() + delimiter + v.getName();
	}

	public static String getRuntimesDataFile(Values v) {
		return Prefix.runtimesDataFile + v.getName() + Suffix.data;
	}

	public static String getRuntimesGnuplotScript(Values v) {
		return Prefix.runtimesGnuplotScript + v.getName() + Suffix.gnuplot;
	}

	public static String getRuntimesGnuplotScript(String name) {
		return Prefix.runtimesGnuplotScript + name;
	}

	public static String getRuntimesPlot(Values v) {
		return Prefix.runtimesPlot + v.getName();
	}

	public static String getRuntimesPlot(String name) {
		return Prefix.runtimesPlot + name;
	}
}