package dna.plot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import dna.io.filesystem.Dir;
import dna.io.filesystem.PlotFilenames;
import dna.io.filesystem.Prefix;
import dna.io.filesystem.Suffix;
import dna.plot.Gnuplot.PlotStyle;
import dna.plot.data.PlotData;
import dna.plot.data.PlotData.PlotType;
import dna.series.SeriesStats;
import dna.series.Values;
import dna.series.data.BatchData;
import dna.series.data.Distribution;
import dna.series.data.MetricData;
import dna.series.data.RunData;
import dna.series.data.RunTime;
import dna.series.data.SeriesData;
import dna.series.data.Value;
import dna.util.Log;

public class Plotting {

	public static void plot(SeriesData seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir);
	}

	public static void plot(SeriesData seriesData, String dstDir,
			PlotType type, PlotStyle style) throws IOException,
			InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir, type, style);
	}

	public static void plot(SeriesData[] seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(seriesData, dstDir, PlotType.average,
				PlotStyle.linespoint);
	}

	public static void plot(SeriesData[] seriesData, String dstDir,
			PlotType type, PlotStyle style) throws IOException,
			InterruptedException {
		Log.infoSep();
		Log.info("plotting all data for " + seriesData.length + " series");
		(new File(dstDir)).mkdirs();
		Plotting.plotDistributions(seriesData, dstDir, type, style);
		Plotting.plotValues(seriesData, dstDir, type, style);
		Plotting.plotStatistics(seriesData, dstDir, type, style);
		Plotting.plotRuntimes(seriesData, dstDir, type, style);
		// TODO plot nodevaluelists
		Log.infoSep();
	}

	private static void plotDistributions(SeriesData[] seriesData,
			String dstDir, PlotType type, PlotStyle style) throws IOException,
			InterruptedException {
		for (MetricData m : seriesData[0].getRun(0).getBatches().get(0)
				.getMetrics().getList()) {
			for (Distribution d : m.getDistributions().getList()) {
				Plotting.plotDistributon(seriesData, dstDir, type, style,
						m.getName(), d.getName());
			}
		}
	}

	private static void plotValues(SeriesData[] seriesData, String dstDir,
			PlotType type, PlotStyle style) throws IOException,
			InterruptedException {
		for (MetricData metric : seriesData[0].getRun(0).getBatches().get(0)
				.getMetrics().getList()) {
			for (Value value : metric.getValues().getList()) {
				Plotting.plotValue(seriesData, dstDir, type, style,
						metric.getName(), value.getName());
			}
		}
	}

	private static void plotStatistics(SeriesData[] seriesData, String dstDir,
			PlotType type, PlotStyle style) throws IOException,
			InterruptedException {
		for (String value : SeriesStats.statisticsToPlot) {
			Plotting.plotValue(seriesData, dstDir, type, style, null, value);
		}
	}

	private static void plotRuntimes(SeriesData[] seriesData, String dstDir,
			PlotType type, PlotStyle style) throws IOException,
			InterruptedException {

		for (SeriesData s : seriesData) {
			if (s.getAggregation().getBatches().size() < 2) {
				return;
			}
		}

		SeriesData s1 = seriesData[0];

		int gr = SeriesStats.generalRuntimesPlot.length * seriesData.length;
		int mr = 0;
		for (SeriesData s : seriesData) {
			mr += s.getAggregation().getBatches().get(0).getMetricRuntimes()
					.size();
		}

		Values[] general = new Values[gr];
		Values[] metrics = new Values[mr];
		Values[] metricsFraction = new Values[mr];

		int index1 = 0;
		int index2 = 0;
		for (SeriesData s : seriesData) {
			for (String runtime : SeriesStats.generalRuntimesPlot) {
				general[index1++] = getGeneralRuntimes(s.getAggregation(),
						runtime, runtime + "-" + s.getName());
			}
			for (String metric : s.getAggregation().getBatches().get(0)
					.getMetricRuntimes().getNames()) {
				metrics[index2] = getMetricRuntimes(s.getAggregation(), metric,
						metric + "-" + s.getName());
				metricsFraction[index2] = getMetricRuntimes(s.getAggregation(),
						metric, metric + "-fraction-" + s.getName());
				index2++;
			}
		}

		int index = 0;
		for (SeriesData s : seriesData) {
			double[] sum = new double[s.getAggregation().getBatches().size() - 1];
			int metricCount = s.getAggregation().getBatches().get(0)
					.getMetricRuntimes().getNames().size();
			for (int i = 0; i < metricCount; i++) {
				for (int j = 0; j < sum.length; j++) {
					sum[j] += metrics[index + i].getValues()[j][1];
				}
			}
			for (int i = 0; i < metricCount; i++) {
				for (int j = 0; j < sum.length; j++) {
					metricsFraction[index + i].getValues()[j][1] /= sum[j];
				}
			}

			index += metricCount;
		}

		Plotting.plot(general, dstDir, PlotFilenames
				.getRuntimesPlot(PlotFilenames.generalRuntimes), PlotFilenames
				.getRuntimesGnuplotScript(PlotFilenames
						.getRuntimesPlot(PlotFilenames.generalRuntimes)));
		Plotting.plot(metrics, dstDir, PlotFilenames
				.getRuntimesPlot(PlotFilenames.metricRuntimes), PlotFilenames
				.getRuntimesGnuplotScript(PlotFilenames
						.getRuntimesPlot(PlotFilenames.metricRuntimes)));
		Plotting.plot(metricsFraction, dstDir, PlotFilenames
				.getRuntimesPlot(PlotFilenames.metricRuntimesFraction),
				PlotFilenames.getRuntimesGnuplotScript(PlotFilenames
						.getRuntimesPlot(PlotFilenames.metricRuntimesFraction)));
	}

	private static void plotDistributon(SeriesData[] seriesData, String dstDir,
			PlotType type, PlotStyle style, String metric, String distribution)
			throws IOException, InterruptedException {
		Log.info("distribution " + distribution + " of " + metric);
		int batches = 0;
		for (SeriesData s : seriesData) {
			batches += s.getAggregation().getBatches().size();
		}

		PlotData[] data = new PlotData[batches];
		int index = 0;

		for (SeriesData s : seriesData) {
			for (BatchData b : s.getAggregation().getBatches().getList()) {
				String path = Dir.getAggregatedMetricDataDir(s.getDir(),
						b.getTimestamp(), metric)
						+ distribution + Suffix.distribution;
				// TODO change to aggregation!
				path = Dir.getMetricDataDir(s.getDir(), 0, b.getTimestamp(),
						metric) + distribution + Suffix.distribution;
				data[index++] = PlotData.get(path, style, s.getName() + " @ "
						+ b.getTimestamp(), type);
			}
		}

		Plot plot = new Plot(data, dstDir, PlotFilenames.getDistributionPlot(
				metric, distribution),
				PlotFilenames
						.getDistributionGnuplotScript(metric, distribution));
		plot.generate();
	}

	private static void plotValue(SeriesData[] seriesData, String dstDir,
			PlotType type, PlotStyle style, String metric, String value)
			throws IOException, InterruptedException {
		PlotData[] data = new PlotData[seriesData.length];
		int index1 = 0;
		String m = metric == null ? Prefix.statsPlot : metric;

		for (SeriesData s : seriesData) {
			double[][] values = new double[s.getAggregation().getBatches()
					.size()][2];
			int index2 = 0;
			// TODO change to aggregation!
			// for (BatchData b : s.getAggregation().getBatches().getList()) {
			for (BatchData b : s.getRun(0).getBatches().getList()) {
				values[index2][0] = b.getTimestamp();
				// TODO change to aggregation!
				if (metric == null) {
					values[index2][1] = b.getValues().get(value).getValue();
				} else {
					values[index2][1] = b.getMetrics().get(metric).getValues()
							.get(value).getValue();
				}
				index2++;
			}
			Values v = new Values(values, value);
			String filename = PlotFilenames.getValuesDataFile(m, value, index1);
			String path = dstDir + filename;
			try {
				v.write(dstDir, filename);
			} catch (IOException e) {
				e.printStackTrace();
			}
			data[index1] = PlotData.get(path, style, s.getName(), type);
			index1++;
		}

		Plot plot = new Plot(data, dstDir,
				PlotFilenames.getValuesPlot(m, value),
				PlotFilenames.getValuesGnuplotScript(m, value));
		plot.generate();
	}

	public static void plot(Values[] values, String dstDir, String filename,
			String script) throws IOException, InterruptedException {
		// TODO use aggregated version
		PlotData[] data = new PlotData[values.length];
		for (int i = 0; i < values.length; i++) {
			values[i].write(dstDir,
					PlotFilenames.getRuntimesDataFile(values[i]));
			data[i] = PlotData
					.get(dstDir + PlotFilenames.getRuntimesDataFile(values[i]),
							PlotStyle.linespoint, values[i].getName(),
							PlotType.average);
		}

		Plot plot = new Plot(data, dstDir, filename, script);
		plot.generate();
	}

	private static Values getGeneralRuntimes(RunData runData, String runtime,
			String name) {
		// TODO use aggregated version
		double[][] values = new double[runData.getBatches().size() - 1][2];
		for (int i = 1; i < runData.getBatches().size(); i++) {
			values[i - 1][0] = runData.getBatches().get(i).getTimestamp();
			if (runData.getBatches().get(i).getGeneralRuntimes().get(runtime) == null) {
				values[i - 1][1] = Double.NaN;
			} else {
				values[i - 1][1] = runData.getBatches().get(i)
						.getGeneralRuntimes().get(runtime).getRuntime() / 1000.0 / 1000.0 / 1000.0;
			}
		}
		return new Values(values, name);
	}

	private static Values getMetricRuntimes(RunData runData, String metric,
			String name) {
		// TODO use aggregated version
		double[][] values = new double[runData.getBatches().size() - 1][2];
		for (int i = 1; i < runData.getBatches().size(); i++) {
			values[i - 1][0] = runData.getBatches().get(i).getTimestamp();
			if (runData.getBatches().get(i).getMetricRuntimes().get(metric) == null) {
				values[i - 1][1] = Double.NaN;
			} else {
				values[i - 1][1] = runData.getBatches().get(i)
						.getMetricRuntimes().get(metric).getRuntime() / 1000.0 / 1000.0 / 1000.0;
			}
		}
		return new Values(values, name);
	}
}
