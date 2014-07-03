package dna.plot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import dna.io.filesystem.Dir;
import dna.io.filesystem.PlotFilenames;
import dna.plot.PlottingConfig.PlotFlag;
import dna.plot.data.ExpressionData;
import dna.plot.data.PlotData;
import dna.plot.data.PlotData.DistributionPlotType;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
import dna.plot.data.PlotData.PlotStyle;
import dna.plot.data.PlotData.PlotType;
import dna.series.SeriesStats;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.series.aggdata.AggregatedDistribution;
import dna.series.aggdata.AggregatedDistributionList;
import dna.series.aggdata.AggregatedMetric;
import dna.series.aggdata.AggregatedMetricList;
import dna.series.aggdata.AggregatedNodeValueList;
import dna.series.aggdata.AggregatedNodeValueListList;
import dna.series.aggdata.AggregatedRunTimeList;
import dna.series.aggdata.AggregatedValue;
import dna.series.aggdata.AggregatedValueList;
import dna.series.data.SeriesData;
import dna.util.Config;
import dna.util.Log;
import dna.util.Memory;

/**
 * Plotting class which holds static method for plotting.
 * 
 * @author Rwilmes
 * @date 19.05.2014
 */
public class Plotting {

	/**
	 * Main plotting method which handles the whole process of plotting. Takes a
	 * plotting config object which controls the behaviour. Which batches will
	 * be plotted is given by the parameters in the PlottingConfig.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param config
	 *            PlottingConfig to control plotting behaviour.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	private static void plotFromTo(SeriesData[] seriesData, String dstDir,
			PlottingConfig config) throws IOException, InterruptedException {
		// log output
		Log.infoSep();
		Log.info("plotting data from " + seriesData.length + " series to "
				+ dstDir);

		// if more than 1 series, call multiple plot method
		if (seriesData.length > 1)
			Plotting.plotFromToMultipleSeries(seriesData, dstDir, config);

		// if single series, call single plot method
		if (seriesData.length == 1)
			Plotting.plotFromToSingleSeries(seriesData[0], dstDir, config);

		// if no series, print out warning
		if (seriesData.length < 1)
			Log.error("Plotting called without a series to plot.");
		else
			Log.info("Plotting finished!");

	}

	/**
	 * Plots multiple series.
	 * 
	 * @param series
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory.
	 * @param config
	 *            PlottingConfig controlling the plot.
	 * @throws IOException
	 *             Thrown by the writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	private static void plotFromToMultipleSeries(SeriesData[] seriesData,
			String dstDir, PlottingConfig config) throws IOException,
			InterruptedException {
		// create dir
		(new File(dstDir)).mkdirs();

		long timestampFrom = config.getTimestampFrom();
		long timestampTo = config.getTimestampTo();
		long stepsize = config.getStepsize();
		PlotType type = config.getPlotType();
		PlotStyle style = config.getPlotStyle();
		NodeValueListOrder order = config.getNvlOrder();
		NodeValueListOrderBy orderBy = config.getNvlOrderBy();
		DistributionPlotType distPlotType = config.getDistPlotType();

		boolean singleFile = Config.getBoolean("GENERATION_BATCHES_AS_ZIP");
		boolean plotDistributions = config.isPlotDistributions();
		boolean plotNodeValues = config.isPlotNodeValueLists();

		boolean plotStatistics = config.isPlotStatistics();
		boolean plotMetricValues = config.isPlotMetricValues();
		boolean plotCustomValues = config.isPlotCustomValues();
		boolean plotRuntimes = config.isPlotRuntimes();

		// gather relevant batches
		String[] batches = Dir.getBatchesFromTo(seriesData[0].getDir(),
				timestampFrom, timestampTo, stepsize);

		for (int i = 0; i < seriesData.length; i++) {
			String tempDir = Dir.getAggregationDataDir(seriesData[i].getDir());
			String[] tempBatches = Dir.getBatchesFromTo(tempDir, timestampFrom,
					timestampTo, stepsize);
			if (tempBatches.length > batches.length)
				batches = tempBatches;
		}

		double timestamps[] = new double[batches.length];
		for (int j = 0; j < batches.length; j++) {
			timestamps[j] = Dir.getTimestamp(batches[j]);
		}

		// list series'
		for (int i = 0; i < seriesData.length; i++) {
			Log.info("\t'" + seriesData[i].getName() + "'");
		}

		@SuppressWarnings("unchecked")
		ArrayList<Long>[] seriesTimestamps = new ArrayList[seriesData.length];

		// check what batches each series possesses
		for (int i = 0; i < seriesData.length; i++) {
			SeriesData sd = seriesData[i];
			seriesTimestamps[i] = new ArrayList<Long>();

			for (double t : timestamps) {
				for (int j = 0; j < sd.getAggregation().getBatches().length; j++) {
					long timestamp = sd.getAggregation().getBatches()[j]
							.getTimestamp();
					if (timestamp == t)
						seriesTimestamps[i].add(timestamp);
				}
			}
		}

		// list relevant batches
		Log.infoSep();
		Log.info("Plotting batches:");
		for (int i = 0; i < batches.length && i <= 3; i++) {
			Log.info("\t" + batches[i]);
		}
		if (batches.length > 3) {
			Log.info("\t...");
			Log.info("\t" + batches[batches.length - 1]);
		}

		// array of the initbatch of each series
		AggregatedBatch[] initBatches = new AggregatedBatch[seriesData.length];

		// read init batches
		for (int i = 0; i < seriesData.length; i++) {
			SeriesData series = seriesData[i];
			String tempDir = Dir.getAggregationDataDir(series.getDir());
			long timestamp = series.getAggregation().getBatches()[0]
					.getTimestamp();
			if (singleFile)
				initBatches[i] = AggregatedBatch.readFromSingleFile(tempDir,
						timestamp, Dir.delimiter, BatchReadMode.readAllValues);
			else
				initBatches[i] = AggregatedBatch.read(
						Dir.getBatchDataDir(tempDir, timestamp), timestamp,
						BatchReadMode.readAllValues);
		}

		// replace wildcards and remove unnecessary plots
		config.checkCustomPlotConfigs(initBatches);

		// plot single value plots
		if (plotStatistics || plotMetricValues || plotRuntimes
				|| plotCustomValues)
			Plotting.plotSingleValuePlots(seriesData, dstDir, batches,
					timestamps, initBatches, plotStatistics,
					config.getCustomStatisticPlots(), plotMetricValues,
					config.getCustomMetricValuePlots(), plotCustomValues,
					config.getCustomValuePlots(), plotRuntimes,
					config.getCustomRuntimePlots(), singleFile, type, style);

		// plot distribution and nodevaluelist plots
		if (plotDistributions || plotNodeValues)
			Plotting.plotDistributionAndNodeValueListPlots(seriesData, dstDir,
					batches, timestamps, initBatches, seriesTimestamps,
					plotDistributions, config.getCustomDistributionPlots(),
					plotNodeValues, config.getCustomNodeValueListPlots(),
					singleFile, distPlotType, order, orderBy, type, style);
	}

	/** Plots the def. distribution and nodeavluelist plots for multiple series. */
	private static void plotDistributionAndNodeValueListPlots(
			SeriesData[] seriesData, String dstDir, String[] batches,
			double[] timestamps, AggregatedBatch[] initBatches,
			ArrayList<Long>[] seriesTimestamps, boolean plotDistributions,
			ArrayList<PlotConfig> customDistributionPlots,
			boolean plotNodeValues,
			ArrayList<PlotConfig> customNodeValueListPlots, boolean singleFile,
			DistributionPlotType distPlotType, NodeValueListOrder order,
			NodeValueListOrderBy orderBy, PlotType type, PlotStyle style)
			throws IOException, InterruptedException {
		Log.infoSep();

		// list of default plots
		ArrayList<Plot> defaultPlots = new ArrayList<Plot>();

		// contains the names of values
		ArrayList<String> distValues = new ArrayList<String>();
		ArrayList<String> nvlValues = new ArrayList<String>();

		// contains for each value a list of domains
		ArrayList<ArrayList<String>> distDomainsList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> nvlDomainsList = new ArrayList<ArrayList<String>>();

		// contains an int which states how often a value occurs
		ArrayList<Integer> distOccurence = new ArrayList<Integer>();
		ArrayList<Integer> nvlOccurence = new ArrayList<Integer>();

		// log flags
		boolean loggedDist = false;
		boolean loggedNvl = false;

		ArrayList<Plot> customPlots = new ArrayList<Plot>();

		if (plotDistributions) {
			if (customDistributionPlots.size() > 0)
				Log.info("Plotting custom distribution plots:");

			// iterate over plot configs
			for (PlotConfig pc : customDistributionPlots) {
				Log.info("\tplotting '" + pc.getFilename() + "'");
				String[] values = pc.getValues();
				String[] domains = pc.getDomains();

				// check what to plot
				boolean plotDist = false;
				boolean plotCdf = false;
				switch (distPlotType) {
				case distOnly:
					plotDist = true;
					break;
				case cdfOnly:
					plotCdf = true;
					break;
				case distANDcdf:
					plotDist = true;
					plotCdf = true;
					break;
				}

				// count different domains
				ArrayList<String> dList = new ArrayList<String>();
				for (String d : domains) {
					if (!dList.contains(d)) {
						dList.add(d);
					}
				}

				// set simpleTitles if only one domain
				boolean simpleTitles = false;
				if (dList.size() == 1)
					simpleTitles = true;

				// init plot data list
				ArrayList<PlotData> dataList = null;
				ArrayList<PlotData> cdfDataList = null;
				int[] seriesDataQuantities = new int[seriesData.length];

				if (plotDist)
					dataList = new ArrayList<PlotData>();
				if (plotCdf)
					cdfDataList = new ArrayList<PlotData>();

				// iterate over batches
				for (int i = 0; i < batches.length; i++) {
					long timestamp = Dir.getTimestamp(batches[i]);

					// iterate over series
					for (int j = 0; j < seriesData.length; j++) {
						// if no batch with the timestamp, continue
						if (!seriesTimestamps[j].contains(timestamp))
							continue;

						// iterate over values
						for (int k = 0; k < values.length; k++) {
							String value = values[k];
							String domain = domains[k];

							// check if series contains domain
							if (initBatches[j].getMetrics().getNames()
									.contains(domain)) {
								// check if series contains distribution
								if (initBatches[j].getMetrics().get(domain)
										.getDistributions().getNames()
										.contains(value)) {
									// set title
									String title;
									if (simpleTitles)
										title = seriesData[j].getName() + " @ "
												+ timestamp;
									else
										title = domain
												+ PlotConfig.customPlotDomainDelimiter
												+ value + " ("
												+ seriesData[j].getName()
												+ ") @ " + timestamp;

									if (plotDist)
										dataList.add(PlotData.get(value,
												domain, style, title, type));

									if (plotCdf) {
										PlotData data = PlotData.get(value,
												domain, style, title, type);
										data.setPlotAsCdf(true);
										cdfDataList.add(data);
									}
									if (i == 0)
										seriesDataQuantities[j]++;
								}
							}
						}
					}
				}

				if (plotDist) {
					// transform plot data to array
					PlotData[] data = dataList.toArray(new PlotData[0]);

					// get filename
					String filename = pc.getFilename();

					// create plot object
					Plot p = new Plot(dstDir,
							PlotFilenames.getDistributionPlot(filename),
							PlotFilenames
									.getDistributionGnuplotScript(filename),
							pc.getTitle(), pc, data);

					// set series quantities
					p.setSeriesDataQuantities(seriesDataQuantities);

					// disable date time
					p.setPlotDateTime(false);

					// add to list
					customPlots.add(p);
				}
				if (plotCdf) {
					// transform plot data to array
					PlotData[] cdfData = cdfDataList.toArray(new PlotData[0]);

					// get filename
					String filename = pc.getFilename();

					// create plot object
					Plot p = new Plot(dstDir,
							PlotFilenames.getDistributionCdfPlot(filename),
							PlotFilenames
									.getDistributionCdfGnuplotScript(filename),
							"CDF of " + pc.getTitle(), pc, cdfData);

					// set series quantities
					p.setSeriesDataQuantities(seriesDataQuantities);

					// disable date time
					p.setPlotDateTime(false);

					// set as cdf
					p.setCdfPlot(true);

					// add to list
					customPlots.add(p);
				}
			}
		}

		if (plotNodeValues) {
			if (customNodeValueListPlots.size() > 0)
				Log.info("Plotting custom nodevaluelist plots:");

			// iterate over plot configs
			for (PlotConfig pc : customNodeValueListPlots) {
				Log.info("\tplotting '" + pc.getFilename() + "'");
				String[] values = pc.getValues();
				String[] domains = pc.getDomains();

				// count different domains
				ArrayList<String> dList = new ArrayList<String>();
				for (String d : domains) {
					if (!dList.contains(d)) {
						dList.add(d);
					}
				}

				// set simpleTitles if only one domain
				boolean simpleTitles = false;
				if (dList.size() == 1)
					simpleTitles = true;

				// init plot data list
				ArrayList<PlotData> dataList = new ArrayList<PlotData>();
				int[] seriesDataQuantities = new int[seriesData.length];

				// iterate over batches
				for (int i = 0; i < batches.length; i++) {
					long timestamp = Dir.getTimestamp(batches[i]);

					// iterate over series
					for (int j = 0; j < seriesData.length; j++) {
						// if no batch with the timestamp, continue
						if (!seriesTimestamps[j].contains(timestamp))
							continue;

						// iterate over values
						for (int k = 0; k < values.length; k++) {
							String value = values[k];
							String domain = domains[k];

							// check if series contains domain
							if (initBatches[j].getMetrics().getNames()
									.contains(domain)) {
								// check if series contains nvl
								if (initBatches[j].getMetrics().get(domain)
										.getNodeValues().getNames()
										.contains(value)) {
									// set title
									String title;
									if (simpleTitles)
										title = seriesData[j].getName() + " @ "
												+ timestamp;
									else
										title = domain
												+ PlotConfig.customPlotDomainDelimiter
												+ value + " ("
												+ seriesData[j].getName()
												+ ") @ " + timestamp;

									// add data to list
									dataList.add(PlotData.get(value, domain,
											style, title, type));
									if (i == 0)
										seriesDataQuantities[j]++;
								}
							}
						}
					}
				}

				// transform plot data to array
				PlotData[] data = dataList.toArray(new PlotData[0]);

				// get filename
				String filename = pc.getFilename();

				// create plot object
				Plot p = new Plot(dstDir,
						PlotFilenames.getNodeValueListPlot(filename),
						PlotFilenames.getNodeValueListGnuplotScript(filename),
						pc.getTitle(), pc, data);

				// set series quantities
				p.setSeriesDataQuantities(seriesDataQuantities);

				// disable date time
				p.setPlotDateTime(false);

				// set nvl sort modes
				p.setNodeValueListOrder(pc.getOrder());
				p.setNodeValueListOrderBy(pc.getOrderBy());

				// add to list
				customPlots.add(p);
			}
		}

		// write script headers
		for (Plot p : customPlots)
			p.writeScriptHeader();

		// combined default plots
		for (int i = 0; i < seriesData.length; i++) {
			// distribution
			AggregatedMetricList metrics = initBatches[i].getMetrics();
			if (plotDistributions && Config.getBoolean("DEFAULT_PLOTS_ENABLED")
					&& Config.getBoolean("DEFAULT_PLOT_COMBINED_DISTRIBUTIONS")) {
				if (!loggedDist) {
					Log.info("Plotting combined default distribution plots:");
					loggedDist = true;
				}
				for (String metric : metrics.getNames()) {
					AggregatedDistributionList dists = metrics.get(metric)
							.getDistributions();
					for (String dist : dists.getNames()) {
						if (!distValues.contains(dist)) {
							Log.info("\tplotting " + "'" + dist + "'");
							// if distribution not present, add it and add new
							// domain list
							distValues.add(dist);
							ArrayList<String> dList = new ArrayList<String>();
							dList.add(metric);
							distDomainsList.add(dList);
							distOccurence.add(1);
						} else {
							// if distribution present, add new domain to domain
							// list
							int index = distValues.indexOf(dist);
							ArrayList<String> domainList = distDomainsList
									.get(index);
							distOccurence.set(index,
									distOccurence.get(index) + 1);
							if (!domainList.contains(metric)) {
								domainList.add(metric);
							}
						}
					}
				}
			}
			// plot node value lists
			if (plotNodeValues
					&& Config.getBoolean("DEFAULT_PLOTS_ENABLED")
					&& Config
							.getBoolean("DEFAULT_PLOT_COMBINED_NODEVALUELISTS")) {
				if (!loggedNvl) {
					Log.info("Plotting combined default nodevaluelist plots:");
					loggedNvl = true;
				}
				for (String metric : metrics.getNames()) {
					AggregatedNodeValueListList nvls = metrics.get(metric)
							.getNodeValues();
					for (String nvl : nvls.getNames()) {
						if (!nvlValues.contains(nvl)) {
							Log.info("\tplotting '" + nvl + "'");
							// if nvl not present, add it and add new
							// domain list
							nvlValues.add(nvl);
							ArrayList<String> dList = new ArrayList<String>();
							dList.add(metric);
							nvlDomainsList.add(dList);
							nvlOccurence.add(1);
						} else {
							// if nvl present, add new domain to domain list
							int index = nvlValues.indexOf(nvl);
							ArrayList<String> domainList = nvlDomainsList
									.get(index);
							nvlOccurence
									.set(index, nvlOccurence.get(index) + 1);
							if (!domainList.contains(metric)) {
								domainList.add(metric);
							}
						}
					}
				}
			}
		}

		// check what to plot
		boolean plotDist = false;
		boolean plotCdf = false;
		switch (distPlotType) {
		case distOnly:
			plotDist = true;
			break;
		case cdfOnly:
			plotCdf = true;
			break;
		case distANDcdf:
			plotDist = true;
			plotCdf = true;
			break;
		}

		// create dist plots
		for (int i = 0; i < distValues.size(); i++) {
			String dist = distValues.get(i);
			ArrayList<PlotData> dataList = null;
			ArrayList<PlotData> cdfDataList = null;

			if (plotDist)
				dataList = new ArrayList<PlotData>();
			if (plotCdf)
				cdfDataList = new ArrayList<PlotData>();

			int[] seriesDataQuantities = new int[seriesData.length];
			ArrayList<String> domains = distDomainsList.get(i);
			boolean simpleTitles = false;
			if (domains.size() == 1)
				simpleTitles = true;

			// iterate over batches
			for (int j = 0; j < batches.length; j++) {
				long timestamp = Dir.getTimestamp(batches[j]);

				// iterate over series
				for (int k = 0; k < seriesData.length; k++) {
					// if no batch with the timestamp, continue
					if (!seriesTimestamps[k].contains(timestamp))
						continue;

					AggregatedBatch initBatch = initBatches[k];

					// iterate over domains that contain the value
					for (String d : domains) {
						String lineTitle;
						if (simpleTitles)
							lineTitle = seriesData[k].getName();
						else
							lineTitle = d + " (" + seriesData[k].getName()
									+ ")";
						if (initBatch.getMetrics().getNames().contains(d)) {
							if (initBatch.getMetrics().get(d)
									.getDistributions().getNames()
									.contains(dist)) {
								// create "line" in plot for each batch
								if (plotDist) {
									dataList.add(PlotData.get(dist, d, style,
											lineTitle + " @ " + timestamps[j],
											type));
								}
								if (plotCdf) {
									PlotData cdfPlotData = PlotData.get(dist,
											d, style, lineTitle + " @ "
													+ timestamps[j], type);
									cdfPlotData.setPlotAsCdf(true);
									cdfDataList.add(cdfPlotData);
								}
								if (j == 0)
									seriesDataQuantities[k]++;
							} else {
								Log.debug("Adding distribution '"
										+ dist
										+ "' of domain '"
										+ d
										+ "', but dist not found in init batch of series "
										+ seriesData[k].getName());
							}
						} else {
							Log.debug("Adding distribution '" + dist
									+ "' but domain '" + d
									+ "' not found in init batch of series "
									+ seriesData[k].getName());
						}
					}
				}
			}

			// transform to plot data arrays
			PlotData[] data = dataList.toArray(new PlotData[0]);
			PlotData[] cdfData = cdfDataList.toArray(new PlotData[0]);

			// generate normal plots
			if (plotDist) {
				// title
				String plotTitle;
				if (simpleTitles)
					plotTitle = domains.get(0)
							+ PlotConfig.customPlotDomainDelimiter + dist
							+ " (" + type + ")";
				else
					plotTitle = dist + " (" + type + ")";

				// create plot
				Plot p = new Plot(dstDir,
						PlotFilenames.getDistributionPlot(dist),
						PlotFilenames.getDistributionGnuplotScript(dist),
						plotTitle, data);

				// set quantities
				p.setSeriesDataQuantities(seriesDataQuantities);

				// disable datetime for distribution plot
				p.setPlotDateTime(false);

				// set nvl sort options
				p.setNodeValueListOrder(order);
				p.setNodeValueListOrderBy(orderBy);

				// add to plot list
				defaultPlots.add(p);
			}

			// generate cdf plots
			if (plotCdf) {
				// title
				String plotTitle = "CDF of ";
				if (simpleTitles)
					plotTitle += domains.get(0)
							+ PlotConfig.customPlotDomainDelimiter + dist
							+ " (" + type + ")";
				else
					plotTitle += dist + " (" + type + ")";

				Plot p = new Plot(dstDir,
						PlotFilenames.getDistributionCdfPlot(dist),
						PlotFilenames.getDistributionCdfGnuplotScript(dist),
						plotTitle, cdfData);
				// set quantities
				p.setSeriesDataQuantities(seriesDataQuantities);

				// disable datetime for distribution plot
				p.setPlotDateTime(false);

				// set as cdf
				p.setCdfPlot(true);

				// add to plot list
				defaultPlots.add(p);
			}
		}

		// create nvl plots
		for (int i = 0; i < nvlValues.size(); i++) {
			String nvl = nvlValues.get(i);
			int[] seriesDataQuantities = new int[seriesData.length];
			ArrayList<String> domains = nvlDomainsList.get(i);

			ArrayList<PlotData> dataList = new ArrayList<PlotData>();

			// simple titles
			boolean simpleTitles = false;
			if (domains.size() == 1)
				simpleTitles = true;

			// iterate over batches
			for (int j = 0; j < batches.length; j++) {
				long timestamp = Dir.getTimestamp(batches[j]);

				// iterate over series
				for (int k = 0; k < seriesData.length; k++) {
					// if no batch with the timestamp, continue
					if (!seriesTimestamps[k].contains(timestamp))
						continue;

					AggregatedBatch initBatch = initBatches[k];

					// iterate over domains that contain the value
					for (String d : domains) {
						String lineTitle;
						if (simpleTitles)
							lineTitle = seriesData[k].getName();
						else
							lineTitle = d + " (" + seriesData[k].getName()
									+ ")";
						if (initBatch.getMetrics().getNames().contains(d)) {
							if (initBatch.getMetrics().get(d).getNodeValues()
									.getNames().contains(nvl)) {
								// create "line" in plot for each batch
								dataList.add(PlotData
										.get(nvl, d, style, lineTitle + " @ "
												+ timestamps[j], type));
								if (j == 0)
									seriesDataQuantities[k]++;
							} else {
								Log.debug("Adding nodevaluelist'"
										+ nvl
										+ "' of domain '"
										+ d
										+ "', but nvl not found in init batch of series "
										+ seriesData[k].getName());
							}
						} else {
							Log.debug("Adding nodevaluelist '" + nvl
									+ "' but domain '" + d
									+ "' not found in init batch of series "
									+ seriesData[k].getName());
						}
					}
				}
			}

			// transform to array
			PlotData[] data = dataList.toArray(new PlotData[0]);

			// title
			String plotTitle;
			if (simpleTitles)
				plotTitle = domains.get(0)
						+ PlotConfig.customPlotDomainDelimiter + nvl + " ("
						+ type + ")";
			else
				plotTitle = nvl + " (" + type + ")";

			// create plot
			Plot p = new Plot(dstDir, PlotFilenames.getNodeValueListPlot(nvl),
					PlotFilenames.getNodeValueListGnuplotScript(nvl),
					plotTitle, data);

			// set quantities
			p.setSeriesDataQuantities(seriesDataQuantities);

			// disable datetime for nodevaluelist plot
			p.setPlotDateTime(false);

			// add to plot list
			defaultPlots.add(p);
		}

		// create default dist plots
		if (Config.getBoolean("DEFAULT_PLOTS_ENABLED")
				&& Config.getBoolean("DEFAULT_PLOT_DISTRIBUTIONS")) {
			HashMap<String, Boolean> printedValues = new HashMap<String, Boolean>();

			// log
			Log.info("Plotting default distribution plots:");

			// iterate over series
			for (int i = 0; i < seriesData.length; i++) {
				// series data quantities
				int[] seriesDataQuantities = new int[seriesData.length];
				seriesDataQuantities[i] = 1;

				AggregatedMetricList aMetrics = initBatches[i].getMetrics();
				// iterate over metrics
				for (String metric : aMetrics.getNames()) {
					AggregatedDistributionList dists = aMetrics.get(metric)
							.getDistributions();
					for (String dist : dists.getNames()) {
						// log
						if (!printedValues.containsKey(metric + dist)) {
							Log.info("\tplotting '" + metric
									+ PlotConfig.customPlotDomainDelimiter
									+ dist + "'");
							printedValues.put(metric + dist, true);
						}

						ArrayList<PlotData> lines = null;
						ArrayList<PlotData> linesCdf = null;

						if (plotDist)
							lines = new ArrayList<PlotData>();
						if (plotCdf)
							linesCdf = new ArrayList<PlotData>();

						// iterate over batches
						for (int j = 0; j < batches.length; j++) {
							long timestamp = Dir.getTimestamp(batches[j]);

							if (!seriesTimestamps[i].contains(timestamp))
								continue;

							// create lines
							String title = seriesData[i].getName() + " @ "
									+ timestamp;

							if (plotDist) {
								lines.add(PlotData.get(dist, metric, style,
										title, type));
							}
							if (plotCdf) {
								PlotData lineCdf = PlotData.get(dist, metric,
										style, title, type);
								lineCdf.setPlotAsCdf(true);
								linesCdf.add(lineCdf);
							}
						}

						// create normal plot
						if (plotDist) {
							PlotData[] data = lines.toArray(new PlotData[0]);

							// create plot
							Plot p = new Plot(
									dstDir,
									PlotFilenames.getDistributionPlot(metric,
											dist + "." + i),
									PlotFilenames.getDistributionGnuplotScript(
											metric, dist + "." + i),
									metric
											+ PlotConfig.customPlotDomainDelimiter
											+ dist + " (" + type + ")", data);

							// set data quantities
							p.setSeriesDataQuantities(seriesDataQuantities);

							// disable datetime
							p.setPlotDateTime(false);

							// add plot to list
							defaultPlots.add(p);

						}
						// create cdf plot
						if (plotCdf) {
							// create plot
							PlotData[] dataCdf = linesCdf
									.toArray(new PlotData[0]);
							Plot pCdf = new Plot(
									dstDir,
									PlotFilenames.getDistributionCdfPlot(
											metric, dist + "." + i),
									PlotFilenames
											.getDistributionCdfGnuplotScript(
													metric, dist + "." + i),
									"CDF of "
											+ metric
											+ PlotConfig.customPlotDomainDelimiter
											+ dist + " (" + type + ")", dataCdf);

							// set data quantities
							pCdf.setSeriesDataQuantities(seriesDataQuantities);
							// disable datetime
							pCdf.setPlotDateTime(false);
							// set cdf plot
							pCdf.setCdfPlot(true);
							// add plot to list
							defaultPlots.add(pCdf);
						}
					}
				}
			}
		}

		// create default nvl plots
		if (Config.getBoolean("DEFAULT_PLOTS_ENABLED")
				&& Config.getBoolean("DEFAULT_PLOT_NODEVALUELISTS")) {
			HashMap<String, Boolean> printedValues = new HashMap<String, Boolean>();

			// log
			Log.info("Plotting default nodevaluelist plots:");

			// iterate over series
			for (int i = 0; i < seriesData.length; i++) {
				// series data quantities
				int[] seriesDataQuantities = new int[seriesData.length];
				seriesDataQuantities[i] = 1;

				AggregatedMetricList aMetrics = initBatches[i].getMetrics();
				// iterate over metrics
				for (String metric : aMetrics.getNames()) {
					AggregatedNodeValueListList nvls = aMetrics.get(metric)
							.getNodeValues();
					for (String nvl : nvls.getNames()) {
						// log
						if (!printedValues.containsKey(metric + nvl)) {
							Log.info("\tplotting '" + metric
									+ PlotConfig.customPlotDomainDelimiter
									+ nvl + "'");
							printedValues.put(metric + nvl, true);
						}

						ArrayList<PlotData> lines = null;

						lines = new ArrayList<PlotData>();

						// iterate over batches
						for (int j = 0; j < batches.length; j++) {
							long timestamp = Dir.getTimestamp(batches[j]);

							if (!seriesTimestamps[i].contains(timestamp))
								continue;

							// create lines
							String title = seriesData[i].getName() + " @ "
									+ timestamp;

							lines.add(PlotData.get(nvl, metric, style, title,
									type));
						}

						// create normal plot
						PlotData[] data = lines.toArray(new PlotData[0]);

						// create plot
						Plot p = new Plot(dstDir,
								PlotFilenames.getNodeValueListPlot(metric, nvl
										+ "." + i),
								PlotFilenames.getNodeValueListGnuplotScript(
										metric, nvl + "." + i), metric
										+ PlotConfig.customPlotDomainDelimiter
										+ nvl + " (" + type + ")", data);

						// set data quantities
						p.setSeriesDataQuantities(seriesDataQuantities);

						// disable datetime
						p.setPlotDateTime(false);

						// set nvl sort options
						p.setNodeValueListOrder(order);
						p.setNodeValueListOrderBy(orderBy);

						// add plot to list
						defaultPlots.add(p);

					}
				}
			}
		}

		// write script headers
		for (Plot p : defaultPlots)
			p.writeScriptHeader();

		// read data batch by batch and add to plots
		for (int i = 0; i < batches.length; i++) {
			for (int j = 0; j < seriesData.length; j++) {
				long timestamp = Dir.getTimestamp(batches[i]);

				// if no batch with the timestamp, continue
				if (!seriesTimestamps[j].contains(timestamp))
					continue;

				AggregatedBatch tempBatch;
				String aggrDir = Dir.getAggregationDataDir(seriesData[j]
						.getDir());

				if (singleFile)
					tempBatch = AggregatedBatch.readFromSingleFile(aggrDir,
							timestamp, Dir.delimiter,
							BatchReadMode.readOnlyDistAndNvl);
				else
					tempBatch = AggregatedBatch.read(
							Dir.getBatchDataDir(aggrDir, timestamp), timestamp,
							BatchReadMode.readOnlyDistAndNvl);

				// append data to plots
				for (Plot p : defaultPlots) {
					// check how often the series is used in the plot
					for (int k = 0; k < p.getSeriesDataQuantity(j); k++) {
						// add data to plot
						p.addDataSequentially(tempBatch);
					}
				}

				// append data to custom plots
				for (Plot p : customPlots) {
					// check how often the series is used in the plot
					for (int k = 0; k < p.getSeriesDataQuantity(j); k++) {
						// add data to plot
						p.addDataSequentially(tempBatch);
					}
				}

				// free resources
				tempBatch = null;
				System.gc();
			}
		}

		// close and execute
		for (Plot p : defaultPlots) {
			p.close();
			p.execute();
		}

		for (Plot p : customPlots) {
			p.close();
			p.execute();
		}
	}

	/** Plots the single value plots for multiple series. */
	private static void plotSingleValuePlots(SeriesData[] seriesData,
			String dstDir, String[] batches, double[] timestamps,
			AggregatedBatch[] initBatches, boolean plotStatistics,
			ArrayList<PlotConfig> customStatisticPlots,
			boolean plotMetricValues,
			ArrayList<PlotConfig> customMetricValuePlots,
			boolean plotCustomValues, ArrayList<PlotConfig> customValuePlots,
			boolean plotRuntimes, ArrayList<PlotConfig> customRuntimePlots,
			boolean singleFile, PlotType type, PlotStyle style)
			throws IOException, InterruptedException {
		// lists of plots
		ArrayList<Plot> defaultPlots = new ArrayList<Plot>();
		ArrayList<Plot> plots = new ArrayList<Plot>();

		// generate statistic plots
		if (plotStatistics) {
			if (customStatisticPlots.size() > 0)
				Log.info("Plotting custom statistic plots:");

			// generate plots and add to customPlot List
			Plotting.generateCustomPlots(customStatisticPlots, plots, dstDir,
					seriesData, initBatches, style, type);
		}

		// generate custom metric value plots
		if (plotMetricValues) {
			if (customMetricValuePlots.size() > 0)
				Log.info("Plotting custom metric value plots:");

			// generate plots and add to customPlot List
			Plotting.generateCustomPlots(customMetricValuePlots, plots, dstDir,
					seriesData, initBatches, style, type);
		}

		// generate custom value plots
		if (plotCustomValues) {
			if (customValuePlots.size() > 0)
				Log.info("Plotting custom value plots:");

			// generate plots and add to customPlot list
			Plotting.generateCustomPlots(customValuePlots, plots, dstDir,
					seriesData, initBatches, style, type);
		}

		// generate runtime plots
		if (plotRuntimes) {
			if (customRuntimePlots.size() > 0)
				Log.info("Plotting custom runtime plots:");

			// generate plots and add to customPlot List
			Plotting.generateCustomPlots(customRuntimePlots, plots, dstDir,
					seriesData, initBatches, style, type);
		}

		// default plots
		if (Config.getBoolean("DEFAULT_PLOTS_ENABLED"))
			Plotting.generateMultiSeriesDefaultPlots(defaultPlots, dstDir,
					seriesData, initBatches, plotStatistics, plotMetricValues,
					plotRuntimes, style, type);

		// write script headers
		for (Plot p : plots)
			p.writeScriptHeader();
		for (Plot p : defaultPlots)
			p.writeScriptHeader();

		// add data to plots
		for (int i = 0; i < seriesData.length; i++) {
			SeriesData series = seriesData[i];
			String tempDir = Dir.getAggregationDataDir(series.getDir());

			// read single values
			AggregatedBatch[] batchData = new AggregatedBatch[batches.length];
			for (int j = 0; j < batches.length; j++) {
				long timestamp = Dir.getTimestamp(batches[j]);
				try {
					if (singleFile)
						batchData[j] = AggregatedBatch.readFromSingleFile(
								tempDir, timestamp, Dir.delimiter,
								BatchReadMode.readOnlySingleValues);
					else
						batchData[j] = AggregatedBatch.read(
								Dir.getBatchDataDir(tempDir, timestamp),
								timestamp, BatchReadMode.readOnlySingleValues);
				} catch (FileNotFoundException e) {
					batchData[j] = null;
				}
			}

			// add data to default plots
			for (Plot p : defaultPlots) {
				// check how often the series is used in the plot
				for (int j = 0; j < p.getSeriesDataQuantity(i); j++) {
					// add data to plot
					p.addDataSequentially(batchData);
				}
			}

			// add data to custom plots
			for (Plot p : plots) {
				// check how often the series is used in the plot
				for (int j = 0; j < p.getSeriesDataQuantity(i); j++) {
					// add data to plot
					p.addDataSequentially(batchData);
				}
			}
		}

		// close and execute
		for (Plot p : defaultPlots) {
			p.close();
			p.execute();
		}

		for (Plot p : plots) {
			p.close();
			p.execute();
		}
	}

	/**
	 * Generates custom plots from the given PlotConfig list and adds them to
	 * the Plot list.
	 * 
	 * @param plotConfigs
	 *            Input plot config list from which the plots will be created.
	 * @param customPlots
	 *            List of Plot-Objects to which the new generated plots will be
	 *            added.
	 * @param dstDir
	 *            Destination directory for the plots.
	 * @param seriesData
	 *            Array of SeriesData objects that will be plotted.
	 * @param initBatches
	 *            Array of init batches, one for each series data object.
	 * @param style
	 *            PlotStyle of the resulting plots.
	 * @param type
	 *            PlotType of the resulting plots.
	 * @throws IOException
	 *             Thrown by the writer created in the plots.
	 */
	private static void generateCustomPlots(ArrayList<PlotConfig> plotConfigs,
			ArrayList<Plot> customPlots, String dstDir,
			SeriesData[] seriesData, AggregatedBatch[] initBatches,
			PlotStyle style, PlotType type) throws IOException {
		for (int i = 0; i < plotConfigs.size(); i++) {
			Log.info("\tplotting '" + plotConfigs.get(i).getFilename() + "'");

			PlotConfig config = plotConfigs.get(i);
			String[] values = config.getValues();
			String[] domains = config.getDomains();

			// set flags for what to plot
			boolean plotNormal = false;
			boolean plotAsCdf = false;

			switch (config.getPlotAsCdf()) {
			case "true":
				plotAsCdf = true;
				break;
			case "false":
				plotNormal = true;
				break;
			case "both":
				plotNormal = true;
				plotAsCdf = true;
				break;
			}

			// series data quantities array
			int[] seriesDataQuantities = new int[seriesData.length];

			// plot data list, will contain "lines" of the plot
			ArrayList<PlotData> dataList = new ArrayList<PlotData>();

			// iterate over values
			for (int k = 0; k < values.length; k++) {
				String value = values[k];
				String domain = domains[k];

				// if function, add it only once
				if (domain.equals(PlotConfig.customPlotDomainFunction)) {
					// if function
					String[] functionSplit = value.split("=");
					if (functionSplit.length != 2) {
						Log.warn("wrong function syntax for '" + value + "'");
						continue;
					}
					dataList.add(PlotData.get(functionSplit[0].trim(),
							functionSplit[1].trim(), style, domain
									+ PlotConfig.customPlotDomainDelimiter
									+ value, PlotType.function));
					// if not function, iterate over series
				} else {
					// iterate over series
					for (int j = 0; j < seriesData.length; j++) {
						String title = value + " (" + seriesData[j].getName()
								+ ")";

						// iterate over values to be plotted
						if (domain
								.equals(PlotConfig.customPlotDomainExpression)) {
							// if expression
							String[] expressionSplit = value.split(":");
							if (expressionSplit.length != 2) {
								Log.warn("wrong expression syntax for '"
										+ value + "'");
								continue;
							}
							// parse name
							String exprName;
							if (expressionSplit[0].equals(""))
								exprName = expressionSplit[1];
							else
								exprName = expressionSplit[0];

							if (initBatches[j].contains(Plotting
									.getDomainFromExpression(value,
											config.getGeneralDomain()),
									Plotting.getValueFromExpression(value))) {
								dataList.add(new ExpressionData(
										exprName,
										expressionSplit[1],
										style,
										exprName.replace("$", "") + " ("
												+ seriesData[j].getName() + ")",
										config.getGeneralDomain()));
								seriesDataQuantities[j]++;
							}
						} else {
							// check if series contains value
							if (initBatches[j].contains(domain, value)) {
								dataList.add(PlotData.get(value, domain, style,
										title, type));
								seriesDataQuantities[j]++;
							}
						}
					}
				}
			}

			// transform datalist to array
			PlotData[] data = dataList.toArray(new PlotData[0]);
			String filename = config.getFilename();

			if (plotNormal) {
				// create plot
				Plot p = new Plot(dstDir, filename,
						PlotFilenames.getValuesGnuplotScript(filename),
						config.getTitle(), config, data);

				// set series data quantities
				p.setSeriesDataQuantities(seriesDataQuantities);

				// add to plot list
				customPlots.add(p);
			}
			if (plotAsCdf) {
				// create plot
				Plot p = new Plot(dstDir,
						PlotFilenames.getValuesPlotCDF(filename),
						PlotFilenames.getValuesGnuplotScriptCDF(filename),
						"CDF of " + config.getTitle(), config, data);

				// set as cdf plot
				p.setCdfPlot(true);

				// set series data quantities
				p.setSeriesDataQuantities(seriesDataQuantities);

				// add to plot list
				customPlots.add(p);
			}
		}
	}

	/** Returns the first value inside the expression. **/
	private static String getValueFromExpression(String expr) {
		String[] split = expr.split("\\$");
		for (int i = 0; i < split.length; i++) {
			if (split.length > 1) {
				String[] split2 = split[1]
						.split(PlotConfig.customPlotDomainDelimiter);
				if (split2.length > 1) {
					String value = "";
					for (int j = 1; j < split2.length; j++)
						value += split2[j];
					return value;
				}
				return split[1];
			}
		}
		return null;
	}

	/** Returns the domain of the first value inside the expression. **/
	private static String getDomainFromExpression(String expr,
			String generalDomain) {
		String[] split = expr.split("\\$");
		for (int i = 0; i < split.length; i++) {
			if (split.length > 1) {
				String[] split2 = split[1]
						.split(PlotConfig.customPlotDomainDelimiter);
				if (split2.length > 1) {
					return split2[0];
				} else {
					return generalDomain;
				}
			}
		}
		return null;
	}

	/**
	 * Generates default plots for the given SeriesData objects and adds them to
	 * the Plot list.
	 * 
	 * @param plotList
	 *            List of Plot-Objects to which the new generated plots will be
	 *            added.
	 * @param dstDir
	 *            Destination directory for the plots.
	 * @param seriesData
	 *            Array of SeriesData objects that will be plotted.
	 * @param initBatches
	 *            Array of init batches, one for each series data object.
	 * @param plotStatistics
	 *            Flag if statistics will be plotted.
	 * @param plotMetricValues
	 *            Flag if metric values will be plotted.
	 * @param plotRuntimes
	 *            Flag if runtimes will be plotted.
	 * @param style
	 *            PlotStyle of the resulting plots.
	 * @param type
	 *            PlotType of the resulting plots.
	 * @throws IOException
	 *             Thrown by the writer created in the plots.
	 */
	private static void generateMultiSeriesDefaultPlots(
			ArrayList<Plot> plotList, String dstDir, SeriesData[] seriesData,
			AggregatedBatch[] initBatches, boolean plotStatistics,
			boolean plotMetricValues, boolean plotRuntimes, PlotStyle style,
			PlotType type) throws IOException {
		// contains the names of values
		ArrayList<String> values = new ArrayList<String>();
		ArrayList<String> genRuntimeValues = new ArrayList<String>();
		ArrayList<String> metRuntimeValues = new ArrayList<String>();

		// contains for each value a list of domains
		ArrayList<ArrayList<String>> valuesDomainsList = new ArrayList<ArrayList<String>>();

		// contains an int which states how often a value occurs
		ArrayList<Integer> valuesOccurence = new ArrayList<Integer>();
		ArrayList<Integer> genRuntimeOccurence = new ArrayList<Integer>();
		ArrayList<Integer> metRuntimeOccurence = new ArrayList<Integer>();

		// printed flag
		boolean printed = false;

		// gather fixed values
		for (int i = 0; i < seriesData.length; i++) {
			// statistic values
			if (plotStatistics && Config.getBoolean("DEFAULT_PLOT_VALUES")) {
				AggregatedValueList aValues = initBatches[i].getValues();
				for (String value : aValues.getNames()) {
					if (!values.contains(value)) {
						// if value not present, add it and add new domain
						// list
						values.add(value);
						ArrayList<String> dList = new ArrayList<String>();
						dList.add(PlotConfig.customPlotDomainStatistics);
						valuesDomainsList.add(dList);
						valuesOccurence.add(1);
					} else {
						// if value present, add new domain to domain list
						int index = values.indexOf(value);
						ArrayList<String> domainList = valuesDomainsList
								.get(index);
						valuesOccurence.set(index,
								valuesOccurence.get(index) + 1);
						if (!domainList
								.contains(PlotConfig.customPlotDomainStatistics)) {
							domainList
									.add(PlotConfig.customPlotDomainStatistics);
						}
					}
				}
			}

			// combined metric value plots
			if (Config.getBoolean("DEFAULT_PLOTS_ENABLED")
					&& Config.getBoolean("DEFAULT_PLOT_COMBINED_METRIC_VALUES")) {
				// plot metric values
				if (plotMetricValues
						&& Config.getBoolean("DEFAULT_PLOT_METRIC_VALUES")) {
					AggregatedMetricList aMetrics = initBatches[i].getMetrics();
					for (String metric : aMetrics.getNames()) {
						AggregatedValueList aMetricValues = aMetrics
								.get(metric).getValues();
						for (String value : aMetricValues.getNames()) {
							if (!values.contains(value)) {
								// if value not present, add it and add new
								// domain
								// list
								values.add(value);
								ArrayList<String> dList = new ArrayList<String>();
								dList.add(metric);
								valuesDomainsList.add(dList);
								valuesOccurence.add(1);
							} else {
								// if value present, add new domain to domain
								// list
								int index = values.indexOf(value);
								ArrayList<String> domainList = valuesDomainsList
										.get(index);
								valuesOccurence.set(index,
										valuesOccurence.get(index) + 1);
								if (!domainList.contains(metric)) {
									domainList.add(metric);
								}
							}
						}
					}
				}
			}
			// runtimes
			if (plotRuntimes && Config.getBoolean("DEFAULT_PLOT_RUNTIMES")) {
				// general runtimes
				for (String runtime : initBatches[i].getGeneralRuntimes()
						.getNames()) {
					if (!genRuntimeValues.contains(runtime)) {
						genRuntimeValues.add(runtime);
						genRuntimeOccurence.add(1);
					} else {
						int index = genRuntimeValues.indexOf(runtime);
						genRuntimeOccurence.set(index,
								genRuntimeOccurence.get(index) + 1);
					}
				}

				// metric runtimes
				for (String runtime : initBatches[i].getMetricRuntimes()
						.getNames()) {
					if (!metRuntimeValues.contains(runtime)) {
						metRuntimeValues.add(runtime);
						metRuntimeOccurence.add(1);
					} else {
						int index = metRuntimeValues.indexOf(runtime);
						metRuntimeOccurence.set(index,
								metRuntimeOccurence.get(index) + 1);
					}

				}
			}
		}

		// create general runtime plots
		printed = false;
		for (int i = 0; i < genRuntimeValues.size(); i++) {
			if (!printed) {
				Log.info("Plotting default general runtimes:");
				printed = true;
			}

			String runtime = genRuntimeValues.get(i);
			PlotData[] data = new PlotData[genRuntimeOccurence.get(i)];
			int index = 0;
			int[] seriesDataQuantities = new int[seriesData.length];

			// iterate over series
			for (int j = 0; j < seriesData.length; j++) {
				AggregatedBatch initBatch = initBatches[j];
				if (initBatch.getGeneralRuntimes().getNames().contains(runtime)) {
					data[index] = PlotData.get(runtime,
							PlotConfig.customPlotDomainGeneralRuntimes, style,
							seriesData[j].getName(), type);
					seriesDataQuantities[j]++;
					index++;
				}
			}

			// log
			Log.info("\tplotting '" + runtime + "'");

			// create plot
			Plot p = new Plot(dstDir,
					PlotFilenames.getRuntimesMultiSeriesGnuplotFile(runtime),
					PlotFilenames.getRuntimesMultiSeriesGnuplotScript(runtime),
					runtime + " (" + type + ")", data);

			// set quantities
			p.setSeriesDataQuantities(seriesDataQuantities);

			// add to plot list
			plotList.add(p);
		}

		// create metric runtime plots
		printed = false;
		for (int i = 0; i < metRuntimeValues.size(); i++) {
			if (!printed) {
				Log.info("Plotting default metric runtimes:");
				printed = true;
			}
			String runtime = metRuntimeValues.get(i);
			PlotData[] data = new PlotData[metRuntimeOccurence.get(i)];
			int index = 0;
			int[] seriesDataQuantities = new int[seriesData.length];

			// iterate over series
			for (int j = 0; j < seriesData.length; j++) {
				AggregatedBatch initBatch = initBatches[j];

				if (initBatch.getMetricRuntimes().getNames().contains(runtime)) {
					data[index] = PlotData.get(runtime,
							PlotConfig.customPlotDomainMetricRuntimes, style,
							seriesData[j].getName(), type);
					seriesDataQuantities[j]++;
					index++;
				}
			}

			// log
			Log.info("\tplotting " + "'" + runtime + "'");

			// create plot
			Plot p = new Plot(dstDir,
					PlotFilenames.getRuntimesMultiSeriesGnuplotFile(runtime),
					PlotFilenames.getRuntimesMultiSeriesGnuplotScript(runtime),
					runtime + " (" + type + ")", data);

			// set quantities
			p.setSeriesDataQuantities(seriesDataQuantities);

			// add to plot list
			plotList.add(p);
		}

		// create default value plots
		printed = false;
		for (int i = 0; i < values.size(); i++) {
			if (!printed) {
				Log.info("Plotting default value plots: ");
				printed = true;
			}

			String value = values.get(i);
			PlotData[] valuePlotData = new PlotData[valuesOccurence.get(i)];
			int index = 0;
			int[] seriesDataQuantities = new int[seriesData.length];
			boolean simpleTitles = false;
			ArrayList<String> domains = valuesDomainsList.get(i);

			// if only one domain enable simple titles
			if (domains.size() == 1)
				simpleTitles = true;

			// iterate over series
			for (int j = 0; j < seriesData.length; j++) {
				AggregatedBatch initBatch = initBatches[j];

				// iterate over domains that contain the value
				for (String d : domains) {
					String lineTitle;
					if (simpleTitles) {
						lineTitle = seriesData[j].getName();
					} else {
						if (d.equals(PlotConfig.customPlotDomainStatistics))
							lineTitle = value + " (" + seriesData[j].getName()
									+ ")";
						else
							lineTitle = d
									+ PlotConfig.customPlotDomainDelimiter
									+ value + " (" + seriesData[j].getName()
									+ ")";
					}

					// check if series j contains value as a statistic
					if (d.equals(PlotConfig.customPlotDomainStatistics)) {
						if (initBatch.getValues().getNames().contains(value)) {
							valuePlotData[index] = PlotData.get(value, d,
									style, lineTitle, type);
							seriesDataQuantities[j]++;
							index++;
						}
					}

					// check if series j contains value in metric d
					if (initBatch.getMetrics().getNames().contains(d)) {
						if (initBatch.getMetrics().get(d).getValues()
								.getNames().contains(value)) {
							valuePlotData[index] = PlotData.get(value, d,
									style, lineTitle, type);
							seriesDataQuantities[j]++;
							index++;
						}
					}
				}
			}

			// title
			String plotTitle;
			if (simpleTitles) {
				if (domains.get(0)
						.equals(PlotConfig.customPlotDomainStatistics))
					plotTitle = value + " (" + type + ")";
				else
					plotTitle = domains.get(0)
							+ PlotConfig.customPlotDomainDelimiter + value
							+ " (" + type + ")";
			} else {
				plotTitle = value + " (" + type + ")";
			}

			// log
			Log.info("\tplotting " + "'" + value + "'");

			// create plot
			Plot p = new Plot(dstDir, PlotFilenames.getValuesPlot(value),
					PlotFilenames.getValuesGnuplotScript(value), plotTitle,
					valuePlotData);

			// set quantities
			p.setSeriesDataQuantities(seriesDataQuantities);

			// add to plot list
			plotList.add(p);
		}

		HashMap<String, Boolean> printedValues = new HashMap<String, Boolean>();

		// create default metric plots
		if (plotMetricValues && Config.getBoolean("DEFAULT_PLOT_METRIC_VALUES")) {
			// log
			Log.info("Plotting default metric value plots:");

			// iterate over series
			for (int i = 0; i < seriesData.length; i++) {
				AggregatedMetricList aMetrics = initBatches[i].getMetrics();
				// iterate over metrics
				for (String metric : aMetrics.getNames()) {
					AggregatedValueList aMetricValues = aMetrics.get(metric)
							.getValues();
					// iterate over values
					for (String value : aMetricValues.getNames()) {
						// create plot data
						PlotData[] data = { PlotData.get(value, metric, style,
								seriesData[i].getName(), type) };
						int[] seriesDataQuantities = new int[seriesData.length];
						seriesDataQuantities[i] = 1;

						// log
						if (!printedValues.containsKey(metric + value)) {
							Log.info("\tplotting " + "'" + metric
									+ PlotConfig.customPlotDomainDelimiter
									+ value + "'");
							printedValues.put(metric + value, true);
						}

						// create plot
						Plot p = new Plot(dstDir, PlotFilenames.getValuesPlot(
								metric, value + "." + i),
								PlotFilenames.getValuesGnuplotScript(metric,
										value + "." + i), metric
										+ PlotConfig.customPlotDomainDelimiter
										+ value + " (" + type + ")", data);

						// set quantities
						p.setSeriesDataQuantities(seriesDataQuantities);

						// add to plot list
						plotList.add(p);
					}
				}
			}
		}
	}

	/**
	 * Plots a single series.
	 * 
	 * @param series
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory.
	 * @param config
	 *            PlottingConfig controlling the plot.
	 * @throws IOException
	 *             Thrown by the writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	private static void plotFromToSingleSeries(SeriesData series,
			String dstDir, PlottingConfig config) throws IOException,
			InterruptedException {
		// read values from config
		long timestampFrom = config.getTimestampFrom();
		long timestampTo = config.getTimestampTo();
		long stepsize = config.getStepsize();
		PlotType type = config.getPlotType();
		PlotStyle style = config.getPlotStyle();
		NodeValueListOrder order = config.getNvlOrder();
		NodeValueListOrderBy orderBy = config.getNvlOrderBy();
		DistributionPlotType distPlotType = config.getDistPlotType();
		String title = series.getName();
		boolean singleFile = Config.getBoolean("GENERATION_BATCHES_AS_ZIP");
		boolean plotDistributions = config.isPlotDistributions();
		boolean plotNodeValues = config.isPlotNodeValueLists();

		// create dir
		(new File(dstDir)).mkdirs();

		// gather relevant batches
		String tempDir = Dir.getAggregationDataDir(series.getDir());
		String[] batches = Dir.getBatchesFromTo(tempDir, timestampFrom,
				timestampTo, stepsize);
		double timestamps[] = new double[batches.length];
		for (int i = 0; i < batches.length; i++) {
			timestamps[i] = Dir.getTimestamp(batches[i]);
		}

		// read single values
		AggregatedBatch[] batchData = new AggregatedBatch[batches.length];
		for (int i = 0; i < batches.length; i++) {
			long timestamp = Dir.getTimestamp(batches[i]);
			if (singleFile)
				batchData[i] = AggregatedBatch.readFromSingleFile(tempDir,
						timestamp, Dir.delimiter,
						BatchReadMode.readOnlySingleValues);
			else
				batchData[i] = AggregatedBatch.read(
						Dir.getBatchDataDir(tempDir, timestamp), timestamp,
						BatchReadMode.readOnlySingleValues);
		}

		// list relevant batches
		Log.infoSep();
		Log.info("Plotting batches:");
		for (int i = 0; i < batches.length && i <= 3; i++) {
			Log.info("\t" + batches[i]);
		}
		if (batches.length > 3) {
			Log.info("\t...");
			Log.info("\t" + batches[batches.length - 1]);
		}

		// generate gnuplot script files
		AggregatedBatch initBatch = batchData[0];

		// replace wildcards and remove unnecessary plots
		config.checkCustomPlotConfigs(new AggregatedBatch[] { initBatch });

		// plot statistics
		if (config.isPlotStatistics()) {
			// plot custom statistic plots
			if (config.getCustomStatisticPlots() != null) {
				if (config.getCustomStatisticPlots().size() > 0) {
					Log.infoSep();
					Log.info("Plotting Custom-Statistic-Plots:");
					Plotting.plotCustomValuePlots(batchData,
							config.getCustomStatisticPlots(), dstDir, title,
							style, type);
				}
			}
		}

		// plot custom value plots
		if (config.isPlotCustomValues()) {
			Log.infoSep();
			Log.info("Plotting Custom-Value-Plots:");
			Plotting.plotCustomValuePlots(batchData,
					config.getCustomValuePlots(), dstDir, title, style, type);
		}

		// plot runtimes
		if (config.isPlotRuntimes()) {
			// plot custom runtimes
			Plotting.plotCustomRuntimes(batchData,
					config.getCustomRuntimePlots(), dstDir, title, style, type);
		}

		// plot metric values
		if (config.isPlotMetricValues()) {
			Plotting.plotMetricValues(batchData, initBatch.getMetrics(),
					dstDir, title, style, type);

			// plot custom metric value plots
			if (config.getCustomMetricValuePlots() != null) {
				if (config.getCustomMetricValuePlots().size() > 0) {
					Log.infoSep();
					Log.info("Plotting Custom-MetricValue-Plots:");
					Plotting.plotCustomValuePlots(batchData,
							config.getCustomMetricValuePlots(), dstDir, title,
							style, type);
				}
			}
		}

		// all at-once plots finished
		// print memory usage
		double mem1 = new Memory().getUsed();
		Log.infoSep();
		Log.info("");
		Log.info("Finished first plotting attempt");
		Log.info("\tused memory: " + mem1);
		Log.info("");
		Log.info("Erasing unsused data");

		// free resources
		batchData = null;
		System.gc();

		// print memory usage after resoruce freeing
		double mem2 = new Memory().getUsed();
		Log.info("\tremoved: " + (mem1 - mem2));
		Log.info("\tused memory (new): " + mem2);
		Log.info("");

		// plot distributions
		if (plotDistributions || plotNodeValues)
			Plotting.plotDistributionsAndNodeValues(plotDistributions,
					plotNodeValues, initBatch, batches, timestamps,
					config.getCustomDistributionPlots(),
					config.getCustomNodeValueListPlots(), tempDir, dstDir,
					title, style, type, distPlotType, order, orderBy);
	}

	/**
	 * Plots the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param config
	 *            PlottingConfig to control plotting behaviour.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plot(SeriesData[] seriesData, String dstDir,
			PlottingConfig config) throws IOException, InterruptedException {
		Plotting.plotFromTo(seriesData, dstDir, config);
	}

	/**
	 * Plots the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param config
	 *            PlottingConfig to control plotting behaviour.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plot(SeriesData seriesData, String dstDir,
			PlottingConfig config) throws IOException, InterruptedException {
		Plotting.plotFromTo(new SeriesData[] { seriesData }, dstDir, config);
	}

	/**
	 * Plots the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param timestampFrom
	 *            Beginning of the timestamp interval to be plotted.
	 * @param timestampTo
	 *            Ending of the timestamp interval to be plotted.
	 * @param stepsize
	 *            Stepsize of the batches to be plotted.
	 * @param flags
	 *            Flags that define which will be plotted.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotFromTo(SeriesData[] seriesData, String dstDir,
			long timestampFrom, long timestampTo, long stepsize,
			PlotFlag... flags) throws IOException, InterruptedException {
		// craft config
		PlottingConfig config = new PlottingConfig(flags);
		config.setPlotInterval(timestampFrom, timestampTo, stepsize);

		// call plotting method
		Plotting.plotFromTo(seriesData, dstDir, config);
	}

	/**
	 * Plots the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param timestampFrom
	 *            Beginning of the timestamp interval to be plotted.
	 * @param timestampTo
	 *            Ending of the timestamp interval to be plotted.
	 * @param stepsize
	 *            Stepsize of the batches to be plotted.
	 * @param flags
	 *            Flags that define which will be plotted.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotFromTo(SeriesData seriesData, String dstDir,
			long timestampFrom, long timestampTo, long stepsize,
			PlotFlag... flags) throws IOException, InterruptedException {
		Plotting.plotFromTo(new SeriesData[] { seriesData }, dstDir,
				timestampFrom, timestampTo, stepsize, flags);
	}

	/**
	 * Plots the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted
	 * @param dstDir
	 *            Destination directory of the plots
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plot(SeriesData[] seriesData, String dstDir)
			throws IOException, InterruptedException {
		// craft config
		PlottingConfig config = new PlottingConfig(PlotFlag.plotAll);

		// call plotting method
		Plotting.plotFromTo(seriesData, dstDir, config);
	}

	/**
	 * Plots the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted
	 * @param dstDir
	 *            Destination directory of the plots
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plot(SeriesData seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir);
	}

	/**
	 * Plots the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted
	 * @param dstDir
	 *            Destination directory of the plots
	 * @param flags
	 *            Flags that define which will be plotted.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plot(SeriesData[] seriesData, String dstDir,
			PlotFlag... flags) throws IOException, InterruptedException {
		// craft config
		PlottingConfig config = new PlottingConfig(flags);

		// call plotting method
		Plotting.plotFromTo(seriesData, dstDir, config);
	}

	/**
	 * Plots the series to the destination dir.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @param flags
	 *            Flags that define which will be plotted.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plot(SeriesData seriesData, String dstDir,
			PlotFlag... flags) throws IOException, InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir, flags);
	}

	/**
	 * Plots only the statistics of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotStatistic(SeriesData[] seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(seriesData, dstDir, PlotFlag.plotStatistics);
	}

	/**
	 * Plots only the statistics of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotStatistic(SeriesData seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir,
				PlotFlag.plotStatistics);
	}

	/**
	 * Plots only the metric values of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotMetricValues(SeriesData[] seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(seriesData, dstDir, PlotFlag.plotMetricValues);
	}

	/**
	 * Plots only the metric values of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotMetricValues(SeriesData seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir,
				PlotFlag.plotMetricValues);
	}

	/**
	 * Plots only the custom values plots on the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotCustomValuePlots(SeriesData[] seriesData,
			String dstDir) throws IOException, InterruptedException {
		Plotting.plot(seriesData, dstDir, PlotFlag.plotCustomValues);
	}

	/**
	 * Plots only the custom value plots on the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotCustomValuePlots(SeriesData seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir,
				PlotFlag.plotCustomValues);
	}

	/**
	 * Plots only the runtimes of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotRuntimes(SeriesData[] seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(seriesData, dstDir, PlotFlag.plotRuntimes);
	}

	/**
	 * Plots only the runtimes of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotRuntimes(SeriesData seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir,
				PlotFlag.plotRuntimes);
	}

	/**
	 * Plots only the distributions of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotDistributions(SeriesData[] seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(seriesData, dstDir, PlotFlag.plotDistributions);
	}

	/**
	 * Plots only the distributions of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotDistributions(SeriesData seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir,
				PlotFlag.plotDistributions);
	}

	/**
	 * Plots only the nodevaluelists of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotNodeValueLists(SeriesData[] seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(seriesData, dstDir, PlotFlag.plotNodeValueLists);
	}

	/**
	 * Plots only the nodevaluelists of the given series.
	 * 
	 * @param seriesData
	 *            SeriesData to be plotted.
	 * @param dstDir
	 *            Destination directory of the plots.
	 * @throws IOException
	 *             Thrown by writer.
	 * @throws InterruptedException
	 *             Thrown by executing gnuplot.
	 */
	public static void plotNodeValueLists(SeriesData seriesData, String dstDir)
			throws IOException, InterruptedException {
		Plotting.plot(new SeriesData[] { seriesData }, dstDir,
				PlotFlag.plotNodeValueLists);
	}

	/** Plots custom value plots **/
	private static void plotCustomValuePlots(AggregatedBatch[] batchData,
			ArrayList<PlotConfig> customValuePlots, String dstDir,
			String title, PlotStyle style, PlotType type) throws IOException,
			InterruptedException {
		for (PlotConfig pc : customValuePlots) {
			String name = pc.getTitle();
			if (name == null)
				continue;

			Log.info("\tplotting '" + name + "'");
			String[] values = pc.getValues();
			String[] domains = pc.getDomains();

			// set flags for what to plot
			boolean plotNormal = false;
			boolean plotAsCdf = false;

			switch (pc.getPlotAsCdf()) {
			case "true":
				plotAsCdf = true;
				break;
			case "false":
				plotNormal = true;
				break;
			case "both":
				plotNormal = true;
				plotAsCdf = true;
				break;
			}

			// gather plot data
			PlotData[] data = new PlotData[values.length];
			for (int j = 0; j < values.length; j++) {
				String value = values[j];
				String domain = domains[j];

				// check if function
				if (domain.equals(PlotConfig.customPlotDomainFunction)) {
					String[] functionSplit = value.split("=");
					if (functionSplit.length != 2) {
						Log.warn("wrong function syntax for '" + value + "'");
						continue;
					}
					data[j] = PlotData.get(functionSplit[0].trim(),
							functionSplit[1].trim(), style, domain
									+ PlotConfig.customPlotDomainDelimiter
									+ value, PlotType.function);
				} else if (domain.equals(PlotConfig.customPlotDomainExpression)) {
					// if expression
					String[] expressionSplit = value.split(":");
					if (expressionSplit.length != 2) {
						Log.warn("wrong expression syntax for '" + value + "'");
						continue;
					}
					// parse name
					String exprName;
					if (expressionSplit[0].equals(""))
						exprName = expressionSplit[1];
					else
						exprName = expressionSplit[0];
					data[j] = new ExpressionData(exprName, expressionSplit[1],
							style, exprName.replace("$", ""),
							pc.getGeneralDomain());
				} else {
					data[j] = PlotData.get(value, domain, style, value, type);
				}
			}

			// get filename
			String filename = PlotFilenames.getValuesPlot(name);
			if (pc.getFilename() != null) {
				filename = pc.getFilename();
			}

			// normal plot
			if (plotNormal) {
				// create plot
				Plot p = new Plot(dstDir, filename,
						PlotFilenames.getValuesGnuplotScript(filename), name
								+ " (" + type + ")", pc, data);

				// write script header
				p.writeScriptHeader();

				// add data
				p.addData(batchData);

				// close and execute
				p.close();
				p.execute();
			}

			// cdf plot
			if (plotAsCdf) {
				// create plot
				Plot p = new Plot(dstDir,
						PlotFilenames.getValuesPlotCDF(filename),
						PlotFilenames.getValuesGnuplotScriptCDF(filename), name
								+ " (" + type + ")", pc, data);

				// set as cdf
				p.setCdfPlot(true);

				// write script header
				p.writeScriptHeader();

				// add data
				p.addDataFromValuesAsCDF(batchData);

				// close and execute
				p.close();
				p.execute();
			}
		}
	}

	/** Plots Distributions and NodeValueLists **/
	private static void plotDistributionsAndNodeValues(
			boolean plotDistributions, boolean plotNodeValues,
			AggregatedBatch initBatch, String[] batches, double[] timestamps,
			ArrayList<PlotConfig> customDistributionPlots,
			ArrayList<PlotConfig> customNodeValueListPlots, String aggrDir,
			String dstDir, String title, PlotStyle style, PlotType type,
			DistributionPlotType distPlotType, NodeValueListOrder order,
			NodeValueListOrderBy orderBy) throws IOException,
			InterruptedException {
		boolean singleFile = Config.getBoolean("GENERATION_BATCHES_AS_ZIP");
		Log.infoSep();
		Log.info("Sequentially plotting Distributions and / or NodeValueLists");
		Log.info("");

		// generate plots
		List<Plot> plots = new LinkedList<Plot>();

		// iterate over metrics and create plots
		for (AggregatedMetric m : initBatch.getMetrics().getList()) {
			String metric = m.getName();
			Log.infoSep();
			Log.info("Plotting metric " + metric);

			// generate distribution plots
			if (plotDistributions && Config.getBoolean("DEFAULT_PLOTS_ENABLED")
					&& Config.getBoolean("DEFAULT_PLOT_DISTRIBUTIONS")) {
				for (AggregatedDistribution d : m.getDistributions().getList()) {
					String distribution = d.getName();
					Log.info("\tplotting distribution '" + distribution + "'");

					// check what to plot
					boolean plotDist = false;
					boolean plotCdf = false;
					switch (distPlotType) {
					case distOnly:
						plotDist = true;
						break;
					case cdfOnly:
						plotCdf = true;
						break;
					case distANDcdf:
						plotDist = true;
						plotCdf = true;
						break;
					}

					// generate normal plots
					if (plotDist) {
						PlotData[] dPlotData = new PlotData[batches.length];
						for (int i = 0; i < batches.length; i++) {
							dPlotData[i] = PlotData.get(distribution, metric,
									style, title + " @ " + timestamps[i], type);
						}
						Plot p = new Plot(dstDir,
								PlotFilenames.getDistributionPlot(metric,
										distribution),
								PlotFilenames.getDistributionGnuplotScript(
										metric, distribution), distribution
										+ " (" + type + ")", dPlotData);

						// disable datetime for distribution plot
						p.setPlotDateTime(false);

						// add to plots
						plots.add(p);
					}

					// generate cdf plots
					if (plotCdf) {
						PlotData[] dPlotDataCDF = new PlotData[batches.length];
						for (int i = 0; i < batches.length; i++) {
							PlotData cdfPlotData = PlotData.get(distribution,
									metric, style, title + " @ "
											+ timestamps[i], type);
							cdfPlotData.setPlotAsCdf(true);
							dPlotDataCDF[i] = cdfPlotData;
						}
						Plot p = new Plot(dstDir,
								PlotFilenames.getDistributionCdfPlot(metric,
										distribution),
								PlotFilenames.getDistributionCdfGnuplotScript(
										metric, distribution), "CDF of "
										+ distribution + " (" + type + ")",
								dPlotDataCDF);

						// set cdf
						p.setCdfPlot(true);

						// disable datetime for distribution plot
						p.setPlotDateTime(false);

						// add to plots
						plots.add(p);
					}
				}
			}

			// generate nodevaluelist plots
			if (plotNodeValues && Config.getBoolean("DEFAULT_PLOTS_ENABLED")
					&& Config.getBoolean("DEFAULT_PLOT_NODEVALUELISTS")) {
				for (AggregatedNodeValueList n : m.getNodeValues().getList()) {
					String nodevaluelist = n.getName();
					Log.info("\tplotting nodevaluelist '" + nodevaluelist + "'");

					// generate normal plots
					PlotData[] nPlotData = new PlotData[batches.length];
					for (int i = 0; i < batches.length; i++) {
						PlotData plotData = PlotData.get(nodevaluelist, metric,
								style, title + " @ " + timestamps[i], type);
						nPlotData[i] = plotData;
					}

					Plot nPlot = new Plot(dstDir,
							PlotFilenames.getNodeValueListPlot(metric,
									nodevaluelist),
							PlotFilenames.getNodeValueListGnuplotScript(metric,
									nodevaluelist), nodevaluelist + " (" + type
									+ ")", nPlotData);

					// disable datetime for nodevaluelist plot
					nPlot.setPlotDateTime(false);

					// set nvl sort options
					nPlot.setNodeValueListOrder(order);
					nPlot.setNodeValueListOrderBy(orderBy);

					// add to plots
					plots.add(nPlot);
				}
			}
		}

		// generate custom distribution plots
		if (customDistributionPlots != null) {
			if (!customDistributionPlots.isEmpty()) {
				Log.infoSep();
				Log.info("Plotting Custom-Distribution-Plots:");
				for (PlotConfig pc : customDistributionPlots) {
					String name = pc.getTitle();
					if (name == null)
						continue;

					Log.info("\tplotting '" + name + "'");

					// check for invalid values
					String[] tempValues = pc.getValues();
					String[] tempDomains = pc.getDomains();
					ArrayList<String> valuesList = new ArrayList<String>();
					ArrayList<String> domainsList = new ArrayList<String>();
					ArrayList<String> functionsList = new ArrayList<String>();

					for (int i = 0; i < tempValues.length; i++) {
						String v = tempValues[i];
						String d = tempDomains[i];

						// check if invalid value
						if (d.equals(PlotConfig.customPlotDomainStatistics)
								|| d.equals(PlotConfig.customPlotDomainRuntimes)) {
							Log.warn("invalid value '" + tempDomains[i]
									+ PlotConfig.customPlotDomainDelimiter
									+ tempValues[i]
									+ "' in distribution plot '" + name + "'");
						} else if (d
								.equals(PlotConfig.customPlotDomainFunction)) {
							// check if function
							functionsList.add(v);
						} else {
							valuesList.add(v);
							domainsList.add(d);
						}
					}

					// only take over valid values
					String[] values = valuesList.toArray(new String[0]);
					String[] domains = domainsList.toArray(new String[0]);

					int valuesCount = values.length;

					// check what to plot
					boolean plotDist = false;
					boolean plotCdf = false;

					if (pc.getDistPlotType() != null) {
						switch (pc.getDistPlotType()) {
						case distOnly:
							plotDist = true;
							break;
						case cdfOnly:
							plotCdf = true;
							break;
						case distANDcdf:
							plotDist = true;
							plotCdf = true;
							break;
						}
					} else {
						plotDist = true;
					}

					// gather plot data
					PlotData[] data = null;
					PlotData[] dataCdf = null;

					if (plotDist)
						data = new PlotData[valuesCount * batches.length
								+ functionsList.size()];
					if (plotCdf)
						dataCdf = new PlotData[valuesCount * batches.length
								+ functionsList.size()];

					// gather plot data
					// example: distributions d1, d2
					// -> data[] = { d1(0), d2(0), d1(1), d2(1), ... }
					// where d1(x) is the plotdata of d1 at timestamp x
					for (int i = 0; i < batches.length; i++) {
						for (int j = 0; j < valuesCount; j++) {
							if (plotDist)
								data[i * valuesCount + j] = PlotData
										.get(values[j],
												domains[j],
												style,
												domains[j]
														+ PlotConfig.customPlotDomainDelimiter
														+ values[j] + " @ "
														+ timestamps[i], type);
							if (plotCdf) {
								PlotData dCdf = PlotData
										.get(values[j],
												domains[j],
												style,
												domains[j]
														+ PlotConfig.customPlotDomainDelimiter
														+ values[j] + " @ "
														+ timestamps[i], type);
								dCdf.setPlotAsCdf(true);
								dataCdf[i * valuesCount + j] = dCdf;
							}
						}
					}

					// add function datas
					int offset = batches.length * valuesCount;
					for (int i = 0; i < functionsList.size(); i++) {
						String f = functionsList.get(i);
						String[] functionSplit = f.split("=");
						if (functionSplit.length != 2) {
							Log.warn("wrong function syntax for " + f);
							continue;
						}
						if (plotDist)
							data[offset + i] = PlotData.get(functionSplit[0],
									functionSplit[1], style, title,
									PlotType.function);
						if (plotCdf)
							dataCdf[offset + i] = PlotData.get(
									functionSplit[0], functionSplit[1], style,
									title, PlotType.function);
					}

					// get filename
					String filename = name;
					if (pc.getFilename() != null) {
						filename = pc.getFilename();
					}

					// create normal plot
					if (plotDist) {
						Plot p = new Plot(
								dstDir,
								PlotFilenames.getDistributionPlot(filename),
								PlotFilenames
										.getDistributionGnuplotScript(filename),
								name + " (" + type + ")", pc, data);

						// set data quantity
						p.setDataQuantity(values.length);

						// disable datetime for distribution plot
						p.setPlotDateTime(false);

						// add to plots
						plots.add(p);
					}

					// create cdf plot
					if (plotCdf) {
						Plot pCdf = new Plot(
								dstDir,
								PlotFilenames.getDistributionCdfPlot(filename),
								PlotFilenames
										.getDistributionCdfGnuplotScript(filename),
								"CDF of " + name + " (" + type + ")", pc,
								dataCdf);

						// set cdf plot
						pCdf.setCdfPlot(true);
						// set data quantity
						pCdf.setDataQuantity(values.length);

						// disable datetime for distribution plot
						pCdf.setPlotDateTime(false);

						// add to plots
						plots.add(pCdf);
					}
				}
			}
		}

		// generate custom nodevaluelist plots
		if (customNodeValueListPlots != null) {
			if (!customNodeValueListPlots.isEmpty()) {
				Log.infoSep();
				Log.info("Plotting Custom-NodeValueList-Plots:");
				for (PlotConfig pc : customNodeValueListPlots) {
					String name = pc.getTitle();
					if (name == null)
						continue;

					Log.info("\tplotting '" + name + "'");

					// check for invalid values
					String[] tempValues = pc.getValues();
					String[] tempDomains = pc.getDomains();
					ArrayList<String> valuesList = new ArrayList<String>();
					ArrayList<String> domainsList = new ArrayList<String>();
					ArrayList<String> functionsList = new ArrayList<String>();

					for (int i = 0; i < tempValues.length; i++) {
						String v = tempValues[i];
						String d = tempDomains[i];

						if (d.equals(PlotConfig.customPlotDomainStatistics)
								|| d.equals(PlotConfig.customPlotDomainRuntimes)) {
							Log.warn("invalid value '" + tempDomains[i]
									+ PlotConfig.customPlotDomainDelimiter
									+ tempValues[i]
									+ "' in distribution plot '" + name + "'");
						} else if (d
								.equals(PlotConfig.customPlotDomainFunction)) {
							// check if function
							functionsList.add(v);
						} else {
							valuesList.add(v);
							domainsList.add(d);
						}
					}

					// only take over valid values
					String[] values = valuesList.toArray(new String[0]);
					String[] domains = domainsList.toArray(new String[0]);

					int valuesCount = values.length;

					// gather plot data
					PlotData[] data = new PlotData[batches.length
							* values.length + functionsList.size()];

					// example: distributions d1, d2
					// -> data[] = { d1(0), d2(0), d1(1), d2(1), ... }
					// where d1(x) is the plotdata of d1 at timestamp x
					for (int i = 0; i < batches.length; i++) {
						for (int j = 0; j < valuesCount; j++) {
							data[i * valuesCount + j] = PlotData
									.get(values[j],
											domains[j],
											style,
											domains[j]
													+ PlotConfig.customPlotDomainDelimiter
													+ values[j] + " @ "
													+ timestamps[i], type);
						}
					}

					// add function datas
					int offset = batches.length * valuesCount;
					for (int i = 0; i < functionsList.size(); i++) {
						String f = functionsList.get(i);
						String[] functionSplit = f.split("=");
						if (functionSplit.length != 2) {
							Log.warn("wrong function syntax for " + f);
							continue;
						}
						data[offset + i] = PlotData.get(functionSplit[0],
								functionSplit[1], style, title,
								PlotType.function);
					}

					// get filename
					String filename = name;
					if (pc.getFilename() != null) {
						filename = pc.getFilename();
					}

					// create plot
					Plot p = new Plot(dstDir,
							PlotFilenames.getNodeValueListPlot(filename),
							PlotFilenames
									.getNodeValueListGnuplotScript(filename),
							name + " (" + type + ")", pc, data);

					// disable datetime for nodevaluelist plot
					p.setPlotDateTime(false);

					// set nvl sort options
					p.setNodeValueListOrder(pc.getOrder());
					p.setNodeValueListOrderBy(pc.getOrderBy());

					// add to plots
					plots.add(p);
				}
			}
		}

		// write headers
		for (Plot p : plots) {
			p.writeScriptHeader();
		}

		// read data batch by batch and add to plots
		for (int i = 0; i < batches.length; i++) {
			AggregatedBatch tempBatch;
			long timestamp = Dir.getTimestamp(batches[i]);

			if (singleFile)
				tempBatch = AggregatedBatch.readFromSingleFile(aggrDir,
						timestamp, Dir.delimiter,
						BatchReadMode.readOnlyDistAndNvl);
			else
				tempBatch = AggregatedBatch.read(
						Dir.getBatchDataDir(aggrDir, timestamp), timestamp,
						BatchReadMode.readOnlyDistAndNvl);

			// append data to plots
			for (Plot p : plots) {
				for (int j = 0; j < p.getDataQuantity(); j++) {
					p.addDataSequentially(tempBatch);
				}
			}

			// free resources
			tempBatch = null;
			System.gc();
		}

		// close and execute plot scripts
		for (Plot p : plots) {
			p.close();
			p.execute();
		}
	}

	/** Plot statistics **/
	private static void plotStatistics(AggregatedBatch[] batchData,
			AggregatedValueList values, String dstDir, String title,
			PlotStyle style, PlotType type) throws IOException,
			InterruptedException {
		Log.infoSep();
		Log.info("Plotting values:");
		for (String value : SeriesStats.statisticsToPlot) {
			if (values.getNames().contains(value)) {
				Log.info("\tplotting '" + value + "'");

				// get plot data
				PlotData valuePlotData = PlotData.get(value,
						PlotConfig.customPlotDomainStatistics, style, title,
						type);

				// create plot
				Plot valuePlot = new Plot(dstDir, PlotFilenames.getValuesPlot(
						Config.get("PREFIX_STATS_PLOT"), value),
						PlotFilenames.getValuesGnuplotScript(
								Config.get("PREFIX_STATS_PLOT"), value), value
								+ " (" + type + ")",
						new PlotData[] { valuePlotData });

				// write header
				valuePlot.writeScriptHeader();

				// append data
				valuePlot.addData(batchData);

				// close and execute
				valuePlot.close();
				valuePlot.execute();
			}
		}
	}

	/** Plots metric values **/
	private static void plotMetricValues(AggregatedBatch[] batchData,
			AggregatedMetricList metrics, String dstDir, String title,
			PlotStyle style, PlotType type) throws IOException,
			InterruptedException {

		// init list for plots
		List<Plot> plots = new LinkedList<Plot>();

		// generate single plots
		for (AggregatedMetric m : metrics.getList()) {
			String metric = m.getName();
			Log.infoSep();
			Log.info("Plotting metric " + metric);
			for (AggregatedValue v : m.getValues().getList()) {
				String value = v.getName();
				Log.info("\tplotting '" + value + "'");

				// get plot data
				PlotData valuePlotData = PlotData.get(value, m.getName(),
						style, metric, type);

				// create plot
				plots.add(new Plot(dstDir, PlotFilenames.getValuesPlot(metric,
						value), PlotFilenames.getValuesGnuplotScript(metric,
						value), value + " (" + type + ")",
						new PlotData[] { valuePlotData }));
			}
		}

		/*
		 * COMBINED PLOTS
		 */
		ArrayList<String> values = new ArrayList<String>();

		for (AggregatedMetric m : metrics.getList()) {
			for (AggregatedValue v : m.getValues().getList()) {
				if (!values.contains(v.getName()))
					values.add(v.getName());
			}
		}

		// list of values, which all have an own list of metrics
		ArrayList<ArrayList<String>> valuesList = new ArrayList<ArrayList<String>>(
				values.size());

		for (int i = 0; i < values.size(); i++) {
			valuesList.add(i, new ArrayList<String>());
		}

		// for each value add metric that has the value
		for (AggregatedMetric m : metrics.getList()) {
			for (AggregatedValue v : m.getValues().getList()) {
				int index = values.indexOf(v.getName());
				valuesList.get(index).add(m.getName());
			}
		}

		for (int i = 0; i < valuesList.size(); i++) {
			ArrayList<String> metricsList = valuesList.get(i);
			String value = values.get(i);
			if (metricsList.size() > 1) {
				// gather plot data
				PlotData[] valuePlotDatas = new PlotData[metricsList.size()];
				for (int j = 0; j < metricsList.size(); j++) {
					String metric = metricsList.get(j);
					valuePlotDatas[j] = PlotData.get(value, metric, style,
							metric, type);
				}

				// create plot
				plots.add(new Plot(dstDir, PlotFilenames
						.getCombinationPlot(value), PlotFilenames
						.getCombinationGnuplotScript(value), value + " ("
						+ type + ")", valuePlotDatas));
			}
		}

		for (Plot p : plots) {
			// write header
			p.writeScriptHeader();

			// append data
			p.addData(batchData);

			// close and execute
			p.close();
			p.execute();
		}
		plots = null;
	}

	/** Plots metric runtimes **/
	private static void plotMetricRuntimes(AggregatedBatch[] batchData,
			AggregatedRunTimeList metricRuntimes, String dstDir, String title,
			PlotStyle style, PlotType type) throws IOException,
			InterruptedException {
		Log.infoSep();
		Log.info("Plotting Metric-Runtimes:");

		PlotData[] metRuntimes = new PlotData[metricRuntimes.size()];
		int index = 0;

		// plot single runtime plots
		for (AggregatedValue met : metricRuntimes.getList()) {
			String runtime = met.getName();
			Log.info("\tplotting '" + runtime + "'");

			// get plot data
			PlotData metPlotData = PlotData.get(runtime,
					PlotConfig.customPlotDomainRuntimes, style, runtime + "-"
							+ title, type);
			metRuntimes[index] = metPlotData;

			// create plot
			Plot metRuntimeSinglePlot = new Plot(dstDir,
					PlotFilenames.getRuntimesMetricPlot(runtime),
					PlotFilenames.getRuntimesGnuplotScript(runtime), runtime
							+ " (" + type + ")", new PlotData[] { metPlotData });
			Plot metRuntimeSinglePlotCDF = new Plot(dstDir,
					PlotFilenames.getRuntimesMetricPlotCDF(runtime),
					PlotFilenames.getRuntimesGnuplotScriptCDF(runtime),
					"CDF of " + runtime + " (" + type + ")",
					new PlotData[] { metPlotData });

			// set cdf plot
			metRuntimeSinglePlotCDF.setCdfPlot(true);

			// write header
			metRuntimeSinglePlot.writeScriptHeader();
			metRuntimeSinglePlotCDF.writeScriptHeader();

			// append data
			metRuntimeSinglePlot.addData(batchData);
			metRuntimeSinglePlotCDF.addDataFromRuntimesAsCDF(batchData);

			// close and execute
			metRuntimeSinglePlot.close();
			metRuntimeSinglePlotCDF.close();

			metRuntimeSinglePlot.execute();
			metRuntimeSinglePlotCDF.execute();

			index++;
		}

		// create combined plots
		String metricRuntimeName = Config.get("PLOT_METRIC_RUNTIMES");
		Plot metricRuntimesPlot = new Plot(dstDir,
				PlotFilenames.getRuntimesStatisticPlot(metricRuntimeName),
				PlotFilenames.getRuntimesGnuplotScript(metricRuntimeName),
				metricRuntimeName + " runtimes (" + type + ")", metRuntimes);
		Plot metricRuntimesPlotCDF = new Plot(dstDir,
				PlotFilenames.getRuntimesStatisticPlotCDF(metricRuntimeName),
				PlotFilenames.getRuntimesGnuplotScriptCDF(metricRuntimeName),
				"CDF of " + metricRuntimeName + " runtimes (" + type + ")",
				metRuntimes);

		// set cdf plot
		metricRuntimesPlotCDF.setCdfPlot(true);

		// write headers
		metricRuntimesPlot.writeScriptHeader();
		metricRuntimesPlotCDF.writeScriptHeader();

		// add data to metric runtime plot
		metricRuntimesPlot.addData(batchData);

		// add cdf data to metric runtime cdf plot
		metricRuntimesPlotCDF.addDataFromRuntimesAsCDF(batchData);

		// close and execute
		metricRuntimesPlot.close();
		metricRuntimesPlot.execute();

		metricRuntimesPlotCDF.close();
		metricRuntimesPlotCDF.execute();
	}

	/** Plot general runtimes **/
	private static void plotGeneralRuntimes(AggregatedBatch[] batchData,
			ArrayList<String> y, String dstDir, String title, PlotStyle style,
			PlotType type) throws IOException, InterruptedException {
		Log.infoSep();
		Log.info("Plotting General-Runtimes:");
		PlotData[] genRuntimes = new PlotData[y.size()];
		int index = 0;
		for (String gen : y) {
			Log.info("\tplotting '" + gen + "'");
			genRuntimes[index] = PlotData.get(gen,
					PlotConfig.customPlotDomainRuntimes, style, gen + "-"
							+ title, type);
			index++;
		}

		// create plots
		String generalRuntimeName = Config.get("PLOT_GENERAL_RUNTIMES");
		Plot generalRuntimesPlot = new Plot(dstDir,
				PlotFilenames.getRuntimesStatisticPlot(generalRuntimeName),
				PlotFilenames.getRuntimesGnuplotScript(generalRuntimeName),
				generalRuntimeName + " runtimes (" + type + ")", genRuntimes);
		Plot generalRuntimesPlotCDF = new Plot(dstDir,
				PlotFilenames.getRuntimesStatisticPlotCDF(generalRuntimeName),
				PlotFilenames.getRuntimesGnuplotScriptCDF(generalRuntimeName),
				"CDF of " + generalRuntimeName + " runtimes (" + type + ")",
				genRuntimes);

		// set cdf plot
		generalRuntimesPlotCDF.setCdfPlot(true);

		// write headers
		generalRuntimesPlot.writeScriptHeader();
		generalRuntimesPlotCDF.writeScriptHeader();

		// add data to general runtime plot
		generalRuntimesPlot.addData(batchData);

		// add cdf data to general runtime cdf plot
		generalRuntimesPlotCDF.addDataFromRuntimesAsCDF(batchData);

		// close and execute
		generalRuntimesPlot.close();
		generalRuntimesPlot.execute();

		generalRuntimesPlotCDF.close();
		generalRuntimesPlotCDF.execute();
	}

	/** Plot custom runtime plots **/
	private static void plotCustomRuntimes(AggregatedBatch[] batchData,
			ArrayList<PlotConfig> customPlots, String dstDir, String title,
			PlotStyle style, PlotType type) throws IOException,
			InterruptedException {
		Log.infoSep();
		Log.info("Plotting Custom-Runtime-Plots:");
		for (PlotConfig pc : customPlots) {
			String name = pc.getTitle();
			if (name == null)
				continue;

			Log.info("\tplotting '" + name + "'");
			String[] values = pc.getValues();
			String[] domains = pc.getDomains();

			// set flags for what to plot
			boolean plotNormal = false;
			boolean plotAsCdf = false;

			switch (pc.getPlotAsCdf()) {
			case "true":
				plotAsCdf = true;
				break;
			case "false":
				plotNormal = true;
				break;
			case "both":
				plotNormal = true;
				plotAsCdf = true;
				break;
			}

			// get filename
			String plotFilename = PlotFilenames.getValuesPlot(name);
			if (pc.getFilename() != null) {
				plotFilename = pc.getFilename();
			}

			// gather plot data
			PlotData[] plotData = new PlotData[values.length];
			for (int i = 0; i < plotData.length; i++) {
				String value = values[i];
				String domain = domains[i];
				// check if function
				if (domain.equals(PlotConfig.customPlotDomainFunction)) {
					String[] functionSplit = value.split("=");
					if (functionSplit.length != 2) {
						Log.warn("wrong function syntax for " + value);
						continue;
					}
					plotData[i] = PlotData.get(functionSplit[0].trim(),
							functionSplit[1].trim(), style, domain
									+ PlotConfig.customPlotDomainDelimiter
									+ value, PlotType.function);
				} else if (domain.equals(PlotConfig.customPlotDomainExpression)) {
					// if expression
					String[] expressionSplit = value.split(":");
					if (expressionSplit.length != 2) {
						Log.warn("wrong expression syntax for '" + value + "'");
						continue;
					}
					// parse name
					String exprName;
					if (expressionSplit[0].equals(""))
						exprName = expressionSplit[1];
					else
						exprName = expressionSplit[0];
					plotData[i] = new ExpressionData(exprName,
							expressionSplit[1], style,
							exprName.replace("$", ""), pc.getGeneralDomain());
				} else {
					plotData[i] = PlotData.get(value, domain, style, value,
							type);
				}
			}

			// normal plot
			if (plotNormal) {
				// create plot
				Plot p = new Plot(dstDir, plotFilename,
						PlotFilenames.getRuntimesGnuplotScript(plotFilename),
						name + " (" + type + ")", pc, plotData);

				// write script header
				p.writeScriptHeader();

				// add data
				p.addData(batchData);

				// close and execute
				p.close();
				p.execute();
			}

			// cdf plot
			if (plotAsCdf) {
				// create plot
				Plot p = new Plot(
						dstDir,
						PlotFilenames.getRuntimesPlotFileCDF(plotFilename),
						PlotFilenames.getRuntimesGnuplotScriptCDF(plotFilename),
						"CDF of " + name + " (" + type + ")", pc, plotData);

				// write script header
				p.writeScriptHeader();

				// add data
				p.addDataFromRuntimesAsCDF(batchData);

				// close and execute
				p.close();
				p.execute();

			}
		}
	}

}
