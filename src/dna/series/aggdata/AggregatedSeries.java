package dna.series.aggdata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import dna.io.ZipReader;
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
		boolean zippedBatches = false;
		if (Config.get("GENERATION_AS_ZIP").equals("runs")) {
			zippedRuns = true;
			ZipWriter.writeFileSystem = ZipWriter
					.createAggregationFileSystem(dir);
		}
		if (Config.get("GENERATION_AS_ZIP").equals("batches"))
			zippedBatches = true;
		for (int i = 0; i < this.getBatches().length; i++) {
			long tempTimestamp = this.getBatches()[i].getTimestamp();
			String tempDir = Dir.getBatchDataDir(dir, tempTimestamp);
			if (zippedRuns)
				tempDir = Dir.getBatchDataDir(Dir.delimiter, tempTimestamp);

			if (zippedBatches)
				this.getBatches()[i].writeSingleFile(dir,
						this.getBatches()[i].getTimestamp(), Dir.delimiter);
			else
				this.getBatches()[i].write(tempDir);
		}
		if (zippedRuns) {
			ZipWriter.writeFileSystem.close();
			ZipWriter.writeFileSystem = null;
		}
	}

	public static AggregatedSeries read(String dir, String name,
			BatchReadMode batchReadMode) throws IOException {
		String[] batches;
		if (Config.get("GENERATION_AS_ZIP").equals("runs")) {
			ZipReader.readFileSystem = ZipWriter
					.createAggregationFileSystem(dir);
			batches = Dir.getBatches(Dir.delimiter);
			ZipReader.readFileSystem.close();
			ZipReader.readFileSystem = null;
		} else {
			batches = Dir.getBatches(Dir.getAggregationDataDir(dir));
		}
		return read(dir, name, batches, batchReadMode);
	}

	private static AggregatedSeries read(String dir, String name,
			String[] batches, BatchReadMode batchReadMode) throws IOException {
		String tempDir = Dir.getAggregationDataDir(dir);
		AggregatedBatch[] aggBatches = new AggregatedBatch[batches.length];

		boolean zippedRuns = false;
		boolean zippedBatches = false;
		if (Config.get("GENERATION_AS_ZIP").equals("runs"))
			zippedRuns = true;
		if (Config.get("GENERATION_AS_ZIP").equals("batches"))
			zippedBatches = true;

		for (int i = 0; i < batches.length; i++) {
			long timestamp = Dir.getTimestamp(batches[i]);
			String aggDir = Dir.getAggregationBatchDir(dir, timestamp);
			if (zippedRuns) {
				ZipReader.readFileSystem = ZipWriter
						.createAggregationFileSystem(dir);
				aggDir = Dir.getBatchDataDir(Dir.delimiter, timestamp);
			}
			if (zippedBatches)
				aggBatches[i] = AggregatedBatch.readFromSingleFile(tempDir,
						timestamp, Dir.delimiter, batchReadMode);
			else
				aggBatches[i] = AggregatedBatch.read(aggDir, timestamp,
						batchReadMode);
			if (zippedRuns) {
				ZipReader.readFileSystem.close();
				ZipReader.readFileSystem = null;
			}
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
