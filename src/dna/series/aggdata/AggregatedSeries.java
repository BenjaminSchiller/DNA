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
	// TODO: READ ??
	public void write(String dir) throws IOException {
		for (int i = 0; i < this.getBatches().length; i++) {
			this.getBatches()[i].write(Dir.getBatchDataDir(dir, i));
		}
	}

}
