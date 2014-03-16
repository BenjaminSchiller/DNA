package dna.series.data;

import java.io.IOException;
import java.nio.file.FileSystem;

import dna.io.ZipWriter;
import dna.io.filesystem.Dir;
import dna.io.filesystem.Files;
import dna.series.SeriesGeneration;
import dna.series.lists.DistributionList;
import dna.series.lists.MetricDataList;
import dna.series.lists.NodeNodeValueListList;
import dna.series.lists.NodeValueListList;
import dna.series.lists.RunTimeList;
import dna.series.lists.ValueList;
import dna.util.Config;
import dna.util.Log;

public class BatchData {

	public static final boolean singleFile = true;
	public static FileSystem fs;

	public BatchData(long timestamp) {
		this.timestamp = timestamp;
		this.stats = new ValueList();
		this.generalRuntimes = new RunTimeList();
		this.metricRuntimes = new RunTimeList();
		this.metrics = new MetricDataList();
	}

	public BatchData(long timestamp, int sizeValues, int sizeGeneralRuntimes,
			int sizeMetricRuntimes, int sizeMetrics) {
		this.timestamp = timestamp;
		this.stats = new ValueList(sizeValues);
		this.generalRuntimes = new RunTimeList(sizeGeneralRuntimes);
		this.metricRuntimes = new RunTimeList(sizeMetricRuntimes);
		this.metrics = new MetricDataList(sizeMetrics);
	}

	public BatchData(long timestamp, ValueList values,
			RunTimeList generalRuntimes, RunTimeList metricRuntimes,
			MetricDataList metrics) {
		this.timestamp = timestamp;
		this.stats = values;
		this.generalRuntimes = generalRuntimes;
		this.metricRuntimes = metricRuntimes;
		this.metrics = metrics;
	}

	private long timestamp;

	public long getTimestamp() {
		return this.timestamp;
	}

	private ValueList stats;

	public ValueList getValues() {
		return this.stats;
	}

	private RunTimeList generalRuntimes;

	public RunTimeList getGeneralRuntimes() {
		return this.generalRuntimes;
	}

	private RunTimeList metricRuntimes;

	public RunTimeList getMetricRuntimes() {
		return this.metricRuntimes;
	}

	private MetricDataList metrics;

	public MetricDataList getMetrics() {
		return this.metrics;
	}

	public void write(String dir) throws IOException {
		Log.info("writing BatchData for " + this.timestamp + " to " + dir);
		this.stats.write(dir,
				Files.getValuesFilename(Config.get("BATCH_STATS")));
		this.generalRuntimes
				.write(dir, Files.getRuntimesFilename(Config
						.get("BATCH_GENERAL_RUNTIMES")));
		this.metricRuntimes.write(dir,
				Files.getRuntimesFilename(Config.get("BATCH_METRIC_RUNTIMES")));
		this.metrics.write(dir);
	}

	public static BatchData read(String dir, long timestamp, boolean readValues)
			throws IOException {
		ValueList values = ValueList.read(dir,
				Files.getValuesFilename(Config.get("BATCH_STATS")));
		RunTimeList generalRuntimes = RunTimeList
				.read(dir, Files.getRuntimesFilename(Config
						.get("BATCH_GENERAL_RUNTIMES")));
		RunTimeList metricRuntimes = RunTimeList.read(dir,
				Files.getRuntimesFilename(Config.get("BATCH_METRIC_RUNTIMES")));
		MetricDataList metrics = MetricDataList.read(dir, readValues);
		return new BatchData(timestamp, values, generalRuntimes,
				metricRuntimes, metrics);
	}

	/**
	 * Reads the values of a whole batch using its structure
	 * 
	 * @param dir
	 *            Directory where the batch will be read from
	 * @param structure
	 *            BatchData object containing a structure which will be used to
	 *            read the corresponding values
	 * @return
	 */
	public static BatchData readBatchValues(String dir, long timestamp,
			BatchData b) throws IOException {
		// read values
		ValueList values = ValueList.read(dir,
				Files.getValuesFilename(Config.get("BATCH_STATS")));

		// read runtimes
		RunTimeList generalRuntimes = RunTimeList
				.read(dir, Files.getRuntimesFilename(Config
						.get("BATCH_GENERAL_RUNTIMES")));
		RunTimeList metricRuntimes = RunTimeList.read(dir,
				Files.getRuntimesFilename(Config.get("BATCH_METRIC_RUNTIMES")));

		// read metrics
		MetricDataList metrics = new MetricDataList(b.getMetrics().size());
		for (MetricData m : b.getMetrics().getList()) {
			String mDir = Dir.getMetricDataDir(dir, m.getName(), m.getType());
			// init metric values
			ValueList mValues = ValueList.read(mDir,
					Files.getValuesFilename(Config.get("METRIC_DATA_VALUES")));
			DistributionList mDistributions = new DistributionList(m
					.getDistributions().size());
			NodeValueListList mNodevalues = new NodeValueListList(m
					.getNodeValues().size());
			NodeNodeValueListList mNodenodevalues = new NodeNodeValueListList(m
					.getNodeNodeValues().size());

			// read distributions
			for (Distribution d : m.getDistributions().getList()) {
				if (d instanceof DistributionInt) {
					if (d instanceof BinnedDistributionInt) {
						mDistributions.add(BinnedDistributionInt.read(mDir,
								Files.getDistributionBinnedIntFilename(d
										.getName()), d.getName(), true));
					} else {
						mDistributions.add(DistributionInt.read(mDir,
								Files.getDistributionIntFilename(d.getName()),
								d.getName(), true));
					}
				} else if (d instanceof DistributionDouble) {
					if (d instanceof BinnedDistributionDouble) {
						mDistributions.add(BinnedDistributionDouble.read(mDir,
								Files.getDistributionBinnedDoubleFilename(d
										.getName()), d.getName(), true));
					} else {
						mDistributions.add(DistributionDouble.read(mDir, Files
								.getDistributionDoubleFilename(d.getName()), d
								.getName(), true));
					}
				} else if (d instanceof DistributionLong) {
					if (d instanceof BinnedDistributionLong) {
						mDistributions.add(BinnedDistributionLong.read(mDir,
								Files.getDistributionBinnedLongFilename(d
										.getName()), d.getName(), true));
					} else {
						mDistributions.add(DistributionLong.read(mDir,
								Files.getDistributionLongFilename(d.getName()),
								d.getName(), true));
					}
				} else if (d instanceof Distribution) {
					mDistributions.add(Distribution.read(mDir,
							Files.getDistributionFilename(d.getName()),
							d.getName(), true));
				} else {
					Log.error("Failed to read distribution " + d.getName()
							+ " for metric " + m.getName() + " on dir " + dir);
				}
			}
			// read nodevaluelists
			for (NodeValueList nvl : m.getNodeValues().getList()) {
				mNodevalues.add(NodeValueList.read(mDir,
						Files.getNodeValueListFilename(nvl.getName()),
						nvl.getName(), true));
			}
			// read nodenodevaluelists
			for (NodeNodeValueList nnvl : m.getNodeNodeValues().getList()) {
				mNodenodevalues.add(NodeNodeValueList.read(mDir,
						Files.getNodeNodeValueListFilename(nnvl.getName()),
						nnvl.getName(), true));
			}
			metrics.add(new MetricData(m.getName(), m.getType(), mValues,
					mDistributions, mNodevalues, mNodenodevalues));
		}

		return new BatchData(b.getTimestamp(), values, generalRuntimes,
				metricRuntimes, metrics);
	}

	/**
	 * Reads the whole batch from a single zip file, using the structure of a
	 * given batch for a faster read without "list"-calls inside the zip-file.
	 * 
	 * @param fsDir
	 *            Directory where the batch zip is located.
	 * @param timestamp
	 *            Timestamp of the batch that will be read.
	 * @param dir
	 *            Relative directoy inside the zip-file.
	 * @param readValues
	 *            If false, no actual data will be read and stored.
	 * @return BatchData object read from the zip-file.
	 * @throws IOException
	 */
	public static BatchData readBatchValuesFromSingleFile(String fsDir,
			long timestamp, String dir, BatchData structure) throws IOException {
		SeriesGeneration.readFileSystem = ZipWriter.createBatchFileSystem(
				fsDir, timestamp);
		BatchData tempBatchData = readBatchValues(dir, timestamp, structure);
		SeriesGeneration.readFileSystem.close();
		SeriesGeneration.readFileSystem = null;
		return tempBatchData;
	}

	/** Writes the whole batch in a single zip file **/
	public void writeSingleFile(String fsDir, long timestamp, String dir)
			throws IOException {
		SeriesGeneration.writeFileSystem = ZipWriter.createBatchFileSystem(
				fsDir, timestamp);
		this.write(dir);
		SeriesGeneration.writeFileSystem.close();
		SeriesGeneration.writeFileSystem = null;
	}

	/** Reads the whole batch from a single zip file **/
	public static BatchData readFromSingleFile(String fsDir, long timestamp,
			String dir, boolean readValues) throws IOException {
		SeriesGeneration.readFileSystem = ZipWriter.createBatchFileSystem(
				fsDir, timestamp);
		BatchData tempBatchData = read(dir, timestamp, readValues);
		SeriesGeneration.readFileSystem.close();
		SeriesGeneration.readFileSystem = null;
		return tempBatchData;
	}

	/**
	 * This method tests if two different BatchData objects can be aggregated.
	 * Checks: - same timestamp - same amount of metrics - same metrics (uses
	 * MetricData.sameType())
	 * 
	 * @author Rwilmes
	 * @date 24.06.2013
	 */
	public static boolean isSameType(BatchData b1, BatchData b2) {
		if (b1.getTimestamp() != b2.getTimestamp()) {
			Log.warn("different timestamps on batch " + b1.getTimestamp()
					+ " and batch " + b2.getTimestamp());
			return false;
		}

		MetricDataList list1 = b1.getMetrics();
		MetricDataList list2 = b2.getMetrics();

		if (list1.size() != list2.size()) {
			Log.warn("different amount of metrics on batch "
					+ b1.getTimestamp() + " and batch " + b2.getTimestamp());
			return false;
		}
		for (String k : list1.getNames()) {
			if (!MetricData.isSameType(list1.get(k), list2.get(k))) {
				Log.warn("different metrics on batch " + b1.getTimestamp()
						+ " and batch " + b2.getTimestamp());
				return false;
			}
		}

		return true;
	}

}