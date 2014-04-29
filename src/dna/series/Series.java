package dna.series;

import java.io.File;
import java.io.IOException;

import dna.graph.Graph;
import dna.graph.generators.GraphGenerator;
import dna.io.filesystem.Dir;
import dna.io.filter.PrefixFilenameFilter;
import dna.metrics.Metric;
import dna.metrics.MetricNotApplicableException;
import dna.series.aggdata.AggregatedSeries;
import dna.series.data.RunData;
import dna.series.data.SeriesData;
import dna.updates.generators.BatchGenerator;
import dna.util.Config;
import dna.util.Log;
import dna.util.Rand;

public class Series {

	// TODO add separate rand for Series and pass it down to metrics, bg, gg

	public static enum RandomSeedType {
		timestamp, fixed
	};

	public static enum RandomSeedReset {
		eachBatch, eachRun, eachSeries, never
	};

	public Series(GraphGenerator gg, BatchGenerator bg, Metric[] metrics,
			String dir, String name) {
		this.graphGenerator = gg;
		this.batchGenerator = bg;
		this.metrics = metrics;
		this.dir = dir;
		this.name = name;
	}

	public static SeriesData get(String dir, String name)
			throws NumberFormatException, IOException {
		SeriesData seriesData = new SeriesData(dir, name);
		int runs = (new File(dir)).listFiles(new PrefixFilenameFilter(Config
				.get("PREFIX_RUNDATA_DIR"))).length;
		for (int run = 0; run < runs; run++) {
			String runDir = Dir.getRunDataDir(dir, run);
			System.out.println(run + ": " + runDir);
			RunData runData = RunData.read(dir, run, false);
			seriesData.addRun(runData);
		}
		AggregatedSeries aggregation = AggregatedSeries.read(dir, name, true);

		seriesData.setAggregation(aggregation);
		return seriesData;
	}

	public SeriesData generate(int runs, int batches)
			throws AggregationException, IOException,
			MetricNotApplicableException {
		return this.generate(runs, batches, true, true, true, 0);
	}

	public SeriesData generate(int runs, int batches, boolean aggregate)
			throws AggregationException, IOException,
			MetricNotApplicableException {
		return this.generate(runs, batches, true, aggregate, true, 0);
	}

	public SeriesData generate(int runs, int batches, long batchGenerationTime)
			throws AggregationException, IOException,
			MetricNotApplicableException {
		return this.generate(runs, batches, true, true, true,
				batchGenerationTime);
	}

	public SeriesData generate(int runs, int batches, boolean compare,
			boolean aggregate, boolean write, long batchGenerationTime)
			throws AggregationException, IOException,
			MetricNotApplicableException {
		return SeriesGeneration.generate(this, runs, batches, compare,
				aggregate, write, batchGenerationTime);
	}

	private GraphGenerator graphGenerator;

	private BatchGenerator batchGenerator;

	private Metric[] metrics;

	private String dir;

	private Graph graph;

	private String name;

	public GraphGenerator getGraphGenerator() {
		return this.graphGenerator;
	}

	public BatchGenerator getBatchGenerator() {
		return this.batchGenerator;
	}

	public Metric[] getMetrics() {
		return this.metrics;
	}

	public String getDir() {
		return this.dir;
	}

	public Graph getGraph() {
		return this.graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public String getName() {
		return this.name;
	}

	public static RandomSeedType defaultRandomSeedType = RandomSeedType.timestamp;

	private RandomSeedType randomSeedType = Series.defaultRandomSeedType;

	public RandomSeedType getRandomSeedType() {
		return randomSeedType;
	}

	public void setRandomSeedType(RandomSeedType randomSeedType) {
		this.randomSeedType = randomSeedType;
	}

	public static RandomSeedReset defaultRandomSeedReset = RandomSeedReset.eachRun;

	private RandomSeedReset randomSeedReset = Series.defaultRandomSeedReset;

	public RandomSeedReset getRandomSeedReset() {
		return randomSeedReset;
	}

	public void setRandomSeedReset(RandomSeedReset randomSeedReset) {
		this.randomSeedReset = randomSeedReset;
	}

	public static long defaultSeed = 0;

	private long seed = Series.defaultSeed;

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public void resetRand() {
		if (this.randomSeedType == RandomSeedType.timestamp) {
			this.seed = System.currentTimeMillis();
		}
		Rand.init(this.seed);
		Log.debug("resetting random seed to " + this.seed + " ("
				+ this.randomSeedReset + "/" + this.randomSeedType + ")");
	}

	private boolean callGC = Config.getBoolean("GENERATION_CALL_GC");
	private int gcOccurence = Config.getInt("GENERATION_GC_OCCURENCE");

	public boolean isCallGC() {
		return callGC;
	}

	public void setCallGC(boolean callGC) {
		this.callGC = callGC;
	}

	public int getGcOccurence() {
		return this.gcOccurence;
	}
}
