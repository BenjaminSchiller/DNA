package dna.plot;

import java.io.File;
import java.io.IOException;

import dna.io.filesystem.Dir;
import dna.io.filesystem.Names;
import dna.io.filesystem.PlotFilenames;
import dna.io.filesystem.Prefix;
import dna.io.filesystem.Suffix;
import dna.plot.Gnuplot.PlotStyle;
import dna.plot.data.PlotData;
import dna.plot.data.PlotData.PlotType;
import dna.series.SeriesStats;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedDistribution;
import dna.series.aggdata.AggregatedMetric;
import dna.series.aggdata.AggregatedNodeValueList;
import dna.series.aggdata.AggregatedSeries;
import dna.series.aggdata.AggregatedValue;
import dna.series.aggdata.AggregatedValueList;
import dna.series.data.SeriesData;
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
		Log.info("plotting all data for " + seriesData.length + " series ("
				+ type + "/" + style + ")");
		(new File(dstDir)).mkdirs();
		Plotting.plotDistributions(seriesData, dstDir, type, style);
		Plotting.plotValues(seriesData, dstDir, type, style);
		Plotting.plotStatistics(seriesData, dstDir, type, style);
		Plotting.plotRuntimes(seriesData, dstDir, type, style);
		Plotting.plotNodeValueLists(seriesData, dstDir, type, style);
		Log.infoSep();
	}

	public static void plotDistributions(SeriesData[] seriesData,
			String dstDir, PlotType type, PlotStyle style) throws IOException,
			InterruptedException {
		Log.info("plotting distributions");
		for (AggregatedMetric m : seriesData[0].getAggregation().getBatches()[0]
				.getMetrics().getList()) {
			for (AggregatedDistribution d : m.getDistributions().getList()) {
				Plotting.plotDistributon(seriesData, dstDir, type, style,
						m.getName(), d.getName());
			}
		}
	}

	public static void plotNodeValueLists(SeriesData[] seriesData,
			String dstDir, PlotType type, PlotStyle style) throws IOException,
			InterruptedException {
		Log.info("plotting nodevaluelists");
		for (AggregatedMetric m : seriesData[0].getAggregation().getBatches()[0]
				.getMetrics().getList()) {
			for (AggregatedNodeValueList n : m.getNodeValues().getList()) {
				Plotting.plotNodeValueList(seriesData, dstDir, type, style,
						m.getName(), n.getName());
			}
		}
	}

	public static void plotValues(SeriesData[] seriesData, String dstDir,
			PlotType type, PlotStyle style) throws IOException,
			InterruptedException {
		Log.info("plotting values");
		for (AggregatedMetric metric : seriesData[0].getAggregation()
				.getBatches()[0].getMetrics().getList()) {
			for (AggregatedValue value : metric.getValues().getList()) {
				Plotting.plotValue(seriesData, dstDir, type, style,
						metric.getName(), value.getName());
			}
		}
	}

	public static void plotStatistics(SeriesData[] seriesData, String dstDir,
			PlotType type, PlotStyle style) throws IOException,
			InterruptedException {
		Log.info("plotting statistics");
		for (String value : SeriesStats.statisticsToPlot) {
			Plotting.plotValue(seriesData, dstDir, type, style, null, value);
		}
	}

	public static void plotRuntimes(SeriesData[] seriesData, String dstDir,
			PlotType type, PlotStyle style) throws IOException,
			InterruptedException {
		Log.info("plotting runtimes");
		for (SeriesData s : seriesData) {
			if (s.getAggregation().getBatches().length < 2) {
				return;
			}
		}

		int gr = SeriesStats.generalRuntimesOfCombinedPlot.length
				* seriesData.length;
		int mr = 0;
		for (SeriesData s : seriesData) {
			mr += s.getAggregation().getBatches()[0].getMetricRuntimes().size();
		}

		for (String runtime : SeriesStats.generalRuntimesToPlot) {
			AggregatedValue[][] runtimes = new AggregatedValue[seriesData.length][];
			String[] names = new String[seriesData.length];
			double[][] x = new double[seriesData.length][];
			int index = 0;
			for (SeriesData s : seriesData) {
				runtimes[index] = getGeneralRuntimes(s.getAggregation(),
						runtime);
				x[index] = getX(s.getAggregation());
				names[index] = s.getName();
				index++;
			}
			Plotting.plot(runtimes, names, x, dstDir, PlotFilenames
					.getRuntimesStatisticPlot(runtime), PlotFilenames
					.getRuntimesGnuplotScript(PlotFilenames
							.getRuntimesStatisticPlot(runtime)), runtime + " ("
					+ type + ")", type, style);
		}

		for (String metric : seriesData[0].getAggregation().getBatches()[0]
				.getMetricRuntimes().getNames()) {
			AggregatedValue[][] runtimes = new AggregatedValue[seriesData.length][];
			String[] names = new String[seriesData.length];
			double[][] x = new double[seriesData.length][];
			int index = 0;
			for (SeriesData s : seriesData) {
				runtimes[index] = getMetricRuntimes(s.getAggregation(), metric);
				x[index] = getX(s.getAggregation());
				names[index] = s.getName();
				index++;
			}
			Plotting.plot(runtimes, names, x, dstDir, PlotFilenames
					.getRuntimesMetricPlot(metric), PlotFilenames
					.getRuntimesGnuplotScript(PlotFilenames
							.getRuntimesMetricPlot(metric)), metric + " ("
					+ type + ")", type, style);
		}

		AggregatedValue[][] general = new AggregatedValue[gr][];
		AggregatedValue[][] metrics = new AggregatedValue[mr][];
		String[] generalNames = new String[gr];
		String[] metricsNames = new String[mr];
		double[][] generalX = new double[gr][];
		double[][] metricsX = new double[mr][];

		int index1 = 0;
		int index2 = 0;
		for (SeriesData s : seriesData) {
			for (String runtime : SeriesStats.generalRuntimesOfCombinedPlot) {
				general[index1] = getGeneralRuntimes(s.getAggregation(),
						runtime);
				generalX[index1] = getX(s.getAggregation());
				generalNames[index1] = runtime + "-" + s.getName();
				index1++;
			}
			for (String metric : s.getAggregation().getBatches()[0]
					.getMetricRuntimes().getNames()) {
				metrics[index2] = getMetricRuntimes(s.getAggregation(), metric);
				metricsX[index2] = getX(s.getAggregation());
				metricsNames[index2] = metric + "-" + s.getName();
				index2++;
			}
		}

		// TODO re-add fraction of runtimes....

		// int index = 0;
		// for (SeriesData s : seriesData) {
		// double[] sum = new double[s.getAggregation().getBatches().length -
		// 1];
		// int metricCount = s.getAggregation().getBatches()[0]
		// .getMetricRuntimes().getNames().size();
		// for (int i = 0; i < metricCount; i++) {
		// for (int j = 0; j < sum.length; j++) {
		// sum[j] += metrics[index + i].getValues()[j][1];
		// }
		// }
		// for (int i = 0; i < metricCount; i++) {
		// for (int j = 0; j < sum.length; j++) {
		// metricsFraction[index + i].getValues()[j][1] /= sum[j];
		// }
		// }
		//
		// index += metricCount;
		// }

		Plotting.plot(
				general,
				generalNames,
				generalX,
				dstDir,
				PlotFilenames
						.getRuntimesStatisticPlot(PlotFilenames.generalRuntimes),
				PlotFilenames.getRuntimesGnuplotScript(PlotFilenames
						.getRuntimesStatisticPlot(PlotFilenames.generalRuntimes)),
				"general runtimes (" + type + ")", type, style);

		Plotting.plot(metrics, metricsNames, metricsX, dstDir, PlotFilenames
				.getRuntimesMetricPlot(PlotFilenames.metricRuntimes),
				PlotFilenames.getRuntimesGnuplotScript(PlotFilenames
						.getRuntimesMetricPlot(PlotFilenames.metricRuntimes)),
				"metric runtimes (" + type + ")", type, style);
	}

	private static void plotDistributon(SeriesData[] seriesData, String dstDir,
			PlotType type, PlotStyle style, String metric, String distribution)
			throws IOException, InterruptedException {
		Log.info("distribution " + distribution + " of " + metric);
		int batches = 0;
		for (SeriesData s : seriesData) {
			batches += s.getAggregation().getBatches().length;
		}

		PlotData[] data = new PlotData[batches];
		int index = 0;

		for (SeriesData s : seriesData) {
			for (AggregatedBatch b : s.getAggregation().getBatches()) {
				String path = Dir.getAggregatedMetricDataDir(s.getDir(),
						b.getTimestamp(), metric)
						+ distribution + Suffix.distribution;
				data[index++] = PlotData.get(path, style, s.getName() + " @ "
						+ b.getTimestamp(), type);
			}
		}

		Plot plot = new Plot(data, dstDir, PlotFilenames.getDistributionPlot(
				metric, distribution),
				PlotFilenames
						.getDistributionGnuplotScript(metric, distribution));
		plot.setTitle(distribution + " (" + type + ")");
		plot.generate();
	}

	private static void plotNodeValueList(SeriesData[] seriesData,
			String dstDir, PlotType type, PlotStyle style, String metric,
			String nodevaluelist) throws IOException, InterruptedException {
		Log.info("nodevaluelist " + nodevaluelist + " of " + metric);
		int batches = 0;
		for (SeriesData s : seriesData) {
			batches += s.getAggregation().getBatches().length;
		}

		PlotData[] data = new PlotData[batches];
		int index = 0;

		for (SeriesData s : seriesData) {
			for (AggregatedBatch b : s.getAggregation().getBatches()) {
				String path = Dir.getAggregatedMetricDataDir(s.getDir(),
						b.getTimestamp(), metric)
						+ nodevaluelist + Suffix.nodeValueList;
				data[index++] = PlotData.get(path, style, s.getName() + " @ "
						+ b.getTimestamp(), type);
			}
		}

		Plot plot = new Plot(data, dstDir, PlotFilenames.getNodeValueListPlot(
				metric, nodevaluelist),
				PlotFilenames.getNodeValueListGnuplotScript(metric,
						nodevaluelist));
		plot.setTitle(nodevaluelist + " (" + type + ")");
		plot.generate();
	}

	private static void plotValue(SeriesData[] seriesData, String dstDir,
			PlotType type, PlotStyle style, String metric, String value)
			throws IOException, InterruptedException {
		PlotData[] data = new PlotData[seriesData.length];
		int index1 = 0;
		String m = metric == null ? Prefix.statsPlot : metric;

		for (SeriesData s : seriesData) {
			double[] x = new double[s.getAggregation().getBatches().length];
			AggregatedValue[] values = new AggregatedValue[s.getAggregation()
					.getBatches().length];
			int index2 = 0;
			for (AggregatedBatch b : s.getAggregation().getBatches()) {
				x[index2] = b.getTimestamp();
				if (metric == null) {
					values[index2] = b.getValues().get(value);
				} else {
					values[index2] = b.getMetrics().get(metric).getValues()
							.get(value);
				}
				index2++;
			}
			String filename = PlotFilenames.getValuesDataFile(m, value, index1);
			String path = dstDir + filename;
			try {
				AggregatedValue.write(x, values, dstDir, filename);
			} catch (IOException e) {
				e.printStackTrace();
			}
			data[index1] = PlotData.get(path, style, s.getName(), type);
			index1++;
		}

		Plot plot = new Plot(data, dstDir,
				PlotFilenames.getValuesPlot(m, value),
				PlotFilenames.getValuesGnuplotScript(m, value));
		plot.setTitle(value + " (" + type + ")");
		plot.generate();
	}

	public static void plot(AggregatedValue[][] values, String[] names,
			double[][] x, String dstDir, String filename, String script,
			String title, PlotType type, PlotStyle style) throws IOException,
			InterruptedException {
		PlotData[] data = new PlotData[values.length];
		for (int i = 0; i < values.length; i++) {
			AggregatedValue.write(x[i], values[i], dstDir,
					PlotFilenames.getRuntimesDataFile(names[i]));
			data[i] = PlotData.get(
					dstDir + PlotFilenames.getRuntimesDataFile(names[i]),
					style, names[i], type);
		}

		Plot plot = new Plot(data, dstDir, filename, script);
		plot.setTitle(title);
		plot.generate();
	}

	private static AggregatedValue[] getGeneralRuntimes(
			AggregatedSeries aggregation, String runtime) {
		AggregatedValue[] values = new AggregatedValue[aggregation.getBatches().length - 1];
		for (int i = 1; i < aggregation.getBatches().length; i++) {
			values[i - 1] = aggregation.getBatches()[i].getGeneralRuntimes()
					.get(runtime).clone(1.0 / 1000.0 / 1000.0 / 1000.0);
			if (values[i - 1] == null) {
				values[i - 1] = AggregatedValue.getNaN();
			}
		}
		return values;
	}

	private static AggregatedValue[] getMetricRuntimes(
			AggregatedSeries aggregation, String metric) {
		AggregatedValue[] values = new AggregatedValue[aggregation.getBatches().length - 1];
		for (int i = 1; i < aggregation.getBatches().length; i++) {
			values[i - 1] = aggregation.getBatches()[i].getMetricRuntimes()
					.get(metric).clone(1.0 / 1000.0 / 1000.0 / 1000.0);
			if (values[i - 1] == null) {
				values[i - 1] = AggregatedValue.getNaN();
			}
		}
		return values;
	}

	private static double[] getX(AggregatedSeries aggregation) {
		double[] x = new double[aggregation.getBatches().length - 1];
		for (int i = 1; i < aggregation.getBatches().length; i++) {
			x[i - 1] = aggregation.getBatches()[i].getTimestamp();
		}
		return x;
	}

}
