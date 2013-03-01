package dna.plot;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import dna.io.Dir;
import dna.plot.Gnuplot.PlotStyle;
import dna.plot.data.PlotData;
import dna.plot.data.PlotData.PlotType;
import dna.series.Values;
import dna.series.data.DiffData;
import dna.series.data.Distribution;
import dna.series.data.MetricData;
import dna.series.data.RunData;
import dna.series.data.RunTime;
import dna.series.data.SeriesData;
import dna.series.data.Value;
import dna.settings.Suffix;
import dna.util.Log;

public class Plotting {
	public static void plotRun(SeriesData seriesData, RunData runData,
			String dstDir) throws IOException, InterruptedException {
		Log.infoSep();
		Log.info("plotting all for run " + runData.getRun() + " of "
				+ seriesData.getRuns().size() + " in " + seriesData.getDir());
		Plotting.plotDistributions(seriesData, runData, dstDir);
		Plotting.plotValues(seriesData, runData, dstDir);
		Plotting.plotRuntimes(seriesData, runData, dstDir);
		Log.infoSep();
	}

	public static void plotDistributions(SeriesData seriesData,
			RunData runData, String dstDir) throws IOException,
			InterruptedException {
		Log.infoSep();
		Log.info("plotting distributions for run " + runData.getRun() + "/"
				+ seriesData.getRuns().size() + " in " + seriesData.getDir());

		for (MetricData metric : runData.getDiffs().get(0).getMetrics()
				.getList()) {
			(new File(dstDir)).mkdirs();

			for (Distribution distribution : metric.getDistributions()
					.getList()) {
				PlotData[] data = new PlotData[runData.getDiffs().size()];
				int i = 0;
				for (DiffData diffData : runData.getDiffs().getList()) {
					MetricData metricData = diffData.getMetrics().get(
							metric.getName());
					Distribution d = metricData.getDistributions().get(
							distribution.getName());
					String path = Dir.getMetricDataDir(seriesData.getDir(),
							runData.getRun(), diffData.getTimestamp(),
							metricData.getName())
							+ d.getName() + Suffix.distribution;
					data[i] = PlotData.get(path, PlotStyle.linespoint,
							diffData.getTimestamp() + "", PlotType.average);
					i++;
				}

				Plot plot = new Plot(
						data,
						dstDir,
						PlotFilenames.getDistributionPlot(metric, distribution),
						PlotFilenames.getDistributionGnuplotScript(metric,
								distribution));
				plot.generate();
			}
		}
	}

	public static void plotValues(SeriesData seriesData, RunData runData,
			String dstDir) throws IOException, InterruptedException {
		Log.infoSep();
		Log.info("plotting values for run " + runData.getRun() + " of "
				+ seriesData.getRuns().size() + " in " + seriesData.getDir());

		for (MetricData metric : runData.getDiffs().get(0).getMetrics()
				.getList()) {
			(new File(dstDir)).mkdirs();

			for (Value value : metric.getValues().getList()) {
				double[][] values = new double[runData.getDiffs().size()][2];
				int i = 0;
				for (DiffData diffData : runData.getDiffs().getList()) {
					MetricData metricData = diffData.getMetrics().get(
							metric.getName());
					values[i][0] = diffData.getTimestamp();
					values[i][1] = metricData.getValues().get(value.getName())
							.getValue();
					i++;
				}

				Values v = new Values(values, value.getName());
				try {
					v.write(dstDir, PlotFilenames.getValuesDataFile(metric, v));
				} catch (IOException e) {
					e.printStackTrace();
				}

				PlotData[] data = new PlotData[] { PlotData
						.get(dstDir
								+ PlotFilenames.getValuesDataFile(metric, v),
								PlotStyle.linespoint, value.getName(),
								PlotType.average) };
				Plot plot = new Plot(data, dstDir, PlotFilenames.getValuesPlot(
						metric, v), PlotFilenames.getValuesGnuplotScript(
						metric, v));
				plot.generate();
			}
		}
	}

	public static void plotRuntimes(SeriesData seriesData, RunData runData,
			String dstDir) throws IOException, InterruptedException {
		Log.infoSep();
		Log.info("plotting runtimes for run " + runData.getRun() + " of "
				+ seriesData.getRuns().size() + " in " + seriesData.getDir());

		if (runData.getDiffs().size() < 2) {
			return;
		}
		ArrayList<RunTime> generalRuntimes = new ArrayList<RunTime>(runData
				.getDiffs().get(1).getGeneralRuntimes().getList());
		ArrayList<RunTime> metricRuntimes = new ArrayList<RunTime>(runData
				.getDiffs().get(1).getMetricRuntimes().getList());

		Values[] general = new Values[generalRuntimes.size()];
		Values[] metric = new Values[metricRuntimes.size()];
		Values[] all = new Values[general.length + metric.length];

		for (int i = 0; i < generalRuntimes.size(); i++) {
			general[i] = getGeneralRuntimes(runData, generalRuntimes.get(i)
					.getName());
			all[i] = general[i];
		}
		for (int i = 0; i < metricRuntimes.size(); i++) {
			metric[i] = getMetricRuntimes(runData, metricRuntimes.get(i)
					.getName());
			all[i + general.length] = metric[i];
		}

		Plotting.plot(general, dstDir,
				PlotFilenames.getRuntimesPlot("general"), PlotFilenames
						.getRuntimesGnuplotScript(PlotFilenames
								.getRuntimesPlot("general")));
		Plotting.plot(metric, dstDir, PlotFilenames.getRuntimesPlot("metric"),
				PlotFilenames.getRuntimesGnuplotScript(PlotFilenames
						.getRuntimesPlot("metric")));
		Plotting.plot(all, dstDir, PlotFilenames.getRuntimesPlot("all"),
				PlotFilenames.getRuntimesGnuplotScript(PlotFilenames
						.getRuntimesPlot("all")));
	}

	public static void plot(Values[] values, String dstDir, String filename,
			String script) throws IOException, InterruptedException {
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

	private static Values getGeneralRuntimes(RunData runData, String name) {
		double[][] values = new double[runData.getDiffs().size() - 1][2];
		for (int i = 1; i < runData.getDiffs().size(); i++) {
			values[i - 1][0] = runData.getDiffs().get(i).getTimestamp();
			if (runData.getDiffs().get(i).getGeneralRuntimes().get(name) == null) {
				values[i - 1][1] = Double.NaN;
			} else {
				values[i - 1][1] = runData.getDiffs().get(i)
						.getGeneralRuntimes().get(name).getRuntime();
			}
		}
		return new Values(values, name);
	}

	private static Values getMetricRuntimes(RunData runData, String name) {
		double[][] values = new double[runData.getDiffs().size() - 1][2];
		for (int i = 1; i < runData.getDiffs().size(); i++) {
			values[i - 1][0] = runData.getDiffs().get(i).getTimestamp();
			if (runData.getDiffs().get(i).getMetricRuntimes().get(name) == null) {
				values[i - 1][1] = Double.NaN;
			} else {
				values[i - 1][1] = runData.getDiffs().get(i)
						.getMetricRuntimes().get(name).getRuntime();
			}
		}
		return new Values(values, name);
	}
}
