package dna.series.aggdata;

import java.io.IOException;

import dna.io.ZipWriter;
import dna.io.filesystem.Files;
import dna.plot.PlottingUtils;
import dna.series.SeriesGeneration;
import dna.util.Config;
import dna.util.Log;

/**
 * An AggregatedBatch contains aggregated values of a batch.
 * 
 * @author Rwilmes
 * @date 04.07.2013
 */
public class AggregatedBatch {

	// read enumeration
	public static enum BatchReadMode {
		readAllValues, readOnlySingleValues, readOnlyDistAndNvl, readNoValues
	};

	// member variables
	private long timestamp;
	private AggregatedValueList stats;
	private AggregatedRunTimeList generalRuntimes;
	private AggregatedRunTimeList metricRuntimes;
	private AggregatedMetricList metrics;

	// constructors
	public AggregatedBatch(long timestamp) {
		this.timestamp = timestamp;
		this.stats = new AggregatedValueList();
		this.generalRuntimes = new AggregatedRunTimeList();
		this.metricRuntimes = new AggregatedRunTimeList();
		this.metrics = new AggregatedMetricList();
	}

	public AggregatedBatch(long timestamp, int sizeValues,
			int sizeGeneralRuntimes, int sizeMetricRuntimes, int sizeMetrics) {
		this.timestamp = timestamp;
		this.stats = new AggregatedValueList(sizeValues);
		this.generalRuntimes = new AggregatedRunTimeList(sizeGeneralRuntimes);
		this.metricRuntimes = new AggregatedRunTimeList(sizeMetricRuntimes);
		this.metrics = new AggregatedMetricList(sizeMetrics);
	}

	public AggregatedBatch(long timestamp, AggregatedValueList values,
			AggregatedRunTimeList generalRuntimes,
			AggregatedRunTimeList metricRuntimes, AggregatedMetricList metrics) {
		this.timestamp = timestamp;
		this.stats = values;
		this.generalRuntimes = generalRuntimes;
		this.metricRuntimes = metricRuntimes;
		this.metrics = metrics;
	}

	// methods
	public long getTimestamp() {
		return this.timestamp;
	}

	public AggregatedValueList getValues() {
		return this.stats;
	}

	public AggregatedRunTimeList getGeneralRuntimes() {
		return this.generalRuntimes;
	}

	public AggregatedRunTimeList getMetricRuntimes() {
		return this.metricRuntimes;
	}

	public AggregatedMetricList getMetrics() {
		return this.metrics;
	}

	// IO methods
	public void write(String dir) throws IOException {
		Log.debug("writing AggregatedBatch for " + this.timestamp + " to "
				+ dir);
		this.stats.write(dir,
				Files.getValuesFilename(Config.get("BATCH_STATS")));
		this.generalRuntimes
				.write(dir, Files.getRuntimesFilename(Config
						.get("BATCH_GENERAL_RUNTIMES")));
		this.metricRuntimes.write(dir,
				Files.getRuntimesFilename(Config.get("BATCH_METRIC_RUNTIMES")));
		this.metrics.write(dir);
	}

	/** Writes the whole aggregated batch in a single zip file **/
	public void writeSingleFile(String fsDir, long timestamp, String dir)
			throws IOException {
		SeriesGeneration.writeFileSystem = ZipWriter.createBatchFileSystem(
				fsDir, Config.get("SUFFIX_ZIP_FILE"), timestamp);
		this.write(dir);
		SeriesGeneration.writeFileSystem.close();
		SeriesGeneration.writeFileSystem = null;
	}

	public static AggregatedBatch read(String dir, long timestamp,
			BatchReadMode batchReadMode) throws IOException {
		boolean readValues;
		if (batchReadMode.equals(BatchReadMode.readNoValues)
				|| batchReadMode.equals(BatchReadMode.readOnlyDistAndNvl))
			readValues = false;
		else
			readValues = true;
		AggregatedValueList values = AggregatedValueList.read(dir,
				Files.getValuesFilename(Config.get("BATCH_STATS")), readValues);
		AggregatedRunTimeList generalRuntimes = AggregatedRunTimeList
				.read(dir, Files.getRuntimesFilename(Config
						.get("BATCH_GENERAL_RUNTIMES")), readValues);
		AggregatedRunTimeList metricRuntimes = AggregatedRunTimeList.read(dir,
				Files.getRuntimesFilename(Config.get("BATCH_METRIC_RUNTIMES")),
				readValues);
		AggregatedMetricList metrics = AggregatedMetricList.read(dir,
				batchReadMode);
		return new AggregatedBatch(timestamp, values, generalRuntimes,
				metricRuntimes, metrics);
	}

	/** Reads the whole batch from a single zip file **/
	public static AggregatedBatch readFromSingleFile(String fsDir,
			long timestamp, String dir, BatchReadMode batchReadMode)
			throws IOException {
		SeriesGeneration.readFileSystem = ZipWriter.createBatchFileSystem(
				fsDir, Config.get("SUFFIX_ZIP_FILE"), timestamp);
		AggregatedBatch tempBatchData = read(dir, timestamp, batchReadMode);
		SeriesGeneration.readFileSystem.close();
		SeriesGeneration.readFileSystem = null;
		return tempBatchData;
	}

	/**
	 * Returns a new AggregatedBatch, which equals b1, except that all runtime
	 * values equal the sum of the runtimes of b1 and b2.
	 * 
	 * @param b1
	 *            First runtime, will be cloned and returned with the sum of b2.
	 * @param b2
	 *            Will be added to b1.
	 * @return New AggregatedBatch, equalling b1, except for the runtime values.
	 */
	public static AggregatedBatch sumRuntimes(AggregatedBatch b1,
			AggregatedBatch b2) {
		AggregatedRunTimeList genRuntimes = new AggregatedRunTimeList(b1
				.getGeneralRuntimes().getName(), b1.getGeneralRuntimes().size());
		AggregatedRunTimeList metRuntimes = new AggregatedRunTimeList(b1
				.getMetricRuntimes().getName(), b1.getMetricRuntimes().size());
		for (String gen : b1.getGeneralRuntimes().getNames()) {
			AggregatedValue v1 = b1.getGeneralRuntimes().get(gen);
			AggregatedValue v2 = b2.getGeneralRuntimes().get(gen);

			double[] values3 = new double[v1.getValues().length];

			for (int i = 0; i < v1.getValues().length; i++) {
				double[] values2;
				if (v2 == null)
					values2 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
				else
					values2 = v2.getValues();
				values3[i] = 0;
				values3[i] += v1.getValues()[i] + values2[i];
			}

			genRuntimes.add(new AggregatedValue(v1.getName(), values3));
		}
		for (String met : b1.getMetricRuntimes().getNames()) {
			AggregatedValue v1 = b1.getMetricRuntimes().get(met);
			AggregatedValue v2 = b2.getMetricRuntimes().get(met);

			double[] values3 = new double[v1.getValues().length];

			for (int i = 0; i < v1.getValues().length; i++) {
				double[] values2;
				if (v2 == null)
					values2 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
				else
					values2 = v2.getValues();
				values3[i] = 0;
				values3[i] += v1.getValues()[i] + values2[i];
			}

			metRuntimes.add(new AggregatedValue(v1.getName(), values3));
		}

		// return new crafted batch
		return new AggregatedBatch(b1.getTimestamp(), b1.getValues(),
				genRuntimes, metRuntimes, b1.getMetrics());
	}

	/**
	 * Returns a new AggregatedBatch, which equals b1, except that all values
	 * equal the sum of the values of b1 and b2.
	 * 
	 * @param b1
	 *            First value, will be cloned and returned with the sum of b2.
	 * @param b2
	 *            Will be added to b1.
	 * @return New AggregatedBatch, equalling b1, except for the value values.
	 */
	public static AggregatedBatch sumValues(AggregatedBatch b1,
			AggregatedBatch b2) {
		AggregatedValueList values = new AggregatedValueList(b1.getValues()
				.size());
		AggregatedMetricList metrics = new AggregatedMetricList(b1.getMetrics()
				.size());
		for (String value : b1.getValues().getNames()) {
			AggregatedValue v1 = b1.getValues().get(value);
			AggregatedValue v2 = b2.getValues().get(value);

			double[] values3 = new double[v1.getValues().length];
			for (int i = 0; i < v1.getValues().length; i++) {
				double[] values2;
				if (v2 == null)
					values2 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
				else
					values2 = v2.getValues();
				values3[i] = 0;
				values3[i] += v1.getValues()[i] + values2[i];
			}
			values.add(new AggregatedValue(v1.getName(), values3));
		}
		for (AggregatedMetric metric : b1.getMetrics().getList()) {
			AggregatedValueList mValues = new AggregatedValueList(metric
					.getValues().size());
			for (String value : metric.getValues().getNames()) {
				AggregatedValue v1 = metric.getValues().get(value);
				AggregatedValue v2 = metric.getValues().get(value);

				double[] values3 = new double[v1.getValues().length];
				for (int i = 0; i < v1.getValues().length; i++) {
					double[] values2;
					if (v2 == null)
						values2 = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
					else
						values2 = v2.getValues();
					values3[i] = 0;
					values3[i] += v1.getValues()[i] + values2[i];
				}
				mValues.add(new AggregatedValue(v1.getName(), values3));
			}
			metrics.add(new AggregatedMetric(metric.getName(), mValues, metric
					.getDistributions(), metric.getNodeValues()));
		}

		// return new crafted batch
		return new AggregatedBatch(b1.getTimestamp(), values,
				b1.getGeneralRuntimes(), b1.getMetricRuntimes(), metrics);
	}

	/** Checks if the batch contains the domain and value. **/
	public boolean contains(String domain, String value) {
		return PlottingUtils.isContained(domain, value, this);
	}
}
