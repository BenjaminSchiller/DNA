package dna.plot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import dna.io.ZipReader;
import dna.io.filesystem.Dir;
import dna.io.filesystem.Files;
import dna.io.filesystem.PlotFilenames;
import dna.plot.PlottingConfig.ValueSortMode;
import dna.plot.data.ExpressionData;
import dna.plot.data.PlotData;
import dna.plot.data.PlotData.DistributionPlotType;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
import dna.plot.data.PlotData.PlotDataLocation;
import dna.plot.data.PlotData.PlotStyle;
import dna.plot.data.PlotData.PlotType;
import dna.series.aggdata.AggregatedBatch;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.series.aggdata.AggregatedBinnedDistribution;
import dna.series.aggdata.AggregatedDistribution;
import dna.series.aggdata.AggregatedMetric;
import dna.series.data.BatchData;
import dna.series.data.IBatch;
import dna.series.data.MetricData;
import dna.series.data.SeriesData;
import dna.series.data.distr.Distr;
import dna.util.Config;
import dna.util.Log;

/**
 * Plotting class which holds static utility methods for plotting.
 * 
 * @author Rwilmes
 * @date 05.11.2014
 */
public class PlottingUtils {

	// internally used delimiter
	private static final String PLOTTING_UTILS_DELIMITER = "§§§";

	/** Returns the first value inside the expression. **/
	public static String getValueFromExpression(String expr) {
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
	public static String getDomainFromExpression(String expr,
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

	/** Replaces the domain-wildcard inside the expression and returns it. **/
	public static String replaceDomainWildcardInsideExpression(String expr,
			String domain) {
		// split at :
		String[] split = expr.split("\\:");
		String temp1 = split[0].replace("$*$", domain);

		// split at $
		String temp2 = "";
		String[] split2 = split[1].split("\\$");
		for (int i = 0; i < split2.length; i++) {
			if ((i & 1) == 0) {
				// even
				temp2 += split2[i];
			} else {
				// odd
				temp2 += "$"
						+ split2[i].replace(PlotConfig.customPlotWildcard,
								domain) + "$";
			}
		}

		// return
		return temp1 + ":" + temp2;
	}

	/**
	 * Checks if the given value of the given domain is contained in atleast on
	 * of the given batches. If yes, true is returned.
	 * 
	 * @param domain
	 *            Domain to be checked.
	 * @param value
	 *            Value to be checked.
	 * @param batches
	 *            Array of batches to be checked.
	 * @return True if domain and value is found in atleast one of the batches.
	 */
	public static boolean isContained(String domain, String value,
			BatchData[] batches) {
		boolean contained = false;

		// if domain or value null, return false
		if (domain == null || value == null)
			return false;

		// check if statistic
		if (domain.equals(PlotConfig.customPlotDomainStatistics)) {
			for (BatchData b : batches) {
				if (b.getValues().getNames().contains(value)) {
					contained = true;
					continue;
				}
			}
		}
		// check if general runtime
		if (domain.equals(PlotConfig.customPlotDomainRuntimes)
				|| domain.equals(PlotConfig.customPlotDomainGeneralRuntimes)) {
			for (BatchData b : batches) {
				if (b.getGeneralRuntimes().getNames().contains(value)) {
					contained = true;
					continue;
				}
			}
		}
		// check if metric runtime
		if (domain.equals(PlotConfig.customPlotDomainRuntimes)
				|| domain.equals(PlotConfig.customPlotDomainMetricRuntimes)) {
			for (BatchData b : batches) {
				if (b.getMetricRuntimes().getNames().contains(value)) {
					contained = true;
					continue;
				}
			}
		}
		// check if domain is a metric
		for (BatchData b : batches) {
			if (b.getMetrics().getNames().contains(domain)) {
				MetricData m = b.getMetrics().get(domain);

				// if value
				if (m.getValues().getNames().contains(value)) {
					contained = true;
					continue;
				}
				// if distribution
				if (m.getDistributions().getNames().contains(value)) {
					contained = true;
					continue;
				}
				// if nodevaluelist
				if (m.getNodeValues().getNames().contains(value)) {
					contained = true;
					continue;
				}
			}
		}

		return contained;
	}

	/**
	 * Checks if the given value of the given domain is contained in atleast on
	 * of the given batches. If yes, true is returned.
	 * 
	 * @param domain
	 *            Domain to be checked.
	 * @param value
	 *            Value to be checked.
	 * @param batches
	 *            Array of batches to be checked.
	 * @return True if domain and value is found in atleast one of the batches.
	 */
	public static boolean isContained(String domain, String value,
			AggregatedBatch[] batches) {
		boolean contained = false;

		// if domain or value null, return false
		if (domain == null || value == null)
			return false;

		// check if statistic
		if (domain.equals(PlotConfig.customPlotDomainStatistics)) {
			for (AggregatedBatch b : batches) {
				if (b.getValues().getNames().contains(value)) {
					contained = true;
					continue;
				}
			}
		}
		// check if general runtime
		if (domain.equals(PlotConfig.customPlotDomainRuntimes)
				|| domain.equals(PlotConfig.customPlotDomainGeneralRuntimes)) {
			for (AggregatedBatch b : batches) {
				if (b.getGeneralRuntimes().getNames().contains(value)) {
					contained = true;
					continue;
				}
			}
		}
		// check if metric runtime
		if (domain.equals(PlotConfig.customPlotDomainRuntimes)
				|| domain.equals(PlotConfig.customPlotDomainMetricRuntimes)) {
			for (AggregatedBatch b : batches) {
				if (b.getMetricRuntimes().getNames().contains(value)) {
					contained = true;
					continue;
				}
			}
		}
		// check if domain is a metric
		for (AggregatedBatch b : batches) {
			if (b.getMetrics().getNames().contains(domain)) {
				AggregatedMetric m = b.getMetrics().get(domain);

				// if value
				if (m.getValues().getNames().contains(value)) {
					contained = true;
					continue;
				}
				// if distribution
				if (m.getDistributions().getNames().contains(value)) {
					contained = true;
					continue;
				}
				// if nodevaluelist
				if (m.getNodeValues().getNames().contains(value)) {
					contained = true;
					continue;
				}
			}
		}

		return contained;
	}

	/** Checks if the value and domain are contained in the given batch. **/
	public static boolean isContained(String domain, String value,
			AggregatedBatch batch) {
		return PlottingUtils.isContained(domain, value,
				new AggregatedBatch[] { batch });
	}

	/** Checks if the value and domain are contained in the given batch. **/
	public static boolean isContained(String domain, String value,
			BatchData batch) {
		return PlottingUtils.isContained(domain, value,
				new BatchData[] { batch });
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
	 * @param indizes
	 *            Indizes of the runs, used for line naming.
	 * @param initBatches
	 *            Array of init batches, one for each series data object.
	 * @param style
	 *            PlotStyle of the resulting plots.
	 * @param type
	 *            PlotType of the resulting plots.
	 * @throws IOException
	 *             Thrown by the writer created in the plots.
	 */
	public static void generateCustomPlots(ArrayList<PlotConfig> plotConfigs,
			ArrayList<Plot> customPlots, String dstDir,
			SeriesData[] seriesData, int[] indizes, IBatch[] initBatches,
			PlotStyle style, PlotType type, ValueSortMode valueSortMode,
			String[] valueSortList, HashMap<Long, Long> timestampMap)
			throws IOException {
		// check if aggregated batches
		boolean aggregatedBatches = false;
		if (initBatches instanceof AggregatedBatch[])
			aggregatedBatches = true;

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
			for (int j = 0; j < seriesData.length; j++) {
				for (int k = 0; k < values.length; k++) {
					String value = values[k];
					String domain = domains[k];
					String title = value + " (" + seriesData[j].getName();
					if (!aggregatedBatches)
						title += " @ run." + indizes[j];
					title += ")";

					// iterate over values to be plotted
					if (domain.equals(PlotConfig.customPlotDomainExpression)) {
						// if expression
						String[] expressionSplit = value.split(":");
						if (expressionSplit.length != 2) {
							Log.warn("wrong expression syntax for '" + value
									+ "'");
							continue;
						}
						// parse name
						String exprName;
						if (expressionSplit[0].equals(""))
							exprName = expressionSplit[1];
						else
							exprName = expressionSplit[0];

						if (initBatches[j].contains(PlottingUtils
								.getDomainFromExpression(value,
										config.getGeneralDomain()),
								PlottingUtils.getValueFromExpression(value))) {
							String runAddition = "";
							if (!aggregatedBatches)
								runAddition = " @ run." + indizes[j];
							dataList.add(new ExpressionData(exprName,
									expressionSplit[1], style, exprName
											.replace("$", "")
											+ " ("
											+ seriesData[j].getName()
											+ runAddition + ")", config
											.getGeneralDomain(), seriesData[j]
											.getDir()));
							seriesDataQuantities[j]++;
						}
					} else {
						// check if series contains value
						if (initBatches[j].contains(domain, value)) {
							dataList.add(PlotData.get(value, domain, style,
									title, type, seriesData[j].getDir()));
							seriesDataQuantities[j]++;
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

				// set timestamp mapping
				p.setTimestampMap(timestampMap);

				// set series data quantities
				p.setSeriesDataQuantities(seriesDataQuantities);

				// sort
				p.sortData(config);

				// add to plot list
				customPlots.add(p);
			}
			if (plotAsCdf) {
				// create plot
				Plot p = new Plot(dstDir,
						PlotFilenames.getValuesPlotCDF(filename),
						PlotFilenames.getValuesGnuplotScriptCDF(filename),
						"CDF of " + config.getTitle(), config, data);

				// set timestamp mapping
				p.setTimestampMap(timestampMap);

				// set as cdf plot
				p.setCdfPlot(true);

				// set series data quantities
				p.setSeriesDataQuantities(seriesDataQuantities);

				// sort
				p.sortData(config);

				// add to plot list
				customPlots.add(p);
			}
		}
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
	 * @param indizes
	 *            Indizes of the runs, used for line naming.
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
	public static void generateMultiSeriesDefaultPlots(
			ArrayList<Plot> plotList, String dstDir, SeriesData[] seriesData,
			int[] indizes, IBatch[] initBatches, boolean plotStatistics,
			boolean plotMetricValues, boolean plotRuntimes, PlotStyle style,
			PlotType type, ValueSortMode valueSortMode, String[] valueSortList,
			ArrayList<PlotConfig> customMetricValuePlots,
			ArrayList<PlotConfig> customValuePlots,
			HashMap<Long, Long> timestampMap) throws IOException {
		boolean aggregatedBatches = false;
		if (initBatches instanceof AggregatedBatch[])
			aggregatedBatches = true;

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

		// define list of custom plots that substitute default plots
		ArrayList<PlotConfig> customDefaultSubstitutePlots = new ArrayList<PlotConfig>();
		if (customMetricValuePlots != null) {
			for (PlotConfig pc : customMetricValuePlots) {
				// if only 1 value, add plot to list of substitutes
				if (pc.getValues().length == 1)
					customDefaultSubstitutePlots.add(pc);
			}
		}

		if (customValuePlots != null) {
			for (PlotConfig pc : customValuePlots) {
				// if only 1 value, add plot to list of substitutes
				if (pc.getValues().length == 1)
					customDefaultSubstitutePlots.add(pc);
			}
		}

		// printed flag
		boolean printed = false;

		// gather fixed values
		for (int i = 0; i < seriesData.length; i++) {
			// statistic values
			if (plotStatistics && Config.getBoolean("DEFAULT_PLOT_VALUES")) {
				// ValueList aValues = initBatches[i].getValues();
				// get statistic value names
				Collection<String> valueNames;
				if (aggregatedBatches)
					valueNames = ((AggregatedBatch) initBatches[i]).getValues()
							.getNames();
				else
					valueNames = ((BatchData) initBatches[i]).getValues()
							.getNames();

				for (String value : valueNames) {
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
					// MetricDataList aMetrics = initBatches[i].getMetrics();
					// get metric names
					Collection<String> metricNames;
					if (aggregatedBatches)
						metricNames = ((AggregatedBatch) initBatches[i])
								.getMetrics().getNames();
					else
						metricNames = ((BatchData) initBatches[i]).getMetrics()
								.getNames();

					for (String metric : metricNames) {
						// ValueList aMetricValues = aMetrics.get(metric)
						// .getValues();
						// get metric value names
						Collection<String> metricValueNames;
						if (aggregatedBatches)
							metricValueNames = ((AggregatedBatch) initBatches[i])
									.getMetrics().get(metric).getValues()
									.getNames();
						else
							metricValueNames = ((BatchData) initBatches[i])
									.getMetrics().get(metric).getValues()
									.getNames();

						for (String value : metricValueNames) {
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
				// get general runtimes
				Collection<String> runtimeNames;
				if (aggregatedBatches)
					runtimeNames = ((AggregatedBatch) initBatches[i])
							.getGeneralRuntimes().getNames();
				else
					runtimeNames = ((BatchData) initBatches[i])
							.getGeneralRuntimes().getNames();

				for (String runtime : runtimeNames) {
					if (!genRuntimeValues.contains(runtime)) {
						genRuntimeValues.add(runtime);
						genRuntimeOccurence.add(1);
					} else {
						int index = genRuntimeValues.indexOf(runtime);
						genRuntimeOccurence.set(index,
								genRuntimeOccurence.get(index) + 1);
					}
				}

				// get metric runtimes
				if (aggregatedBatches)
					runtimeNames = ((AggregatedBatch) initBatches[i])
							.getMetricRuntimes().getNames();
				else
					runtimeNames = ((BatchData) initBatches[i])
							.getMetricRuntimes().getNames();

				for (String runtime : runtimeNames) {
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
				// check if batch contains general runtime
				Collection<String> runtimeNames;
				if (aggregatedBatches)
					runtimeNames = ((AggregatedBatch) initBatches[j])
							.getGeneralRuntimes().getNames();
				else
					runtimeNames = ((BatchData) initBatches[j])
							.getGeneralRuntimes().getNames();
				if (runtimeNames.contains(runtime)) {
					String runAddition = "";
					if (!aggregatedBatches)
						runAddition = " @ run." + indizes[j];
					data[index] = PlotData.get(runtime,
							PlotConfig.customPlotDomainGeneralRuntimes, style,
							seriesData[j].getName() + runAddition, type,
							seriesData[j].getDir());
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
					runtime, data);

			// set quantities
			p.setSeriesDataQuantities(seriesDataQuantities);

			// sort
			p.sortData(valueSortMode, valueSortList);

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
				// check if batch contains metric runtime
				Collection<String> runtimeNames;
				if (aggregatedBatches)
					runtimeNames = ((AggregatedBatch) initBatches[j])
							.getGeneralRuntimes().getNames();
				else
					runtimeNames = ((BatchData) initBatches[j])
							.getGeneralRuntimes().getNames();
				if (runtimeNames.contains(runtime)) {
					String runAddition = "";
					if (!aggregatedBatches)
						runAddition = " @ run." + indizes[j];
					data[index] = PlotData.get(runtime,
							PlotConfig.customPlotDomainMetricRuntimes, style,
							seriesData[j].getName() + runAddition, type,
							seriesData[j].getDir());
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
					runtime, data);

			// set timestamp mapping
			p.setTimestampMap(timestampMap);

			// set quantities
			p.setSeriesDataQuantities(seriesDataQuantities);

			// sort
			p.sortData(valueSortMode, valueSortList);

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
				// get batch statistics names
				Collection<String> valueNames;
				if (aggregatedBatches)
					valueNames = ((AggregatedBatch) initBatches[j]).getValues()
							.getNames();
				else
					valueNames = ((BatchData) initBatches[j]).getValues()
							.getNames();

				// iterate over domains that contain the value
				for (String d : domains) {
					String lineTitle;
					String runAddition = "";
					if (!aggregatedBatches)
						runAddition = " @ run." + indizes[j];
					if (simpleTitles) {
						lineTitle = seriesData[j].getName();
					} else {
						if (d.equals(PlotConfig.customPlotDomainStatistics))
							lineTitle = value + " (" + seriesData[j].getName()
									+ runAddition + ")";
						else
							lineTitle = d
									+ PlotConfig.customPlotDomainDelimiter
									+ value + " (" + seriesData[j].getName()
									+ runAddition + ")";
					}

					// check if series j contains value as a statistic
					if (d.equals(PlotConfig.customPlotDomainStatistics)) {
						if (valueNames.contains(value)) {
							valuePlotData[index] = PlotData.get(value, d,
									style, lineTitle, type,
									seriesData[j].getDir());
							seriesDataQuantities[j]++;
							index++;
						}
					}

					// get metric names
					Collection<String> metricNames;
					if (aggregatedBatches)
						metricNames = ((AggregatedBatch) initBatches[j])
								.getMetrics().getNames();
					else
						metricNames = ((BatchData) initBatches[j]).getMetrics()
								.getNames();

					// check if series j contains value in metric d
					if (metricNames.contains(d)) {
						// get metric value names
						Collection<String> metricValueNames;
						if (aggregatedBatches)
							metricValueNames = ((AggregatedBatch) initBatches[j])
									.getMetrics().get(d).getValues().getNames();
						else
							metricValueNames = ((BatchData) initBatches[j])
									.getMetrics().get(d).getValues().getNames();
						if (metricValueNames.contains(value)) {
							valuePlotData[index] = PlotData.get(value, d,
									style, lineTitle, type,
									seriesData[j].getDir());
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
					plotTitle = value;
				else
					plotTitle = domains.get(0)
							+ PlotConfig.customPlotDomainDelimiter + value;
			} else {
				plotTitle = value;
			}

			// log
			Log.info("\tplotting " + "'" + value + "'");

			// create plot
			Plot p = new Plot(dstDir, PlotFilenames.getValuesPlot(value),
					PlotFilenames.getValuesGnuplotScript(value), plotTitle,
					valuePlotData);

			// set timestamp mapping
			p.setTimestampMap(timestampMap);

			// set quantities
			p.setSeriesDataQuantities(seriesDataQuantities);

			// sort
			p.sortData(valueSortMode, valueSortList);

			// add to plot list
			plotList.add(p);
		}

		// HashMap<String, Boolean> printedValues = new HashMap<String,
		// Boolean>();

		// create default metric plots
		if (plotMetricValues && Config.getBoolean("DEFAULT_PLOT_METRIC_VALUES")) {
			// log
			Log.info("Plotting default metric value plots:");

			ArrayList<String> usedValues = new ArrayList<String>();

			for (int i = 0; i < seriesData.length; i++) {
				// get metric names
				Collection<String> metricNames;
				if (aggregatedBatches)
					metricNames = ((AggregatedBatch) initBatches[i])
							.getMetrics().getNames();
				else
					metricNames = ((BatchData) initBatches[i]).getMetrics()
							.getNames();

				for (String metric : metricNames) {
					// get metric value names
					Collection<String> metricValueNames;
					if (aggregatedBatches)
						metricValueNames = ((AggregatedBatch) initBatches[i])
								.getMetrics().get(metric).getValues()
								.getNames();
					else
						metricValueNames = ((BatchData) initBatches[i])
								.getMetrics().get(metric).getValues()
								.getNames();

					// check values
					for (String value : metricValueNames) {
						if (!usedValues.contains(metric
								+ PlotConfig.customPlotDomainDelimiter + value)) {
							usedValues.add(metric
									+ PlotConfig.customPlotDomainDelimiter
									+ value);
						}
					}
				}
			}

			// create value plots
			for (String entry : usedValues) {
				String[] split = entry
						.split(PlotConfig.customPlotDomainDelimiter);

				String metric = split[0];
				String value = split[1];

				// substitution
				boolean substituteAvailable = false;

				for (PlotConfig pc : customDefaultSubstitutePlots) {
					if (substituteAvailable)
						continue;

					if (pc.getDomains()[0]
							.equals(PlotConfig.customPlotDomainExpression)) {
						if (PlottingUtils.getDomainFromExpression(
								pc.getValues()[0], pc.getGeneralDomain())
								.equals(metric)
								&& PlottingUtils.getValueFromExpression(
										pc.getValues()[0]).equals(value)) {
							Log.info("\tskipping '" + metric + "." + value
									+ "'");
							Log.info("\t\t->  replaced by customplot '"
									+ pc.getFilename() + "'");
							substituteAvailable = true;
						}
					} else if (pc.getDomains()[0].equals(metric)
							&& pc.getValues()[0].equals(value)) {
						Log.info("\tskipping '" + metric + "." + value + "'");
						Log.info("\t\t->  replaced by customplot '"
								+ pc.getFilename() + "'");
						substituteAvailable = true;
					}
				}

				// skip if substitution is available
				if (substituteAvailable)
					continue;

				// log
				Log.info("\tplotting '" + metric + "." + value + "'");

				ArrayList<PlotData> lines = new ArrayList<PlotData>();
				int[] seriesDataQuantities = new int[seriesData.length];

				// iterate over series
				for (int i = 0; i < seriesData.length; i++) {
					// get metric names
					Collection<String> metricNames;
					if (aggregatedBatches)
						metricNames = ((AggregatedBatch) initBatches[i])
								.getMetrics().getNames();
					else
						metricNames = ((BatchData) initBatches[i]).getMetrics()
								.getNames();

					if (metricNames.contains(metric)) {
						// get metric value names
						Collection<String> metricValueNames;
						if (aggregatedBatches)
							metricValueNames = ((AggregatedBatch) initBatches[i])
									.getMetrics().get(metric).getValues()
									.getNames();
						else
							metricValueNames = ((BatchData) initBatches[i])
									.getMetrics().get(metric).getValues()
									.getNames();

						if (metricValueNames.contains(value)) {
							String runAddition = "";
							if (!aggregatedBatches)
								runAddition = " @ run." + indizes[i];
							// create "line"
							PlotData data = PlotData.get(value, metric, style,
									seriesData[i].getName() + runAddition,
									type, seriesData[i].getDir());
							lines.add(data);
							seriesDataQuantities[i]++;
						}
					}
				}

				// creeate plot
				PlotData[] data = lines.toArray(new PlotData[0]);

				Plot p = new Plot(dstDir, PlotFilenames.getValuesPlot(metric,
						value), PlotFilenames.getValuesGnuplotScript(metric,
						value), metric + "." + value, data);

				// set timestamp mapping
				p.setTimestampMap(timestampMap);

				// set quantities
				p.setSeriesDataQuantities(seriesDataQuantities);

				// sort
				p.sortData(valueSortMode, valueSortList);

				// add plot to list
				plotList.add(p);
			}
		}
	}

	/** Plots Distributions and NodeValueLists **/
	public static void plotDistributionsAndNodeValues(
			boolean plotDistributions, boolean plotNodeValues,
			IBatch initBatch, String source, String[] batches,
			double[] timestamps, ArrayList<PlotConfig> customDistributionPlots,
			ArrayList<PlotConfig> customNodeValueListPlots, String seriesDir,
			String aggrDir, String dstDir, String title, PlotStyle style,
			PlotType type, DistributionPlotType distPlotType,
			NodeValueListOrder order, NodeValueListOrderBy orderBy)
			throws IOException, InterruptedException {
		Log.infoSep();
		Log.info("Sequentially plotting Distributions and / or NodeValueLists");
		Log.info("");

		// check if aggregated batches
		boolean aggregatedBatches = false;
		if (initBatch instanceof AggregatedBatch)
			aggregatedBatches = true;

		// check if singlefile
		boolean zippedRuns = false;
		boolean zippedBatches = false;
		if (Config.get("GENERATION_AS_ZIP").equals("runs"))
			zippedRuns = true;
		if (Config.get("GENERATION_AS_ZIP").equals("batches"))
			zippedBatches = true;

		// generate plots
		List<Plot> plots = new LinkedList<Plot>();

		// define list of custom plots that substitute default plots
		ArrayList<PlotConfig> customDefaultSubstitutePlots = new ArrayList<PlotConfig>();
		for (PlotConfig pc : customDistributionPlots) {
			// if only 1 value, add plot to list of substitutes
			if (pc.getValues().length == 1)
				customDefaultSubstitutePlots.add(pc);
		}
		for (PlotConfig pc : customNodeValueListPlots) {
			// if only 1 value, add plot to list of substitutes
			if (pc.getValues().length == 1)
				customDefaultSubstitutePlots.add(pc);
		}

		// if aggregated, add type
		String aggAddition = "";
		if (aggregatedBatches)
			aggAddition = " (" + type + ")";

		// get metric names
		Collection<String> metricNames;
		if (aggregatedBatches)
			metricNames = ((AggregatedBatch) initBatch).getMetrics().getNames();
		else
			metricNames = ((BatchData) initBatch).getMetrics().getNames();

		// iterate over metrics and create plots
		for (String metric : metricNames) {
			Log.infoSep();
			Log.info("Plotting metric " + metric);

			// generate distribution plots
			if (plotDistributions && Config.getBoolean("DEFAULT_PLOTS_ENABLED")
					&& Config.getBoolean("DEFAULT_PLOT_DISTRIBUTIONS")) {
				// get distribution names
				Collection<String> distributionNames;
				if (aggregatedBatches)
					distributionNames = ((AggregatedBatch) initBatch)
							.getMetrics().get(metric).getDistributions()
							.getNames();
				else
					distributionNames = ((BatchData) initBatch).getMetrics()
							.get(metric).getDistributions().getNames();

				for (String distribution : distributionNames) {
					// substitution
					boolean substituteAvailable = false;

					for (PlotConfig pc : customDefaultSubstitutePlots) {
						if (substituteAvailable)
							continue;

						if (pc.getDomains()[0].equals(metric)
								&& pc.getValues()[0].equals(distribution)) {
							Log.info("\tskipping '" + metric + "."
									+ distribution + "'");
							Log.info("\t\t->  replaced by customplot '"
									+ pc.getFilename() + "'");
							substituteAvailable = true;
						}
					}

					// skip if substitution is available
					if (substituteAvailable)
						continue;

					// log
					Log.info("\tplotting distribution '" + distribution + "'");

					// get dist filename
					String distFilename;
					if (aggregatedBatches) {
						AggregatedDistribution d = ((AggregatedBatch) initBatch)
								.getMetrics().get(metric).getDistributions()
								.get(distribution);
						if (d instanceof AggregatedBinnedDistribution)
							distFilename = Files
									.getAggregatedBinnedDistributionFilename(d
											.getName());
						else
							distFilename = Files
									.getAggregatedDistributionFilename(d
											.getName());
					} else {
						Distr<?, ?> d = ((BatchData) initBatch).getMetrics()
								.get(metric).getDistributions()
								.get(distribution);
						distFilename = Files.getDistributionFilename(
								d.getName(), d.getDistrType());
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

					// generate normal plots
					if (plotDist) {
						PlotData[] dPlotData = new PlotData[batches.length];
						for (int i = 0; i < batches.length; i++) {
							dPlotData[i] = PlotData.get(distribution, metric,
									style, title + " @ " + timestamps[i], type,
									source);
							if (!Config.getBoolean("GNUPLOT_DATA_IN_SCRIPT")
									&& !zippedBatches && !zippedRuns) {
								if (aggregatedBatches) {
									dPlotData[i]
											.setDataLocation(
													PlotDataLocation.dataFile,
													Dir.getMetricDataDir(
															Dir.getBatchDataDir(
																	aggrDir,
																	(long) timestamps[i]),
															metric)
															+ distFilename);
								} else {
									dPlotData[i]
											.setDataLocation(
													PlotDataLocation.dataFile,
													Dir.getMetricDataDir(
															Dir.getBatchDataDir(
																	aggrDir,
																	(long) timestamps[i]),
															metric,
															((BatchData) initBatch)
																	.getMetrics()
																	.get(metric)
																	.getType())
															+ distFilename);
								}
							}
						}
						Plot p = new Plot(dstDir,
								PlotFilenames.getDistributionPlot(metric,
										distribution),
								PlotFilenames.getDistributionGnuplotScript(
										metric, distribution), distribution
										+ aggAddition, dPlotData);

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
											+ timestamps[i], type, source);
							cdfPlotData.setPlotAsCdf(true);
							if (!Config.getBoolean("GNUPLOT_DATA_IN_SCRIPT")
									&& !zippedBatches && !zippedRuns) {
								if (aggregatedBatches) {
									cdfPlotData
											.setDataLocation(
													PlotDataLocation.dataFile,
													Dir.getMetricDataDir(
															Dir.getBatchDataDir(
																	aggrDir,
																	(long) timestamps[i]),
															metric)
															+ distFilename);
								} else {
									cdfPlotData
											.setDataLocation(
													PlotDataLocation.dataFile,
													Dir.getMetricDataDir(
															Dir.getBatchDataDir(
																	aggrDir,
																	(long) timestamps[i]),
															metric,
															((BatchData) initBatch)
																	.getMetrics()
																	.get(metric)
																	.getType())
															+ distFilename);
								}
							}
							dPlotDataCDF[i] = cdfPlotData;
						}
						Plot p = new Plot(dstDir,
								PlotFilenames.getDistributionCdfPlot(metric,
										distribution),
								PlotFilenames.getDistributionCdfGnuplotScript(
										metric, distribution), "CDF of "
										+ distribution + aggAddition,
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
				// get nodevaluelist names
				Collection<String> nodevaluelistNames;
				if (aggregatedBatches)
					nodevaluelistNames = ((AggregatedBatch) initBatch)
							.getMetrics().get(metric).getNodeValues()
							.getNames();
				else
					nodevaluelistNames = ((BatchData) initBatch).getMetrics()
							.get(metric).getNodeValues().getNames();

				for (String nodevaluelist : nodevaluelistNames) {
					// substitution
					boolean substituteAvailable = false;

					for (PlotConfig pc : customDefaultSubstitutePlots) {
						if (substituteAvailable)
							continue;

						if (pc.getDomains()[0].equals(metric)
								&& pc.getValues()[0].equals(nodevaluelist)) {
							Log.info("\tskipping '" + metric + "."
									+ nodevaluelist + "'");
							Log.info("\t\t->  replaced by customplot '"
									+ pc.getFilename() + "'");
							substituteAvailable = true;
						}
					}

					// skip if substitution is available
					if (substituteAvailable)
						continue;

					// log
					Log.info("\tplotting nodevaluelist '" + nodevaluelist + "'");

					// generate normal plots
					PlotData[] nPlotData = new PlotData[batches.length];
					for (int i = 0; i < batches.length; i++) {
						PlotData plotData = PlotData.get(nodevaluelist, metric,
								style, title + " @ " + timestamps[i], type,
								source);
						if (!Config.getBoolean("GNUPLOT_DATA_IN_SCRIPT")
								&& !zippedBatches && !zippedRuns) {
							if (aggregatedBatches) {
								plotData.setDataLocation(
										PlotDataLocation.dataFile,
										Dir.getMetricDataDir(Dir
												.getBatchDataDir(aggrDir,
														(long) timestamps[i]),
												metric)
												+ Files.getNodeValueListFilename(nodevaluelist));
							} else {
								plotData.setDataLocation(
										PlotDataLocation.dataFile,
										Dir.getMetricDataDir(
												Dir.getBatchDataDir(aggrDir,
														(long) timestamps[i]),
												metric,
												((BatchData) initBatch)
														.getMetrics()
														.get(metric).getType())
												+ Files.getNodeValueListFilename(nodevaluelist));
							}
						}
						nPlotData[i] = plotData;
					}

					Plot nPlot = new Plot(dstDir,
							PlotFilenames.getNodeValueListPlot(metric,
									nodevaluelist),
							PlotFilenames.getNodeValueListGnuplotScript(metric,
									nodevaluelist),
							nodevaluelist + aggAddition, nPlotData);

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
							// get dist filename
							String distFilename = Files
									.getDistributionFilename(initBatch,
											domains[j], values[j],
											aggregatedBatches);

							if (plotDist) {
								PlotData pd = PlotData
										.get(values[j],
												domains[j],
												style,
												domains[j]
														+ PlotConfig.customPlotDomainDelimiter
														+ values[j] + " @ "
														+ timestamps[i], type,
												source);
								if (!Config
										.getBoolean("GNUPLOT_DATA_IN_SCRIPT")
										&& !zippedBatches && !zippedRuns) {
									if (aggregatedBatches) {
										pd.setDataLocation(
												PlotDataLocation.dataFile,
												Dir.getMetricDataDir(
														Dir.getBatchDataDir(
																aggrDir,
																(long) timestamps[i]),
														domains[j])
														+ distFilename);
									} else {
										pd.setDataLocation(
												PlotDataLocation.dataFile,
												Dir.getMetricDataDir(
														Dir.getBatchDataDir(
																aggrDir,
																(long) timestamps[i]),
														domains[j],
														((BatchData) initBatch)
																.getMetrics()
																.get(domains[j])
																.getType())
														+ distFilename);
									}
								}
								data[i * valuesCount + j] = pd;
							}
							if (plotCdf) {
								PlotData dCdf = PlotData
										.get(values[j],
												domains[j],
												style,
												domains[j]
														+ PlotConfig.customPlotDomainDelimiter
														+ values[j] + " @ "
														+ timestamps[i], type,
												source);
								if (!Config
										.getBoolean("GNUPLOT_DATA_IN_SCRIPT")
										&& !zippedBatches && !zippedRuns) {
									if (aggregatedBatches) {
										dCdf.setDataLocation(
												PlotDataLocation.dataFile,
												Dir.getMetricDataDir(
														Dir.getBatchDataDir(
																aggrDir,
																(long) timestamps[i]),
														domains[j])
														+ distFilename);
									} else {
										dCdf.setDataLocation(
												PlotDataLocation.dataFile,
												Dir.getMetricDataDir(
														Dir.getBatchDataDir(
																aggrDir,
																(long) timestamps[i]),
														domains[j],
														((BatchData) initBatch)
																.getMetrics()
																.get(domains[j])
																.getType())
														+ distFilename);
									}
								}
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
									PlotType.function, null);
						if (plotCdf)
							dataCdf[offset + i] = PlotData.get(
									functionSplit[0], functionSplit[1], style,
									title, PlotType.function, null);
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
								name + aggAddition, pc, data);

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
								"CDF of " + name + aggAddition, pc, dataCdf);

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
							PlotData d = PlotData
									.get(values[j],
											domains[j],
											style,
											domains[j]
													+ PlotConfig.customPlotDomainDelimiter
													+ values[j] + " @ "
													+ timestamps[i], type,
											source);
							if (!Config.getBoolean("GNUPLOT_DATA_IN_SCRIPT")
									&& !zippedBatches && !zippedRuns) {
								if (aggregatedBatches) {
									d.setDataLocation(
											PlotDataLocation.dataFile,
											Dir.getMetricDataDir(
													Dir.getBatchDataDir(
															aggrDir,
															(long) timestamps[i]),
													domains[j])
													+ Files.getNodeValueListFilename(values[j]));
								} else {
									d.setDataLocation(
											PlotDataLocation.dataFile,
											Dir.getMetricDataDir(
													Dir.getBatchDataDir(
															aggrDir,
															(long) timestamps[i]),
													domains[j],
													((BatchData) initBatch)
															.getMetrics()
															.get(domains[j])
															.getType())
													+ Files.getNodeValueListFilename(values[j]));
								}
							}
							data[i * valuesCount + j] = d;
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
								PlotType.function, null);
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
							name + aggAddition, pc, data);

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

		String tempAggrDir = aggrDir;

		// read data batch by batch and add to plots
		for (int i = 0; i < batches.length; i++) {
			if (aggregatedBatches) {
				long timestamp = Dir.getTimestamp(batches[i]);

				// read data
				AggregatedBatch tempBatch = AggregatedBatch.readIntelligent(
						Dir.getBatchDataDir(tempAggrDir, timestamp), timestamp,
						BatchReadMode.readOnlyDistAndNvl);

				// append data to plots
				for (Plot p : plots) {
					for (int j = 0; j < p.getDataQuantity(); j++) {
						p.addDataSequentially(tempBatch);
					}
				}

				// free resources
				tempBatch = null;
			} else {
				long timestamp = Dir.getTimestamp(batches[i]);

				// read data
				BatchData tempBatch = BatchData.readIntelligent(
						Dir.getBatchDataDir(tempAggrDir, timestamp), timestamp,
						BatchReadMode.readOnlyDistAndNvl);

				// append data to plots
				for (Plot p : plots) {
					for (int j = 0; j < p.getDataQuantity(); j++) {
						p.addDataSequentially(tempBatch);
					}
				}

				// free resources
				tempBatch = null;
			}
			// free resources
			System.gc();
		}

		// close and execute plot scripts
		for (Plot p : plots) {
			p.close();
			p.execute();
		}
	}

	/** Plots custom value plots **/
	public static void plotCustomValuePlots(IBatch[] batchData, String source,
			ArrayList<PlotConfig> customValuePlots, String dstDir,
			String title, PlotStyle style, PlotType type,
			ValueSortMode valueSortMode, String[] valueSortList,
			HashMap<Long, Long> timestampMap) throws IOException,
			InterruptedException {
		// check if aggregated batches
		boolean aggregatedBatches = false;
		if (batchData instanceof AggregatedBatch[])
			aggregatedBatches = true;

		String aggAddition = "";
		if (aggregatedBatches)
			aggAddition = " (" + type + ")";

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
									+ value, PlotType.function, null);
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
							pc.getGeneralDomain(), source);
				} else {
					data[j] = PlotData.get(value, domain, style, value, type,
							source);
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
								+ aggAddition, pc, data);

				// set timestamp mapping
				p.setTimestampMap(timestampMap);

				// sort
				p.sortData(pc);

				// add plot labels
				if (p.isPlotLabels())
					p.addPlotLabels(batchData);

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
								+ aggAddition, pc, data);

				// set timestamp mapping
				p.setTimestampMap(timestampMap);

				// set as cdf
				p.setCdfPlot(true);

				// sort
				p.sortData(pc);

				// add plot labels
				if (p.isPlotLabels())
					p.addPlotLabels(batchData);

				// write script header
				p.writeScriptHeader();

				// add data
				p.addData(batchData);

				// close and execute
				p.close();
				p.execute();
			}
		}
	}

	/** Plot custom runtime plots **/
	public static void plotCustomRuntimes(IBatch[] batchData, String source,
			ArrayList<PlotConfig> customPlots, String dstDir, String title,
			PlotStyle style, PlotType type, ValueSortMode valueSortMode,
			String[] valueSortList, HashMap<Long, Long> timestampMap)
			throws IOException, InterruptedException {
		Log.infoSep();
		Log.info("Plotting Custom-Runtime-Plots:");

		// check if aggregated batches
		boolean aggregatedBatches = false;
		if (batchData instanceof AggregatedBatch[])
			aggregatedBatches = true;

		String aggAddition = "";
		if (aggregatedBatches)
			aggAddition = " (" + type + ")";

		// start plotting process
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
									+ value, PlotType.function, null);
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
							exprName.replace("$", ""), pc.getGeneralDomain(),
							source);
				} else {
					plotData[i] = PlotData.get(value, domain, style, value,
							type, source);
				}
			}

			// normal plot
			if (plotNormal) {
				// create plot
				Plot p = new Plot(dstDir, plotFilename,
						PlotFilenames.getRuntimesGnuplotScript(plotFilename),
						name + aggAddition, pc, plotData);

				// sort
				p.sortData(pc);

				// set timestamp mapping
				p.setTimestampMap(timestampMap);

				// add plot labels
				if (p.isPlotLabels())
					p.addPlotLabels(batchData);

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
						"CDF of " + name + aggAddition, pc, plotData);

				// set timestamp mapping
				p.setTimestampMap(timestampMap);

				// set cdf plot
				p.setCdfPlot(true);

				// sort
				p.sortData(pc);

				// add plot labels
				if (p.isPlotLabels())
					p.addPlotLabels(batchData);

				// write script header
				p.writeScriptHeader();

				// add data
				p.addData(batchData);

				// close and execute
				p.close();
				p.execute();

			}
		}
	}

	/** Plots metric values **/
	public static void plotMetricValues(IBatch[] batchData, String source,
			IBatch initBatch, String dstDir, String title, PlotStyle style,
			PlotType type, ValueSortMode valueSortMode, String[] valueSortList,
			ArrayList<PlotConfig> customMetricValuePlots,
			ArrayList<PlotConfig> customValuePlots,
			HashMap<Long, Long> timestampMap) throws IOException,
			InterruptedException {
		// check if aggregated batches
		boolean aggregatedBatches = false;
		if (initBatch instanceof AggregatedBatch)
			aggregatedBatches = true;

		String aggAddition = "";
		if (aggregatedBatches)
			aggAddition = " (" + type + ")";

		// init list for plots
		List<Plot> plots = new LinkedList<Plot>();

		// define list of custom plots that substitute default plots
		ArrayList<PlotConfig> customDefaultSubstitutePlots = new ArrayList<PlotConfig>();
		if (customMetricValuePlots != null) {
			for (PlotConfig pc : customMetricValuePlots) {
				// if only 1 value, add plot to list of substitutes
				if (pc.getValues().length == 1)
					customDefaultSubstitutePlots.add(pc);
			}
		}
		if (customValuePlots != null) {
			for (PlotConfig pc : customValuePlots) {
				// if only 1 value, add plot to list of substitutes
				if (pc.getValues().length == 1)
					customDefaultSubstitutePlots.add(pc);
			}
		}

		/*
		 * SINGLE PLOTS
		 */

		// get metric names
		Collection<String> metricNames;
		if (aggregatedBatches)
			metricNames = ((AggregatedBatch) initBatch).getMetrics().getNames();
		else
			metricNames = ((BatchData) initBatch).getMetrics().getNames();

		// iterate over metrics
		for (String metric : metricNames) {
			Log.infoSep();
			Log.info("Plotting metric " + metric);

			// get metric value names
			Collection<String> metricValueNames;
			if (aggregatedBatches)
				metricValueNames = ((AggregatedBatch) initBatch).getMetrics()
						.get(metric).getValues().getNames();
			else
				metricValueNames = ((BatchData) initBatch).getMetrics()
						.get(metric).getValues().getNames();

			// iterate over values
			for (String value : metricValueNames) {
				// substitution
				boolean substituteAvailable = false;

				for (PlotConfig pc : customDefaultSubstitutePlots) {
					if (substituteAvailable)
						continue;

					if (pc.getDomains()[0]
							.equals(PlotConfig.customPlotDomainExpression)) {
						if (PlottingUtils.getDomainFromExpression(
								pc.getValues()[0], pc.getGeneralDomain())
								.equals(metric)
								&& PlottingUtils.getValueFromExpression(
										pc.getValues()[0]).equals(value)) {
							Log.info("\tskipping '" + metric + "." + value
									+ "'");
							Log.info("\t\t->  replaced by customplot '"
									+ pc.getFilename() + "'");
							substituteAvailable = true;
						}
					} else if (pc.getDomains()[0].equals(metric)
							&& pc.getValues()[0].equals(value)) {
						Log.info("\tskipping '" + metric + "." + value + "'");
						Log.info("\t\t->  replaced by customplot '"
								+ pc.getFilename() + "'");
						substituteAvailable = true;
					}
				}

				// skip if substitution is available
				if (substituteAvailable)
					continue;

				// log
				Log.info("\tplotting '" + value + "'");

				// get plot data
				PlotData valuePlotData = PlotData.get(value, metric, style,
						metric, type, source);

				// create plot
				Plot p = new Plot(dstDir, PlotFilenames.getValuesPlot(metric,
						value), PlotFilenames.getValuesGnuplotScript(metric,
						value), value + aggAddition,
						new PlotData[] { valuePlotData });

				// set timestamp mapping
				p.setTimestampMap(timestampMap);

				// sort
				p.sortData(valueSortMode, valueSortList);

				// add plot
				plots.add(p);
			}
		}

		/*
		 * COMBINED PLOTS
		 */
		ArrayList<String> values = new ArrayList<String>();

		for (String metric : metricNames) {
			// get metric value names
			Collection<String> metricValueNames;
			if (aggregatedBatches)
				metricValueNames = ((AggregatedBatch) initBatch).getMetrics()
						.get(metric).getValues().getNames();
			else
				metricValueNames = ((BatchData) initBatch).getMetrics()
						.get(metric).getValues().getNames();

			for (String value : metricValueNames) {
				values.add(value);
			}
		}

		// list of values, which all have an own list of metrics
		ArrayList<ArrayList<String>> valuesList = new ArrayList<ArrayList<String>>(
				values.size());

		for (int i = 0; i < values.size(); i++) {
			valuesList.add(i, new ArrayList<String>());
		}

		// for each value add metric that has the value
		for (String metric : metricNames) {
			// get metric value names
			Collection<String> metricValueNames;
			if (aggregatedBatches)
				metricValueNames = ((AggregatedBatch) initBatch).getMetrics()
						.get(metric).getValues().getNames();
			else
				metricValueNames = ((BatchData) initBatch).getMetrics()
						.get(metric).getValues().getNames();

			for (String value : metricValueNames) {
				int index = values.indexOf(value);
				valuesList.get(index).add(metric);
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
							metric, type, source);
				}

				// create plot
				Plot p = new Plot(dstDir,
						PlotFilenames.getCombinationPlot(value),
						PlotFilenames.getCombinationGnuplotScript(value), value
								+ aggAddition, valuePlotDatas);

				// set timestamp mapping
				p.setTimestampMap(timestampMap);

				// sort
				p.sortData(valueSortMode, valueSortList);

				// add plot
				plots.add(p);
			}
		}

		for (Plot p : plots) {
			// add plot labels
			if (p.isPlotLabels())
				p.addPlotLabels(batchData);

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

	/** Plots the def. distribution and nodeavluelist plots for multiple series. */
	public static void plotDistributionAndNodeValueListPlots(
			SeriesData[] seriesData, int[] indizes, String dstDir,
			String[] batches, double[] timestamps, IBatch[] initBatches,
			ArrayList<Long>[] seriesTimestamps, boolean plotDistributions,
			ArrayList<PlotConfig> customDistributionPlots,
			boolean plotNodeValues,
			ArrayList<PlotConfig> customNodeValueListPlots,
			boolean zippedBatches, boolean zippedRuns,
			DistributionPlotType distPlotType, NodeValueListOrder order,
			NodeValueListOrderBy orderBy, PlotType type, PlotStyle style,
			ValueSortMode valueSortMode, String[] valueSortList)
			throws IOException, InterruptedException {
		Log.infoSep();

		// check if aggregated batches
		boolean aggregatedBatches = false;
		if (initBatches instanceof AggregatedBatch[])
			aggregatedBatches = true;

		String aggAddition = "";
		if (aggregatedBatches)
			aggAddition = " (" + type + ")";

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

		// define list of custom plots that substitute default plots
		ArrayList<PlotConfig> customDefaultSubstitutePlots = new ArrayList<PlotConfig>();

		if (plotDistributions) {
			if (customDistributionPlots.size() > 0)
				Log.info("Plotting custom distribution plots:");

			// iterate over plot configs
			for (PlotConfig pc : customDistributionPlots) {
				Log.info("\tplotting '" + pc.getFilename() + "'");
				String[] values = pc.getValues();
				String[] domains = pc.getDomains();

				// if only 1 value, add plot to list of substitutes
				if (values.length == 1)
					customDefaultSubstitutePlots.add(pc);

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
							Collection<String> metricNames;
							if (aggregatedBatches)
								metricNames = ((AggregatedBatch) initBatches[j])
										.getMetrics().getNames();
							else
								metricNames = ((BatchData) initBatches[j])
										.getMetrics().getNames();

							if (metricNames.contains(domain)) {
								// check if series contains distribution
								Collection<String> distributionNames;
								if (aggregatedBatches)
									distributionNames = ((AggregatedBatch) initBatches[j])
											.getMetrics().get(domain)
											.getDistributions().getNames();
								else
									distributionNames = ((BatchData) initBatches[j])
											.getMetrics().get(domain)
											.getDistributions().getNames();

								if (distributionNames.contains(value)) {
									// get dist filename
									String distFilename = Files
											.getDistributionFilename(
													initBatches[j], domain,
													value, aggregatedBatches);

									String runAddition = "";
									if (!aggregatedBatches)
										runAddition = " @ run." + indizes[j];

									// set title
									String title;
									if (simpleTitles)
										title = seriesData[j].getName()
												+ runAddition + " @ "
												+ timestamp;
									else
										title = domain
												+ PlotConfig.customPlotDomainDelimiter
												+ value + " ("
												+ seriesData[j].getName()
												+ runAddition + ") @ "
												+ timestamp;

									if (plotDist) {
										PlotData data = PlotData.get(value,
												domain, style, title, type,
												seriesData[j].getDir());
										if (!Config
												.getBoolean("GNUPLOT_DATA_IN_SCRIPT")
												&& !zippedBatches
												&& !zippedRuns) {
											if (aggregatedBatches) {
												data.setDataLocation(
														PlotDataLocation.dataFile,
														Dir.getAggregatedMetricDataDir(
																seriesData[j]
																		.getDir(),
																timestamp,
																domain)
																+ distFilename);
											} else {
												data.setDataLocation(
														PlotDataLocation.dataFile,
														Dir.getMetricDataDir(
																seriesData[j]
																		.getDir(),
																indizes[j],
																timestamp,
																domain,
																((BatchData) initBatches[j])
																		.getMetrics()
																		.get(domain)
																		.getType())
																+ distFilename);
											}
										}
										dataList.add(data);
									}
									if (plotCdf) {
										PlotData data = PlotData.get(value,
												domain, style, title, type,
												seriesData[j].getDir());
										data.setPlotAsCdf(true);
										if (!Config
												.getBoolean("GNUPLOT_DATA_IN_SCRIPT")
												&& !zippedBatches
												&& !zippedRuns) {
											if (aggregatedBatches) {
												data.setDataLocation(
														PlotDataLocation.dataFile,
														Dir.getAggregatedMetricDataDir(
																seriesData[j]
																		.getDir(),
																timestamp,
																domain)
																+ distFilename);
											} else {
												data.setDataLocation(
														PlotDataLocation.dataFile,
														Dir.getMetricDataDir(
																seriesData[j]
																		.getDir(),
																indizes[j],
																timestamp,
																domain,
																((BatchData) initBatches[j])
																		.getMetrics()
																		.get(domain)
																		.getType())
																+ distFilename);
											}
										}
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

				// if only 1 value, add plot to list of substitutes
				if (values.length == 1)
					customDefaultSubstitutePlots.add(pc);

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
							Collection<String> metricNames;
							if (aggregatedBatches)
								metricNames = ((AggregatedBatch) initBatches[j])
										.getMetrics().getNames();
							else
								metricNames = ((BatchData) initBatches[j])
										.getMetrics().getNames();

							// check if series contains domain
							if (metricNames.contains(domain)) {
								// check if series contains nvl
								Collection<String> nodevaluelistNames;
								if (aggregatedBatches)
									nodevaluelistNames = ((AggregatedBatch) initBatches[j])
											.getMetrics().get(domain)
											.getNodeValues().getNames();
								else
									nodevaluelistNames = ((BatchData) initBatches[j])
											.getMetrics().get(domain)
											.getNodeValues().getNames();

								String runAddition = "";
								if (!aggregatedBatches)
									runAddition = " @ run." + indizes[j];

								if (nodevaluelistNames.contains(value)) {
									// set title
									String title;
									if (simpleTitles)
										title = seriesData[j].getName()
												+ runAddition + " @ "
												+ timestamp;
									else
										title = domain
												+ PlotConfig.customPlotDomainDelimiter
												+ value + " ("
												+ seriesData[j].getName()
												+ runAddition + ") @ "
												+ timestamp;

									// add data to list
									PlotData line = PlotData.get(value, domain,
											style, title, type,
											seriesData[j].getDir());
									if (!Config
											.getBoolean("GNUPLOT_DATA_IN_SCRIPT")
											&& !zippedBatches && !zippedRuns) {
										if (aggregatedBatches) {
											line.setDataLocation(
													PlotDataLocation.dataFile,
													Dir.getAggregatedMetricDataDir(
															seriesData[j]
																	.getDir(),
															timestamp, domain)
															+ Files.getNodeValueListFilename(value));
										} else {
											line.setDataLocation(
													PlotDataLocation.dataFile,
													Dir.getMetricDataDir(
															seriesData[j]
																	.getDir(),
															indizes[j],
															timestamp,
															domain,
															((BatchData) initBatches[j])
																	.getMetrics()
																	.get(domain)
																	.getType())
															+ Files.getNodeValueListFilename(value));
										}
									}
									dataList.add(line);
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
			// metrics
			Collection<String> metricNames;
			if (aggregatedBatches)
				metricNames = ((AggregatedBatch) initBatches[i]).getMetrics()
						.getNames();
			else
				metricNames = ((BatchData) initBatches[i]).getMetrics()
						.getNames();

			if (plotDistributions && Config.getBoolean("DEFAULT_PLOTS_ENABLED")
					&& Config.getBoolean("DEFAULT_PLOT_COMBINED_DISTRIBUTIONS")) {
				if (!loggedDist) {
					Log.info("Plotting combined default distribution plots:");
					loggedDist = true;
				}
				for (String metric : metricNames) {
					// distributions
					Collection<String> distributionNames;
					if (aggregatedBatches)
						distributionNames = ((AggregatedBatch) initBatches[i])
								.getMetrics().get(metric).getDistributions()
								.getNames();
					else
						distributionNames = ((BatchData) initBatches[i])
								.getMetrics().get(metric).getDistributions()
								.getNames();

					for (String dist : distributionNames) {
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

				for (String metric : metricNames) {
					// nodevaluelists
					Collection<String> nodevaluelistNames;
					if (aggregatedBatches)
						nodevaluelistNames = ((AggregatedBatch) initBatches[i])
								.getMetrics().get(metric).getNodeValues()
								.getNames();
					else
						nodevaluelistNames = ((BatchData) initBatches[i])
								.getMetrics().get(metric).getNodeValues()
								.getNames();

					for (String nvl : nodevaluelistNames) {
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

					IBatch initBatch = initBatches[k];

					String runAddition = "";
					if (!aggregatedBatches)
						runAddition = " @ run." + indizes[k];
					// iterate over domains that contain the value
					for (String d : domains) {
						String lineTitle;
						if (simpleTitles)
							lineTitle = seriesData[k].getName() + runAddition;
						else
							lineTitle = d + " (" + seriesData[k].getName()
									+ runAddition + ")";

						// metrics
						Collection<String> metricNames;
						if (aggregatedBatches)
							metricNames = ((AggregatedBatch) initBatch)
									.getMetrics().getNames();
						else
							metricNames = ((BatchData) initBatch).getMetrics()
									.getNames();

						if (metricNames.contains(d)) {
							// distributions
							Collection<String> distributionNames;
							if (aggregatedBatches)
								distributionNames = ((AggregatedBatch) initBatch)
										.getMetrics().get(d).getDistributions()
										.getNames();
							else
								distributionNames = ((BatchData) initBatch)
										.getMetrics().get(d).getDistributions()
										.getNames();
							if (distributionNames.contains(dist)) {
								// get dist filename
								String distFilename = Files
										.getDistributionFilename(
												initBatches[j], d, dist,
												aggregatedBatches);

								// create "line" in plot for each batch
								if (plotDist) {
									PlotData line = PlotData.get(dist, d,
											style, lineTitle + " @ "
													+ timestamps[j], type,
											seriesData[k].getDir());
									if (!Config
											.getBoolean("GNUPLOT_DATA_IN_SCRIPT")
											&& !zippedBatches && !zippedRuns) {
										if (aggregatedBatches) {
											line.setDataLocation(
													PlotDataLocation.dataFile,
													Dir.getAggregatedMetricDataDir(
															seriesData[k]
																	.getDir(),
															timestamp, d)
															+ distFilename);
										} else {
											line.setDataLocation(
													PlotDataLocation.dataFile,
													Dir.getMetricDataDir(
															seriesData[k]
																	.getDir(),
															indizes[k],
															timestamp,
															d,
															((BatchData) initBatch)
																	.getMetrics()
																	.get(d)
																	.getType())
															+ distFilename);
										}
									}
									line.setPlotAsCdf(true);
									dataList.add(line);
								}
								if (plotCdf) {
									PlotData cdfPlotData = PlotData.get(dist,
											d, style, lineTitle + " @ "
													+ timestamps[j], type,
											seriesData[k].getDir());
									cdfPlotData.setPlotAsCdf(true);
									if (!Config
											.getBoolean("GNUPLOT_DATA_IN_SCRIPT")
											&& !zippedBatches && !zippedRuns) {
										if (aggregatedBatches) {
											cdfPlotData
													.setDataLocation(
															PlotDataLocation.dataFile,
															Dir.getAggregatedMetricDataDir(
																	seriesData[k]
																			.getDir(),
																	timestamp,
																	d)
																	+ distFilename);
										} else {
											cdfPlotData
													.setDataLocation(
															PlotDataLocation.dataFile,
															Dir.getMetricDataDir(
																	seriesData[k]
																			.getDir(),
																	indizes[k],
																	timestamp,
																	d,
																	((BatchData) initBatch)
																			.getMetrics()
																			.get(d)
																			.getType())
																	+ distFilename);
										}
									}
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
							+ aggAddition;
				else
					plotTitle += dist + aggAddition;

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

					IBatch initBatch = initBatches[k];

					String runAddition = "";
					if (aggregatedBatches)
						runAddition = " @ run." + indizes[k];

					// iterate over domains that contain the value
					for (String d : domains) {
						String lineTitle;
						if (simpleTitles)
							lineTitle = seriesData[k].getName() + runAddition;
						else
							lineTitle = d + " (" + seriesData[k].getName()
									+ runAddition + ")";

						// metrics
						Collection<String> metricNames;
						if (aggregatedBatches)
							metricNames = ((AggregatedBatch) initBatch)
									.getMetrics().getNames();
						else
							metricNames = ((BatchData) initBatch).getMetrics()
									.getNames();

						if (metricNames.contains(d)) {
							// nodevaluelists
							Collection<String> nodevaluelistNames;
							if (aggregatedBatches)
								nodevaluelistNames = ((AggregatedBatch) initBatch)
										.getMetrics().get(d).getNodeValues()
										.getNames();
							else
								nodevaluelistNames = ((BatchData) initBatch)
										.getMetrics().get(d).getNodeValues()
										.getNames();

							if (nodevaluelistNames.contains(nvl)) {
								// create "line" in plot for each batch
								PlotData line = PlotData.get(nvl, d, style,
										lineTitle + " @ " + timestamps[j],
										type, seriesData[k].getDir());
								if (!Config
										.getBoolean("GNUPLOT_DATA_IN_SCRIPT")
										&& !zippedBatches && !zippedRuns) {
									if (aggregatedBatches) {
										line.setDataLocation(
												PlotDataLocation.dataFile,
												Dir.getAggregatedMetricDataDir(
														seriesData[k].getDir(),
														timestamp, d)
														+ Files.getNodeValueListFilename(nvl));
									} else {
										line.setDataLocation(
												PlotDataLocation.dataFile,
												Dir.getMetricDataDir(
														seriesData[k].getDir(),
														indizes[k], timestamp,
														d,
														((BatchData) initBatch)
																.getMetrics()
																.get(d)
																.getType())
														+ Files.getNodeValueListFilename(nvl));
									}
								}
								dataList.add(line);
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
						+ PlotConfig.customPlotDomainDelimiter + nvl
						+ aggAddition;
			else
				plotTitle = nvl + aggAddition;

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
			// log
			Log.info("Plotting default distribution plots:");

			// list of used metric . dist combinations
			ArrayList<String> usedDists = new ArrayList<String>();

			// gather values
			for (int i = 0; i < seriesData.length; i++) {
				// metrics
				Collection<String> metricNames;
				if (aggregatedBatches)
					metricNames = ((AggregatedBatch) initBatches[i])
							.getMetrics().getNames();
				else
					metricNames = ((BatchData) initBatches[i]).getMetrics()
							.getNames();

				// iterate over metrics
				for (String metric : metricNames) {
					// distributions
					Collection<String> distributionNames;
					if (aggregatedBatches)
						distributionNames = ((AggregatedBatch) initBatches[i])
								.getMetrics().get(metric).getDistributions()
								.getNames();
					else
						distributionNames = ((BatchData) initBatches[i])
								.getMetrics().get(metric).getDistributions()
								.getNames();

					// iterate over distributions
					for (String dist : distributionNames) {
						if (!usedDists.contains(metric
								+ PlotConfig.customPlotDomainDelimiter + dist)) {
							usedDists.add(metric
									+ PlotConfig.customPlotDomainDelimiter
									+ dist);
						}
					}
				}
			}

			// create plots
			for (String entry : usedDists) {
				String[] split = entry
						.split(PlotConfig.customPlotDomainDelimiter);
				int[] seriesDataQuantities = new int[seriesData.length];
				String metric = split[0];
				String dist = split[1];

				// substitution
				boolean substituteAvailable = false;

				for (PlotConfig pc : customDefaultSubstitutePlots) {
					if (substituteAvailable)
						continue;

					if (pc.getDomains()[0].equals(metric)
							&& pc.getValues()[0].equals(dist)) {
						Log.info("\tskipping '" + metric + "." + dist + "'");
						Log.info("\t\t->  replaced by customplot '"
								+ pc.getFilename() + "'");
						substituteAvailable = true;
					}
				}

				// skip if substitution is available
				if (substituteAvailable)
					continue;

				// log
				Log.info("\tplotting '" + metric + "." + dist + "'");

				ArrayList<PlotData> dataList = null;
				ArrayList<PlotData> dataListCdf = null;

				if (plotDist)
					dataList = new ArrayList<PlotData>();
				if (plotCdf)
					dataListCdf = new ArrayList<PlotData>();

				// iterate over series
				for (int i = 0; i < seriesData.length; i++) {
					String runAddition = "";
					if (!aggregatedBatches)
						runAddition = " @ run." + indizes[0];

					// metrics
					Collection<String> metricNames;
					if (aggregatedBatches)
						metricNames = ((AggregatedBatch) initBatches[i])
								.getMetrics().getNames();
					else
						metricNames = ((BatchData) initBatches[i]).getMetrics()
								.getNames();

					// iterate over metrics
					if (metricNames.contains(metric)) {
						// distributions
						Collection<String> distributionNames;
						if (aggregatedBatches)
							distributionNames = ((AggregatedBatch) initBatches[i])
									.getMetrics().get(metric)
									.getDistributions().getNames();
						else
							distributionNames = ((BatchData) initBatches[i])
									.getMetrics().get(metric)
									.getDistributions().getNames();

						// iterate over distributions
						if (distributionNames.contains(dist)) {
							// get filename
							String distFilename = Files
									.getDistributionFilename(initBatches[i],
											metric, dist, aggregatedBatches);

							// set data quantity
							seriesDataQuantities[i] = 1;

							// create plot data
							for (int j = 0; j < batches.length; j++) {
								long timestamp = (long) timestamps[j];

								// skip if series doesnt contain timestamp
								if (!seriesTimestamps[i].contains(timestamp))
									continue;

								String title = seriesData[i].getName()
										+ runAddition + " @ " + timestamp;

								if (plotDist) {
									PlotData data = PlotData.get(dist, metric,
											style, title, type,
											seriesData[i].getDir());
									if (!Config
											.getBoolean("GNUPLOT_DATA_IN_SCRIPT")
											&& !zippedBatches && !zippedRuns) {
										if (aggregatedBatches) {
											data.setDataLocation(
													PlotDataLocation.dataFile,
													Dir.getAggregatedMetricDataDir(
															seriesData[i]
																	.getDir(),
															timestamp, metric)
															+ distFilename);
										} else {
											data.setDataLocation(
													PlotDataLocation.dataFile,
													Dir.getMetricDataDir(
															seriesData[i]
																	.getDir(),
															indizes[i],
															timestamp,
															metric,
															((BatchData) initBatches[i])
																	.getMetrics()
																	.get(metric)
																	.getType())
															+ distFilename);
										}
									}
									dataList.add(data);
								}
								if (plotCdf) {
									PlotData data = PlotData.get(dist, metric,
											style, title, type,
											seriesData[i].getDir());
									data.setPlotAsCdf(true);
									if (!Config
											.getBoolean("GNUPLOT_DATA_IN_SCRIPT")
											&& !zippedBatches && !zippedRuns) {
										if (aggregatedBatches) {
											data.setDataLocation(
													PlotDataLocation.dataFile,
													Dir.getAggregatedMetricDataDir(
															seriesData[i]
																	.getDir(),
															timestamp, metric)
															+ distFilename);
										} else {
											data.setDataLocation(
													PlotDataLocation.dataFile,
													Dir.getMetricDataDir(
															seriesData[i]
																	.getDir(),
															indizes[i],
															timestamp,
															metric,
															((BatchData) initBatches[i])
																	.getMetrics()
																	.get(metric)
																	.getType())
															+ distFilename);
										}
									}
									dataListCdf.add(data);
								}
							}
						}
					}
				}

				// create regular plot
				if (plotDist) {
					PlotData[] data = dataList.toArray(new PlotData[0]);

					Plot p = new Plot(dstDir,
							PlotFilenames.getDistributionPlot(metric, dist),
							PlotFilenames.getDistributionGnuplotScript(metric,
									dist), metric + "." + dist + aggAddition,
							data);

					// set data quantities
					p.setSeriesDataQuantities(seriesDataQuantities);

					// disable datetime
					p.setPlotDateTime(false);

					// add plot to list
					defaultPlots.add(p);
				}

				// create cdf plot
				if (plotCdf) {
					PlotData[] dataCdf = dataListCdf.toArray(new PlotData[0]);

					Plot p = new Plot(dstDir,
							PlotFilenames.getDistributionCdfPlot(metric, dist),
							PlotFilenames.getDistributionCdfGnuplotScript(
									metric, dist), "CDF of " + metric + "."
									+ dist + aggAddition, dataCdf);

					// set data quantities
					p.setSeriesDataQuantities(seriesDataQuantities);

					// set cdf plot
					p.setCdfPlot(true);

					// disable datetime
					p.setPlotDateTime(false);

					// add plot to list
					defaultPlots.add(p);
				}
			}
		}

		// create default nvl plots
		if (Config.getBoolean("DEFAULT_PLOTS_ENABLED")
				&& Config.getBoolean("DEFAULT_PLOT_NODEVALUELISTS")) {
			// log
			Log.info("Plotting default nodevaluelist plots:");

			// list of used metric . dist combinations
			ArrayList<String> usedNvls = new ArrayList<String>();

			// gather values
			for (int i = 0; i < seriesData.length; i++) {
				// metrics
				Collection<String> metricNames;
				if (aggregatedBatches)
					metricNames = ((AggregatedBatch) initBatches[i])
							.getMetrics().getNames();
				else
					metricNames = ((BatchData) initBatches[i]).getMetrics()
							.getNames();

				// iterate over metrics
				for (String metric : metricNames) {
					// nodevaluelists
					Collection<String> nodevaluelistNames;
					if (aggregatedBatches)
						nodevaluelistNames = ((AggregatedBatch) initBatches[i])
								.getMetrics().get(metric).getNodeValues()
								.getNames();
					else
						nodevaluelistNames = ((BatchData) initBatches[i])
								.getMetrics().get(metric).getNodeValues()
								.getNames();

					// iterate over nodevaluelists
					for (String nvl : nodevaluelistNames) {
						if (!usedNvls.contains(metric
								+ PlotConfig.customPlotDomainDelimiter + nvl)) {
							usedNvls.add(metric
									+ PlotConfig.customPlotDomainDelimiter
									+ nvl);
						}
					}
				}
			}

			// create plots
			for (String entry : usedNvls) {
				String[] split = entry
						.split(PlotConfig.customPlotDomainDelimiter);
				int[] seriesDataQuantities = new int[seriesData.length];
				String metric = split[0];
				String nvl = split[1];

				// substitution
				boolean substituteAvailable = false;

				for (PlotConfig pc : customDefaultSubstitutePlots) {
					if (substituteAvailable)
						continue;

					if (pc.getDomains()[0].equals(metric)
							&& pc.getValues()[0].equals(nvl)) {
						Log.info("\tskipping '" + metric + "." + nvl + "'");
						Log.info("\t\t->  replaced by customplot '"
								+ pc.getFilename() + "'");
						substituteAvailable = true;
					}
				}

				// skip if substitution is available
				if (substituteAvailable)
					continue;

				// log
				Log.info("\tplotting '" + metric + "." + nvl + "'");

				ArrayList<PlotData> dataList = null;

				dataList = new ArrayList<PlotData>();

				// iterate over series
				for (int i = 0; i < seriesData.length; i++) {
					String runAddition = "";
					if (!aggregatedBatches)
						runAddition = " @ run." + indizes[i];

					// metrics
					Collection<String> metricNames;
					if (aggregatedBatches)
						metricNames = ((AggregatedBatch) initBatches[i])
								.getMetrics().getNames();
					else
						metricNames = ((BatchData) initBatches[i]).getMetrics()
								.getNames();

					// iterate over metrics
					if (metricNames.contains(metric)) {
						// nodevaluelists
						Collection<String> nodevaluelistNames;
						if (aggregatedBatches)
							nodevaluelistNames = ((AggregatedBatch) initBatches[i])
									.getMetrics().get(metric).getNodeValues()
									.getNames();
						else
							nodevaluelistNames = ((BatchData) initBatches[i])
									.getMetrics().get(metric).getNodeValues()
									.getNames();

						// iterate over nodevaluelists
						if (nodevaluelistNames.contains(nvl)) {

							// set data quantity
							seriesDataQuantities[i] = 1;

							// create plot data
							for (int j = 0; j < batches.length; j++) {
								long timestamp = (long) timestamps[j];

								// skip if series doesnt contain timestamp
								if (!seriesTimestamps[i].contains(timestamp))
									continue;

								String title = seriesData[i].getName()
										+ runAddition + " @ " + timestamp;

								PlotData data = PlotData.get(nvl, metric,
										style, title, type,
										seriesData[i].getDir());
								if (!Config
										.getBoolean("GNUPLOT_DATA_IN_SCRIPT")
										&& !zippedBatches && !zippedRuns) {
									if (aggregatedBatches) {
										data.setDataLocation(
												PlotDataLocation.dataFile,
												Dir.getAggregatedMetricDataDir(
														seriesData[i].getDir(),
														timestamp, metric)
														+ Files.getNodeValueListFilename(nvl));
									} else {
										data.setDataLocation(
												PlotDataLocation.dataFile,
												Dir.getMetricDataDir(
														seriesData[i].getDir(),
														indizes[i],
														timestamp,
														metric,
														((BatchData) initBatches[i])
																.getMetrics()
																.get(metric)
																.getType())
														+ Files.getNodeValueListFilename(nvl));
									}
								}
								dataList.add(data);
							}
						}
					}
				}

				// create plot
				PlotData[] data = dataList.toArray(new PlotData[0]);

				Plot p = new Plot(dstDir, PlotFilenames.getNodeValueListPlot(
						metric, nvl),
						PlotFilenames
								.getNodeValueListGnuplotScript(metric, nvl),
						metric + "." + nvl + aggAddition, data);

				// set data quantities
				p.setSeriesDataQuantities(seriesDataQuantities);

				// set plot date time
				p.setPlotDateTime(false);

				// set nvl sort mode
				p.setNodeValueListOrder(order);
				p.setNodeValueListOrderBy(orderBy);

				// add plot to list
				defaultPlots.add(p);
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

				// tempbatch
				IBatch tempBatch;

				if (aggregatedBatches) {
					tempBatch = AggregatedBatch.readIntelligent(Dir
							.getBatchDataDir(Dir
									.getAggregationDataDir(seriesData[j]
											.getDir()), timestamp), timestamp,
							BatchReadMode.readOnlyDistAndNvl);
				} else {
					tempBatch = BatchData.readIntelligent(Dir.getBatchDataDir(
							Dir.getRunDataDir(seriesData[j].getDir(),
									indizes[j]), timestamp), timestamp,
							BatchReadMode.readOnlyDistAndNvl);
				}

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
	public static void plotSingleValuePlots(SeriesData[] seriesData,
			int[] indizes, String dstDir, String[] batches,
			double[] timestamps, IBatch[] initBatches, boolean plotStatistics,
			ArrayList<PlotConfig> customStatisticPlots,
			boolean plotMetricValues,
			ArrayList<PlotConfig> customMetricValuePlots,
			boolean plotCustomValues, ArrayList<PlotConfig> customValuePlots,
			boolean plotRuntimes, ArrayList<PlotConfig> customRuntimePlots,
			boolean zippedBatches, boolean zippedRuns, PlotType type,
			PlotStyle style, ValueSortMode valueSortMode,
			String[] valueSortList, HashMap<Long, Long> timestampMap)
			throws IOException, InterruptedException {
		boolean aggregatedBatches = false;
		if (initBatches instanceof AggregatedBatch[])
			aggregatedBatches = true;

		// lists of plots
		ArrayList<Plot> defaultPlots = new ArrayList<Plot>();
		ArrayList<Plot> plots = new ArrayList<Plot>();

		// generate statistic plots
		if (plotStatistics) {
			if (customStatisticPlots.size() > 0)
				Log.info("Plotting custom statistic plots:");

			// generate plots and add to customPlot List
			PlottingUtils.generateCustomPlots(customStatisticPlots, plots,
					dstDir, seriesData, indizes, initBatches, style, type,
					valueSortMode, valueSortList, timestampMap);
		}

		// generate custom metric value plots
		if (plotMetricValues) {
			if (customMetricValuePlots.size() > 0)
				Log.info("Plotting custom metric value plots:");

			// generate plots and add to customPlot List
			PlottingUtils.generateCustomPlots(customMetricValuePlots, plots,
					dstDir, seriesData, indizes, initBatches, style, type,
					valueSortMode, valueSortList, timestampMap);
		}

		// generate custom value plots
		if (plotCustomValues) {
			if (customValuePlots.size() > 0)
				Log.info("Plotting custom value plots:");

			// generate plots and add to customPlot list
			PlottingUtils.generateCustomPlots(customValuePlots, plots, dstDir,
					seriesData, indizes, initBatches, style, type,
					valueSortMode, valueSortList, timestampMap);
		}

		// generate runtime plots
		if (plotRuntimes) {
			if (customRuntimePlots.size() > 0)
				Log.info("Plotting custom runtime plots:");

			// generate plots and add to customPlot List
			PlottingUtils.generateCustomPlots(customRuntimePlots, plots,
					dstDir, seriesData, indizes, initBatches, style, type,
					valueSortMode, valueSortList, timestampMap);
		}

		// default plots
		if (Config.getBoolean("DEFAULT_PLOTS_ENABLED")) {
			PlottingUtils.generateMultiSeriesDefaultPlots(defaultPlots, dstDir,
					seriesData, indizes, initBatches, plotStatistics,
					plotMetricValues, plotRuntimes, style, type, valueSortMode,
					valueSortList, customMetricValuePlots, customValuePlots,
					timestampMap);
		}

		// add labels to plots
		for (int i = 0; i < seriesData.length; i++) {
			SeriesData series = seriesData[i];
			String tempDir = "";
			if (aggregatedBatches)
				tempDir = Dir.getAggregationDataDir(series.getDir());
			else
				tempDir = Dir.getRunDataDir(series.getDir(), indizes[i]);

			// read single values
			IBatch[] batchData;
			if (aggregatedBatches) {
				// no labels in aggregated batches
				batchData = new AggregatedBatch[0];
			} else {
				batchData = new BatchData[batches.length];
				for (int j = 0; j < batches.length; j++) {
					long timestamp = Dir.getTimestamp(batches[j]);
					try {
						batchData[j] = BatchData.readIntelligent(
								Dir.getBatchDataDir(tempDir, timestamp),
								timestamp, BatchReadMode.readOnlyLabels);
					} catch (FileNotFoundException e) {
						if (zippedBatches) {
							ZipReader.closeReadFilesystem();
							String remDir = tempDir
									+ Config.get("PREFIX_BATCHDATA_DIR")
									+ timestamp + Config.get("SUFFIX_ZIP_FILE");
							Log.debug("removing unnecessary zipfile: " + remDir);
							Files.delete(new File(remDir));
						}

						batchData[j] = null;
					}
				}
			}

			// add labels to default plots
			for (Plot p : defaultPlots) {
				// add labels
				if (p.isPlotLabels())
					p.addPlotLabels(batchData, seriesData[i].getName());
			}

			// add labels to custom plots
			for (Plot p : plots) {
				// add labels
				if (p.isPlotLabels())
					p.addPlotLabels(batchData, seriesData[i].getName());
			}
		}

		// write script headers
		for (Plot p : defaultPlots)
			p.writeScriptHeader();
		for (Plot p : plots)
			p.writeScriptHeader();

		// add data to plots
		for (int i = 0; i < seriesData.length; i++) {
			SeriesData series = seriesData[i];
			String tempDir = "";
			if (aggregatedBatches)
				tempDir = Dir.getAggregationDataDir(series.getDir());
			else
				tempDir = Dir.getRunDataDir(series.getDir(), indizes[i]);

			// read single values
			IBatch[] batchData;
			if (aggregatedBatches) {
				batchData = new AggregatedBatch[batches.length];
				for (int j = 0; j < batches.length; j++) {
					long timestamp = Dir.getTimestamp(batches[j]);
					try {
						batchData[j] = AggregatedBatch.readIntelligent(
								Dir.getBatchDataDir(tempDir, timestamp),
								timestamp, BatchReadMode.readOnlySingleValues);
					} catch (FileNotFoundException e) {
						// error handling
						if (zippedBatches || zippedRuns) {
							if (ZipReader.isZipOpen())
								ZipReader.closeReadFilesystem();
							String remDir = tempDir
									+ Config.get("PREFIX_BATCHDATA_DIR")
									+ timestamp + Config.get("SUFFIX_ZIP_FILE");
							Log.debug("removing unnecessary zipfile: " + remDir);
							Files.delete(new File(remDir));
						}
						batchData[j] = null;
					}
				}
			} else {
				batchData = new BatchData[batches.length];
				for (int j = 0; j < batches.length; j++) {
					long timestamp = Dir.getTimestamp(batches[j]);
					try {
						batchData[j] = BatchData.readIntelligent(
								Dir.getBatchDataDir(tempDir, timestamp),
								timestamp, BatchReadMode.readOnlySingleValues);
					} catch (FileNotFoundException e) {
						if (zippedBatches) {
							ZipReader.closeReadFilesystem();
							String remDir = tempDir
									+ Config.get("PREFIX_BATCHDATA_DIR")
									+ timestamp + Config.get("SUFFIX_ZIP_FILE");
							Log.debug("removing unnecessary zipfile: " + remDir);
							Files.delete(new File(remDir));
						}

						batchData[j] = null;
					}
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

}
