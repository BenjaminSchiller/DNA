package dna.util.network;

import java.io.IOException;

import org.joda.time.DateTime;

import dna.metrics.Metric;
import dna.metrics.assortativity.AssortativityU;
import dna.metrics.centrality.BetweennessCentralityU;
import dna.metrics.clustering.DirectedClusteringCoefficientU;
import dna.metrics.clustering.local.DirectedLocalClusteringCoefficientR;
import dna.metrics.degree.DegreeDistributionR;
import dna.metrics.degree.DegreeDistributionU;
import dna.metrics.degree.WeightedDegreeDistributionR;
import dna.metrics.motifs.DirectedMotifsU;
import dna.metrics.paths.unweighted.UnweightedAllPairsShortestPathsU;
import dna.metrics.paths.weighted.intWeighted.IntWeightedAllPairsShortestPathsU;
import dna.metrics.richClub.RichClubConnectivityByDegreeU;
import dna.metrics.similarityMeasures.matching.MatchingU;
import dna.metrics.similarityMeasures.overlap.OverlapU;
import dna.metrics.weights.EdgeWeightsR;
import dna.plot.Plotting;
import dna.plot.PlottingConfig;
import dna.plot.PlottingConfig.PlotFlag;
import dna.series.data.SeriesData;
import dna.util.Config;
import dna.visualization.graph.GraphVisualization;

/**
 * A utility class containing several enumerations, configurations and
 * convenience methods used by gIDS for tha analysis of different types of
 * datasets.
 * 
 * @author Rwilmes
 * 
 */
public class DatasetUtils {

	/*
	 * ENUMERATIONS
	 */
	public enum DatasetType {
		packet, netflow, session, botnet
	}

	public enum ModelType {
		modelA
	}

	public enum TimestampFormat {
		timestamp, week_day
	}

	public enum ZipMode {
		none, runs, batches
	}

	/**
	 * Returns the absolute date of a DARPA 1998 week and day pair in
	 * dd-MM-yyyy.
	 **/
	public static String getDarpaDate(int week, int day) {
		int d = 1;
		int m = 6;

		int days = (week - 1) * 7 + (day - 1);

		if (days > 31) {
			d += (days - 30);
			m++;
		} else {
			d += days;
		}

		String ds = (d > 9) ? "" + d : "0" + d;
		String ms = (m > 9) ? "" + m : "0" + m;
		String ys = "1998";

		return ds + "-" + ms + "-" + ys;
	}

	/** Returns a conventional series-name. **/
	public static String getName(int secondsPerBatch,
			long lifeTimePerEdgeSeconds) {
		return secondsPerBatch + "_" + lifeTimePerEdgeSeconds;
	}

	/** Returns a conventional series-name. **/
	public static String getName(int secondsPerBatch,
			long lifeTimePerEdgeSeconds, String descr) {
		System.out.println("GETNAME: " + descr);
		if (descr == null || descr.equals("") || descr.equals("null"))
			return getName(secondsPerBatch, lifeTimePerEdgeSeconds);
		else
			return secondsPerBatch + "_" + lifeTimePerEdgeSeconds + "_" + descr;
	}

	/*
	 * PLOTTING
	 */
	/** Plotting method for datasets. **/
	public static void plot(SeriesData sd, String dir) throws IOException,
			InterruptedException {
		DatasetUtils.plot(sd, dir, -1, -1);
	}

	/** Plotting method for datasets. **/
	public static void plot(SeriesData sd, String dir, DateTime from,
			DateTime to) throws IOException, InterruptedException {
		DatasetUtils.plot(sd, dir, from.getMillis() / 1000,
				to.getMillis() / 1000);
	}

	/** Plotting method for datasets. **/
	public static void plot(SeriesData sd, String dir, long from, long to)
			throws IOException, InterruptedException {
		String defXTics = Config.get(gnuplot_xtics);
		String defDateTime = Config.get(gnuplot_datetime);
		String defPlotDateTime = Config.get(gnuplot_plotdatetime);
		Config.overwrite(gnuplot_datetime, "%H:%M");
		Config.overwrite(gnuplot_plotdatetime, "true");
		GraphVisualization.setText("Generating single scalar plots for "
				+ sd.getName());
		PlottingConfig pcfg = new PlottingConfig(
				PlotFlag.plotSingleScalarValues);
		if (from != to)
			pcfg.setPlotInterval(from, to, 1);
		Plotting.plot(sd, dir, pcfg);
		Config.overwrite(gnuplot_xtics, defXTics);
		Config.overwrite(gnuplot_datetime, defDateTime);
		Config.overwrite(gnuplot_plotdatetime, defPlotDateTime);
		GraphVisualization
				.setText("Plotting of " + sd.getName() + " finished!");
	}

	/*
	 * CONFIGURATION
	 */
	public static final long second = 1;
	public static final long minute = 60 * second;
	public static final long hour = 60 * minute;

	public static final int gnuplotOffsetSeconds = 7200;

	public static final String gnuplot_xtics = "GNUPLOT_XTICS";
	public static final String gnuplot_datetime = "GNUPLOT_DATETIME";
	public static final String gnuplot_plotdatetime = "GNUPLOT_PLOTDATETIME";
	public static final String gnuplot_xoffset = "GNUPLOT_XOFFSET";

	public static void setGnuplotPath(String path) {
		Config.overwrite("GNUPLOT_PATH", path);
	}

	public static void setGnuplotSettings() {
		Config.overwrite("GNUPLOT_DEFAULT_PLOT_LABELS", "true");
		Config.overwrite("GNUPLOT_LABEL_BIG_TIMESTAMPS", "true");
		// Config.overwrite("GNUPLOT_LABEL_FILTER_LIST",
		// "DoS1:max, DoS2:product");
		Config.overwrite("GNUPLOT_LABEL_COLOR_OFFSET", "12");
	}

	public static void setGraphVisSettings() {
		Config.overwrite("GRAPH_VIS_NETWORK_NODE_SHAPE", "true");
		Config.overwrite("GRAPH_VIS_TIMESTAMP_IN_SECONDS", "true");
		Config.overwrite("GRAPH_VIS_DATETIME_FORMAT", "HH:mm:ss");
		Config.overwrite("GRAPH_VIS_TIMESTAMP_OFFSET", "-" + (int) (2 * hour));
	}

	/*
	 * METRICS
	 */
	public static final String[] metricHostFilter = new String[] { "HOST" };
	public static final String[] metricPortFilter = new String[] { "PORT" };

	public static final Metric[] metricsAll = new Metric[] {
			new AssortativityU(), new BetweennessCentralityU(),
			new DirectedClusteringCoefficientU(),
			new DirectedLocalClusteringCoefficientR(),
			new UnweightedAllPairsShortestPathsU(),
			new IntWeightedAllPairsShortestPathsU(),
			new RichClubConnectivityByDegreeU(), new MatchingU(),
			new OverlapU(), new DegreeDistributionU(), new EdgeWeightsR(1.0),
			new DirectedMotifsU() };

	public static final Metric[] metricsDefault = new Metric[] {
			new DegreeDistributionU(), new DirectedMotifsU(),
			new EdgeWeightsR(1.0), new WeightedDegreeDistributionR() };

	public static final Metric[] metricsDefaultHostOnly = new Metric[] {
			new DegreeDistributionR(metricHostFilter), new DirectedMotifsU(),
			new EdgeWeightsR(1.0),
			new WeightedDegreeDistributionR(metricHostFilter) };

	public static final Metric[] metricsDefaultPortOnly = new Metric[] {
			new DegreeDistributionR(metricPortFilter), new DirectedMotifsU(),
			new EdgeWeightsR(1.0),
			new WeightedDegreeDistributionR(metricPortFilter) };

	public static final Metric[] metricsDefaultAll = new Metric[] {
			new DegreeDistributionR(metricHostFilter),
			new DegreeDistributionR(metricPortFilter),
			new DegreeDistributionR(), new DirectedMotifsU(),
			new EdgeWeightsR(1.0),
			new WeightedDegreeDistributionR(metricHostFilter),
			new WeightedDegreeDistributionR(metricPortFilter),
			new WeightedDegreeDistributionR() };

}