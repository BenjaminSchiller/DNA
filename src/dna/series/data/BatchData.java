package dna.series.data;

import java.io.IOException;
import java.util.ArrayList;

import dna.io.ZipReader;
import dna.io.ZipWriter;
import dna.io.filesystem.Dir;
import dna.io.filesystem.Files;
import dna.labels.LabelList;
import dna.plot.PlottingUtils;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.series.data.distr.BinnedDistr;
import dna.series.data.distr.BinnedIntDistr;
import dna.series.data.distr.Distr;
import dna.series.data.distr.Distr.DistrType;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.series.lists.DistributionList;
import dna.series.lists.MetricDataList;
import dna.series.lists.NodeNodeValueListList;
import dna.series.lists.NodeValueListList;
import dna.series.lists.RunTimeList;
import dna.series.lists.ValueList;
import dna.updates.batch.Batch;
import dna.updates.batch.BatchSanitizationStats;
import dna.util.Config;
import dna.util.Log;

public class BatchData implements IBatch {

	public BatchData(long timestamp) {
		this.timestamp = timestamp;
		this.stats = new ValueList();
		this.generalRuntimes = new RunTimeList();
		this.metricRuntimes = new RunTimeList();
		this.metrics = new MetricDataList();
		this.labels = new LabelList();
	}

	public BatchData(Batch b, BatchSanitizationStats sanitizationStats,
			int sizeValues, int sizeGeneralRuntimes, int sizeMetricRuntimes,
			int sizeMetrics) {
		this(b.getTo(), sizeValues, sizeGeneralRuntimes, sizeMetricRuntimes,
				sizeMetrics);
		this.batch = b;
		this.sanitizationStats = sanitizationStats;
	}

	public BatchData(long timestamp, int sizeValues, int sizeGeneralRuntimes,
			int sizeMetricRuntimes, int sizeMetrics) {
		this.timestamp = timestamp;
		this.stats = new ValueList(sizeValues);
		this.generalRuntimes = new RunTimeList(sizeGeneralRuntimes);
		this.metricRuntimes = new RunTimeList(sizeMetricRuntimes);
		this.metrics = new MetricDataList(sizeMetrics);
		this.labels = new LabelList();
	}

	public BatchData(long timestamp, ValueList values,
			RunTimeList generalRuntimes, RunTimeList metricRuntimes,
			MetricDataList metrics) {
		this(timestamp, values, generalRuntimes, metricRuntimes, metrics,
				new LabelList());
	}

	public BatchData(long timestamp, ValueList values,
			RunTimeList generalRuntimes, RunTimeList metricRuntimes,
			MetricDataList metrics, LabelList labels) {
		this.timestamp = timestamp;
		this.stats = values;
		this.generalRuntimes = generalRuntimes;
		this.metricRuntimes = metricRuntimes;
		this.metrics = metrics;
		this.labels = labels;
	}

	private Batch batch;

	public Batch getBatch() {
		return this.batch;
	}

	public void releaseBatch() {
		this.batch = null;
	}

	private BatchSanitizationStats sanitizationStats;

	public BatchSanitizationStats getSanitizationStats() {
		return this.sanitizationStats;
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

	private LabelList labels;

	public LabelList getLabels() {
		return this.labels;
	}

	/** Writes the batch to the specified location. **/
	public void write(String dir) throws IOException {
		Log.debug("writing BatchData for " + this.timestamp + " to " + dir);
		this.stats.write(dir,
				Files.getValuesFilename(Config.get("BATCH_STATS")));
		this.generalRuntimes
				.write(dir, Files.getRuntimesFilename(Config
						.get("BATCH_GENERAL_RUNTIMES")));
		this.metricRuntimes.write(dir,
				Files.getRuntimesFilename(Config.get("BATCH_METRIC_RUNTIMES")));
		this.labels.write(dir,
				Files.getLabelsFilename(Config.get("BATCH_LABELS")));
		this.metrics.write(dir);
	}

	/**
	 * Writes the batch to the specified location either as a plain batch
	 * directory or as a zip file.
	 * 
	 * Example: Input-Dir: "data/scenario.1/series/run.0/batch.0/":
	 * 
	 * No-Zip will write the batch at "data/scenario.1/series/run.0/batch.0/".
	 * 
	 * Zipped-Batch will write the zipped batch
	 * "data/scenario.1/series/run.0/batch.0.zip"
	 * 
	 * Zipped-Run will write the batch into the run-zip as
	 * "data/scenario.1/series/run.0.zip/batch.0/".
	 * 
	 * @throws IOException
	 **/
	public void writeIntelligent(String dir) throws IOException {
		String tempDir = "";
		if (Config.get("GENERATION_AS_ZIP").equals("batches")) {
			// write zip batch
			String[] splits = dir.split(Dir.delimiter);

			// iterate over splits last to first
			for (int i = splits.length - 1; i >= 0; i--) {
				if (splits[i].startsWith(Config.get("PREFIX_BATCHDATA_DIR"))) {
					// build dir string
					for (int j = 0; j < i; j++)
						tempDir += splits[j] + Dir.delimiter;

					// parse suffix
					String suffix = "";
					String[] splits2 = splits[i].split(Config
							.get("PREFIX_BATCHDATA_DIR"));
					if (splits2.length == 2) {
						String s = splits2[1];
						for (int j = 0; j < s.length(); j++) {
							boolean isDigit = (s.charAt(j) >= '0' && s
									.charAt(j) <= '9');
							if (!isDigit)
								suffix = s.substring(j, s.length());
						}
					}

					this.writeSingleFile(tempDir, this.getTimestamp(),
							Config.get("SUFFIX_ZIP_FILE") + suffix,
							Dir.delimiter);
				}
			}

		} else if (Config.get("GENERATION_AS_ZIP").equals("runs")) {
			// write batch to zipped run
			String[] splits = dir.split(Dir.delimiter);

			// iterate over splits last to first
			for (int i = splits.length - 1; i >= 0; i--) {
				if (splits[i].startsWith(Config.get("PREFIX_RUNDATA_DIR"))) {
					// build dir string
					for (int j = 0; j < i; j++)
						tempDir += splits[j] + Dir.delimiter;

					// build relative dir string
					String relDir = Dir.delimiter;
					for (int j = i + 1; j < splits.length; j++)
						relDir += splits[j] + Dir.delimiter;

					// parse run id
					int runId = Integer.parseInt(splits[i].replace(
							Config.get("PREFIX_RUNDATA_DIR"), ""));

					// open zip
					ZipWriter.setWriteFilesystem(ZipWriter.createRunFileSystem(
							tempDir, runId));

					// write
					this.write(relDir);

					// close zip
					ZipWriter.closeWriteFilesystem();

					// break from for loop
					break;
				}
			}

		} else {
			// write normal batch
			this.write(dir);
		}
	}

	/**
	 * Reads the batch and its values corresponding to the BatchReadMode.
	 * 
	 * @throws IOException
	 **/
	public static BatchData read(String dir, long timestamp,
			BatchReadMode batchReadMode) throws IOException {
		boolean readSingles;
		boolean readLabels;

		switch (batchReadMode) {
		case readAllValues:
			readSingles = true;
			readLabels = true;
			break;
		case readNoValues:
			readSingles = false;
			readLabels = false;
			break;
		case readOnlyDistAndNvl:
			readSingles = false;
			readLabels = false;
			break;
		case readOnlyLabels:
			readSingles = false;
			readLabels = true;
			break;
		case readOnlySingleValues:
			readSingles = true;
			readLabels = false;
			break;
		default:
			readSingles = false;
			readLabels = false;
			break;
		}

		ValueList values = ValueList
				.read(dir, Files.getValuesFilename(Config.get("BATCH_STATS")),
						readSingles);
		RunTimeList generalRuntimes = RunTimeList
				.read(dir, Files.getRuntimesFilename(Config
						.get("BATCH_GENERAL_RUNTIMES")), readSingles);
		RunTimeList metricRuntimes = RunTimeList.read(dir,
				Files.getRuntimesFilename(Config.get("BATCH_METRIC_RUNTIMES")),
				readSingles);
		LabelList labels = LabelList
				.read(dir, Files.getLabelsFilename(Config.get("BATCH_LABELS")),
						readLabels);
		MetricDataList metrics = MetricDataList.read(dir, batchReadMode);
		return new BatchData(timestamp, values, generalRuntimes,
				metricRuntimes, metrics, labels);
	}

	/**
	 * Reads the batch and its values while using the structure of the given
	 * BatchData object. This results in fast read-times especially when using
	 * zips.
	 * 
	 * Example: Input-Dir: "data/scenario.1/series/run.0/batch.0/":
	 * 
	 * No-Zip will return the batch at "data/scenario.1/series/run.0/batch.0/".
	 * 
	 * Zipped-Batch will read and return the zipped batch
	 * "data/scenario.1/series/run.0/batch.0.zip"
	 * 
	 * Zipped-Run will read the zipped run "data/scenario.1/series/run.0.zip"
	 * and return batch.0 of run.0.
	 * 
	 * @param dir
	 *            Directory where the batch will be read from
	 * @param timestamp
	 *            Timestamp of the batch.
	 * @param b
	 *            BatchData object containing a structure which will be used to
	 *            read the corresponding values
	 * @return
	 * @throws IOException
	 */
	public static BatchData readBatchValuesIntelligent(String dir,
			long timestamp, BatchData b) throws IOException {
		BatchData temp = null;
		String tempDir = dir;
		if (Config.get("GENERATION_AS_ZIP").equals("batches")) {
			// get batch from zip
			String[] splits = dir.split(Dir.delimiter);
			tempDir = "";

			// iterate over splits last to first
			for (int i = splits.length - 1; i >= 0; i--) {
				if (splits[i].startsWith(Config.get("PREFIX_BATCHDATA_DIR"))) {
					// build dir string
					for (int j = 0; j < i; j++)
						tempDir += splits[j] + Dir.delimiter;

					// read batch from zip
					temp = BatchData.readBatchValuesFromSingleFile(tempDir,
							timestamp, Dir.delimiter, b);
				}
			}
		} else if (Config.get("GENERATION_AS_ZIP").equals("runs")) {
			// get batch from zipped run
			String[] splits = dir.split(Dir.delimiter);
			tempDir = "";

			// iterate over splits last to first
			for (int i = splits.length - 1; i >= 0; i--) {
				if (splits[i].startsWith(Config.get("PREFIX_RUNDATA_DIR"))) {
					// build dir string
					for (int j = 0; j < i; j++)
						tempDir += splits[j] + Dir.delimiter;

					// read run from zip
					int runId = Integer.parseInt(splits[i].replace(
							Config.get("PREFIX_RUNDATA_DIR"), ""));

					// open zip
					ZipReader.setReadFilesystem(ZipReader.getRunFileSystem(
							tempDir, runId));

					// read
					temp = BatchData.readBatchValues(
							Dir.getBatchDataDir(Dir.delimiter, timestamp),
							timestamp, b);

					// close zip
					ZipReader.closeReadFilesystem();

					// break for loop
					break;
				}
			}
		} else {
			// get batch
			temp = BatchData.readBatchValues(dir, timestamp, b);
		}
		return temp;
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
				Files.getValuesFilename(Config.get("BATCH_STATS")), true);

		// read runtimes
		RunTimeList generalRuntimes = RunTimeList
				.read(dir, Files.getRuntimesFilename(Config
						.get("BATCH_GENERAL_RUNTIMES")), true);
		RunTimeList metricRuntimes = RunTimeList.read(dir,
				Files.getRuntimesFilename(Config.get("BATCH_METRIC_RUNTIMES")),
				true);

		// read metrics
		MetricDataList metrics = new MetricDataList(b.getMetrics().size());
		for (MetricData m : b.getMetrics().getList()) {
			String mDir = Dir.getMetricDataDir(dir, m.getName(), m.getType());
			// init metric values
			ValueList mValues = ValueList.read(mDir,
					Files.getValuesFilename(Config.get("METRIC_DATA_VALUES")),
					true);
			DistributionList mDistributions = new DistributionList(m
					.getDistributions().size());
			NodeValueListList mNodevalues = new NodeValueListList(m
					.getNodeValues().size());
			NodeNodeValueListList mNodenodevalues = new NodeNodeValueListList(m
					.getNodeNodeValues().size());

			// read distributions
			for (Distr<?, ?> d : m.getDistributions().getList()) {
				if (d.getDistrType().equals(DistrType.BINNED_DOUBLE)
						|| d.getDistrType().equals(DistrType.BINNED_INT)
						|| d.getDistrType().equals(DistrType.BINNED_LONG)) {
					mDistributions.add(BinnedDistr.read(
							mDir,
							Files.getDistributionFilename(d.getName(),
									d.getDistrType()), d.getName(), true,
							((BinnedDistr<?>) d).getClass()));
				}
				if (d.getDistrType().equals(DistrType.QUALITY_DOUBLE)
						|| d.getDistrType().equals(DistrType.QUALITY_INT)
						|| d.getDistrType().equals(DistrType.QUALITY_LONG)) {

				}
				mDistributions.add(Distr.read(
						mDir,
						Files.getDistributionFilename(d.getName(),
								d.getDistrType()), d.getName(),
						d.getDistrType(), true));
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
		ZipReader.setReadFilesystem(ZipReader.getBatchFileSystem(fsDir,
				Config.get("SUFFIX_ZIP_FILE"), timestamp));
		BatchData tempBatchData = readBatchValues(dir, timestamp, structure);
		ZipReader.closeReadFilesystem();
		return tempBatchData;
	}

	/** Writes the whole batch in a single zip file **/
	public void writeSingleFile(String fsDir, long timestamp, String suffix,
			String dir) throws IOException {
		ZipWriter.setWriteFilesystem(ZipWriter.createBatchFileSystem(fsDir,
				suffix, timestamp));
		this.write(dir);
		ZipWriter.closeWriteFilesystem();
	}

	/** Reads the whole batch from a single zip file **/
	public static BatchData readFromSingleFile(String fsDir, long timestamp,
			String dir, BatchReadMode batchReadMode) throws IOException {
		ZipReader.setReadFilesystem(ZipReader.getBatchFileSystem(fsDir,
				Config.get("SUFFIX_ZIP_FILE"), timestamp));
		BatchData tempBatchData = read(dir, timestamp, batchReadMode);
		ZipReader.closeReadFilesystem();
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

	/** Checks if the batch contains the domain and value. **/
	public boolean contains(String domain, String value) {
		return PlottingUtils.isContained(domain, value, this);
	}

	/**
	 * Reads the batch and its values in respect to the BatchReadMode while also
	 * checking if zipped-runs, zipped-batches or nozips are configured.<br>
	 * <br>
	 * 
	 * <b>Example:</b> Input-Dir: "data/scenario.1/series/run.0/batch.0/":<br>
	 * <br>
	 * 
	 * - <b>No-Zip</b> will return the batch at
	 * "data/scenario.1/series/run.0/batch.0/".<br>
	 * <br>
	 * 
	 * - <b>Zipped-Batch</b> will read and return the zipped batch
	 * "data/scenario.1/series/run.0/batch.0.zip"<br>
	 * <br>
	 * 
	 * - <b>Zipped-Run</b> will read the zipped run
	 * "data/scenario.1/series/run.0.zip" and return batch.0 of run.0.
	 * 
	 * @throws IOException
	 **/
	public static BatchData readIntelligent(String dir, long timestamp,
			BatchReadMode batchReadMode) throws IOException {
		BatchData temp = null;
		String tempDir = dir;
		if (Config.get("GENERATION_AS_ZIP").equals("batches")) {
			// get batch from zip
			String[] splits = dir.split(Dir.delimiter);
			tempDir = "";

			// iterate over splits last to first
			for (int i = splits.length - 1; i >= 0; i--) {
				if (splits[i].startsWith(Config.get("PREFIX_BATCHDATA_DIR"))) {
					// build dir string
					for (int j = 0; j < i; j++)
						tempDir += splits[j] + Dir.delimiter;

					// read batch from zip
					temp = BatchData.readFromSingleFile(tempDir, timestamp,
							Dir.delimiter, batchReadMode);
				}
			}
		} else if (Config.get("GENERATION_AS_ZIP").equals("runs")) {
			// get batch from zipped run
			String[] splits = dir.split(Dir.delimiter);
			tempDir = "";

			// iterate over splits last to first
			for (int i = splits.length - 1; i >= 0; i--) {
				if (splits[i].startsWith(Config.get("PREFIX_RUNDATA_DIR"))) {
					// build dir string
					for (int j = 0; j < i; j++)
						tempDir += splits[j] + Dir.delimiter;

					// read run from zip
					int runId = Integer.parseInt(splits[i].replace(
							Config.get("PREFIX_RUNDATA_DIR"), ""));
					RunData tempRun = RunData.readFromSingleFile(tempDir,
							Dir.delimiter, runId, batchReadMode);

					// get batch
					ArrayList<BatchData> batches = tempRun.getBatches()
							.getList();
					for (int j = 0; j < batches.size(); j++) {
						BatchData b = batches.get(j);
						if (b.getTimestamp() == timestamp) {
							temp = b;
							break;
						}
					}
					break;
				}
			}
		} else {
			// get batch
			temp = BatchData.read(dir, timestamp, batchReadMode);
		}
		return temp;
	}

	/** Returns true if both BatchData objects are equal. **/
	public static boolean equal(BatchData b1, BatchData b2) {
		// check amount values
		if (b1.getGeneralRuntimes().size() != b2.getGeneralRuntimes().size())
			return false;
		if (b1.getMetricRuntimes().size() != b2.getMetricRuntimes().size())
			return false;
		if (b1.getValues().size() != b2.getValues().size())
			return false;
		if (b1.getMetrics().size() != b2.getMetrics().size())
			return false;

		// compare runtimes
		for (String runtime : b1.getGeneralRuntimes().getNames()) {
			if (!b2.getGeneralRuntimes().getNames().contains(runtime))
				return false;
		}

		// metric runtimes
		for (String runtime : b1.getMetricRuntimes().getNames()) {
			if (!b2.getMetricRuntimes().getNames().contains(runtime))
				return false;
		}

		// metrics
		for (String metric : b1.getMetrics().getNames()) {
			if (!b2.getMetrics().getNames().contains(metric))
				return false;

			if (!MetricData.isSameType(b1.getMetrics().get(metric), b2
					.getMetrics().get(metric)))
				return false;
		}

		return true;
	}

	/** Returns a clone of the batch-data object. **/
	public BatchData cloneStructure() {
		ValueList values = new ValueList(this.stats.size());
		for (Value v : this.stats.getList())
			values.add(new Value(v.getName(), 0));

		RunTimeList generalRuntimes = new RunTimeList(
				this.generalRuntimes.size());
		for (RunTime gen : this.generalRuntimes.getList())
			generalRuntimes.add(new RunTime(gen.getName(), 0));

		RunTimeList metricRuntimes = new RunTimeList(this.metricRuntimes.size());
		for (RunTime met : this.metricRuntimes.getList())
			generalRuntimes.add(new RunTime(met.getName(), 0));

		MetricDataList metrics = new MetricDataList(this.metrics.size());
		for (MetricData m : metrics.getList()) {
			ValueList metricValues = new ValueList(m.getValues().size());
			for (Value v : m.getValues().getList())
				metricValues.add(new Value(v.getName(), 0));

			DistributionList distributions = new DistributionList(m
					.getDistributions().size());
			for (Distr<?, ?> d : m.getDistributions().getList())
				distributions.add(new BinnedIntDistr(d.getName()));

			NodeValueListList nvls = new NodeValueListList(m.getNodeValues()
					.size());
			for (NodeValueList nvl : m.getNodeValues().getList())
				nvls.add(new NodeValueList(nvl.getName(), 0));

			NodeNodeValueListList nnvls = new NodeNodeValueListList(m
					.getNodeNodeValues().size());
			for (NodeNodeValueList nnvl : m.getNodeNodeValues().getList())
				nnvls.add(new NodeNodeValueList(nnvl.getName(), 0));

			metrics.add(new MetricData(m.getName(), m.getType(), metricValues,
					distributions, nvls, nnvls));
		}

		return new BatchData(this.timestamp, values, generalRuntimes,
				metricRuntimes, metrics);
	}
}