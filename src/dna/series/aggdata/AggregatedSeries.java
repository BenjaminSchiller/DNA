package dna.series.aggdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import dna.io.ZipWriter;
import dna.io.filesystem.Dir;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.util.Config;

/**
 * AggregatedSeries is a class for objects that contain the aggregation for a
 * whole series.
 * 
 * @author Rwilmes
 * @date 04.07.2013
 */
public class AggregatedSeries {

	// member variables
	private AggregatedBatch[] batches;

	// constructors
	public AggregatedSeries() {
	}

	public AggregatedSeries(AggregatedBatch[] batches) {
		this.batches = batches;
	}

	// methods
	public AggregatedBatch[] getBatches() {
		return this.batches;
	}

	// IO Methods
	public void write(String dir) throws IOException {
		boolean zippedRuns = false;
		if (Config.get("GENERATION_AS_ZIP").equals("runs")) {
			zippedRuns = true;
			ZipWriter.setWriteFilesystem(ZipWriter
					.createAggregationFileSystem(dir));
		}

		for (int i = 0; i < this.getBatches().length; i++) {
			long timestamp = this.getBatches()[i].getTimestamp();
			if (zippedRuns)
				this.getBatches()[i].write(Dir.getBatchDataDir(Dir.delimiter,
						timestamp));
			else
				this.getBatches()[i].writeIntelligent(Dir.getBatchDataDir(
						Dir.getAggregationDataDir(dir), timestamp));
		}

		if (zippedRuns)
			ZipWriter.closeWriteFilesystem();
	}

	public static AggregatedSeries read(String dir, String name,
			BatchReadMode batchReadMode) throws IOException {
		return read(dir, name,
				Dir.getBatchesIntelligent(Dir.getAggregationDataDir(dir)),
				batchReadMode);
	}

	private static AggregatedSeries read(String dir, String name,
			String[] batches, BatchReadMode batchReadMode) throws IOException {
		AggregatedBatch[] aggBatches = new AggregatedBatch[batches.length];

		for (int i = 0; i < batches.length; i++) {
			long timestamp = Dir.getTimestamp(batches[i]);
			aggBatches[i] = AggregatedBatch.readIntelligent(
					Dir.getAggregationBatchDir(dir, timestamp), timestamp,
					batchReadMode);
		}
		return new AggregatedSeries(aggBatches);
	}

	/** Reads only selected batches with a given stepSize **/
	public static AggregatedSeries readFromTo(String dir, String name,
			long timestampFrom, long timestampTo, long stepSize,
			BatchReadMode batchReadMode) throws IOException {
		if (timestampTo == Long.MAX_VALUE && stepSize == 1) {
			// if read all batches -> use normal method
			return AggregatedSeries.read(dir, name, batchReadMode);
		}

		String tempDir = Dir.getAggregationDataDir(dir);

		String[] tempBatches = Dir.getBatches(tempDir);
		long[] timestamps = new long[tempBatches.length];

		// get timestamps
		for (int i = 0; i < tempBatches.length; i++) {
			String[] splits = tempBatches[i].split("\\.");
			timestamps[i] = Long.parseLong(splits[splits.length - 1]);
		}

		// sort timestamps
		Arrays.sort(timestamps);

		// gather relevant batches
		ArrayList<String> batchesList = new ArrayList<String>();
		boolean firstBatch = true;
		int counter = 0;
		int firstBatchIndex = 0;
		for (int i = 0; i < timestamps.length; i++) {
			if (timestamps[i] < timestampFrom || timestamps[i] > timestampTo)
				continue;
			if (timestamps[i] >= timestampFrom) {
				if (firstBatch) {
					batchesList.add(Config.get("PREFIX_BATCHDATA_DIR")
							+ timestamps[i]);
					firstBatch = false;
					firstBatchIndex = i;
					counter = 1;
				} else {
					long offset = counter * stepSize;
					if (i == firstBatchIndex + offset) {
						batchesList.add(Config.get("PREFIX_BATCHDATA_DIR")
								+ timestamps[i]);
						counter++;
					}
				}
			}
		}
		String[] batches = batchesList.toArray(new String[batchesList.size()]);

		return read(dir, name, batches, batchReadMode);
	}
}
