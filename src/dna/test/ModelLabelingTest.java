package dna.test;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import dna.graph.datastructures.GDS;
import dna.graph.generators.GraphGenerator;
import dna.graph.generators.network.NetflowBatch;
import dna.graph.generators.network.NetflowGraph;
import dna.graph.weights.Weight.WeightSelection;
import dna.graph.weights.multi.DoubleMultiWeight;
import dna.graph.weights.network.NetworkNodeWeight;
import dna.io.network.netflow.NetflowEvent.NetflowDirection;
import dna.io.network.netflow.NetflowEvent.NetflowEventField;
import dna.io.network.netflow.NetflowEventReader;
import dna.labels.labeler.Labeler;
import dna.labels.labeler.LabelerNotApplicableException;
import dna.labels.labeler.attacks.GroundTruthLabelerAttacks;
import dna.labels.labeler.models.ModelLabeler;
import dna.metrics.Metric;
import dna.metrics.MetricNotApplicableException;
import dna.series.AggregationException;
import dna.series.Series;
import dna.series.data.SeriesData;
import dna.test.graphModel.GraphModel;
import dna.test.graphModel.GraphModelUtils;
import dna.updates.generators.BatchGenerator;
import dna.util.Config;
import dna.util.Log;
import dna.util.network.DatasetUtils;
import dna.util.network.NetflowAnalysis.EdgeWeightValue;
import dna.util.network.NetflowAnalysis.NodeWeightValue;
import dna.visualization.BatchHandler.ZipMode;
import dna.visualization.graph.GraphVisualization;
import dna.visualization.graph.rules.nodes.NetworkNodeStyles;

public class ModelLabelingTest {

	// settings
	public static final boolean writeDistributions = false;
	public static final boolean writeNodeValueLists = false;
	public static final boolean writeNodeNodeValueLists = false;
	public static final boolean enableVis = true;
	public static final int dataOffsetSeconds = 0;
	public static final ZipMode zipMode = ZipMode.none;

	public static void main(String[] args) throws IOException, ParseException, AggregationException,
			MetricNotApplicableException, LabelerNotApplicableException {
		GraphVisualization.setConfigPath("config/gvis/gids.cfg");

		int week = 2;
		int day = 1;

		String homeDir = "/home/chibo/";

		String srcDir = homeDir + "data/datasets/darpa1998/";
		String srcFilename = week + "_" + day + ".netflow";

		String dstDir = homeDir + "tests/data/blub/";
		String name = "blub";

		String attackListDir = homeDir + "data/lists/darpa1998/";
		String attackListFile = week + "_" + day + ".labels";

		// model settings
		int batchLengthSeconds = 1;
		int edgeLifeTimeSeconds = 300;
		GraphModel model = GraphModelUtils.model1; // select model

		DateTime from = getDateTime(week, day, "17:25:00");
		DateTime to = getDateTime(week, day, "17:30:00");

		String labelMode = "2";
		int numberOfFeatures = 200;

		String scriptPath = homeDir + "customscripts/wrapper/blackbox_java_wrapper.py";
		String featureListPath = homeDir + "data/features/darpa1998/train/ranking/" + week + "_" + day + "/"
				+ batchLengthSeconds + "_" + edgeLifeTimeSeconds + "_" + model.getName() + "_l" + labelMode + ".lasso";
		String modelPath = homeDir + "q-data/models/darpa1998/train/" + week + "_" + day + "/" + batchLengthSeconds
				+ "_" + edgeLifeTimeSeconds + "_" + model.getName() + "_f" + numberOfFeatures + "_l" + labelMode
				+ ".svm";

		Labeler[] labeler = new Labeler[] {
				new ModelLabeler("ModelLabeler", scriptPath, featureListPath, numberOfFeatures, modelPath),
				new GroundTruthLabelerAttacks("DarpaGroundTruth", attackListDir, attackListFile, edgeLifeTimeSeconds) };

		// MainDisplayConfig visConfig =
		// MainDisplayConfig.readConfig("config/gui_min_lab.cfg");
		// visConfig.setDefaultDir(dstDir + "run.0/");
		// visConfig.setLiveDisplayMode(true);
		// MainDisplay display = new MainDisplay(visConfig);
		// display.startLiveMonitoring();

		SeriesData sd = generate(srcDir, srcFilename, dstDir, name, batchLengthSeconds, edgeLifeTimeSeconds, from, to,
				model, enableVis, labeler);
	}

	public static SeriesData generate(String srcDir, String srcFilename, String dstDir, String name,
			int batchLengthSeconds, int edgeLifeTimeSeconds, DateTime from, DateTime to, GraphModel model,
			boolean enableVis, Labeler[] labeler) throws IOException, ParseException, AggregationException,
			MetricNotApplicableException, LabelerNotApplicableException {
		return generate(srcDir, srcFilename, dstDir, srcFilename, batchLengthSeconds, dataOffsetSeconds,
				edgeLifeTimeSeconds, from, to, enableVis, model.getMetrics(), model.getEdges(),
				model.getEdgeDirections(), model.getEdgeWeights(), model.getNodeWeights(), labeler);

	}

	public static SeriesData generate(String srcDir, String srcFilename, String dstDir, String name,
			int batchLengthSeconds, int dataOffsetSeconds, int edgeLifeTimeSeconds, DateTime from, DateTime to,
			boolean enableVis, Metric[] metrics, NetflowEventField[][] edges, NetflowDirection[] edgeDirections,
			EdgeWeightValue[] edgeWeights, NodeWeightValue[] nodeWeights, Labeler[] labeler) throws IOException,
			ParseException, AggregationException, MetricNotApplicableException, LabelerNotApplicableException {
		// vis
		Config.overwrite("GRAPH_VIS_SHOW_NODE_WEIGHTS", "true");
		Config.overwrite("GRAPH_VIS_SHOW_NODE_INDEX", "true");
		if (enableVis) {
			DatasetUtils.setGraphVisSettings();
			GraphVisualization.enable();
		} else
			GraphVisualization.disable();

		// IO settings
		Config.overwrite("GENERATION_WRITE_DISTRIBUTONS", String.valueOf(writeDistributions));
		Config.overwrite("GENERATION_WRITE_NVL", String.valueOf(writeNodeValueLists));
		Config.overwrite("GENERATION_WRITE_NNVL", String.valueOf(writeNodeNodeValueLists));
		setZipMode();

		// additional values
		Config.overwrite("GENERATE_VALUES_FROM_DISTRIBUTION", "true");
		Config.overwrite("GENERATE_DISTRIBUTION_PERCENT_VALUES", "true");

		// configure extra value generation
		Config.overwrite("EXTRA_VALUE_DISTRIBUTION_PERCENT", "99, 98, 97, 96, 95, 90, 85, 80, 70, 50");

		// init graph generator
		GraphGenerator gg = new NetflowGraph(
				GDS.directedVE(NetworkNodeWeight.class, WeightSelection.None, DoubleMultiWeight.class,
						WeightSelection.None),
				TimeUnit.MILLISECONDS.toSeconds(from.getMillis()), TimeUnit.MILLISECONDS.toSeconds(to.getMillis()),
				srcDir, srcFilename, batchLengthSeconds, edgeLifeTimeSeconds, edges, edgeDirections, edgeWeights,
				nodeWeights);

		// init batch generator with gg reader
		NetflowEventReader reader = ((NetflowGraph) gg).getReader();
		BatchGenerator bg = new NetflowBatch(name, reader, edges, edgeDirections, edgeWeights, nodeWeights);

		// transfer mappings
		((NetflowBatch) bg).setMap(((NetflowGraph) gg).getBatchGenerator().getMap());

		// set new batch-generator for presentation
		NetworkNodeStyles.netflowBatchGenerator = (NetflowBatch) bg;

		// init series
		Series s = new Series(gg, bg, metrics, labeler, dstDir, name);

		// generate
		SeriesData sd = s.generate(1, 500, false, false, true, 1);

		GraphVisualization.setText("Finished");
		Log.infoSep();

		return sd;
	}

	/** Date time formatting **/
	public static final DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-MM-yyyy-HH:mm:ss");

	public static DateTime getDateTime(int week, int day, String hhmmss) {
		String dayString = DatasetUtils.getDarpaDate(week, day);
		return fmt.parseDateTime(dayString + "-" + hhmmss).plusSeconds(7200);
	}

	/** Zip Mode **/
	public static void setZipMode() {
		switch (zipMode) {
		case batches:
			Config.zipBatches();
			break;
		case none:
			Config.zipNone();
			break;
		case runs:
			Config.zipRuns();
			break;
		}
	}

}
