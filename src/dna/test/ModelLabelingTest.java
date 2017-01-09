package dna.test;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
import dna.util.network.DatasetUtils.ZipMode;
import dna.util.network.NetflowAnalysis.EdgeWeightValue;
import dna.util.network.NetflowAnalysis.NodeWeightValue;
import dna.visualization.graph.GraphVisualization;
import dna.visualization.graph.toolTips.infoLabel.NetworkNodeKeyLabel;

public class ModelLabelingTest {

	// settings
	public static final boolean writeDistributions = false;
	public static final boolean writeNodeValueLists = false;
	public static final boolean writeNodeNodeValueLists = false;
	public static final boolean enableVis = false;
	public static final int dataOffsetSeconds = 0;
	public static final ZipMode zipMode = ZipMode.batches;

	public static void main(String[] args) throws IOException, ParseException, AggregationException,
			MetricNotApplicableException, LabelerNotApplicableException {
		int week = 2;
		int day = 1;

		String homeDir = "/home/chibo/";

		String srcDir = homeDir + "data/datasets/darpa1998/";
		String srcFilename = week + "_" + day + ".netflow";

		String dstDir = homeDir + "tests/data/";
		String name = "blub";

		String attackListPath = homeDir + "data/lists/darpa1998/" + week + "_" + day + ".labels";

		// model settings
		int batchLengthSeconds = 1;
		int edgeLifeTimeSeconds = 300;
		GraphModel model = GraphModelUtils.model0; // select model

		DateTime from = getDateTime(week, day, "07:55:00");
		DateTime to = getDateTime(week, day, "08:10:00");

		String labelMode = "2";
		int numberOfFeatures = 200;

		String modelPath = homeDir + "q-data/models/darpa1998/train/" + week + "_" + day + "_" + model.getName() + "_f"
				+ numberOfFeatures + "_l" + labelMode + ".svm";

		Labeler[] labeler = new Labeler[] {};

		SeriesData sd = generate(srcDir, srcFilename, dstDir, name, batchLengthSeconds, edgeLifeTimeSeconds, from, to,
				model, attackListPath, enableVis, labeler);
	}

	public static SeriesData generate(String srcDir, String srcFilename, String dstDir, String name,
			int batchLengthSeconds, int edgeLifeTimeSeconds, DateTime from, DateTime to, GraphModel model,
			String attackListPath, boolean enableVis, Labeler[] labeler) throws IOException, ParseException,
			AggregationException, MetricNotApplicableException, LabelerNotApplicableException {
		return generate(srcDir, srcFilename, dstDir, srcFilename, batchLengthSeconds, dataOffsetSeconds,
				edgeLifeTimeSeconds, from, to, attackListPath, enableVis, model.getMetrics(), model.getEdges(),
				model.getEdgeDirections(), model.getEdgeWeights(), model.getNodeWeights(), labeler);

	}

	public static SeriesData generate(String srcDir, String srcFilename, String dstDir, String name,
			int batchLengthSeconds, int dataOffsetSeconds, int edgeLifeTimeSeconds, DateTime from, DateTime to,
			String attackListPath, boolean enableVis, Metric[] metrics, NetflowEventField[][] edges,
			NetflowDirection[] edgeDirections, EdgeWeightValue[] edgeWeights, NodeWeightValue[] nodeWeights,
			Labeler[] labeler) throws IOException, ParseException, AggregationException, MetricNotApplicableException,
			LabelerNotApplicableException {
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
		long timestampSeconds = TimeUnit.MILLISECONDS.toSeconds(timestampMillis);

		// new data structures
		// Log.info("using new datastructure gds2");
		// GraphGenerator gg = new EmptyNetwork(GDS2.directedVE(
		// NetworkNodeWeight.class, WeightSelection.None,
		// NetworkEdgeWeight.class, WeightSelection.None),
		// timestampSeconds);

		// normal
		GraphGenerator gg = new EmptyNetwork(GDS.directedVE(NetworkNodeWeight.class, WeightSelection.None,
				DoubleMultiWeight.class, WeightSelection.None), timestampSeconds);

		// GraphGenerator gg = new
		// EmptyNetwork(GDS.directedVE(TypedWeight.class,
		// WeightSelection.None, IntWeight.class, WeightSelection.Zero),
		// timestampSeconds);

		// init batch generator
		BatchGenerator bg = new NetflowBatch(name, reader, edges, edgeDirections, edgeWeights, nodeWeights);

		// for graph representation
		NetworkNodeKeyLabel.netflowBatchGenerator = (NetflowBatch) bg;

		// init Labeler
		// Labeler[] labeler = new Labeler[0];
		// if (attackListPath != null && !attackListPath.equals("null"))
		// labeler = new Labeler[] { new DarpaAttackLabeler(attackListPath, "")
		// };

		// init series
		Series s = new Series(gg, bg, metrics, labeler, dstDir, name);

		// generate
		SeriesData sd = s.generate(1, Integer.MAX_VALUE, false, false, true, 0);

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
