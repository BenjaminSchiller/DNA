package dna.util.network;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import argList.ArgList;
import argList.types.array.StringArrayArg;
import argList.types.atomic.BooleanArg;
import argList.types.atomic.EnumArg;
import argList.types.atomic.IntArg;
import argList.types.atomic.StringArg;
import dna.graph.datastructures.GDS;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.network.EmptyNetwork;
import dna.graph.generators.network.NetflowBatch;
import dna.graph.weights.Weight.WeightSelection;
import dna.graph.weights.multi.DoubleMultiWeight;
import dna.graph.weights.network.NetworkNodeWeight;
import dna.io.network.netflow.NetflowEvent.NetflowDirection;
import dna.io.network.netflow.NetflowEvent.NetflowEventField;
import dna.io.network.netflow.NetflowEventReader;
import dna.io.network.netflow.darpa.DarpaNetflowReader;
import dna.labels.labeler.Labeler;
import dna.labels.labeler.LabelerNotApplicableException;
import dna.labels.labeler.attacks.DarpaAttackLabeler;
import dna.metrics.Metric;
import dna.metrics.MetricNotApplicableException;
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
import dna.series.AggregationException;
import dna.series.Series;
import dna.series.data.SeriesData;
import dna.updates.generators.BatchGenerator;
import dna.util.Config;
import dna.util.Log;
import dna.util.network.DatasetUtils.TimestampFormat;
import dna.visualization.BatchHandler.ZipMode;
import dna.visualization.graph.GraphVisualization;
import dna.visualization.graph.toolTips.infoLabel.NetworkNodeKeyLabel;

/**
 * The NetflowAnalysis class is used as the main-class for actual analysis using
 * gIDS. It can be build using the specified build file. <br>
 * <br>
 * 
 * This class makes use of ArgList in order to create a runnable .jar with a
 * convenient arguments interface.
 * 
 * @author Rwilmes
 * 
 */
public class NetflowAnalysis {

	public enum NodeWeightValue {
		numberOfNetflowsIn, numberOfNetflowsOut, PacketsIn, PacketsOut, BytesIn, BytesOut
	};

	public enum EdgeWeightValue {
		numberOfNetflows, Packets, Bytes
	};

	/*
	 * MAIN
	 */
	public static void main(String[] args) throws IOException, ParseException,
			AggregationException, MetricNotApplicableException,
			LabelerNotApplicableException {
		ArgList<NetflowAnalysis> argList = new ArgList<NetflowAnalysis>(
				NetflowAnalysis.class,
				new StringArg("srcDir", "dir of the source data"),
				new StringArg("srcFilename",
						"filename of the dataset sourcefile"),
				new StringArg("dstDir", "dir of the destination data"),
				new StringArg("name", "name of the destination data, set "
						+ '"' + "null" + '"'
						+ " for automatic name-setting based on model"),
				new StringArg(
						"descr",
						"description/version of the generated run, will be added as suffix to the destination name"),
				new BooleanArg("writeDistributions",
						"if true distributions will be written to fs"),
				new IntArg("dataOffset",
						"offset to be added to the data timestamps in seconds"),
				new IntArg("batchWindow",
						"length of a batch timewindow in seconds"),
				new IntArg("edgeLifeTime", "lifetime of an edge in seconds"),
				new StringArrayArg(
						"edges",
						"edges to be added to the graph of format: NetflowEventField-NetflowEventField-...-NetflowEventField. Possible NetflowEventFields are: Bytes, BytesToDst, BytesToSrc, ConnectionState, Date, Direction, DstAddress, DstPort, Duration, Flags, Label, None, numberOfNetflows, Packets, PacketsToDst, PacketsToSrc, Protocol, SrcAddress, SrcPort, Time",
						";"),
				new StringArrayArg(
						"edgeDirections",
						"directions of the edges to be added, possible values: bw, fw",
						";"),
				new StringArrayArg(
						"edgeWeights",
						"edgeWeights to be added to the graph, possible values: numberOfNetflows, Packets, Bytes",
						";"),
				new StringArrayArg(
						"nodeWeights",
						"nodeWeights to be added to the graph, possible values: numberOfNetflowsIn, numberOfNetflowsOut, PacketsIn, PacketsOut, BytesIn, BytesOut",
						";"),
				new EnumArg(
						"timestamp-format",
						"toggles if timestamp (dd-MM-yyyy HH:mm:ss) or DARPA98 week-day (w-d HH:mm:ss) will be used by the timestamp fields",
						TimestampFormat.values()),
				new StringArg("from", "starting timestamp"),
				new StringArg("to", "maximum timestamp"),
				new StringArg("attackList",
						"path to the attack-list file to be used"),
				new BooleanArg("enableVis",
						"true to enable graph-visualization"),
				new EnumArg("zip-mode", "zip mode of the data to be plotted",
						ZipMode.values()),
				new StringArrayArg(
						"metrics",
						"list of metrics to be computed with format: [class-path]+[_host/_port/_all (optional)]. Instead of metrics one may also add the following flags : metricsAll, metricsDefaultAll, metricsDefaultHosts, metricsDefaultPorts to add predefined metrics.",
						";"));
		NetflowAnalysis d = argList.getInstance(args);
		d.generate();
	}

	/*
	 * GENERATION CLASS
	 */
	// general
	protected String srcDir;
	protected String srcFilename;
	protected String dstDir;
	protected String name;
	protected String descr;
	protected Boolean writeDistributions;

	// modeling
	protected int batchLengthSeconds;
	protected int edgeLifeTimeSeconds;

	protected NetflowEventField[][] edges;
	protected NetflowDirection[] edgeDirections;
	protected EdgeWeightValue[] edgeWeights;
	protected NodeWeightValue[] nodeWeights;

	// timestamp
	protected DateTimeFormatter fmt = DateTimeFormat
			.forPattern("dd-MM-yyyy-HH:mm:ss");
	protected DateTime from;
	protected DateTime to;

	protected int dataOffsetSeconds;

	protected String attackListPath;

	protected boolean enableVis;

	protected Metric[] metrics;

	/** Constructor **/
	public NetflowAnalysis(String srcDir, String srcFilename, String dstDir,
			String name, String descr, Boolean writeDistributions,
			Integer dataOffsetSeconds, Integer batchLengthSeconds,
			Integer edgeLifeTimeSeconds, String[] edges,
			String[] edgeDirections, String[] edgeWeights,
			String[] nodeWeights, String timestampFormat, String from,
			String to, String attackListPath, Boolean enableVis,
			String zipMode, String[] metrics) {
		this.srcDir = srcDir;
		this.srcFilename = srcFilename;
		this.dstDir = dstDir;
		if (name == null || name.equals("null")) {
			this.name = generateName(edges, edgeDirections, edgeWeights,
					nodeWeights, batchLengthSeconds, edgeLifeTimeSeconds);
		} else {
			this.name = name;
		}

		this.descr = descr;
		this.writeDistributions = writeDistributions;
		this.dataOffsetSeconds = dataOffsetSeconds;
		this.batchLengthSeconds = batchLengthSeconds;
		this.edgeLifeTimeSeconds = edgeLifeTimeSeconds;

		this.edges = parseNetflowEventFields(edges);
		this.edgeDirections = parseEdgeDirections(edgeDirections);
		this.edgeWeights = parseEdgeWeightValues(edgeWeights);
		this.nodeWeights = parseNodeWeightValues(nodeWeights);

		// timestamps
		if (timestampFormat.equals("timestamp")) {
			this.from = this.fmt.parseDateTime(from);
			this.to = this.fmt.parseDateTime(to);
		}
		if (timestampFormat.equals("week_day")) {
			String dateFrom = DatasetUtils.getDarpaDate(
					Integer.parseInt("" + from.charAt(0)),
					Integer.parseInt("" + from.charAt(2)));
			this.from = this.fmt.parseDateTime(dateFrom + "-"
					+ from.substring(4));
			String dateTo = DatasetUtils.getDarpaDate(
					Integer.parseInt("" + to.charAt(0)),
					Integer.parseInt("" + to.charAt(2)));
			this.to = this.fmt.parseDateTime(dateTo + "-" + to.substring(4));
		}

		this.attackListPath = attackListPath;
		this.enableVis = enableVis;

		ZipMode zipM = ZipMode.valueOf(zipMode);
		switch (zipM) {
		case batches:
			Config.zipBatches();
			break;
		case runs:
			Config.zipRuns();
			break;
		case none:
			Config.zipNone();
			break;
		}

		// metrics
		ArrayList<Metric> metricList = new ArrayList<Metric>();

		for (int i = 0; i < metrics.length; i++) {
			String classPath = metrics[i];

			// metric-flag cases
			if (classPath.equals("metricsAll")) {
				addMetricsToList(metricList, NetflowAnalysis.metricsAll);
			} else if (classPath.equals("metricsDefaultAll")) {
				addMetricsToList(metricList, NetflowAnalysis.metricsDefaultAll);
			} else if (classPath.equals("metricsDefaultHosts")) {
				addMetricsToList(metricList,
						NetflowAnalysis.metricsDefaultHostOnly);
			} else if (classPath.equals("metricsDefaultPorts")) {
				addMetricsToList(metricList,
						NetflowAnalysis.metricsDefaultPortOnly);
			} else {
				// normal metric-cases
				double binsize = 0;
				int index = -1;

				if (classPath.contains("_bs")) {
					String[] splits = classPath.split("_");
					binsize = Double.parseDouble(splits[splits.length - 1]
							.replaceAll("bs", ""));
					classPath = splits[0];
					for (int j = 1; j < splits.length - 1; j++) {
						classPath += "_" + splits[j];
					}
				}

				if (classPath.contains("_i")) {
					String[] splits = classPath.split("_");
					index = Integer.parseInt(splits[splits.length - 1]
							.replaceAll("i", ""));
					classPath = splits[0];
					for (int j = 1; j < splits.length - 1; j++) {
						classPath += "_" + splits[j];
					}
				}

				if (classPath.endsWith("_host")) {
					metricList.add(instantiateMetric(
							classPath.replaceAll("_host", ""), "HOST", binsize,
							index));
				} else if (classPath.endsWith("_port")) {
					metricList.add(instantiateMetric(
							classPath.replaceAll("_port", ""), "PORT", binsize,
							index));
				} else if (classPath.endsWith("_prot")) {
					metricList.add(instantiateMetric(
							classPath.replaceAll("_prot", ""), "PROT", binsize,
							index));
				} else if (classPath.endsWith("_all")) {
					metricList.add(instantiateMetric(
							classPath.replaceAll("_all", ""), null, binsize,
							index));
					metricList.add(instantiateMetric(
							classPath.replaceAll("_all", ""), "HOST", binsize,
							index));
					metricList.add(instantiateMetric(
							classPath.replaceAll("_all", ""), "PORT", binsize,
							index));
					metricList.add(instantiateMetric(
							classPath.replaceAll("_all", ""), "PROT", binsize,
							index));
				} else {
					metricList.add(instantiateMetric(classPath, null, binsize,
							index));
				}
			}
		}

		this.metrics = metricList.toArray(new Metric[metricList.size()]);
	}

	/*
	 * GENERATION
	 */
	/** Generation method. **/
	public void generate() throws IOException, ParseException,
			AggregationException, MetricNotApplicableException,
			LabelerNotApplicableException {
		Log.info("generating data from '" + srcDir + srcFilename + "'");
		Log.info("to:\t" + dstDir + name);
		Log.info("batch window:\t" + batchLengthSeconds + "s");
		Log.info("edgeLifeTime:\t" + edgeLifeTimeSeconds + "s");
		Log.info("descr:\t" + descr);
		Log.info("write dists:\t" + writeDistributions);
		if (from != null)
			Log.info("from:\t\t" + from.toString());
		if (to != null)
			Log.info("to:\t\t" + to.toString());
		Log.info("offset:\t" + dataOffsetSeconds);
		Log.info("attack-list:\t" + attackListPath);
		Log.info("enable-vis:\t" + enableVis);

		Log.infoSep();
		Log.info("edges:");
		for (int i = 0; i < edges.length; i++) {
			String buff = "\t";
			switch (edgeDirections[i]) {
			case backward:
				buff += "<--\t";
				break;
			case forward:
				buff += "-->\t";
				break;
			}

			for (int j = 0; j < edges[i].length; j++) {
				if (j > 0)
					buff += "-";
				buff += edges[i][j].toString();
			}
			Log.info(buff);
		}

		Log.info("edgeWeights:");
		for (int i = 0; i < this.edgeWeights.length; i++) {
			Log.info("\t" + i + "\t" + this.edgeWeights[i].toString());
		}

		Log.info("nodeWeights:");
		for (int i = 0; i < this.nodeWeights.length; i++) {
			Log.info("\t" + i + "\t" + this.nodeWeights[i].toString());
		}
		Log.infoSep();
		Log.info("metrics:");
		for (Metric m : this.metrics)
			Log.info("\t" + m.getName());
		Log.infoSep();

		// limit extra value generation
		Config.overwrite("EXTRA_VALUE_DISTRIBUTION_PERCENT",
				"99, 98, 97, 96, 95, 90, 85, 80, 70, 50");

		String destinationName = name;
		if (this.descr != null && !this.descr.equals("null"))
			destinationName += "_" + this.descr;

		String destinationDir = dstDir + destinationName + "/";

		SeriesData sd = generate(srcDir, srcFilename, destinationDir,
				destinationName, writeDistributions, dataOffsetSeconds,
				batchLengthSeconds, edgeLifeTimeSeconds, from, to,
				attackListPath, enableVis, metrics, edges, edgeDirections,
				edgeWeights, nodeWeights);
	}

	/** Actual DNA Series generation. **/
	public static SeriesData generate(String srcDir, String srcFilename,
			String dstDir, String name, boolean writeDistributions,
			int dataOffsetSeconds, int batchLengthSeconds,
			int edgeLifeTimeSeconds, DateTime from, DateTime to,
			String attackListPath, boolean enableVis, Metric[] metrics,
			NetflowEventField[][] edges, NetflowDirection[] edgeDirections,
			EdgeWeightValue[] edgeWeights, NodeWeightValue[] nodeWeights)
			throws IOException, ParseException, AggregationException,
			MetricNotApplicableException, LabelerNotApplicableException {
		// vis
		Config.overwrite("GRAPH_VIS_SHOW_NODE_WEIGHTS", "true");
		Config.overwrite("GRAPH_VIS_SHOW_NODE_INDEX", "true");
		if (enableVis) {
			DatasetUtils.setGraphVisSettings();
			GraphVisualization.enable();
		} else
			GraphVisualization.disable();

		// IO settings
		Config.overwrite("GENERATION_WRITE_DISTRIBUTONS",
				String.valueOf(writeDistributions));
		Config.overwrite("GENERATION_WRITE_NVL", "false");
		Config.overwrite("GENERATION_WRITE_NNVL", "false");

		// additional values
		Config.overwrite("GENERATE_VALUES_FROM_DISTRIBUTION", "true");
		Config.overwrite("GENERATE_DISTRIBUTION_PERCENT_VALUES", "true");

		// init reader
		NetflowEventReader reader = new DarpaNetflowReader(srcDir, srcFilename);
		reader.setBatchIntervalSeconds(batchLengthSeconds);
		reader.setEdgeLifeTimeSeconds(edgeLifeTimeSeconds);
		reader.setDataOffset(reader.getDataOffset() + dataOffsetSeconds);

		if (from != null)
			reader.setMinimumTimestamp(from);
		if (to != null)
			reader.setMaximumTimestamp(to);

		// init graph generator
		long timestampMillis = reader.getInitTimestamp().getMillis();
		long timestampSeconds = TimeUnit.MILLISECONDS
				.toSeconds(timestampMillis);

		// new data structures
		// Log.info("using new datastructure gds2");
		// GraphGenerator gg = new EmptyNetwork(GDS2.directedVE(
		// NetworkNodeWeight.class, WeightSelection.None,
		// NetworkEdgeWeight.class, WeightSelection.None),
		// timestampSeconds);

		// normal
		GraphGenerator gg = new EmptyNetwork(GDS.directedVE(
				NetworkNodeWeight.class, WeightSelection.NaN,
				DoubleMultiWeight.class, WeightSelection.NaN), timestampSeconds);

		// GraphGenerator gg = new
		// EmptyNetwork(GDS.directedVE(TypedWeight.class,
		// WeightSelection.None, IntWeight.class, WeightSelection.Zero),
		// timestampSeconds);

		// init batch generator
		BatchGenerator bg = new NetflowBatch(name, reader, edges,
				edgeDirections, edgeWeights, nodeWeights);

		// for graph representation
		NetworkNodeKeyLabel.netflowBatchGenerator = (NetflowBatch) bg;

		// init Labeler
		Labeler[] labeler = new Labeler[0];
		if (attackListPath != null && !attackListPath.equals("null"))
			labeler = new Labeler[] { new DarpaAttackLabeler(attackListPath, "") };

		// init series
		Series s = new Series(gg, bg, metrics, labeler, dstDir, name);

		// generate
		SeriesData sd = s.generate(1, Integer.MAX_VALUE, false, false, true, 0);

		GraphVisualization.setText("Finished");
		Log.infoSep();

		return sd;
	}

	/*
	 * UTLITY & STATICS
	 */
	/**
	 * Instantiates a metric by the given classPath, nodeType and binsize.
	 * nodeType may be null to instantiate without. Binsize should be set to
	 * zero for metrics without binsize.
	 **/
	public static Metric instantiateMetric(String classPath, String nodeType,
			double binsize, int index) {
		Metric m = null;
		//
		// System.out.println("______");
		// System.out.println(classPath);
		// System.out.println(nodeType);
		// System.out.println(binsize);
		// System.out.println(index);

		try {
			Class<?> cl = Class.forName(classPath);
			Constructor<?> cons;
			if (nodeType == null && binsize > 0 && index >= 0) {
				cons = cl.getConstructor(int.class, double.class);
				m = (Metric) cons.newInstance(index, binsize);
			}
			if (nodeType == null && binsize > 0 && index < 0) {
				cons = cl.getConstructor(double.class);
				m = (Metric) cons.newInstance(binsize);
			}
			if (nodeType == null && binsize <= 0 && index >= 0) {
				cons = cl.getConstructor(int.class);
				m = (Metric) cons.newInstance(index);
			}
			if (nodeType == null && binsize <= 0 && index < 0) {
				cons = cl.getConstructor();
				m = (Metric) cons.newInstance();
			}
			if (nodeType != null && binsize > 0 && index >= 0) {
				cons = cl.getConstructor(String[].class, int.class,
						double.class);
				m = (Metric) cons.newInstance(
						(Object) new String[] { nodeType }, index, binsize);
			}
			if (nodeType != null && binsize > 0 && index < 0) {
				cons = cl.getConstructor(String[].class, double.class);
				m = (Metric) cons.newInstance(
						(Object) new String[] { nodeType }, binsize);
			}
			if (nodeType != null && binsize <= 0 && index >= 0) {
				cons = cl.getConstructor(String[].class, int.class);
				m = (Metric) cons.newInstance(
						(Object) new String[] { nodeType }, index);
			}
			if (nodeType != null && binsize <= 0 && index < 0) {
				cons = cl.getConstructor(String[].class);
				m = (Metric) cons
						.newInstance((Object) new String[] { nodeType });
			} else {

			}
		} catch (ClassNotFoundException | NoSuchMethodException
				| SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			Log.error("problem when instantiating metric: " + classPath
					+ " with nodeType: " + nodeType);
			e.printStackTrace();
		}

		return m;
	}

	/** Adds metrics from array to list. Checks and prevents duplicates. **/
	public static ArrayList<Metric> addMetricsToList(
			ArrayList<Metric> metricList, Metric[] metrics) {
		for (Metric m : metrics) {
			boolean contained = false;
			for (Metric m2 : metricList) {
				if (m.getName().equals(m2.getName()))
					contained = true;
			}
			if (!contained)
				metricList.add(m);
		}
		return metricList;
	}

	/*
	 * STATIC METRICS
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
			new OverlapU(), new DegreeDistributionU(),
			new DegreeDistributionR(metricHostFilter),
			new DegreeDistributionR(metricPortFilter), new EdgeWeightsR(1.0),
			new DirectedMotifsU(), new WeightedDegreeDistributionR(),
			new WeightedDegreeDistributionR(metricHostFilter),
			new WeightedDegreeDistributionR(metricPortFilter) };

	public static final Metric[] metricsDefault = new Metric[] {
			new DegreeDistributionU(), new DirectedMotifsU(),
			new EdgeWeightsR(1.0), new WeightedDegreeDistributionR() };

	public static final Metric[] metricsDefaultHostOnly = new Metric[] {
			new DegreeDistributionR(DatasetUtils.metricHostFilter),
			new DirectedMotifsU(), new EdgeWeightsR(1.0),
			new WeightedDegreeDistributionR(DatasetUtils.metricHostFilter) };

	public static final Metric[] metricsDefaultPortOnly = new Metric[] {
			new DegreeDistributionR(DatasetUtils.metricPortFilter),
			new DirectedMotifsU(), new EdgeWeightsR(1.0),
			new WeightedDegreeDistributionR(DatasetUtils.metricPortFilter) };

	public static final Metric[] metricsDefaultAll = new Metric[] {
			new DegreeDistributionR(DatasetUtils.metricHostFilter),
			new DegreeDistributionR(DatasetUtils.metricPortFilter),
			new DegreeDistributionR(), new DirectedMotifsU(),
			new EdgeWeightsR(1.0),
			new WeightedDegreeDistributionR(DatasetUtils.metricHostFilter),
			new WeightedDegreeDistributionR(DatasetUtils.metricPortFilter),
			new WeightedDegreeDistributionR() };

	/*
	 * PARSING
	 */

	/**
	 * Parses a String array where each String has the format:<br>
	 * <br>
	 * 
	 * NetflowEventField-NetflowEventField-...-NetflowEventField
	 */
	protected NetflowEventField[][] parseNetflowEventFields(String[] input) {
		NetflowEventField[][] edges = new NetflowEventField[input.length][];
		for (int i = 0; i < input.length; i++) {
			String[] splits = input[i].split("-");
			NetflowEventField[] fields = new NetflowEventField[splits.length];

			for (int j = 0; j < splits.length; j++) {
				if (splits[j] == null || splits[j].equals("null"))
					fields[j] = NetflowEventField.None;
				else
					fields[j] = NetflowEventField.valueOf(splits[j]);
			}

			edges[i] = fields;
		}

		return edges;
	}

	/** Parses edge weight values. **/
	protected EdgeWeightValue[] parseEdgeWeightValues(String[] input) {
		EdgeWeightValue[] edgeWeights = new EdgeWeightValue[input.length];
		for (int i = 0; i < input.length; i++) {
			edgeWeights[i] = EdgeWeightValue.valueOf(input[i]);
		}
		return edgeWeights;
	}

	/** Parses node weight values. **/
	protected NodeWeightValue[] parseNodeWeightValues(String[] input) {
		NodeWeightValue[] nodeWeights = new NodeWeightValue[input.length];
		for (int i = 0; i < input.length; i++) {
			nodeWeights[i] = NodeWeightValue.valueOf(input[i]);
		}
		return nodeWeights;
	}

	/** Parses edge directions. **/
	protected NetflowDirection[] parseEdgeDirections(String[] inputDirections) {
		NetflowDirection[] directions = new NetflowDirection[inputDirections.length];
		for (int i = 0; i < inputDirections.length; i++) {
			if (inputDirections[i].toLowerCase().equals("fw"))
				directions[i] = NetflowDirection.forward;
			if (inputDirections[i].toLowerCase().equals("bw"))
				directions[i] = NetflowDirection.backward;
		}
		return directions;
	}

	/** Used for automated name generation. **/
	protected String map(NodeWeightValue key) {
		switch (key) {
		case BytesIn:
			return "bi";
		case BytesOut:
			return "bo";
		case numberOfNetflowsIn:
			return "fi";
		case numberOfNetflowsOut:
			return "fo";
		case PacketsIn:
			return "pi";
		case PacketsOut:
			return "po";
		default:
			return "unknown";
		}
	}

	/** Used for automated name generation. **/
	protected String map(NetflowEventField key) {
		switch (key) {
		case Bytes:
			return "b";
		case BytesToDst:
			return "bd";
		case BytesToSrc:
			return "bs";
		case ConnectionState:
			return "cs";
		case Date:
			return "date";
		case Direction:
			return "dir";
		case DstAddress:
			return "d";
		case DstPort:
			return "dp";
		case Duration:
			return "dur";
		case Flags:
			return "f";
		case Label:
			return "l";
		case None:
			return "";
		case numberOfNetflows:
			return "n";
		case Packets:
			return "p";
		case PacketsToDst:
			return "pd";
		case PacketsToSrc:
			return "ps";
		case Protocol:
			return "pr";
		case SrcAddress:
			return "s";
		case SrcPort:
			return "sp";
		case Time:
			return "time";
		default:
			return "unknown";
		}
	}

	/**
	 * Automated name-generation based on the given model. Will only be used
	 * when no specific name is being passed.
	 **/
	protected String generateName(String[] edges, String[] edgeDirections,
			String[] edgeWeights, String[] nodeWeights,
			Integer batchLengthSeconds, Integer edgeLifeTimeSeconds) {
		String name = "directed.";

		for (int i = 0; i < edges.length; i++) {
			String temp = edgeDirections[i].toUpperCase() + "";

			String[] splits = edges[i].split("-");

			for (int j = 0; j < splits.length; j++) {
				NetflowEventField f = NetflowEventField.valueOf(splits[j]);
				temp += "" + map(f);
			}

			if (i > 0)
				name += ".";
			name += temp;
		}
		name += "/" + batchLengthSeconds + "_" + edgeLifeTimeSeconds + "/";

		if (edgeWeights.length == 0
				|| (edgeWeights.length == 1 && (edgeWeights[0] == null || edgeWeights[0]
						.equals("null")))) {
			name += "none";
		} else {
			for (int i = 0; i < edgeWeights.length; i++) {
				if (edgeWeights[i] != null && !edgeWeights[i].equals("null")) {
					if (i > 0)
						name += ".";
					name += map(NetflowEventField.valueOf(edgeWeights[i]));
				}
			}
		}

		name += "_";

		if (nodeWeights.length == 0
				|| (nodeWeights.length == 1 && (nodeWeights[0] == null || nodeWeights[0]
						.equals("null")))) {
			name += "none";
		} else {
			for (int i = 0; i < nodeWeights.length; i++) {
				if (nodeWeights[i] != null && !nodeWeights[i].equals("null")) {
					if (i > 0)
						name += ".";
					name += map(NodeWeightValue.valueOf(nodeWeights[i]));
				}
			}
		}

		return name;
	}
}