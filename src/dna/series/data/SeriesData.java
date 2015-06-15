package dna.series.data;

import java.io.IOException;
import java.util.ArrayList;

import dna.io.filesystem.Dir;
import dna.metrics.IMetric;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.series.aggdata.AggregatedSeries;
import dna.series.lists.MetricDataList;
import dna.util.Config;
import dna.util.Log;

public class SeriesData {

	public SeriesData(String dir, String name) {
		this.dir = dir;
		this.name = name;
		this.runs = new ArrayList<RunData>();
		this.aggregation = null;
	}

	public SeriesData(String dir, String name, int size) {
		this.dir = dir;
		this.name = name;
		this.runs = new ArrayList<RunData>(size);
		this.aggregation = null;
	}

	public SeriesData(String dir, String name, RunData[] runs) {
		this(dir, name, runs.length);
		for (RunData run : runs) {
			this.runs.add(run);
		}
		this.aggregation = null;
	}

	public SeriesData(String dir, String name, RunData[] runs,
			AggregatedSeries aggregation) {
		this(dir, name, runs);
		this.aggregation = aggregation;
	}

	private String dir;

	public String getDir() {
		return this.dir;
	}

	private String name;

	public String getName() {
		return this.name;
	}

	private ArrayList<RunData> runs;

	public ArrayList<RunData> getRuns() {
		return this.runs;
	}

	public RunData getRun(int index) {
		return this.runs.get(index);
	}

	public void addRun(RunData run) {
		this.runs.add(run);
	}

	private AggregatedSeries aggregation;

	public AggregatedSeries getAggregation() {
		return this.aggregation;
	}

	public void setAggregation(AggregatedSeries aggregation) {
		this.aggregation = aggregation;
	}

	/**
	 * This method tests if two different SeriesData objects are from the same
	 * type and can be compared. Checks: - same amount of runs - same runs (uses
	 * RunData.sameType())
	 * 
	 * @author Rwilmes
	 * @date 15.07.2013
	 */
	public static boolean isSameType(SeriesData s1, SeriesData s2) {
		Log.debug("Comparing SeriesData " + s1.getName() + " and SeriesData "
				+ s2.getName());

		if (s1.getRuns().size() != s2.getRuns().size()) {
			Log.warn("different amount of runs on series " + s1.getName()
					+ " and series " + s2.getName());
			return false;
		}

		for (int i = 0; i < s1.getRuns().size(); i++) {
			RunData.isSameType(s1.getRun(i), s2.getRun(i));
		}

		return true;
	}

	public static SeriesData read(String dir, String name,
			boolean readAggregation, boolean readValues) throws IOException {
		String[] runs = Dir.getRuns(dir);
		RunData[] runList = new RunData[runs.length];

		// batch read mode
		BatchReadMode batchReadMode;
		if (readValues)
			batchReadMode = BatchReadMode.readAllValues;
		else
			batchReadMode = BatchReadMode.readNoValues;

		for (String run : runs) {
			int runId = Dir.getRun(run.replace(Config.get("SUFFIX_ZIP_FILE"),
					""));

			if (Config.get("GENERATION_AS_ZIP").equals("runs")) {
				runList[runId] = RunData.readFromSingleFile(dir, Dir.delimiter,
						runId, batchReadMode);
			} else {
				runList[runId] = RunData.read(Dir.getRunDataDir(dir, runId),
						runId, batchReadMode);
			}
		}
		if (readAggregation) {
			AggregatedSeries aggr = AggregatedSeries.read(dir, name,
					batchReadMode);
			return new SeriesData(dir, name, runList, aggr);
		} else {
			return new SeriesData(dir, name, runList);
		}

	}

	/**
	 * This method compares every metric that is flagged with 'heuristic' with
	 * an 'exact' metric that is comparable, thus calculating a 'quality'
	 * MetricData object.
	 * 
	 * @param writeValues
	 *            Flag that indicates if the resulting 'quality' MetricData
	 *            object will be written on the filesystem.
	 * 
	 * @author Rwilmes
	 * @date 15.07.2013
	 */
	public void compareMetrics(boolean writeValues) throws IOException,
			InterruptedException {
		boolean printed = false;
		MetricDataList exacts = new MetricDataList();
		MetricDataList heuristics = new MetricDataList();

		// get exacts and heuristics
		for (MetricData metric : this.getRuns().get(0).getBatches().get(0)
				.getMetrics().getList()) {
			if (metric.getType() != null) {
				if (metric.getType().equals(IMetric.MetricType.exact))
					exacts.add(metric);
				if (metric.getType().equals(IMetric.MetricType.heuristic))
					heuristics.add(metric);
			}
		}

		// iterate over heuristics, compare each once
		for (MetricData heuristic : heuristics.getList()) {
			// log out
			if (!printed) {
				Log.info("comparing metrics");
				printed = true;
			}

			// get best matching comparison metric
			MetricData exact = exacts
					.getBestMatchingComparisonMetric(heuristic);

			// compare
			if (MetricData.isComparable(heuristic, exact)) {
				Log.info("  => heuristic \"" + heuristic.getName()
						+ "\" with exact \"" + exact.getName() + "\"");

				// iterate over runs
				for (RunData run : this.getRuns()) {
					// iterate over batches
					for (BatchData batch : run.getBatches().getList()) {
						// read batch
						BatchData tempBatch = BatchData.readIntelligent(Dir
								.getBatchDataDir(this.dir, run.getRun(),
										batch.getTimestamp()), batch
								.getTimestamp(), BatchReadMode.readAllValues);

						// compare metrics
						MetricData quality = MetricData.compare(tempBatch
								.getMetrics().get(exact.getName()), tempBatch
								.getMetrics().get(heuristic.getName()));

						// add quality metric to current structure
						batch.getMetrics().add(quality);
						tempBatch.getMetrics().add(quality);

						// write
						if (writeValues)
							tempBatch.writeIntelligent(Dir.getBatchDataDir(
									this.dir, run.getRun(),
									tempBatch.getTimestamp()));

					}
				}
			}
		}
	}

	/**
	 * Reads the aggregation of the series data object.
	 * 
	 * @param index
	 *            The index will be used in the aggregations name. Used for
	 *            plotting with multiple series.
	 * @throws IOException
	 *             Can be thrown when reading aggregation from filesystem.
	 */
	public void readAggregation(int index, long timestampFrom,
			long timestampTo, long stepSize) throws IOException {
		this.setAggregation(AggregatedSeries.readFromTo(this.getDir(),
				this.getName() + index + "_" + Config.get("RUN_AGGREGATION"),
				timestampFrom, timestampTo, stepSize,
				BatchReadMode.readAllValues));
	}

	/**
	 * Reads the aggregation of the series data objects.
	 * 
	 * @param seriesData
	 *            SeriesData objects whose aggregations will be read.
	 * @return Same seriesData objects that are handed over.
	 * @throws IOException
	 *             Can be thrown when reading aggregations from filesystem.
	 */
	public static SeriesData[] getAggregations(SeriesData[] seriesData,
			long timestampFrom, long timestampTo, long stepSize)
			throws IOException {
		for (int i = 0; i < seriesData.length; i++) {
			seriesData[i].readAggregation(i, timestampFrom, timestampTo,
					stepSize);
		}
		return seriesData;
	}

	/**
	 * Returns if an array of series data objects is plottable, which means no
	 * series has a null aggregation.
	 * 
	 * @param seriesData
	 *            SeriesData objects which will be checked.
	 * @return If the objects are ready to be plotted or not.
	 */
	public static boolean isPlottable(SeriesData[] seriesData) {
		for (int i = 0; i < seriesData.length; i++) {
			if (seriesData[i].getAggregation() == null)
				return false;
		}
		return true;
	}
}
