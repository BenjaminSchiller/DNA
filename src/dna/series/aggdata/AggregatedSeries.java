package dna.series.aggdata;

import java.io.IOException;

import dna.io.filesystem.Dir;

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
		for (int i = 0; i < this.getBatches().length; i++) {
			this.getBatches()[i].write(Dir.getBatchDataDir(dir, i));
		}
	}

	public static AggregatedSeries read(String dir, String name,
			boolean readValues) throws IOException {
		String tempDir = Dir.getAggregationDataDir(dir);

		String[] batches = Dir.getBatches(tempDir);
		AggregatedBatch[] aggBatches = new AggregatedBatch[Dir
				.getBatches(tempDir).length];

		for (String batch : batches) {
			long timestamp = Dir.getTimestamp(batch);
			aggBatches[(int) timestamp] = AggregatedBatch.read(
					Dir.getAggregationBatchDir(dir, timestamp), timestamp,
					readValues);
		}

		return new AggregatedSeries(aggBatches);
	}
}
