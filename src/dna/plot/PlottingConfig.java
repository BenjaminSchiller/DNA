package dna.plot;

import java.util.ArrayList;
import java.util.HashMap;

import dna.plot.data.PlotData.DistributionPlotType;
import dna.plot.data.PlotData.NodeValueListOrder;
import dna.plot.data.PlotData.NodeValueListOrderBy;
import dna.plot.data.PlotData.PlotStyle;
import dna.plot.data.PlotData.PlotType;
import dna.series.aggdata.AggregatedBatch;
import dna.series.data.BatchData;
import dna.series.data.IBatch;
import dna.util.Config;
import dna.util.Log;

/**
 * The PlottingConfig is a config object that controls the plotting behaviour.
 * 
 * @author RWilmes
 * @date 11.05.2014
 */
public class PlottingConfig {
	// enumeration for plotflags
	public static enum PlotFlag {
		plotAll, plotStatistics, plotRuntimes, plotMetricValues, plotMetricEntirely, plotDistributions, plotNodeValueLists, plotCustomValues, plotSingleScalarValues, plotMultiScalarValues
	};

	// enumeration for sorting
	public enum ValueSortMode {
		NONE, LIST_FIRST, LIST_LAST, LIST_FIRST_THEN_ALPHABETICAL, ALPHABETICAL_FIRST_THEN_LIST, ALPHABETICAL
	}

	// config
	private PlotType plotType;
	private PlotStyle plotStyle;
	private DistributionPlotType distPlotType;
	private NodeValueListOrder nvlOrder;
	private NodeValueListOrderBy nvlOrderBy;

	private ValueSortMode valueSortMode;
	private String[] valueSortList;

	// combined metric runtimes plot
	private ArrayList<String> generalRuntimes;

	// custom plots
	private ArrayList<PlotConfig> customValuePlots;
	private ArrayList<PlotConfig> customStatisticPlots;
	private ArrayList<PlotConfig> customRuntimePlots;
	private ArrayList<PlotConfig> customMetricValuePlots;
	private ArrayList<PlotConfig> customDistributionPlots;
	private ArrayList<PlotConfig> customNodeValueListPlots;

	// plot flags
	private boolean plotStatistics;
	private boolean plotRuntimes;
	private boolean plotMetricValues;
	private boolean plotDistributions;
	private boolean plotNodeValueLists;
	private boolean plotCustomValues;

	// interval selection
	private long plotFrom;
	private long plotTo;
	private long stepsize;
	private boolean intervalByIndex;
	private HashMap<Long, Long> timestampMap;

	// constructors
	public PlottingConfig(PlotType plotType, PlotStyle plotStyle,
			long timestampFrom, long timestampTo, long stepsize,
			HashMap<Long, Long> timestampMap,
			DistributionPlotType distPlotType, NodeValueListOrder nvlOrder,
			NodeValueListOrderBy nvlOrderBy, ValueSortMode valueSortMode,
			String[] valueSortList, PlotFlag... flags) {
		PlotFlag[] tempFlags;
		if (flags.length == 0) {
			Log.warn("Initializing PlottingConfig without flags, enabling ALL plots!");
			tempFlags = new PlotFlag[] { PlotFlag.plotAll };
		} else {
			tempFlags = flags;
		}

		this.customValuePlots = new ArrayList<PlotConfig>();
		this.customStatisticPlots = new ArrayList<PlotConfig>();
		this.customRuntimePlots = new ArrayList<PlotConfig>();
		this.customMetricValuePlots = new ArrayList<PlotConfig>();
		this.customDistributionPlots = new ArrayList<PlotConfig>();
		this.customNodeValueListPlots = new ArrayList<PlotConfig>();

		this.plotType = plotType;
		this.plotStyle = plotStyle;
		this.plotFrom = timestampFrom;
		this.plotTo = timestampTo;
		this.stepsize = stepsize;
		this.distPlotType = distPlotType;
		this.nvlOrder = nvlOrder;
		this.nvlOrderBy = nvlOrderBy;

		this.valueSortMode = valueSortMode;
		this.valueSortList = valueSortList;

		this.generalRuntimes = new ArrayList<String>();
		this.generalRuntimes.add("total");
		this.generalRuntimes.add("overhead");
		this.generalRuntimes.add("metrics");
		this.generalRuntimes.add("sum");
		this.generalRuntimes.add("batchGeneration");
		this.generalRuntimes.add("graphUpdate");

		this.plotStatistics = false;
		this.plotRuntimes = false;
		this.plotMetricValues = false;
		this.plotDistributions = false;
		this.plotNodeValueLists = false;
		this.plotCustomValues = false;

		this.intervalByIndex = false;

		// check plot flags
		for (PlotFlag flag : tempFlags) {
			switch (flag) {
			case plotAll:
				this.plotStatistics = true;
				this.plotRuntimes = true;
				this.plotMetricValues = true;
				this.plotDistributions = true;
				this.plotNodeValueLists = true;
				this.plotCustomValues = true;
				break;
			case plotStatistics:
				this.plotStatistics = true;
				break;
			case plotRuntimes:
				this.plotRuntimes = true;
				break;
			case plotMetricValues:
				this.plotMetricValues = true;
				break;
			case plotMetricEntirely:
				this.plotMetricValues = true;
				this.plotDistributions = true;
				this.plotNodeValueLists = true;
				break;
			case plotDistributions:
				this.plotDistributions = true;
				break;
			case plotNodeValueLists:
				this.plotNodeValueLists = true;
				break;
			case plotCustomValues:
				this.plotCustomValues = true;
				break;
			case plotMultiScalarValues:
				this.plotDistributions = true;
				this.plotNodeValueLists = true;
				break;
			case plotSingleScalarValues:
				this.plotCustomValues = true;
				this.plotMetricValues = true;
				this.plotRuntimes = true;
				this.plotStatistics = true;
				break;
			}
		}
		// if custom plots are enabled by default, read them
		if (Config.getBoolean("CUSTOM_PLOTS_ENABLED"))
			this.createCustomPlotsFromConfig();
	}

	public PlottingConfig(long timestampFrom, long timestampTo, long stepsize) {
		this(
				Config.getPlotType(PlotConfig.gnuplotDefaultKeyPlotType),
				Config.getPlotStyle(PlotConfig.gnuplotDefaultKeyPlotStyle),
				timestampFrom,
				timestampTo,
				stepsize,
				null,
				Config.getDistributionPlotType(PlotConfig.gnuplotDefaultKeyDistPlotType),
				Config.getNodeValueListOrder(PlotConfig.gnuplotDefaultKeyNodeValueListOrder),
				Config.getNodeValueListOrderBy(PlotConfig.gnuplotDefaultKeyNodeValueListOrderBy),
				Config.getValueSortMode(PlotConfig.gnuplotDefaultKeyValueSortMode),
				Config.keys(PlotConfig.gnuplotDefaultKeyValueSortList),
				PlotFlag.plotAll);
	}

	public PlottingConfig(PlotFlag... flags) {
		this(
				Config.getPlotType(PlotConfig.gnuplotDefaultKeyPlotType),
				Config.getPlotStyle(PlotConfig.gnuplotDefaultKeyPlotStyle),
				0,
				Long.MAX_VALUE,
				1,
				null,
				Config.getDistributionPlotType(PlotConfig.gnuplotDefaultKeyDistPlotType),
				Config.getNodeValueListOrder(PlotConfig.gnuplotDefaultKeyNodeValueListOrder),
				Config.getNodeValueListOrderBy(PlotConfig.gnuplotDefaultKeyNodeValueListOrderBy),
				Config.getValueSortMode(PlotConfig.gnuplotDefaultKeyValueSortMode),
				Config.keys(PlotConfig.gnuplotDefaultKeyValueSortList), flags);
	}

	// class methods
	/**
	 * Creates custom plots from the config. This method will be called by
	 * default on initialization if "CUSTOM_PLOTS_ENABLED" is set as true in the
	 * plotting.properties.
	 * 
	 * Note: Configs will be created depending on the PlotFlag's that the
	 * PlottingConfig object holds.
	 **/
	public void createCustomPlotsFromConfig() {
		if (this.plotStatistics)
			this.customStatisticPlots = PlotConfig.getCustomStatisticPlots();
		if (this.plotRuntimes)
			this.customRuntimePlots = PlotConfig.getCustomRuntimePlots();
		if (this.plotMetricValues)
			this.customMetricValuePlots = PlotConfig
					.getCustomMetricValuePlots();
		if (this.plotDistributions)
			this.customDistributionPlots = PlotConfig
					.getCustomMetricDistributionPlots();
		if (this.plotNodeValueLists)
			this.customNodeValueListPlots = PlotConfig
					.getCustomMetricNodeValueListPlots();
		if (this.plotCustomValues)
			this.customValuePlots = PlotConfig.getCustomValuePlots();
	}

	/**
	 * Checks if the custom plots make sense on the given batches. Plots that
	 * have no domains/values in the given batches will be removed.
	 * 
	 * @param batches
	 *            Batches to be checked.
	 */
	public void checkCustomPlotConfigs(AggregatedBatch[] batches) {
		// log
		Log.infoSep();
		Log.info("Checking for unnecessary custom plots:");

		// only check necessary
		if (this.plotStatistics)
			this.customStatisticPlots = PlottingConfig.checkPlotConfig(
					this.customStatisticPlots, batches);

		if (this.plotMetricValues)
			this.customMetricValuePlots = PlottingConfig.checkPlotConfig(
					this.customMetricValuePlots, batches);

		if (this.plotCustomValues)
			this.customValuePlots = PlottingConfig.checkPlotConfig(
					this.customValuePlots, batches);

		if (this.plotRuntimes)
			this.customRuntimePlots = PlottingConfig.checkPlotConfig(
					this.customRuntimePlots, batches);

		if (this.plotDistributions)
			this.customDistributionPlots = PlottingConfig.checkPlotConfig(
					this.customDistributionPlots, batches);

		if (this.plotNodeValueLists)
			this.customNodeValueListPlots = PlottingConfig.checkPlotConfig(
					this.customNodeValueListPlots, batches);
	}

	/**
	 * Checks if the custom plots make sense on the given batches. Plots that
	 * have no domains/values in the given batches will be removed.
	 * 
	 * @param batches
	 *            Batches to be checked.
	 */
	public void checkCustomPlotConfigs(BatchData[] batches) {
		// log
		Log.infoSep();
		Log.info("Checking for unnecessary custom plots:");

		// only check necessary
		if (this.plotStatistics)
			this.customStatisticPlots = PlottingConfig.checkPlotConfig(
					this.customStatisticPlots, batches);

		if (this.plotMetricValues)
			this.customMetricValuePlots = PlottingConfig.checkPlotConfig(
					this.customMetricValuePlots, batches);

		if (this.plotCustomValues)
			this.customValuePlots = PlottingConfig.checkPlotConfig(
					this.customValuePlots, batches);

		if (this.plotRuntimes)
			this.customRuntimePlots = PlottingConfig.checkPlotConfig(
					this.customRuntimePlots, batches);

		if (this.plotDistributions)
			this.customDistributionPlots = PlottingConfig.checkPlotConfig(
					this.customDistributionPlots, batches);

		if (this.plotNodeValueLists)
			this.customNodeValueListPlots = PlottingConfig.checkPlotConfig(
					this.customNodeValueListPlots, batches);
	}

	/**
	 * Checks if the given PlotConfig's can be plotted on the given batches. A
	 * list of PlotConfig's with only plottable PlotConfig's will be returned.
	 * 
	 * @param configs
	 *            ArrayList of PlotConfig's to be checked.
	 * @param batches
	 *            Batches to be checked with the given PlotConfigs.
	 * @return ArrayList of PlotConfig's plottable on the given Batches.
	 */
	private static ArrayList<PlotConfig> checkPlotConfig(
			ArrayList<PlotConfig> configs, AggregatedBatch[] batches) {
		// new list of configs
		ArrayList<PlotConfig> configsNew = new ArrayList<PlotConfig>();

		// if null, return empty list
		if (configs == null)
			return configsNew;

		// replace wildcards
		PlottingConfig.replaceWildcards(configs, batches);

		// iterate over configs
		for (PlotConfig pc : configs) {
			boolean useful = false;
			String[] values = pc.getValues();
			String[] domains = pc.getDomains();
			int functionCounter = 0;

			// iterate over values & domains
			for (int i = 0; i < values.length && !useful; i++) {
				String value = values[i];
				String domain = domains[i];

				// check if null
				if (value == null || domain == null)
					continue;

				// if expression -> check if domains exist
				if (domain.equals(PlotConfig.customPlotDomainExpression)) {
					String[] split = value.split("\\$");

					// iterate over split
					for (int j = 0; j < split.length; j++) {
						// skip even numbers
						if ((j & 1) == 0)
							continue;

						String[] split2 = split[j]
								.split(PlotConfig.customPlotDomainDelimiter);

						// if no split possible check gen. domain
						if (split2.length == 1) {
							useful = PlottingUtils.isContained(
									pc.getGeneralDomain(), split2[0], batches);
						}

						// if split = 2, check domain and value
						if (split2.length == 2) {
							useful = PlottingUtils.isContained(split2[0],
									split2[1], batches);
						}
					}
					continue;
				}
				// if function -> continue
				if (domain.equals(PlotConfig.customPlotDomainFunction)) {
					functionCounter++;
					if (functionCounter == values.length)
						useful = true;
					continue;
				}

				// check if contained in batches
				if (PlottingUtils.isContained(domain, value, batches)) {
					useful = true;
					continue;
				}
			}

			if (useful) {
				configsNew.add(pc);
			} else {
				Log.info("\tremoving: '" + pc.getFilename() + "'");
			}
		}

		return configsNew;
	}

	/**
	 * Checks if the given PlotConfig's can be plotted on the given batches. A
	 * list of PlotConfig's with only plottable PlotConfig's will be returned.
	 * 
	 * @param configs
	 *            ArrayList of PlotConfig's to be checked.
	 * @param batches
	 *            Batches to be checked with the given PlotConfigs.
	 * @return ArrayList of PlotConfig's plottable on the given Batches.
	 */
	private static ArrayList<PlotConfig> checkPlotConfig(
			ArrayList<PlotConfig> configs, BatchData[] batches) {
		// new list of configs
		ArrayList<PlotConfig> configsNew = new ArrayList<PlotConfig>();

		// if null, return empty list
		if (configs == null)
			return configsNew;

		// replace wildcards
		PlottingConfig.replaceWildcards(configs, batches);

		// iterate over configs
		for (PlotConfig pc : configs) {
			boolean useful = false;
			String[] values = pc.getValues();
			String[] domains = pc.getDomains();
			int functionCounter = 0;

			// iterate over values & domains
			for (int i = 0; i < values.length && !useful; i++) {
				String value = values[i];
				String domain = domains[i];

				// check if null
				if (value == null || domain == null)
					continue;

				// if expression -> check if domains exist
				if (domain.equals(PlotConfig.customPlotDomainExpression)) {
					String[] split = value.split("\\$");

					// iterate over split
					for (int j = 0; j < split.length; j++) {
						// skip even numbers
						if ((j & 1) == 0)
							continue;

						String[] split2 = split[j]
								.split(PlotConfig.customPlotDomainDelimiter);

						// if no split possible check gen. domain
						if (split2.length == 1) {
							useful = PlottingUtils.isContained(
									pc.getGeneralDomain(), split2[0], batches);
						}

						// if split = 2, check domain and value
						if (split2.length == 2) {
							useful = PlottingUtils.isContained(split2[0],
									split2[1], batches);
						}
					}
					continue;
				}
				// if function -> continue
				if (domain.equals(PlotConfig.customPlotDomainFunction)) {
					functionCounter++;
					if (functionCounter == values.length)
						useful = true;
					continue;
				}

				// check if contained in batches
				if (PlottingUtils.isContained(domain, value, batches)) {
					useful = true;
					continue;
				}
			}

			if (useful) {
				configsNew.add(pc);
			} else {
				Log.info("\tremoving: '" + pc.getFilename() + "'");
			}
		}

		return configsNew;
	}

	/**
	 * Replaces all wildcards in the given config with the corresponding values
	 * from the given batches, where each batch represents the init batch of one
	 * series.
	 * 
	 * @param config
	 *            Config to be altered.
	 * @param batches
	 *            Array of init-batches holding the names of the values which
	 *            will be inserted into the config.
	 */
	private static void replaceWildcards(ArrayList<PlotConfig> config,
			IBatch[] batches) {
		// if config null or empty, return
		if (config == null || config.size() == 0)
			return;

		/*
		 * GATHER POSSIBLE VALUES
		 */
		ArrayList<String> stats = new ArrayList<String>();
		ArrayList<String> metRuntimes = new ArrayList<String>();
		ArrayList<String> genRuntimes = new ArrayList<String>();
		ArrayList<String> metrics = new ArrayList<String>();
		ArrayList<String>[][] metricField;

		// gather available values
		if (batches instanceof AggregatedBatch[]) {
			// aggregated batches
			metricField = gatherValuesFromAggregatedBatches(
					(AggregatedBatch[]) batches, stats, metRuntimes,
					genRuntimes, metrics);
		} else {
			// normal batches
			metricField = gatherValuesFromBatches((BatchData[]) batches, stats,
					metRuntimes, genRuntimes, metrics);
		}

		/*
		 * REPLACE WILDCARDS
		 */
		for (PlotConfig cfg : config) {
			// if plot all is false, no wildcard is included -> skip
			if (!cfg.isPlotAll())
				continue;

			// replace wildcards
			replaceWildcards(cfg, stats, metRuntimes, genRuntimes, metrics,
					metricField[0], metricField[1], metricField[2]);
		}
	}

	/** Gathers all available values from the aggregated batches. **/
	@SuppressWarnings("unchecked")
	private static ArrayList<String>[][] gatherValuesFromAggregatedBatches(
			AggregatedBatch[] batches, ArrayList<String> stats,
			ArrayList<String> metRuntimes, ArrayList<String> genRuntimes,
			ArrayList<String> metrics) {
		for (AggregatedBatch b : batches) {
			// statistics
			for (String s : b.getValues().getNames()) {
				if (!stats.contains(s))
					stats.add(s);
			}
			// metric runtimes
			for (String r : b.getMetricRuntimes().getNames()) {
				if (!metRuntimes.contains(r))
					metRuntimes.add(r);
			}
			// general runtimes
			for (String r : b.getGeneralRuntimes().getNames()) {
				if (!genRuntimes.contains(r))
					genRuntimes.add(r);
			}
			// metric names
			for (String m : b.getMetrics().getNames()) {
				if (!metrics.contains(m))
					metrics.add(m);
			}
		}

		// metric values
		ArrayList<String>[] metricValues = new ArrayList[metrics.size()];
		ArrayList<String>[] metricDistributions = new ArrayList[metrics.size()];
		ArrayList<String>[] metricNodeValues = new ArrayList[metrics.size()];

		for (AggregatedBatch b : batches) {
			for (int i = 0; i < metrics.size(); i++) {
				String metric = metrics.get(i);
				metricValues[i] = new ArrayList<String>();
				metricDistributions[i] = new ArrayList<String>();
				metricNodeValues[i] = new ArrayList<String>();

				if (b.getMetrics().getNames().contains(metric)) {
					// values
					for (String v : b.getMetrics().get(metric).getValues()
							.getNames()) {
						if (!metricValues[i].contains(v))
							metricValues[i].add(v);
					}
					// distributions
					for (String d : b.getMetrics().get(metric)
							.getDistributions().getNames()) {
						if (!metricDistributions[i].contains(d))
							metricDistributions[i].add(d);
					}
					// nodevaluelists
					for (String n : b.getMetrics().get(metric).getNodeValues()
							.getNames()) {
						if (!metricNodeValues[i].contains(n))
							metricNodeValues[i].add(n);
					}
				}
			}
		}

		// return
		return new ArrayList[][] { metricValues, metricDistributions,
				metricNodeValues };
	}

	/** Gathers all available values from the batches. **/
	@SuppressWarnings("unchecked")
	private static ArrayList<String>[][] gatherValuesFromBatches(
			BatchData[] batches, ArrayList<String> stats,
			ArrayList<String> metRuntimes, ArrayList<String> genRuntimes,
			ArrayList<String> metrics) {
		for (BatchData b : batches) {
			// statistics
			for (String s : b.getValues().getNames()) {
				if (!stats.contains(s))
					stats.add(s);
			}
			// metric runtimes
			for (String r : b.getMetricRuntimes().getNames()) {
				if (!metRuntimes.contains(r))
					metRuntimes.add(r);
			}
			// general runtimes
			for (String r : b.getGeneralRuntimes().getNames()) {
				if (!genRuntimes.contains(r))
					genRuntimes.add(r);
			}
			// metric names
			for (String m : b.getMetrics().getNames()) {
				if (!metrics.contains(m))
					metrics.add(m);
			}
		}

		// metric values
		ArrayList<String>[] metricValues = new ArrayList[metrics.size()];
		ArrayList<String>[] metricDistributions = new ArrayList[metrics.size()];
		ArrayList<String>[] metricNodeValues = new ArrayList[metrics.size()];

		for (BatchData b : batches) {
			for (int i = 0; i < metrics.size(); i++) {
				String metric = metrics.get(i);
				metricValues[i] = new ArrayList<String>();
				metricDistributions[i] = new ArrayList<String>();
				metricNodeValues[i] = new ArrayList<String>();

				if (b.getMetrics().getNames().contains(metric)) {
					// values
					for (String v : b.getMetrics().get(metric).getValues()
							.getNames()) {
						if (!metricValues[i].contains(v))
							metricValues[i].add(v);
					}
					// distributions
					for (String d : b.getMetrics().get(metric)
							.getDistributions().getNames()) {
						if (!metricDistributions[i].contains(d))
							metricDistributions[i].add(d);
					}
					// nodevaluelists
					for (String n : b.getMetrics().get(metric).getNodeValues()
							.getNames()) {
						if (!metricNodeValues[i].contains(n))
							metricNodeValues[i].add(n);
					}
				}
			}
		}

		// return arrays
		return new ArrayList[][] { metricValues, metricDistributions,
				metricNodeValues };
	}

	/** Actual wildcard replacement takes places here. **/
	private static void replaceWildcards(PlotConfig cfg,
			ArrayList<String> stats, ArrayList<String> metRuntimes,
			ArrayList<String> genRuntimes, ArrayList<String> metrics,
			ArrayList<String>[] metricValues,
			ArrayList<String>[] metricDistributions,
			ArrayList<String>[] metricNodeValues) {

		String[] values = cfg.getValues();
		String[] domains = cfg.getDomains();

		ArrayList<String> vList = new ArrayList<String>();
		ArrayList<String> dList = new ArrayList<String>();

		// iterate over all values
		for (int i = 0; i < values.length; i++) {
			String value = values[i];
			String domain = domains[i];
			String wildcard = PlotConfig.customPlotWildcard;

			// if function or no wildcard included, no replacement
			if (domain.equals(PlotConfig.customPlotDomainFunction)
					|| (!value.contains(wildcard) && !domain.contains(wildcard))) {
				vList.add(value);
				dList.add(domain);
				continue;
			}

			// if expression
			if (domain.equals(PlotConfig.customPlotDomainExpression)) {
				// case mathematical expression
				String generalDomain = cfg.getGeneralDomain();
				String[] split = value.split("\\$");
				// statistics
				if (generalDomain.equals(PlotConfig.customPlotDomainStatistics)) {
					for (String v : stats) {
						String string = "";
						for (int j = 0; j < split.length; j++) {
							if ((j & 1) == 0) {
								// even
								string += split[j];
							} else {
								// odd
								string += "$" + v + "$";
							}
						}
						vList.add(string);
						dList.add(domain);
					}
				} else if (generalDomain
						.equals(PlotConfig.customPlotDomainGeneralRuntimes)
						|| generalDomain
								.equals(PlotConfig.customPlotDomainRuntimes)) {
					// general runtimes
					for (String v : genRuntimes) {
						// skip graphgeneration
						if (v.equals("graphGeneration"))
							continue;

						String string = "";
						for (int j = 0; j < split.length; j++) {
							if ((j & 1) == 0) {
								// even
								string += split[j];
							} else {
								// odd
								string += "$" + v + "$";
							}
						}
						vList.add(string);
						dList.add(domain);
					}
				} else if (generalDomain
						.equals(PlotConfig.customPlotDomainMetricRuntimes)
						|| generalDomain
								.equals(PlotConfig.customPlotDomainRuntimes)) {
					// metric runtimes
					for (String v : metRuntimes) {
						String string = "";
						for (int j = 0; j < split.length; j++) {
							if ((j & 1) == 0) {
								// even
								string += split[j];
							} else {
								// odd
								string += "$" + v + "$";
							}
						}
						vList.add(string);
						dList.add(domain);
					}
				} else if (generalDomain.equals(PlotConfig.customPlotWildcard)) {
					// case domain is wildcard
					String[] split2 = value.split("\\:");
					if (split2.length > 2) {
						continue;
					}
					String valueTemp = PlottingUtils
							.getValueFromExpression(split2[1]);

					// statistics
					for (String v : stats) {
						if (v.equals(valueTemp)) {
							vList.add(PlottingUtils
									.replaceDomainWildcardInsideExpression(
											value,
											PlotConfig.customPlotDomainStatistics));
							dList.add(PlotConfig.customPlotDomainExpression);
						}
					}

					// general runtimes
					for (String v : genRuntimes) {
						if (v.equals(valueTemp)) {
							vList.add(PlottingUtils
									.replaceDomainWildcardInsideExpression(
											value,
											PlotConfig.customPlotDomainGeneralRuntimes));
							dList.add(PlotConfig.customPlotDomainExpression);
						}
					}

					// metric runtimes
					for (String v : metRuntimes) {
						if (v.equals(valueTemp)) {
							vList.add(PlottingUtils
									.replaceDomainWildcardInsideExpression(
											value,
											PlotConfig.customPlotDomainMetricRuntimes));
							dList.add(PlotConfig.customPlotDomainExpression);
						}
					}

					// metric values
					for (int j = 0; j < metrics.size(); j++) {
						String metric = metrics.get(j);
						if (metricValues[j].contains(valueTemp)) {
							vList.add(PlottingUtils
									.replaceDomainWildcardInsideExpression(
											value, metric));
							dList.add(PlotConfig.customPlotDomainExpression);
						}
					}
				} else {
					// metric value
					if (metrics.contains(generalDomain)) {
						int index = metrics.indexOf(generalDomain);
						for (String v : metricValues[index]) {
							String string = "";
							for (int j = 0; j < split.length; j++) {
								if ((j & 1) == 0) {
									// even
									string += split[j];
								} else {
									// odd
									string += "$" + v + "$";
								}
							}
							vList.add(string);
							dList.add(domain);
						}
					}
				}
			} else if (domain.equals(wildcard)) {
				// no mathematical expression
				// case domain is wildcard

				// statistics
				for (String v : stats) {
					if (v.equals(value)) {
						vList.add(v);
						dList.add(PlotConfig.customPlotDomainStatistics);
					}
				}

				// general runtimes
				for (String v : genRuntimes) {
					if (v.equals(value)) {
						vList.add(v);
						dList.add(PlotConfig.customPlotDomainGeneralRuntimes);
					}
				}

				// metric runtimes
				for (String v : metRuntimes) {
					if (v.equals(value)) {
						vList.add(v);
						dList.add(PlotConfig.customPlotDomainMetricRuntimes);
					}
				}

				// metrics
				for (int j = 0; j < metrics.size(); j++) {
					String metric = metrics.get(j);
					if (metricValues[j].contains(value)) {
						vList.add(value);
						dList.add(metric);
					}
				}
			} else {
				// domain is no wildcard, only replace wildcards
				if (domain.equals(PlotConfig.customPlotDomainStatistics)) {
					// statistics
					for (String v : stats) {
						vList.add(value.replace(wildcard, v));
						dList.add(domain);
					}
				} else if (domain
						.equals(PlotConfig.customPlotDomainGeneralRuntimes)
						|| domain.equals(PlotConfig.customPlotDomainRuntimes)) {
					// general runtimes
					for (String v : genRuntimes) {
						// skip graphgeneration
						if (v.equals("graphGeneration"))
							continue;

						vList.add(value.replace(wildcard, v));
						dList.add(domain);
					}
				} else if (domain
						.equals(PlotConfig.customPlotDomainMetricRuntimes)
						|| domain.equals(PlotConfig.customPlotDomainRuntimes)) {
					// metric runtimes
					for (String v : metRuntimes) {
						vList.add(value.replace(wildcard, v));
						dList.add(domain);
					}
				} else {
					// metric value
					if (metrics.contains(domain)) {
						int index = metrics.indexOf(domain);
						for (String v : metricValues[index]) {
							vList.add(value.replace(wildcard, v));
							dList.add(domain);
						}
					}
				}

			}
		}

		// set new values and domains
		cfg.setValues(vList.toArray(new String[0]));
		cfg.setDomains(dList.toArray(new String[0]));
	}

	/**
	 * Clones the PlottingConfig object and overwrites the plot flags by the
	 * given PlotFlags.
	 * 
	 * @param flags
	 *            PlotFlags to be set in the new PlottingConfig object.
	 * @return
	 */
	public PlottingConfig clone(PlotFlag... flags) {
		// set plot flags
		PlottingConfig tempConfig = new PlottingConfig(flags);

		// set properties
		tempConfig.setDistPlotType(this.getDistPlotType());
		tempConfig.setNvlOrder(this.getNvlOrder());
		tempConfig.setNvlOrderBy(this.getNvlOrderBy());
		tempConfig.setPlotStyle(this.getPlotStyle());
		tempConfig.setPlotType(this.getPlotType());

		// set interval
		if (this.isIntervalByIndex())
			tempConfig.setPlotIntervalByIndex((int) this.getPlotFrom(),
					(int) this.getPlotTo(), (int) this.getStepsize());
		else
			tempConfig.setPlotInterval(this.getPlotFrom(), this.getPlotTo(),
					this.getStepsize());

		// if custom plots are enabled by default, read them
		if (Config.getBoolean("CUSTOM_PLOTS_ENABLED"))
			tempConfig.createCustomPlotsFromConfig();

		return tempConfig;
	}

	// getters and setters
	public PlotType getPlotType() {
		return plotType;
	}

	public void setPlotType(PlotType plotType) {
		this.plotType = plotType;
	}

	public PlotStyle getPlotStyle() {
		return plotStyle;
	}

	public void setPlotStyle(PlotStyle plotStyle) {
		this.plotStyle = plotStyle;
	}

	public ValueSortMode getValueSortMode() {
		return valueSortMode;
	}

	public void setValueSortMode(ValueSortMode valueSortMode) {
		this.valueSortMode = valueSortMode;
	}

	public String[] getValueSortList() {
		return valueSortList;
	}

	public void setValueSortList(String[] valueSortList) {
		this.valueSortList = valueSortList;
	}

	public DistributionPlotType getDistPlotType() {
		return distPlotType;
	}

	public void setDistPlotType(DistributionPlotType distPlotType) {
		this.distPlotType = distPlotType;
	}

	public NodeValueListOrder getNvlOrder() {
		return nvlOrder;
	}

	public void setNvlOrder(NodeValueListOrder nvlOrder) {
		this.nvlOrder = nvlOrder;
	}

	public NodeValueListOrderBy getNvlOrderBy() {
		return nvlOrderBy;
	}

	public void setNvlOrderBy(NodeValueListOrderBy nvlOrderBy) {
		this.nvlOrderBy = nvlOrderBy;
	}

	public boolean isPlotStatistics() {
		return plotStatistics;
	}

	public void setPlotStatistics(boolean plotStatistics) {
		this.plotStatistics = plotStatistics;
	}

	public boolean isPlotRuntimes() {
		return plotRuntimes;
	}

	public void setPlotRuntimes(boolean plotRuntimes) {
		this.plotRuntimes = plotRuntimes;
	}

	public boolean isPlotMetricValues() {
		return plotMetricValues;
	}

	public void setPlotMetricValues(boolean plotMetricValues) {
		this.plotMetricValues = plotMetricValues;
	}

	public boolean isPlotDistributions() {
		return plotDistributions;
	}

	public void setPlotDistributions(boolean plotDistributions) {
		this.plotDistributions = plotDistributions;
	}

	public boolean isPlotNodeValueLists() {
		return plotNodeValueLists;
	}

	public void setPlotNodeValueLists(boolean plotNodeValueLists) {
		this.plotNodeValueLists = plotNodeValueLists;
	}

	public void setPlotCustomValues(boolean plotCustomValues) {
		this.plotCustomValues = plotCustomValues;
	}

	public void setTimestampMap(HashMap<Long, Long> timestampMap) {
		this.timestampMap = timestampMap;
	}

	/**
	 * Sets the plotting interval [timestampFrom:timestampTo] with the specified
	 * stepsize. Intervals are based on the timestamps of the batches.
	 * 
	 * @param timestamoFrom
	 *            Timestamp of first batch to be plotted.
	 * @param timestampTo
	 *            Timestamp of last batch to be plotted.
	 * @param stepsize
	 *            Stepsize between batches.
	 */
	public void setPlotInterval(long timestampFrom, long timestampTo,
			long stepsize) {
		this.plotFrom = timestampFrom;
		this.plotTo = timestampTo;
		this.stepsize = stepsize;
		this.intervalByIndex = false;
	}

	/**
	 * Sets the plotting interval [indexFrom:indexTo] with the specified
	 * stepsize. Intervals are based on batches indexes.
	 * 
	 * @param indexFrom
	 *            Index of first batch to be plotted.
	 * @param indexTo
	 *            Index of last batch to be plotted.
	 * @param stepsize
	 *            Stepsize between batches.
	 */
	public void setPlotIntervalByIndex(int indexFrom, int indexTo, int stepsize) {
		this.plotFrom = indexFrom;
		this.plotTo = indexTo;
		this.stepsize = stepsize;
		this.intervalByIndex = true;
	}

	public long getPlotFrom() {
		return plotFrom;
	}

	public long getPlotTo() {
		return plotTo;
	}

	public long getStepsize() {
		return stepsize;
	}

	public boolean isIntervalByIndex() {
		return intervalByIndex;
	}

	public ArrayList<String> getGeneralRuntimes() {
		return this.generalRuntimes;
	}

	public ArrayList<PlotConfig> getCustomStatisticPlots() {
		return customStatisticPlots;
	}

	public ArrayList<PlotConfig> getCustomRuntimePlots() {
		return customRuntimePlots;
	}

	public ArrayList<PlotConfig> getCustomMetricValuePlots() {
		return customMetricValuePlots;
	}

	public ArrayList<PlotConfig> getCustomDistributionPlots() {
		return customDistributionPlots;
	}

	public ArrayList<PlotConfig> getCustomNodeValueListPlots() {
		return customNodeValueListPlots;
	}

	public ArrayList<PlotConfig> getCustomValuePlots() {
		return customValuePlots;
	}

	public boolean isPlotCustomValues() {
		return plotCustomValues;
	}

	public HashMap<Long, Long> getTimestampMap() {
		return timestampMap;
	}

}
